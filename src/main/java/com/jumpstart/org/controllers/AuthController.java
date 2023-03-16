package com.jumpstart.org.controllers;

import com.google.gson.Gson;
import com.jumpstart.org.exception.BadRequestException;
import com.jumpstart.org.models.Brand;
import com.jumpstart.org.models.Role;
import com.jumpstart.org.models.User;
import com.jumpstart.org.models.UserRole;
import com.jumpstart.org.payload.AuthResponse;
import com.jumpstart.org.payload.LoginRequest;
import com.jumpstart.org.payload.SignUpRequest;
import com.jumpstart.org.repositories.BrandRepository;
import com.jumpstart.org.repositories.RoleRepository;
import com.jumpstart.org.repositories.UserRepository;
import com.jumpstart.org.repositories.UserRoleRepository;
import com.jumpstart.org.services.CartServices;
import com.jumpstart.org.status.AppConstants;
import com.jumpstart.org.status.Provider;
import com.jumpstart.org.utils.JwtUtils;
import net.minidev.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = {"http://localhost:3000"}, allowCredentials = "true")
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
    @Autowired
    private CartServices cartServices;
    @Autowired
    private BrandRepository brandRepository;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest,
                                              HttpServletResponse response) throws UnsupportedEncodingException {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtUtils.createToken(authentication);
        String email = jwtUtils.getUserNameFromToken(token);
        Optional<User> user = this.userRepository.findByEmail(email);
        Optional<Brand> brand = user.isEmpty() ? this.brandRepository.findByBrandEmail(email) : null;
        Cookie cookie = new Cookie("token", token);
        cookie.setPath("/");
        response.addCookie(cookie);

        if(brand == null && user.isPresent()){
            ArrayList<String> roles = new ArrayList<>();
            user.get().getUserRoles().forEach((userRole -> roles.add(userRole.getRole().getRoleName())));
            Gson gson = new Gson();
            String stringRoles = URLEncoder.encode( gson.toJson(roles), "UTF-8");
            Cookie cookie1 = new Cookie("roles", stringRoles);
            cookie1.setPath("/");
            response.addCookie(cookie1);
        }
        if(brand.isPresent()){
            Cookie cookie1 = new Cookie("isBrand", "true");
            cookie1.setPath("/");
            response.addCookie(cookie1);
        }

        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/signUp")
    public ResponseEntity<?> registerUser(@RequestBody SignUpRequest signUpRequest,
                                          HttpServletResponse response) {
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
        this.cartServices.createCart(user);
        UserRole userRole = new UserRole();
        Role role = this.roleRepository.findById(AppConstants.ROLE_MEMBER.longValue()).get();
        userRole.setUser(user);
        userRole.setRole(role);
        this.userRoleRepository.save(userRole);
        String token = jwtUtils.createTokenWhileRegistration(signUpRequest);
        Cookie cookie = new Cookie("token", token);
        response.addCookie(cookie);

        return ResponseEntity.ok(new AuthResponse(token));
    }
}
