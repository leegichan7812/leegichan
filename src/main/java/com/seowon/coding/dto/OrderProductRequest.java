package com.seowon.coding.dto;

import lombok.Data;

@Data
public class OrderProductRequest {
    private Long productId;
    private int quantity;
}