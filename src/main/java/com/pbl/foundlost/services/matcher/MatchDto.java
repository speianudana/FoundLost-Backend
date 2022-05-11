package com.pbl.foundlost.services.matcher;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.util.UUID;

@Value
public class MatchDto {
    @JsonProperty("matched_post_uuid")
    UUID matchedPostUuid;
    @JsonProperty("number_intersected_keywords")
    Integer numberIntersectedKeywords;
}
