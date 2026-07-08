package edu.cit.caleza.washtrack.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders") // "order" is a reserved SQL keyword, same reasoning as users vs user
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // BR: mandatory pickup-first flow — every order must reference an existing pickup request
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pickup_id", nullable = false)
    private PickupRequest pickupRequest;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServiceType serviceType;

    @Column(nullable = false)
    private Double weightKg;

    @Column(nullable = false)
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    private String specialInstr;

    @Column(nullable = false)
    private Boolean paid;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) this.status = OrderStatus.PENDING;
        if (this.paid == null) this.paid = false;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // BR-003: real workflow order — Pending -> Picked Up -> Processing -> Ready -> Delivered.
    // (Note: this is the actual state machine used for validation. It is separate from the
    // dashboard's visual step display order, which was intentionally reordered for the UI.)
    public enum OrderStatus {
        PENDING, PICKED_UP, PROCESSING, READY, DELIVERED
    }

    public enum ServiceType {
        WASH_FOLD, DRY_CLEAN, FOLD_ONLY
    }
}
