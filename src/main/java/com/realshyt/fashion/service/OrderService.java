package com.realshyt.fashion.service;

import com.realshyt.fashion.entity.Order;
import com.realshyt.fashion.entity.OrderItem;
import com.realshyt.fashion.repository.OrderRepository;
import com.realshyt.fashion.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
    
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }
    
    public List<Order> getOrdersByEmail(String email) {
        return orderRepository.findByCustomerEmailOrderByCreatedAtDesc(email);
    }
    
    public List<Order> getOrdersByStatus(Order.OrderStatus status) {
        return orderRepository.findByStatus(status);
    }
    
    public Order createOrder(Order order) {
        // Calculate total amount
        BigDecimal totalAmount = BigDecimal.ZERO;
        
        for (OrderItem item : order.getOrderItems()) {
            item.setOrder(order);
            item.calculateSubtotal();
            totalAmount = totalAmount.add(item.getSubtotal());
            
            // Update product stock
            productRepository.findById(item.getProduct().getId())
                .ifPresent(product -> {
                    product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
                    productRepository.save(product);
                });
        }
        
        order.setTotalAmount(totalAmount);
        return orderRepository.save(order);
    }
    
    public Order updateOrderStatus(Long id, Order.OrderStatus status) {
        return orderRepository.findById(id)
            .map(order -> {
                order.setStatus(status);
                return orderRepository.save(order);
            })
            .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
    }
    
    public void cancelOrder(Long id) {
        orderRepository.findById(id).ifPresent(order -> {
            order.setStatus(Order.OrderStatus.CANCELLED);
            
            // Restore product stock
            for (OrderItem item : order.getOrderItems()) {
                productRepository.findById(item.getProduct().getId())
                    .ifPresent(product -> {
                        product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
                        productRepository.save(product);
                    });
            }
            
            orderRepository.save(order);
        });
    }
}