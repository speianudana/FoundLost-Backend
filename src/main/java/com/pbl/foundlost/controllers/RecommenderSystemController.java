package com.example.foundlost.controllers;

import com.thesis.findpet.model.Post;
import com.thesis.findpet.payload.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RecommenderSystemController {

    @Autowired
    RestTemplate restTemplate;

    private static String token;

    public static String authenticateRecommenderSystem() {

        // request url
        String url = "http://localhost:5005/api/auth";

        // create an instance of RestTemplate
        RestTemplate restTemplate = new RestTemplate();

        // create headers
        HttpHeaders headers = new HttpHeaders();
        // set `content-type` header
        headers.setContentType(MediaType.APPLICATION_JSON);
            // set `accept` header
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        // request body parameters
        Map<String, Object> map = new HashMap<>();
        map.put("username", "Dana");
        map.put("password", "123");

        // build the request
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);

        // send POST request
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

        // check response
        if (response.getStatusCode() == HttpStatus.OK) {
            System.out.println("Authentication  Successful");
            token = response.getBody().split(":")[1].replace("\"","");
            token = token.substring(0,token.length()-1);
            return response.getBody();
        } else {
            System.out.println("Request Failed");
            System.out.println(response.getStatusCode());
            return response.getStatusCode().toString();
        }
    }

}
