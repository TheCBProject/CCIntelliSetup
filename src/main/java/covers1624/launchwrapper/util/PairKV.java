package covers1624.launchwrapper.util;

import java.util.Map;

/**
 * Created by covers1624 on 6/13/2016.
 */
public class PairKV<K, V> implements Map.Entry<K, V> {

    private K key;
    private V value;

    public PairKV(K key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public K getKey() {
        return key;
    }

    @Override
    public V getValue() {
        return value;
    }

    @Override
    public V setValue(V value) {
        V oldValue = this.value;
        this.value = value;
        return oldValue;
    }

    @Override
    public String toString() {
        return "Pair: " + key.toString() + " | " + value.toString();
    }
}
