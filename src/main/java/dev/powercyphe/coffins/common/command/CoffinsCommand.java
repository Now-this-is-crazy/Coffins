package dev.powercyphe.coffins.common.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import dev.powercyphe.coffins.common.payload.OpenPlayerCoffinsPayload;
import dev.powercyphe.coffins.common.util.CUtil;

public class CoffinsCommand implements CommandRegistrationCallback {
    public static final String COFFINS = "coffins";
    public static final String PLAYER = "player";

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext ctx, Commands.CommandSelection selection) {
        dispatcher.register(
                Commands.literal(COFFINS).requires(Commands.hasPermission(Commands.LEVEL_ADMINS))
                        .then(Commands.argument(PLAYER, EntityArgument.player())
                                .executes(this::execute)
                        )
                        .executes(this::execute)
        );
    }

    public int execute(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();

        if (player != null) {
            return executeWithPlayer(context, player);
        }
        return 0;
    }

    public int executeWithPlayer(CommandContext<CommandSourceStack> context, ServerPlayer player) {
        var coffins = CUtil.getCoffins(player);
        OpenPlayerCoffinsPayload.send(context.getSource().getPlayer(), player, coffins);
        return Command.SINGLE_SUCCESS;
    }


}
