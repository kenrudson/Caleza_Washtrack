package edu.cit.caleza.washtrack.features.staffordermanagement;

import edu.cit.caleza.washtrack.shared.entity.Order;
import edu.cit.caleza.washtrack.shared.entity.PickupRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StaffOrderService {

    private final StaffOrderRepository orderRepository;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MMMM d, yyyy");

    // BR-003: Pending -> Picked Up -> Processing -> Ready -> Delivered. No skipping, no reversing.
    private static final List<Order.OrderStatus> STATUS_SEQUENCE = List.of(
            Order.OrderStatus.PENDING,
            Order.OrderStatus.PICKED_UP,
            Order.OrderStatus.PROCESSING,
            Order.OrderStatus.READY,
            Order.OrderStatus.DELIVERED
    );

    public List<StaffOrderResponse> getAllOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc()
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    public StaffOrderResponse advanceStatus(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        int currentIndex = STATUS_SEQUENCE.indexOf(order.getStatus());
        if (currentIndex == STATUS_SEQUENCE.size() - 1) {
            throw new IllegalArgumentException("Order is already Delivered and cannot be advanced further");
        }

        // The order cannot be marked Picked Up before the customer's scheduled pickup
        // date has actually arrived. This is checked against the server's own clock,
        // not the caller's device, since a client-side date can't be trusted for a
        // business rule like this.
        if (order.getStatus() == Order.OrderStatus.PENDING) {
            LocalDate scheduledDate = order.getPickupRequest().getScheduledDate();
            if (LocalDate.now().isBefore(scheduledDate)) {
                throw new IllegalArgumentException(
                        "This order cannot be marked as picked up yet. The scheduled pickup date is "
                                + scheduledDate.format(DATE_FORMAT) + "."
                );
            }
        }

        Order.OrderStatus nextStatus = STATUS_SEQUENCE.get(currentIndex + 1);
        order.setStatus(nextStatus);
        Order saved = orderRepository.save(order);

        return toResponse(saved);
    }

    // Lightweight payment marking, reusing the existing Order.paid flag.
    // NOTE: this is an interim implementation. It does not yet record payment type
    // (on-delivery vs on-pickup) or create a dedicated Payment record as described
    // in the approved ERD — that fuller version is planned as a later, separate task.
    @Transactional
    public StaffOrderResponse markAsPaid(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (order.getStatus() == Order.OrderStatus.PENDING) {
            throw new IllegalArgumentException("Cannot record payment before the order has been picked up");
        }

        order.setPaid(true);
        Order saved = orderRepository.save(order);

        return toResponse(saved);
    }

    private StaffOrderResponse toResponse(Order order) {
        PickupRequest pickup = order.getPickupRequest();
        return StaffOrderResponse.builder()
                .orderId(order.getOrderId())
                .orderCode("ORD-" + (1000 + order.getOrderId()))
                .customerName(order.getUser().getFullName())
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
