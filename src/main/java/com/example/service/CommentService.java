package com.example.service;

import com.example.dto.CommentDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CommentService {

    private final List<CommentDto> comments = new ArrayList<>();
    private final AtomicInteger idCounter = new AtomicInteger(1);
    private final WebClient webClient;
    private static final String API_BASE_URL = "https://jsonplaceholder.typicode.com";

    @Autowired
    public CommentService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(API_BASE_URL).build();
    }

    public List<CommentDto> getCommentsByPostId(int postId) {
        log.info("Fetching comments for post id: {} from external API", postId);
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/comments")
                        .queryParam("postId", postId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(CommentDto.class)
                .collectList()
                .block();
    }

    public List<CommentDto> getAllComments() {
        log.info("Fetching all comments from external API");
        return webClient.get()
                .uri("/comments")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(CommentDto.class)
                .collectList()
                .block();
    }

    public CommentDto createComment(CommentDto commentDto) {
        log.info("Creating new comment for post id: {} via external API", commentDto.getPostId());
        return webClient.post()
                .uri("/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(commentDto)
                .retrieve()
                .bodyToMono(CommentDto.class)
                .block();
    }
}
