package com.example.RTCEngine;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;

@RestController
@RequestMapping("/project_kill")
public class ProjectKiller {
    @Autowired
    private LatencyTask latencyTask;
    //发送GET请求
    public static String sendGet(String url) throws Exception{
        String result = "";
        BufferedReader in = null;
        try {
            String urlName = url;
            URL realUrl = new URL(urlName);
            URLConnection conn = realUrl.openConnection();// 打开和URL之间的连接
            conn.setRequestProperty("accept", "*/*");// 设置通用的请求属性
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
            conn.setConnectTimeout(4000);
            conn.connect();// 建立实际的连接
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));// 定义BufferedReader输入流来读取URL的响应
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
        } finally {// 使用finally块来关闭输入流
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                System.out.println("关闭流异常");
            }
        }
        return result;
    }

    @CrossOrigin
    @PostMapping
    public String kill_project(@RequestParam("id") String id)throws Exception{
        String res = "err";
        String logStr = null;
        try {
            String job_id = RtcEngineMemory.RtcProjectJobID.get(id);

            String commandStr = RtcEngineMemory.flink_path + "/bin/flink cancel -s ";
            String exec_com = commandStr + job_id;
            Process pro = Runtime.getRuntime().exec(exec_com);

            latencyTask.getProjectSavePath(id, 2000);
            latencyTask.getProjectJobID(id, 2000);
            for(int i = 0;i<RtcEngineMemory.RtcEngineProjectList.size();i++){
                if(id.equals(RtcEngineMemory.RtcEngineProjectList.get(i).id)){
                    RtcEngineMemory.RtcEngineProjectList.remove(i);
                    latencyTask.deleteFromTable(id, "project");
                    res = "success";
                    break;
                }
            }
        }catch (Exception e) {
            logStr = StreamLog.createExLog(e, "ERROR", "项目终止失败",
                    RtcEngineMemory.engine_url+"/project_kill", "id:"+id, "POST");
        }
        if(logStr == null){
            logStr = StreamLog.createExLog(null, "INFO", "项目终止成功",
                    RtcEngineMemory.engine_url+"/project_kill", "id:"+id, "POST");
        }
        latencyTask.writeLog(Collections.singletonList(logStr));
        return res;

    }
}
