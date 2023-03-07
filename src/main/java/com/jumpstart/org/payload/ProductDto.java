package com.jumpstart.org.payload;

import com.jumpstart.org.models.Brand;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProductDto {
    private Long pid;
    private String itemName;
    private String description;
    private Integer price;
    private String category;
    private String madeIn;
    private BrandDto brand;
    private List<ProductImagesDto> productImages;
}
