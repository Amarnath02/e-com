package com.spring.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.dto.ProductDto;
import com.spring.entities.Product;
import com.spring.service.product.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/e-com/v1/product/")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/add-product")
    public ResponseEntity<ProductDto> addProductHandler(
            @RequestPart MultipartFile file,
            @RequestPart String productDto)
        throws IOException {

        if (file == null) {
            throw new RuntimeException("File is empty! Please send file");
        }

        ProductDto product = convertToJson(productDto);
        return ResponseEntity.ok(productService.addProduct(product, file));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    private ProductDto convertToJson(String movieDto)
            throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(movieDto, ProductDto.class);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id)
            throws IOException {
        productService.deleteProductById(id);
        return ResponseEntity.ok("Product deleted with id = " + id);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<ProductDto> updateProduct(
            @RequestPart MultipartFile file,
            @RequestPart String productDto,
            @PathVariable Long id) throws IOException {

        if (file.isEmpty()) {
            file = null;
        }
        ProductDto product = convertToJson(productDto);
        return ResponseEntity.ok(productService.updateProduct(product, file, id));
    }

    @GetMapping("/")
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/category={name}")
    public ResponseEntity<List<ProductDto>> getProductByCategory(@PathVariable String name) {
        return ResponseEntity.ok(productService.getProductsByCategory(name));
    }

    @GetMapping("/brand={brand}")
    public ResponseEntity<List<ProductDto>> getProductByBrand(@PathVariable String brand) {
        return ResponseEntity.ok(productService.getProductsByBrand(brand));
    }

    @GetMapping("/name={name}")
    public ResponseEntity<List<ProductDto>> getProductByName(@PathVariable String name) {
        return ResponseEntity.ok(productService.getProductsByName(name));
    }

    @GetMapping("/category={category}/brand={brand}")
    public ResponseEntity<List<ProductDto>> getProductByCategoryNameAndBrand(
            @PathVariable String category, @PathVariable String brand) {
        return ResponseEntity.ok(productService.getProductsByCategoryAndBrand(category, brand));
    }

    @GetMapping("/brand={brand}/name={name}")
    public ResponseEntity<List<ProductDto>> getProductByBrandAndName(
            @PathVariable String brand, @PathVariable String name) {
        return ResponseEntity.ok(productService.getProductsByBrandAndName(brand, name));
    }
}