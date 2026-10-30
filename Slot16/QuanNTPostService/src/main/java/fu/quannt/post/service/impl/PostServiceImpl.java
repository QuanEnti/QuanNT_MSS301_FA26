package fu.quannt.post.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import fu.quannt.post.client.UserClient;
import fu.quannt.post.dto.ApiResponse;
import fu.quannt.post.dto.PageDTO;
import fu.quannt.post.dto.PostDTO;
import fu.quannt.post.dto.PostDetailDTO;
import fu.quannt.post.dto.UserDTO;
import fu.quannt.post.entity.Post;
import fu.quannt.post.entity.PostStatus;
import fu.quannt.post.repository.PostRepository;
import fu.quannt.post.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

@Service
public class PostServiceImpl implements PostService {

    private static final String ACTIVE = "ACTIVE";

    private final PostRepository postRepository;
    private final UserClient userClient;
    private final ObjectMapper objectMapper;

    public PostServiceImpl(PostRepository postRepository,
                           UserClient userClient,
                           ObjectMapper objectMapper) {
        this.postRepository = postRepository;
        this.userClient = userClient;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public ApiResponse createPost(PostDTO dto) {
        if (!hasValidDates(dto.getStartDate(), dto.getEndDate())) {
            return ApiResponse.error(2, "endDate must be after startDate");
        }

        UserDTO user = fetchUser(dto.getUserId());
        if (user == null) {
            return ApiResponse.error(4, "User not found");
        }
        ApiResponse userValidation = validateUserForPost(user, dto.getNumberOfPosts());
        if (userValidation != null) {
            return userValidation;
        }

        Post post = new Post();
        post.setAdvertiserName(dto.getAdvertiserName());
        post.setAdvertiserEmail(dto.getAdvertiserEmail());
        post.setAdvertiserPhone(dto.getAdvertiserPhone());
        post.setStartDate(dto.getStartDate());
        post.setEndDate(dto.getEndDate());
        post.setNumberOfPosts(dto.getNumberOfPosts());
        post.setTotalCost(computeTotal(
                user.getRatePerPost(), dto.getStartDate(), dto.getEndDate()));
        post.setStatus(PostStatus.SCHEDULED);
        post.setUserId(dto.getUserId());

        return ApiResponse.success(
                "Post created successfully", toDTO(postRepository.save(post)));
    }

    @Override
    @Transactional
    public ApiResponse updatePost(Long postId, PostDTO dto) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            return ApiResponse.error(4, "Post not found");
        }

        Date effectiveStart = dto.getStartDate() == null
                ? post.getStartDate() : dto.getStartDate();
        Date effectiveEnd = dto.getEndDate() == null
                ? post.getEndDate() : dto.getEndDate();
        if (!hasValidDates(effectiveStart, effectiveEnd)) {
            return ApiResponse.error(2, "endDate must be after startDate");
        }

        Long effectiveUserId = dto.getUserId() == null
                ? post.getUserId() : dto.getUserId();
        Integer effectiveNumberOfPosts = dto.getNumberOfPosts() == null
                ? post.getNumberOfPosts() : dto.getNumberOfPosts();
        UserDTO user = fetchUser(effectiveUserId);
        if (user == null) {
            return ApiResponse.error(4, "User not found");
        }
        ApiResponse userValidation = validateUserForPost(user, effectiveNumberOfPosts);
        if (userValidation != null) {
            return userValidation;
        }

        if (dto.getAdvertiserName() != null) {
            post.setAdvertiserName(dto.getAdvertiserName());
        }
        if (dto.getAdvertiserEmail() != null) {
            post.setAdvertiserEmail(dto.getAdvertiserEmail());
        }
        if (dto.getAdvertiserPhone() != null) {
            post.setAdvertiserPhone(dto.getAdvertiserPhone());
        }
        if (dto.getStatus() != null) {
            post.setStatus(PostStatus.valueOf(dto.getStatus()));
        }
        post.setStartDate(effectiveStart);
        post.setEndDate(effectiveEnd);
        post.setNumberOfPosts(effectiveNumberOfPosts);
        post.setUserId(effectiveUserId);
        post.setTotalCost(computeTotal(
                user.getRatePerPost(), effectiveStart, effectiveEnd));

        return ApiResponse.success(
                "Post updated successfully", toDTO(postRepository.save(post)));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse getPostById(Long postId) {
        return postRepository.findById(postId)
                .map(post -> ApiResponse.success("Successful", toDetailDTO(post)))
                .orElseGet(() -> ApiResponse.error(4, "Post not found"));
    }

    @Override
    @Transactional
    public ApiResponse cancelPost(Long postId) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            return ApiResponse.error(4, "Post not found");
        }

        post.setStatus(PostStatus.CANCELLED);
        postRepository.save(post);
        return ApiResponse.success("Post cancelled successfully", null);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse getPosts(
            int page, int size, String advertiserName, String status) {
        if (page < 0) {
            return ApiResponse.error(2, "page must be greater than or equal to 0");
        }
        if (size < 1 || size > 100) {
            return ApiResponse.error(2, "size must be between 1 and 100");
        }

        PostStatus statusFilter;
        try {
            statusFilter = parseStatus(status);
        } catch (IllegalArgumentException exception) {
            return ApiResponse.error(2, exception.getMessage());
        }

        Page<Post> result = postRepository.search(
                emptyToNull(advertiserName),
                statusFilter,
                PageRequest.of(page, size, Sort.by("postId").ascending()));
        List<PostDetailDTO> posts = result.getContent().stream()
                .map(this::toDetailDTO)
                .toList();
        PageDTO pageDTO = new PageDTO(
                result.getSize(),
                result.getNumber(),
                result.getTotalPages(),
                result.getTotalElements(),
                result.isFirst(),
                result.isLast(),
                posts);
        return ApiResponse.success("Successful", pageDTO);
    }

    private ApiResponse validateUserForPost(UserDTO user, Integer numberOfPosts) {
        if (!ACTIVE.equals(user.getStatus())) {
            return ApiResponse.error(5, "User is not ACTIVE");
        }
        if (numberOfPosts == null || numberOfPosts < 1) {
            return ApiResponse.error(2, "numberOfPosts must be at least 1");
        }
        if (numberOfPosts > user.getMaxDailyPosts()) {
            return ApiResponse.error(
                    2, "numberOfPosts must not exceed user's maxDailyPosts");
        }
        return null;
    }

    private UserDTO fetchUser(Long userId) {
        try {
            ApiResponse response = userClient.getUserById(userId);
            if (response == null || response.getStatus() != 1
                    || response.getData() == null) {
                return null;
            }
            return objectMapper.convertValue(response.getData(), UserDTO.class);
        } catch (FeignException.NotFound exception) {
            return null;
        }
    }

    private boolean hasValidDates(Date startDate, Date endDate) {
        return startDate != null && endDate != null && endDate.after(startDate);
    }

    private BigDecimal computeTotal(
            BigDecimal ratePerPost, Date startDate, Date endDate) {
        long days = ChronoUnit.DAYS.between(
                toLocalDate(startDate), toLocalDate(endDate));
        return ratePerPost.multiply(BigDecimal.valueOf(Math.max(days, 1)));
    }

    private LocalDate toLocalDate(Date date) {
        return Instant.ofEpochMilli(date.getTime())
                .atZone(ZoneOffset.UTC)
                .toLocalDate();
    }

    private PostStatus parseStatus(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }
        try {
            return PostStatus.valueOf(status);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                    "status must be DRAFT, SCHEDULED, PUBLISHED, or CANCELLED");
        }
    }

    private String emptyToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }

    private PostDTO toDTO(Post post) {
        PostDTO dto = new PostDTO();
        dto.setPostId(post.getPostId());
        dto.setAdvertiserName(post.getAdvertiserName());
        dto.setAdvertiserEmail(post.getAdvertiserEmail());
        dto.setAdvertiserPhone(post.getAdvertiserPhone());
        dto.setStartDate(post.getStartDate());
        dto.setEndDate(post.getEndDate());
        dto.setNumberOfPosts(post.getNumberOfPosts());
        dto.setTotalCost(post.getTotalCost());
        dto.setStatus(post.getStatus().name());
        dto.setUserId(post.getUserId());
        return dto;
    }

    private PostDetailDTO toDetailDTO(Post post) {
        PostDetailDTO dto = new PostDetailDTO();
        dto.setPostId(post.getPostId());
        dto.setAdvertiserName(post.getAdvertiserName());
        dto.setAdvertiserEmail(post.getAdvertiserEmail());
        dto.setAdvertiserPhone(post.getAdvertiserPhone());
        dto.setStartDate(post.getStartDate());
        dto.setEndDate(post.getEndDate());
        dto.setNumberOfPosts(post.getNumberOfPosts());
        dto.setTotalCost(post.getTotalCost());
        dto.setStatus(post.getStatus().name());
        dto.setUser(fetchUser(post.getUserId()));
        return dto;
    }
}
