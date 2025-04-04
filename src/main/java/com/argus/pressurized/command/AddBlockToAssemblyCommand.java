package com.argus.pressurized.command;

import com.argus.pressurized.entity.AssemblyEntity;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;

public class AddBlockToAssemblyCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext commandBuildContext) {
        dispatcher.register(Commands.literal("addBlockToAssembly")
                .then(Commands.argument("target", EntityArgument.entity())
                        .then(Commands.argument("relativePos", BlockPosArgument.blockPos())
                                .then(Commands.argument("blockState", BlockStateArgument.block(commandBuildContext))
                                        .requires((commandSource) -> commandSource.hasPermission(2)).executes((commandContext) -> {
                                            Entity target = EntityArgument.getEntity(commandContext, "target");
                                            if (target instanceof AssemblyEntity assemblyEntity) {
                                                BlockPos relativePos = BlockPosArgument.getBlockPos(commandContext, "relativePos");
                                                BlockState blockState = BlockStateArgument.getBlock(commandContext, "blockState").getState();
                                                assemblyEntity.addBlockState(relativePos, blockState);
                                                commandContext.getSource().sendSuccess(() -> Component.literal("Block added to assembly entity."), true);
                                                return 1;
                                            } else {
                                                commandContext.getSource().sendFailure(Component.literal("Target must be an Assembly entity."));
                                                return 0;
                                            }
                                        })))));
    }
}
