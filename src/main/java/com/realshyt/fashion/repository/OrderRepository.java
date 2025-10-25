package com.realshyt.fashion.repository;

import com.realshyt.fashion.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    List<Order> findByCustomerEmail(String email);
    
    List<Order> findByStatus(Order.OrderStatus status);
    
    List<Order> findByCustomerEmailOrderByCreatedAtDesc(String email);
}