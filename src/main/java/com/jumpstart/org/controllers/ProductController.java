package com.jumpstart.org.controllers;

import com.jumpstart.org.models.Brand;
import com.jumpstart.org.models.ProductImages;
import com.jumpstart.org.payload.*;
import com.jumpstart.org.models.Product;
import com.jumpstart.org.repositories.BrandRepository;
import com.jumpstart.org.services.CloudinaryService;
import com.jumpstart.org.services.ProductService;
import com.jumpstart.org.utils.JwtUtils;
import org.apache.tomcat.util.http.parser.HttpParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.modelmapper.ModelMapper;
import org.springframework.web.multipart.MultipartFile;


import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/product")
public class ProductController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ProductService productService;
    private final CloudinaryService cloudinaryService;
    private final ModelMapper modelMapper;
    private final JwtUtils jwtUtils;
    private final BrandRepository brandRepository;

    public ProductController(ProductService productService, CloudinaryService cloudinaryService, ModelMapper modelMapper, JwtUtils jwtUtils, BrandRepository brandRepository) {
        this.productService = productService;
        this.cloudinaryService = cloudinaryService;
        this.modelMapper = modelMapper;
        this.jwtUtils = jwtUtils;
        this.brandRepository = brandRepository;
    }

    @GetMapping(value = {"/all", "/all/{pageNumber}"})
    public <T> T getProducts(
            @RequestParam(required = false, defaultValue = "9") Integer limit,
            @RequestParam(required = false) String category,
            @PathVariable(required = false) Integer pageNumber,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) Integer lowPrice,
            @RequestParam(required = false) Integer highPrice,
            @RequestParam(required = false) Integer fixedPrice
            ) {
        //if there is no page number defined, then default equals 1
        pageNumber = pageNumber == null ? 0 : pageNumber;

        //if category is empty then will just retrieve normally
        List<Product> products = productService.getProducts(pageNumber,
                limit, lowPrice, highPrice, fixedPrice, brand, category);

        List<ProductDto> productDtos = products.stream().map((product -> this.modelMapper.map(product, ProductDto.class)))
                .collect(Collectors.toList());
        if (products.isEmpty()) {
            return (T) "No products to present";
        }
        double totalPage = this.productService.getTotalPages();
        return (T) new ProductRespone(productDtos, totalPage);
    }

    @PostMapping("/upload/{id}")
    public ResponseEntity<?> upload(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        String imageURL = cloudinaryService.upload(file);
        if (imageURL == null) {
            return ResponseEntity.internalServerError().body("Error While Upload Please Retry Again");
        }
        Product product = productService.getProduct(id);
        if (product == null) {
            return ResponseEntity.badRequest().body("Product with id : " + id + " not found!");
        }
        ProductImages productImages = this.productService.addProductImage(product, imageURL);
        return ResponseEntity.ok(imageURL);
    }

    @PostMapping("/postProduct")
    public ResponseEntity<?> postProduct(
            @RequestBody ProductRequest productRequest,
            HttpServletRequest request){
        String token = jwtUtils.getJWTFromRequest(request);
        String email = jwtUtils.getUserNameFromToken(token);
        Brand brand = this.brandRepository.findByBrandEmail(email).get();
        ProductDto productDto = this.productService.postProduct(brand.getBid(), productRequest);
        return ResponseEntity.ok(productDto);
    }

    @GetMapping("/single/{id}")
    public  ResponseEntity<?> getProduct(
            @PathVariable Long id
    ){
        Product product = this.productService.getProduct(id);
        if(product == null){
            return ResponseEntity.badRequest().body("Product with id: " + id + " does not exist!");
        }
        return ResponseEntity.ok(this.modelMapper.map(product, ProductDto.class));
    }

    @GetMapping("/multiple-products/{id}")
    public ResponseEntity<?> getMultipleProducts(
            @PathVariable String id
    ){
        String[] ids = id.split("\\+");
        List<ProductDto> productDtos = new ArrayList<>();
        for (String idd: ids) {
            productDtos.add(this.modelMapper.map(this.productService.getProduct(Long.parseLong(idd)), ProductDto.class));
        }
        return ResponseEntity.ok(productDtos);
    }

    @GetMapping("/brands")
    public ResponseEntity<?> getBrands() {
        return ResponseEntity.ok(this.productService.getBrands().stream().map((brand) -> this.modelMapper.map(brand, BrandForPublic.class)));
    }

    @PutMapping("/edit-brand")
    public ResponseEntity<?> editBrand(@RequestBody BrandDto brandDto){
        return ResponseEntity.ok(this.productService.editBrand(brandDto));
    }

    @PutMapping("/edit-brand-img/{id}")
    public ResponseEntity<?> editBrandImg(@PathVariable Long id,
                                          @RequestParam("file") MultipartFile image){
        return ResponseEntity.ok(this.productService.editImage(id,image));
    }

    @GetMapping("/get-brand-info")
    public ResponseEntity<?> getBrandInfo(HttpServletRequest request){
        String token = jwtUtils.getJWTFromRequest(request);
        String email = jwtUtils.getUserNameFromToken(token);
        Brand brand = this.brandRepository.findByBrandEmail(email).get();
        return ResponseEntity.ok(this.modelMapper.map(brand, BrandDto.class));
    }

    @GetMapping("/get-brand-product")
    public ResponseEntity<?> getBrandProduct(HttpServletRequest request){
        String token = jwtUtils.getJWTFromRequest(request);
        String email = jwtUtils.getUserNameFromToken(token);
        Brand brand = this.brandRepository.findByBrandEmail(email).get();
        return ResponseEntity.ok(brand.getProducts().stream()
                .map((product) -> this.modelMapper.map(product, ProductDto.class)).collect(Collectors.toList()));
    }

    @PutMapping("/edit-product")
    public ResponseEntity<?> editProduct(HttpServletRequest request, @RequestBody ProductDto productDto){
        return ResponseEntity.ok(this.productService.editProduct(productDto));
    }

    @DeleteMapping("/delete-product/{id}")
    public ResponseEntity<?> deleteProduct(HttpServletRequest request, @PathVariable Long id){
        this.productService.deleteProduct(id);
        return ResponseEntity.ok("Delete Product Successfully!");
    }
}
