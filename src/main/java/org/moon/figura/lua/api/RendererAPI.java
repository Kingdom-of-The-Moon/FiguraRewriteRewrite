package org.moon.figura.lua.api;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.moon.figura.avatar.Avatar;
import org.moon.figura.lua.LuaNotNil;
import org.moon.figura.lua.LuaWhitelist;
import org.moon.figura.lua.docs.LuaMethodDoc;
import org.moon.figura.lua.docs.LuaTypeDoc;
import org.moon.figura.math.vector.FiguraVec2;
import org.moon.figura.math.vector.FiguraVec3;

import java.util.UUID;

@LuaWhitelist
@LuaTypeDoc(
        name = "RendererAPI",
        value = "renderer"
)
public class RendererAPI {

    private final UUID owner;

    public Float shadowRadius, fov;

    @LuaWhitelist
    public boolean renderFire = true;
    @LuaWhitelist
    public boolean renderVehicle = true;
    @LuaWhitelist
    public boolean renderCrosshair = true;
    @LuaWhitelist
    public boolean forcePaperdoll;

    public FiguraVec3 cameraPos;
    public FiguraVec3 cameraPivot;
    public FiguraVec3 cameraOffsetPivot;
    public FiguraVec3 cameraRot;
    public FiguraVec3 cameraOffsetRot;
    public ResourceLocation postShader;
    public FiguraVec2 crosshairOffset;

    public RendererAPI(Avatar owner) {
        this.owner = owner.owner;
    }

    private static boolean checkCameraOwner(UUID entity) {
        Entity e = Minecraft.getInstance().getCameraEntity();
        return e != null && e.getUUID().equals(entity);
    }

    @LuaWhitelist
    public boolean shouldRenderFire() {
        return renderFire;
    }

    @LuaWhitelist
    public void setRenderFire(boolean renderFire) {
        this.renderFire = renderFire;
    }

    @LuaWhitelist
    public boolean shouldRenderVehicle() {
        return renderVehicle;
    }

    @LuaWhitelist
    public void setRenderVehicle(boolean renderVehicle) {
        this.renderVehicle = renderVehicle;
    }

    @LuaWhitelist
    public boolean shouldRenderCrosshair() {
        return renderCrosshair;
    }

    @LuaWhitelist
    public void setRenderCrosshair(boolean renderCrosshair) {
        this.renderCrosshair = renderCrosshair;
    }

    @LuaWhitelist
    public boolean shouldForcePaperdoll() {
        return forcePaperdoll;
    }

    @LuaWhitelist
    public void setForcePaperdoll(boolean forcePaperdoll) {
        this.forcePaperdoll = forcePaperdoll;
    }

    @LuaWhitelist
    public Float getShadowRadius() {
        return this.shadowRadius;
    }

    @LuaWhitelist
    @LuaMethodDoc("shadowRadius")
    public RendererAPI setShadowRadius(Float shadowRadius) {
        this.shadowRadius = shadowRadius == null ? null : Mth.clamp(shadowRadius, 0f, 12f);
        return this;
    }

    @LuaWhitelist
    public boolean isFirstPerson() {
        return checkCameraOwner(this.owner) && Minecraft.getInstance().options.getCameraType().isFirstPerson();
    }

    @LuaWhitelist
    public boolean isCameraBackwards() {
        return checkCameraOwner(this.owner) && Minecraft.getInstance().options.getCameraType().isMirrored();
    }

    @LuaWhitelist
    public FiguraVec3 getCameraPos() {
        return this.cameraPos;
    }

    @LuaWhitelist
    public RendererAPI setCameraPos(@LuaNotNil double x, double y, double z) {
        return setCameraPos(FiguraVec3.oneUse(x, y, z));
    }

    @LuaWhitelist
    @LuaMethodDoc("cameraPos")
    public RendererAPI setCameraPos(FiguraVec3 pos) {
        this.cameraPos = pos == null ? null : pos.copy();
        return this;
    }

    @LuaWhitelist
    public FiguraVec3 getCameraPivot() {
        return this.cameraPivot;
    }

    @LuaWhitelist
    public RendererAPI setCameraPivot(@LuaNotNil double x, double y, double z) {
        return setCameraPivot(FiguraVec3.oneUse(x, y, z));
    }

    @LuaWhitelist
    @LuaMethodDoc("cameraPivot")
    public RendererAPI setCameraPivot(FiguraVec3 pivot) {
        this.cameraPivot = pivot == null ? null : pivot.copy();
        return this;
    }

    @LuaWhitelist
    public FiguraVec3 getCameraOffsetPivot() {
        return this.cameraOffsetPivot;
    }

    @LuaWhitelist
    public RendererAPI setCameraOffsetPivot(@LuaNotNil double x, double y, double z) {
        return setCameraOffsetPivot(FiguraVec3.oneUse(x, y, z));
    }

    @LuaWhitelist
    @LuaMethodDoc(value = {"offsetCameraPivot"},key = "setOffsetCameraPivot")
    public RendererAPI setCameraOffsetPivot(FiguraVec3 pivotOffset) {
        this.cameraOffsetPivot = pivotOffset == null ? null : pivotOffset.copy();
        return this;
    }

    @LuaWhitelist
    public FiguraVec3 getCameraRot() {
        return this.cameraRot;
    }

    @LuaWhitelist
    public RendererAPI setCameraRot(@LuaNotNil double x, double y, double z) {
        return setCameraRot(FiguraVec3.oneUse(x, y, z));
    }

    @LuaWhitelist
    @LuaMethodDoc("cameraRot")
    public RendererAPI setCameraRot(FiguraVec3 rot) {
        this.cameraRot = rot == null ? null : rot.copy();
        return this;
    }

    @LuaWhitelist
    public FiguraVec3 getCameraOffsetRot() {
        return this.cameraOffsetRot;
    }

    @LuaWhitelist
    public RendererAPI setOffsetCameraRot(@LuaNotNil double x, double y, double z) {
        return setOffsetCameraRot(FiguraVec3.oneUse(x, y, z));
    }

    @LuaWhitelist
    @LuaMethodDoc("offsetCameraRot")
    public RendererAPI setOffsetCameraRot(FiguraVec3 rot) {
        this.cameraOffsetRot = rot == null ? null : rot.copy();
        return this;
    }

    @LuaWhitelist
    public String getPostEffect() {
        return this.postShader.getPath().substring(13, this.postShader.getPath().length() - 5);
    }

    @LuaWhitelist
    @LuaMethodDoc("postEffect")
    public RendererAPI setPostEffect(String effect) {
        this.postShader = effect == null ? null : new ResourceLocation("shaders/post/" + effect.toLowerCase() + ".json");
        return this;
    }

    @LuaWhitelist
    public Float getFOV() {
        return this.fov;
    }

    @LuaWhitelist
    @LuaMethodDoc("fov")
    public RendererAPI setFOV(Float fov) {
        this.fov = fov;
        return this;
    }

    @LuaWhitelist
    public FiguraVec2 getCrosshairOffset() {
        return this.crosshairOffset;
    }

    @LuaWhitelist
    public RendererAPI setCrosshairOffset(@LuaNotNil double x, double y) {
        return setCrosshairOffset(FiguraVec2.oneUse(x, y));
    }

    @LuaWhitelist
    @LuaMethodDoc("crosshairOffset")
    public RendererAPI setCrosshairOffset(FiguraVec2 crosshairOffset) {
        this.crosshairOffset = crosshairOffset == null ? null : crosshairOffset.copy();
        return this;
    }

    @LuaWhitelist
    public Object __index(String arg) {
        if (arg == null) return null;
        return switch (arg) {
            case "renderFire" -> renderFire;
            case "renderVehicle" -> renderVehicle;
            case "renderCrosshair" -> renderCrosshair;
            case "forcePaperdoll" -> forcePaperdoll;
            default -> null;
        };
    }

    @LuaWhitelist
    public void __newindex(String key, boolean value) {
        if (key == null) return;
        switch (key) {
            case "renderFire" -> renderFire = value;
            case "renderVehicle" -> renderVehicle = value;
            case "renderCrosshair" -> renderCrosshair = value;
            case "forcePaperdoll" -> forcePaperdoll = value;
        }
    }

    @Override
    public String toString() {
        return "RendererAPI";
    }
}
