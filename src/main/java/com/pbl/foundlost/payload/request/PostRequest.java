package com.pbl.foundlost.payload.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Data
public class PostRequest {
    //All
    Long id;
    MultipartFile image;
    Long authorId;
    String status;
    String type;
    Date createdDate;
    String address;
    String contacts;
    String details;
//=======================
    //Things, animals
    Long reward;
    //Animals
    String breed;
    String furColor;
    String species;

    //People, animals
    String gender;
    String age;
    String eyeColor;
    String specialSigns;
    String name;

    //People
    String nationality;
}
