package com.example.controller;

import com.example.dto.CommentDto;
import com.example.dto.PageResponse;
import com.example.dto.PostDto;
import com.example.service.CommentService;
import com.example.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/posts")
@Slf4j
public class PostController {

    @Autowired
    private CommentService commentService;
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public ResponseEntity<PageResponse<PostDto>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        log.info("GET /posts - Page: {}, Size: {}, Sort: {}, Direction: {}, Correlation ID: {}",
                page, size, sortBy, direction);

        PageResponse<PostDto> posts = postService.getAllPosts(page, size, sortBy, direction);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDto> getPostById(@PathVariable int id,
                                               @RequestHeader("X-Correlation-Id") String correlationId) {
        log.info("GET /posts/{} - Correlation ID: {}", id, correlationId);
        PostDto post = postService.getPostById(id);
        if (post != null) {
            return ResponseEntity.ok(post);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<List<CommentDto>> getCommentsByPostId(@PathVariable int id,
                                                                @RequestHeader("X-Correlation-Id") String correlationId) {
        log.info("GET /posts/{}/comments - Correlation ID: {}", id, correlationId);
        List<CommentDto> comments = commentService.getCommentsByPostId(id);
        return ResponseEntity.ok(comments);
    }

    @PostMapping
    public ResponseEntity<PostDto> createPost(@RequestBody PostDto postDto,
                                              @RequestHeader("X-Correlation-Id") String correlationId) {
        log.info("POST /posts - Correlation ID: {}", correlationId);
        PostDto createdPost = postService.createPost(postDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostDto> updatePost(@PathVariable int id,
                                              @RequestBody PostDto postDto,
                                              @RequestHeader("X-Correlation-Id") String correlationId) {
        log.info("PUT /posts/{} - Correlation ID: {}", id, correlationId);
        PostDto updatedPost = postService.updatePost(id, postDto);
        if (updatedPost != null) {
            return ResponseEntity.ok(updatedPost);
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PostDto> partialUpdatePost(@PathVariable int id,
                                                     @RequestBody Map<String, Object> updates,
                                                     @RequestHeader("X-Correlation-Id") String correlationId) {
        log.info("PATCH /posts/{} - Correlation ID: {}", id, correlationId);
        PostDto updatedPost = postService.partialUpdatePost(id, updates);
        if (updatedPost != null) {
            return ResponseEntity.ok(updatedPost);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable int id,
                                           @RequestHeader("X-Correlation-Id") String correlationId) {
        log.info("DELETE /posts/{} - Correlation ID: {}", id, correlationId);
        boolean deleted = postService.deletePost(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
