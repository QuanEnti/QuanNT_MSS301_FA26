package fu.quannt.user.service.impl;

import fu.quannt.user.dto.ApiResponse;
import fu.quannt.user.dto.PageDTO;
import fu.quannt.user.dto.UserDTO;
import fu.quannt.user.entity.Role;
import fu.quannt.user.entity.User;
import fu.quannt.user.entity.UserStatus;
import fu.quannt.user.repository.UserRepository;
import fu.quannt.user.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public ApiResponse createUser(UserDTO dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            return ApiResponse.error(3, "Username already exists");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setRole(Role.valueOf(dto.getRole()));
        user.setRatePerPost(dto.getRatePerPost());
        user.setMaxDailyPosts(dto.getMaxDailyPosts());
        user.setLevel(dto.getLevel());
        user.setStatus(UserStatus.ACTIVE);

        return ApiResponse.success(
                "User created successfully", toDTO(userRepository.save(user)));
    }

    @Override
    @Transactional
    public ApiResponse updateUser(Long userId, UserDTO dto) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ApiResponse.error(4, "User not found");
        }
        if (dto.getUsername() != null
                && userRepository.existsByUsernameAndUserIdNot(dto.getUsername(), userId)) {
            return ApiResponse.error(3, "Username already exists");
        }

        if (dto.getUsername() != null) {
            user.setUsername(dto.getUsername());
        }
        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }
        if (dto.getRole() != null) {
            user.setRole(Role.valueOf(dto.getRole()));
        }
        if (dto.getRatePerPost() != null) {
            user.setRatePerPost(dto.getRatePerPost());
        }
        if (dto.getMaxDailyPosts() != null) {
            user.setMaxDailyPosts(dto.getMaxDailyPosts());
        }
        if (dto.getLevel() != null) {
            user.setLevel(dto.getLevel());
        }
        if (dto.getStatus() != null) {
            user.setStatus(UserStatus.valueOf(dto.getStatus()));
        }

        return ApiResponse.success(
                "User updated successfully", toDTO(userRepository.save(user)));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse getUserById(Long userId) {
        return userRepository.findById(userId)
                .map(user -> ApiResponse.success("Successful", toDTO(user)))
                .orElseGet(() -> ApiResponse.error(4, "User not found"));
    }

    @Override
    @Transactional
    public ApiResponse deactivateUser(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ApiResponse.error(4, "User not found");
        }

        user.setStatus(UserStatus.DEACTIVATED);
        userRepository.save(user);
        return ApiResponse.success("User deactivated successfully", null);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse getUsers(int page, int size, String role, String status) {
        if (page < 0) {
            return ApiResponse.error(2, "page must be greater than or equal to 0");
        }
        if (size < 1 || size > 100) {
            return ApiResponse.error(2, "size must be between 1 and 100");
        }

        Role roleFilter;
        UserStatus statusFilter;
        try {
            roleFilter = parseRole(role);
            statusFilter = parseStatus(status);
        } catch (IllegalArgumentException exception) {
            return ApiResponse.error(2, exception.getMessage());
        }

        Page<User> result = userRepository.search(
                roleFilter,
                statusFilter,
                PageRequest.of(page, size, Sort.by("userId").ascending()));
        List<UserDTO> users = result.getContent().stream()
                .map(this::toDTO)
                .toList();
        PageDTO pageDTO = new PageDTO(
                result.getSize(),
                result.getNumber(),
                result.getTotalPages(),
                result.getTotalElements(),
                result.isFirst(),
                result.isLast(),
                users);
        return ApiResponse.success("Successful", pageDTO);
    }

    private Role parseRole(String role) {
        if (role == null || role.isBlank()) {
            return null;
        }
        try {
            return Role.valueOf(role);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                    "role must be USER, CREATOR, or ADMIN");
        }
    }

    private UserStatus parseStatus(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }
        try {
            return UserStatus.valueOf(status);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                    "status must be ACTIVE, SUSPENDED, or DEACTIVATED");
        }
    }

    private UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setUserId(user.getUserId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole().name());
        dto.setRatePerPost(user.getRatePerPost());
        dto.setMaxDailyPosts(user.getMaxDailyPosts());
        dto.setLevel(user.getLevel());
        dto.setStatus(user.getStatus().name());
        return dto;
    }
}
