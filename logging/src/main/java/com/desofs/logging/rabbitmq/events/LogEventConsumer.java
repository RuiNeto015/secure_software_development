package com.desofs.logging.rabbitmq.events;

import com.desofs.logging.model.Log;
import com.desofs.logging.repositories.ILogRepository;
import com.desofs.logging.service.EncryptionService;
import com.desofs.logging.service.FileLoggerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Getter
@Component
@Slf4j
@RequiredArgsConstructor
public class LogEventConsumer {

    private final ILogRepository logRepository;
    private final EncryptionService encryptionService;
    private final ObjectMapper objectMapper;
    private final FileLoggerService fileLoggerService;

    @RabbitListener(queues = "#{queue.name}")
    private void receiveQueue(String encryptedMessage) {
        try {
            String decryptedMessage = encryptionService.verifyAndDecrypt(encryptedMessage);
            EventLogMessage eventLog = objectMapper.readValue(decryptedMessage, EventLogMessage.class);

            Log log = Log.builder()
                    .message(eventLog.getMessage())
                    .logType(eventLog.getLogType())
                    .timestamp(eventLog.getTimestamp())
                    .provider(eventLog.getProvider())
                    .user(eventLog.getUser())
                    .ipAddress(eventLog.getIpAddress())
                    .build();

            logRepository.save(log);
            fileLoggerService.writeLog(log);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}