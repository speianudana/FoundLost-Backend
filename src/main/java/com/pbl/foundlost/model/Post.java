package com.pbl.foundlost.model;

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

    @Column(name = "image")
    String image;

    @Column(name = "status")
    String status;

    //Thing, animal, person
    @Column(name = "type")
    String type;

    @Column(name = "fur_color")
    String furColor;

    @Column(name = "breed")
    String breed;

    @Column(name = "species")
    String species;

    @Column(name = "nationality")
    String nationality;

    @Column(name = "age")
    String age;

    @Column(name = "gender")
    String gender;

    @Column(name = "eye_color")
    String eyeColor;

    @Column(name = "special_signs")
    String specialSigns;

    @Column(name = "name")
    String name;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date", columnDefinition = "DATETIME")
    Date createdDate;

    @Column(name = "address")
    String address;

    @Column(name = "contacts")
    String contacts;

    @Column(name = "reward")
    Integer reward;

    @Column(name = "details")
    String details;

    @ManyToOne
    @JoinColumn(name = "author_id", referencedColumnName = "id", columnDefinition = "int")
    private User user;
}
