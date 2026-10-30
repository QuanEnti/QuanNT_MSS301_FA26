package fu.quannt.user.service;

import fu.quannt.user.dto.ApiResponse;
import fu.quannt.user.dto.UserDTO;
import fu.quannt.user.entity.Role;
import fu.quannt.user.entity.User;
import fu.quannt.user.entity.UserStatus;
import fu.quannt.user.repository.UserRepository;
import fu.quannt.user.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void createUserSetsActiveStatus() {
        UserDTO request = validRequest();
        when(userRepository.existsByUsername("creator_new")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setUserId(10L);
            return user;
        });

        ApiResponse response = userService.createUser(request);

        assertEquals(1, response.getStatus());
        UserDTO created = (UserDTO) response.getData();
        assertEquals(10L, created.getUserId());
        assertEquals("ACTIVE", created.getStatus());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals(UserStatus.ACTIVE, captor.getValue().getStatus());
    }

    @Test
    void createUserRejectsDuplicateUsername() {
        when(userRepository.existsByUsername("creator_new")).thenReturn(true);

        ApiResponse response = userService.createUser(validRequest());

        assertEquals(3, response.getStatus());
        assertNull(response.getData());
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUserAppliesOnlySuppliedFields() {
        User existing = existingUser();
        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.save(existing)).thenReturn(existing);
        UserDTO request = new UserDTO();
        request.setEmail("updated@example.com");

        ApiResponse response = userService.updateUser(1L, request);

        assertEquals(1, response.getStatus());
        assertEquals("updated@example.com", existing.getEmail());
        assertEquals("creator_a", existing.getUsername());
    }

    @Test
    void deactivateUserUsesSoftDelete() {
        User existing = existingUser();
        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.save(existing)).thenReturn(existing);

        ApiResponse response = userService.deactivateUser(1L);

        assertEquals(1, response.getStatus());
        assertNull(response.getData());
        assertEquals(UserStatus.DEACTIVATED, existing.getStatus());
    }

    @Test
    void getUsersRejectsPageSizesOverOneHundred() {
        ApiResponse response = userService.getUsers(0, 101, null, null);

        assertEquals(2, response.getStatus());
        verify(userRepository, never()).search(any(), any(), any(Pageable.class));
    }

    @Test
    void getUsersAcceptsSupportedFilters() {
        when(userRepository.search(
                any(Role.class), any(UserStatus.class), any(Pageable.class)))
                .thenReturn(Page.empty());

        ApiResponse response = userService.getUsers(
                0, 10, "CREATOR", "ACTIVE");

        assertEquals(1, response.getStatus());
        assertTrue(response.getData() != null);
    }

    private UserDTO validRequest() {
        UserDTO dto = new UserDTO();
        dto.setUsername("creator_new");
        dto.setEmail("new@example.com");
        dto.setRole("CREATOR");
        dto.setRatePerPost(new BigDecimal("250000"));
        dto.setMaxDailyPosts(3);
        dto.setLevel(1);
        return dto;
    }

    private User existingUser() {
        User user = new User();
        user.setUserId(1L);
        user.setUsername("creator_a");
        user.setEmail("a@example.com");
        user.setRole(Role.CREATOR);
        user.setRatePerPost(new BigDecimal("500000"));
        user.setMaxDailyPosts(3);
        user.setLevel(1);
        user.setStatus(UserStatus.ACTIVE);
        return user;
    }
}
