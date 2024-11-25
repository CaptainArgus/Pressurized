package com.argus.pressurized.block.custom;

import com.argus.pressurized.Config;
import com.argus.pressurized.block.ModBlocks;
import com.argus.pressurized.block.entity.BoilerCoreBlockEntity;
import com.argus.pressurized.client.render.WireframeRenderer;
import com.argus.pressurized.util.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class BoilerCoreBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public BoilerCoreBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    public void verifyBoilerStructure(Level world, BlockPos pos, Player player) {
        BlockPos.MutableBlockPos checkPos = new BlockPos.MutableBlockPos();
        BlockPos behindCore = pos.relative(world.getBlockState(pos).getValue(FACING).getOpposite());
        BoilerCoreBlockEntity boilerCoreBlockEntity = (BoilerCoreBlockEntity) world.getBlockEntity(pos);

        int minX = behindCore.getX(), maxX = behindCore.getX();
        int minY = behindCore.getY(), maxY = behindCore.getY();
        int minZ = behindCore.getZ(), maxZ = behindCore.getZ();

        for (Direction dir : Direction.values()) {
            for (int i = 1; i <= Config.maxBoilerSize + 1; i++) {
                if (i > Config.maxBoilerSize) {
                    //player.sendSystemMessage(Component.literal("OVERSIZED: " + dir.getName()));
                }
                checkPos.set(behindCore).move(dir, i);
                if (!world.getBlockState(behindCore.relative(dir, i)).is(ModTags.Blocks.BOILER_INTERIOR_BLOCKS)) {
                    break;
                }

                if (dir == Direction.EAST) maxX = checkPos.getX();
                if (dir == Direction.WEST) minX = checkPos.getX();
                if (dir == Direction.UP) maxY = checkPos.getY();
                if (dir == Direction.DOWN) minY = checkPos.getY();
                if (dir == Direction.SOUTH) maxZ = checkPos.getZ();
                if (dir == Direction.NORTH) minZ = checkPos.getZ();
            }
        }

        int totalSpace = (maxX - minX + 1) * (maxY - minY + 1) * (maxZ - minZ + 1);
        if (totalSpace < 1) {
            //TOO SMALL
            player.sendSystemMessage(Component.literal("TOO SMALL: " + totalSpace));
            return;
        }

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    if (!world.getBlockState(new BlockPos(x, y, z)).is(ModTags.Blocks.BOILER_INTERIOR_BLOCKS)) {
                        //NOT HOLLOW
                        WireframeRenderer.addWireframe(x, y, z, x + 1, y + 1, z + 1, -0.01f, 0xFF0000, 100);
                        player.sendSystemMessage(Component.literal("FOUND ILLEGAL BLOCK: " + x + " " + y + " " + z));
                        return;
                    }
                }
            }
        }

        for (int x = minX - 1; x <= maxX + 1; x++) {
            for (int y = minY - 1; y <= maxY + 1; y++) {
                for (int z = minZ - 1; z <= maxZ + 1; z++) {
                    BlockPos check = new BlockPos(x, y, z);
                    if (x == minX - 1 || x == maxX + 1 || y == minY - 1 || y == maxY + 1 || z == minZ - 1 || z == maxZ + 1 && !check.equals(pos)) {
                        if (!world.getBlockState(check).is(ModTags.Blocks.BOILER_SHELL_BLOCKS)) {
                            //NOT MADE OF CORRECT MATERIAL
                            player.sendSystemMessage(Component.literal("FOUND A NON-SHELL BLOCK: " + x + " " + y + " " + z));
                            WireframeRenderer.addWireframe(x, y, z, x + 1, y + 1, z + 1, -0.001f, 0xFF0000, 100);
                            boilerCoreBlockEntity.setBlockPosList(new ArrayList<>());
                            return;
                        } else if (world.getBlockState(check).is(ModBlocks.BOILER_CORE_BLOCK.get())) {
                            //MORE THAN 1 CORE BLOCK
                            player.sendSystemMessage(Component.literal("FOUND EXTRA CORE BLOCK: " + x + " " + y + " " + z));
                            WireframeRenderer.addWireframe(x, y, z, x, y + 1, z + 1, -0.001f, 0xFF0000, 100);
                            boilerCoreBlockEntity.setBlockPosList(new ArrayList<>());
                            return;
                        }
                        boilerCoreBlockEntity.addShellBlock(check);
                    }
                }
            }
        }
        boilerCoreBlockEntity.setAssembled(true);
        player.sendSystemMessage(Component.literal("SUCCESS"));
    }

    private static boolean isWithinStructure(Level world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return state.isAir() || state.is(BlockTags.create(new ResourceLocation("pressurized", "boiler_shell")));
    }

    @Override
    public RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new BoilerCoreBlockEntity(blockPos, blockState);
    }
}
