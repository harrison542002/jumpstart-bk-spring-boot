package com.jumpstart.org.controllers;

import com.jumpstart.org.exception.BadRequestException;
import com.jumpstart.org.models.Role;
import com.jumpstart.org.models.User;
import com.jumpstart.org.models.UserRole;
import com.jumpstart.org.payload.AuthResponse;
import com.jumpstart.org.payload.LoginRequest;
import com.jumpstart.org.payload.SignUpRequest;
import com.jumpstart.org.repositories.RoleRepository;
import com.jumpstart.org.repositories.UserRepository;
import com.jumpstart.org.repositories.UserRoleRepository;
import com.jumpstart.org.status.AppConstants;
import com.jumpstart.org.status.Provider;
import com.jumpstart.org.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtUtils.createToken(authentication);
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/signUp")
    public ResponseEntity<?> registerUser(@RequestBody SignUpRequest signUpRequest) {
        if (this.userRepository.findByEmail(signUpRequest.getEmail()).isPresent()) {
            throw new BadRequestException("Email address already in use.");
        }
        User user = new User();
        user.setFirstName(signUpRequest.getFirstName());
        user.setLastName(signUpRequest.getLastName());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setProvider(Provider.local.name());
        user = this.userRepository.save(user);
        System.out.println(user.getFirstName());
        UserRole userRole = new UserRole();
        Role role = this.roleRepository.findById(AppConstants.ROLE_MEMBER.longValue()).get();
        userRole.setUser(user);
        userRole.setRole(role);
        this.userRoleRepository.save(userRole);
        String token = jwtUtils.createTokenWhileRegistration(signUpRequest);
        return ResponseEntity.ok(new AuthResponse(token));
    }
}
