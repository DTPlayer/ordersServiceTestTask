package io.refactor.ordersservice;

import io.refactor.ordersservice.db.OrderService;
import io.refactor.ordersservice.models.request.CreateOrderRequest;
import io.refactor.ordersservice.models.response.base.BaseResponse;
import io.refactor.ordersservice.utils.GetOrderCode;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    public final OrderService orderService;
    public final GetOrderCode getOrderCode;

    public OrderController(OrderService orderService, GetOrderCode getOrderCode) {
        this.getOrderCode = getOrderCode;
        this.orderService = orderService;
    }

    @PostMapping("/create")
    public BaseResponse createOrder(@RequestBody @Valid CreateOrderRequest createOrderRequest) {
        return new BaseResponse();
    }
}
