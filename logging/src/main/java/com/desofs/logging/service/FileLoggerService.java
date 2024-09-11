package com.desofs.logging.service;

import com.desofs.logging.model.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;

@Service
@Slf4j
public class FileLoggerService {

    private static final String LOG_FILE_PATH = "logs/application.log";

    public void writeLog(Log myLog) {
        String formattedLog = formatLog(myLog);
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE_PATH, true))) {
            writer.println(formattedLog);
        } catch (IOException e) {
            log.error("Error writing log {}, exception: {}", myLog, e.getMessage());
        }
    }

    private String formatLog(Log log) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedTimestamp = dateFormat.format(log.getTimestamp());

        int timestampWidth = 19;
        int logTypeWidth = 7;
        int providerWidth = 8;
        int ipAddressWidth = 15;
        int userWidth = 36;

        return String.format("%-" + timestampWidth + "s " +
                        "[%-" + logTypeWidth + "s] " +
                        "[%-" + providerWidth + "s]" +
                        " [%-" + ipAddressWidth + "s] " +
                        "[%-" + userWidth + "s] [%s]",
                formattedTimestamp, log.getLogType(), log.getProvider(), log.getIpAddress(), log.getUser(), log.getMessage());
    }

}
