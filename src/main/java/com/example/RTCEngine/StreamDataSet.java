package com.example.RTCEngine;

import org.dom4j.Element;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;

@Component
public class StreamDataSet implements Cloneable{
    String id;
    String name;
    String source_id;
    String data_id;
    String type;
    String topic;
    String offset;
    String filter;
    String group;
    String query;
    String exchange;
    String delay;
    String startt;
    String endt;


    public void setDataSet(Element e){
        this.id = e.element("uuid").getTextTrim();
        this.type = e.attributeValue("type");
        this.data_id = (e.element("dataid")==null?null:e.element("dataid").getTextTrim());
        this.topic = (e.element("topic")==null?null:e.element("topic").getTextTrim());
        this.offset = (e.element("offset")==null?null:e.element("offset").getTextTrim());
        this.group = (e.element("group")==null?null:e.element("group").getTextTrim());
        this.query = (e.element("query")==null?null:e.element("query").getTextTrim());
        this.source_id = e.element("datasource").element("uuid").getTextTrim();

        this.filter = (e.element("filter")==null?null:("True".equals(e.element("filter").attributeValue("set"))?e.element("filter").getTextTrim():null));

        this.exchange = (e.element("rmqexchange")==null?null:e.element("rmqexchange").getTextTrim());
        this.delay = (e.element("rmqdelay")==null?null: e.element("rmqdelay").getTextTrim());
        this.startt = (e.element("starttime")==null?null:e.element("starttime").getTextTrim());
        this.endt = (e.element("endtime")==null?null:e.element("endtime").getTextTrim());
    }

    public void setDataSet(ResultSet resultSet) throws Exception {
        this.id = resultSet.getString(1);
        this.name = resultSet.getString(2);
        this.source_id = resultSet.getString(3);
        this.data_id = resultSet.getString(4);
        this.type = resultSet.getString(5);
        this.topic = resultSet.getString(6);
        this.offset = resultSet.getString(7);
        this.filter = resultSet.getString(8);
        this.group = resultSet.getString(9);
        this.query = resultSet.getString(10);
        this.exchange = resultSet.getString(11);
        this.delay = resultSet.getString(12);
    }

    @Override
    public String toString() {
        return "StreamDataSet{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", source_id='" + source_id + '\'' +
                ", data_id='" + data_id + '\'' +
                ", type='" + type + '\'' +
                ", topic='" + topic + '\'' +
                ", offset='" + offset + '\'' +
                ", filter='" + filter + '\'' +
                ", group='" + group + '\'' +
                ", query='" + query + '\'' +
                '}';
    }
}
