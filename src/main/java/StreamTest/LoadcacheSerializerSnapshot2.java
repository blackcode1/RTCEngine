package StreamTest;

import StreamTest.LoadcacheSerializer;
import com.google.common.cache.LoadingCache;
import org.apache.flink.api.common.typeutils.CompositeTypeSerializerSnapshot;
import org.apache.flink.api.common.typeutils.TypeSerializer;


public class LoadcacheSerializerSnapshot2<K, V> extends CompositeTypeSerializerSnapshot<LoadingCache<K, V>, LoadcacheSerializer<K, V>> {
    private static final int CURRENT_VERSION = 1;

    public LoadcacheSerializerSnapshot2() {
        super(LoadcacheSerializer.class);
        }

    public LoadcacheSerializerSnapshot2(LoadcacheSerializer<K, V> mapSerializer) {
        super(mapSerializer);
    }

    public int getCurrentOuterSnapshotVersion() {
        return 1;
    }

    protected LoadcacheSerializer<K, V> createOuterSerializerWithNestedSerializers(TypeSerializer<?>[] nestedSerializers) {
        TypeSerializer keySerializer = nestedSerializers[0];
        TypeSerializer valueSerializer = nestedSerializers[1];
        return new LoadcacheSerializer(keySerializer, valueSerializer);
    }

    protected TypeSerializer<?>[] getNestedSerializers(LoadcacheSerializer<K, V> outerSerializer) {
        return new TypeSerializer[]{outerSerializer.getKeySerializer(), outerSerializer.getValueSerializer()};
    }
}
