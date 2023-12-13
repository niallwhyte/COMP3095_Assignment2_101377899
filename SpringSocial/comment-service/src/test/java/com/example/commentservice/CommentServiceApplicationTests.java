package com.example.commentservice;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.containers.PostgreSQLContainer;

import com.example.commentservice.dto.CommentRequest;
import com.example.commentservice.model.Comment;
import com.example.commentservice.repository.CommentRepository;
import com.example.commentservice.service.CommentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.PostgreSQLContainer;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
class CommentServiceApplicationTests {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;


    private CommentRequest getCommentRequest(){
        return CommentRequest.builder()
                .text("Hi")
                .userId("abc934")
                .postId(2L)
                .commenterName("Jimmy Smith")
                .build();
    }

    private List<Comment> getCommentList() {
        List<Comment> comments = new ArrayList<>();

        Comment comment = Comment.builder()
                .text("Hi")
                .userId("abc934")
                .postId(2L)
                .commenterName("Jimmy Smith")
                .build();
        comments.add(comment);
        return comments;
    }

    @Test
    void createComment() throws Exception{
        Comment savedComment = commentRepository.save(new Comment(2L, "Hello", "abc934", 2L,"Jimmy Smith"));

        assertThat(savedComment.getId()).isGreaterThan(0);
    }

    @Test
    void getAllComments() throws Exception{
        commentRepository.saveAll(getCommentList());

//        assertThat(!getCommentList().isEmpty());
    }

    @Test
    void updateComment() throws Exception{
        Comment saveComment = Comment.builder()
                .text("Hey")
                .userId("abc934")
                .postId(2L)
                .commenterName("Jimmy Smith")
                .build();
        commentRepository.save(saveComment);

        saveComment.setText("Helloolol");
        commentRepository.save(saveComment);

        Comment updatedComment = commentRepository.findById(saveComment.getId())
                .orElseThrow(()-> new NoSuchElementException("Comment not found"));

        assertEquals("Helloolol", updatedComment.getText());
    }

    @Test
    void deleteComment(){
        Comment savedComment = Comment.builder()
                .text("Hey")
                .userId("abc934")
                .postId(2L)
                .commenterName("Jimmy Smith")
                .build();
        commentRepository.save(savedComment);

        commentRepository.deleteById(savedComment.getId());

        Comment deletedComment = commentRepository.findById(savedComment.getId()).orElse(null);

        assertNull(deletedComment, "Comment deleted");

    }
}

