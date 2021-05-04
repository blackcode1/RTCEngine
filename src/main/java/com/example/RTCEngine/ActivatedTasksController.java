package com.example.RTCEngine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;


@RestController
@RequestMapping("/activatedtasks")
public class ActivatedTasksController {
    @Autowired
    private LatencyTask latencyTask = new LatencyTask();
    @CrossOrigin
    @GetMapping
    public String ActivatedTasksSearch(@RequestParam("id") String pid)throws Exception{
        String logStr = null;
        String res = null;
        try{
            res =  GetJson.createACtivatedTaskList(pid).toJSONString();
        } catch (Exception e) {
            logStr = StreamLog.createExLog(e, "ERROR", "活跃任务查询失败",
                    RtcEngineMemory.engine_url+"/activatedtasks", "id:"+pid, "GET");
        }
        if(logStr == null){
            logStr = StreamLog.createExLog(null, "INFO", "活跃任务查询成功",
                    RtcEngineMemory.engine_url+"/activatedtasks", "id:"+pid, "GET");
        }
        latencyTask.writeLog(Collections.singletonList(logStr));
        return res;
    }
}
