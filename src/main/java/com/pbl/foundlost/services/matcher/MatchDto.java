package com.pbl.foundlost.services.matcher;

import lombok.Value;

import java.util.UUID;

@Value
public class MatchDto {
    UUID matchedPostUuid;
    Integer numberIntersectedKeywords;
}
