package com.pbl.foundlost.services;

import com.pbl.foundlost.model.GeographicalLocation;
import com.pbl.foundlost.model.Post;
import com.pbl.foundlost.payload.dto.NearPostDto;
import com.pbl.foundlost.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;
import java.util.Objects;

import static java.math.BigDecimal.TEN;
import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class PostsService {
    private final BigDecimal RANGE = TEN;

    private final PostRepository postRepository;

    public List<NearPostDto> getNearPosts(Post post) {
        if (isNull(post.getGeographicalLocation())) {
            return emptyList();
        }

        return postRepository.findAll()
                .stream()
                .filter(candidate -> !candidate.getType().equals(post.getType()))
                .map(candidate -> mapIfInRange(post, candidate))
                .filter(Objects::nonNull)
                .collect(toList());
    }

    @Nullable
    private NearPostDto mapIfInRange(Post post, Post candidate) {
        BigDecimal distance = getDistance(post, candidate);
        if (distance.compareTo(RANGE) <= 0) {
            return new NearPostDto(post, distance);
        }
        return null;
    }

    private BigDecimal getDistance(Post post, Post candidate) {
        MathContext mc = new MathContext(10);
        GeographicalLocation currentPostPosition = post.getGeographicalLocation();

        GeographicalLocation candidatePosition = candidate.getGeographicalLocation();
        BigDecimal square1 = currentPostPosition.getLatitude().subtract(candidatePosition.getLatitude()).pow(2, mc);
        BigDecimal square2 = currentPostPosition.getLongitude().subtract(candidatePosition.getLongitude()).pow(2, mc);
        return square1.add(square2).sqrt(mc);
    }
}
