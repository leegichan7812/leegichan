package com.seowon.coding.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders") // "order" is a reserved keyword in SQL
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerName;

    private String customerEmail;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private LocalDateTime orderDate;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    private BigDecimal totalAmount;

    // Business logic
    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
        recalculateTotalAmount();
    }

    public void removeItem(OrderItem item) {
        items.remove(item);
        item.setOrder(null);
        recalculateTotalAmount();
    }

    public void recalculateTotalAmount() {
        this.totalAmount = items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void markAsProcessing() {
        this.status = OrderStatus.PROCESSING;
    }

    public void markAsShipped() {
        this.status = OrderStatus.SHIPPED;
    }

    public void markAsDelivered() {
        this.status = OrderStatus.DELIVERED;
    }

    public void markAsCancelled() {
        this.status = OrderStatus.CANCELLED;
    }

    public enum OrderStatus {
        PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED
    }

    public static Order create(String customerName, String customerEmail) {
        return Order.builder()
                .customerName(customerName)
                .customerEmail(customerEmail)
                .status(OrderStatus.PENDING)
                .orderDate(LocalDateTime.now())
                .items(new ArrayList<>())
                .totalAmount(BigDecimal.ZERO)
                .build();
    }

    public void addOrderItem(Product product, int quantity) {
        OrderItem item = OrderItem.builder()
                .order(this)
                .product(product)
                .quantity(quantity)
                .price(product.getPrice())
                .build();

        this.items.add(item);
    }

    public void checkout(String couponCode) {
        BigDecimal subtotal = items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal shipping = subtotal.compareTo(new BigDecimal("100.00")) >= 0
                ? BigDecimal.ZERO
                : new BigDecimal("5.00");

        BigDecimal discount = (couponCode != null && couponCode.startsWith("SALE"))
                ? new BigDecimal("10.00")
                : BigDecimal.ZERO;

        this.totalAmount = subtotal.add(shipping).subtract(discount);
        this.status = OrderStatus.PROCESSING;
    }
}