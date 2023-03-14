package com.jumpstart.org.services;

import com.jumpstart.org.models.Cart;
import com.jumpstart.org.models.CartProduct;
import com.jumpstart.org.models.Product;
import com.jumpstart.org.models.User;
import com.jumpstart.org.repositories.CartProductRepository;
import com.jumpstart.org.repositories.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartServices {
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartProductRepository cartProductRepository;
    @Autowired
    private ProductService productService;

    public Cart createCart(User user){
        Cart cart = new Cart();
        cart.setUser(user);
        return this.cartRepository.save(cart);
    }

    public Cart addItems(User user, Long id){
        Cart cart = user.getCart();
        Product product = this.productService.getProduct(id);
        if(product == null){
            return null;
        }
        CartProduct cartProduct = new CartProduct();
        cartProduct.setCart(cart);
        cartProduct.setProduct(product);
        cartProduct = this.cartProductRepository.save(cartProduct);
        return cartProduct.getCart();
    }
    public String deleteCart(User user,Long id){
        Cart cart = user.getCart();
        cart.getCartProducts().stream().forEach((cartProduct -> {
            if(cartProduct.getProduct().getPid() == id) {
                this.cartProductRepository.delete(cartProduct);
            }
        }));
        return "Delete Successfully!";
    }
}
