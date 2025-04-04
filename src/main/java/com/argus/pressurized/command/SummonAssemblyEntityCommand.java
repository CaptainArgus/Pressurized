package com.argus.pressurized.command;

import com.argus.pressurized.entity.AssemblyEntity;
import com.argus.pressurized.entity.ModEntities;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class SummonAssemblyEntityCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext commandBuildContext) {
        dispatcher.register(Commands.literal("summonRotatableEntity").requires((commandSource) -> commandSource.hasPermission(2)) // Permission level 2 (Operator)
                .executes((commandContext) -> {
                    return execute(commandContext.getSource());
                }));
    }

    public static int execute(CommandSourceStack source) {
        if (source.getEntity() instanceof Player player) {
            BlockPos playerPos = player.blockPosition();
            Level level = player.level();
            AssemblyEntity entity = new AssemblyEntity(ModEntities.ASSEMBLY_ENTITY.get(), level);
            entity.setPos(player.getX(), player.getY(), player.getZ());
            level.addFreshEntity(entity);
            source.sendSuccess(() -> Component.literal("Assembly entity summoned."), true);

            return Command.SINGLE_SUCCESS;
        }

        return 0;
    }
}
