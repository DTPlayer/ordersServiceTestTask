package io.refactor.ordersservice.db;

import io.refactor.ordersservice.db.mappers.OrderDetailsRowMapper;
import io.refactor.ordersservice.db.mappers.OrderRowMapper;
import io.refactor.ordersservice.db.models.OrderDetailModel;
import io.refactor.ordersservice.db.models.OrderModel;
import io.refactor.ordersservice.models.request.CreateOrderDetails;
import io.refactor.ordersservice.models.request.CreateOrderRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;

@Service
public class OrderService {

    private final JdbcTemplate jdbcTemplate;

    public OrderService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void createOrder(CreateOrderRequest createOrderRequest, Long orderId) {

        double totalAmount = 0d;

        for (CreateOrderDetails details : createOrderRequest.getDetails()) {
            totalAmount += details.getAmountPerItem().doubleValue() * details.getQuantity();
        }

        KeyHolder keyHolder = new GeneratedKeyHolder();

        double finalTotalAmount = totalAmount;
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO public.\"order\" " +
                            "(order_id, total_amount, date_order, recipient, address_delivery, payment_type, delivery_type) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setLong(1, orderId);
            ps.setBigDecimal(2, BigDecimal.valueOf(finalTotalAmount));
            ps.setDate(3, new java.sql.Date(new Date().getTime()));
            ps.setString(4, createOrderRequest.getRecipient());
            ps.setString(5, createOrderRequest.getAddressDelivery());
            ps.setString(6, createOrderRequest.getPaymentType());
            ps.setString(7, createOrderRequest.getDeliveryType());
            return ps;
        }, keyHolder);

        for (CreateOrderDetails details : createOrderRequest.getDetails()) {
            jdbcTemplate.update(
                    "INSERT INTO public.\"order_detail\" " +
                    "(article_id, item_name, quantity, amount_per_item, order_id) " +
                    "VALUES (?, ?, ?, ?, ?)",
                    details.getArticleId(),
                    details.getProductName(),
                    details.getQuantity(),
                    details.getAmountPerItem(),
                    keyHolder.getKeyList().getFirst().get("id")
            );
        }
    }

    public OrderModel getOrder(Long orderId) {
        String sqlOrder = "SELECT * FROM public.\"order\" WHERE order_id=?";
        String sqlOrderDetails = "SELECT * FROM public.\"order_detail\" WHERE order_id=?";

        OrderModel order = jdbcTemplate.queryForObject(sqlOrder, new OrderRowMapper(), orderId);
        assert order != null;
        List<OrderDetailModel> orderDetails = jdbcTemplate.query(sqlOrderDetails, new OrderDetailsRowMapper(), order.getId());

        order.setDetails(orderDetails);

        return order;
    }
}
