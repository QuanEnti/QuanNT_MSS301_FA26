package fu.quannt.post.controller;

import fu.quannt.post.common.OnCreate;
import fu.quannt.post.dto.ApiResponse;
import fu.quannt.post.dto.PostDTO;
import fu.quannt.post.service.PostService;
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
@RequestMapping(value = "/api/posts", produces = MediaType.APPLICATION_JSON_VALUE)
public class QuanNTPostController {

    private final PostService postService;

    public QuanNTPostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse> createPost(
            @Validated(OnCreate.class) @RequestBody PostDTO dto) {
        return toResponse(postService.createPost(dto), HttpStatus.CREATED);
    }

    @PutMapping(value = "/{postId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse> updatePost(
            @PathVariable Long postId,
            @Valid @RequestBody PostDTO dto) {
        return toResponse(postService.updatePost(postId, dto), HttpStatus.OK);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse> getPost(@PathVariable Long postId) {
        return toResponse(postService.getPostById(postId), HttpStatus.OK);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse> cancelPost(@PathVariable Long postId) {
        return toResponse(postService.cancelPost(postId), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String advertiserName,
            @RequestParam(required = false) String status) {
        return toResponse(
                postService.getPosts(page, size, advertiserName, status),
                HttpStatus.OK);
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
