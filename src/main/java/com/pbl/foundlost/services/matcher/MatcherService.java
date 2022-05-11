package com.pbl.foundlost.services.matcher;

import com.pbl.foundlost.model.Post;
import com.pbl.foundlost.payload.dto.MatchedPostDto;
import com.pbl.foundlost.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

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

    public List<MatchedPostDto> getMatchedPosts(Post post) {
        ResponseEntity<MatchesResponse> response = restTemplate.exchange(
                "http://lostfound-matcher:5000/api/v1/posts/" + post.getUuid() + "/matches",
                HttpMethod.GET,
                null,
                MatchesResponse.class
        );

        return ofNullable(response.getBody())
                .orElseThrow(() -> new RuntimeException("Null response from matcher"))
                .getMatches()
                .stream()
                .map(match -> new MatchedPostDto(match.getMatchedPostUuid(), match.getNumberIntersectedKeywords(), getPost(match)))
                .collect(toList());
    }

    private Post getPost(MatchDto match) {
        return postRepository.findByUuid(match.getMatchedPostUuid())
                .orElseThrow(() -> new RuntimeException("Matched post not found"));
    }
}
