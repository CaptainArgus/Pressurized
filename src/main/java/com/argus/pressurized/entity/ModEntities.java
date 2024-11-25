package com.argus.pressurized.entity;

import com.argus.pressurized.Pressurized;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Pressurized.MODID);

    public static final RegistryObject<EntityType<BlockCollectionEntity>> BLOCK_COLLECTION_ENTITY = ENTITY_TYPES.register("block_collection",
            () -> EntityType.Builder.<BlockCollectionEntity>of(BlockCollectionEntity::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .build(new ResourceLocation(Pressurized.MODID, "block_collection").toString()));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}