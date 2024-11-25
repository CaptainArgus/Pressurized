package com.argus.pressurized.block.entity;

import com.argus.pressurized.block.custom.BoilerShellBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class BoilerShellBlockEntity extends BlockEntity {

    private int health = 20;

    public BoilerShellBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.BOILER_SHELL_BE.get(), blockPos, blockState);
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
        if (health <= 0 && this.level != null) {
            BoilerShellBlock block = (BoilerShellBlock) this.level.getBlockState(this.worldPosition).getBlock();
            if (block.decayBlock != null) {
                this.level.setBlock(this.worldPosition, block.decayBlock.get().defaultBlockState(), 3);
            } else {
                //EXPLOSION
                this.level.setBlock(this.worldPosition, Blocks.AIR.defaultBlockState(), 3);
            }
        }
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        if (compound.contains("health")) {
            this.health = compound.getInt("health");
        }
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        compound.putInt("health", this.health);
    }
}
