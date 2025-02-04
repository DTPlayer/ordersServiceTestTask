package io.refactor.ordersservice.db.mappers;

import io.refactor.ordersservice.db.models.OrderDetailModel;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderDetailsRowMapper implements RowMapper<OrderDetailModel> {

    @Override
    public OrderDetailModel mapRow(ResultSet rs, int rowNext) throws SQLException {
        return new OrderDetailModel(
            rs.getLong("article_id"),
            rs.getString("item_name"),
            rs.getInt("quantity"),
            rs.getBigDecimal("amount_per_item")
        );
    }
}
