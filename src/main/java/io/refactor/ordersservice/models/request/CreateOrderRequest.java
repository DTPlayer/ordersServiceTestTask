package io.refactor.ordersservice.models.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateOrderRequest {
    @NotNull(message = "Details must not be null")
    @Schema(description = "Array of order details", requiredMode = Schema.RequiredMode.REQUIRED)
    private CreateOrderDetails[] details;

    @Schema(description = "Recipient's name", example = "Иванов Иван Иванович", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Recipient must not be null")
    private String recipient;

    @Schema(description = "Address for delivery", example = "ул. Ленина д. 1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Address delivery must not be null")
    private String addressDelivery;

    @NotNull(message = "Payment type must not be null")
    @Pattern(regexp = "^(card|cash)$", message = "Payment type must be 'card' or 'cash'")
    @Schema(description = "Payment method", example = "card", allowableValues = {"card", "cash"}, requiredMode = Schema.RequiredMode.REQUIRED)
    private String paymentType;

    @NotNull(message = "Delivery type must not be null")
    @Pattern(regexp = "^(pickup|delivery)$", message = "Delivery type must be 'pickup' or 'delivery'")
    @Schema(description = "Delivery method", example = "delivery", allowableValues = {"pickup", "delivery"}, requiredMode = Schema.RequiredMode.REQUIRED)
    private String deliveryType;
}