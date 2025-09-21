package dev.flomik.dota.ui.guiscreen;

import dev.flomik.dota.ui.camera.OrthoviewClientEvents;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TopdownGuiClientEvents {

    private static final Minecraft MC = Minecraft.getInstance();
    private static int noScreenTicks = 0;
    private static boolean shouldPause = false;
    private static final int MAX_GUI_SCALE_MOD = 3;
    private static Integer prevCalculatedScale = null;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent evt) {
        if (evt.phase != TickEvent.Phase.END) return;

        if (OrthoviewClientEvents.isEnabled()) {
            if (MC.screen == null) {
                noScreenTicks++;
                if (noScreenTicks >= 3) {
                    MC.setScreen(new TopdownGui());
                    noScreenTicks = 0;
                }
            }

            if (MC.screen instanceof TopdownGui) {
                // === EDGE PANNING ===
                double edgePanX = 0;
                double edgePanZ = 0;

                double[] mouseXBuf = new double[1];
                double[] mouseYBuf = new double[1];
                GLFW.glfwGetCursorPos(MC.getWindow().getWindow(), mouseXBuf, mouseYBuf);
                double rawMouseX = mouseXBuf[0];
                double rawMouseY = mouseYBuf[0];

                double guiWidth = MC.getWindow().getGuiScaledWidth();
                double guiHeight = MC.getWindow().getGuiScaledHeight();
                double screenWidth = MC.getWindow().getScreenWidth();
                double screenHeight = MC.getWindow().getScreenHeight();

                double mouseX = rawMouseX * guiWidth / screenWidth;
                double mouseY = rawMouseY * guiHeight / screenHeight;

                int EDGE_SIZE = 12;
                double EDGE_SPEED = 1.5;

                if (mouseX <= EDGE_SIZE) edgePanX += EDGE_SPEED;
                else if (mouseX >= guiWidth - EDGE_SIZE) edgePanX -= EDGE_SPEED;

                if (mouseY <= EDGE_SIZE) edgePanZ += EDGE_SPEED;
                else if (mouseY >= guiHeight - EDGE_SIZE) edgePanZ -= EDGE_SPEED;

                OrthoviewClientEvents.moveCamera(edgePanX, edgePanZ);
            }
        }
    }

    @SubscribeEvent
    public static void onScreenOpening(ScreenEvent.Opening evt) {
        if (evt.getScreen() instanceof TopdownGui) {
            var options = MC.options;
            int requested = options.guiScale().get();
            int target = Math.min(MAX_GUI_SCALE_MOD, requested);
            if (requested == 0) target = MAX_GUI_SCALE_MOD;

            prevCalculatedScale = MC.getWindow().calculateScale(requested, MC.isEnforceUnicode());
            int newScale = MC.getWindow().calculateScale(target, MC.isEnforceUnicode());
            MC.getWindow().setGuiScale(newScale);
        } else {
            if (prevCalculatedScale != null) {
                MC.getWindow().setGuiScale(prevCalculatedScale);
            } else {
                int requested = MC.options.guiScale().get();
                int scale = MC.getWindow().calculateScale(requested, MC.isEnforceUnicode());
                MC.getWindow().setGuiScale(scale);
            }
        }
    }

    @SubscribeEvent
    public static void onScreenClosing(ScreenEvent.Closing evt) {
        if (evt.getScreen() instanceof TopdownGui) {
            shouldPause = false;
        }
        int requested = MC.options.guiScale().get();
        int scale = MC.getWindow().calculateScale(requested, MC.isEnforceUnicode());
        MC.getWindow().setGuiScale(scale);
        prevCalculatedScale = null;
    }

    @SubscribeEvent
    public static void onKeyPress(ScreenEvent.KeyPressed.Pre evt) {
        if (!OrthoviewClientEvents.isEnabled()) return;

        var key = evt.getKeyCode();
        var invKey = MC.options.keyInventory.getKey().getValue();
        var chatKey = MC.options.keyChat.getKey().getValue();

        if (key == invKey || key == chatKey) {
            evt.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void beforeGuiRender(ScreenEvent.Render.Pre evt) {
        if (evt.getScreen() instanceof TopdownGui) {
            evt.setCanceled(true);
        }
    }
}
