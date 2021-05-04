package com.example.RTCEngine;

import org.dom4j.Element;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;


@Component
public class StreamPublicState {
    String id;
    String name;
    String task_id;
    String descriptor;

    public void setPublicState(Element e){
        this.task_id = e.element("uuid").getTextTrim();
        this.id = e.element("state").attributeValue("uuid");
        this.descriptor = e.element("state").attributeValue("descriptor");
        this.name = e.element("state").attributeValue("name");
    }

    public void setPublicState(ResultSet resultSet) throws Exception{
        this.id = resultSet.getString(1);
        this.name = resultSet.getString(2);
        this.task_id = resultSet.getString(3);
        this.descriptor = resultSet.getString(4);
    }
}
