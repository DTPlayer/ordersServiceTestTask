package io.refactor.ordersservice.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.Optional;

@Service
public class GetOrderCode {

    @Value("${generate.endpoint:http://localhost:8080/numbers/generate}")
    private String endpoint;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Data
    private static class Response {
        private String message;
        private int status;
        private Long code;
    }

    public Optional<Long> getCode() {
        try {
            URL url = URI.create(endpoint).toURL();

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                return Optional.empty();
            }

            Response response = objectMapper.readValue(connection.getInputStream(), Response.class);

            return Optional.ofNullable(response.getCode());
        } catch (Exception e) {
            System.err.println("Error retrieving order code: " + e.getMessage());
            return Optional.empty();
        }
    }
}