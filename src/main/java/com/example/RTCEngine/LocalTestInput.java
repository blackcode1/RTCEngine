package com.example.RTCEngine;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;


@RestController
@RequestMapping("/localtestinput")
public class LocalTestInput {
    @Autowired
    private LatencyTask latencyTask = new LatencyTask();

    @CrossOrigin
    @GetMapping
    public String LocalTestInputSourceGet(@RequestParam("id") String task_id)throws Exception{
        String resS = null;
        String logStr = null;
        try {
            JSONObject res = new JSONObject();
            res.put("inputSource", GetJson.createDataSetInfo(RtcEngineMemory.localtest_dataset_id));
            StreamTask task = RtcEngineMemory.getTaskbyId(task_id);
            if (task != null && RtcEngineMemory.getProjectbyId(task.project_id) != null) {
                String outputSourceID = RtcEngineMemory.getProjectbyId(task.project_id).OutputDataSetId;
                res.put("outputSource", GetJson.createDataSetInfo(outputSourceID));
                resS =  res.toJSONString();
            }
        }catch (Exception e) {
            logStr = StreamLog.createExLog(e, "ERROR", "获取本地测试数据集信息失败",
                    RtcEngineMemory.engine_url+"/localtestinput", "id:"+task_id, "GET");
        }
        if(logStr == null){
            logStr = StreamLog.createExLog(null, "INFO", "获取本地测试数据集信息成功",
                    RtcEngineMemory.engine_url+"/localtestinput", "id:"+task_id, "GET");
        }
        latencyTask.writeLog(Collections.singletonList(logStr));
        return resS;

    }
}
