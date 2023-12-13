package com.example.postservice.service;

import com.example.postservice.dto.CommentResponse;
import com.example.postservice.dto.PostRequest;
import com.example.postservice.dto.PostResponse;
import com.example.postservice.dto.User;
import com.example.postservice.model.Post;
import com.example.postservice.repository.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final WebClient webClient;
    private final WebClient webClient1;

    private static final String USER_SERVICE_URL = "http://user-service:8088/api/user/";
    private static final String COMMENT_SERVICE_URL = "http://comment-service:8083/api/comment/post/";

    @Autowired
    public PostServiceImpl(PostRepository postRepository, WebClient webClient,WebClient webClient1) {
        this.postRepository = postRepository;
        this.webClient = webClient;
        this.webClient1 = webClient1;
    }

    @Override
    public void createPost(String userId, PostRequest postRequest) {
        User user = getUserById(userId);
        if (user == null) {
            throw new RuntimeException("Invalid user!");
        }

        Post post = Post.builder()
                .title(postRequest.getTitle())
                .content(postRequest.getContent())
                .userId(userId)
                .posterName(user.getName())
                .build();

        postRepository.save(post);
        log.info("Post {} is saved", post.getId());
    }

    @Override
    public Long updatePost(Long postId, PostRequest postRequest) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found!"));
        post.setTitle(postRequest.getTitle());
        post.setContent(postRequest.getContent());
        postRepository.save(post);
        log.info("Post {} is updated", post.getId());
        return post.getId();
    }

    @Override
    public void deletePost(Long postId) {
        postRepository.deleteById(postId);
        log.info("Post {} is deleted", postId);
    }

    @Override
    public List<PostResponse> getAllPosts() {
        return postRepository.findAll()
                .stream()
                .map(this::mapToPostResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PostResponse getPostById(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found!"));

        User user = getUserById(post.getUserId());
        if (user == null) {
            throw new RuntimeException("User not found for ID: " + post.getUserId());
        }

        List<CommentResponse> comments = getCommentsForPost(postId);

        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .userId(post.getUserId())
                .posterName(user.getName())
                .comments(comments)
                .build();
    }

    private PostResponse mapToPostResponse(Post post) {
        User user = getUserById(post.getUserId());
        if (user == null) {
            throw new RuntimeException("User not found for ID: " + post.getUserId());
        }

        List<CommentResponse> comments = getCommentsForPost(post.getId());

        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .userId(post.getUserId())
                .posterName(user.getName())
                .comments(comments)
                .build();
    }

    private User getUserById(String userId) {
        log.info(USER_SERVICE_URL + userId);
        return webClient1.get()
                .uri(USER_SERVICE_URL + userId)
                .retrieve()
                .bodyToMono(User.class)
                .block(); //
    }

    private List<CommentResponse> getCommentsForPost(Long postId) {
        try {
            log.info(COMMENT_SERVICE_URL + postId);
            return webClient.get()
                    .uri(COMMENT_SERVICE_URL + postId)
                    .retrieve()
                    .bodyToFlux(CommentResponse.class)
                    .collectList()
                    .block();

        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {

                return new ArrayList<>();
            }
            throw e;
        }
    }
}


