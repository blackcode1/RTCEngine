package com.example.RTCEngine;
import StreamDataPacket.BaseClassDataType.TaskState;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.BufferedReader;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

public class RtcEngineMemory {
    public static String flink_path = "";
    public static String flink_url = "";
    public static String flink_jar_path = "";
    public static String xml_root_path = "";
    public static String flink_save_path = "";
    public static String engine_save_path = "";
    public static String localtest_dataset_id = "";
    public static String log_dataset_id = "";
    public static String signal_dataset_id = "";
    public static String engine_url = "";
    public static String state_url = "";
    public static Integer heart_time = 60000;
    public static ArrayList<StreamProject> RtcEngineProjectList = new ArrayList<>();
    public static ArrayList<StreamTask> RtcEngineTaskList = new ArrayList<>();
    public static ArrayList<StreamDataSet> RtcEngineDatasetList = new ArrayList<>();
    public static ArrayList<StreamDataSource> RtcEngineDataSourceList = new ArrayList<>();
    public static ArrayList<StreamFunction> RtcEngineFunctionList = new ArrayList<>();
    public static ArrayList<StreamPublicState> RtcEnginePublicStateList = new ArrayList<>();
    public static Map<String, Map<String, TaskState>> RtcTaskState = new HashMap<>();
    public static Map<String, Map<String, Boolean>> RtcTaskInit = new HashMap<>();
    public static Map<String, String> RtcProjectJobID = new HashMap<>();
    public static Map<String, String> RtcProjectSavepath = new HashMap<>();

    public static void updateRtcEngineMemoryFromXML() {
        String engine_path = System.getProperty("user.dir");
        SAXReader saxReader = new SAXReader();
        try {
            Document document = saxReader.read(new File(engine_path + "/Engine.xml"));
            Element rootElement = document.getRootElement();
            flink_path = rootElement.element("flinkpath").getTextTrim();
            flink_url = rootElement.element("flinkurl").getTextTrim();
            flink_jar_path = rootElement.element("flinkjarpath").getTextTrim();
            flink_save_path = rootElement.element("flinksavepath").getTextTrim();
            xml_root_path =  rootElement.element("xmlrootpath").getTextTrim();
            engine_save_path =  rootElement.element("enginesavepath").getTextTrim();
            heart_time = Integer.valueOf(rootElement.element("hearttime").getTextTrim());
            engine_url = rootElement.element("engineurl").getTextTrim();
            state_url = rootElement.element("stateurl").getTextTrim();
            HeartNotice.setTime(heart_time);

            List<Element> dss = rootElement.element("datasets").elements("dataset");
            for(Element ds:dss){
                StreamDataSet nds = new StreamDataSet();
                nds.setDataSet(ds);
                RtcEngineMemory.RtcEngineDatasetList.add(nds);
                StreamDataSource ndso = new StreamDataSource();
                ndso.setDataSource(ds.element("datasource"));
                RtcEngineMemory.RtcEngineDataSourceList.add(ndso);

                if("test".equals(ds.attributeValue("use"))){
                    localtest_dataset_id = nds.id;
                }else if("log".equals(ds.attributeValue("use"))){
                    log_dataset_id = nds.id;
                }else if("signal".equals(ds.attributeValue("use"))){
                    signal_dataset_id = nds.id;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateRtcEngineMemoryFromDB() {
        try {
            String Drivde = "org.sqlite.JDBC";
            Class.forName(Drivde);// 加载驱动,连接sqlite的jdbc
            Connection connection = DriverManager.getConnection("jdbc:sqlite:"+RtcEngineMemory.engine_save_path);//连接数据库zhou.db,不存在则创建
            Statement statement = connection.createStatement();   //创建连接对象，是Java的一个操作数据库的重要接口
            String sql = "create table if not exists project(id varchar(20) PRIMARY KEY,name varchar(20),dataid varchar(20)," +
                    "para varchar(20),withcp int,cppace varchar(20),status varchar(20),input varchar(255),output varchar(20))";
            statement.executeUpdate(sql);
            sql = "create table if not exists task(id varchar(20) PRIMARY KEY,name varchar(20),pid varchar(20)," +
                    "stid varchar(20),stpublic varchar(20),stname varchar(20),stde varchar(20),sttype varchar(20)," +
                    "stperiod varchar(20),cond varchar(20),broad varchar(20),log varchar(20),outtopic varchar(20)," +
                    "taskstate int,alarm varchar(20),alg varchar(20),input varchar(255),caldev varchar(255)," +
                    "logdev varchar(255), pubstate varchar(255), timeout int, inputtopic varchar(255))" ;
            statement.executeUpdate(sql);
            sql = "create table if not exists dataset(id varchar(20) PRIMARY KEY,name varchar(20),sid varchar(20)," +
                    "dataid varchar(20),type varchar(20),topic varchar(20),offset varchar(20),filter varchar(20)," +
                    "usergroup varchar(20),query varchar(255), exchange varchar(20), delay varchar(20))";
            statement.executeUpdate(sql);
            sql = "create table if not exists datasource(id varchar(20) PRIMARY KEY,name varchar(20)," +
                    "type varchar(20),ip varchar(20),port varchar(20),user varchar(20),password varchar(20),dbtable varchar(20))" ;
            statement.executeUpdate(sql);
            sql = "create table if not exists func(id varchar(20) PRIMARY KEY,name varchar(20)," +
                    "classpath varchar(255),para varchar(255),input varchar(20),output varchar(20),type varchar(255)" +
                    ",path varchar(255))" ;
            statement.executeUpdate(sql);
            sql = "create table if not exists state(id varchar(20) PRIMARY KEY,name varchar(20)," +
                    "taskid varchar(20),descriptor varchar(255))";
            statement.executeUpdate(sql);
            sql = "create table if not exists projectjob(id varchar(20) PRIMARY KEY,jobid varchar(20))";
            statement.executeUpdate(sql);
            sql = "create table if not exists projectsave(id varchar(20) PRIMARY KEY,savepath varchar(20))";
            statement.executeUpdate(sql);

            ResultSet rSet = statement.executeQuery("select*from task");//搜索数据库，将搜索的放入数据集ResultSet中
            while (rSet.next()) {            //遍历这个数据集
                StreamTask task = new StreamTask();
                task.setTask(rSet);
                RtcEngineMemory.RtcEngineTaskList.add(task);
            }
            rSet.close();//关闭数据集
            rSet = statement.executeQuery("select*from dataset");//搜索数据库，将搜索的放入数据集ResultSet中
            while (rSet.next()) {            //遍历这个数据集
                StreamDataSet dataSet = new StreamDataSet();
                dataSet.setDataSet(rSet);
                RtcEngineMemory.RtcEngineDatasetList.add(dataSet);
            }
            rSet.close();//关闭数据集
            rSet = statement.executeQuery("select*from datasource");//搜索数据库，将搜索的放入数据集ResultSet中
            while (rSet.next()) {            //遍历这个数据集
                StreamDataSource streamDataSource = new StreamDataSource();
                streamDataSource.setDataSource(rSet);
                RtcEngineMemory.RtcEngineDataSourceList.add(streamDataSource);
            }
            rSet.close();//关闭数据集
            rSet = statement.executeQuery("select*from func");//搜索数据库，将搜索的放入数据集ResultSet中
            while (rSet.next()) {            //遍历这个数据集
                StreamFunction function = new StreamFunction();
                function.setFunction(rSet);
                RtcEngineMemory.RtcEngineFunctionList.add(function);
            }
            rSet.close();//关闭数据集
            rSet = statement.executeQuery("select*from state");//搜索数据库，将搜索的放入数据集ResultSet中
            while (rSet.next()) {            //遍历这个数据集
                StreamPublicState publicState = new StreamPublicState();
                publicState.setPublicState(rSet);
                RtcEngineMemory.RtcEnginePublicStateList.add(publicState);
            }
            rSet.close();//关闭数据集
            rSet = statement.executeQuery("select*from projectjob");//搜索数据库，将搜索的放入数据集ResultSet中
            while (rSet.next()) {            //遍历这个数据集
                String pid = rSet.getString(1);
                String jobid = rSet.getString(2);
                RtcEngineMemory.RtcProjectJobID.put(pid, jobid);
            }
            rSet.close();//关闭数据集
            rSet = statement.executeQuery("select*from projectsave");//搜索数据库，将搜索的放入数据集ResultSet中
            while (rSet.next()) {            //遍历这个数据集
                String pid = rSet.getString(1);
                String savepath = rSet.getString(2);
                RtcEngineMemory.RtcProjectSavepath.put(pid, savepath);
            }
            rSet.close();//关闭数据集
            rSet = statement.executeQuery("select*from project");//搜索数据库，将搜索的放入数据集ResultSet中
            while (rSet.next()) {            //遍历这个数据集
                StreamProject streamProject = new StreamProject();
                streamProject.setProject(rSet);
                RtcEngineMemory.RtcEngineProjectList.add(streamProject);
            }
            rSet.close();//关闭数据集
            connection.close();//关闭数据库连接
           // for(StreamProject streamProject: RtcEngineProjectList){
            //    new ProjectController().execProject(streamProject.id, 0);
            //}

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static ArrayList<StreamTask> getTaskListbyPid(String id){
        ArrayList<StreamTask> r = new ArrayList<>();
        for(StreamTask ta:RtcEngineTaskList){
            if(id.equals(ta.project_id)){
                r.add(ta);
            }
        }
        return r;
    }

    public static Boolean duplicated(String id){
        Iterator<StreamTask> it = RtcEngineTaskList.iterator();
        while (it.hasNext()){
            StreamTask ta = it.next();
            if(id.equals(ta.id) && ta.getTaskState()==1){
                return Boolean.TRUE;
            }
            else if(id.equals(ta.id)){
                it.remove();
            }
        }
        return Boolean.FALSE;
    }

    public static StreamFunction getFunctionbyId(String id){
        for(StreamFunction f:RtcEngineFunctionList){
            if(id.equals(f.uuid)){
                return f;
            }
        }
        return null;
    }

    public static StreamPublicState getPublicStatebyId(String id){
        for(StreamPublicState ps:RtcEnginePublicStateList){
            if(id.equals(ps.id)){
                return ps;
            }
        }
        return null;
    }

    public static StreamProject getProjectbyId(String pid){
        for(StreamProject pr:RtcEngineProjectList){
            if(pid.equals(pr.id)){
                return pr;
            }
        }
        return null;
    }

    public static StreamTask getTaskbyId(String tid){
        for(StreamTask ta:RtcEngineTaskList){
            if(tid.equals(ta.id)){
                return ta;
            }
        }
        return null;
    }

    public static StreamDataSet getDataSetbyId(String dsid){
        for(StreamDataSet da:RtcEngineDatasetList){
            if(dsid.equals(da.id)){
                return da;
            }
        }
        return null;
    }

    public static StreamDataSource getDataSourcebyId(String dsid){
        for(StreamDataSource das:RtcEngineDataSourceList){
            //System.out.println(dsid);
            if(dsid.equals(das.id)){
                return das;
            }
        }
        return null;
    }

    public static void addDataset(StreamDataSet dataSet){
        Iterator<StreamDataSet> it = RtcEngineDatasetList.iterator();
        while (it.hasNext()){
            StreamDataSet ta = it.next();
            if(dataSet.id.equals(ta.id)){
                it.remove();
            }
        }
        RtcEngineDatasetList.add(dataSet);
    }

    public static void addDatasource(StreamDataSource data){
        Iterator<StreamDataSource> it = RtcEngineDataSourceList.iterator();
        while (it.hasNext()){
            StreamDataSource ta = it.next();
            if(data.id.equals(ta.id)){
                it.remove();
            }
        }
        RtcEngineDataSourceList.add(data);
    }

    public static void addFunc(StreamFunction data){
        Iterator<StreamFunction> it = RtcEngineFunctionList.iterator();
        while (it.hasNext()){
            StreamFunction ta = it.next();
            if(data.uuid.equals(ta.uuid)){
                it.remove();
            }
        }
        RtcEngineFunctionList.add(data);
    }

    public static void addPublicstate(StreamPublicState data){
        Iterator<StreamPublicState> it = RtcEnginePublicStateList.iterator();
        while (it.hasNext()){
            StreamPublicState ta = it.next();
            if(data.id.equals(ta.id)){
                it.remove();
            }
        }
        RtcEnginePublicStateList.add(data);
    }
}
