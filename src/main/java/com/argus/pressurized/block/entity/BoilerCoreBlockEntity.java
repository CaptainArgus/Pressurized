package com.argus.pressurized.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import java.util.ArrayList;
import java.util.List;

public class BoilerCoreBlockEntity extends BlockEntity {

    private List<BlockPos> shellBlocks;
    private boolean assembled;
    private FluidTank waterTank;
    private FluidTank steamTank;
    private int maxWater;
    private int maxSteam;

    public BoilerCoreBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.BOILER_CORE_BE.get(), blockPos, blockState);
        this.shellBlocks = new ArrayList<>();
        assembled = false;
        this.waterTank = new FluidTank(maxWater, fluidStack -> fluidStack.getFluid() == Fluids.WATER);
        this.steamTank = new FluidTank(maxSteam, fluidStack -> fluidStack.getFluid() == Fluids.LAVA);
    }

    public boolean isAssembled() {
        return assembled;
    }

    public void setAssembled(boolean assembled) {
        this.assembled = assembled;
    }

    public List<BlockPos> getBlockPosList() {
        return shellBlocks;
    }

    public void setBlockPosList(List<BlockPos> shellBlocks) {
        this.shellBlocks = shellBlocks;
    }

    public void addShellBlock(BlockPos pos) {
        shellBlocks.add(pos);
    }

    public void removeShellBlock(BlockPos pos) {
        shellBlocks.remove(pos);
    }

    public void setMaxWater(int maxWater) {
        this.maxWater = maxWater;
        this.waterTank.setCapacity(maxWater);
    }

    public void setMaxSteam(int maxSteam) {
        this.maxSteam = maxSteam;
        this.steamTank.setCapacity(maxSteam);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("BlockPosList")) {
            shellBlocks.clear();
            ListTag listTag = tag.getList("BlockPosList", Tag.TAG_COMPOUND);
            for (Tag element : listTag) {
                shellBlocks.add(BlockPos.of(((CompoundTag) element).getLong("Pos")));
            }
        }
        if (tag.contains("Assembled")) assembled = tag.getBoolean("Assembled");
        if (tag.contains("WaterTank")) waterTank.readFromNBT(tag.getCompound("WaterTank"));
        if (tag.contains("SteamTank")) steamTank.readFromNBT(tag.getCompound("SteamTank"));
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ListTag listTag = new ListTag();
        for (BlockPos pos : shellBlocks) {
            CompoundTag posTag = new CompoundTag();
            posTag.putLong("Pos", pos.asLong());
            listTag.add(posTag);
        }
        tag.put("BlockPosList", listTag);
        tag.putBoolean("Assembled", assembled);
        tag.put("WaterTank", waterTank.writeToNBT(new CompoundTag()));
        tag.put("SteamTank", steamTank.writeToNBT(new CompoundTag()));
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        load(tag);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        return super.getCapability(cap, side);
    }
}
