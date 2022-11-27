package org.moon.figura.model.rendering.texture;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import org.moon.figura.lua.docs.LuaTypeDoc;
import org.moon.figura.mixin.render.layers.elytra.ElytraLayerAccessor;

import java.util.UUID;

public class FiguraTextureSet {
    public final FiguraTexture mainTex, emissiveTex;

    public FiguraTextureSet(FiguraTexture mainData, FiguraTexture emissiveData) {
        mainTex = mainData;
        emissiveTex = emissiveData;
    }

    public void clean() {
        if (mainTex != null)
            mainTex.close();
        if (emissiveTex != null)
            emissiveTex.close();
    }

    public void uploadIfNeeded() {
        if (mainTex != null)
            mainTex.registerAndUpload();
        if (emissiveTex != null)
            emissiveTex.registerAndUpload();
    }

    public int getWidth() {
        if (mainTex != null)
            return mainTex.getWidth();
        else if (emissiveTex != null)
            return emissiveTex.getWidth();
        else
            return -1;
    }

    public int getHeight() {
        if (mainTex != null)
            return mainTex.getHeight();
        else if (emissiveTex != null)
            return emissiveTex.getHeight();
        else
            return -1;
    }

    public ResourceLocation getOverrideTexture(UUID owner, Pair<OverrideType, Object> pair) {
        OverrideType type;

        if (pair == null || (type = pair.getFirst()) == null)
            return null;

        return switch (type) {
            case SKIN, CAPE, ELYTRA -> {
                if (Minecraft.getInstance().player == null)
                    yield null;

                PlayerInfo info = Minecraft.getInstance().player.connection.getPlayerInfo(owner);
                if (info == null)
                    yield null;

                yield switch (type) {
                    case CAPE -> info.getCapeLocation();
                    case ELYTRA -> info.getElytraLocation() == null ? ElytraLayerAccessor.getWingsLocation() : info.getElytraLocation();
                    default -> info.getSkinLocation();
                };
            }
            case RESOURCE -> {
                try {
                    ResourceLocation resource = new ResourceLocation(String.valueOf(pair.getSecond()));
                    yield Minecraft.getInstance().getResourceManager().getResource(resource).isPresent() ? resource : MissingTextureAtlasSprite.getLocation();
                } catch (Exception ignored) {
                    yield MissingTextureAtlasSprite.getLocation();
                }
            }
            case PRIMARY -> mainTex == null ? null : mainTex.textureID;
            case SECONDARY -> emissiveTex == null ? null : emissiveTex.textureID;
            case CUSTOM -> {
                try {
                    yield ((FiguraTexture) pair.getSecond()).textureID;
                } catch (Exception ignored) {
                    yield MissingTextureAtlasSprite.getLocation();
                }
            }
        };
    }

    @LuaTypeDoc(value = "texture_types", name = "TextureTypes")
    public enum OverrideType {
        SKIN,
        CAPE,
        ELYTRA,
        RESOURCE,
        PRIMARY,
        SECONDARY,
        CUSTOM
    }
}
