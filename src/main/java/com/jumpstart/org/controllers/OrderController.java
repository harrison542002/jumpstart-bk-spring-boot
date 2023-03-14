package com.jumpstart.org.controllers;

import com.jumpstart.org.models.CustomerOrder;
import com.jumpstart.org.models.ShippingAddress;
import com.jumpstart.org.models.User;
import com.jumpstart.org.payload.ShippingResponse;
import com.jumpstart.org.payload.TotalSummary;
import com.jumpstart.org.repositories.ShippingAddressRepository;
import com.jumpstart.org.repositories.UserRepository;
import com.jumpstart.org.services.OrderServices;
import com.jumpstart.org.utils.JwtUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ShippingAddressRepository shippingAddressRepository;
    @Autowired
    private OrderServices orderServices;
    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/get-shipping")
    public ResponseEntity<?> getShippingAddresses(HttpServletRequest request){
        String token = jwtUtils.getJWTFromRequest(request);
        String email = jwtUtils.getUserNameFromToken(token);
        Optional<User> optionalUser = this.userRepository.findByEmail(email);
        if (optionalUser.isEmpty()){
            return ResponseEntity.badRequest().body("User with email " + email + " does not exist!");
        }
        List<ShippingAddress> shippingAddresses = this.orderServices.getShippingAddress(optionalUser.get());
        return ResponseEntity.ok(shippingAddresses.stream()
                .map(shippingAddress -> this.modelMapper.map(shippingAddress, ShippingResponse.class))
                .collect(Collectors.toList()));
    }

    @PostMapping("/add-shipping")
    public ResponseEntity<?> addAddresses(HttpServletRequest request, @RequestBody ShippingResponse shippingResponse){
        String token = jwtUtils.getJWTFromRequest(request);
        String email = jwtUtils.getUserNameFromToken(token);
        Optional<User> optionalUser = this.userRepository.findByEmail(email);
        if (optionalUser.isEmpty()){
            return ResponseEntity.badRequest().body("User with email " + email + " does not exist!");
        }
        ShippingAddress shippingAddress = this.modelMapper.map(shippingResponse, ShippingAddress.class);
        shippingAddress.setUser(optionalUser.get());
        shippingAddress = shippingAddressRepository.save(shippingAddress);
        return ResponseEntity.ok(this.modelMapper.map(shippingAddress, ShippingResponse.class));
    }

    @PostMapping("/add-order/{deliId}")
    public ResponseEntity<?> addOrder(@RequestParam("pids") String pids, HttpServletRequest request,
                                      @RequestParam("status") String status, @RequestParam("type") String paymentType,
                                      @PathVariable Long deliId){
        String token = jwtUtils.getJWTFromRequest(request);
        String email = jwtUtils.getUserNameFromToken(token);
        Optional<User> optionalUser = this.userRepository.findByEmail(email);
        if (optionalUser.isEmpty()){
            return ResponseEntity.badRequest().body("User with email " + email + " does not exist!");
        }
        CustomerOrder customerOrder = this.orderServices.addOrder(pids, deliId, optionalUser.get(), paymentType, status);
        if(customerOrder == null){
            return ResponseEntity.badRequest().body("Address with id " + deliId + " does not exist!");
        }
        return ResponseEntity.ok("Order has been placed!");
    }

    @GetMapping("/get-single-shipping/{id}")
    public ResponseEntity<?> getSingleShipping(HttpServletRequest request, @PathVariable Long id){
        ShippingResponse shippingResponse = this.orderServices.getSingleShipping(id);
        if(shippingResponse == null){
            return ResponseEntity.badRequest().body("Shipping Address with id " + id + " does not exist!");
        }
        return ResponseEntity.ok(shippingResponse);
    }

    @GetMapping("/total-summary")
    public ResponseEntity<?> getTotal(HttpServletRequest request){
        TotalSummary totalSummary = this.orderServices.getTotal();
        return ResponseEntity.ok(totalSummary);
    }
}
