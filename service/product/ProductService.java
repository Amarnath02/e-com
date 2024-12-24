package com.spring.service.product;

import com.spring.dto.ProductDto;
import com.spring.entities.Product;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProductService {

    ProductDto addProduct(ProductDto product, MultipartFile file) throws IOException;
    ProductDto getProductById(Long id);

    void deleteProductById(Long id) throws IOException;
    ProductDto updateProduct(ProductDto product, MultipartFile file, Long productId) throws IOException;
    List<ProductDto> getAllProducts();
    List<ProductDto> getProductsByCategory(String category);
    List<ProductDto> getProductsByBrand(String brand);
    List<ProductDto> getProductsByCategoryAndBrand(String category, String brand);
    List<ProductDto> getProductsByName(String name);
    List<ProductDto> getProductsByBrandAndName(String category, String name);
//    Long countProductsByBrandAndName(String brand, String name);
//
//    List<ProductDto> getConvertedProducts(List<Product> products);
//
//    ProductDto convertToDto(Product product);

    Product productUsingOutsideFunction(Long id);
}
