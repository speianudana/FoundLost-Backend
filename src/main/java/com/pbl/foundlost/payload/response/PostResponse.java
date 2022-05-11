package com.pbl.foundlost.payload.response;

import com.pbl.foundlost.model.Post;
import com.pbl.foundlost.payload.dto.MatchedPostDto;
import lombok.Value;

import java.util.List;

@Value
public class PostResponse {
    Post post;
    List<MatchedPostDto> matchingPosts;
}
