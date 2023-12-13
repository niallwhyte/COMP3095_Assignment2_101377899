package com.example.postservice.service;

import com.example.postservice.dto.PostRequest;
import com.example.postservice.dto.PostResponse;

import java.util.List;

public interface PostService {

    void createPost(String userId, PostRequest postRequest);

    Long updatePost(Long postId, PostRequest postRequest);

    void deletePost(Long postId);

    List<PostResponse> getAllPosts();

    PostResponse getPostById(Long postId);

}
