package com.example.service;

import com.example.dto.PageResponse;
import com.example.dto.PostDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
@Slf4j
public class PostService {

    private final WebClient webClient;
    private static final String API_BASE_URL = "https://jsonplaceholder.typicode.com";

    @Autowired
    public PostService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(API_BASE_URL).build();
    }

    public PageResponse<PostDto> getAllPosts(int page, int size, String sortBy, String direction) {
        log.info("Fetching posts from external API with page={}, size={}, sortBy={}, direction={}",
                page, size, sortBy, direction);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/posts")
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .queryParam("sortBy", sortBy)
                        .queryParam("direction", direction)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<PageResponse<PostDto>>() {})
                .block();
    }

    public PostDto getPostById(int id) {
        log.info("Fetching post with id: {} from external API", id);
        return webClient.get()
                .uri("/posts/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(PostDto.class)
                .block();
    }

    public PostDto createPost(PostDto postDto) {
        log.info("Creating new post with title: {} via external API", postDto.getTitle());
        return webClient.post()
                .uri("/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(postDto)
                .retrieve()
                .bodyToMono(PostDto.class)
                .block();
    }

    public PostDto updatePost(int id, PostDto postDto) {
        log.info("Updating post with id: {} via external API", id);
        return webClient.put()
                .uri("/posts/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(postDto)
                .retrieve()
                .bodyToMono(PostDto.class)
                .block();
    }

    public PostDto partialUpdatePost(int id, Map<String, Object> updates) {
        log.info("Partially updating post with id: {} via external API", id);
        return webClient.patch()
                .uri("/posts/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(updates)
                .retrieve()
                .bodyToMono(PostDto.class)
                .block();
    }

    public boolean deletePost(int id) {
        log.info("Deleting post with id: {} via external API", id);
        return webClient.delete()
                .uri("/posts/{id}", id)
                .retrieve()
                .toBodilessEntity()
                .map(response -> response.getStatusCode().is2xxSuccessful())
                .block();
    }
}
