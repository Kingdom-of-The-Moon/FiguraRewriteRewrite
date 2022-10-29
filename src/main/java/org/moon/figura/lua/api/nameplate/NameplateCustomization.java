package org.moon.figura.lua.api.nameplate;

import net.minecraft.network.chat.Component;
import org.luaj.vm2.LuaError;
import org.moon.figura.lua.LuaWhitelist;
import org.moon.figura.lua.docs.LuaTypeDoc;
import org.moon.figura.utils.TextUtils;

@LuaWhitelist
@LuaTypeDoc(
        name = "NameplateCustomization",
        value = "nameplate_customization"
)
public class NameplateCustomization {

    private String text;

    public static Component applyCustomization(String text) {
        return TextUtils.removeClickableObjects(TextUtils.noBadges4U(TextUtils.tryParseJson(text)));
    }

    @LuaWhitelist
    public String getText() {
        return this.text;
    }

    @LuaWhitelist
    public void setText(String text) {
        if (text != null && TextUtils.tryParseJson(text).getString().length() > 256)
            throw new LuaError("Text length exceeded limit of 256 characters");
        this.text = text;
    }

    @Override
    public String toString() {
        return "NameplateCustomization";
    }
}
