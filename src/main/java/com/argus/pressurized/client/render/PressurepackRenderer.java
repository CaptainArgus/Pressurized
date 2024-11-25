package com.argus.pressurized.client.render;

import com.argus.pressurized.Pressurized;
import com.argus.pressurized.client.model.PressurepackModel;
import com.argus.pressurized.item.ModItems;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class PressurepackRenderer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    private static final ResourceLocation PRESSUREPACK_TEXTURE = new ResourceLocation(Pressurized.MODID, "textures/item/pressurepack.png");
    private final PressurepackModel<AbstractClientPlayer> pressurepackModel;

    public PressurepackRenderer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> parent, EntityModelSet modelSet) {
        super(parent);
        this.pressurepackModel = new PressurepackModel<>(modelSet.bakeLayer(ClientRenderEvent.PRESSUREPACK_LAYER));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        //Pressurized.LOGGER.info("<DEBUG> RENDERING");
        ItemStack itemstack = player.getItemBySlot(EquipmentSlot.CHEST);
        if (true) { //itemstack.getItem() == ModItems.PRESSURE_CYLINDER_ITEM.get()) {
            //Pressurized.LOGGER.info("<DEBUG> found item, rendering");
            poseStack.pushPose();
            this.getParentModel().body.translateAndRotate(poseStack);
            poseStack.translate(0.0D, 0.0D, 0.125D);
            poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
            this.pressurepackModel.setupAnim(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            this.pressurepackModel.renderToBuffer(poseStack, bufferSource.getBuffer(RenderType.entityCutoutNoCull(PRESSUREPACK_TEXTURE)), packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            poseStack.popPose();
        }
    }
}