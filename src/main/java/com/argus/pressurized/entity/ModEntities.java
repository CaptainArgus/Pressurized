package com.argus.pressurized.entity;

import com.argus.pressurized.Pressurized;
import com.argus.pressurized.entity.trains.RotatedSolidEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Pressurized.MODID);

    public static final RegistryObject<EntityType<?>> ROTATED_SOLID_ENTITY = ENTITIES.register("rotator",
            () -> EntityType.Builder.<RotatedSolidEntity>of(RotatedSolidEntity::new, MobCategory.MISC)
                    .sized(4.0F, 1.0F)
                    .build(new ResourceLocation(Pressurized.MODID, "rotator").toString())
    );

    public static void register(IEventBus eventBus) {
        ENTITIES.register(eventBus);
    }
}