package io.refactor.ordersservice;

import io.refactor.ordersservice.db.OrderService;
import io.refactor.ordersservice.models.response.base.PingResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrderController {

    public final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/")
    public PingResponse getOrders() {
        return new PingResponse();
    }
}
