package com.argus.pressurized.client;

import com.argus.pressurized.Pressurized;
import com.argus.pressurized.block.custom.BoilerCoreBlock;
import com.argus.pressurized.block.custom.BoilerShellBlock;
import com.argus.pressurized.block.custom.VisualBlock;
import com.argus.pressurized.block.entity.BoilerShellBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = Pressurized.MODID, value = Dist.CLIENT)
public class ClientEventHandler {
    public static final Minecraft MC = Minecraft.getInstance();
    public static boolean lookingAtVisualBlock = false;
    public static String visualBlockTranslationKey = "";

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) throws InstantiationException, IllegalAccessException {
        Level world = event.getLevel();
        BlockPos pos = event.getPos();
        Block block = world.getBlockState(pos).getBlock();
        if (!world.isClientSide && event.getHand() == InteractionHand.MAIN_HAND) {
            if (block instanceof BoilerCoreBlock coreBlock) {
                coreBlock.verifyBoilerStructure(event.getLevel(), event.getPos(), event.getEntity());
            } else if (block instanceof BoilerShellBlock) {
                BoilerShellBlockEntity boilerShellBlockEntity = (BoilerShellBlockEntity) world.getBlockEntity(pos);
                boilerShellBlockEntity.setHealth(boilerShellBlockEntity.getHealth() - 5);
            }
        }
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            updateBlockLooking();
        }
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiOverlayEvent.Post event) {
        if (lookingAtVisualBlock) {
            renderVisualBlockInfo(event.getGuiGraphics().pose());
        }
    }

    public static void updateBlockLooking() {
        lookingAtVisualBlock = false;
        if (MC.hitResult instanceof BlockHitResult blockHitResult)
            if (MC.level != null) {
                Block testBlock = MC.level.getBlockState(blockHitResult.getBlockPos()).getBlock();
                if (testBlock instanceof VisualBlock block) {
                    lookingAtVisualBlock = true;
                    visualBlockTranslationKey = block.getTranslationKey();
                    if (testBlock instanceof BoilerShellBlock) {
                        BoilerShellBlockEntity boilerShellBlockEntity = (BoilerShellBlockEntity) MC.level.getBlockEntity(blockHitResult.getBlockPos());
                        visualBlockTranslationKey = "Health: " + boilerShellBlockEntity.getHealth();
                    }
                }
            }
    }

    public static void renderVisualBlockInfo(PoseStack poseStack) {
        int screenWidth = MC.getWindow().getGuiScaledWidth();
        int screenHeight = MC.getWindow().getGuiScaledHeight();

        List<String> splitTooltip = new ArrayList<>();
        String[] lines = Component.translatable(visualBlockTranslationKey).getString().split("\n");
        for (String line : lines) {
            splitTooltip.add(line);
        }

        int tooltipHeight = splitTooltip.size() * MC.font.lineHeight;
        int tooltipX = (int) (screenWidth / 1.9);
        int tooltipY = screenHeight / 2 - tooltipHeight / 2;

        for (int i = 0; i < splitTooltip.size(); i++) {
            MC.font.drawInBatch(splitTooltip.get(i),
                    tooltipX,
                    tooltipY + i * 10,
                    0,
                    false,
                    poseStack.last().pose(),
                    MC.renderBuffers().bufferSource(),
                    Font.DisplayMode.NORMAL,
                    0,
                    15728880
            );
        }
    }
}