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
            return new LuaParticle(id, p, owner);
        } catch (Exception e) {
            throw new LuaError(e.getMessage());
        }
    }

    @LuaWhitelist
    public LuaParticle newParticle(String name, Double x, Double y, Double z) {
       return newParticle(name, FiguraVec3.oneUse(x, y, z));
    }

    @LuaWhitelist
    public LuaParticle newParticle(String name, FiguraVec3 vec) {
       return newParticle(name, vec, FiguraVec3.oneUse());
    }

    @LuaWhitelist
    public LuaParticle newParticle(String name, Double x, Double y, Double z, Double axisX, Double axisY, Double axisZ) {
       return newParticle(name, FiguraVec3.oneUse(x, y, z), FiguraVec3.oneUse(axisX, axisY, axisZ));
    }

    @LuaWhitelist
    public LuaParticle newParticle(String name, FiguraVec3 vec, Double axisX, Double axisY, Double axisZ) {
       return newParticle(name, vec, FiguraVec3.oneUse(axisX, axisY, axisZ));
    }

    @LuaWhitelist
    public LuaParticle newParticle(String name, Double x, Double y, Double z, FiguraVec3 axis) {
       return newParticle(name, FiguraVec3.oneUse(x, y, z), axis);
    }

    @LuaWhitelist
    public LuaParticle newParticle(String name, FiguraVec3 pos, FiguraVec3 vel) {

       return generate(name, pos.x, pos.y, pos.z, vel.x, vel.y, vel.z).spawn();

    }

    @LuaWhitelist
    public ParticleAPI removeParticles() {
        getParticleEngine().figura$clearParticles(owner.owner);
        return this;
    }

    @LuaWhitelist
    public boolean isPresent(String id) {
        try {
            ParticleOptions options = ParticleArgument.readParticle(new StringReader(id));
            return getParticleEngine().figura$makeParticle(options, 0, 0, 0, 0, 0, 0) != null;
        } catch (Exception ignored) {
            return false;
        }
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
