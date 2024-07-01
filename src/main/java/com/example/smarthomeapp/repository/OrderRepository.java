package com.example.smarthomeapp.repository;

import com.example.smarthomeapp.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}