package com.pbl.foundlost.services.matcher;

import com.pbl.foundlost.model.Post;
import com.pbl.foundlost.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;

@Service
@RequiredArgsConstructor
public class MatcherService {
    private final RestTemplate restTemplate;
    private final PostRepository postRepository;

    public CreatePostResponse createPost(Post post) {
        ResponseEntity<CreatePostResponse> response = restTemplate.exchange(
                "http://lostfound-matcher:5000/api/v1/posts/process",
                HttpMethod.POST,
                new HttpEntity<>(post),
                CreatePostResponse.class
        );

        return ofNullable(response.getBody())
                .orElseThrow(() -> new RuntimeException("Null response from matcher"));
    }

    public MatchesResponse getMatchedPosts(Post post) {
        ResponseEntity<MatchesResponse> response = restTemplate.exchange(
                "http://lostfound-matcher:5000/api/v1/posts/" + post.getUuid() + "/matches",
                HttpMethod.GET,
                null,
                MatchesResponse.class
        );

        return ofNullable(response.getBody())
                .orElseThrow(() -> new RuntimeException("Null response from matcher"));
    }

    public void deletePost(Post post) {
        if (isNull(post.getUuid())) {
            throw new RuntimeException("Null post uuid");
        }

        restTemplate.exchange(
                "http://lostfound-matcher:5000/api/v1/posts/" + post.getUuid(),
                HttpMethod.DELETE,
                null,
                DeletePostResponse.class
        );
    }
}
