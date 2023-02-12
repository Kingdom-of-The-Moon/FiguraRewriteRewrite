package org.moon.figura.lua.api.nameplate;

import net.minecraft.network.chat.Component;
import org.luaj.vm2.LuaError;
import org.moon.figura.avatar.Badges;
import org.moon.figura.gui.Emojis;
import org.moon.figura.lua.LuaWhitelist;
import org.moon.figura.lua.docs.LuaTypeDoc;
import org.moon.figura.utils.TextUtils;

@LuaWhitelist
@LuaTypeDoc(
        name = "NameplateCustomization",
        value = "nameplate_customization"
)
public class NameplateCustomization {

    private Component json;
    private String text;

    private Component parseJsonText(String text) {
        Component component = TextUtils.tryParseJson(text);
        component = Badges.noBadges4U(component);
        component = TextUtils.removeClickableObjects(component);
        return Emojis.applyEmojis(component);
    }

    public Component getJson() {
        return json;
    }

    @LuaWhitelist
    public String getText() {
        return this.text;
    }

    @LuaWhitelist
    public NameplateCustomization setText(String text) {
        this.text = text;
        if (text != null) {
            Component component = parseJsonText(text);
            if (component.getString().length() > 64)
                throw new LuaError("Text length exceeded limit of 64 characters");
            json = component;
        } else {
            json = null;
        }
        return this;
    }

    @Override
    public String toString() {
        return "NameplateCustomization";
    }
}
