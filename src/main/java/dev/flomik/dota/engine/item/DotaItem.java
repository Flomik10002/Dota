package dev.flomik.dota.engine.item;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class DotaItem extends Item{
    private final boolean isPassive;
    @Nullable
    private final Integer cooldown;
    private final boolean stackable;

    public DotaItem(CustomProperties properties, boolean stackable) {
        super(properties);
        this.isPassive = properties.isPassive;
        this.cooldown = properties.cooldown;
        this.stackable = stackable;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, level, entity, slot, selected);
    }

    public boolean isPassive() {
        return isPassive;
    }

    @Nullable
    public Integer getCooldown() {
        return cooldown;
    }

    public static class CustomProperties extends Item.Properties {
        private boolean isPassive = false;
        @Nullable
        private Integer cooldown = null;

        public CustomProperties passive() {
            this.isPassive = true;
            this.cooldown = null;
            return this;
        }

        public CustomProperties activeWithCooldown(int cooldownTicks) {
            this.isPassive = false;
            this.cooldown = cooldownTicks;
            return this;
        }
    }
}