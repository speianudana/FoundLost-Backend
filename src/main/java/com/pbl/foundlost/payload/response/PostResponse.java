package com.example.foundlost.payload.response;

import lombok.Data;

import java.util.Date;

@Data
public class PostResponse {
    Long id;
    String petImage;
    String status;
    String species;
    Boolean sterilization;
    String furColor;
    Date createdDate;
    String address;
    String contacts;
    String gender;
    String breed;
    String eyeColor;
    String specialSigns;
    Integer reward;
    Integer age;
    Long authorId;
}
