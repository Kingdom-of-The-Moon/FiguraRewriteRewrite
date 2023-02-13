package org.moon.figura.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import org.moon.figura.FiguraMod;
import org.moon.figura.avatar.AvatarManager;
import org.moon.figura.backend2.BackendCommands;
import org.moon.figura.lua.docs.FiguraDocsManager;
import org.moon.figura.lua.newdocswip.NewDocsManager;

public class FiguraCommands {

    public static void init() {
        //root
        LiteralArgumentBuilder<FabricClientCommandSource> root = LiteralArgumentBuilder.literal(FiguraMod.MOD_ID);

        //docs
        root.then(FiguraDocsManager.getCommand());
        root.then(NewDocsManager.getCommand());
        root.then(FiguraDocsManager.getExportCommand());

        //links
        root.then(FiguraLinkCommand.getCommand());

        //run
        root.then(FiguraRunCommand.getCommand());

        //load
        root.then(FiguraLoadCommand.getCommand());

        //load
        root.then(FiguraReloadCommand.getCommand());

        //debug
        root.then(FiguraDebugCommand.getCommand());

        if (FiguraMod.DEBUG_MODE) {
            //backend debug
            root.then(BackendCommands.getCommand());

            //set avatar command
            root.then(AvatarManager.getCommand());
        }

        //register
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(root));
    }
}
