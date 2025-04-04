package com.argus.pressurized.recipe;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;

public class CrucibleFurnaceRecipe implements Recipe<Container> {
    private final ResourceLocation id;
    private final ItemStack input;
    private final FluidStack outputFluid;
    private final int requiredHeat;  // Heat required to process the item

    public CrucibleFurnaceRecipe(ResourceLocation id, ItemStack input, FluidStack outputFluid, int requiredHeat) {
        this.id = id;
        this.input = input;
        this.outputFluid = outputFluid;
        this.requiredHeat = requiredHeat;
    }

    @Override
    public boolean matches(Container pContainer, Level p_44003_) {
        return ItemStack.isSameItemSameTags(pContainer.getItem(0), input);  // Check if the input matches
    }

    @Override
    public ItemStack assemble(Container p_44001_, RegistryAccess p_267165_) {
        return ItemStack.EMPTY;  // Fluid is the output, not an item
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.CRUCIBLE_FURNACE.get();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.CRUCIBLE_FURNACE_RECIPE.get();  // Custom RecipeSerializer
    }

    @Override
    public boolean canCraftInDimensions(int p_43999_, int p_44000_) {
        return false;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess p_267052_) {
        return null;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    public FluidStack getOutputFluid() {
        return outputFluid;
    }

    public int getRequiredHeat() {
        return requiredHeat;
    }

    public ItemStack getInput() {
        return input;
    }
}