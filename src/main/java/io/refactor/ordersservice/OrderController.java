package io.refactor.ordersservice;

import io.refactor.ordersservice.db.OrderService;
import io.refactor.ordersservice.db.models.OrderModel;
import io.refactor.ordersservice.models.request.CreateOrderRequest;
import io.refactor.ordersservice.models.response.FilterOrderResponse;
import io.refactor.ordersservice.models.response.OrderResponse;
import io.refactor.ordersservice.models.response.base.BaseResponse;
import io.refactor.ordersservice.utils.GetOrderCode;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.sql.Date;
import java.util.Optional;
import java.util.List;
import java.util.TimeZone;

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

        boolean created = orderService.createOrder(createOrderRequest, orderId.get());

        if (!created) {
            response.setStatus(400);
            return new BaseResponse("Unable to create order", 400);
        }

        return new BaseResponse();
    }

    @GetMapping("/get/{orderId}")
    public OrderResponse getOrder(@PathVariable Long orderId, HttpServletResponse response) {

        OrderModel order = orderService.getOrder(orderId);

        return new OrderResponse(order);
    }

    @GetMapping("/filter/order")
    public FilterOrderResponse getFiltersWithOrder(
            @RequestParam(required = false) @Nullable String actualDay,
            @RequestParam(required = false) @Nullable Long minAmount,
            HttpServletResponse response
    ) throws ParseException {
        if (actualDay == null && minAmount == null) {
            response.setStatus(422);
            return new FilterOrderResponse(null, "Invalid request", 422);
        }

        Optional<List<OrderModel>> filteredOrders;
        DateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        if (actualDay != null && minAmount != null) {
            filteredOrders = orderService.getFilterOrders(new Date(dateFormat.parse(actualDay).getTime()), minAmount);
        } else if (actualDay != null) {
            filteredOrders = orderService.getFilterOrders(new Date(dateFormat.parse(actualDay).getTime()));
        } else {
            filteredOrders = orderService.getFilterOrders(minAmount);
        }

        if (filteredOrders.isPresent()) {
            return new FilterOrderResponse(filteredOrders.get(), "", 200);
        } else {
            response.setStatus(404);
            return new FilterOrderResponse(null, "No orders found", 404);
        }
    }


    @GetMapping("/filter/orderDetails")
    public FilterOrderResponse getFiltersWithOrderDetails(
            @RequestParam(required = false) @Nullable String startDate,
            @RequestParam(required = false) @Nullable String endDate,
            @RequestParam(required = false) @Nullable List<Long> disableArticles,
            HttpServletResponse response
    ) throws ParseException {
        if (startDate == null && endDate == null && disableArticles == null) {
            response.setStatus(422);
            return new FilterOrderResponse(null, "Invalid request", 422);
        }

        if ((startDate != null && endDate == null) || (startDate == null && endDate != null)) {
            response.setStatus(422);
            return new FilterOrderResponse(null, "Invalid request", 422);
        }

        Optional<List<OrderModel>> filteredOrders;
        DateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        if (startDate == null) {
            System.out.println(disableArticles);
            filteredOrders = orderService.getFilterOrdersDetails(disableArticles);
        } else if (disableArticles == null) {
            filteredOrders = orderService.getFilterOrdersDetails(
                new Date(dateFormat.parse(startDate).getTime()),
                new Date(dateFormat.parse(endDate).getTime())
            );
        } else {
            filteredOrders = orderService.getFilterOrdersDetails(
                new Date(dateFormat.parse(startDate).getTime()),
                new Date(dateFormat.parse(endDate).getTime()),
                disableArticles
            );
        }

        if (filteredOrders.isPresent()) {
            return new FilterOrderResponse(filteredOrders.get(), "", 200);
        } else {
            response.setStatus(404);
            return new FilterOrderResponse(null, "No orders found", 404);
        }
    }
}
