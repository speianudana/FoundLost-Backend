package com.pbl.foundlost.services.matcher;

import lombok.Data;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.List;

@Data
public class MatchesResponse {
    private final String status;
    @NonNull
    private final List<MatchDto> matches;
    @Nullable
    private final String message;
}
