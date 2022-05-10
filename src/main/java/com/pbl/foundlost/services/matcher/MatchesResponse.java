package com.pbl.foundlost.services.matcher;

import lombok.Value;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.List;

@Value
public class MatchesResponse {
    @NonNull
    String status;
    @NonNull
    List<MatchDto> matches;
    @Nullable
    String message;
}
