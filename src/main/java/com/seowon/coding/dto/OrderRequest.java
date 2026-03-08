package com.seowon.coding.dto;

import lombok.Data;
import java.util.List;

@Data
public class OrderRequest {
    private String customerName;
    private String customerEmail;
    private List<OrderProductRequest> products;
}
