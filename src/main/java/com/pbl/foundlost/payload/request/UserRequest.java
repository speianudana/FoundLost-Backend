package com.pbl.foundlost.payload.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UserRequest {
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String password;
    private String contacts;
    private String address;
    private MultipartFile userPhoto;
}

