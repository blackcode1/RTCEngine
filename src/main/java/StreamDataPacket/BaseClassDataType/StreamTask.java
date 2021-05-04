package StreamDataPacket.BaseClassDataType;

import StreamDataPacket.DataType;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class StreamTask {

    public String taskID;
    public String jarID;
    public List<String> inputDataSetList;
    public Boolean useOnTimeSource;
    public String outputDataSet;
    public List<String> publicStateIDList;
    public String stateType;
    public Long statePeriod;
    public List<String> condition;
    public List<String> deviceList;
    public Integer taskState;
    public String outputTopic;

    public StreamTask(JSONObject jsonObject) {
        this.taskID = jsonObject.getString("TaskID");
        this.jarID = jsonObject.getString("JarID");
        this.inputDataSetList = new ArrayList<String>(Arrays.asList(
                jsonObject.getString("InputDataSetList").split(",")));
        this.useOnTimeSource = jsonObject.getBoolean("Broadcast");
        this.outputDataSet = jsonObject.getString("OutputDataSet");
        this.publicStateIDList = new ArrayList<String>(Arrays.asList(
                jsonObject.getString("PublicStateIDList").split(",")));
        this.stateType = jsonObject.getString("StateType");
        this.statePeriod = jsonObject.getLong("StatePeriod");
        this.condition = new ArrayList<String>(Arrays.asList(
                jsonObject.getString("Condition").split(",")));
        this.deviceList = new ArrayList<String>(Arrays.asList(
                jsonObject.getString("DeviceList").split(",")));
        this.taskState = jsonObject.getInteger("TaskState");
        this.outputTopic = jsonObject.getString("outputTopic");
    }

    @Override
    public String toString() {
        return "StreamTask{" +
                "taskID='" + taskID + '\'' +
                ", jarID='" + jarID + '\'' +
                ", inputDataSetList=" + inputDataSetList +
                ", useOnTimeSource=" + useOnTimeSource +
                ", outputDataSet='" + outputDataSet + '\'' +
                ", publicStateIDList=" + publicStateIDList +
                ", stateType='" + stateType + '\'' +
                ", statePeriod=" + statePeriod +
                ", condition=" + condition +
                ", deviceList=" + deviceList +
                ", taskState=" + taskState +
                '}';
    }

    public static class TaskVarPacket extends DataType {
        public Map<String, List<Map<String, String>>> taskvar;

        public TaskVarPacket(Map<String, List<Map<String, String>>> taskvar) {
            super("taskVarBig");
            this.taskvar = taskvar;
        }

        public TaskVarPacket(Map<String, List<Map<String, String>>> taskvar, String filter) {
            super("taskVarSmall");
            this.taskvar = taskvar;
            this.dataID = filter;
        }

        @Override
        public String toString() {
            return "TaskVarPacket{" +
                    "taskvar=" + taskvar +
                    ", dataID='" + dataID + '\'' +
                    ", streamDataType='" + streamDataType + '\'' +
                    '}';
        }
    }
}
