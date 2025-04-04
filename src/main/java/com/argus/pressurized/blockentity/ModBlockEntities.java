package com.argus.pressurized.blockentity;

import com.argus.pressurized.Pressurized;
import com.argus.pressurized.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Pressurized.MODID);

    public static final RegistryObject<BlockEntityType<CrucibleFurnaceBlockEntity>> CRUCIBLE_FURNACE_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("crucible_furnace_block_entity",
                    () -> BlockEntityType.Builder.of(CrucibleFurnaceBlockEntity::new, ModBlocks.CRUCIBLE_FURNACE_BLOCK.get())
                            .build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
