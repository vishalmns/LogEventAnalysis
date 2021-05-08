package com.example.log;

import java.util.Date;

public class Log {
    String serviceName;
    Date timestamp;
    String logMessage;
    int statusCode;

    public Log(String serviceName, Date timestamp, String logMessage, int statusCode) {
        this.serviceName = serviceName;
        this.timestamp = timestamp;
        this.logMessage = logMessage;
        this.statusCode = statusCode;
    }


}
