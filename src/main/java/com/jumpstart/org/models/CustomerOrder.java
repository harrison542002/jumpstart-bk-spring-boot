package com.jumpstart.org.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "customerorder")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long oid;
    @OneToOne
    @JoinColumn(name = "sid", referencedColumnName = "sid")
    private ShippingAddress shippingAddress;
    private String status;
    private String paymentType;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "uid", referencedColumnName = "uid")
    private User user;
    @OneToMany(cascade = CascadeType.MERGE, fetch = FetchType.LAZY, mappedBy = "customerOrder")
    private List<OrderProduct> orderProduct;
}
