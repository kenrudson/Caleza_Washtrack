package edu.cit.caleza.washtrack.controller;

import edu.cit.caleza.washtrack.dto.NewOrderRequest;
import edu.cit.caleza.washtrack.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // FR-004 + FR-005: creates the pickup request and the order together in one submission
    @PostMapping("/new")
    public ResponseEntity<?> createOrder(@Valid @RequestBody NewOrderRequest request) {
        try {
            return ResponseEntity.ok(orderService.createOrder(request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // FR-011: order history for the logged-in customer
    @GetMapping("/my/{userId}")
    public ResponseEntity<?> getMyOrders(@PathVariable Long userId) {
        return ResponseEntity.ok(orderService.getOrdersForUser(userId));
    }

    // Staff-facing order queue (all orders)
    @GetMapping("/all")
    public ResponseEntity<?> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }
}
