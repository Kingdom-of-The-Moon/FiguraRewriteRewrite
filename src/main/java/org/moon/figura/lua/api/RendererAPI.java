package org.moon.figura.lua.api;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.moon.figura.avatar.Avatar;
import org.moon.figura.lua.LuaNotNil;
import org.moon.figura.lua.LuaWhitelist;
import org.moon.figura.lua.docs.LuaTypeDoc;
import org.moon.figura.math.vector.FiguraVec3;
import org.moon.figura.utils.LuaUtils;

import java.util.UUID;

@LuaWhitelist
@LuaTypeDoc(
        name = "RendererAPI",
        value = "renderer"
)
public class RendererAPI {

    private final UUID owner;

    public Float shadowRadius;

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

    public RendererAPI(Avatar owner) {
        this.owner = owner.owner;
    }

    private static boolean checkCameraOwner(UUID entity) {
        Entity e = Minecraft.getInstance().getCameraEntity();
        return e != null && e.getUUID().equals(entity);
    }

    @LuaWhitelist
    public void setShadowRadius(){
        shadowRadius = null;
    }

    @LuaWhitelist
    public void setShadowRadius(float shadowRadius) {
        this.shadowRadius = Mth.clamp(shadowRadius, 0f, 12f);
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
    public void setCameraPos(){
        cameraPos  = null;
    }

    @LuaWhitelist
    public void setCameraPos(Double x, Double y, Double z){
        setCameraPos(LuaUtils.freeVec3("setCameraPos", x, y, z));
    }

    @LuaWhitelist
    public void setCameraPos(@LuaNotNil FiguraVec3 pos) {
        this.cameraPos = pos.copy();
    }

    @LuaWhitelist
    public FiguraVec3 getCameraPivot() {
        return this.cameraPivot;
    }

    @LuaWhitelist
    public void setCameraPivot(){
        cameraPivot = null;
    }

    @LuaWhitelist
    public void setCameraPivot(Double x, Double y, Double z){
        setCameraPivot(LuaUtils.freeVec3("setCameraPivot", x, y, z));
    }

    @LuaWhitelist
    public void setCameraPivot(@LuaNotNil FiguraVec3 pivot) {
        this.cameraPivot = pivot.copy();
    }

    @LuaWhitelist
    public FiguraVec3 getCameraOffsetPivot() {
        return this.cameraOffsetPivot;
    }

    @LuaWhitelist
    public void offsetCameraPivot(){
        this.cameraOffsetPivot = null;
    }

    @LuaWhitelist
    public void offsetCameraPivot(Double x, Double y, Double z){
        offsetCameraPivot(LuaUtils.freeVec3("offsetCameraPivot", x, y, z));
    }

    @LuaWhitelist
    public void offsetCameraPivot(@LuaNotNil FiguraVec3 pivotOffset) {
        this.cameraOffsetPivot = pivotOffset.copy();
    }

    @LuaWhitelist
    public FiguraVec3 getCameraRot() {
        return this.cameraRot;
    }

    @LuaWhitelist
    public void setCameraRot(){
        this.cameraRot = null;
    }

    public void setCameraRot(Double x, Double y, Double z){
        setCameraRot(LuaUtils.freeVec3("setCameraRot", x, y, z));
    }

    @LuaWhitelist
    public void setCameraRot(@LuaNotNil FiguraVec3 rot) {
        this.cameraRot = rot.copy();
    }

    @LuaWhitelist
    public FiguraVec3 getCameraOffsetRot() {
        return this.cameraOffsetRot;
    }

    @LuaWhitelist
    public void offsetCameraRot(){
        this.cameraOffsetRot = null;
    }

    @LuaWhitelist
    public void offsetCameraRot(Double x, Double y, Double z){
        offsetCameraPivot(LuaUtils.freeVec3("offsetCameraRot", x, y, z));
    }

    @LuaWhitelist
    public void offsetCameraRot(@LuaNotNil FiguraVec3 rot) {
        this.cameraOffsetRot = rot.copy();
    }

    @LuaWhitelist
    public void setPostEffect(String effect) {
        this.postShader = effect == null ? null : new ResourceLocation("shaders/post/" + effect.toLowerCase() + ".json");
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
