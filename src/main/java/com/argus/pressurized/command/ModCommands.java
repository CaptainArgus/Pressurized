package com.argus.pressurized.command;

import com.argus.pressurized.Pressurized;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Pressurized.MODID)
public class ModCommands {
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        CommandBuildContext buildContext = event.getBuildContext();

        SummonAssemblyEntityCommand.register(dispatcher, buildContext);
        AddBlockToAssemblyCommand.register(dispatcher, buildContext);
        SetHeatCommand.register(dispatcher);
        GetHeatCommand.register(dispatcher);
    }
}
