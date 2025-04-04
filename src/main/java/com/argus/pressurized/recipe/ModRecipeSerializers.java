package com.argus.pressurized.recipe;

import com.argus.pressurized.Pressurized;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Pressurized.MODID);
    public static final RegistryObject<RecipeSerializer<CrucibleFurnaceRecipe>> CRUCIBLE_FURNACE_RECIPE = RECIPE_SERIALIZERS.register("crucible_furnace", CrucibleFurnaceRecipeSerializer::new);

    public static void register(IEventBus eventBus) {
        RECIPE_SERIALIZERS.register(eventBus);
    }
}
