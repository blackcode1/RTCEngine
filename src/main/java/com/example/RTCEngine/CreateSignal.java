package com.example.RTCEngine;

import com.alibaba.fastjson.JSONObject;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@RestController
@RequestMapping("/create_signal")
public class CreateSignal {
    @Autowired
    private LatencyTask latencyTask;
    @CrossOrigin
    @PostMapping
    public String kill_task(@RequestParam("filepath") String filepath,@RequestParam(value = "topic", defaultValue = "", required = false) String topic)throws Exception{
        String res = "sucess";
        String logStr = null;
        try {
            JSONObject signal = new JSONObject();
            signal.put("deviceID", "0");
            signal.put("timeStamp", System.currentTimeMillis());
            Map<String, Map<String, String>> wsmap = new HashMap<>();
            signal.put("workStatusMap", wsmap);
            signal.put("signal", true);
            signal.put("filePath", filepath);
            StreamDataSet dataSet = RtcEngineMemory.getDataSetbyId(RtcEngineMemory.signal_dataset_id);
            StreamDataSource dataSource = RtcEngineMemory.getDataSourcebyId(dataSet.source_id);
            String brokerList = dataSource.ip+":"+dataSource.port;
            if(topic.length() < 1){
                topic = dataSet.topic;
            }
            Properties kafkaProps = new Properties();
            kafkaProps.put("bootstrap.servers", brokerList);
            kafkaProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            kafkaProps.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            KafkaProducer<String, String> producer = new KafkaProducer<String, String>(kafkaProps);
            ProducerRecord<String, String> record = new ProducerRecord<String, String>(topic, null, signal.toString());//Topic Key Value
            producer.send(record);
        }catch (Exception e) {
            res = "err";
            logStr = StreamLog.createExLog(e, "ERROR", "信令发送失败",
                    RtcEngineMemory.engine_url+"/create_signal", "topic:"+topic, "POST");
        }
        if(logStr == null){
            logStr = StreamLog.createExLog(null, "INFO", "信令发送成功",
                    RtcEngineMemory.engine_url+"/create_signal", "topic:"+topic, "POST");
        }
        latencyTask.writeLog(Collections.singletonList(logStr));

        return res;
    }
}