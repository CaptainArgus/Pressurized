package com.argus.pressurized.client.render;

import com.argus.pressurized.entity.BlockCollectionEntity;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class BlockCollectionEntityRenderer extends EntityRenderer<BlockCollectionEntity> {

    public BlockCollectionEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(BlockCollectionEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);

        BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();

        poseStack.pushPose();
        poseStack.scale(entity.getScale(), entity.getScale(), entity.getScale());
        poseStack.mulPose(Axis.XP.rotationDegrees(entity.getRotationX()));
        poseStack.mulPose(Axis.YP.rotationDegrees(entity.getRotationY()));
        poseStack.mulPose(Axis.ZP.rotationDegrees(entity.getRotationZ()));

        for (Map.Entry<BlockPos, BlockState> entry : entity.getBlockMap().entrySet()) {
            BlockPos pos = entry.getKey();
            BlockState state = entry.getValue();

            poseStack.pushPose();
            poseStack.translate(pos.getX(), pos.getY(), pos.getZ());

            blockRenderer.renderSingleBlock(state, poseStack, buffer, packedLight, OverlayTexture.NO_OVERLAY);

            poseStack.popPose();
        }

        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(BlockCollectionEntity p_114482_) {
        return null;
    }
}
