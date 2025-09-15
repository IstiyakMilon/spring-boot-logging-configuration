package com.example.controller;

import com.example.dto.CommentDto;
import com.example.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
@Slf4j
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    public ResponseEntity<List<CommentDto>> getComments(@RequestParam(required = false) Integer postId,
                                                        @RequestHeader("X-Correlation-Id") String correlationId) {
        log.info("GET /comments - Post ID: {}, Correlation ID: {}", postId, correlationId);
        List<CommentDto> comments;
        if (postId != null) {
            comments = commentService.getCommentsByPostId(postId);
        } else {
            comments = commentService.getAllComments();
        }
        return ResponseEntity.ok(comments);
    }
}

