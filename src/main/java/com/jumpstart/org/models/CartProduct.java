package com.jumpstart.org.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;

@Entity
@Table(name = "cartproduct")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cpid;

    @LazyCollection(LazyCollectionOption.TRUE)
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "cid", referencedColumnName = "cid")
    private Cart cart;
    @LazyCollection(LazyCollectionOption.TRUE)
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "pid", referencedColumnName = "pid")
    private Product product;
}
