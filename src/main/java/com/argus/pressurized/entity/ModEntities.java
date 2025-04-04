package com.argus.pressurized.entity;

import com.argus.pressurized.Pressurized;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Pressurized.MODID);

    public static final RegistryObject<EntityType<AssemblyEntity>> ASSEMBLY_ENTITY = ENTITIES.register("assembly_entity",
            () -> EntityType.Builder.of(AssemblyEntity::new, MobCategory.MISC).sized(1.0f, 1.0f).build("assembly_entity"));
    public static final RegistryObject<EntityType<CollisionTestEntity>> COLLISION_TEST_ENTITY = ENTITIES.register("collision_test_entity",
            () -> EntityType.Builder.of(CollisionTestEntity::new, MobCategory.MISC).sized(1.0f, 1.0f).build("collision_test_entity"));

    public static void register(IEventBus eventBus) {
        ENTITIES.register(eventBus);
    }
}