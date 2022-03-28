package com.example.foundlost.payload.response;

import lombok.Data;

@Data
public class UserResponse {
    Long id;
    String firstName;
    String lastName;
    String username;
    String email;
    String contacts;
    String address;
    String userPhoto;

}
