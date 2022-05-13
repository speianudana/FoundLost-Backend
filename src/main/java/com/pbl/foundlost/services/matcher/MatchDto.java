package com.pbl.foundlost.services.matcher;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.UUID;

@Data
public class MatchDto {
    @JsonProperty("matched_post_uuid")
    private final UUID matchedPostUuid;
    @JsonProperty("number_intersected_keywords")
    private final Integer numberIntersectedKeywords;
}
