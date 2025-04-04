package com.argus.pressurized;

import com.argus.pressurized.block.ModBlocks;
import com.argus.pressurized.blockentity.ModBlockEntities;
import com.argus.pressurized.capability.HeatCapability;
import com.argus.pressurized.capability.HeatCapabilityProvider;
import com.argus.pressurized.client.ClientEventHandler;
import com.argus.pressurized.client.render.ClientRenderEvent;
import com.argus.pressurized.client.render.WireframeRenderer;
import com.argus.pressurized.client.render.entity.ClientEntityRenderHandler;
import com.argus.pressurized.entity.ModEntities;
import com.argus.pressurized.item.ModItems;
import com.argus.pressurized.gui.screen.CrucibleFurnaceScreen;
import com.argus.pressurized.gui.menu.ModMenuTypes;
import com.argus.pressurized.network.ModPackets;
import com.argus.pressurized.recipe.ModRecipeSerializers;
import com.argus.pressurized.recipe.ModRecipeTypes;
import com.mojang.logging.LogUtils;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.RenderTypeHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.slf4j.Logger;

@Mod(Pressurized.MODID)
public class Pressurized {

    public static final String MODID = "pressurized";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Pressurized() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::clientSetup);
        modEventBus.addListener(this::commonSetup);

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModEntities.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        ModRecipeTypes.register(modEventBus);
        ModRecipeSerializers.register(modEventBus);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            ClientRenderEvent.registerHandlers();
        }

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(WireframeRenderer.class);
        MinecraftForge.EVENT_BUS.register(HeatCapabilityProvider.class);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    @SubscribeEvent
    public void clientSetup(final FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
        MinecraftForge.EVENT_BUS.register(new ClientEntityRenderHandler());

        MenuScreens.register(ModMenuTypes.CRUCIBLE_FURNACE_MENU.get(), CrucibleFurnaceScreen::new);
    }

    @SubscribeEvent
    public void commonSetup(final FMLCommonSetupEvent event) {
        System.out.println("REGISTERED PACKETS");
        ModPackets.register();
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.register(HeatCapability.class);
    }
}
