package com.jumpstart.org.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
    private Long uid;
    private String firstName;
    private String lastName;
    private String provider;
    private String password;
    private String email;
}
