package dev.flomik.dota.engine.stats;

import java.util.EnumMap;
import java.util.Set;

public class StatContainer {
    private final EnumMap<StatType, Double> stats = new EnumMap<>(StatType.class);

    public void set(StatType type, double value) {
        stats.put(type, value);
    }

    public double get(StatType type) {
        return stats.getOrDefault(type, 0.0);
    }

    public void add(StatType type, double delta) {
        stats.put(type, get(type) + delta);
    }

    public void mergeFrom(StatContainer other) {
        for (StatType type : other.stats.keySet()) {
            this.add(type, other.get(type));
        }
    }

    public Set<StatType> types() {
        return stats.keySet();
    }
}
