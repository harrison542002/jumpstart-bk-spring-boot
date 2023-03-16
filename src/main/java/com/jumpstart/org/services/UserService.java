package com.jumpstart.org.services;

import com.jumpstart.org.models.User;
import com.jumpstart.org.payload.UserDto;
import com.jumpstart.org.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;

    public List<UserDto> getUsers(){
        List<User> users = this.userRepository.findAll();
        return users.stream().map((user -> this.modelMapper.map(user, UserDto.class))).collect(Collectors.toList());
    }
}
