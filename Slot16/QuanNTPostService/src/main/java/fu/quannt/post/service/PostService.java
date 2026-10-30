package fu.quannt.post.service;

import fu.quannt.post.dto.ApiResponse;
import fu.quannt.post.dto.PostDTO;

public interface PostService {

    ApiResponse createPost(PostDTO dto);

    ApiResponse updatePost(Long postId, PostDTO dto);

    ApiResponse getPostById(Long postId);

    ApiResponse cancelPost(Long postId);

    ApiResponse getPosts(int page, int size, String advertiserName, String status);
}
