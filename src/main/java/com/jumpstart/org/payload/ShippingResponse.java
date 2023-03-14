package com.jumpstart.org.payload;

import com.jumpstart.org.models.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShippingResponse {
    private Long sid;
    private String type;
    private String addressDetail;
    private String region;
    private String fullName;
    private String city;
    private String phone;
}
