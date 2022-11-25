package org.moon.figura.backend2;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.moon.figura.FiguraMod;

public class BackendCommands {

    public static LiteralArgumentBuilder<FabricClientCommandSource> getCommand() {
        //root
        LiteralArgumentBuilder<FabricClientCommandSource> backend = LiteralArgumentBuilder.literal("backend2");

        //force backend connection
        LiteralArgumentBuilder<FabricClientCommandSource> connect = LiteralArgumentBuilder.literal("connect");
        connect.executes(context -> {
            NetworkStuff.reAuth();
            return 1;
        });

        backend.then(connect);

        //run
        LiteralArgumentBuilder<FabricClientCommandSource> run = LiteralArgumentBuilder.literal("run");
        run.executes(context -> runRequest(context, ""));

        RequiredArgumentBuilder<FabricClientCommandSource, String> request = RequiredArgumentBuilder.argument("request", StringArgumentType.greedyString());
        request.executes(context -> runRequest(context, StringArgumentType.getString(context, "request")));

        run.then(request);
        backend.then(run);

        //debug mode
        LiteralArgumentBuilder<FabricClientCommandSource> debug = LiteralArgumentBuilder.literal("debug");
        debug.executes(context -> {
            NetworkStuff.debug = !NetworkStuff.debug;
            FiguraMod.sendChatMessage(Component.literal("Backend Debug Mode set to: " + NetworkStuff.debug).withStyle(NetworkStuff.debug ? ChatFormatting.GREEN : ChatFormatting.RED));
            return 1;
        });

        backend.then(debug);

        //return
        return backend;
    }

    private static int runRequest(CommandContext<FabricClientCommandSource> context, String request) {
        try {
            NetworkStuff.api.runString(
                    NetworkStuff.api.header(request).build(),
                    (code, data) -> FiguraMod.sendChatMessage(Component.literal(data))
            );
            return 1;
        } catch (Exception e) {
            context.getSource().sendError(Component.literal(e.getMessage()));
            return 0;
        }
    }
}
