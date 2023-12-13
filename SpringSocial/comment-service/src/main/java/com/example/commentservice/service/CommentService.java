package com.example.commentservice.service;

import com.example.commentservice.dto.CommentRequest;
import com.example.commentservice.dto.CommentResponse;
import java.util.List;

public interface CommentService {

    void createComment(CommentRequest commentRequest);

    Long updateComment(Long commentId, CommentRequest commentRequest);

    void deleteComment(Long commentId);

    List<CommentResponse> getAllComments();

    List<CommentResponse> getCommentsByPostId(Long postId);

}
