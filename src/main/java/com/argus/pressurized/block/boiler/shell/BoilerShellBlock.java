package com.argus.pressurized.block.boiler.shell;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraftforge.registries.RegistryObject;

public class BoilerShellBlock extends Block {

    public static final int MAX_HEALTH = 20;
    public static final IntegerProperty HEALTH = IntegerProperty.create("health", 1, 20);

    private static RegistryObject<Block> decayTarget;

    public BoilerShellBlock(Properties p_49795_, RegistryObject<Block> decayTarget) {
        super(p_49795_);
        BoilerShellBlock.decayTarget = decayTarget;
        this.registerDefaultState(this.stateDefinition.any().setValue(HEALTH, 20));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HEALTH);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(HEALTH, 20);
    }

    public static void subtractHealth(Level world, BlockPos pos, int amount, Player p) throws InstantiationException, IllegalAccessException {
        BlockState state = world.getBlockState(pos);
        int newHealth = Math.max(0, state.getValue(HEALTH) - amount);
        if (newHealth <= 0) {
            p.sendSystemMessage(Component.literal("health hit 0"));
            if (decayTarget != null) {
                p.sendSystemMessage(Component.literal("" + decayTarget.get().getName()));
                world.setBlock(pos, decayTarget.get().defaultBlockState(), 3);
            } else {
                p.sendSystemMessage(Component.literal("no block was found"));
                world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                // summon debris explosion
            }
            return;
        }
        world.setBlock(pos, state.setValue(HEALTH, newHealth), 3);
    }

    public static void addHealth(Level world, BlockPos pos, int amount) {
        BlockState state = world.getBlockState(pos);
        int newHealth = Math.min(MAX_HEALTH, state.getValue(HEALTH) + amount);
        world.setBlock(pos, state.setValue(HEALTH, newHealth), 3);
    }
}
