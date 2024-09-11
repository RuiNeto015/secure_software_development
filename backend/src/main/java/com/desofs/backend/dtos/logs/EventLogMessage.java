package com.desofs.backend.dtos.logs;

import com.desofs.backend.enums.LogType;
import lombok.Getter;
import lombok.ToString;

import java.util.Date;
import java.util.UUID;

@Getter
@ToString
public class EventLogMessage {

    private final String messageId;
    private final String message;
    private final LogType logType;
    private final Date timestamp;
    private final String provider;
    private final String user;
    private final String ipAddress;

    public EventLogMessage(String message, LogType logType, String provider) {
        this.messageId = UUID.randomUUID().toString();
        this.message = message;
        this.logType = logType;
        this.timestamp = new Date();
        this.provider = provider;
        this.ipAddress = "N/A";
        this.user = "N/A";
    }

    public EventLogMessage(String message, LogType logType, String provider, String ipAddress, String user) {
        this.messageId = UUID.randomUUID().toString();
        this.message = message;
        this.logType = logType;
        this.provider = provider;
        this.timestamp = new Date();
        this.ipAddress = ipAddress;
        this.user = user;
    }

}
