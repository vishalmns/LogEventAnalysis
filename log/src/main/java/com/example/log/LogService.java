package com.example.log;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;


@Service
public class LogService {

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    Date date;

    public void sendLog() throws IOException {

        InputStream logs = getResourceFileAsInputStream("logs");
        String oneLine;

        try (BufferedReader bf = new BufferedReader(new InputStreamReader(logs))) {

            while ((oneLine = bf.readLine()) != null) {
                kafkaTemplate.send("test_topic", oneLine);
            }
        }


    }
    public InputStream getResourceFileAsInputStream(String fileName) {
        ClassLoader classLoader = LogService.class.getClassLoader();
        return classLoader.getResourceAsStream(fileName);
    }

    public void sendAnomalyOne() throws IOException {

        InputStream logs = getResourceFileAsInputStream("logs");
        String oneLine;
        int count = 0;
        try (BufferedReader bf = new BufferedReader(new InputStreamReader(logs))) {

            while ((oneLine = bf.readLine()) != null) {
                String[] split = oneLine.split("\\|");
                Log logObject = null;
                if(count > 500 && count <= 540) {
                    logObject = createLogObject(split, 400);
                }
                else {
                    logObject = createLogObject(split,200);
                }
                count++;
                System.out.println(count);
                kafkaTemplate.send("test_topic", toJsonString(logObject));
            }
        }
    }
    private Log createLogObject(String[] split, int statusCode) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd-HH:mm:ss:SSS");
        Date date = null;
        try {
           date  = simpleDateFormat.parse(split[0]);
        } catch (ParseException e) {
            System.out.println("unable to parse time");
        }
        return new Log("service_one", date, split[1], statusCode);
    }



    public void sendAnomalyTwo() throws IOException, InterruptedException {
        date = new Date();
        date.setMinutes(date.getMinutes() - 30);
        List<Integer> integers = generateOneLakhSortedNumberBtw1To1200();

        InputStream logs = getResourceFileAsInputStream("logs");
        String oneLine;
        BufferedReader bf;
        bf = new BufferedReader(new InputStreamReader(logs));
        Log logObject = null;
            for (int sec : integers) {
                oneLine = bf.readLine();
                if (oneLine == null) {
                    logs.close();
                    logs = getResourceFileAsInputStream("logs");
                    bf = new BufferedReader(new InputStreamReader(logs));
                }
                else {
                    String[] split = oneLine.split("\\|");
                    logObject = createLogObjectWithTimeStamp(split, sec, "service_two");
                    kafkaTemplate.send("test_topic", toJsonString(logObject));
                }

            }
    }

    public void sendAnomalyThree() throws IOException {
        List<Integer> integers = new ArrayList<>();
        integers = generateXNumberBtweenAtoB(10000, 0, 600);
        generateDataAndSend(integers, "file_one");
        integers = generateXNumberBtweenAtoB(4000,720,900);
        generateDataAndSend(integers, "file_two");
        integers = generateXNumberBtweenAtoB(3000, 1080, 1200);
        generateDataAndSend(integers, "file_three");
    }

    private void generateDataAndSend(List<Integer> integers, String fileName) throws IOException {
        FileWriter outputFile = new FileWriter(fileName);
        boolean x = true;
        InputStream logs = getResourceFileAsInputStream("logs");
        String oneLine;
        int count = 1;
        BufferedReader bf;
        Log logObject = null;
        bf = new BufferedReader(new InputStreamReader(logs));
            for (int sec : integers) {
                oneLine = bf.readLine();
                if (oneLine == null) {
                    logs.close();
                    logs = getResourceFileAsInputStream("logs");
                    bf = new BufferedReader(new InputStreamReader(logs));
                }
                else {
                    String[] split = oneLine.split("\\|");
                    logObject = createLogObjectWithTimeStamp(split, sec, "service_three");
                    if(x) {
                        System.out.println(logObject.timestamp);
                        x = false;
                    }
                    ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send("test_topic", toJsonString(logObject));
                    try {
                        SendResult<String, String> sentData = future.get();
                        outputFile.write(sentData.getProducerRecord().value());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }

            }
        System.out.println("last message sent in that batch" + logObject.timestamp);
            outputFile.close();
    }

    private List<Integer> generateXNumberBtweenAtoB(int number, int start, int end) {
        Random rn = new Random();
        List<Integer> li = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            li.add(rn.nextInt(end - start) + start);
        }
        Collections.sort(li);
        return li;
    }

    private List<Integer> generateOneLakhSortedNumberBtw1To1200() {
        Random rn = new Random();
        List<Integer> li = new ArrayList<>();
        for(int i=0; i<100000; i++) {
            li.add(rn.nextInt(1200));
        }
      Collections.sort(li);
        return li;
    }

    private Log createLogObjectWithTimeStamp(String[] split, int addSeconds, String serviceName) {
        Date date = new Date();
        date.setMinutes(date.getMinutes() - 30);
        date.setSeconds(date.getSeconds() + addSeconds);

        return new Log(serviceName, date, split[1], 200);
    }

    public String toJsonString(Log log)  {
        ObjectMapper obj = new ObjectMapper();
        obj.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        try {
            return obj.writeValueAsString(log);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
