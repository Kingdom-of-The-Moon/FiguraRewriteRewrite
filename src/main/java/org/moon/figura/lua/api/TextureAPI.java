package org.moon.figura.lua.api;

import com.mojang.blaze3d.platform.NativeImage;
import org.luaj.vm2.LuaError;
import org.moon.figura.avatar.Avatar;
import org.moon.figura.lua.LuaNotNil;
import org.moon.figura.lua.LuaWhitelist;
import org.moon.figura.lua.docs.LuaTypeDoc;
import org.moon.figura.model.rendering.texture.FiguraTexture;
import org.moon.figura.trust.Trust;
import org.moon.figura.utils.ColorUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@LuaWhitelist
@LuaTypeDoc(
        name = "TextureAPI",
        value = "textures"
)
public class TextureAPI {

    private static final int TEXTURE_LIMIT = 128;

    private final Avatar owner;

    public TextureAPI(Avatar owner) {
        this.owner = owner;
    }

    private void check() {
        if (owner.renderer == null)
            throw new LuaError("Avatar have no active renderer!");
    }

    private FiguraTexture register(String name, NativeImage image) {
        int max = owner.trust.get(Trust.TEXTURE_SIZE);
        if (image.getWidth() > max || image.getHeight() > max)
            throw new LuaError("Texture exceeded max size of " + max + " x " + max + " resolution, got " + image.getWidth() + " x " + image.getHeight());

        FiguraTexture oldText = get(name);
        if (oldText != null)
            oldText.close();

        if (owner.renderer.customTextures.size() > TEXTURE_LIMIT)
            throw new LuaError("Maximum amount of textures reached!");

        FiguraTexture texture = new FiguraTexture(owner, name, image);
        owner.renderer.customTextures.put(name, texture);
        return texture;
    }

    @LuaWhitelist
    public FiguraTexture register(@LuaNotNil String name, int width, int height) {
        NativeImage image;
        try {
            image = new NativeImage(width, height, true);
        } catch (Exception e) {
            throw new LuaError(e.getMessage());
        }

        FiguraTexture texture = register(name, image);
        texture.fill(0, 0, width, height, ColorUtils.Colors.FRAN_PINK.vec.augmented());
        return texture;
    }

    @LuaWhitelist
    public FiguraTexture read(@LuaNotNil String name, @LuaNotNil String data) {
        NativeImage image;
        try {
            image = NativeImage.fromBase64(data);
        } catch (Exception e) {
            throw new LuaError(e.getMessage());
        }

        return register(name, image);
    }

    @LuaWhitelist
    public FiguraTexture get(@LuaNotNil String name) {
        check();
        return owner.renderer.customTextures.get(name);
    }

    @LuaWhitelist
    public List<FiguraTexture> getTextures() {
        check();
        return new ArrayList<>(owner.renderer.textures.values());
    }

    @LuaWhitelist
    public FiguraTexture __index(@LuaNotNil String name) {
        check();

        FiguraTexture texture = get(name);
        if (texture != null)
            return texture;

        for (Map.Entry<String, FiguraTexture> entry : owner.renderer.textures.entrySet()) {
            if (entry.getKey().equals(name))
                return entry.getValue();
        }

        return null;
    }

    @Override
    public String toString() {
        return "TextureAPI";
    }
}
