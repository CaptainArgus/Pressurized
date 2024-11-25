package com.argus.pressurized.command;

import com.argus.pressurized.entity.BlockCollectionEntity;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;

public class BlockCollectionEntityBuilderCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext commandBuildContext) {
        dispatcher.register(Commands.literal("addBlockToCollection")
                .then(Commands.argument("target", EntityArgument.entity())
                        .then(Commands.argument("relativePos", BlockPosArgument.blockPos())
                                .then(Commands.argument("blockState", BlockStateArgument.block(commandBuildContext))
                                        .executes(context -> {
                                            Entity target = EntityArgument.getEntity(context, "target");
                                            if (target instanceof BlockCollectionEntity blockCollectionEntity) {
                                                BlockPos relativePos = BlockPosArgument.getBlockPos(context, "relativePos");
                                                BlockInput blockStateResult = BlockStateArgument.getBlock(context, "blockState");
                                                BlockState blockState = blockStateResult.getState();
                                                blockCollectionEntity.addBlock(relativePos, blockState);
                                                context.getSource().sendSuccess(() -> Component.literal("Block added to collection entity."), true);
                                                return 1;
                                            } else {
                                                context.getSource().sendFailure(Component.literal("Target must be a BlockCollectionEntity."));
                                                return 0;
                                            }
                                        })))));
    }
}
