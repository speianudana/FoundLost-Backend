package com.example.foundlost.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue
    @Column(name = "post_id")
    Long id;

    @Column(name = "pet_image")
    String petImage;

    @Column(name = "status")
    String status;

    @Column(name = "species")
    String species;

    @Column(name = "sterilization")
    Boolean sterilization;

    @Column(name = "fur_color")
    String furColor;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date", columnDefinition = "DATETIME")
    Date  createdDate;

    @Column(name = "address")
    String address;

    @Column(name = "contacts")
    String contacts;

    @Column(name = "gender")
    String gender;

    @Column(name = "breed")
    String breed;

    @Column(name = "eye_color")
    String eyeColor;

    @Column(name = "special_signs")
    String specialSigns;

    @Column(name = "reward")
    Integer reward;

    @Column(name = "age")
    Integer age;

    @Column(name = "details")
    String details;

    @ManyToOne
    @JoinColumn(name="author_id", referencedColumnName = "id", columnDefinition = "int")
    private User user;
}
