package edu.cit.caleza.washtrack.repository;

import edu.cit.caleza.washtrack.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    // FR-011: order history sorted by creation date descending
    List<Order> findByUser_UserIdOrderByCreatedAtDesc(Long userId);

    List<Order> findAllByOrderByCreatedAtDesc();
}
