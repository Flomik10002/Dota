package dev.flomik.dota.ui.cursor;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.flomik.dota.ui.camera.OrthoviewClientEvents;
import dev.flomik.dota.ui.guiscreen.TopdownGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RenderHighlightEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Vector3d;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CursorClientEvents {

    private static final Minecraft MC = Minecraft.getInstance();

    private static boolean leftDown = false;
    private static boolean rightDown = false;

    private static BlockPos preselectedBlockPos = BlockPos.ZERO;
    private static Vector3d cursorWorldPos = new Vector3d(0,0,0);
    private static Vector3d cursorWorldPosLast = new Vector3d(0,0,0);
    private static Vec2 leftDownPos = new Vec2(-1, -1);
    private static Vec2 leftDragPos = new Vec2(-1, -1);

    private static final ResourceLocation TEX_POINTER = new ResourceLocation("dota", "textures/cursor/cursor.png");
    private static final ResourceLocation TEX_GRAB    = new ResourceLocation("dota", "textures/cursor/cursor_hand_grab.png");

    @SubscribeEvent
    public static void onDrawScreen(ScreenEvent.Render evt) {
        if (!OrthoviewClientEvents.isEnabled()) return;
        if (!(evt.getScreen() instanceof TopdownGui)) return;
        if (MC.player == null || MC.level == null) return;

        long window = MC.getWindow().getWindow();
        GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_HIDDEN);
        if (GLFW.glfwRawMouseMotionSupported()) {
            GLFW.glfwSetInputMode(window, GLFW.GLFW_RAW_MOUSE_MOTION, GLFW.GLFW_FALSE);
        }

        ResourceLocation tex = leftDown || rightDown ? TEX_GRAB : TEX_POINTER;

        int drawX = Math.min(evt.getMouseX(), MC.getWindow().getGuiScaledWidth() - 5);
        int drawY = Math.min(evt.getMouseY(), MC.getWindow().getGuiScaledHeight() - 5);
        drawX = Math.max(0, drawX);
        drawY = Math.max(0, drawY);

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1f,1f,1f,1f);
        RenderSystem.setShaderTexture(0, tex);
        evt.getGuiGraphics().pose().pushPose();
        evt.getGuiGraphics().pose().translate(0, 0, 2500); // поверх всего
        evt.getGuiGraphics().blit(tex, drawX, drawY, 0, 0, 16, 16, 16, 16);
        evt.getGuiGraphics().pose().popPose();

        cursorWorldPosLast.set(cursorWorldPos);
        Vec3 cameraPos = MC.gameRenderer.getMainCamera().getPosition();
        Vec3 look = MC.player.getLookAngle();
        Vec3 near = cameraPos.add(look.scale(0.0));
        Vec3 far  = cameraPos.add(look.scale(200.0));

        BlockHitResult hit = MC.level.clip(new ClipContext(near, far, ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, MC.player));
        if (hit != null && hit.getType() != HitResult.Type.MISS) {
            Vec3 hp = hit.getLocation().add(-0.001, -0.001, -0.001);
            cursorWorldPos.set(hp.x, hp.y, hp.z);
            preselectedBlockPos = hit.getBlockPos();
        } else {
            cursorWorldPos.set(0,0,0);
            preselectedBlockPos = BlockPos.ZERO;
        }
    }

    @SubscribeEvent
    public static void onMouseClick(ScreenEvent.MouseButtonPressed.Post evt) {
        if (!OrthoviewClientEvents.isEnabled()) return;
        if (!(evt.getScreen() instanceof TopdownGui)) return;

        if (evt.getButton() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            leftDown = true;
            leftDownPos = new Vec2(Mth.floor(evt.getMouseX()), Mth.floor(evt.getMouseY()));
            leftDragPos = leftDownPos;
        } else if (evt.getButton() == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            rightDown = true;
        }
    }

    @SubscribeEvent
    public static void onMouseDrag(ScreenEvent.MouseDragged.Pre evt) {
        if (!OrthoviewClientEvents.isEnabled()) return;
        if (!(evt.getScreen() instanceof TopdownGui)) return;

        if (leftDown && !(leftDownPos.x < 0 || leftDownPos.y < 0)) {
            leftDragPos = new Vec2(Mth.floor(evt.getMouseX()), Mth.floor(evt.getMouseY()));
        }
    }

    @SubscribeEvent
    public static void onMouseRelease(ScreenEvent.MouseButtonReleased.Post evt) {
        if (!OrthoviewClientEvents.isEnabled()) return;
        if (!(evt.getScreen() instanceof TopdownGui)) return;

        if (evt.getButton() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            leftDown = false;
            leftDownPos = new Vec2(-1, -1);
            leftDragPos = new Vec2(-1, -1);
        } else if (evt.getButton() == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            rightDown = false;
        }
    }

    @SubscribeEvent
    public static void renderOverlay(RenderGuiOverlayEvent.Post evt) {
        if (leftDown && leftDownPos.x >= 0 && leftDownPos.y >= 0 && leftDragPos.x >= 0 && leftDragPos.y >= 0) {
            int x1 = Math.round(leftDownPos.x);
            int y1 = Math.round(leftDownPos.y);
            int x2 = Math.round(leftDragPos.x);
            int y2 = Math.round(leftDragPos.y);
            evt.getGuiGraphics().fill(x1, y1, x2, y2, 0x3400FFFF);
            evt.getGuiGraphics().fill(x1, y1, x2, y1+1, 0xAA00FFFF);
            evt.getGuiGraphics().fill(x1, y2-1, x2, y2, 0xAA00FFFF);
            evt.getGuiGraphics().fill(x1, y1, x1+1, y2, 0xAA00FFFF);
            evt.getGuiGraphics().fill(x2-1, y1, x2, y2, 0xAA00FFFF);
        }
    }

    @SubscribeEvent
    public static void onHighlightBlock(RenderHighlightEvent.Block evt) {
        if (MC.level != null && OrthoviewClientEvents.isEnabled()) {
            evt.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent evt) {
        // if (evt.getStage() == RenderLevelStageEvent.Stage.AFTER_CUTOUT_BLOCKS) { ... }
    }

    public static Vector3d getCursorWorldPos() { return cursorWorldPos; }
    public static BlockPos getPreselectedBlockPos() { return preselectedBlockPos; }

    private static boolean rayIntersectsAABB(Vec3 origin, Vec3 dir, AABB box) {
        return false;
    }
}
