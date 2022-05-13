package com.pbl.foundlost.payload.dto;

import com.pbl.foundlost.model.Post;
import lombok.Value;

import java.util.UUID;

@Value
public class MatchedPostDto {
    UUID matchedPostUuid;
    Integer numberIntersectedKeywords;
    Post post;
}
