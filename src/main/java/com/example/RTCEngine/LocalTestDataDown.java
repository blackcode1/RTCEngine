package com.example.RTCEngine;

import StreamDataPacket.BaseClassDataType.TaskState;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;


@RestController
@RequestMapping("/localtestdatadown")
public class LocalTestDataDown {
    @Autowired
    private LatencyTask latencyTask = new LatencyTask();
    @CrossOrigin
    @GetMapping
    public String LocalTestDataDownload(@RequestParam("id") String task_id)throws Exception{
        String res = null;
        String logStr = null;
        try {
            StreamTask task = RtcEngineMemory.getTaskbyId(task_id);
            if(task != null && task.getTaskState() == -1){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("state", RtcEngineMemory.RtcTaskState.get(task_id));
                jsonObject.put("init", RtcEngineMemory.RtcTaskInit.get(task_id));
                res = jsonObject.toJSONString();
            }
        } catch (Exception e) {
            logStr = StreamLog.createExLog(e, "ERROR", "本地测试任务数据下载失败",
                    RtcEngineMemory.engine_url+"/localtestdatadown", "id:"+task_id, "GET");
        }
        if(logStr == null){
            logStr = StreamLog.createExLog(null, "INFO", "本地测试任务数据下载成功",
                    RtcEngineMemory.engine_url+"/localtestdatadown", "id:"+task_id, "GET");
        }
        latencyTask.writeLog(Collections.singletonList(logStr));

        return res;
    }
}
