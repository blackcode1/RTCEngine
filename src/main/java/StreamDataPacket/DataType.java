package StreamDataPacket;

public class DataType implements Cloneable {
    public String streamDataType;
    public String dataID;
    public String outputTopic = null;

    public DataType(String streamDataType, String outputTopic) {
        this.streamDataType = streamDataType;
        this.outputTopic = outputTopic;
    }

    public DataType(String streamDataType) {
        this.streamDataType = streamDataType;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        return "DataType{" +
                "streamDataType='" + streamDataType + '\'' +
                ", dataID='" + dataID + '\'' +
                '}';
    }
}

