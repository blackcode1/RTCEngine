package com.example.RTCEngine;

import org.dom4j.Element;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class StreamProject {
    String id = null;
    String name = null;
    String data_id = null;
    String para = null;
    Boolean with_checkpoint = null;
    String checkpoint_pace = null;
    String status = null;
    ArrayList<String> InputDataSetList  = new ArrayList<>();
    String OutputDataSetId = null;

    public void setProject(Element e){
        //to get setup using e
        this.id = e.element("uuid").getTextTrim();
        this.name = e.element("name").getTextTrim();
        this.data_id = e.element("dataid").getTextTrim();
        this.with_checkpoint = ("True".equals(e.element("checkpoint").attributeValue("set"))?true:false);
        this.checkpoint_pace = e.element("checkpoint").getTextTrim();
        this.para = e.element("parallel").getTextTrim();
        List<Element> dss = e.element("datasets").elements("dataset");
        for(Element ds:dss){
            if("input".equals(ds.attributeValue("io"))){
                InputDataSetList.add(ds.element("uuid").getTextTrim());
            }else{
                OutputDataSetId = ds.element("uuid").getTextTrim();
            }

        }
    }

    public void setProject(ResultSet resultSet) throws Exception{
        this.id = resultSet.getString(1);
        this.name = resultSet.getString(2);
        this.data_id = resultSet.getString(3);
        this.para = resultSet.getString(4);
        this.with_checkpoint = resultSet.getBoolean(5);
        this.checkpoint_pace = resultSet.getString(6);
        this.status = resultSet.getString(7);
        if(resultSet.getString(8).length() > 0){
            this.InputDataSetList = new ArrayList<String>(Arrays.asList(resultSet.getString(8).split(",")));
        }

        this.OutputDataSetId = resultSet.getString(9);
    }
}


