package io.refactor.ordersservice.models.response;

import io.refactor.ordersservice.db.models.OrderModel;
import io.refactor.ordersservice.models.response.base.BaseResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class OrderResponse extends BaseResponse {
    private OrderModel order;

    public OrderResponse(OrderModel order) {
        this.order = order;
    }

    public OrderResponse(OrderModel order, String message, Integer status) {
        super(message, status);
        this.order = order;
    }
}
