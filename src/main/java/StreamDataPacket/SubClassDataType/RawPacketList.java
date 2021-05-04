package StreamDataPacket.SubClassDataType;

import StreamDataPacket.DataType;
import ty.pub.RawDataPacket;

public class RawPacketList extends DataType {
    public RawDataPacket streamData;

    public RawPacketList(RawDataPacket streamData) {
        super("RawDataPacket");
        this.streamData = streamData;
        this.dataID = streamData.getRawDataId();
    }


}
