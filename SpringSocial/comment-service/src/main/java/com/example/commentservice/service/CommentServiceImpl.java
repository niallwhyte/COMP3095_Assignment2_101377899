package com.example.commentservice.service;

import com.example.commentservice.dto.CommentRequest;
import com.example.commentservice.dto.CommentResponse;
import com.example.commentservice.dto.Post;
import com.example.commentservice.dto.User;
import com.example.commentservice.model.Comment;
import com.example.commentservice.repository.CommentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final WebClient webClient;

    private static final String userUrl = "http://user-service:8088/api/user/";
    private static final String postUrl = "http://post-service:8082/api/post/";

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository, WebClient webClient) {
        this.commentRepository = commentRepository;
        this.webClient = webClient;
    }

    @Override
    public void createComment(CommentRequest commentRequest) {
        log.info("Creating a new comment {}", commentRequest.getText());

        if (!userExists(commentRequest.getUserId())) {
            throw new IllegalArgumentException("Invalid User ID");
        }

        if (!postExists(commentRequest.getPostId())) {
            throw new IllegalArgumentException("Invalid Post ID");
        }

        User user = getUserById(commentRequest.getUserId());

        Comment comment = Comment.builder()
                .text(commentRequest.getText())
                .postId(commentRequest.getPostId())
                .userId(commentRequest.getUserId())
                .commenterName(user.getName())
                .build();

        commentRepository.save(comment);
        log.info("Comment {} is saved", comment.getId());
    }

    @Override
    public Long updateComment(Long commentId, CommentRequest commentRequest) {
        Optional<Comment> optionalComment = commentRepository.findById(commentId);

        if (optionalComment.isPresent()) {
            Comment comment = optionalComment.get();
            comment.setText(commentRequest.getText());
            commentRepository.save(comment);
            log.info("Comment {} is updated", comment.getId());
            return comment.getId();
        }

        throw new IllegalArgumentException("Comment not found with ID: " + commentId);
    }

    @Override
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
        log.info("Comment {} is deleted", commentId);
    }

    @Override
    public List<CommentResponse> getAllComments() {
        List<Comment> comments = commentRepository.findAll();
        return comments.stream()
                .map(this::mapToCommentResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentResponse> getCommentsByPostId(Long postId) {
        List<Comment> comments = commentRepository.findByPostId(postId);
        return comments.stream()
                .map(this::mapToCommentResponse)
                .collect(Collectors.toList());
    }

    private CommentResponse mapToCommentResponse(Comment comment) {
        User user = getUserById(comment.getUserId());
        if (user == null) {
            throw new RuntimeException("User not found for ID: " + comment.getUserId());
        }

        return CommentResponse.builder()
                .id(comment.getId())
                .text(comment.getText())
                .postId(comment.getPostId())
                .userId(comment.getUserId())
                .commenterName(user.getName())
                .build();
    }

    private boolean userExists(String userId) {
        try {
            webClient.get().uri(userUrl + userId).retrieve().bodyToMono(User.class).block();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean postExists(Long postId) {
        try {
            webClient.get()
                    .uri(postUrl + postId)
                    .retrieve()
                    .bodyToMono(Post.class)
                    .block();
            return true;
        } catch (Exception e) {
            // Log the exception or handle it as needed
            log.error("Error checking if post exists: " + e.getMessage());
            return false;
        }
    }

    private User getUserById(String userId) {
        return webClient.get()
                .uri(userUrl + userId)
                .retrieve()
                .bodyToMono(User.class)
                .block();
    }





}

