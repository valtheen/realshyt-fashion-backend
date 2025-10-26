package com.realshyt.fashion.repository;

import com.realshyt.fashion.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    Optional<Payment> findByOrderId(Long orderId);
    
    Optional<Payment> findByTransactionId(String transactionId);
    
    Optional<Payment> findByMidtransOrderId(String midtransOrderId);
    
    boolean existsByOrderId(Long orderId);
}