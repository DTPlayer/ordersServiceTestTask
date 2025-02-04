package io.refactor.ordersservice.db.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class OrderDetailModel {
    private Long articleId;
    private String productName;
    private Integer quantity;
    private BigDecimal amountPerItem;
}
