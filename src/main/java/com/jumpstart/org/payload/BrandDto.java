package com.jumpstart.org.payload;

import com.jumpstart.org.models.Product;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
public class BrandDto {
    private Long bid;
    private String description;
    private String contact;
    private String password;
    private String brandEmail;
    private String brandName;
    private String img;
}
