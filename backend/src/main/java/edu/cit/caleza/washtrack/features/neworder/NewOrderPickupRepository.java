package edu.cit.caleza.washtrack.features.neworder;

import edu.cit.caleza.washtrack.shared.entity.PickupRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewOrderPickupRepository extends JpaRepository<PickupRequest, Long> {
}