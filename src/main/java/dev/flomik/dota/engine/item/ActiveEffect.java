package dev.flomik.dota.engine.item;

public interface ActiveEffect {

    String getId();

    default int getManaCost() {
        return 0;
    }

    default double getCooldownSeconds() {
        return 0.0;
    }

    default boolean canActivate(HeroEntity hero, DotaItem item) {
        return true;
    }

    void onActivate(HeroEntity hero, DotaItem item);
}