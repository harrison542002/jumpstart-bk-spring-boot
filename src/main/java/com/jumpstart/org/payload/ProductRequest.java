package com.jumpstart.org.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequest {
    private String itemName;
    private String description;
    private Integer price;
    private String category;
    private String madeIn;
}
