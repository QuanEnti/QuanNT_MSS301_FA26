package fu.quannt.post.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import fu.quannt.post.client.UserClient;
import fu.quannt.post.dto.ApiResponse;
import fu.quannt.post.dto.PostDTO;
import fu.quannt.post.dto.UserDTO;
import fu.quannt.post.entity.Post;
import fu.quannt.post.entity.PostStatus;
import fu.quannt.post.repository.PostRepository;
import fu.quannt.post.service.impl.PostServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserClient userClient;

    private PostServiceImpl postService;

    @BeforeEach
    void setUp() {
        postService = new PostServiceImpl(
                postRepository, userClient, new ObjectMapper());
    }

    @Test
    void createPostCalculatesCostAndSchedulesCampaign() {
        PostDTO request = validRequest();
        when(userClient.getUserById(1L))
                .thenReturn(ApiResponse.success("Successful", activeUser()));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> {
            Post post = invocation.getArgument(0);
            post.setPostId(20L);
            return post;
        });

        ApiResponse response = postService.createPost(request);

        assertEquals(1, response.getStatus());
        PostDTO created = (PostDTO) response.getData();
        assertEquals(PostStatus.SCHEDULED.name(), created.getStatus());
        assertEquals(0, new BigDecimal("1500000").compareTo(created.getTotalCost()));
    }

    @Test
    void createPostRejectsUnknownUser() {
        PostDTO request = validRequest();
        when(userClient.getUserById(1L))
                .thenReturn(ApiResponse.error(4, "User not found"));

        ApiResponse response = postService.createPost(request);

        assertEquals(4, response.getStatus());
        verify(postRepository, never()).save(any());
    }

    @Test
    void createPostRejectsInactiveUser() {
        UserDTO user = activeUser();
        user.setStatus("SUSPENDED");
        when(userClient.getUserById(1L))
                .thenReturn(ApiResponse.success("Successful", user));

        ApiResponse response = postService.createPost(validRequest());

        assertEquals(5, response.getStatus());
        verify(postRepository, never()).save(any());
    }

    @Test
    void createPostEnforcesCreatorDailyLimit() {
        UserDTO user = activeUser();
        user.setMaxDailyPosts(1);
        when(userClient.getUserById(1L))
                .thenReturn(ApiResponse.success("Successful", user));

        ApiResponse response = postService.createPost(validRequest());

        assertEquals(2, response.getStatus());
        verify(postRepository, never()).save(any());
    }

    @Test
    void updatePostValidatesEffectiveDatesBeforeMutation() {
        Post existing = existingPost();
        when(postRepository.findById(1L)).thenReturn(Optional.of(existing));
        PostDTO request = new PostDTO();
        request.setEndDate(Date.valueOf("2026-07-24"));

        ApiResponse response = postService.updatePost(1L, request);

        assertEquals(2, response.getStatus());
        assertEquals(Date.valueOf("2026-07-28"), existing.getEndDate());
        verify(postRepository, never()).save(any());
    }

    @Test
    void cancelPostUsesSoftDelete() {
        Post existing = existingPost();
        when(postRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(postRepository.save(existing)).thenReturn(existing);

        ApiResponse response = postService.cancelPost(1L);

        assertEquals(1, response.getStatus());
        assertNull(response.getData());
        assertEquals(PostStatus.CANCELLED, existing.getStatus());
    }

    private PostDTO validRequest() {
        PostDTO dto = new PostDTO();
        dto.setAdvertiserName("Shop ABC");
        dto.setAdvertiserEmail("ads@example.com");
        dto.setAdvertiserPhone("0900000000");
        dto.setStartDate(Date.valueOf("2026-07-25"));
        dto.setEndDate(Date.valueOf("2026-07-28"));
        dto.setNumberOfPosts(2);
        dto.setUserId(1L);
        return dto;
    }

    private UserDTO activeUser() {
        UserDTO dto = new UserDTO();
        dto.setUserId(1L);
        dto.setUsername("creator_a");
        dto.setEmail("a@example.com");
        dto.setRole("CREATOR");
        dto.setRatePerPost(new BigDecimal("500000"));
        dto.setMaxDailyPosts(3);
        dto.setLevel(1);
        dto.setStatus("ACTIVE");
        return dto;
    }

    private Post existingPost() {
        Post post = new Post();
        post.setPostId(1L);
        post.setAdvertiserName("Shop ABC");
        post.setAdvertiserEmail("ads@example.com");
        post.setAdvertiserPhone("0900000000");
        post.setStartDate(Date.valueOf("2026-07-25"));
        post.setEndDate(Date.valueOf("2026-07-28"));
        post.setNumberOfPosts(2);
        post.setTotalCost(new BigDecimal("1500000"));
        post.setStatus(PostStatus.SCHEDULED);
        post.setUserId(1L);
        return post;
    }
}
