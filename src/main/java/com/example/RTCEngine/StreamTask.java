package com.example.RTCEngine;

import org.dom4j.Element;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class StreamTask {
    String id;
    String name;
    String project_id;
    String state_id;
    String state_public;
    String state_name;
    String state_descriptor;
    String state_type;
    String stateperiod;
    String condition;
    String broadcast;
    String log;
    String output_topic;
    String timeout;
    Integer TaskState = 0;

    String alarm;
    String algorithm;
    ArrayList<String> input_datasetlist = new ArrayList<>();
    String cal_device;
    String log_device;
    ArrayList<String> publicStatelist = new ArrayList<>();
   String inputTopicList;

    public void setTask(Element e){
        this.TaskState = 1;
        this.id = e.element("uuid").getTextTrim();
        this.name = e.element("name").getTextTrim();
        this.alarm = (e.element("alarm")==null?null:e.element("alarm").getTextTrim());
        this.project_id = e.element("projectid").getTextTrim();
        this.stateperiod = e.element("stateperiod").getTextTrim();
        this.condition = e.element("condition").getTextTrim();
        this.log = (e.element("log")==null?null:e.element("log").getTextTrim());
        //this.state_id = e.element("state").attributeValue("uuid");
        this.state_id = this.id;
        this.state_descriptor = e.element("state").attributeValue("descriptor");
        this.state_name = e.element("state").attributeValue("name");
        this.state_public = e.element("state").attributeValue("public");
        this.state_type = e.element("state").getTextTrim();
        this.broadcast = e.element("broadcast").getTextTrim();
        this.algorithm = e.element("algorithm").element("uuid").getTextTrim();
        this.output_topic = e.element("outputtopic").getTextTrim();
        this.timeout = e.element("timeout").getTextTrim();
        List<Element> devicelist = e.element("devices").elements("device");
        for(Element dl:devicelist){
            if("calc".equals(dl.attributeValue("type"))){
                this.cal_device = dl.getTextTrim();
            }else {
                this.log_device = dl.getTextTrim();
            }
        }

        List<Element> dss = e.element("datasets").elements("dataset");
        for(Element ds:dss){
            input_datasetlist.add(ds.element("uuid").getTextTrim());
        }

        List<Element> pss = e.element("publicstates").elements("publicstate");
        if(pss.size()>0){
            for(Element ps:pss) {
                if (ps != null) { publicStatelist.add(ps.getTextTrim()); }
            }
        }

        this.inputTopicList = e.element("inputtopic").getTextTrim();
    }

    public void setTask(ResultSet resultSet) throws Exception{
        this.id = resultSet.getString(1);
        this.name = resultSet.getString(2);
        this.project_id = resultSet.getString(3);
        this.state_id = resultSet.getString(4);
        this.state_public = resultSet.getString(5);
        this.state_name = resultSet.getString(6);
        this.state_descriptor = resultSet.getString(7);
        this.state_type = resultSet.getString(8);
        this.stateperiod = resultSet.getString(9);
        this.condition = resultSet.getString(10);
        this.broadcast = resultSet.getString(11);
        this.log = resultSet.getString(12);
        this.output_topic = resultSet.getString(13);
        this.TaskState = resultSet.getInt(14);
        this.alarm = resultSet.getString(15);
        this.algorithm = resultSet.getString(16);
        if(resultSet.getString(17).length() > 0){
            this.input_datasetlist = new ArrayList<String>(Arrays.asList(resultSet.getString(17).split(",")));
        }
        this.cal_device = resultSet.getString(18);
        this.log_device = resultSet.getString(19);
        if(resultSet.getString(20).length() > 0){
            this.publicStatelist = new ArrayList<String>(Arrays.asList(resultSet.getString(20).split(",")));
        }
        this.timeout = resultSet.getString(21);
        this.inputTopicList = resultSet.getString(22);

    }

    public void setCancel(){
        this.TaskState = 0;
    }

    public void setTest(){
        this.TaskState = -1;
    }

    public Integer getTaskState(){
        return this.TaskState;
    }

    @Override
    public String toString() {
        return "StreamTask{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", project_id='" + project_id + '\'' +
                ", state_id='" + state_id + '\'' +
                ", state_public='" + state_public + '\'' +
                ", state_name='" + state_name + '\'' +
                ", state_descriptor='" + state_descriptor + '\'' +
                ", state_type='" + state_type + '\'' +
                ", stateperiod='" + stateperiod + '\'' +
                ", condition='" + condition + '\'' +
                ", broadcast='" + broadcast + '\'' +
                ", log='" + log + '\'' +
                ", output_topic='" + output_topic + '\'' +
                ", timeout='" + timeout + '\'' +
                ", TaskState=" + TaskState +
                ", alarm='" + alarm + '\'' +
                ", algorithm='" + algorithm + '\'' +
                ", input_datasetlist=" + input_datasetlist +
                ", cal_device='" + cal_device + '\'' +
                ", log_device='" + log_device + '\'' +
                ", publicStatelist=" + publicStatelist +
                ", inputTopicList='" + inputTopicList + '\'' +
                '}';
    }
}
