package edu.cit.caleza.washtrack.features.staffordermanagement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class StaffOrderResponse {
    private Long orderId;
    private String orderCode;       // e.g. "ORD-1042"
    private String customerName;
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
