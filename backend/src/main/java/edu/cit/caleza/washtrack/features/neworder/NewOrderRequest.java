package edu.cit.caleza.washtrack.features.neworder;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class NewOrderRequest {

    @NotNull(message = "User is required")
    private Long userId;

    @NotBlank(message = "Pickup address is required")
    private String pickupAddress;

    @NotNull(message = "Scheduled date is required")
    private LocalDate scheduledDate;

    @NotBlank(message = "Time slot is required")
    private String timeSlot;

    private String pickupNotes;

    @NotBlank(message = "Service type is required")
    private String serviceType;

    @NotNull(message = "Weight is required")
    @DecimalMin(value = "0.01", message = "Weight must be greater than 0 kg")
    @DecimalMax(value = "50.0", message = "Weight cannot exceed 50 kg")
    private Double weightKg;

    private String specialInstructions;
}