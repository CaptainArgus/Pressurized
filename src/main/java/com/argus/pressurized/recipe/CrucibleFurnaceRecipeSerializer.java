package com.argus.pressurized.recipe;

import com.argus.pressurized.Pressurized;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

public class CrucibleFurnaceRecipeSerializer implements RecipeSerializer<CrucibleFurnaceRecipe> {

    @Override
    public CrucibleFurnaceRecipe fromJson(ResourceLocation pRecipeId, JsonObject pJson) {
        Pressurized.LOGGER.info("Loading recipe: " + pRecipeId);
        JsonArray ingredientsArray = pJson.getAsJsonArray("ingredients");
        ItemStack input = ItemStack.EMPTY;
        for (JsonElement element : ingredientsArray) {
            JsonObject ingredientObj = element.getAsJsonObject();
            input = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(ingredientObj.get("item").getAsString())));
        }

        FluidStack output = FluidStack.EMPTY;
        if (pJson.has("output")) {
            JsonObject outputObj = pJson.getAsJsonObject("output");
            Fluid fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(outputObj.get("fluid").getAsString()));
            int amount = outputObj.get("amount").getAsInt();
            output = new FluidStack(fluid, amount);
        }

        int requiredHeat = pJson.get("heat_required").getAsInt();

        return new CrucibleFurnaceRecipe(pRecipeId, input, output, requiredHeat);
    }

    @Override
    public @Nullable CrucibleFurnaceRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
        // Deserialize the recipe from network
        return null;
    }

    @Override
    public void toNetwork(FriendlyByteBuf pBuffer, CrucibleFurnaceRecipe pRecipe) {
        // Serialize the recipe to network
    }
}