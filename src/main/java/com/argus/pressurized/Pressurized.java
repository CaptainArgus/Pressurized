package com.argus.pressurized;

import com.argus.pressurized.block.ModBlocks;
import com.argus.pressurized.client.ClientEventHandler;
import com.argus.pressurized.client.render.ClientRenderEvent;
import com.argus.pressurized.client.render.WireframeRenderer;
import com.argus.pressurized.entity.ModEntities;
import com.argus.pressurized.item.ModItems;
import com.mojang.logging.LogUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.slf4j.Logger;

@Mod(Pressurized.MODID)
public class Pressurized {

    public static final String MODID = "pressurized";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Pressurized() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModEntities.register(modEventBus);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            ClientRenderEvent.registerHandlers();
        }

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(WireframeRenderer.class);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    @SubscribeEvent
    public void clientSetup(final FMLClientSetupEvent event) {
        LOGGER.info("CLIENT SETUP STARTED");
        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
        //ClientRenderEvent.registerHandlers(event);
    }
}
