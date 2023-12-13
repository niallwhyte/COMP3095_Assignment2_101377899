package com.example.commentservice.controller;

import com.example.commentservice.dto.CommentRequest;
import com.example.commentservice.dto.CommentResponse;
import com.example.commentservice.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comment/")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/{postId}/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void createComment(@PathVariable Long postId, @PathVariable String userId, @RequestBody CommentRequest commentRequest) {
        commentRequest.setPostId(postId);
        commentRequest.setUserId(userId);
        commentService.createComment(commentRequest);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CommentResponse> getAllComments(){
        return commentService.getAllComments();
    }

    @GetMapping("/post/{postId}")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentResponse> getCommentsByPostId(@PathVariable Long postId) {
        return commentService.getCommentsByPostId(postId);
    }

    @PutMapping({"/{commentId}"})
    public ResponseEntity<?> updateComment(@PathVariable("commentId") Long commentId, @RequestBody CommentRequest commentRequest){

        Long updatedCommentId = commentService.updateComment(commentId, commentRequest);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "/api/comment/" + updatedCommentId);

        return new ResponseEntity<>(headers, HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable("commentId") Long CommentId){
        commentService.deleteComment(CommentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
