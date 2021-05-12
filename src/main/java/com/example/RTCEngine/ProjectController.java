package com.example.RTCEngine;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.example.RTCEngine.ProjectKiller.sendGet;

@RestController
@RequestMapping("/project")
public class ProjectController {
    @Autowired
    private LatencyTask latencyTask = new LatencyTask();
    @CrossOrigin
    @PostMapping
    public String init_project(@RequestParam("id") String id,
                               @RequestParam(required = false, defaultValue = "10000", value = "wait") Integer waittime) throws Exception {
        StreamProject newProject = new StreamProject();

        SAXReader saxReader = new SAXReader();
        String res = "err";
        String logStr = null;

        try {
            Document document = saxReader.read(new File(RtcEngineMemory.xml_root_path + id + ".xml"));
            Element rootElement = document.getRootElement();
            Element p = rootElement.element("project");
            String pid = p.element("uuid").getTextTrim();
            String projectType = p.element("type").getTextTrim(); ;
            newProject.setProject(p);
            for(StreamProject streamProject: RtcEngineMemory.RtcEngineProjectList){
                if(streamProject.id.equals(newProject.id)){
                    return "duplicate";
                }
            }

            RtcEngineMemory.RtcEngineProjectList.add(newProject);
            latencyTask.insertProject(newProject);

            //to get datasets
            List<Element> dss = p.element("datasets").elements("dataset");

            for (Element ds : dss) {
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
            res = execProject(pid, waittime, projectType);

        } catch (Exception e) {
            logStr = StreamLog.createExLog(e, "ERROR", "项目启动失败",
                    RtcEngineMemory.engine_url+"/project", "id:"+id, "POST");
        }
        if(logStr == null){
            logStr = StreamLog.createExLog(null, "INFO", "项目启动成功",
                    RtcEngineMemory.engine_url+"/project", "id:"+id, "POST");
        }
        latencyTask.writeLog(Collections.singletonList(logStr));
        return res;
    }

    public String execProject(String pid, Integer waittime, String projectType) throws Exception {
        String cp_path = "";
        if(RtcEngineMemory.RtcProjectJobID.containsKey(pid)){
            String sps = sendGet(RtcEngineMemory.flink_url+"/jobs/"+RtcEngineMemory.RtcProjectJobID.get(pid)+"/checkpoints");
            if(sps != null && sps.length() != 0){
                JSONObject savepaths = JSON.parseObject(sps);
                JSONObject savepoint = savepaths.getJSONObject("latest").getJSONObject("savepoint");
                if(savepoint != null){
                    cp_path = savepoint.getString("external_path");
                }
            }
            else if(RtcEngineMemory.RtcProjectSavepath.containsKey(pid)){
                cp_path = RtcEngineMemory.RtcProjectSavepath.get(pid);
            }
        }

        String commandStr = RtcEngineMemory.flink_path + "/bin/flink run ";
        //if(cp_path.length() != 0){
        //    commandStr = commandStr + "-s " + cp_path + " ";
        //}
        if(projectType.equals("stream"))
            commandStr = commandStr + RtcEngineMemory.stream_flink_jar_path + " ";
        else
            commandStr = commandStr + RtcEngineMemory.batch_flink_jar_path + " ";
        String args = GetJson.createProjectInfo(pid).toJSONString();

        String exec_com = commandStr + args;
        System.out.println(exec_com);

        Process p = Runtime.getRuntime().exec(exec_com);
        InputStream is = p.getInputStream();
        Thread.sleep(waittime);
        byte[] b = new byte[is.available()];
        is.read(b);
        String res = new String(b);
        is.close();
        latencyTask.getProjectJobID(pid, 20000);

        return res;
    }

}

