package edu.cit.caleza.washtrack.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class OrderResponse {
    private Long orderId;
    private Long pickupId;
    private String pickupAddress;
    private LocalDate scheduledDate;
    private String timeSlot;
    private String serviceType;
    private Double weightKg;
    private BigDecimal totalPrice;
    private String status;
    private String specialInstructions;
    private Boolean paid;
    private LocalDateTime createdAt;
}
