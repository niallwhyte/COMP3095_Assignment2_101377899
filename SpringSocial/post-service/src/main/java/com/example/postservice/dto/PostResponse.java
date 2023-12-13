package com.example.postservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostResponse {

    private Long id;
    private String title;
    private String content;
    private String userId;
    private String posterName;
    private List<CommentResponse> comments;

}
