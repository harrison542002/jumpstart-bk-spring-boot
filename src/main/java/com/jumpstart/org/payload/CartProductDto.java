package com.jumpstart.org.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartProductDto {
    private Long cpid;
    private CartDto cart;
    private ProductDto product;
}
