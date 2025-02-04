package io.refactor.ordersservice;

import io.refactor.ordersservice.db.OrderService;
import io.refactor.ordersservice.db.models.OrderModel;
import io.refactor.ordersservice.models.request.CreateOrderRequest;
import io.refactor.ordersservice.models.response.OrderResponse;
import io.refactor.ordersservice.models.response.base.BaseResponse;
import io.refactor.ordersservice.utils.GetOrderCode;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

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
    public BaseResponse createOrder(
            @RequestBody @Valid CreateOrderRequest createOrderRequest,
            HttpServletResponse response
    ) {
        Optional<Long> orderId = getOrderCode.getCode();

        if (orderId.isEmpty()) {
            response.setStatus(400);
            return new BaseResponse("Unable to generate order code", 400);
        }

        orderService.createOrder(createOrderRequest, orderId.get());

        return new BaseResponse();
    }

    @GetMapping("/get/{orderId}")
    public OrderResponse getOrder(@PathVariable Long orderId, HttpServletResponse response) {

        OrderModel order = orderService.getOrder(orderId);

        return new OrderResponse(order);
    }
}
