package com.pbl.foundlost.payload.response;

import lombok.Data;

import java.util.Date;

@Data
public class PostResponse {
    Long id;
    String image;
    String status;
    String type;
    Date createdDate;
    String address;
    String contacts;
    Integer reward;
    Long authorId;
    String breed;
    String details;
    String gender;
    String age;
    String eyeColor;
    String furColor;
    String specialSigns;
    String name;
    String nationality;
    String species;
}
