package com.argus.pressurized.client.render;

import com.argus.pressurized.Pressurized;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = Pressurized.MODID, value = Dist.CLIENT)
public class WireframeRenderer {

    private static final List<Wireframe> wireframes = new ArrayList<>();

    @SubscribeEvent
    public static void onRenderWorldLast(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null || mc.level == null) {
                return;
            }

            for (Wireframe wireframe : wireframes) {
                if (wireframe.shouldRender()) {
                    renderWireframe(event.getPoseStack(), wireframe.minX, wireframe.minY, wireframe.minZ, wireframe.maxX, wireframe.maxY, wireframe.maxZ, wireframe.offset, wireframe.color);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            return;
        }

        for (Wireframe wireframe : wireframes) {
            wireframe.tick();
            if (!wireframe.shouldRender()) {
                wireframes.remove(wireframe);
            }
        }
    }

    public static void addWireframe(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, float offset, int color, float lifetime) {
        wireframes.add(new Wireframe(minX, minY, minZ, maxX, maxY, maxZ, offset, color, lifetime));
    }

    /*
    public static void removeWireframe(BlockPos pos) {
        wireframes.removeIf(wireframe -> wireframe.pos.equals(pos));
    }
     */

    public static void renderWireframe(PoseStack poseStack, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, float offset, int color) {
        Minecraft MC = Minecraft.getInstance();
        Vec3 camera = MC.gameRenderer.getMainCamera().getPosition();

        float r = (color >> 16 & 255) / 255.0F;
        float g = (color >> 8 & 255) / 255.0F;
        float b = (color & 255) / 255.0F;
        float a = 1.0F;

        VertexConsumer vertexConsumer = MC.renderBuffers().bufferSource().getBuffer(RenderType.LINES);

        poseStack.pushPose();
        poseStack.translate(-camera.x, -camera.y, -camera.z);
        LevelRenderer.renderLineBox(poseStack, vertexConsumer, minX + offset, minY + offset, minZ + offset, maxX - offset, maxY - offset, maxZ - offset, r, g, b, a);
        poseStack.popPose();
    }

    private static class Wireframe {
        private final float minX;
        private final float minY;
        private final float minZ;
        private final float maxX;
        private final float maxY;
        private final float maxZ;
        private final float offset;
        private final int color;
        private final float lifetime;
        private float age;

        public Wireframe(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, float offset, int color, float lifetime) {
            this.minX = minX;
            this.minY = minY;
            this.minZ = minZ;
            this.maxX = maxX;
            this.maxY = maxY;
            this.maxZ = maxZ;
            this.offset = offset;
            this.color = color;
            this.lifetime = lifetime;
            this.age = 0;
        }

        public void tick() {
            age++;
        }

        public boolean shouldRender() {
            return age < lifetime;
        }
    }
}