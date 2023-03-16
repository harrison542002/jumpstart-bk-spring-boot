package com.jumpstart.org.services;

import com.jumpstart.org.models.*;
import com.jumpstart.org.payload.BrandDto;
import com.jumpstart.org.payload.ProductDto;
import com.jumpstart.org.payload.ProductRequest;
import com.jumpstart.org.repositories.*;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ProductRepository productRepository;
    private final ProductImagesRepository productImagesRepository;
    private final BrandRepository brandRepository;
    private final ModelMapper modelMapper;
    private final CartProductRepository cartProductRepository;
    private final PasswordEncoder passwordEncoder;
    private final CloudinaryService cloudinaryService;
    private final OrderProductRepository orderProductRepository;

    public ProductService(ProductRepository productRepository, ProductImagesRepository productImagesRepository,
                          BrandRepository brandRepository, ModelMapper modelMapper, CartProductRepository cartProductRepository, PasswordEncoder passwordEncoder, CloudinaryService cloudinaryService, OrderProductRepository orderProductRepository) {
        this.productRepository = productRepository;
        this.productImagesRepository = productImagesRepository;
        this.brandRepository = brandRepository;
        this.modelMapper = modelMapper;
        this.cartProductRepository = cartProductRepository;
        this.passwordEncoder = passwordEncoder;
        this.cloudinaryService = cloudinaryService;
        this.orderProductRepository = orderProductRepository;
    }


    public Product getProduct(Long id){
        Optional<Product> optionalProduct = productRepository.findById(id);
        return (optionalProduct.isEmpty()) ? null : optionalProduct.get();
    };

    public List<Brand> getBrands(){
        return this.brandRepository.findAll();
    }
    public String addBrandImage(Brand brand, String imageLink) {
        brand.setImg(imageLink);
        brand = this.brandRepository.save(brand);
        return imageLink;
    }

    public BrandDto addBrand(BrandDto brandDto){
        Brand brand = this.modelMapper.map(brandDto, Brand.class);
        brand.setPassword(passwordEncoder.encode(brandDto.getPassword()));
        brandDto = this.modelMapper.map(this.brandRepository.save(brand), BrandDto.class);
        return brandDto;
    }

    public Brand getBrand(Long brandId){
        Optional<Brand> optionalBrand = this.brandRepository.findById(brandId);
        if(optionalBrand.isEmpty()){
            return null;
        }
        return optionalBrand.get();
    }

    public BrandDto editBrand(BrandDto brandDto){
        Brand brand = this.brandRepository.findById(brandDto.getBid()).get();
        brand.setPassword(this.passwordEncoder.encode(brandDto.getPassword()));
        brand.setBrandEmail(brandDto.getBrandEmail());
        brand.setBrandName(brandDto.getBrandName());
        brand.setContact(brandDto.getContact());
        brand.setDescription(brandDto.getDescription());
        this.brandRepository.save(brand);
        return this.modelMapper.map(brand, BrandDto.class);
    }

    public String editImage(Long id, MultipartFile image){
        Brand brand = this.brandRepository.findById(id).get();
        this.cloudinaryService.delete(brand.getImg());
        String imageURL = this.cloudinaryService.upload(image);
        brand.setImg(imageURL);
        brand = this.brandRepository.save(brand);
        return brand.getImg();
    }

    public void deleteProduct(Long id){
        Product product = this.productRepository.findById(id).get();
        List<CartProduct> cartProducts = product.getCartProducts();
        List<ProductImages> productImages = product.getProductImages();
        List<OrderProduct> orderProducts = product.getOrderProducts();
        cartProducts.forEach(this.cartProductRepository::delete);
        productImages.forEach((productImages1 -> {
            this.cloudinaryService.delete(productImages1.getImg());
            this.productImagesRepository.delete(productImages1);
        }));
        orderProducts.forEach(this.orderProductRepository::delete);
        this.productRepository.delete(product);
    }

    public ProductImages addProductImage(Product product, String image){
        ProductImages productImages = new ProductImages();
        productImages.setProduct(product);
        productImages.setImg(image);
        productImages = productImagesRepository.save(productImages);
        return productImages;
    }

    public ProductDto editProduct(ProductDto productDto){
        Product product = this.getProduct(productDto.getPid());
        product.setDescription(productDto.getDescription());
        product.setCategory(productDto.getCategory());
        product.setItemName(productDto.getItemName());
        product.setMadeIn(productDto.getMadeIn());
        product.setPrice(productDto.getPrice());
        return this.modelMapper.map(productRepository.save(product), ProductDto.class);
    }

    public List<Product> getProducts(Integer pageNumber, Integer limit, Integer lowPrice, Integer highPrice, Integer fixedPrice,
                                     String brand, String category) {
        Pageable pageable = PageRequest.of(pageNumber, limit);
        Page<Product> productPage = null;
        productPage = this.productRepository.findByPriceBetween(pageable, brand, category, lowPrice, highPrice);
        return productPage.getContent();
    }

    public double getTotalPages(){
        Long totalProducts = this.productRepository.count();
        double totalPage = totalProducts.intValue()/9.0;
        logger.info("------------ " + Math.ceil(totalPage) +"----------" + totalPage + "-----" + totalProducts);
        return Math.ceil(totalPage);
    }

    public ProductDto postProduct(Long id, ProductRequest productRequest) {
        Optional<Brand> optionalBrand = this.brandRepository.findById(id);
        if (optionalBrand.isEmpty()) {
            return null;
        }
        Brand brand = optionalBrand.get();
        Product product = this.modelMapper.map(productRequest, Product.class);
        product.setBrand(brand);
        product = this.productRepository.save(product);
        return this.modelMapper.map(product, ProductDto.class);
    }
}
