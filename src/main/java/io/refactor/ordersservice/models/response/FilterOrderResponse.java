package io.refactor.ordersservice.models.response;

import io.refactor.ordersservice.db.models.OrderModel;
import io.refactor.ordersservice.models.response.base.BaseResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class FilterOrderResponse extends BaseResponse {
    private List<OrderModel> order;

    public FilterOrderResponse(List<OrderModel> order, String message, Integer status) {
        super(message, status);
        this.order = order;
    }
}
