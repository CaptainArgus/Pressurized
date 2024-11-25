package com.argus.pressurized.block.entity;

import com.argus.pressurized.Pressurized;
import com.argus.pressurized.block.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Pressurized.MODID);

    public static final RegistryObject<BlockEntityType<BoilerShellBlockEntity>> BOILER_SHELL_BE =
            BLOCK_ENTITIES.register("boiler_shell_be", () -> BlockEntityType.Builder.of(BoilerShellBlockEntity::new,
                    ModBlocks.BOILER_SHELL_BLOCK_TIER_1_DAMAGE_0.get(), ModBlocks.BOILER_SHELL_BLOCK_TIER_1_DAMAGE_1.get(),
                    ModBlocks.BOILER_SHELL_BLOCK_TIER_1_DAMAGE_2.get(), ModBlocks.BOILER_SHELL_BLOCK_TIER_1_DAMAGE_3.get(),
                    ModBlocks.BOILER_SHELL_BLOCK_TIER_1_DAMAGE_4.get()).build(null));
    public static final RegistryObject<BlockEntityType<BoilerCoreBlockEntity>> BOILER_CORE_BE =
            BLOCK_ENTITIES.register("boiler_core_be", () -> BlockEntityType.Builder.of(BoilerCoreBlockEntity::new,
                    ModBlocks.BOILER_CORE_BLOCK.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
