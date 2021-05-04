package StreamDataPacket.SubClassDataType;

import StreamDataPacket.DataType;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonList extends DataType {
    //Json:{String dataID, Long timestamp, Map<String gk, Map<Long key, String value>> workStatusMap}
    public JSONObject streamData;

    public JsonList(JSONObject streamData) {
        super("JSONObject");
        this.streamData = streamData;
        this.dataID = streamData.getString("deviceID");
    }

    public JsonList(JSONObject streamData, String dataID) {
        super("JSONObject");
        this.streamData = streamData;
        this.dataID = dataID;
    }

    public JsonList(JSONObject streamData, String dataID, String outputTopic) {
        super("JSONObject", outputTopic);
        this.streamData = streamData;
        this.dataID = dataID;
    }

    public List<String> allGKID(){
        Map<String, Map<String, String>> res = (Map<String, Map<String, String>>) this.streamData.get("workStatusMap");
        List<String> gkIDList = new ArrayList<String>(res.keySet());
        return gkIDList;
    }

    public Map<String, Map<String, String>> getWorkStatusMap(){
        Map<String, Map<String, String>> res = (Map<String, Map<String, String>>) this.streamData.get("workStatusMap");
        return res;
    }

    @Override
    public String toString() {
        return "JsonList{" +
                "streamData=" + streamData +
                ", dataID='" + dataID + '\'' +
                ", streamDataType='" + streamDataType + '\'' +
                '}';
    }
}
