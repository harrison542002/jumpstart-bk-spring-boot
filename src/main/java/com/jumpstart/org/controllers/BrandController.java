package com.jumpstart.org.controllers;

import com.jumpstart.org.models.Brand;
import com.jumpstart.org.payload.BrandDto;
import com.jumpstart.org.services.CloudinaryService;
import com.jumpstart.org.services.ProductService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/brand")
public class BrandController {

    @Autowired
    private ProductService productService;
    @Autowired
    private CloudinaryService cloudinaryService;
    @Autowired
    private ModelMapper modelMapper;



    @PostMapping("/add-brand")
    public ResponseEntity<?> addBrand(@RequestBody BrandDto brandDto){
        brandDto = this.productService.addBrand(brandDto);
        return ResponseEntity.ok(brandDto);
    }

    @PostMapping("/upload/{id}")
    public ResponseEntity<?> upload(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {

        Brand brand = this.productService.getBrand(id);
        if (brand == null) {
            return ResponseEntity.badRequest().body("Brand with id : " + id + " not found!");
        }
        String imageURL = cloudinaryService.upload(file);
        if (imageURL == null) {
            return ResponseEntity.internalServerError().body("Error While Upload Please Retry Again");
        }
        String imageString = this.productService.addBrandImage(brand, imageURL);
        return ResponseEntity.ok(imageString);
    }
    @GetMapping("/get-brands")
    public ResponseEntity<?> getBrands(){
        return ResponseEntity.ok(this.productService.getBrands().stream().map((brand -> this.modelMapper.map(brand, BrandDto.class))).collect(Collectors.toList()));
    }

    @GetMapping("/single-brand/{id}")
    public ResponseEntity<?> getSingleBrand(@PathVariable Long id){
        Brand brand = this.productService.getBrand(id);
        if(brand == null){
            return ResponseEntity.badRequest().body("Brand with " + id + " does not exist!");
        }
        return ResponseEntity.ok(this.modelMapper.map(brand, BrandDto.class));
    }
}
