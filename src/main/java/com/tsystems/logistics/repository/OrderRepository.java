package com.tsystems.logistics.repository;

import com.tsystems.logistics.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    // Optional Consulting Methods
    List<Order> findByCompleted(Boolean completed);

    Optional<Order> findByIdAndCompleted(Integer id, Boolean completed);

}
