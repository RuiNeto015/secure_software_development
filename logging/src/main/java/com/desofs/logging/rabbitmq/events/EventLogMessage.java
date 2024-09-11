package com.desofs.logging.rabbitmq.events;

import com.desofs.logging.enums.LogType;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@ToString
@Data
@NoArgsConstructor
public class EventLogMessage {

    private String messageId;
    private String message;
    private LogType logType;
    private Date timestamp;
    private String provider;
    private String user;
    private String ipAddress;

}
