package com.example.RTCEngine;

import StreamDataPacket.BaseClassDataType.TaskState;
import StreamTest.LoadcacheSerializer;
import com.google.common.cache.LoadingCache;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.JobID;
import org.apache.flink.api.common.functions.MapPartitionFunction;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeutils.base.StringSerializer;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.contrib.streaming.state.RocksDBStateBackend;
import org.apache.flink.queryablestate.client.QueryableStateClient;
import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.state.api.ExistingSavepoint;
import org.apache.flink.state.api.Savepoint;
import org.apache.flink.state.api.functions.KeyedStateReaderFunction;
import org.apache.flink.util.Collector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

//import com.google.common.cache.LoadingCache;


@RestController
@RequestMapping("/states")
public class states {
    @Autowired
    private LatencyTask latencyTask = new LatencyTask();
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
    @GetMapping
    public String getStates(@RequestParam("id") String tid) throws Exception {
        String resS = "err";
        String logStr = null;
        String pid = RtcEngineMemory.getTaskbyId(tid).project_id;
        String job_id = RtcEngineMemory.RtcProjectJobID.get(pid);
        try {
            Map<String, TaskState> resMap = new HashMap<String, TaskState>();
            JobID jobId = JobID.fromHexString(job_id);
            String[] hosts = RtcEngineMemory.state_url.split(",");
            for(String host : hosts){
                QueryableStateClient client = new QueryableStateClient(host, 9069);

                MapStateDescriptor<String, LoadingCache<String, TaskState>> stateDescriptor =
                        new MapStateDescriptor<>(
                                "allTaskState",
                                new StringSerializer(),
                                new LoadcacheSerializer<String, TaskState>()
                        );
                for(int i = 0; i < 100; i++){
                    String key = String.valueOf(i);
                    CompletableFuture<MapState<String, LoadingCache<String, TaskState>>> resultFuture =
                            client.getKvState(jobId, "allTaskStateName", key, BasicTypeInfo.STRING_TYPE_INFO, stateDescriptor);
                    MapState<String, LoadingCache<String, TaskState>> res = null;
                    try {
                        res = resultFuture.join();
                    } catch (Exception e) {
                    }
                    if(res == null){
                        break;
                    }
                    for(Map.Entry<String, TaskState> entry: res.get(tid).asMap().entrySet()){
                        resMap.put(entry.getKey(), entry.getValue());
                    }
                }
            }
            resS = "success: "+resMap.toString();

        }catch (Exception e) {
            resS = "err";
            logStr = StreamLog.createExLog(e, "ERROR", "状态读取失败",
                    RtcEngineMemory.engine_url+"/states", "id:"+tid+",jobid:"+job_id, "GET");
        }
        if(logStr == null){
            logStr = StreamLog.createExLog(null, "INFO", "状态读取成功",
                    RtcEngineMemory.engine_url+"/states", "id:"+tid+",jobid:"+job_id, "GET");
        }
        latencyTask.writeLog(Collections.singletonList(logStr));
        return resS;
    }

    public static String getStateFromCP(String savepath, String tid)throws Exception{
        ExecutionEnvironment bEnv = ExecutionEnvironment.getExecutionEnvironment();
        ExistingSavepoint savepoint = null;
        if(RtcEngineMemory.flink_save_path.startsWith("file")){
            savepoint = Savepoint.load(bEnv, savepath, new FsStateBackend(RtcEngineMemory.flink_save_path));
        }
        else {
            savepoint = Savepoint.load(bEnv, savepath, new RocksDBStateBackend(RtcEngineMemory.flink_save_path));
        }

        DataSet<Map<String, TaskState>> data = savepoint.readKeyedState("cal-id", new KeyedStateReaderFunction<String, Map<String, Map<String, TaskState>>>() {
            private transient MapState<String, LoadingCache<String, TaskState>> allTaskState;
            @Override
            public void open(Configuration configuration) throws Exception {
                ExecutionConfig config = getRuntimeContext().getExecutionConfig();
                MapStateDescriptor<String, LoadingCache<String, TaskState>> stateDescriptor =
                        new MapStateDescriptor<String, LoadingCache<String, TaskState>>(
                                "allTaskState",
                                new StringSerializer(),
                                new LoadcacheSerializer<String, TaskState>(config)
                        );
                stateDescriptor.setQueryable("allTaskStateName");
                allTaskState = getRuntimeContext().getMapState(stateDescriptor);
            }

            @Override
            public void readKey(String s, Context context, Collector<Map<String, Map<String, TaskState>>> collector) throws Exception {
                Map<String, Map<String, TaskState>> map = new HashMap<>();
                for(Map.Entry<String, LoadingCache<String, TaskState>> entry: allTaskState.entries()){
                    Map<String, TaskState> stateMap = new HashMap<>();
                    stateMap.putAll(entry.getValue().asMap());
                    map.put(entry.getKey(), stateMap);
                }
                collector.collect(map);
            }
        }).mapPartition(new MapPartitionFunction<Map<String, Map<String, TaskState>>, Map<String, TaskState>>() {
            @Override
            public void mapPartition(Iterable<Map<String, Map<String, TaskState>>> iterable, Collector<Map<String, TaskState>> collector) throws Exception {
                Map<String, TaskState> stateMap = new HashMap<>();
                for(Map<String, Map<String, TaskState>> mapMap: iterable){
                    Map<String, TaskState> taskStateMap = mapMap.get(tid);
                    stateMap.putAll(taskStateMap);
                }
                collector.collect(stateMap);
            }
        }).setParallelism(1);
        return data.collect().get(0).toString();
    }
}
