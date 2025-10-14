package ru.practicum.shareit;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.exception.CustomFeignException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class CustomErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        String message = String.format("Feign client error - Status: %s, Method: %s",
                response.status(), methodKey);

        String responseBody = extractResponseBody(response);

        log.warn("Feign error occurred: {}", message);

        return new CustomFeignException(
                message,
                response.status(),
                responseBody
        );
    }

    private String extractResponseBody(Response response) {
        try {
            if (response.body() != null) {
                return new String(response.body().asInputStream().readAllBytes(),
                        StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            log.warn("Failed to extract response body", e);
        }
        return null;
    }
}