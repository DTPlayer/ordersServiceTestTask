package io.refactor.ordersservice.db.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
public class OrderModel {
    private Long id;
    private Long orderId;
    private String recipient;
    private String addressDelivery;
    private String paymentType;
    private String deliveryType;
    private BigDecimal totalAmount;
    private Date dateOrder;
    private List<OrderDetailModel> details;
}
