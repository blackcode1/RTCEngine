package StreamTest;

import StreamDataPacket.BaseClassDataType.TaskState;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.api.common.typeutils.TypeSerializerSnapshot;
import org.apache.flink.api.common.typeutils.base.StringSerializer;
import org.apache.flink.core.memory.DataInputView;
import org.apache.flink.core.memory.DataOutputView;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class LoadcacheSerializer<K, V> extends TypeSerializer<LoadingCache<K, V>>  {
    private static final long serialVersionUID = 1L;
    private final TypeSerializer<K> keySerializer;
    private final TypeSerializer<V> valueSerializer;

    public LoadcacheSerializer(TypeSerializer<K> keySerializer, TypeSerializer<V> valueSerializer) {
        this.keySerializer = keySerializer;
        this.valueSerializer = valueSerializer;
    }

    public LoadcacheSerializer() {
        this.keySerializer = (TypeSerializer<K>) new StringSerializer();
        this.valueSerializer = (TypeSerializer<V>) TypeInformation.of(new TypeHint<TaskState>() {}).createSerializer(new ExecutionConfig());
    }

    public LoadcacheSerializer(ExecutionConfig config) {
        this.keySerializer = (TypeSerializer<K>) new StringSerializer();
        this.valueSerializer = (TypeSerializer<V>) TypeInformation.of(new TypeHint<TaskState>() {}).createSerializer(config);
    }

    public TypeSerializer<K> getKeySerializer() {
        return this.keySerializer;
    }

    public TypeSerializer<V> getValueSerializer() {
        return this.valueSerializer;
    }

    @Override
    public boolean isImmutableType() {
        return false;
    }

    @Override
    public TypeSerializer<LoadingCache<K, V>> duplicate() {
        TypeSerializer<K> duplicateKeySerializer = this.keySerializer.duplicate();
        TypeSerializer<V> duplicateValueSerializer = this.valueSerializer.duplicate();
        return duplicateKeySerializer == this.keySerializer && duplicateValueSerializer == this.valueSerializer ? this : new LoadcacheSerializer(duplicateKeySerializer, duplicateValueSerializer);
    }

    @Override
    public LoadingCache<K, V> createInstance() {
        Long statePeriod = 1L;
        LoadingCache<K, V> loadingCache = CacheBuilder
                .newBuilder()
                .expireAfterAccess(statePeriod, TimeUnit.MINUTES)
                .build(new CacheLoader<K, V>() {
                    @Override
                    public V load(K s) throws Exception {
                        TaskState taskState = new TaskState(s.toString());
                        return (V)taskState;
                    }
                });
        loadingCache.put((K)"Time", (V)new TaskState(statePeriod.toString()));
        return loadingCache;
    }

    @Override
    public LoadingCache<K, V> copy(LoadingCache<K, V> kvLoadingCache) {
        //Long statePeriod = kvLoadingCache.get(()"Time").toString();
        LoadingCache<K, V> loadingCache = CacheBuilder
                .newBuilder()
                .expireAfterAccess(1, TimeUnit.MINUTES)
                .build(new CacheLoader<K, V>() {
                    @Override
                    public V load(K s) throws Exception {
                        TaskState taskState = new TaskState(s.toString());
                        return (V)taskState;
                    }
                });
        loadingCache.putAll(kvLoadingCache.asMap());
        return loadingCache;
    }

    @Override
    public LoadingCache<K, V> copy(LoadingCache<K, V> kvLoadingCache, LoadingCache<K, V> t1) {
        return this.copy(kvLoadingCache);
    }

    @Override
    public int getLength() {
        return -1;
    }

    @Override
    public void serialize(LoadingCache<K, V> kvLoadingCache, DataOutputView dataOutputView) throws IOException {
        kvLoadingCache.cleanUp();
        Map<K, V> map = kvLoadingCache.asMap();
        Long statePeriod = 3*365L;
        try {
            if(map.containsKey((K)"Time")){
                TaskState taskState = (TaskState) kvLoadingCache.get((K)"Time");
                statePeriod = Long.parseLong(taskState.getStateType());
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        int size = map.size();
        dataOutputView.writeLong(statePeriod);
        dataOutputView.writeInt(size);
        Iterator var4 = map.entrySet().iterator();

        while(var4.hasNext()) {
            Map.Entry<K, V> entry = (Map.Entry)var4.next();
            this.keySerializer.serialize(entry.getKey(), dataOutputView);
            if (entry.getValue() == null) {
                dataOutputView.writeBoolean(true);
            } else {
                dataOutputView.writeBoolean(false);
                this.valueSerializer.serialize(entry.getValue(), dataOutputView);
            }
        }
    }

    @Override
    public LoadingCache<K, V> deserialize(DataInputView dataInputView) throws IOException {
        Long statePeriod = dataInputView.readLong();
        int size = dataInputView.readInt();
        Map<K, V> map = new HashMap(size);
        LoadingCache<K, V> loadingCache = CacheBuilder
                .newBuilder()
                .expireAfterAccess(statePeriod, TimeUnit.MINUTES)
                .build(new CacheLoader<K, V>() {
                    @Override
                    public V load(K s) throws Exception {
                        TaskState taskState = new TaskState(s.toString());
                        return (V)taskState;
                    }
                });
        for(int i = 0; i < size; ++i) {
            K key = this.keySerializer.deserialize(dataInputView);
            boolean isNull = dataInputView.readBoolean();
            V value = isNull ? null : this.valueSerializer.deserialize(dataInputView);
            map.put(key, value);
        }
        loadingCache.putAll(map);
        return loadingCache;
    }

    @Override
    public LoadingCache<K, V> deserialize(LoadingCache<K, V> kvLoadingCache, DataInputView dataInputView) throws IOException {
        return this.deserialize(dataInputView);
    }

    @Override
    public void copy(DataInputView dataInputView, DataOutputView dataOutputView) throws IOException {
        Long statePeriod = dataInputView.readLong();
        dataOutputView.writeLong(statePeriod);
        int size = dataInputView.readInt();
        dataOutputView.writeInt(size);

        for(int i = 0; i < size; ++i) {
            this.keySerializer.copy(dataInputView, dataOutputView);
            boolean isNull = dataInputView.readBoolean();
            dataOutputView.writeBoolean(isNull);
            if (!isNull) {
                this.valueSerializer.copy(dataInputView, dataOutputView);
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this || obj != null && obj.getClass() == this.getClass() && this.keySerializer.equals(((LoadcacheSerializer)obj).getKeySerializer()) && this.valueSerializer.equals(((LoadcacheSerializer)obj).getValueSerializer());
    }

    public boolean canEqual(Object obj) {
        return obj != null && obj.getClass() == this.getClass();
    }

    @Override
    public int hashCode() {
        return this.keySerializer.hashCode() * 31 + this.valueSerializer.hashCode();
    }

    @Override
    public TypeSerializerSnapshot<LoadingCache<K, V>> snapshotConfiguration() {
        return new LoadcacheSerializerSnapshot2(this);
    }
}
