package org.moon.figura.ducks;

import com.mojang.brigadier.suggestion.Suggestions;
import net.minecraft.client.gui.Font;

public interface CommandSuggestionsAccessor {
    void figura$setUseFiguraSuggester(boolean use);
    boolean figura$shouldShowFiguraBadges();
    Font figura$getFont();

    interface SuggestionBehaviour {}
    record AcceptBehaviour(Suggestions suggest) implements SuggestionBehaviour {}
    record HintBehaviour(String hint, int pos) implements SuggestionBehaviour {}
    record RejectBehaviour(String err) implements SuggestionBehaviour {}
}
