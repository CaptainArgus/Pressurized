package com.argus.pressurized.block.boiler;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

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

    public static int countAirBlocks(Level world, BlockPos startPos) {
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new LinkedList<>();
        queue.add(startPos.relative(world.getBlockState(startPos).getValue(FACING).getOpposite())); // Assuming FACING is a property you have
        int airBlockCount = 0;

        while (!queue.isEmpty()) {
            BlockPos currentPos = queue.poll();
            if (visited.contains(currentPos)) continue;
            visited.add(currentPos);

            if (world.getBlockState(currentPos).isAir()) { //.is(BlockTags.create(new ResourceLocation("pressurized:air")))) {
                airBlockCount++;
                if (airBlockCount > 1000) return airBlockCount;

                for (Direction direction : Direction.values()) {
                    BlockPos neighborPos = currentPos.relative(direction);
                    if (!visited.contains(neighborPos) && isWithinStructure(world, neighborPos)) {
                        queue.add(neighborPos);
                    }
                }
            }
        }

        return airBlockCount;
    }

    private static boolean isWithinStructure(Level world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return state.isAir() || state.is(BlockTags.create(new ResourceLocation("pressurized", "boiler_shell")));
    }
}
