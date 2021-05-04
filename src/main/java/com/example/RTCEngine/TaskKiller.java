package com.example.RTCEngine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/task_kill")
public class TaskKiller {
    @Autowired
    private LatencyTask latencyTask;
    @CrossOrigin
    @PostMapping
    public String kill_task(@RequestParam("id") String id)throws Exception{
        String res = "sucess";
        String logStr = null;
        try {
            RtcEngineMemory.getTaskbyId(id).setCancel();
            latencyTask.updateTable(id, "task", "taskstate", "0");
        }catch (Exception e) {
            res = "err";
            logStr = StreamLog.createExLog(e, "ERROR", "任务终止失败",
                    RtcEngineMemory.engine_url+"/task_kill", "id:"+id, "POST");
        }
        if(logStr == null){
            logStr = StreamLog.createExLog(null, "INFO", "任务终止成功",
                    RtcEngineMemory.engine_url+"/task_kill", "id:"+id, "POST");
        }
        latencyTask.writeLog(Collections.singletonList(logStr));

        return res;
    }
}