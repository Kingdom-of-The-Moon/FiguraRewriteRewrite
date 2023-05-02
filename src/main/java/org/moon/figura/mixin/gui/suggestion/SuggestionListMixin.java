package org.moon.figura.mixin.gui.suggestion;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.brigadier.suggestion.Suggestion;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import org.moon.figura.avatar.Badges;
import org.moon.figura.ducks.CommandSuggestionsAccessor;
import org.moon.figura.utils.ColorUtils;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(CommandSuggestions.SuggestionsList.class)
public class SuggestionListMixin {
    @Shadow
    private int offset;
    @Dynamic
    @Final
    @Shadow
    CommandSuggestions field_21615; // CommandSuggestions.this
    @Final
    @Shadow
    private List<Suggestion> suggestionList;
    @Final
    @Shadow
    private Rect2i rect;
    @Shadow
    private int current;

    @ModifyArg(
            method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Font;drawShadow(Lcom/mojang/blaze3d/vertex/PoseStack;Ljava/lang/String;FFI)I"),
            index = 1
    )
    String figura_jankCancelRender(String text) {
        if (!((CommandSuggestionsAccessor) field_21615).figura$shouldShowFiguraBadges()) {
            return text;
        }
        // jank-cancel
        return "";
    }

    @SuppressWarnings("InvalidInjectorMethodSignature")  // the plugin doesn't know better
    @Inject(
            method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Font;drawShadow(Lcom/mojang/blaze3d/vertex/PoseStack;Ljava/lang/String;FFI)I"),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    void figura_renderOwn(PoseStack matrices, int mouseX, int mouseY, CallbackInfo ci, int i, boolean bl, boolean bl2, boolean bl3, boolean bl4, boolean bl5, int l) {
        if (!((CommandSuggestionsAccessor) field_21615).figura$shouldShowFiguraBadges()) {
            return;
        }

        // jank-redo
        Suggestion suggestion = this.suggestionList.get(l + this.offset);

        MutableComponent component = Component.empty();
        MutableComponent badge = Badges.System.DEFAULT.badge.copy();
        badge.setStyle(Style.EMPTY.withColor(ColorUtils.rgbToInt(ColorUtils.Colors.DEFAULT.vec)).withFont(Badges.FONT));
        component.append(badge);
        component.append(" ");
        component.append(suggestion.getText());

        ((CommandSuggestionsAccessor) field_21615).figura$getFont()
                .drawShadow(
                        matrices, component, (float) (this.rect.getX() + 1), (float) (this.rect.getY() + 2 + 12 * l), l + this.offset == this.current ? -256 : -5592406
                );
    }
}
