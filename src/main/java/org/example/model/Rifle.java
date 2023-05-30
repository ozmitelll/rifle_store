package org.example.model;

import jakarta.persistence.*;
import lombok.Data;


@Entity
@Data
public class Rifle {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(length = 40)
    private String inventory_number;
    @Column(length = 40)
    private String name;
}
