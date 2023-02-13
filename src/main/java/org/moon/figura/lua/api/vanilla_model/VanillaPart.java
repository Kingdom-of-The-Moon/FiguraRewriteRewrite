package org.moon.figura.lua.api.vanilla_model;

import net.minecraft.client.model.EntityModel;
import org.moon.figura.avatar.Avatar;
import org.moon.figura.lua.LuaNotNil;
import org.moon.figura.lua.LuaWhitelist;
import org.moon.figura.lua.docs.LuaMethodDoc;
import org.moon.figura.lua.docs.LuaTypeDoc;
import org.moon.figura.math.vector.FiguraVec3;
import org.moon.figura.utils.LuaUtils;

@LuaWhitelist
@LuaTypeDoc(
        name = "VanillaPart",
        value = "vanilla_part"
)
public abstract class VanillaPart {

    protected final String name;
    protected final Avatar owner;

    //transforms
    protected Boolean visible;
    protected FiguraVec3 pos, rot, scale;
    protected FiguraVec3 offsetRot, offsetScale;

    public VanillaPart(Avatar owner, String name) {
        this.owner = owner;
        this.name = name;
    }

    public boolean checkVisible() {
        return visible == null || visible;
    }

    public abstract void save(EntityModel<?> model);
    public abstract void preTransform(EntityModel<?> model);
    public abstract void posTransform(EntityModel<?> model);
    public abstract void restore(EntityModel<?> model);

    @LuaWhitelist
    public Boolean getVisible() {
        return this.visible;
    }

    @LuaWhitelist
    @LuaMethodDoc("visible")
    public VanillaPart setVisible(Boolean visible) {
        this.visible = visible;
        return this;
    }

    @LuaWhitelist
    public FiguraVec3 getPos() {
        return pos;
    }

    @LuaWhitelist
    public VanillaPart setPos(@LuaNotNil double x, double y, double z){
        return setPos(FiguraVec3.oneUse(x, y, z));
    }
    
    @LuaWhitelist
    @LuaMethodDoc("pos")
    public VanillaPart setPos(FiguraVec3 pos) {
        this.pos = pos == null ? null : pos.copy();
        return this;
    }

    @LuaWhitelist
    public FiguraVec3 getRot() {
        return rot;
    }

    @LuaWhitelist
    public VanillaPart setRot(@LuaNotNil double x, double y, double z) {
        return setRot(FiguraVec3.oneUse(x, y, z));
    }
    
    @LuaWhitelist
    @LuaMethodDoc("rot")
    public VanillaPart setRot(FiguraVec3 rot){
        this.rot = rot == null ? null : rot.copy();
        return this;
    }

    @LuaWhitelist
    public FiguraVec3 getOffsetRot() {
        return offsetRot;
    }

    @LuaWhitelist
    public VanillaPart setOffsetRot(@LuaNotNil double x, double y, double z) {
        return setOffsetRot(FiguraVec3.oneUse(x, y, z));
    }
    
    @LuaWhitelist
    @LuaMethodDoc("offsetRot")
    public VanillaPart setOffsetRot(FiguraVec3 offsetRot) {
        this.offsetRot = offsetRot == null ? null : offsetRot.copy();
        return this;
    }


    @LuaWhitelist
    public FiguraVec3 getScale() {
        return scale;
    }

    @LuaWhitelist
    public VanillaPart setScale(Double x, Double y, Double z){
        return setScale(LuaUtils.parseVec3("setScale", x, y, z, 1, 1, 1));
    }
    
    @LuaWhitelist
    @LuaMethodDoc("scale")
    public VanillaPart setScale(FiguraVec3 scale) {
        this.scale = scale == null ? null : scale.copy();
        return this;
    }

    @LuaWhitelist
    public FiguraVec3 getOffsetScale() {
        return offsetScale;
    }
    
    @LuaWhitelist
    public VanillaPart setOffsetScale(Double x, Double y, Double z){
        return setOffsetScale(LuaUtils.parseVec3("setOffsetScale", x, y, z, 1, 1, 1));
    }

    @LuaWhitelist
    @LuaMethodDoc("offsetScale")
    public VanillaPart setOffsetScale(FiguraVec3 offsetScale) {
        this.offsetScale = offsetScale == null ? null : offsetScale.copy();
        return this;
    }

    @Override
    public String toString() {
        return "VanillaPart";
    }
}
