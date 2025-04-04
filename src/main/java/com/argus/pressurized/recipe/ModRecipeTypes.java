package com.argus.pressurized.recipe;

import com.argus.pressurized.Pressurized;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipeTypes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, Pressurized.MODID);
    public static final RegistryObject<RecipeType<CrucibleFurnaceRecipe>> CRUCIBLE_FURNACE = RECIPE_TYPES.register("crucible_furnace", () -> new RecipeType<CrucibleFurnaceRecipe>() {});

    public static void register(IEventBus eventBus) {
        RECIPE_TYPES.register(eventBus);
    }
}