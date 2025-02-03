package io.refactor.ordersservice.db;

import io.refactor.ordersservice.models.request.CreateOrderDetails;
import io.refactor.ordersservice.models.request.CreateOrderRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class OrderService {

    private final JdbcTemplate jdbcTemplate;

    public OrderService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void createOrder(CreateOrderRequest createOrderRequest, Long orderId) {

        int totalAmount = 0;
        List<String> queries = new ArrayList<>();

        for (CreateOrderDetails details : createOrderRequest.getDetails()) {
            totalAmount += details.getAmountPerItem() * details.getQuantity();
            queries.add(
                    String.format(
                            "INSERT INTO public.order_detail (article_id, item_name, quantity, amount_per_item, order_id) " +
                                    "VALUES (%s, %s, %s, %s, %s)",
                            details.getArticleId(),
                            details.getProductName(),
                            details.getQuantity(),
                            details.getAmountPerItem(),
                            orderId
                    )
            );

            jdbcTemplate.update(
                    "INSERT INTO public.\"order\" " +
                            "(order_id, total_amount, date_order, recipient, address_delivery, payment_type, delivery_type) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?)",
                    orderId,
                    totalAmount,
                    new Date(),
                    createOrderRequest.getRecipient(),
                    createOrderRequest.getAddressDelivery(),
                    createOrderRequest.getPaymentType(),
                    createOrderRequest.getDeliveryType()
            );

            for (String query : queries) {
                jdbcTemplate.update(query);
            }
        }
    }
}
