package com.desofs.backend.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.owasp.encoder.Encode;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class XSSRequestWrapper extends HttpServletRequestWrapper {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Set<String> fieldsToIgnore;

    public XSSRequestWrapper(HttpServletRequest request, Set<String> fieldsToIgnore) {
        super(request);
        this.fieldsToIgnore = fieldsToIgnore;
    }

    @Override
    public String[] getParameterValues(String parameter) {
        String[] values = super.getParameterValues(parameter);
        if (values == null) {
            return null;
        }
        String[] sanitizedValues = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            sanitizedValues[i] = sanitizeInput(parameter, values[i]);
        }
        return sanitizedValues;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        ServletInputStream originalInputStream = super.getInputStream();
        String requestBody = new String(originalInputStream.readAllBytes());
        String sanitizedBody = sanitizeJsonBody(requestBody);
        return new ServletInputStream() {
            private final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                    sanitizedBody.getBytes()
            );

            @Override
            public int read() {
                return byteArrayInputStream.read();
            }

            @Override
            public boolean isFinished() {
                return byteArrayInputStream.available() == 0;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
            }
        };
    }

    private String sanitizeInput(String fieldName, String input) {
        if (fieldName != null && fieldsToIgnore.stream().anyMatch(fieldName::contains)) {
            return input;
        }
        return Encode.forHtml(input);
    }

    private String sanitizeJsonBody(String jsonBody) throws IOException {
        Map<String, Object> map = objectMapper.readValue(jsonBody, new TypeReference<>() {
        });
        sanitizeMap(map);
        return objectMapper.writeValueAsString(map);
    }

    private void sanitizeMap(Map<String, Object> map) {
        map.replaceAll((key, value) -> {
            if (value instanceof String) {
                return sanitizeInput(key, (String) value);
            } else if (value instanceof Map) {
                sanitizeMap((Map<String, Object>) value);
            } else if (value instanceof List) {
                sanitizeList((List<Object>) value);
            }
            return value;
        });
    }

    private void sanitizeList(List<Object> list) {
        for (int i = 0; i < list.size(); i++) {
            Object value = list.get(i);
            if (value instanceof String) {
                list.set(i, sanitizeInput(null, (String) value));
            } else if (value instanceof Map) {
                sanitizeMap((Map<String, Object>) value);
            } else if (value instanceof List) {
                sanitizeList((List<Object>) value);
            }
        }
    }
}
