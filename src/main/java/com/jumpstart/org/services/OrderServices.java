package com.jumpstart.org.services;

import com.jumpstart.org.models.*;
import com.jumpstart.org.payload.ShippingResponse;
import com.jumpstart.org.payload.TotalSummary;
import com.jumpstart.org.repositories.*;
import org.hibernate.criterion.Order;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServices {
    @Autowired
    private ShippingAddressRepository shippingAddressRepository;
    @Autowired
    private OrderProductRepository orderProductRepository;
    @Autowired
    private CustomerOrderRepository customerOrderRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private CartProductRepository cartProductRepository;
    @Autowired
    private UserRepository userRepository;
    public List<ShippingAddress> getShippingAddress(User user){
        return user.getShippingAddresses();
    }

    public ShippingResponse getSingleShipping(Long id){
        Optional<ShippingAddress> shippingAddress = this.shippingAddressRepository.findById(id);
        if(shippingAddress.isEmpty()){
            return  null;
        }
        return this.modelMapper.map(shippingAddress, ShippingResponse.class);
    }

    public CustomerOrder addOrder(String pids, Long deliId, User user, String paymentType, String status){
        Optional<ShippingAddress> optionalShippingAddress = this.shippingAddressRepository.findById(deliId);
        if(optionalShippingAddress.isEmpty()){
            return null;
        }
        ShippingAddress shippingAddress = optionalShippingAddress.get();
        CustomerOrder customerOrder = new CustomerOrder();
        customerOrder.setUser(user);
        customerOrder.setShippingAddress(shippingAddress);
        customerOrder.setPaymentType(paymentType);
        customerOrder.setStatus(status);
        CustomerOrder savedCustomerOrder = this.customerOrderRepository.save(customerOrder);
        Arrays.stream(pids.split("%2B")).forEach((pid) ->{
            System.out.println("-------------------" + pid + "-------------------");
            OrderProduct orderProduct = new OrderProduct();
            Product product = this.productRepository.findById(Long.parseLong(pid)).get();
            orderProduct.setProduct(product);
            orderProduct.setCustomerOrder(savedCustomerOrder);
            this.orderProductRepository.save(orderProduct);
        });
        List<CartProduct> cartProducts = user.getCart().getCartProducts();
        if(cartProducts.size() > 0){
            cartProducts.stream().forEach((cartProduct -> {
                this.cartProductRepository.delete(cartProduct);
            }));
        }
        return savedCustomerOrder;
    }

    public TotalSummary getTotal(){
        int totalBrands = (int) brandRepository.count();
        int totalUsers = (int) userRepository.count();
        int totalOrders = (int) customerOrderRepository.count();
        int totalProducts = (int) productRepository.count();
        return new TotalSummary(totalBrands, totalProducts, totalUsers, totalOrders);
    }
}
