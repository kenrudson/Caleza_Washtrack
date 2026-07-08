package edu.cit.caleza.washtrack.features.neworder;

import edu.cit.caleza.washtrack.shared.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewOrderRepository extends JpaRepository<Order, Long> {
}