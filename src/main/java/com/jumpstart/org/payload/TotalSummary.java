package com.jumpstart.org.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.GetMapping;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TotalSummary {
    private int total_brands;
    private int total_products;
    private int total_users;
    private int total_orders;
}
