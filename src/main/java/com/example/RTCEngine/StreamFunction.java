package com.example.RTCEngine;

import org.dom4j.Element;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;

@Component
public class StreamFunction {
    String uuid;
    String name;
    String classpath;
    String para;
    String input;
    String output;
    String type;
    String path;


    public void setFunction(Element e){
        this.uuid = e.element("uuid").getTextTrim();
        this.name = e.element("name").getTextTrim();
        this.classpath = e.element("classpath").getTextTrim();
        this.para = e.element("parameter").getTextTrim();
        this.input = e.element("input").getTextTrim();
        this.output = e.element("output").getTextTrim();
        this.type = e.element("type").getTextTrim();
        this.path = e.element("path").getTextTrim();
    }

    public void setFunction(ResultSet resultSet) throws Exception {
        this.uuid = resultSet.getString(1);
        this.name = resultSet.getString(2);
        this.classpath = resultSet.getString(3);
        this.para = resultSet.getString(4);
        this.input = resultSet.getString(5);
        this.output = resultSet.getString(6);
        this.type = resultSet.getString(7);
        this.path = resultSet.getString(8);
    }
}
