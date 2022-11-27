package org.moon.figura.model.rendering.texture;

import org.moon.figura.lua.docs.LuaTypeDoc;

@LuaTypeDoc(value = "render_modes", name = "RenderModes")
public enum EntityRenderMode {
    FIGURA_GUI,
    PAPERDOLL,
    MINECRAFT_GUI,
    FIRST_PERSON,
    RENDER,
    OTHER
}