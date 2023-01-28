package org.moon.figura.lua.api.nameplate;

import org.moon.figura.lua.LuaWhitelist;
import org.moon.figura.lua.docs.LuaTypeDoc;

@LuaWhitelist
@LuaTypeDoc(
        name = "NameplateAPI",
        value = "nameplate"
)
public class NameplateAPI {

    @LuaWhitelist
    public final NameplateCustomization CHAT;
    @LuaWhitelist
    public final EntityNameplateCustomization ENTITY;
    @LuaWhitelist
    public final NameplateCustomization LIST;
    @LuaWhitelist
    public final NameplateCustomizationGroup ALL;

    public NameplateAPI() {
        CHAT = new NameplateCustomization();
        ENTITY = new EntityNameplateCustomization();
        LIST = new NameplateCustomization();
        ALL = new NameplateCustomizationGroup(CHAT, ENTITY, LIST);
    }

    @LuaWhitelist
    public Object __index(String arg) {
        if (arg == null) return null;
        return switch (arg) {
            case "CHAT" -> CHAT;
            case "ENTITY" -> ENTITY;
            case "LIST" -> LIST;
            case "ALL" -> ALL;
            default -> null;
        };
    }

    @Override
    public String toString() {
        return "NameplateAPI";
    }
}
