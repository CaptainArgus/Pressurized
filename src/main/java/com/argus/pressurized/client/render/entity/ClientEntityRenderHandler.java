package com.argus.pressurized.client.render.entity;

import com.argus.pressurized.Pressurized;
import com.argus.pressurized.entity.ModEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Pressurized.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
@OnlyIn(Dist.CLIENT)
public class ClientEntityRenderHandler {
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        Pressurized.LOGGER.info("REGISTERED ENTITY RENDERERS");
        event.registerEntityRenderer(ModEntities.ASSEMBLY_ENTITY.get(), AssemblyEntityRenderer::new);
        event.registerEntityRenderer(ModEntities.COLLISION_TEST_ENTITY.get(), CollisionTestEntityRenderer::new);
    }
}
