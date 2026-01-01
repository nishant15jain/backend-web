package com.example.pharmabackend.product;

import com.example.pharmabackend.exceptions.ResourceNotFoundException;
import com.example.pharmabackend.upload.FileUploadService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductService {
    
    private final ProductRepository productRepository;
    private final FileUploadService fileUploadService;
    
    public ProductService(ProductRepository productRepository, FileUploadService fileUploadService) {
        this.productRepository = productRepository;
        this.fileUploadService = fileUploadService;
    }
    
    public ProductResponse createProduct(CreateProductRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setImageUrl(request.getImageUrl());
        
        Product saved = productRepository.save(product);
        return ProductResponse.fromEntity(saved);
    }
    
    public ProductResponse updateProduct(Long id, UpdateProductRequest request) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        
        if (request.getName() != null && !request.getName().isBlank()) {
            product.setName(request.getName());
        }
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }
        if (request.getImageUrl() != null) {
            // Delete old image if updating
            if (product.getImageUrl() != null && !product.getImageUrl().equals(request.getImageUrl())) {
                fileUploadService.deleteFile(product.getImageUrl());
            }
            product.setImageUrl(request.getImageUrl());
        }
        
        Product saved = productRepository.save(product);
        return ProductResponse.fromEntity(saved);
    }
    
    @Transactional(readOnly = true)
    public ProductResponse getProduct(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        return ProductResponse.fromEntity(product);
    }
    
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
            .map(ProductResponse::fromEntity)
            .collect(Collectors.toList());
    }
    
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        
        // Delete image from S3
        if (product.getImageUrl() != null) {
            fileUploadService.deleteFile(product.getImageUrl());
        }
        
        productRepository.delete(product);
    }
}

