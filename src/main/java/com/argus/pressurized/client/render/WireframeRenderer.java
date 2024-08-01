package com.argus.pressurized.client.render;

import com.argus.pressurized.Pressurized;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@Mod.EventBusSubscriber(modid = Pressurized.MODID, value = Dist.CLIENT)
public class WireframeRenderer {

    private static final Set<Wireframe> wireframes = new HashSet<>();

    @SubscribeEvent
    public static void onRenderWorldLast(RenderLevelStageEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) {
            return;
        }

        PoseStack poseStack = event.getPoseStack();

        for (Wireframe wireframe : wireframes) {
            if (wireframe.shouldRender()) {
                renderWireframe(poseStack, wireframe.pos, wireframe.scale, wireframe.color);
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

    public static void addWireframe(BlockPos pos, float scale, int color, float fadeInTime, float lifetime, float fadeOutTime) {
        wireframes.add(new Wireframe(pos, scale, color, fadeInTime, lifetime, fadeOutTime));
        Pressurized.LOGGER.info("Adding wireframe");
    }

    public static void removeWireframe(BlockPos pos) {
        wireframes.removeIf(wireframe -> wireframe.pos.equals(pos));
        Pressurized.LOGGER.info("Removing wireframe");
    }

    public static void renderWireframe(PoseStack poseStack, BlockPos pos, float scale, int color) {
        Minecraft minecraft = Minecraft.getInstance();
        Vec3 cameraPos = minecraft.gameRenderer.getMainCamera().getPosition();
        AABB box = new AABB(pos).inflate(scale).move(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        RenderSystem.setShader(GameRenderer::getRendertypeLinesShader);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.lineWidth(3.0F);
        RenderSystem.depthMask(false);

        VertexConsumer buffer = minecraft.renderBuffers().bufferSource().getBuffer(RenderType.LINES);

        drawBox(buffer, box, poseStack, color);

        RenderSystem.depthMask(true);
        minecraft.renderBuffers().bufferSource().endBatch(RenderType.LINES);
    }

    private static void drawBox(VertexConsumer buffer, AABB box, PoseStack stack, int color) {
        float red = (color >> 16 & 255) / 255.0F;
        float green = (color >> 8 & 255) / 255.0F;
        float blue = (color & 255) / 255.0F;
        float alpha = 1.0F;

        float minX = (float) box.minX;
        float minY = (float) box.minY;
        float minZ = (float) box.minZ;
        float maxX = (float) box.maxX;
        float maxY = (float) box.maxY;
        float maxZ = (float) box.maxZ;

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
        private final BlockPos pos;
        private final float scale;
        private final int color;
        private final float fadeInTime;
        private final float lifetime;
        private final float fadeOutTime;
        private float age;

        public Wireframe(BlockPos pos, float scale, int color, float fadeInTime, float lifetime, float fadeOutTime) {
            this.pos = pos;
            this.scale = scale;
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