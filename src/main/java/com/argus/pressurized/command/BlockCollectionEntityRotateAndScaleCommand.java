package com.argus.pressurized.command;

import com.argus.pressurized.entity.BlockCollectionEntity;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

public class BlockCollectionEntityRotateAndScaleCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("setblockcollectionentity")
                .then(Commands.argument("entity", EntityArgument.entity())
                        .then(Commands.argument("rotationX", FloatArgumentType.floatArg())
                                .then(Commands.argument("rotationY", FloatArgumentType.floatArg())
                                        .then(Commands.argument("rotationZ", FloatArgumentType.floatArg())
                                                .then(Commands.argument("scale", FloatArgumentType.floatArg())
                                                        .executes(BlockCollectionEntityRotateAndScaleCommand::execute)
                                                )
                                        )
                                )
                        )
                )
        );
    }

    private static int execute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity entity = EntityArgument.getEntity(context, "entity");

        if (entity instanceof BlockCollectionEntity blockCollectionEntity) {
            float rotationX = FloatArgumentType.getFloat(context, "rotationX");
            float rotationY = FloatArgumentType.getFloat(context, "rotationY");
            float rotationZ = FloatArgumentType.getFloat(context, "rotationZ");
            float scale = FloatArgumentType.getFloat(context, "scale");

            blockCollectionEntity.setRotationX(rotationX);
            blockCollectionEntity.setRotationY(rotationY);
            blockCollectionEntity.setRotationZ(rotationZ);
            blockCollectionEntity.setScale(scale);

            context.getSource().sendSuccess(() -> Component.literal("Updated BlockCollectionEntity properties."), true);
            return Command.SINGLE_SUCCESS;
        } else {
            context.getSource().sendFailure(Component.literal("Target entity is not a BlockCollectionEntity."));
            return 0;
        }
    }
}
