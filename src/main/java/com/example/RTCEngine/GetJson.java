package com.example.RTCEngine;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class GetJson {

    public static JSONObject createDataSetInfo(String id){
        JSONObject dataSetJson = new JSONObject();
        StreamDataSet ds = RtcEngineMemory.getDataSetbyId(id);
        StreamDataSource dso = RtcEngineMemory.getDataSourcebyId(ds.source_id);
        dataSetJson.put("DataSetID", id);
        dataSetJson.put("DataSetName", ds.name);
        dataSetJson.put("DataSetType",ds.type);
        dataSetJson.put("SourceID", ds.source_id);
        dataSetJson.put("DataType", ds.data_id);
        dataSetJson.put("DataSetTopic", ds.topic);
        dataSetJson.put("DataSetGroupID", ds.group);
        dataSetJson.put("DataSetOffset", ds.offset);
        dataSetJson.put("Query",ds.query.replace(' ', '@'));
        //dataSetJson.put("filter", "MtnAS_VclId");
        //inputDataset.put("filter", "");
        dataSetJson.put("DataSourceID", ds.source_id);
        dataSetJson.put("DataSourceName", dso.name);
        dataSetJson.put("DataSourceType", dso.type);
        dataSetJson.put("DataSourceIp", dso.ip);
        dataSetJson.put("DataSourcePort", dso.port);
        dataSetJson.put("DataSourceUser", dso.user);
        dataSetJson.put("DataSourcePassword", dso.password);
        dataSetJson.put("DataBaseName", dso.table);
        dataSetJson.put("Filter",ds.filter);

        dataSetJson.put("RMQExchange",ds.exchange);
        dataSetJson.put("RMQDelay",ds.delay);
        dataSetJson.put("StartTime",ds.startt);
        dataSetJson.put("EndTime",ds.endt);
        return dataSetJson;
    }
    private static JSONObject createDataSourceInfo(String id){
        JSONObject dataSourceJson = new JSONObject();
        StreamDataSource ds = RtcEngineMemory.getDataSourcebyId(id);
        dataSourceJson.put("DataSourceID", id);
        dataSourceJson.put("DataSourceName", ds.name);
        dataSourceJson.put("DataSourceType", ds.type);
        dataSourceJson.put("DataSourceIp", ds.ip);
        dataSourceJson.put("DataSourcePort", ds.port);
        dataSourceJson.put("DataSourceUser", ds.user);
        dataSourceJson.put("DataSourceType",ds.type);
        dataSourceJson.put("DataSourcePassword", ds.password);
        dataSourceJson.put("DatabaseName",ds.table);
        return dataSourceJson;
    }

    private static JSONObject createJarInfo(String id){
        JSONObject jarInfo = new JSONObject();
        StreamFunction f = RtcEngineMemory.getFunctionbyId(id);
        jarInfo.put("JarID", id);
        jarInfo.put("JarName", f.name);
        jarInfo.put("JarPath", f.path);
        jarInfo.put("JarClass", f.classpath);
        jarInfo.put("InputType", f.input);
        jarInfo.put("OutputType", f.output);
        return jarInfo;
    }

    private static JSONObject createPublicSateInfo(String id){
        JSONObject stateInfo = new JSONObject();
        StreamPublicState ps = RtcEngineMemory.getPublicStatebyId(id);
        if(ps == null){
            return null;
        }
        stateInfo.put("StateID", ps.id);
        stateInfo.put("StateName", ps.name);
        stateInfo.put("Desciriptor", ps.descriptor);
        stateInfo.put("TaskID", ps.task_id);
        return stateInfo;
    }
    private static JSONObject createTaskInfo(String id){
        JSONObject streamTask = new JSONObject();
        StreamTask ta = RtcEngineMemory.getTaskbyId(id);
        streamTask.put("TaskID", id);
        streamTask.put("JarID", ta.algorithm);
        streamTask.put("ProjectID", ta.project_id);
        streamTask.put("InputDataSetList", String.join(",", ta.input_datasetlist));
        //streamTask.put("OutputDataSet", ta.output_datasetlist.toString());
        streamTask.put("PublicStateIDList", String.join(",", ta.publicStatelist));
        streamTask.put("StateType", ta.state_type);
        streamTask.put("StatePeriod", ta.stateperiod);
        streamTask.put("Broadcast",ta.broadcast);
        streamTask.put("Condition", ta.condition);
        streamTask.put("TaskState",ta.getTaskState());
        streamTask.put("DeviceList", ta.cal_device);
        streamTask.put("outputTopic", ta.output_topic);
        streamTask.put("timeout", ta.timeout);
        streamTask.put("inputTopicList", ta.inputTopicList);
        return streamTask;
    }

    public static  JSONObject createACtivatedTaskList(String pid){
        JSONObject taskInfo = new JSONObject();
        JSONArray taskList = new JSONArray();
        JSONArray stateInfoList = new JSONArray();
        JSONArray jarInfoList = new JSONArray();
        JSONArray inputDatasetList = new JSONArray();
        //JSONArray outputDatasetList = new JSONArray();
        JSONArray DatasourceList = new JSONArray();

        ArrayList<StreamTask> tl = RtcEngineMemory.getTaskListbyPid(pid);
        Set<String> state_set = new HashSet<>();
        for(StreamTask t:tl){
            state_set.add(t.state_id);
        }
        while (state_set.size() != 0){
            for(StreamTask t:tl){
                int rely_num = 0;
                for(String psid:t.publicStatelist){
                    if(state_set.contains(psid)){
                        rely_num = 1;
                        break;
                    }
                }
                if(rely_num == 0 && state_set.contains(t.state_id) && t.TaskState != 0){
                    state_set.remove(t.state_id);
                    taskList.add(createTaskInfo(t.id));
                    jarInfoList.add(createJarInfo(t.algorithm));
                    for(String ids:t.input_datasetlist){
                        inputDatasetList.add(createDataSetInfo(ids));
                    }
                    for(String psid:t.publicStatelist){
                        JSONObject stateInfo = createPublicSateInfo(psid);
                        if(stateInfo != null){
                            stateInfoList.add(createPublicSateInfo(psid));
                        }
                    }
                }
            }
        }

        taskInfo.put("TaskList", taskList);
        taskInfo.put("StateInfoList", stateInfoList);
        taskInfo.put("JarInfoList", jarInfoList);
        taskInfo.put("InputDataSetList", inputDatasetList);

        return taskInfo;
    }

    public static JSONObject createProjectInfo(String pid){
        JSONObject projectJson = new JSONObject();
        JSONArray streamDatasetList = new JSONArray();
        //JSONArray streamDatasourceList = new JSONArray();
        StreamProject pr = RtcEngineMemory.getProjectbyId(pid);

        projectJson.put("StreamProjectID", pid);
        projectJson.put("StreamProjectName", pr.name);
        projectJson.put("IsCheckpoint", pr.with_checkpoint);
        projectJson.put("CheckpointTime", pr.checkpoint_pace);
        projectJson.put("Para", pr.para);
        projectJson.put("status", pr.status);
        projectJson.put("StreamDataType", pr.data_id);
        //projectJson.put("StreamDataType", "TransPacket");

        for(String dsid:pr.InputDataSetList){
            streamDatasetList.add(GetJson.createDataSetInfo(dsid));
        }

        //streamDatasetList.add(GetJson.createDataSetInfo(pr.OutputDataSetId));
        projectJson.put("OutputDataSet",GetJson.createDataSetInfo(pr.OutputDataSetId));
        //projectJson.put("StreamDatasourceList", streamDatasourceList);
        projectJson.put("StreamDataSetList", streamDatasetList);
        return projectJson;
    }


}
