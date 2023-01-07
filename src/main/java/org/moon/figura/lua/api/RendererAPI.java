package org.moon.figura.lua.api;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.moon.figura.avatar.Avatar;
import org.moon.figura.lua.LuaNotNil;
import org.moon.figura.lua.LuaWhitelist;
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
    public void setShadowRadius(Float shadowRadius) {
        this.shadowRadius = shadowRadius == null ? null : Mth.clamp(shadowRadius, 0f, 12f);
    }

    @LuaWhitelist
    public RendererAPI shadowRadius(Float shadowRadius) {
        setShadowRadius(shadowRadius);
        return this;
    }

    @LuaWhitelist
    public Float getShadowRadius() {
        return this.shadowRadius;
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
    public void setCameraPos(@LuaNotNil double x, double y, double z) {
        setCameraPos(FiguraVec3.oneUse(x, y, z));
    }

    @LuaWhitelist
    public void setCameraPos(FiguraVec3 pos) {
        this.cameraPos = pos == null ? null : pos.copy();
    }

    @LuaWhitelist
    public RendererAPI cameraPos(@LuaNotNil double x, double y, double z) {
        return cameraPos(FiguraVec3.oneUse(x, y, z));
    }

    @LuaWhitelist
    public RendererAPI cameraPos(FiguraVec3 pos) {
        setCameraPos(pos);
        return this;
    }

    @LuaWhitelist
    public FiguraVec3 getCameraPivot() {
        return this.cameraPivot;
    }

    @LuaWhitelist
    public void setCameraPivot(@LuaNotNil double x, double y, double z) {
        setCameraPivot(FiguraVec3.oneUse(x, y, z));
    }

    @LuaWhitelist
    public void setCameraPivot(FiguraVec3 pivot) {
        this.cameraPivot = pivot == null ? null : pivot.copy();
    }

    @LuaWhitelist
    public RendererAPI cameraPivot(@LuaNotNil double x, double y, double z) {
        return cameraPivot(FiguraVec3.oneUse(x, y, z));
    }

    @LuaWhitelist
    public RendererAPI cameraPivot(FiguraVec3 pivot) {
        setCameraPivot(pivot);
        return this;
    }

    @LuaWhitelist
    public FiguraVec3 getCameraOffsetPivot() {
        return this.cameraOffsetPivot;
    }

    @LuaWhitelist
    public void setCameraOffsetPivot(@LuaNotNil double x, double y, double z) {
        setCameraOffsetPivot(FiguraVec3.oneUse(x, y, z));
    }

    @LuaWhitelist
    public void setCameraOffsetPivot(FiguraVec3 pivotOffset) {
        this.cameraOffsetPivot = pivotOffset == null ? null : pivotOffset.copy();
    }

    @LuaWhitelist
    public RendererAPI offsetCameraPivot(@LuaNotNil double x, double y, double z) {
        return offsetCameraPivot(FiguraVec3.oneUse(x, y, z));
    }

    @LuaWhitelist
    public RendererAPI offsetCameraPivot(FiguraVec3 pivotOffset) {
        setCameraOffsetPivot(pivotOffset);
        return this;
    }

    @LuaWhitelist
    public FiguraVec3 getCameraRot() {
        return this.cameraRot;
    }

    @LuaWhitelist
    public void setCameraRot(@LuaNotNil double x, double y, double z) {
        setCameraRot(FiguraVec3.oneUse(x, y, z));
    }

    @LuaWhitelist
    public void setCameraRot(FiguraVec3 rot) {
        this.cameraRot = rot == null ? null : rot.copy();
    }

    @LuaWhitelist
    public RendererAPI cameraRot(@LuaNotNil double x, double y, double z) {
        return cameraRot(FiguraVec3.oneUse(x, y, z));
    }

    @LuaWhitelist
    public RendererAPI cameraRot(FiguraVec3 rot) {
        setCameraRot(rot);
        return this;
    }

    @LuaWhitelist
    public FiguraVec3 getCameraOffsetRot() {
        return this.cameraOffsetRot;
    }

    @LuaWhitelist
    public void setOffsetCameraRot(@LuaNotNil double x, double y, double z) {
        setOffsetCameraRot(FiguraVec3.oneUse(x, y, z));
    }

    @LuaWhitelist
    public void setOffsetCameraRot(FiguraVec3 rot) {
        this.cameraOffsetRot = rot == null ? null : rot.copy();
    }

    @LuaWhitelist
    public RendererAPI offsetCameraRot(@LuaNotNil double x, double y, double z) {
        return offsetCameraRot(FiguraVec3.oneUse(x, y, z));
    }

    @LuaWhitelist
    public RendererAPI offsetCameraRot(FiguraVec3 rot) {
        setOffsetCameraRot(rot);
        return this;
    }

    @LuaWhitelist
    public String getPostEffect() {
        return this.postShader.getPath().substring(13, this.postShader.getPath().length() - 5);
    }

    @LuaWhitelist
    public void setPostEffect(String effect) {
        this.postShader = effect == null ? null : new ResourceLocation("shaders/post/" + effect.toLowerCase() + ".json");
    }

    @LuaWhitelist
    public RendererAPI postEffect(String effect) {
        setPostEffect(effect);
        return this;
    }

    @LuaWhitelist
    public Float getFOV() {
        return this.fov;
    }

    @LuaWhitelist
    public void setFOV(Float fov) {
        this.fov = fov;
    }

    @LuaWhitelist
    public RendererAPI fov(Float fov) {
        setFOV(fov);
        return this;
    }

    @LuaWhitelist
    public FiguraVec2 getCrosshairOffset() {
        return this.crosshairOffset;
    }

    @LuaWhitelist
    public void setCrosshairOffset(@LuaNotNil double x, double y) {
        setCrosshairOffset(FiguraVec2.oneUse(x, y));
    }

    @LuaWhitelist
    public void setCrosshairOffset(FiguraVec2 crosshairOffset) {
        this.crosshairOffset = crosshairOffset == null ? null : crosshairOffset.copy();
    }

    @LuaWhitelist
    public RendererAPI crosshairOffset(@LuaNotNil double x, double y) {
        return crosshairOffset(FiguraVec2.oneUse(x, y));
    }

    @LuaWhitelist
    public RendererAPI crosshairOffset(FiguraVec2 crosshairOffset) {
        setCrosshairOffset(crosshairOffset);
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
