package com.example.RTCEngine;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.lang.reflect.Array;
import java.util.*;


@RestController
@RequestMapping("/task")
public class TaskController {
    @Autowired
    private LatencyTask latencyTask;
    private Boolean error_rely(StreamTask task, ArrayList<StreamTask> all_task, ArrayList<StreamTask> rely_task)throws Exception{
        Set<String> rely_state = new HashSet<>();
        for(StreamTask t: rely_task){
            if(task.publicStatelist.contains(t.state_id)){
                return true;
            }
            rely_state.add(t.state_id);
            if(all_task.contains(t)){
                all_task.remove(t);
            }
        }
        ArrayList<StreamTask> new_rely_task = new ArrayList<>();
        for(StreamTask t: all_task){
            for(String s: t.publicStatelist){
                if(rely_state.contains(s)){
                    new_rely_task.add(t);
                    break;
                }
            }
        }
        if(new_rely_task.size() == 0){
            return false;
        }
        else {
            return error_rely(task, all_task, new_rely_task);
        }
    }

    @CrossOrigin
    @PostMapping
    public String init_task(@RequestParam("id") String id, @RequestParam(value = "test", defaultValue = "false", required = false) Boolean test)throws Exception{

        StreamTask newTask = new StreamTask();
        StreamFunction newFunction = new StreamFunction();

        SAXReader saxReader = new SAXReader();

        String return_value = "err";
        String logStr = null;
        try{
            Document document = saxReader.read(new File(RtcEngineMemory.xml_root_path + id + ".xml"));
            Element rootElement = document.getRootElement();
            Element t = rootElement.element("job");

            newTask.setTask(t);
            if(test){
                newTask.setTest();
            }
            if(RtcEngineMemory.duplicated(t.element("uuid").getTextTrim())){
                return_value = "duplicate";
            }
            else if(error_rely(newTask, RtcEngineMemory.getTaskListbyPid(newTask.project_id), new ArrayList<StreamTask>(Collections.singleton(newTask)))){
                return_value = "error publicstates rely";
            }
            else{
                RtcEngineMemory.RtcEngineTaskList.add(newTask);
                latencyTask.insertTask(newTask);

                //to get datasets
                List<Element> dss = t.element("datasets").elements("dataset");
                for(Element ds:dss){
                    StreamDataSet nds = new StreamDataSet();
                    nds.setDataSet(ds);
                    RtcEngineMemory.addDataset(nds);
                    latencyTask.insertDataset(nds);

                    //to get datasources
                    StreamDataSource ndso = new StreamDataSource();
                    ndso.setDataSource(ds.element("datasource"));
                    RtcEngineMemory.addDatasource(ndso);
                    latencyTask.insertDatasource(ndso);
                }

                //to get functions
                newFunction.setFunction(t.element("algorithm"));
                RtcEngineMemory.addFunc(newFunction);
                latencyTask.insertFunc(newFunction);

                if("True".equals(t.element("state").attributeValue("public"))){
                    StreamPublicState newps = new StreamPublicState();
                    newps.setPublicState(t);
                    RtcEngineMemory.addPublicstate(newps);
                    latencyTask.insertState(newps);
                }

                return_value = "sucess";
            }

        }catch (Exception e) {
            logStr = StreamLog.createExLog(e, "ERROR", "任务启动失败",
                    RtcEngineMemory.engine_url+"/task", "id:"+id, "GET");
        }
        if(logStr == null){
            logStr = StreamLog.createExLog(null, "INFO", "任务启动成功",
                    RtcEngineMemory.engine_url+"/task", "id:"+id, "GET");
        }
        latencyTask.writeLog(Collections.singletonList(logStr));

        return return_value;
    }
}