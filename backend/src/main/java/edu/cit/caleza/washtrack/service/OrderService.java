package edu.cit.caleza.washtrack.service;

import edu.cit.caleza.washtrack.dto.NewOrderRequest;
import edu.cit.caleza.washtrack.dto.OrderResponse;
import edu.cit.caleza.washtrack.entity.*;
import edu.cit.caleza.washtrack.repository.NotificationRepository;
import edu.cit.caleza.washtrack.repository.OrderRepository;
import edu.cit.caleza.washtrack.repository.PickupRequestRepository;
import edu.cit.caleza.washtrack.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final PickupRequestRepository pickupRequestRepository;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    // Approximate per-kg rates derived from the project's sample pricing
    private static final Map<Order.ServiceType, BigDecimal> RATE_PER_KG = Map.of(
            Order.ServiceType.WASH_FOLD, BigDecimal.valueOf(50),
            Order.ServiceType.DRY_CLEAN, BigDecimal.valueOf(150),
            Order.ServiceType.FOLD_ONLY, BigDecimal.valueOf(40)
    );

    @Transactional
    public OrderResponse createOrder(NewOrderRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // BR-002: pickup must be scheduled at least 24 hours in advance; same-day scheduling is not permitted.
        // Compared by calendar date (not exact hours) since the form only collects a date + a
        // time-slot label, not a precise time — this matches the SRS's literal "same-day not
        // permitted" wording and avoids incorrectly rejecting valid next-day bookings.
        LocalDate today = LocalDate.now();
        if (!request.getScheduledDate().isAfter(today)) {
            throw new IllegalArgumentException("Pickup must be scheduled at least 24 hours in advance (same-day scheduling is not permitted)");
        }

        Order.ServiceType serviceType;
        try {
            serviceType = Order.ServiceType.valueOf(request.getServiceType());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid service type");
        }

        // BR-008 is also enforced at the DTO validation level (@DecimalMin/@DecimalMax);
        // re-checked here as a safety net since this method could be called from elsewhere later.
        if (request.getWeightKg() <= 0 || request.getWeightKg() > 50) {
            throw new IllegalArgumentException("Weight must be greater than 0 kg and at most 50 kg");
        }

        // Step 1: create the pickup request first — orders cannot exist without one
        PickupRequest pickup = PickupRequest.builder()
                .user(user)
                .pickupAddress(request.getPickupAddress())
                .scheduledDate(request.getScheduledDate())
                .timeSlot(request.getTimeSlot())
                .notes(request.getPickupNotes())
                .status(PickupRequest.PickupStatus.PENDING)
                .build();
        PickupRequest savedPickup = pickupRequestRepository.save(pickup);

        // Step 2: create the order, linked to that pickup (mandatory FK)
        BigDecimal rate = RATE_PER_KG.get(serviceType);
        BigDecimal totalPrice = rate.multiply(BigDecimal.valueOf(request.getWeightKg()))
                .setScale(2, RoundingMode.HALF_UP);

        Order order = Order.builder()
                .user(user)
                .pickupRequest(savedPickup)
                .serviceType(serviceType)
                .weightKg(request.getWeightKg())
                .totalPrice(totalPrice)
                .status(Order.OrderStatus.PENDING)
                .specialInstr(request.getSpecialInstructions())
                .paid(false)
                .build();
        Order savedOrder = orderRepository.save(order);

        // Step 3: log a confirmation notification (per the SRS Activity Diagram)
        Notification notification = Notification.builder()
                .user(user)
                .order(savedOrder)
                .message("Your order " + formatOrderCode(savedOrder.getOrderId()) + " has been placed and is pending pickup.")
                .type("ORDER_CREATED")
                .isRead(false)
                .build();
        notificationRepository.save(notification);

        return toResponse(savedOrder);
    }

    public List<OrderResponse> getOrdersForUser(Long userId) {
        return orderRepository.findByUser_UserIdOrderByCreatedAtDesc(userId)
                .stream().map(this::toResponse).toList();
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc()
                .stream().map(this::toResponse).toList();
    }

    private String formatOrderCode(Long orderId) {
        return "ORD-" + (1000 + orderId);
    }

    private OrderResponse toResponse(Order order) {
        PickupRequest pickup = order.getPickupRequest();
        return OrderResponse.builder()
                .orderId(order.getOrderId())
                .pickupId(pickup.getPickupId())
                .pickupAddress(pickup.getPickupAddress())
                .scheduledDate(pickup.getScheduledDate())
                .timeSlot(pickup.getTimeSlot())
                .serviceType(order.getServiceType().name())
                .weightKg(order.getWeightKg())
                .totalPrice(order.getTotalPrice())
                .status(order.getStatus().name())
                .specialInstructions(order.getSpecialInstr())
                .paid(order.getPaid())
                .createdAt(order.getCreatedAt())
                .build();
    }
}
