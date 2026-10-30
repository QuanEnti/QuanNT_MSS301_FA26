package fu.quannt.user.service;

import fu.quannt.user.dto.ApiResponse;
import fu.quannt.user.dto.UserDTO;

public interface UserService {

    ApiResponse createUser(UserDTO dto);

    ApiResponse updateUser(Long userId, UserDTO dto);

    ApiResponse getUserById(Long userId);

    ApiResponse deactivateUser(Long userId);

    ApiResponse getUsers(int page, int size, String role, String status);
}
