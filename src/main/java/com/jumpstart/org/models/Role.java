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
@Table(name = "role")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Role {
    @Id
    @Column(name = "role_id")
    private Long role_id;
    private String roleName;

    @LazyCollection(LazyCollectionOption.TRUE)
    @OneToMany(cascade = CascadeType.MERGE, mappedBy = "role")
    private List<UserRole> userRoles;
}
