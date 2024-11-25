package com.argus.pressurized.client.render;

import com.argus.pressurized.Pressurized;
import com.argus.pressurized.client.model.PressurepackModel;
import com.argus.pressurized.entity.ModEntities;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ClientRenderEvent {
    public static final ModelLayerLocation PRESSUREPACK_LAYER = new ModelLayerLocation(new ResourceLocation(Pressurized.MODID, "backpack"), "main");

    public static void registerHandlers() {
        Pressurized.LOGGER.info("<DEBUG> REGISTERING RENDERERS");
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(ClientRenderEvent::registerLayer);
        modBus.addListener(ClientRenderEvent::registerEntityRenderers);
        modBus.addListener(ClientRenderEvent::onRegisterRenderers);
    }

    public static void registerLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
        Pressurized.LOGGER.info("<DEBUG> REGISTERING LAYER");
        event.registerLayerDefinition(PRESSUREPACK_LAYER, PressurepackModel::createLayer);
    }

    public static void registerEntityRenderers(EntityRenderersEvent.AddLayers event) {
        Pressurized.LOGGER.info("<DEBUG> REGISTERING PLAYER RENDERER LAYER");
        for (String skin : event.getSkins()) {
            PlayerRenderer playerRenderer = event.getSkin(skin);
            playerRenderer.addLayer(new PressurepackRenderer(playerRenderer, event.getEntityModels()));
        }
    }

    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        Pressurized.LOGGER.info("<DEBUG> REGISTERING ENTITY RENDERER");
        event.registerEntityRenderer(ModEntities.BLOCK_COLLECTION_ENTITY.get(), BlockCollectionEntityRenderer::new);
    }
}