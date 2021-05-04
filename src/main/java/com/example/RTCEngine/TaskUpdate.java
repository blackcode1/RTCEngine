package com.example.RTCEngine;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;


@RestController
@RequestMapping("/taskupdate")
public class TaskUpdate {
    @Autowired
    private LatencyTask latencyTask;
    @CrossOrigin
    @PostMapping
    public String update_task(@RequestParam("id") String id, @RequestBody String request_body)throws Exception{
        String res = "success";
        String logStr = null;
        try {
            JSONObject obj = JSON.parseObject(request_body);
            String new_device_list = obj.getString("device_list");
            StreamTask t = RtcEngineMemory.getTaskbyId(id);
            t.cal_device = new_device_list;
            latencyTask.updateTable(id, "task", "caldev", new_device_list);
        }catch (Exception e) {
            res = "err";
            logStr = StreamLog.createExLog(e, "ERROR", "任务修改失败",
                    RtcEngineMemory.engine_url+"/taskupdate", "id:"+id, "POST");
        }
        if(logStr == null){
            logStr = StreamLog.createExLog(null, "INFO", "任务修改成功",
                    RtcEngineMemory.engine_url+"/taskupdate", "id:"+id, "POST");
        }
        latencyTask.writeLog(Collections.singletonList(logStr));
        return res;
    }
}
