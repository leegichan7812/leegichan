package com.seowon.coding.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Product name is required")
    private String name;

    private String description;

    @Positive(message = "Price must be positive")
    private BigDecimal price;

    private int stockQuantity;

    private String category;

    // Business logic
    public boolean isInStock() {
        return stockQuantity > 0;
    }

    public void decreaseStock(int quantity) {
        if (quantity > stockQuantity) {
            throw new IllegalArgumentException("Not enough stock available");
        }
        stockQuantity -= quantity;
    }

    public void increaseStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        stockQuantity += quantity;
    }

    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");
    private static final BigDecimal VAT_RATE = new BigDecimal("0.10");

    public void applyPriceChange(BigDecimal percentage, boolean includeTax) {
        if (percentage == null) {
            throw new IllegalArgumentException("percentage must not be null");
        }
        if (price == null) {
            throw new IllegalStateException("price must not be null");
        }

        BigDecimal multiplier = BigDecimal.ONE.add(
                percentage.divide(ONE_HUNDRED, 4, RoundingMode.HALF_UP));

        BigDecimal changedPrice = price.multiply(multiplier);

        if (includeTax) {
            changedPrice = changedPrice.multiply(BigDecimal.ONE.add(VAT_RATE));
        }

        this.price = changedPrice.setScale(2, RoundingMode.HALF_UP);
    }
}