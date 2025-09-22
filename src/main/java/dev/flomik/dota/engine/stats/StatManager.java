package dev.flomik.dota.engine.stats;

import java.util.ArrayList;
import java.util.List;

public class StatManager {
    private final StatContainer baseStats = new StatContainer();
    private final List<StatContainer> modifiers = new ArrayList<>();

    public void setBase(StatType type, double value) {
        baseStats.set(type, value);
    }

    public void addModifier(StatContainer modifier) {
        modifiers.add(modifier);
    }

    public void clearModifiers() {
        modifiers.clear();
    }

    public double getFinal(StatType type) {
        double total = baseStats.get(type);
        for (StatContainer mod : modifiers) {
            total += mod.get(type);
        }
        return total;
    }

    public double getBase(StatType type) {
        return baseStats.get(type);
    }
}

