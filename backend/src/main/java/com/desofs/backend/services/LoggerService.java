package com.desofs.backend.services;

import com.desofs.backend.dtos.logs.EventLogMessage;
import com.desofs.backend.enums.LogType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import static com.desofs.backend.config.RemoteIpFilter.getClientIp;

@Service
@RequiredArgsConstructor
public class LoggerService {

    private final EncryptionService encryptionService;
    private final ObjectMapper objectMapper;
    private final TopicExchange exchange;
    private final RabbitTemplate rabbitTemplate;

    private void sendLog(String message, LogType logType) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String name = "N/A";
        if (authentication != null) {
            name = authentication.getName();
        }

        String ipAddress = getClientIp();
        if (ipAddress == null) {
            ipAddress = "N/A";
        }

        EventLogMessage eventLogMessage = new EventLogMessage(message, logType, "BACKEND", ipAddress, name);
        try {
            String objectString = objectMapper.writeValueAsString(eventLogMessage);
            String safeStringMessage = encryptionService.encryptAndSignMessage(objectString);

            rabbitTemplate.convertAndSend(exchange.getName(), "topic.logs", safeStringMessage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void info(String message) {
        this.sendLog(message, LogType.INFO);
    }

    public void warn(String message) {
        this.sendLog(message, LogType.WARNING);
    }

    public void error(String message) {
        this.sendLog(message, LogType.ERROR);
    }

    public void auth(String message) {
        this.sendLog(message, LogType.AUTH);
    }
}
