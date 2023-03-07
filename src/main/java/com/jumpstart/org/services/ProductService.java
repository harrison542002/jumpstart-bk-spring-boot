package com.jumpstart.org.services;

import com.jumpstart.org.models.Brand;
import com.jumpstart.org.models.Product;
import com.jumpstart.org.models.ProductImages;
import com.jumpstart.org.payload.ProductDto;
import com.jumpstart.org.payload.ProductRequest;
import com.jumpstart.org.repositories.BrandRepository;
import com.jumpstart.org.repositories.ProductImagesRepository;
import com.jumpstart.org.repositories.ProductRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ProductRepository productRepository;
    private final ProductImagesRepository productImagesRepository;
    private final BrandRepository brandRepository;
    private final ModelMapper modelMapper;

    public ProductService(ProductRepository productRepository, ProductImagesRepository productImagesRepository, BrandRepository brandRepository, ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.productImagesRepository = productImagesRepository;
        this.brandRepository = brandRepository;
        this.modelMapper = modelMapper;
    }


    public Product getProduct(Long id){
        Optional<Product> optionalProduct = productRepository.findById(id);
        return (optionalProduct.isEmpty()) ? null : optionalProduct.get();
    };

    public ProductImages addProductImage(Product product, String image){
        ProductImages productImages = new ProductImages();
        productImages.setProduct(product);
        productImages.setImg(image);
        productImages = productImagesRepository.save(productImages);
        return productImages;
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

    public ProductDto postProduct(Long id, ProductRequest productRequest){
        Optional<Brand> optionalBrand = this.brandRepository.findById(id);
        if(optionalBrand.isEmpty()){
            return null;
        }
        Brand brand = optionalBrand.get();
        Product product = this.modelMapper.map(productRequest, Product.class);
        product.setBrand(brand);
        product = this.productRepository.save(product);
        return this.modelMapper.map(product, ProductDto.class);
    }
}
