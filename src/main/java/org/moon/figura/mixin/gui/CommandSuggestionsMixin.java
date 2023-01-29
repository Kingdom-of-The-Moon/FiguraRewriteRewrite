package org.moon.figura.mixin.gui;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.SuggestionContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.datafixers.util.Either;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.moon.figura.FiguraMod;
import org.moon.figura.avatar.Avatar;
import org.moon.figura.avatar.AvatarManager;
import org.moon.figura.ducks.CommandSuggestionsAccessor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Mixin(CommandSuggestions.class)
public class CommandSuggestionsMixin implements CommandSuggestionsAccessor {
    @Unique
    boolean useFiguraSuggester;
    @Shadow
    private CompletableFuture<Suggestions> pendingSuggestions;
    @Final
    @Shadow
    private List<FormattedCharSequence> commandUsage;
    @Shadow
    private int commandUsageWidth;
    @Shadow
    private int commandUsagePosition;
    @Shadow
    private boolean allowSuggestions;
    @Shadow
    boolean keepSuggestions;
    @Final
    @Shadow
    EditBox input;
    @Final
    @Shadow
    Screen screen;


    @Shadow @Final
    Font font;

    @Override
    public void setUseFiguraSuggester(boolean use) {
        useFiguraSuggester = use;
    }

    @SuppressWarnings("InvalidInjectorMethodSignature")  // the plugin keeps telling that it's wrong for some reason
    @Inject(
            method = "updateCommandInfo",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/client/gui/components/EditBox;getCursorPosition()I",
                    shift = At.Shift.AFTER
            ),
            cancellable = true,
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    public void useFigura(CallbackInfo ci, String string, StringReader stringReader, boolean bl2, int i) {
        if (useFiguraSuggester && !keepSuggestions) {
            Avatar avatar = AvatarManager.getAvatarForPlayer(FiguraMod.getLocalPlayerUUID());
            if (avatar == null || avatar.luaRuntime == null)
                return;

            SuggestionBehaviour behaviour = avatar.chatAutocompleteEvent(string, i);
            if (behaviour == null) {
                return;
            }
            commandUsageWidth = screen.width;
            commandUsagePosition = 0;

            if (behaviour instanceof AcceptBehaviour accepting) {
                Suggestions s = accepting.suggest();
                pendingSuggestions = CompletableFuture.completedFuture(s);

                if (s.isEmpty()) {
                    commandUsage.add(FormattedCharSequence.forward("This avatar did not provide any suggestion", Style.EMPTY));
                }
            }
            else {
                String usage;
                pendingSuggestions = Suggestions.empty();
                if (behaviour instanceof HintBehaviour hint) {
                    usage = hint.hint();
                    commandUsagePosition = Mth.clamp(input.getScreenX(hint.pos()), 0, this.input.getScreenX(0) + this.input.getInnerWidth() - i);
                    commandUsageWidth = font.width(usage);
                }
                else if (behaviour instanceof RejectBehaviour reject) {
                    usage = reject.err();
                }
                else {
                    throw new RuntimeException("Unexpected " + behaviour.getClass().getName());
                }

                commandUsage.add(FormattedCharSequence.forward(usage, Style.EMPTY));
            }

            if (commandUsage.isEmpty()) {
                commandUsageWidth = 0;
            }

            if (allowSuggestions && Minecraft.getInstance().options.autoSuggestions().get()) {
                ((CommandSuggestions) (Object) this).showSuggestions(false);
            }
            ci.cancel();
        }
    }
}
