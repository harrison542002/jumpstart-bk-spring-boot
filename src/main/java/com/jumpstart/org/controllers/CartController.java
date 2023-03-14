package com.jumpstart.org.controllers;

import com.jumpstart.org.models.Cart;
import com.jumpstart.org.models.CartProduct;
import com.jumpstart.org.models.User;
import com.jumpstart.org.payload.CartProductDto;
import com.jumpstart.org.repositories.UserRepository;
import com.jumpstart.org.services.CartServices;
import com.jumpstart.org.utils.JwtUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/cart")
public class CartController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private CartServices cartServices;

    @Autowired
    private JwtUtils jwtUtils;

    @GetMapping("/get-items")
    public ResponseEntity<?> getCartItems(HttpServletRequest request){
        String token = jwtUtils.getJWTFromRequest(request);
        String email = jwtUtils.getUserNameFromToken(token);
        Optional<User> optionalUser = this.userRepository.findByEmail(email);
        if (optionalUser.isEmpty()){
            return ResponseEntity.badRequest().body("User with email " + email + " does not exist!");
        }
        User user = optionalUser.get();
        List<CartProduct> cartProducts = user.getCart().getCartProducts();
        List<CartProductDto> cartProductDtos = cartProducts.size() > 0 ? cartProducts.stream().map((cartProduct -> this.modelMapper.map(cartProduct, CartProductDto.class))).collect(Collectors.toList()) : new ArrayList<>();
        return ResponseEntity.ok(cartProductDtos);
    }

    @PostMapping("/add-items/{id}")
    public ResponseEntity<?> addItem(@PathVariable Long id,HttpServletRequest request){
        String token = jwtUtils.getJWTFromRequest(request);
        String email = jwtUtils.getUserNameFromToken(token);
        Optional<User> optionalUser = this.userRepository.findByEmail(email);
        if (optionalUser.isEmpty()){
            return ResponseEntity.badRequest().body("User with email " + email + " does not exist!");
        }
        User user = optionalUser.get();
        Cart cart = this.cartServices.addItems(user, id);
        if(cart == null) {
            return ResponseEntity.badRequest().body("Product with id: " + id + " does not exist");
        }
        System.out.println("---------------" + cart.getCartProducts().size() + "---------------------");
        List<CartProductDto> cartProductDtos = cart.getCartProducts().size() > 0 ? cart.getCartProducts().stream().map((cartProduct -> this.modelMapper.map(cartProduct, CartProductDto.class))).collect(Collectors.toList()) : new ArrayList<>();
        return ResponseEntity.ok(cartProductDtos);
    }

    @DeleteMapping("/delete-cart/{id}")
    public ResponseEntity<?> deleteFromCart(@PathVariable Long id, HttpServletRequest request){
        String token = jwtUtils.getJWTFromRequest(request);
        String email = jwtUtils.getUserNameFromToken(token);
        Optional<User> optionalUser = this.userRepository.findByEmail(email);
        if (optionalUser.isEmpty()){
            return ResponseEntity.badRequest().body("User with email " + email + " does not exist!");
        }
        User user = optionalUser.get();
        String message = this.cartServices.deleteCart(user, id);
        return ResponseEntity.ok(message);
    }
}
