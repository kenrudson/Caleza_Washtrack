package edu.cit.caleza.washtrack.repository;

import edu.cit.caleza.washtrack.entity.PickupRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PickupRequestRepository extends JpaRepository<PickupRequest, Long> {
}
