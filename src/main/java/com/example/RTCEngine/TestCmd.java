package com.example.RTCEngine;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.List;

public class TestCmd {
    public static void exeCmd(String commandStr) {
        System.out.println("中文测试");
        BufferedReader br = null;
        try {
            Process p = Runtime.getRuntime().exec(commandStr);
            br = new BufferedReader(new InputStreamReader(p.getInputStream(),"gbk"));
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            System.out.println(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally
        {
            if (br != null)
            {
                try {
                    br.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public static void main(String[] args) throws Exception{
        StreamProject newProject = new StreamProject();

        SAXReader saxReader = new SAXReader();

        Document document = saxReader.read(new File("C:\\Users\\12905\\Desktop\\tes\\EBC0AED3BF5542449D6A1A5D5679A371.xml"));
        Element rootElement = document.getRootElement();
        Element p = rootElement.element("project");
        String pid = p.element("uuid").getTextTrim();

        newProject.setProject(p);
        List<Element> dss = p.element("datasets").elements("dataset");

        for (Element ds : dss) {
            StreamDataSet nds = new StreamDataSet();
            nds.setDataSet(ds);
            RtcEngineMemory.addDataset(nds);

            //to get datasources
            StreamDataSource ndso = new StreamDataSource();
            ndso.setDataSource(ds.element("datasource"));
            RtcEngineMemory.addDatasource(ndso);
        }
        System.out.println(newProject);
    }
}