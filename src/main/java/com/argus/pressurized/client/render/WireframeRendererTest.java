package com.argus.pressurized.client.render;

import com.argus.pressurized.Pressurized;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashSet;
import java.util.Set;

@Mod.EventBusSubscriber(modid = Pressurized.MODID, value = Dist.CLIENT)
public class WireframeRendererTest {

    private static final Set<Wireframe> wireframes = new HashSet<>();

    @SubscribeEvent
    public static void onRenderWorld(RenderLevelStageEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) {
            return;
        }

        PoseStack poseStack = event.getPoseStack();

        for (Wireframe wireframe : wireframes) {
            drawLineBox(poseStack, wireframe.aabb, wireframe.color);
        }
    }

    /*
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            return;
        }

        Iterator<Wireframe> iterator = wireframes.iterator();
        while (iterator.hasNext()) {
            Wireframe wireframe = iterator.next();
            wireframe.tick();
            if (!wireframe.shouldRender()) {
                iterator.remove();
            }
        }
    }
     */

    public static void addWireframe(BlockPos pos, float scale, int color, float fadeInTime, float lifetime, float fadeOutTime) {
        wireframes.add(new Wireframe(new AABB(pos).inflate(scale), color, fadeInTime, lifetime, fadeOutTime));
        Pressurized.LOGGER.info("Adding wireframe");
    }

    /*
    public static void removeWireframe(BlockPos pos) {
        wireframes.removeIf(wireframe -> wireframe.pos.equals(pos));
        Pressurized.LOGGER.info("Removing wireframe");
    }
     */

    public static void drawLineBox(PoseStack poseStack, AABB aabb, int color) {
        Minecraft MC = Minecraft.getInstance();

        Vec3 camera = MC.gameRenderer.getMainCamera().getPosition();

        float r = (color >> 16 & 255) / 255.0F;
        float g = (color >> 8 & 255) / 255.0F;
        float b = (color & 255) / 255.0F;
        float a = 1.0F;

        RenderSystem.setShader(GameRenderer::getRendertypeLinesShader);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.lineWidth(5.0F);
        RenderSystem.depthMask(false);

        VertexConsumer vertexConsumer = MC.renderBuffers().bufferSource().getBuffer(RenderType.LINES);

        poseStack.pushPose();
        poseStack.translate(-camera.x, -camera.y, -camera.z);
        LevelRenderer.renderLineBox(poseStack, vertexConsumer, aabb, r, g, b, a);
        poseStack.popPose();

        RenderSystem.depthMask(true);
        MC.renderBuffers().bufferSource().endBatch(RenderType.LINES);
    }

    private static class Wireframe {
        private final AABB aabb;
        private final int color;
        private final float fadeInTime;
        private final float lifetime;
        private final float fadeOutTime;
        private float age;

        public Wireframe(AABB aabb, int color, float fadeInTime, float lifetime, float fadeOutTime) {
            this.aabb = aabb;
            this.color = color;
            this.fadeInTime = fadeInTime;
            this.lifetime = lifetime;
            this.fadeOutTime = fadeOutTime;
            this.age = 0;
        }

        public void tick() {
            age++;
            if (age > fadeInTime + lifetime + fadeOutTime) {
                wireframes.remove(this);
            }
        }
    }
}