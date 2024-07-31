package com.argus.pressurized.client;

import com.argus.pressurized.Pressurized;
import com.argus.pressurized.block.VisualBlock;
import com.argus.pressurized.block.boiler.BoilerCoreBlock;
import com.argus.pressurized.client.render.WireframeRenderer;
import com.argus.pressurized.util.ModTags;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
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
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Level world = event.getLevel();
        BlockPos pos = event.getPos();
        if (!world.isClientSide && event.getHand() == InteractionHand.MAIN_HAND && world.getBlockState(pos).getBlock() instanceof BoilerCoreBlock) {
            BoilerCoreBlock block = (BoilerCoreBlock) world.getBlockState(event.getPos()).getBlock();

            //world.getBlockState(pos.relative(Direction.byName("north"), 1)).is(BlockTags.create(new ResourceLocation("pressurized", "boiler_shell")))
            //event.getEntity().sendSystemMessage(Component.literal("You right-clicked a BoilerCoreBlock!"));
            event.getEntity().sendSystemMessage(Component.literal("Behind block: " + world.getBlockState(pos.relative(world.getBlockState(pos).getValue(block.FACING).getOpposite())).is(ModTags.Blocks.BOILER_SHELL_BLOCKS)));

            int airBlockCount = BoilerCoreBlock.countAirBlocks(world, pos);
            if (airBlockCount > 1000) {
                event.getEntity().sendSystemMessage(Component.literal("The structure is too large: " + airBlockCount + " air blocks"));
                BlockPos blockPos = pos; //new BlockPos(100, 64, 100);

                float scale = 1.0F;
                int color = 0xFF0000; // Red color in hexadecimal
                float fadeInTime = 10.0F; // 10 ticks
                float lifetime = 100.0F; // 100 ticks
                float fadeOutTime = 10.0F; // 10 ticks

                WireframeRenderer.addWireframe(blockPos, scale, color, fadeInTime, lifetime, fadeOutTime);

            } else {
                event.getEntity().sendSystemMessage(Component.literal("Structure size: " + airBlockCount + " air blocks"));
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