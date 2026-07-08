package edu.cit.caleza.washtrack.features.orderhistory;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderHistoryController {

    private final OrderHistoryService orderHistoryService;

    @GetMapping("/my/{userId}")
    public ResponseEntity<?> getMyOrders(@PathVariable Long userId) {
        return ResponseEntity.ok(orderHistoryService.getOrdersForUser(userId));
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllOrders() {
        return ResponseEntity.ok(orderHistoryService.getAllOrders());
    }
}