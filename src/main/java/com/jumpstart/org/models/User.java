package com.jumpstart.org.models;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "user")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uid;
    private String firstName;
    private String lastName;
    private String provider;
    private String password;
    private String email;
    @OneToOne(cascade=CascadeType.MERGE, mappedBy = "user")
    private Cart cart;
    @OneToMany(cascade = CascadeType.MERGE, mappedBy = "user", fetch = FetchType.EAGER)
    private List<UserRole> userRoles;
    @LazyCollection(LazyCollectionOption.TRUE)
    @OneToMany(cascade = CascadeType.MERGE, mappedBy = "user")
    private List<ShippingAddress> shippingAddresses;
}
