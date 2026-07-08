package edu.cit.caleza.washtrack.features.orderhistory;

import edu.cit.caleza.washtrack.shared.entity.Order;
import edu.cit.caleza.washtrack.shared.entity.PickupRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderHistoryService {

    private final OrderHistoryRepository orderRepository;

    public List<OrderHistoryResponse> getOrdersForUser(Long userId) {
        return orderRepository.findByUser_UserIdOrderByCreatedAtDesc(userId)
                .stream().map(this::toResponse).toList();
    }

    public List<OrderHistoryResponse> getAllOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc()
                .stream().map(this::toResponse).toList();
    }

    private OrderHistoryResponse toResponse(Order order) {
        PickupRequest pickup = order.getPickupRequest();
        return OrderHistoryResponse.builder()
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