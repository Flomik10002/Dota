package dev.flomik.dota.engine.item;

public interface PassiveEffect {

    String getId();

    void onEquip(HeroEntity hero, DotaItem item);

    void onUnequip(HeroEntity hero, DotaItem item);

    default Double getCooldownSeconds() {
        return null;
    }
}