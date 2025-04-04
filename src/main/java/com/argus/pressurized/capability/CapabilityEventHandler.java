package com.argus.pressurized.capability;

import com.argus.pressurized.Pressurized;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Pressurized.MODID)
public class CapabilityEventHandler {
    private static final ResourceLocation HEAT_CAP_ID = new ResourceLocation(Pressurized.MODID, "heat");

    @SubscribeEvent
    public static void attachCapabilities(AttachCapabilitiesEvent<ItemStack> event) {
        ItemStack stack = event.getObject();
        HeatCapabilityProvider provider = new HeatCapabilityProvider(stack);

        if (stack.hasTag()) {
            provider.deserializeNBT(stack.getTag().getCompound("HeatCapability"));
        }

        event.addCapability(HEAT_CAP_ID, provider);
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        ModCapabilities.registerCapabilities(event);
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();

        // Check if the ItemStack has a Heat value in its NBT
        if (stack.hasTag() && stack.getTag().contains("Heat")) {
            int heatValue = stack.getTag().getInt("Heat");

            // Append the Heat value to the tooltip
            event.getToolTip().add(Component.literal("Heat: " + heatValue));
        }
    }
}