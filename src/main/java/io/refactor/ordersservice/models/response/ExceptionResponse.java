package io.refactor.ordersservice.models.response;

import io.refactor.ordersservice.models.response.base.BaseResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
public class ExceptionResponse extends BaseResponse {
    private Map<String, String> details;

    public ExceptionResponse(Map<String, String> details, String message, int status) {
        super(message, status);
        this.details = details;
    }
}
