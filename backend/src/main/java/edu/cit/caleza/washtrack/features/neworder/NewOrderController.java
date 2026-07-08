package edu.cit.caleza.washtrack.features.neworder;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class NewOrderController {

    private final NewOrderService newOrderService;

    @PostMapping("/new")
    public ResponseEntity<?> createOrder(@Valid @RequestBody NewOrderRequest request) {
        try {
            return ResponseEntity.ok(newOrderService.createOrder(request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}