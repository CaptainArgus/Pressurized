package com.argus.pressurized.block.boiler;

import com.argus.pressurized.Config;
import com.argus.pressurized.block.ModBlocks;
import com.argus.pressurized.util.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public class BoilerCoreBlock extends Block {
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

    public static boolean verifyBoilerStructure(Level world, BlockPos pos, Player player) {
        BlockPos.MutableBlockPos checkPos = new BlockPos.MutableBlockPos();
        BlockPos behindCore = pos.relative(world.getBlockState(pos).getValue(FACING).getOpposite());

        int minX = behindCore.getX(), maxX = behindCore.getX();
        int minY = behindCore.getY(), maxY = behindCore.getY();
        int minZ = behindCore.getZ(), maxZ = behindCore.getZ();

        for (Direction dir : Direction.values()) {
            for (int i = 1; i <= Config.maxBoilerSize + 1; i++) {
                if (i > Config.maxBoilerSize) {
                    player.sendSystemMessage(Component.literal("OVERSIZED: " + dir.getName()));
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
        //world.setBlock(new BlockPos(minX, minY, minZ), Blocks.GRANITE.defaultBlockState(), 3);

        int totalSpace = (maxX - minX + 1) * (maxY - minY + 1) * (maxZ - minZ + 1);
        if (totalSpace < 1) {
            //TOO SMALL
            player.sendSystemMessage(Component.literal("TOO SMALL: " + totalSpace));
            return false;
        }
        player.sendSystemMessage(Component.literal("TOTAL SPACE: " + totalSpace));

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    if (!world.getBlockState(new BlockPos(x, y, z)).is(ModTags.Blocks.BOILER_INTERIOR_BLOCKS)) {
                        //NOT HOLLOW
                        player.sendSystemMessage(Component.literal("FOUND ILLEGAL BLOCK: " + x + " " + y + " " + z));
                        return false;
                    }
                }
            }
        }

        for (int x = minX - 1; x <= maxX + 1; x++) {
            for (int y = minY - 1; y <= maxY + 1; y++) {
                for (int z = minZ - 1; z <= maxZ + 1; z++) {
                    if (x == minX - 1 || x == maxX + 1 || y == minY - 1 || y == maxY + 1 || z == minZ - 1 || z == maxZ + 1) {
                        if (!world.getBlockState(new BlockPos(x, y, z)).is(ModTags.Blocks.BOILER_SHELL_BLOCKS)) {
                            //NOT MADE OF CORRECT MATERIAL
                            player.sendSystemMessage(Component.literal("FOUND A NON-SHELL BLOCK: " + x + " " + y + " " + z));
                            return false;
                        } else if (world.getBlockState(new BlockPos(x, y, z)).is(ModBlocks.BOILER_CORE_BLOCK.get()) && !pos.equals(new BlockPos(x, y, z))) {
                            //MORE THAN 1 CORE BLOCK
                            player.sendSystemMessage(Component.literal("FOUND EXTRA CORE BLOCK: " + x + " " + y + " " + z));
                            return false;
                        }
                    }
                }
            }
        }
        player.sendSystemMessage(Component.literal("SUCCESS"));
        return true;
    }

    private static boolean isWithinStructure(Level world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return state.isAir() || state.is(BlockTags.create(new ResourceLocation("pressurized", "boiler_shell")));
    }
}
