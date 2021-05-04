package com.example.RTCEngine;

import org.dom4j.Element;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;

@Component
public class StreamDataSource implements Cloneable{
    String id;
    String name;
    String type;
    String ip;
    String port;
    String user;
    String password;
    String table;

    public void setDataSource(Element e){
        this.id = e.element("uuid").getTextTrim();
        this.type = e.element("type").getTextTrim();
        this.ip = e.element("host").getTextTrim();
        this.port = e.element("port").getTextTrim();
        this.user = e.element("username").getTextTrim();
        this.password = e.element("password").getTextTrim();
        this.table = (e.element("table")==null?null:e.element("table").getTextTrim());

    }
    public void setDataSource(ResultSet resultSet) throws Exception {
        this.id = resultSet.getString(1);
        this.name = resultSet.getString(2);
        this.type = resultSet.getString(3);
        this.ip = resultSet.getString(4);
        this.port = resultSet.getString(5);
        this.user = resultSet.getString(6);
        this.password = resultSet.getString(7);
        this.table = resultSet.getString(8);
    }
}
