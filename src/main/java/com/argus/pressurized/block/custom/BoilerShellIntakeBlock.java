package com.argus.pressurized.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BoilerShellIntakeBlock extends BaseEntityBlock implements VisualBlock {

    private String visualBlockTranslationKey = "";

    protected BoilerShellIntakeBlock(Properties p_49224_) {
        super(p_49224_);
    }

    @Override
    public String getTranslationKey() {
        return visualBlockTranslationKey;
    }

    @Override
    public void setTranslationKey(String key) {
        visualBlockTranslationKey = key;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return null;
    }

    @Override
    public RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.MODEL;
    }
}
