package com.argus.pressurized.client.render.entity;


import com.argus.pressurized.entity.AssemblyEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class AssemblyEntityRenderer extends EntityRenderer<AssemblyEntity> {
    public AssemblyEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(AssemblyEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);

        BlockRenderDispatcher blockRenderDispatcher = Minecraft.getInstance().getBlockRenderer();

        //poseStack.pushPose();
        //poseStack.mulPose(Axis.YP.rotationDegrees(entity.getYRot()));

        for (Map.Entry<BlockPos, BlockState> entry : entity.getBlockMap().entrySet()) {
            BlockPos pos = entry.getKey();
            BlockState state = entry.getValue();

            poseStack.pushPose();
            poseStack.mulPose(Axis.YP.rotationDegrees(-entity.getYRot()));
            poseStack.translate(pos.getX() - 0.5, pos.getY(), pos.getZ() - 0.5);

            blockRenderDispatcher.renderSingleBlock(state, poseStack, buffer, packedLight, OverlayTexture.NO_OVERLAY);

            poseStack.popPose();
        }
        //poseStack.popPose();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(AssemblyEntity rotatedSolidEntity) {
        return null;
    }
}