package com.pbl.foundlost.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue
    @Column(name = "post_id", columnDefinition = "bigint")
    Long id;

    @Column(name = "uuid", unique = true, columnDefinition = "VARCHAR(36)")
    @JsonProperty("post_uuid")
    @Type(type = "uuid-char")
    UUID uuid;

    @Column(name = "image")
    String image;

    @Column(name = "is_resolved", nullable = false)
    Boolean isResolved = false;

    @Column(name = "status")
    String status;

    //Thing, animal, person
    @Column(name = "type")
    @JsonProperty("type")
    String type;

    @Column(name = "category")
    String category;

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
    Long reward;

    @Column(name = "details")
    @JsonProperty("details")
    String details;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private GeographicalLocation geographicalLocation;

    @ManyToOne
    @JoinColumn(name = "author_id", columnDefinition = "bigint")
    private User user;
}
