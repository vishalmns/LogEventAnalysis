package com.example.log;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class LogController {

    @Autowired
    LogService logService;


    @GetMapping("anomalyone")
    public String anomolyOne() {
        try {
            logService.sendAnomalyOne();
        }
        catch (IOException e) {
            e.printStackTrace();;
            return "Failed to send anomoly one";
        }
        return "Sucessfully sent anomoly one";
    }


    @GetMapping("anomalytwo")
    public String anomolyTwo() {
        try {
            logService.sendAnomalyTwo();
        }
        catch (IOException | InterruptedException e) {
            e.printStackTrace();;
            return "Failed to send anomoly two";
        }
        return "Sucessfully sent anomoly two";
    }

    @GetMapping("anomalythree")
    public String anomolyThree() {
        try {
            logService.sendAnomalyThree();
        }
        catch (Exception e) {
            e.printStackTrace();;
            return "Failed to send anomoly Three";
        }
        return "Sucessfully sent anomoly Three";
    }

    @GetMapping("/healthcheck")
    public String healthCheck() {
        return "running";
    }

}
