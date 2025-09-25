package ai.smartdoc.garage.common.utils;

import lombok.Data;

@Data
public class Pair<K, V> {

    private K first;
    private V second;

    public Pair(K first, V second) {
        this.first = first;
        this.second = second;
    }
}
