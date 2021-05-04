package StreamDataPacket.BaseClassDataType.TransPacketRely;

import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import ty.pub.TransPacket;

import java.io.IOException;

public class TransPacketDeserializationSchema implements DeserializationSchema<TransPacket> {

    private static final long serialVersionUID = 1L;
    private ParsedPacketDecoder decoder = new ParsedPacketDecoder();

    public TypeInformation<TransPacket> getProducedType() {
        return TypeInformation.of(new TypeHint<TransPacket>() {
            public TypeInformation<TransPacket> getTypeInfo() {
                return super.getTypeInfo();
            }
        });
    }

    public TransPacket deserialize(byte[] arg0) throws IOException {
        return decoder.deserialize("", arg0);
    }

    public boolean isEndOfStream(TransPacket arg0) {
        // TODO Auto-generated method stub
        return false;
    }

}
