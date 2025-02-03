package io.refactor.ordersservice.models.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateOrderDetails {
    @NotNull(message = "Article id must not be null")
    @Schema(description = "Article id", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long articleId;
    @NotNull(message = "Product name must not be null")
    @Schema(description = "Product name", example = "Product 1", requiredMode = Schema.RequiredMode.REQUIRED)
    private String productName;
    @NotNull(message = "Quantity must not be null")
    @Schema(description = "Quantity", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer quantity;
    @NotNull(message = "Amount per item must not be null")
    @Schema(description = "Amount per item", example = "100", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer amountPerItem;
}
