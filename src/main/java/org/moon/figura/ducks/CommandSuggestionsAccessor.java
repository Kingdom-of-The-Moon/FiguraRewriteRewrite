package org.moon.figura.ducks;

import com.mojang.brigadier.suggestion.Suggestions;

public interface CommandSuggestionsAccessor {
    void setUseFiguraSuggester(boolean use);

    interface SuggestionBehaviour {}
    record AcceptBehaviour(Suggestions suggest) implements SuggestionBehaviour {}
    record HintBehaviour(String hint, int pos) implements SuggestionBehaviour {}
    record RejectBehaviour(String err) implements SuggestionBehaviour {}
}
