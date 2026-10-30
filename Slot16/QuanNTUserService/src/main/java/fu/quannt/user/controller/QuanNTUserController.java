package fu.quannt.user.controller;

import fu.quannt.user.common.OnCreate;
import fu.quannt.user.dto.ApiResponse;
import fu.quannt.user.dto.UserDTO;
import fu.quannt.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class QuanNTUserController {

    private final UserService userService;

    public QuanNTUserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse> createUser(
            @Validated(OnCreate.class) @RequestBody UserDTO dto) {
        return toResponse(userService.createUser(dto), HttpStatus.CREATED);
    }

    @PutMapping(value = "/{userId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody UserDTO dto) {
        return toResponse(userService.updateUser(userId, dto), HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse> getUser(@PathVariable Long userId) {
        return toResponse(userService.getUserById(userId), HttpStatus.OK);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse> deactivateUser(@PathVariable Long userId) {
        return toResponse(userService.deactivateUser(userId), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status) {
        return toResponse(
                userService.getUsers(page, size, role, status), HttpStatus.OK);
    }

    private ResponseEntity<ApiResponse> toResponse(
            ApiResponse response, HttpStatus successStatus) {
        HttpStatus httpStatus = switch (response.getStatus()) {
            case 1 -> successStatus;
            case 2 -> HttpStatus.NOT_ACCEPTABLE;
            case 3 -> HttpStatus.IM_USED;
            case 4 -> HttpStatus.NOT_FOUND;
            case 5 -> HttpStatus.BAD_REQUEST;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
        return ResponseEntity.status(httpStatus).body(response);
    }
}
