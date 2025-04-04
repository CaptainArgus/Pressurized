package com.argus.pressurized.gui.screen;

import com.argus.pressurized.Pressurized;
import com.argus.pressurized.gui.menu.CrucibleFurnaceMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class CrucibleFurnaceScreen extends AbstractContainerScreen<CrucibleFurnaceMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Pressurized.MODID, "textures/gui/crucible_furnace_gui.png");

    public CrucibleFurnaceScreen(CrucibleFurnaceMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        List<FluidStack> fluidStacks = new ArrayList<>(this.menu.getBlockEntity().getStoredFluids());

        int offset = 0;
        for (FluidStack stack : fluidStacks) {
            IClientFluidTypeExtensions fluidTypeExtensions = IClientFluidTypeExtensions.of(stack.getFluid());
            ResourceLocation stillTexture = fluidTypeExtensions.getStillTexture(stack);

            int fluidHeight = getFluidHeight(stack);

            if (stillTexture == null)
                return;

            TextureAtlasSprite sprite = this.minecraft.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(stillTexture);
            int tintColor = fluidTypeExtensions.getTintColor(stack);

            float alpha = ((tintColor >> 24) & 0xFF) / 255f;
            float red = ((tintColor >> 16) & 0xFF) / 255f;
            float green = ((tintColor >> 8) & 0xFF) / 255f;
            float blue = (tintColor & 0xFF) / 255f;

            guiGraphics.setColor(red, green, blue, alpha);

            //renderSpritePart(guiGraphics.pose(), sprite, 100, 50, 16, 16, 0);

            renderTiledSprite(guiGraphics.pose(), sprite, 89 + this.leftPos, 13 + 60 - fluidHeight - offset + this.topPos, 34, fluidHeight, 0);



            offset += fluidHeight;
        }

        guiGraphics.setColor(1.0f, 1.0f, 1.0f, 1.0f);

        guiGraphics.blit(TEXTURE, 89 + this.leftPos, 13 + this.topPos, 176, 0, 6, 60);
    }

    private static void renderTiledSprite(PoseStack poseStack, TextureAtlasSprite sprite, int x, int y, int width, int height, int z) {
        int spriteWidth = sprite.contents().width();
        int spriteHeight = sprite.contents().height();
        for (int i = 0; i < width; i += spriteWidth) {
            for (int j = 0; j < height; j += spriteHeight) {
                renderSpritePart(poseStack, sprite, i + x, j + y, Math.min(spriteWidth, width - i), Math.min(spriteHeight, height - j), 0);
            }
        }
    }

    public static void renderSpritePart(PoseStack poseStack, TextureAtlasSprite sprite, int x, int y, int width, int height, int z) {
        Tesselator tess = Tesselator.getInstance();
        BufferBuilder buffer = tess.getBuilder();
        RenderSystem.setShaderTexture(0, sprite.atlasLocation());

        float u0 = sprite.getU0();
        float v0 = sprite.getV0();
        float u1 = sprite.getU0() + (sprite.getU1() - sprite.getU0()) * (width / 16f);
        float v1 = sprite.getV0() + (sprite.getV1() - sprite.getV0()) * (height / 16f);

        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        Matrix4f matrix = poseStack.last().pose();

        buffer.vertex(matrix, x,         y + height, z).uv(u0, v1).endVertex();
        buffer.vertex(matrix, x + width, y + height, z).uv(u1, v1).endVertex();
        buffer.vertex(matrix, x + width, y,          z).uv(u1, v0).endVertex();
        buffer.vertex(matrix, x,         y,          z).uv(u0, v0).endVertex();

        tess.end();
    }

    private static int getFluidHeight(FluidStack stack) {
        return (int) (60 * ((float) stack.getAmount() / 8000));
    }

    private int getFluidY(int fluidHeight) {
        return this.topPos + 13 + (60 - fluidHeight);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {

        //guiGraphics.drawString(this.font, this.title, 8, 3, 4210752);
        //guiGraphics.drawString(this.font, this.playerInventoryTitle, 8, this.imageHeight - 91, 4210752);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
