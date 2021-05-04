package StreamDataPacket.SubClassDataType;

import StreamDataPacket.DataType;
import ty.pub.TransPacket;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TransPacketList extends DataType {
    public TransPacket streamData;

    public TransPacketList(TransPacket streamData) {
        super("TransPacket");
        this.streamData = streamData;
        this.dataID = streamData.getDeviceId();
    }

    public TransPacketList(TransPacket streamData, String dataID) {
        super("TransPacket");
        this.streamData = streamData;
        this.dataID = dataID;
    }

    public TransPacketList(TransPacket streamData, String dataID, String outputTopic) {
        super("TransPacket", outputTopic);
        this.streamData = streamData;
        this.dataID = dataID;
    }

    public List<String> allGKID(){
        Map<String, Map<Long, String>> res = this.streamData.getWorkStatusMap();
        List<String> gkIDList = new ArrayList<String>(res.keySet());
        return gkIDList;
    }

    @Override
    public String toString() {
        return "TransPacketList{" +
                "streamData=" + streamData +
                ", dataID='" + dataID + '\'' +
                ", streamDataType='" + streamDataType + '\'' +
                '}';
    }
}
