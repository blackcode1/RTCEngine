package com.example.RTCEngine;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;


@RestController
@RequestMapping("/activatedproject")
public class ActivatedProjectController {
    @Autowired
    private LatencyTask latencyTask = new LatencyTask();
    @CrossOrigin
    @GetMapping
    public String ActivatedTasksSearch(@RequestParam("id") String pid)throws Exception{
        String logStr = null;
        String res = "err";
        try{
            JSONObject jsonObject =  GetJson.createProjectInfo(pid);
            jsonObject.put("savepath", RtcEngineMemory.RtcProjectSavepath.get(pid));
            jsonObject.put("jobID", RtcEngineMemory.RtcProjectJobID.get(pid));
            res =  jsonObject.toJSONString();
        } catch (Exception e) {
            logStr = StreamLog.createExLog(e, "ERROR", "项目信息查询失败",
                    RtcEngineMemory.engine_url+"/activatedproject", "id:"+pid, "GET");
        }
        if(logStr == null){
            logStr = StreamLog.createExLog(null, "INFO", "项目信息查询成功",
                    RtcEngineMemory.engine_url+"/activatedproject", "id:"+pid, "GET");
        }
        latencyTask.writeLog(Collections.singletonList(logStr));
        return res;
    }
}
