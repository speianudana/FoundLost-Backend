package com.pbl.foundlost.payload.response;

import lombok.Data;

import java.util.HashMap;

@Data
public class Response {
    String status;
    HashMap<Integer, String> results;
}
