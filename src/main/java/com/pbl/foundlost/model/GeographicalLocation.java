package com.pbl.foundlost.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@Entity
@NoArgsConstructor
@Table(name = "geographical_location")
public class GeographicalLocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "latitude", nullable = false, precision = 19, scale = 14)
    private BigDecimal latitude;

    @Column(name = "longitude", nullable = false, precision = 19, scale = 14)
    private BigDecimal longitude;

}
