package dev.flomik.dota.ui.camera;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class OrthoviewClientEvents {
    private static final Minecraft MC = Minecraft.getInstance();

    private static boolean enabled = false;
    private static Entity followTarget = null;
    private static double height = 25.0;
    private static double lerp = 0.25;
    private static double baseY = 0;

    public static boolean isEnabled() {
        return enabled;
    }

    public static void enable(Entity target) {
        followTarget = target;
        baseY = target != null ? target.getY() : 64.0;
        enabled = true;
        if (MC.player != null) {
            MC.player.setNoGravity(true);
            MC.player.setInvisible(true);
        }
    }

    public static void disable() {
        enabled = false;
        followTarget = null;
        if (MC.player != null) {
            MC.player.setNoGravity(false);
            MC.player.setInvisible(false);
        }
    }


    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent evt) {
        if (!enabled || evt.phase != TickEvent.Phase.END || MC.isPaused() || MC.player == null) return;

        Entity target = followTarget != null ? followTarget : MC.player;

        double targetX = target.getX();
        double targetZ = target.getZ();
        double targetY = baseY + height;

        double camX = MC.player.getX();
        double camY = MC.player.getY();
        double camZ = MC.player.getZ();

        double newX = camX + (targetX - camX) * lerp;
        double newZ = camZ + (targetZ - camZ) * lerp;
        double newY = camY + (targetY - camY) * lerp;

        MC.player.setPos(newX, newY, newZ);
        MC.player.setXRot(75.0f);
        MC.player.setYRot(0.0f);
    }

    @SubscribeEvent
    public static void onKey(InputEvent.Key evt) {
        if (evt.getAction() != GLFW.GLFW_PRESS) return;
        if (evt.getKey() == GLFW.GLFW_KEY_F10) {
            if (enabled) disable();
            else enable(MC.player);
        }
    }

    public static void moveCamera(double dx, double dz) {
        if (followTarget != null) {
            followTarget.setPos(followTarget.getX() + dx, followTarget.getY(), followTarget.getZ() + dz);
        }
    }
}
