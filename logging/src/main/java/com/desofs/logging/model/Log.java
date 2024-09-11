package com.desofs.logging.model;

import com.desofs.logging.enums.LogType;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.UUID;

@Document
@Data
@Builder
public class Log {

    @Id
    private String id;
    private String message;
    private LogType logType;
    private Date timestamp;
    private String provider;
    private String user;
    private String ipAddress;

}
