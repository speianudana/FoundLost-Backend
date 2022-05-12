package com.pbl.foundlost.payload.dto;

import com.pbl.foundlost.model.Post;
import lombok.Value;

import java.math.BigDecimal;


@Value
public class NearPostDto {
    Post post;
    BigDecimal distance;
}
