package dev.flomik.dota.engine.stats;

import java.util.Locale;

public enum StatType {
    STR,
    AGI,
    INT,

    HP,
    MAX_HP,
    MANA,
    MAX_MANA,
    HP_REGEN,
    MANA_REGEN,

    XP_GAIN_BONUS,
    GOLD_GAIN_BONUS,

    ARMOR,
    PHYSICAL_RESIST,
    MAGIC_RESIST,
    STATUS_RESIST,
    SLOW_RESIST,
    EVASION,
    LIFESTEAL,
    SPELL_LIFESTEAL,

    ATTACK_DAMAGE_MIN,
    ATTACK_DAMAGE_MAX,
    ATTACK_SPEED,
    ATTACK_RANGE,
    PROJECTILE_SPEED,
    AOE_RADIUS_BONUS,
    DEBUFF_DURATION_AMPLIFY,
    INCOMING_DAMAGE_REDUCTION,
    OUTGOING_DAMAGE_BONUS,

    MOVESPEED_FLAT,
    MOVESPEED_PERCENT,
    TURN_RATE,
    VISION_DAY,
    VISION_NIGHT,

    SPELL_AMPLIFICATION,
    SPELL_CAST_RANGE,
    COOLDOWN_REDUCTION,
    MANA_COST_REDUCTION;


    @Override
    public String toString() {
        return name().toLowerCase(Locale.ROOT);
    }
}