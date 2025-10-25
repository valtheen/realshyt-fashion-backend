package com.realshyt.fashion.service;

import com.realshyt.fashion.entity.Product;
import com.realshyt.fashion.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {
    
    private final ProductRepository productRepository;
    
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    public List<Product> getActiveProducts() {
        return productRepository.findByIsActiveTrue();
    }
    
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }
    
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }
    
    public List<Product> searchProducts(String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword);
    }
    
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }
    
    public Product updateProduct(Long id, Product productDetails) {
        return productRepository.findById(id)
            .map(product -> {
                product.setName(productDetails.getName());
                product.setPrice(productDetails.getPrice());
                product.setDescription(productDetails.getDescription());
                product.setImageUrl(productDetails.getImageUrl());
                product.setCategory(productDetails.getCategory());
                product.setStockQuantity(productDetails.getStockQuantity());
                product.setIsActive(productDetails.getIsActive());
                return productRepository.save(product);
            })
            .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }
    
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
    
    public boolean updateStock(Long productId, Integer quantity) {
        return productRepository.findById(productId)
            .map(product -> {
                product.setStockQuantity(product.getStockQuantity() - quantity);
                productRepository.save(product);
                return true;
            })
            .orElse(false);
    }
}