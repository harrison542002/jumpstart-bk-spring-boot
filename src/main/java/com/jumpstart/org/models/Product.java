package com.jumpstart.org.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "product")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pid;
    private String itemName;
    private Integer price;
    private String category;
    private String madeIn;
    private String description;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "bid", referencedColumnName = "bid")
    private Brand brand;
    @LazyCollection(LazyCollectionOption.TRUE)
    @OneToMany(cascade = CascadeType.MERGE, mappedBy = "product")
    private List<CartProduct> cartProducts;
    @OneToMany(cascade = CascadeType.MERGE, fetch = FetchType.LAZY, mappedBy = "product")
    private List<ProductImages> productImages;
}
