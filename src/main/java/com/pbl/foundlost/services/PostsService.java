package com.pbl.foundlost.services;

import com.pbl.foundlost.model.GeographicalLocation;
import com.pbl.foundlost.model.Post;
import com.pbl.foundlost.payload.dto.NearPostDto;
import com.pbl.foundlost.repository.PostRepository;
import com.pbl.foundlost.services.matcher.MatchDto;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;
import java.util.Objects;

import static com.pbl.foundlost.services.DistanceUnit.KM;
import static com.pbl.foundlost.services.DistanceUnit.NAUTICAL_MILES;
import static java.math.BigDecimal.valueOf;
import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class PostsService {
    private final BigDecimal KM_RANGE = new BigDecimal("1");

    private final PostRepository postRepository;

    public Post getPost(MatchDto match) {
        return postRepository.findByUuid(match.getMatchedPostUuid()).orElse(null);
    }

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
        if (distance.compareTo(KM_RANGE) <= 0) {
            return new NearPostDto(candidate, distance);
        }
        return null;
    }

    private BigDecimal getDistance(Post post, Post candidate) {
        MathContext mc = new MathContext(14);
        GeographicalLocation currentPostPosition = post.getGeographicalLocation();
        GeographicalLocation candidatePosition = candidate.getGeographicalLocation();
        if (isNull(currentPostPosition) || isNull(candidatePosition)) {
            return BigDecimal.ZERO;
        }

        double lat1 = currentPostPosition.getLatitude().doubleValue();
        double lon1 = currentPostPosition.getLongitude().doubleValue();

        double lat2 = candidatePosition.getLatitude().doubleValue();
        double lon2 = candidatePosition.getLongitude().doubleValue();

        return valueOf(getGeographicalDistance(lat1, lon1, lat2, lon2, KM));
    }

    public double getGeographicalDistance(double lat1, double lon1, double lat2, double lon2, DistanceUnit unit) {
        if ((lat1 == lat2) && (lon1 == lon2)) {
            return 0;
        } else {
            double theta = lon1 - lon2;
            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2))
                    + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515;
            if (KM.equals(unit)) {
                dist = dist * 1.609344;
            } else if (NAUTICAL_MILES.equals(unit)) {
                dist = dist * 0.8684;
            }
            return (dist);
        }
    }
}