package com.argus.pressurized.network;

import com.argus.pressurized.blockentity.CrucibleFurnaceBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class SyncFluidsPacket {

    private final BlockPos pos;
    private final List<FluidStack> fluids;

    public SyncFluidsPacket(BlockPos pos, List<FluidStack> fluids) {
        this.pos = pos;
        this.fluids = fluids;
    }

    public SyncFluidsPacket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        int size = buf.readInt();
        this.fluids = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            this.fluids.add(FluidStack.readFromPacket(buf));
        }
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeInt(fluids.size());
        for (FluidStack stack : fluids) {
            stack.writeToPacket(buf);
        }
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            Level level = Minecraft.getInstance().level;
            if (level == null) return;

            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof CrucibleFurnaceBlockEntity crucible) {
                crucible.setStoredFluids(fluids);
            }
        });
        context.setPacketHandled(true);
    }
}
