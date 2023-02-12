package org.moon.figura.lua.api.vanilla_model;

import net.minecraft.client.model.EntityModel;
import org.moon.figura.avatar.Avatar;
import org.moon.figura.lua.LuaWhitelist;
import org.moon.figura.lua.docs.LuaMethodDoc;
import org.moon.figura.lua.docs.LuaTypeDoc;
import org.moon.figura.math.vector.FiguraVec3;
import org.moon.figura.utils.LuaUtils;

import java.util.Collection;
import java.util.HashMap;

@LuaWhitelist
@LuaTypeDoc(
        name = "VanillaModelGroup",
        value = "vanilla_group_part"
)
public class VanillaGroupPart extends VanillaPart {

    private final Collection<VanillaPart> cachedParts;
    private final HashMap<String, VanillaPart> partMap;

    public VanillaGroupPart(Avatar owner, String name, VanillaPart... parts) {
        super(owner, name);
        partMap = new HashMap<>();
        for (VanillaPart part : parts)
            partMap.put(part.name, part);
        cachedParts = partMap.values();
    }

    @Override
    public void save(EntityModel<?> model) {
        for (VanillaPart part : cachedParts)
            part.save(model);
    }

    @Override
    public void preTransform(EntityModel<?> model) {
        for (VanillaPart part : cachedParts)
            part.preTransform(model);
    }

    @Override
    public void posTransform(EntityModel<?> model) {
        for (VanillaPart part : cachedParts)
            part.posTransform(model);
    }

    @Override
    public void restore(EntityModel<?> model) {
        for (VanillaPart part : cachedParts)
            part.restore(model);
    }

    @Override
    @LuaWhitelist
    @LuaMethodDoc("visible")
    public VanillaPart setVisible(Boolean visible) {
        for (VanillaPart part : cachedParts)
            part.setVisible(visible);
        return super.setVisible(visible);
    }

    @Override
    @LuaWhitelist
    public VanillaPart setPos(double x, double y, double z){
        return setPos(FiguraVec3.oneUse(x, y, z));
    }
    
    @Override
    @LuaWhitelist
    @LuaMethodDoc("pos")
    public VanillaPart setPos(FiguraVec3 pos) {
        for (VanillaPart part : cachedParts)
            part.setPos(pos);
        return super.setPos(pos);
    }

    @Override
    @LuaWhitelist
    public VanillaPart setRot(double x, double y, double z){
        return setRot(FiguraVec3.oneUse(x, y, z));
    }

    @Override
    @LuaWhitelist
    @LuaMethodDoc("rot")
    public VanillaPart setRot(FiguraVec3 rot) {
        for (VanillaPart part : cachedParts)
            part.setRot(rot);
        return super.setRot(rot);
    }

    @Override
    @LuaWhitelist
    public VanillaPart setOffsetRot(double x, double y, double z){
        return setOffsetRot(FiguraVec3.oneUse(x, y, z));
    }

    @Override
    @LuaWhitelist
    @LuaMethodDoc("offsetRot")
    public VanillaPart setOffsetRot(FiguraVec3 rot) {
        for (VanillaPart part : cachedParts)
            part.setOffsetRot(rot);
        return super.setOffsetRot(rot);
    }

    @Override
    @LuaWhitelist
    public VanillaPart setScale(Double x, Double y, Double z){
        return setScale(LuaUtils.parseVec3("setScale", x, y, z, 1, 1, 1));
    }
    
    @Override
    @LuaWhitelist
    @LuaMethodDoc("scale")
    public VanillaPart setScale(FiguraVec3 scale) {
        for (VanillaPart part : cachedParts)
            part.setScale(scale);
        return super.setScale(scale);
    }

    @Override
    @LuaWhitelist
    public VanillaPart setOffsetScale(Double x, Double y, Double z) {
        return setOffsetScale(LuaUtils.parseVec3("setOffsetScale", x, y, z, 1, 1, 1));
    }

    @Override
    @LuaWhitelist
    @LuaMethodDoc("offsetScale")
    public VanillaPart setOffsetScale(FiguraVec3 scale) {
        for (VanillaPart part : cachedParts)
            part.setOffsetScale(scale);
        return super.setOffsetScale(scale);
    }

    @LuaWhitelist
    public Object __index(String key) {
        return partMap.get(key);
    }

    @Override
    public String toString() {
        return "VanillaModelGroup";
    }
}
