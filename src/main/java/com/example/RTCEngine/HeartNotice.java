package com.example.RTCEngine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

@Configuration
@ComponentScan(value = "com.es.evaluation_teaching_wp.utils")
@EnableScheduling
public class HeartNotice implements SchedulingConfigurer {
    @Autowired
    private LatencyTask latencyTask;
    private static Integer t = 1000;

    public static void setTime(Integer time) {
        t = time;
    }


    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.addTriggerTask(new Runnable() {
            @Override
            public void run() {
                for (StreamProject project : RtcEngineMemory.RtcEngineProjectList) {
                    String pid = project.id;
                    if(!RtcEngineMemory.RtcProjectJobID.containsKey(pid) || RtcEngineMemory.RtcProjectJobID.get(pid) == null){
                        try {
                            latencyTask.getProjectJobID(pid, 0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        latencyTask.getProjectStatus(pid, 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (RtcEngineMemory.RtcProjectJobID.containsKey(pid) && RtcEngineMemory.RtcProjectJobID.get(pid) != null) {
                        try {
                            latencyTask.getProjectSavePath(pid, 0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                try {
                    String logStr = StreamLog.createExLog(null, "Heart", "Engine is running", null, null, null);
                    latencyTask.writeLog(Collections.singletonList(logStr));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Trigger() {
            @Override
            public Date nextExecutionTime(TriggerContext triggerContext) {
                // 定时任务触发，可修改定时任务的执行周期
                PeriodicTrigger trigger1 = new PeriodicTrigger(t);
                trigger1.setFixedRate(true);
                Date nextExecDate = trigger1.nextExecutionTime(triggerContext);
                return nextExecDate;
            }
        });
    }
}
