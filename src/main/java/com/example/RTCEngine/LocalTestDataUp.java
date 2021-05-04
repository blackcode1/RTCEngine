package com.example.RTCEngine;

import StreamDataPacket.BaseClassDataType.TaskState;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;


@RestController
@RequestMapping("/localtestdataup")
public class LocalTestDataUp {
    @Autowired
    private LatencyTask latencyTask = new LatencyTask();

    @CrossOrigin
    @GetMapping
    public String LocalTestDataUpload(@RequestParam("id") String task_id, @RequestParam("data") String task_data)throws Exception{
        String res = null;
        String logStr = null;
        try {
            StreamTask task = RtcEngineMemory.getTaskbyId(task_id);
            if (task != null && task.getTaskState() == -1) {
                JSONObject jsonObject = JSONObject.parseObject(task_data);
                Map<String, TaskState> state = (Map<String, TaskState>) jsonObject.get("state");
                Map<String, Boolean> init = (Map<String, Boolean>) jsonObject.get("init");
                RtcEngineMemory.RtcTaskState.put(task_id, state);
                RtcEngineMemory.RtcTaskInit.put(task_id, init);
                res =  "sucess";
            } else {
                res = "not test task";
            }
        } catch (Exception e) {
            logStr = StreamLog.createExLog(e, "ERROR", "本地测试任务数据上传失败",
                    RtcEngineMemory.engine_url+"/localtestdataup", "id:"+task_id+",data:"+task_data, "GET");
        }
        if(logStr == null){
            logStr = StreamLog.createExLog(null, "INFO", "本地测试任务数据上传成功",
                    RtcEngineMemory.engine_url+"/localtestdataup", "id:"+task_id+",data:"+task_data, "GET");
        }
        latencyTask.writeLog(Collections.singletonList(logStr));
        return res;
    }
}
