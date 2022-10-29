package org.moon.figura.lua.api.particle;

import com.mojang.brigadier.StringReader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.commands.arguments.ParticleArgument;
import net.minecraft.core.particles.ParticleOptions;
import org.luaj.vm2.LuaError;
import org.moon.figura.avatar.Avatar;
import org.moon.figura.ducks.ParticleEngineAccessor;
import org.moon.figura.lua.LuaWhitelist;
import org.moon.figura.lua.docs.LuaTypeDoc;
import org.moon.figura.math.vector.FiguraVec3;
import org.moon.figura.utils.LuaUtils;

@LuaWhitelist
@LuaTypeDoc(
        name = "ParticleAPI",
        value = "particles"
)
public class ParticleAPI {

    private final Avatar owner;

    public ParticleAPI(Avatar owner) {
        this.owner = owner;
    }

    public static ParticleEngineAccessor getParticleEngine() {
        return (ParticleEngineAccessor) Minecraft.getInstance().particleEngine;
    }

    private LuaParticle generate(String id, double x, double y, double z, double w, double t, double h) {
        try {
            ParticleOptions options = ParticleArgument.readParticle(new StringReader(id));
            Particle p = getParticleEngine().figura$makeParticle(options, x, y, z, w, t, h);
            if (p == null) throw new LuaError("Could not parse particle \"" + id + "\"");
            return new LuaParticle(p, owner);
        } catch (Exception e) {
            throw new LuaError(e.getMessage());
        }
    }

    @LuaWhitelist
    public LuaParticle addParticle(String name, Double x, Double y, Double z){
       return addParticle(name, LuaUtils.freeVec3("addParticle", x, y, z));
    }

    @LuaWhitelist
    public LuaParticle addParticle(String name, FiguraVec3 vec){
       return addParticle(name, vec, FiguraVec3.oneUse());
    }

    @LuaWhitelist
    public LuaParticle addParticle(String name, Double x, Double y, Double z, Double axisX, Double axisY, Double axisZ){
       return addParticle(name, LuaUtils.freeVec3("addParticle", x, y, z), LuaUtils.freeVec3("addParticle", axisX, axisY, axisZ));
    }

    @LuaWhitelist
    public LuaParticle addParticle(String name, FiguraVec3 vec, Double axisX, Double axisY, Double axisZ){
       return addParticle(name, vec, LuaUtils.freeVec3("addParticle", axisX, axisY, axisZ));
    }

    @LuaWhitelist
    public LuaParticle addParticle(String name, Double x, Double y, Double z, FiguraVec3 axis){
       return addParticle(name, LuaUtils.freeVec3("addParticle", x, y, z), axis);
    }

    @LuaWhitelist
    public LuaParticle addParticle(String name, FiguraVec3 pos, FiguraVec3 vel){

       return generate(name, pos.x, pos.y, pos.z, vel.x, vel.y, vel.z).spawn();

    }

    @LuaWhitelist
    public void removeParticles() {
        getParticleEngine().figura$clearParticles(owner.owner);
    }

    @LuaWhitelist
    public LuaParticle __index(String id) {
        return generate(id, 0, 0, 0, 0, 0, 0);
    }

    @Override
    public String toString() {
        return "ParticleAPI";
    }
}
