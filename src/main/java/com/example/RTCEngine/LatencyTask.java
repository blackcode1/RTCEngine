package com.example.RTCEngine;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Future;

import static com.example.RTCEngine.ProjectKiller.sendGet;

@Component
public class LatencyTask {

    @Async
    public void getProjectJobID(String id, long sleepTime)throws Exception{
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String flink_url = RtcEngineMemory.flink_url+"/jobs/overview";
        String ovs = sendGet(flink_url);
        JSONObject overviews = JSON.parseObject(ovs);
        JSONArray arrays = overviews.getJSONArray("jobs");

        String job_id = null;
        for(int i =0;i<arrays.size();i++){
            JSONObject j = arrays.getJSONObject(i);
            if(id.equals(j.getString("name")) && "RUNNING".equals(j.getString("state"))){
                job_id = j.getString("jid");
                RtcEngineMemory.RtcProjectJobID.put(id, job_id);
                insertJobid(id, job_id);
            }
        }
        for(int i =0;i<arrays.size();i++){
            JSONObject j = arrays.getJSONObject(i);
            String jid = RtcEngineMemory.RtcProjectJobID.get(id);
            if(jid.equals(j.getString("jid"))){
                RtcEngineMemory.getProjectbyId(id).status = j.getString("state");
            }
        }
    }

    @Async
    public void getProjectStatus(String id, long sleepTime)throws Exception{
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String flink_url = RtcEngineMemory.flink_url+"/jobs/overview";
        String ovs = sendGet(flink_url);
        JSONObject overviews = JSON.parseObject(ovs);
        JSONArray arrays = overviews.getJSONArray("jobs");

        for(int i =0;i<arrays.size();i++){
            JSONObject j = arrays.getJSONObject(i);
            String jid = RtcEngineMemory.RtcProjectJobID.get(id);
            if(jid.equals(j.getString("jid"))){
                RtcEngineMemory.getProjectbyId(id).status = j.getString("state");
            }
        }
    }

    @Async
    public void getProjectSavePath(String id, long sleepTime)throws Exception{
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String cp_path = null;
        String sps = sendGet(RtcEngineMemory.flink_url+"/jobs/"+RtcEngineMemory.RtcProjectJobID.get(id)+"/checkpoints");
        if(sps != null && sps.length() != 0){
            JSONObject savepaths = JSON.parseObject(sps);
            JSONObject savepoint = savepaths.getJSONObject("latest").getJSONObject("savepoint");
            JSONObject checkp = savepaths.getJSONObject("latest").getJSONObject("completed");
            if(savepoint != null && !savepoint.getBoolean("discarded")){
                cp_path = savepoint.getString("external_path");
            }
            else if(checkp != null && !checkp.getBoolean("discarded")){
                cp_path = checkp.getString("external_path");
            }
        }
        if(cp_path != null && cp_path.length() > 1){
            RtcEngineMemory.RtcProjectSavepath.put(id, cp_path);
            insertSavepath(id, cp_path);
        }
    }


    @Async
    public void deleteFromTable(String id, String table){
        try {
            String Drivde = "org.sqlite.JDBC";
            Class.forName(Drivde);// 加载驱动,连接sqlite的jdbc
            Connection connection = DriverManager.getConnection("jdbc:sqlite:"+RtcEngineMemory.engine_save_path);
            Statement statement = connection.createStatement();   //创建连接对象，是Java的一个操作数据库的重要接口
            String sql = "delete from "+ table+" where id = '"+id+"'";
            statement.executeUpdate(sql);//向数据库中插入数据

            connection.close();//关闭数据库连接
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Async
    public void updateTable(String id, String table, String key, String value){
        try {
            String Drivde = "org.sqlite.JDBC";
            Class.forName(Drivde);// 加载驱动,连接sqlite的jdbc
            Connection connection = DriverManager.getConnection("jdbc:sqlite:"+RtcEngineMemory.engine_save_path);
            Statement statement = connection.createStatement();   //创建连接对象，是Java的一个操作数据库的重要接口
            String sql = "update "+ table+" set "+key+" = '"+value+"' where id = '"+id+"'";
            statement.executeUpdate(sql);//向数据库中插入数据
            connection.close();//关闭数据库连接
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Async
    public void insertProject(StreamProject project){
        try {
            String Drivde = "org.sqlite.JDBC";
            Class.forName(Drivde);// 加载驱动,连接sqlite的jdbc
            Connection connection = DriverManager.getConnection("jdbc:sqlite:"+RtcEngineMemory.engine_save_path);
            Statement statement = connection.createStatement();   //创建连接对象，是Java的一个操作数据库的重要接口
            String sql = "replace into project values('"+project.id+"','"+project.name+"','"+project.data_id+"','"+project.para
                    +"','"+project.with_checkpoint+"','"+project.checkpoint_pace+"','"+project.status+"','"+
                    String.join(",", project.InputDataSetList)+"','"+project.OutputDataSetId+"')";
            statement.executeUpdate(sql);//向数据库中插入数据

            connection.close();//关闭数据库连接
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Async
    public void insertTask(StreamTask task){
        try {
            String Drivde = "org.sqlite.JDBC";
            Class.forName(Drivde);// 加载驱动,连接sqlite的jdbc
            Connection connection = DriverManager.getConnection("jdbc:sqlite:"+RtcEngineMemory.engine_save_path);
            Statement statement = connection.createStatement();   //创建连接对象，是Java的一个操作数据库的重要接口

            String sql = "replace into task values('"+task.id+"','"+task.name+"','"+task.project_id+"','"+task.state_id
                    +"','"+task.state_public+"','"+task.state_name+"','"+task.state_descriptor+"','"+task.state_type+"','"+
                    task.stateperiod+"','"+task.condition+"','"+task.broadcast+"','"+task.log+"','"+task.output_topic+"','"+
                    task.getTaskState()+"','"+task.alarm+"','"+task.algorithm+"','"+ String.join(",", task.input_datasetlist)
                    +"','"+task.cal_device+"','"+task.log_device+"','"+String.join(",", task.publicStatelist)
                    +"','"+task.timeout+"','"+task.inputTopicList+"')";
            statement.executeUpdate(sql);//向数据库中插入数据

            connection.close();//关闭数据库连接
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Async
    public void insertDataset(StreamDataSet set){
        try {
            String Drivde = "org.sqlite.JDBC";
            Class.forName(Drivde);// 加载驱动,连接sqlite的jdbc
            Connection connection = DriverManager.getConnection("jdbc:sqlite:"+RtcEngineMemory.engine_save_path);
            Statement statement = connection.createStatement();   //创建连接对象，是Java的一个操作数据库的重要接口
            String newQuery = "";
            if(set.query != null){
                newQuery = set.query.replace("'", "''");
            }
            String sql = "replace into dataset values('"+set.id+"','"+set.name+"','"+set.source_id+"','"+set.data_id
                    +"','"+set.type+"','"+set.topic+"','"+set.offset+"','"+set.filter+"','"+
                    set.group+"','"+newQuery+"','"+set.exchange+"','"+set.delay+"')";
            statement.executeUpdate(sql);//向数据库中插入数据

            connection.close();//关闭数据库连接
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Async
    public void insertDatasource(StreamDataSource set){
        try {
            String Drivde = "org.sqlite.JDBC";
            Class.forName(Drivde);// 加载驱动,连接sqlite的jdbc
            Connection connection = DriverManager.getConnection("jdbc:sqlite:"+RtcEngineMemory.engine_save_path);

            String sql = "replace into datasource values('"+set.id+"','"+set.name+"','"+set.type+"','"+set.ip
                    +"','"+set.port+"','"+set.user+"','"+set.password+"','"+set.table+"')";
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);//向数据库中插入数据

            connection.close();//关闭数据库连接
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Async
    public void insertFunc(StreamFunction set){
        try {
            String Drivde = "org.sqlite.JDBC";
            Class.forName(Drivde);// 加载驱动,连接sqlite的jdbc
            Connection connection = DriverManager.getConnection("jdbc:sqlite:"+RtcEngineMemory.engine_save_path);
            Statement statement = connection.createStatement();   //创建连接对象，是Java的一个操作数据库的重要接口

            String sql = "replace into func values('"+set.uuid+"','"+set.name+"','"+set.classpath+"','"+set.para
                    +"','"+set.input+"','"+set.output+"','"+set.type+"','"+set.path+"')";
            statement.executeUpdate(sql);//向数据库中插入数据

            connection.close();//关闭数据库连接
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Async
    public void insertState(StreamPublicState set){
        try {
            String Drivde = "org.sqlite.JDBC";
            Class.forName(Drivde);// 加载驱动,连接sqlite的jdbc
            Connection connection = DriverManager.getConnection("jdbc:sqlite:"+RtcEngineMemory.engine_save_path);
            Statement statement = connection.createStatement();   //创建连接对象，是Java的一个操作数据库的重要接口

            String sql = "replace into state values('"+set.id+"','"+set.name+"','"+set.task_id+"','"+set.descriptor +"')";
            statement.executeUpdate(sql);//向数据库中插入数据

            connection.close();//关闭数据库连接
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Async
    public void insertJobid(String pid, String jobid){
        try {
            String Drivde = "org.sqlite.JDBC";
            Class.forName(Drivde);// 加载驱动,连接sqlite的jdbc
            Connection connection = DriverManager.getConnection("jdbc:sqlite:"+RtcEngineMemory.engine_save_path);
            Statement statement = connection.createStatement();   //创建连接对象，是Java的一个操作数据库的重要接口

            String sql = "replace into projectjob values('"+pid+"','"+jobid+"')";
            System.out.println(sql);
            statement.executeUpdate(sql);//向数据库中插入数据

            connection.close();//关闭数据库连接
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Async
    public void insertSavepath(String pid, String savepath){
        try {
            String Drivde = "org.sqlite.JDBC";
            Class.forName(Drivde);// 加载驱动,连接sqlite的jdbc
            Connection connection = DriverManager.getConnection("jdbc:sqlite:"+RtcEngineMemory.engine_save_path);
            Statement statement = connection.createStatement();   //创建连接对象，是Java的一个操作数据库的重要接口

            String sql = "replace into projectsave values('"+pid+"','"+savepath+"')";
            System.out.println(sql);
            statement.executeUpdate(sql);//向数据库中插入数据
            connection.close();//关闭数据库连接
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Async
    public void writeLog(List<String> logs) throws Exception {
        StreamDataSet dataSet = RtcEngineMemory.getDataSetbyId(RtcEngineMemory.log_dataset_id);
        StreamDataSource dataSource = RtcEngineMemory.getDataSourcebyId(dataSet.source_id);
        String brokerList = dataSource.ip+":"+dataSource.port;
        String topicId = dataSet.topic;

        Properties kafkaProps = new Properties();
        kafkaProps.put("bootstrap.servers", brokerList);
        kafkaProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        kafkaProps.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(kafkaProps);

        for(String log: logs){
            ProducerRecord<String, String> record = new ProducerRecord<String, String>(topicId, null, log);//Topic Key Value
            try{
                producer.send(record);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        producer.close();
    }

}
