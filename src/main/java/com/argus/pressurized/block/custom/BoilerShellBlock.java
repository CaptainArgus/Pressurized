package com.argus.pressurized.block.custom;

import com.argus.pressurized.block.entity.BoilerShellBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

public class BoilerShellBlock extends BaseEntityBlock implements VisualBlock {

    private String visualBlockTranslationKey = "";
    public RegistryObject<Block> decayBlock;

    public BoilerShellBlock(Properties properties, RegistryObject<Block> decayBlock) {
        super(properties);
        this.decayBlock = decayBlock;
    }

    @Override
    public RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new BoilerShellBlockEntity(blockPos, blockState);
    }

    @Override
    public String getTranslationKey() {
        return visualBlockTranslationKey;
    }

    @Override
    public void setTranslationKey(String key) {
        visualBlockTranslationKey = key;
    }
}
