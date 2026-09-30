package com.fudn.orderservice.repository;

import com.fudn.orderservice.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository — tự sinh implementation.
 * Cung cấp sẵn: save(), findAll(), findById(), deleteById()...
 */
public interface OrderRepository extends JpaRepository<Order, Long> {
}
