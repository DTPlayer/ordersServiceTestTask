package io.refactor.ordersservice.db;

import io.refactor.ordersservice.db.mappers.OrderDetailsRowMapper;
import io.refactor.ordersservice.db.mappers.OrderRowMapper;
import io.refactor.ordersservice.db.models.OrderDetailModel;
import io.refactor.ordersservice.db.models.OrderModel;
import io.refactor.ordersservice.models.request.CreateOrderDetails;
import io.refactor.ordersservice.models.request.CreateOrderRequest;
import jakarta.validation.Valid;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Date;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class OrderService {

    private final JdbcTemplate jdbcTemplate;
    private static final String sqlDetails = "SELECT * FROM public.\"order_detail\" WHERE order_id=?";
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public OrderService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean createOrder(@Valid CreateOrderRequest createOrderRequest, Long orderId) {
        try {
            if (createOrderRequest == null) {
                return false;
            }

            double totalAmount = 0d;
            for (CreateOrderDetails details : createOrderRequest.getDetails()) {
                totalAmount += details.getAmountPerItem().doubleValue() * details.getQuantity();
            }

            KeyHolder keyHolder = new GeneratedKeyHolder();
            double finalTotalAmount = totalAmount;

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                        "INSERT INTO public.\"order\" " +
                                "(order_id, total_amount, recipient, address_delivery, payment_type, delivery_type) " +
                                "VALUES (?, ?, ?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS
                );
                ps.setLong(1, orderId);
                ps.setBigDecimal(2, BigDecimal.valueOf(finalTotalAmount));
                ps.setString(3, createOrderRequest.getRecipient());
                ps.setString(4, createOrderRequest.getAddressDelivery());
                ps.setString(5, createOrderRequest.getPaymentType());
                ps.setString(6, createOrderRequest.getDeliveryType());
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

            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public OrderModel getOrder(Long orderId) {
        String sqlOrder = "SELECT * FROM public.\"order\" WHERE order_id=?";

        OrderModel order = jdbcTemplate.queryForObject(sqlOrder, new OrderRowMapper(), orderId);
        assert order != null;
        List<OrderDetailModel> orderDetails = jdbcTemplate.query(sqlDetails, new OrderDetailsRowMapper(), order.getId());

        order.setDetails(orderDetails);

        return order;
    }

    public Optional<List<OrderModel>> getFilterOrders(Date actualDay) {
        String sql = "SELECT * FROM public.\"order\" WHERE DATE(date_order) = ?";

        List<OrderModel> orders = jdbcTemplate.query(sql, new OrderRowMapper(), actualDay);

        return getOrderModels(orders);
    }

    public Optional<List<OrderModel>> getFilterOrders(Long minAmount) {
        if (minAmount < 0) {
            return Optional.empty();
        }

        String sql = "SELECT * FROM public.\"order\" WHERE total_amount > ?";

        List<OrderModel> orders = jdbcTemplate.query(sql, new OrderRowMapper(), minAmount);

        return getOrderModels(orders);
    }

    public Optional<List<OrderModel>> getFilterOrders(Date actualDay, Long minAmount) {
        if (minAmount < 0) {
            return Optional.empty();
        }

        String sql = "SELECT * FROM public.\"order\" WHERE DATE(date_order) = ? AND total_amount > ?";

        List<OrderModel> orders = jdbcTemplate.query(sql, new OrderRowMapper(), actualDay, minAmount);

        return getOrderModels(orders);
    }

    public Optional<List<OrderModel>> getFilterOrdersDetails(List<Long> disableArticles) {
        String sql = "SELECT * FROM public.\"order\" " +
                "WHERE id NOT IN (SELECT order_id FROM public.\"order_detail\" WHERE article_id = ANY (?))";

        Long[] articleArray = disableArticles.toArray(new Long[0]);

        List<OrderModel> orders = jdbcTemplate.query(sql, (ps) -> {
            ps.setArray(1, ps.getConnection().createArrayOf("BIGINT", articleArray));
        }, new OrderRowMapper());

        return getOrderModels(orders);
    }

    public Optional<List<OrderModel>> getFilterOrdersDetails(Date startDate, Date endDate, List<Long> disableArticles) {
        String sql = "SELECT * FROM public.\"order\" " +
                "WHERE id NOT IN (SELECT order_id FROM public.\"order_detail\" WHERE article_id = ANY (?)) " +
                "AND DATE(date_order) BETWEEN ? AND ?";

        Long[] articleArray = disableArticles.toArray(new Long[0]);

        List<OrderModel> orders = jdbcTemplate.query(sql, (ps) -> {
            ps.setArray(1, ps.getConnection().createArrayOf("BIGINT", articleArray));
            ps.setDate(2, startDate);
            ps.setDate(3, endDate);
        }, new OrderRowMapper());

        return getOrderModels(orders);
    }

    public Optional<List<OrderModel>> getFilterOrdersDetails(Date startDate, Date endDate) {
        String sql = "SELECT * FROM public.\"order\" WHERE DATE(date_order) BETWEEN ? AND ?";

        List<OrderModel> orders = jdbcTemplate.query(sql, new OrderRowMapper(), startDate, endDate);

        return getOrderModels(orders);
    }

    private Optional<List<OrderModel>> getOrderModels(List<OrderModel> orders) {
        if (orders.isEmpty()) {
            return Optional.empty();
        }

        for (OrderModel order : orders) {
            List<OrderDetailModel> orderDetails = jdbcTemplate.query(sqlDetails, new OrderDetailsRowMapper(), order.getId());
            order.setDetails(orderDetails);
        }

        return Optional.of(orders);
    }
}
