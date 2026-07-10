package edu.cit.caleza.washtrack.features.staffordermanagement;

import edu.cit.caleza.washtrack.shared.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StaffOrderRepository extends JpaRepository<Order, Long> {
    // FR-007: staff-facing order queue, newest first
    List<Order> findAllByOrderByCreatedAtDesc();
}
