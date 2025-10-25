package com.realshyt.fashion.config;

import com.realshyt.fashion.entity.Product;
import com.realshyt.fashion.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    
    private final ProductRepository productRepository;
    
    @Override
    public void run(String... args) {
        if (productRepository.count() == 0) {
            initializeProducts();
        }
    }
    
    private void initializeProducts() {
        Product[] products = {
            createProduct("REAL SHYT Hoodie", new BigDecimal("89.99"), 
                "Premium heavyweight hoodie", 
                "https://images.unsplash.com/photo-1556821840-3a63f95609a7?w=400&h=500&fit=crop", 
                "Hoodies", 50),
            
            createProduct("Graffiti Tee", new BigDecimal("45.99"), 
                "Limited edition street art", 
                "https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=400&h=500&fit=crop", 
                "T-Shirts", 100),
            
            createProduct("Urban Joggers", new BigDecimal("79.99"), 
                "Comfort meets style", 
                "https://images.unsplash.com/photo-1624378439575-d8705ad7ae80?w=400&h=500&fit=crop", 
                "Pants", 75),
            
            createProduct("Street Cap", new BigDecimal("35.99"), 
                "Classic snapback design", 
                "https://images.unsplash.com/photo-1588850561407-ed78c282e89b?w=400&h=500&fit=crop", 
                "Accessories", 150),
            
            createProduct("Smoke Jacket", new BigDecimal("129.99"), 
                "Weather-resistant flex", 
                "https://images.unsplash.com/photo-1551028719-00167b16eac5?w=400&h=500&fit=crop", 
                "Jackets", 30),
            
            createProduct("Drip Shorts", new BigDecimal("59.99"), 
                "Summer essential", 
                "https://images.unsplash.com/photo-1591195853828-11db59a44f6b?w=400&h=500&fit=crop", 
                "Shorts", 80)
        };
        
        for (Product product : products) {
            productRepository.save(product);
        }
        
        System.out.println("✅ Sample products initialized!");
    }
    
    private Product createProduct(String name, BigDecimal price, String description, 
                                   String imageUrl, String category, Integer stock) {
        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        product.setDescription(description);
        product.setImageUrl(imageUrl);
        product.setCategory(category);
        product.setStockQuantity(stock);
        product.setIsActive(true);
        return product;
    }
}