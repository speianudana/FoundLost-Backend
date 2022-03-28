package com.example.foundlost.payload.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Data
public class PostRequest {

    Long id;
    MultipartFile petImage;
    Long authorId;
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
    String details;
}
