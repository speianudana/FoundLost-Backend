package com.pbl.foundlost.payload.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Data
public class PostRequestData {
    //All
    Long id;
    MultipartFile image;
    UUID uuid;
    Long authorId;
    String status;
    String type;
    Date createdDate;
    String address;
    String contacts;
    String details;
    BigDecimal latitude;
    BigDecimal longitude;
    //=======================
    //Things, animals
    String reward;
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
