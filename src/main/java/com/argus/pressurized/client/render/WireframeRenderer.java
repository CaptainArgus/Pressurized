package com.argus.pressurized.client.render;

import com.argus.pressurized.Pressurized;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.*;

@Mod.EventBusSubscriber(modid = Pressurized.MODID, value = Dist.CLIENT)
public class WireframeRenderer {

    private static List<Wireframe> wireframes = new ArrayList<>();

    @SubscribeEvent
    public static void onRenderWorldLast(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null || mc.level == null) {
                return;
            }

            for (Wireframe wireframe : wireframes) {
                if (wireframe.shouldRender()) {
                    renderWireframe(event.getPoseStack(), wireframe.minX, wireframe.minY, wireframe.minZ, wireframe.maxX, wireframe.maxY, wireframe.maxZ, wireframe.color);
                }
            }
        }
    }

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

    public static void addWireframe(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, int color, float fadeInTime, float lifetime, float fadeOutTime) {
        wireframes.add(new Wireframe(minX, minY, minZ, maxX, maxY, maxZ, color, fadeInTime, lifetime, fadeOutTime));
        Pressurized.LOGGER.info("Adding wireframe");
    }

    /*
    public static void removeWireframe(BlockPos pos) {
        wireframes.removeIf(wireframe -> wireframe.pos.equals(pos));
        Pressurized.LOGGER.info("Removing wireframe");
    }
     */

    public static void renderWireframe(PoseStack poseStack, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, int color) {
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
        drawBox(vertexConsumer, minX, minY, minZ, maxX, maxY, maxZ, poseStack, color);
        poseStack.popPose();

        RenderSystem.depthMask(true);
        MC.renderBuffers().bufferSource().endBatch(RenderType.LINES);
    }

    private static void drawBox(VertexConsumer buffer, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, PoseStack stack, int color) {
        float red = (color >> 16 & 255) / 255.0F;
        float green = (color >> 8 & 255) / 255.0F;
        float blue = (color & 255) / 255.0F;
        float alpha = 1.0F;

        //bottom
        drawLine(buffer, stack, minX, minY, minZ, maxX, minY, minZ, red, green, blue, alpha);
        drawLine(buffer, stack, maxX, minY, minZ, maxX, minY, maxZ, red, green, blue, alpha);
        drawLine(buffer, stack, maxX, minY, maxZ, minX, minY, maxZ, red, green, blue, alpha);
        drawLine(buffer, stack, minX, minY, maxZ, minX, minY, minZ, red, green, blue, alpha);

        //top
        drawLine(buffer, stack, minX, maxY, minZ, maxX, maxY, minZ, red, green, blue, alpha);
        drawLine(buffer, stack, maxX, maxY, minZ, maxX, maxY, maxZ, red, green, blue, alpha);
        drawLine(buffer, stack, maxX, maxY, maxZ, minX, maxY, maxZ, red, green, blue, alpha);
        drawLine(buffer, stack, minX, maxY, maxZ, minX, maxY, minZ, red, green, blue, alpha);

        //vertical
        drawLine(buffer, stack, minX, minY, minZ, minX, maxY, minZ, red, green, blue, alpha);
        drawLine(buffer, stack, maxX, minY, minZ, maxX, maxY, minZ, red, green, blue, alpha);
        drawLine(buffer, stack, maxX, minY, maxZ, maxX, maxY, maxZ, red, green, blue, alpha);
        drawLine(buffer, stack, minX, minY, maxZ, minX, maxY, maxZ, red, green, blue, alpha);
    }

    private static void drawLine(VertexConsumer buffer, PoseStack stack, float x1, float y1, float z1, float x2, float y2, float z2, float r, float g, float b, float a) {
        Matrix4f matrix4f = stack.last().pose();
        Matrix3f matrix3f = stack.last().normal();
        Vec3 normal = new Vec3(x2 - x1, y2 - y1, z2 - z1).normalize();
        buffer.vertex(matrix4f, x1, y1, z1).color(r, g, b, a).normal(matrix3f, (float) normal.x, (float) normal.y, (float) normal.z).endVertex();
        buffer.vertex(matrix4f, x2, y2, z2).color(r, g, b, a).normal(matrix3f, (float) normal.x, (float) normal.y, (float) normal.z).endVertex();
    }

    private static class Wireframe {
        private final float minX;
        private final float minY;
        private final float minZ;
        private final float maxX;
        private final float maxY;
        private final float maxZ;
        private final int color;
        private final float fadeInTime;
        private final float lifetime;
        private final float fadeOutTime;
        private float age;

        public Wireframe(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, int color, float fadeInTime, float lifetime, float fadeOutTime) {
            this.minX = minX;
            this.minY = minY;
            this.minZ = minZ;
            this.maxX = maxX;
            this.maxY = maxY;
            this.maxZ = maxZ;
            this.color = color;
            this.fadeInTime = fadeInTime;
            this.lifetime = lifetime;
            this.fadeOutTime = fadeOutTime;
            this.age = 0;
        }

        public void tick() {
            age++;
        }

        public boolean shouldRender() {
            return age < fadeInTime + lifetime + fadeOutTime;
        }
    }
}