package edu.cit.caleza.washtrack.features.staffordermanagement;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/staff/orders")
@RequiredArgsConstructor
public class StaffOrderController {

    private final StaffOrderService staffOrderService;

    // FR-007: staff-facing order queue (all customers' orders)
    @GetMapping
    public ResponseEntity<?> getAllOrders() {
        return ResponseEntity.ok(staffOrderService.getAllOrders());
    }

    // FR-007: advance an order to its next status in the BR-003 sequence
    @PostMapping("/{orderId}/advance-status")
    public ResponseEntity<?> advanceStatus(@PathVariable Long orderId) {
        try {
            return ResponseEntity.ok(staffOrderService.advanceStatus(orderId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
