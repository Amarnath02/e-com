package com.spring.service.product;

import com.spring.dto.ProductDto;
import com.spring.entities.Product;
import com.spring.image.FileService;
import com.spring.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Value("${project.poster}")
    private String path;

    @Value("${base.url}")
    private String baseUrl;

    @Autowired
    private FileService fileService;

    @Override
    public ProductDto addProduct(ProductDto productDto, MultipartFile file)
            throws IOException {

        if (Files.exists(Paths.get(path + File.separator + file.getOriginalFilename()))) {
            throw new RuntimeException("File exists already! Please upload with another file name!");
        }

        String imageName = fileService.uploadFile(path, file);

        productDto.setImage(imageName);

        Product product = new Product(
                null,
                productDto.getName(),
                productDto.getBrand(),
                productDto.getPrice(),
                productDto.getInventory(),
                productDto.getDescription(),
                productDto.getImage(),
                productDto.getCategory()
        );

        productRepository.save(product);

        String imageUrl = baseUrl + "/file/" + imageName;

        return new ProductDto(
                product.getId(),
                product.getName(),
                product.getBrand(),
                product.getPrice(),
                product.getInventory(),
                product.getDescription(),
                product.getCategory(),
                product.getImage(),
                imageUrl
        );
    }

    @Override
    public ProductDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Product not found with id = " + id));

        String imageUrl = baseUrl + "/file/" + product.getImage();

        return new ProductDto(
                product.getId(),
                product.getName(),
                product.getBrand(),
                product.getPrice(),
                product.getInventory(),
                product.getDescription(),
                product.getCategory(),
                product.getImage(),
                imageUrl
        );
    }

    @Override
    public void deleteProductById(Long id) throws IOException {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Product not found with id = " + id));

        Files.deleteIfExists(Paths.get(path + File.separator + product.getImage()));

        productRepository.delete(product);
    }

    @Override
    public ProductDto updateProduct(ProductDto productDto,
                                 MultipartFile file,
                                 Long productId) throws IOException {
        Product productFind = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Product not found with id = " + productId));

        String fileName = productFind.getImage();
        if (file != null) {
            Files.deleteIfExists(Paths.get(path + File.separator + fileName));
            fileName = fileService.uploadFile(path, file);
        }

        productRepository.save(productFind);

        Product product = new Product(
                null,
                productDto.getName(),
                productDto.getBrand(),
                productDto.getPrice(),
                productDto.getInventory(),
                productDto.getDescription(),
                productDto.getImage(),
                productDto.getCategory()
        );

        productRepository.save(product);

        String imageUrl = baseUrl + "/file/" + fileName;

        return new ProductDto(
                product.getId(),
                product.getName(),
                product.getBrand(),
                product.getPrice(),
                product.getInventory(),
                product.getDescription(),
                product.getCategory(),
                product.getImage(),
                imageUrl
        );
    }

    @Override
    public List<ProductDto> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return getMovieDtoObject(products);
    }

    @Override
    public List<ProductDto> getProductsByCategory(String name) {
        List<Product> products = productRepository.findByCategoryName(name);
        return getMovieDtoObject(products);
    }

    @Override
    public List<ProductDto> getProductsByBrand(String brand) {
        List<Product> products = productRepository.findByBrand(brand);
        return getMovieDtoObject(products);
    }

    @Override
    public List<ProductDto> getProductsByCategoryAndBrand(String category, String brand) {
        List<Product> products = productRepository.findByCategoryNameAndBrand(category, brand);
        return getMovieDtoObject(products);
    }

    @Override
    public List<ProductDto> getProductsByName(String name) {
        List<Product> products = productRepository.findByName(name);
        return getMovieDtoObject(products);
    }

    @Override
    public List<ProductDto> getProductsByBrandAndName(String category, String name) {
        List<Product> products = productRepository.findByBrandAndName(category, name);
        return getMovieDtoObject(products);
    }

    private List<ProductDto> getMovieDtoObject(List<Product> products) {
        List<ProductDto> productDtoList = new ArrayList<>();

        for (Product i : products) {
            String imageUrl = baseUrl + "/file/" + i.getImage();
            ProductDto response = new ProductDto(
                    i.getId(),
                    i.getName(),
                    i.getBrand(),
                    i.getPrice(),
                    i.getInventory(),
                    i.getDescription(),
                    i.getCategory(),
                    i.getImage(),
                    imageUrl
            );
            productDtoList.add(response);
        }
        if (productDtoList.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found");
        }
        return productDtoList;
    }

    @Override
    public Product productUsingOutsideFunction(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Product Not Found"));
    }
}
