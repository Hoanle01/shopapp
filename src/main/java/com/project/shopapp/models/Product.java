package com.project.shopapp.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name="products")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product extends BaseEntity {
    public static final int MAXIMUM_IMAGES_PER_PRODUCT=5;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name",nullable = false,length = 350)
    private String name;
    private Float price;
    @Column(name="thumbnail",length = 300)
    private String thumbnail;
    @Column(name="descriptions")
    private  String descriptions;

    @ManyToOne
    @JoinColumn(name="category_id")
    private Category category;
}
