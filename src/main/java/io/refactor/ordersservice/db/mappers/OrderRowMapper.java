package io.refactor.ordersservice.db.mappers;

import io.refactor.ordersservice.db.models.OrderModel;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class OrderRowMapper implements RowMapper<OrderModel> {

    @Override
    public OrderModel mapRow(ResultSet rs, int rowNum) throws SQLException {
        if (rs.getFetchSize() > 0) {
            return null;
        }

        return new OrderModel(
            rs.getLong("id"),
            rs.getLong("order_id"),
            rs.getString("recipient"),
            rs.getString("address_delivery"),
            rs.getString("payment_type"),
            rs.getString("delivery_type"),
            rs.getBigDecimal("total_amount"),
            rs.getDate("date_order"),
            null
        );
    }
}
