package edu.cit.caleza.washtrack.features.neworder;

import edu.cit.caleza.washtrack.shared.entity.Notification;
import edu.cit.caleza.washtrack.shared.entity.Order;
import edu.cit.caleza.washtrack.shared.entity.PickupRequest;
import edu.cit.caleza.washtrack.shared.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NewOrderService {

    private final NewOrderRepository orderRepository;
    private final NewOrderPickupRepository pickupRequestRepository;
    private final NewOrderNotificationRepository notificationRepository;
    private final NewOrderUserRepository userRepository;

    private static final Map<Order.ServiceType, BigDecimal> RATE_PER_KG = Map.of(
            Order.ServiceType.WASH_FOLD, BigDecimal.valueOf(50),
            Order.ServiceType.DRY_CLEAN, BigDecimal.valueOf(150),
            Order.ServiceType.FOLD_ONLY, BigDecimal.valueOf(40)
    );

    @Transactional
    public NewOrderResponse createOrder(NewOrderRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

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

        if (request.getWeightKg() <= 0 || request.getWeightKg() > 50) {
            throw new IllegalArgumentException("Weight must be greater than 0 kg and at most 50 kg");
        }

        PickupRequest pickup = PickupRequest.builder()
                .user(user)
                .pickupAddress(request.getPickupAddress())
                .scheduledDate(request.getScheduledDate())
                .timeSlot(request.getTimeSlot())
                .notes(request.getPickupNotes())
                .status(PickupRequest.PickupStatus.PENDING)
                .build();
        PickupRequest savedPickup = pickupRequestRepository.save(pickup);

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

        Notification notification = Notification.builder()
                .user(user)
                .order(savedOrder)
                .message("Your order ORD-" + (1000 + savedOrder.getOrderId()) + " has been placed and is pending pickup.")
                .type("ORDER_CREATED")
                .isRead(false)
                .build();
        notificationRepository.save(notification);

        return toResponse(savedOrder);
    }

    private NewOrderResponse toResponse(Order order) {
        PickupRequest pickup = order.getPickupRequest();
        return NewOrderResponse.builder()
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