package com.argus.pressurized.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HeatCapability implements IHeatCapability, ICapabilitySerializable<CompoundTag> {
    private final ItemStack stack; // Reference to the item this capability belongs to
    private int heat;

    public HeatCapability(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public void setHeat(int heat) {
        this.heat = heat;
        // Update the ItemStack's NBT for synchronization
        stack.getOrCreateTag().put("HeatCapability", serializeNBT());
    }

    @Override
    public int getHeat() {
        return this.heat;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("Heat", heat);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.heat = nbt.getInt("Heat");
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return null;
    }
}