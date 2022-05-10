package com.pbl.foundlost.payload.response;

import com.pbl.foundlost.model.Post;
import lombok.Data;
import lombok.Value;

import java.util.Date;
import java.util.List;

@Value
public class PostResponse {
    Post post;
    List<Post> matchingPosts;
}
