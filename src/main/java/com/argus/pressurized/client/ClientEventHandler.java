package com.argus.pressurized.client;

import com.argus.pressurized.Pressurized;
import com.argus.pressurized.block.VisualBlock;
import com.argus.pressurized.block.boiler.BoilerCoreBlock;
import com.argus.pressurized.block.boiler.shell.BoilerShellBlock;
import com.argus.pressurized.client.render.WireframeRenderer;
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

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) throws InstantiationException, IllegalAccessException {
        Level world = event.getLevel();
        BlockPos pos = event.getPos();
        Block block = world.getBlockState(pos).getBlock();
        if (!world.isClientSide && event.getHand() == InteractionHand.MAIN_HAND) {
            if (block instanceof BoilerCoreBlock) {
                BoilerCoreBlock.verifyBoilerStructure(event.getLevel(), event.getPos(), event.getEntity());
                BlockPos bp = pos.relative(world.getBlockState(pos).getValue(BoilerCoreBlock.FACING));
                WireframeRenderer.addWireframe(bp.getX(), bp.getY(), bp.getZ(), bp.getX() + 1, bp.getY() + 1,  bp.getZ() + 1, 0xFFFFFF, 10, 100, 10);
                //WireframeRendererTest.addWireframe(pos.relative(world.getBlockState(pos).getValue(BoilerCoreBlock.FACING)), 0.1f, 0xE32F08, 10, 100, 10);
            } else if (block instanceof BoilerShellBlock) {
                //event.getEntity().sendSystemMessage(Component.literal("clicked shell block"));
                BoilerShellBlock.subtractHealth(world, event.getPos(), 5, event.getEntity());
                event.getEntity().sendSystemMessage(Component.literal("removed health: "
                        + world.getBlockState(pos).getValue(BoilerShellBlock.HEALTH) + " left"));
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
            if (MC.level != null)
                if (MC.level.getBlockState(blockHitResult.getBlockPos()).getBlock() instanceof VisualBlock)
                    lookingAtVisualBlock = true;
    }

    public static void renderVisualBlockInfo(PoseStack poseStack) {
        int screenWidth = MC.getWindow().getGuiScaledWidth();
        int screenHeight = MC.getWindow().getGuiScaledHeight();

        List<String> splitTooltip = new ArrayList<>();
        String[] lines = Component.translatable("block.visualblock.inspect").getString().split("\n");
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