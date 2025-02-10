package io.refactor.ordersservice;

import io.refactor.ordersservice.db.OrderService;
import io.refactor.ordersservice.db.models.OrderModel;
import io.refactor.ordersservice.models.request.CreateOrderDetails;
import io.refactor.ordersservice.models.request.CreateOrderRequest;
import io.refactor.ordersservice.models.response.base.BaseResponse;
import io.refactor.ordersservice.utils.GetOrderCode;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class OrdersServiceApplicationTests {
    @Autowired
    private OrderService orderService;

    @Autowired
    private GetOrderCode getOrderCode;

    @Autowired
    private OrderController orderController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetCode() {
        Optional<Long> code = getOrderCode.getCode();
        assertTrue(code.isPresent());
    }

    @Test
    void testCreateOrder_Success() {
        CreateOrderRequest request = new CreateOrderRequest(
                new CreateOrderDetails[]{
                        new CreateOrderDetails(1L, "Товар 1", 1, BigDecimal.valueOf(100))
                },
                "Ivanov Ivan",
                "г. Москва, ул. Ленина, д. 1",
                "card",
                "delivery"
        );

        HttpServletResponse response = mock(HttpServletResponse.class);

        BaseResponse result = orderController.createOrder(request, response);

        assertEquals("", result.getMessage());
        assertEquals(200, result.getStatus().intValue());
    }

    @Test
    void testCreateOrder_Failure() {
        CreateOrderRequest request = new CreateOrderRequest(
                new CreateOrderDetails[]{
                        new CreateOrderDetails(1L, "Товар 1", 1, BigDecimal.valueOf(100))
                },
                null,
                "г. Москва, ул. Ленина, д. 1",
                "card",
                "delivery"
        );

        HttpServletResponse response = mock(HttpServletResponse.class);

        BaseResponse result = orderController.createOrder(request, response);

        assertEquals("Unable to create order", result.getMessage());
        assertEquals(400, result.getStatus().intValue());
    }

    @Test
    void testFilterWithOrder_Date() {
        Date actualDay = new Date(System.currentTimeMillis());
        Optional<List<OrderModel>> filteredOrders = orderService.getFilterOrders(actualDay);

        assertTrue(filteredOrders.isPresent());
    }

    @Test
    void testFilterWithOrder_Amount() {
        Long minAmount = 100L;

        Optional<List<OrderModel>> filteredOrders = orderService.getFilterOrders(minAmount);

        assertTrue(filteredOrders.isPresent());
    }

    @Test
    void testFilterWithOrder_DateAndAmount() {
        Date actualDay = new Date(System.currentTimeMillis());
        Long minAmount = 50L;

        Optional<List<OrderModel>> filteredOrders = orderService.getFilterOrders(actualDay, minAmount);

        assertTrue(filteredOrders.isPresent());
    }

    @Test
    void testFilterWinOrder_InvalidAmount() {
        Long minAmount = -50L;

        Optional<List<OrderModel>> filteredOrders = orderService.getFilterOrders(minAmount);

        assertFalse(filteredOrders.isPresent());
    }
}
