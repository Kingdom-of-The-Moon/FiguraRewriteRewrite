package org.moon.figura.lua.api.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.WakeParticle;
import org.moon.figura.avatar.Avatar;
import org.moon.figura.lua.LuaNotNil;
import org.moon.figura.lua.LuaWhitelist;
import org.moon.figura.lua.docs.LuaTypeDoc;
import org.moon.figura.math.vector.FiguraVec3;
import org.moon.figura.math.vector.FiguraVec4;
import org.moon.figura.mixin.particle.ParticleAccessor;
import org.moon.figura.utils.LuaUtils;

@LuaWhitelist
@LuaTypeDoc(
        name = "Particle",
        value = "particle"
)
public class LuaParticle {

    private final Avatar owner;
    private final Particle particle;

    public LuaParticle(Particle particle, Avatar owner) {
        this.particle = particle;
        this.owner = owner;
    }

    @LuaWhitelist
    public LuaParticle spawn() {
        if (!Minecraft.getInstance().isPaused() && owner.particlesRemaining.use())
            ParticleAPI.getParticleEngine().figura$spawnParticle(particle, owner.owner);
        return this;
    }

    @LuaWhitelist
    public LuaParticle remove() {
        particle.remove();
        return this;
    }

    @LuaWhitelist
    public boolean isAlive() {
        return particle.isAlive();
    }

    @LuaWhitelist
    public int getLifetime() {
        return particle.getLifetime();
    }

    @LuaWhitelist
    public LuaParticle pos(Double x, Double y, Double z){
        return pos(LuaUtils.freeVec3("pos", x, y, z));
    }

    @LuaWhitelist
    public LuaParticle pos(@LuaNotNil FiguraVec3 pos) {
        particle.setPos(pos.x, pos.y, pos.z);

        ParticleAccessor p = (ParticleAccessor) particle;
        p.setXo(pos.x);
        p.setYo(pos.y);
        p.setZo(pos.z);
        return this;
    }

    @LuaWhitelist
    public LuaParticle velocity(Double x, Double y, Double z){
        return velocity(LuaUtils.freeVec3("velocity", x, y, z));
    }

    @LuaWhitelist
    public LuaParticle velocity(@LuaNotNil FiguraVec3 velocity) {
        particle.setParticleSpeed(velocity.x, velocity.y, velocity.z);
        return this;
    }

    @LuaWhitelist
    public LuaParticle color(Double r, Double g, Double b, Double a){
        return color(LuaUtils.freeVec4("color", r, g, b, a, 1, 1, 1, 1));
    }

    @LuaWhitelist
    public LuaParticle color(FiguraVec3 rgb){
        return color(FiguraVec4.oneUse(rgb.x, rgb.y, rgb.z, 1));
    }

    @LuaWhitelist
    public LuaParticle color(FiguraVec4 rgba) {
        particle.setColor((float) rgba.x, (float) rgba.y, (float) rgba.z);
        ((ParticleAccessor) particle).setParticleAlpha((float) rgba.w);
        return this;
    }

    @LuaWhitelist
    public LuaParticle lifetime(int age) {
        particle.setLifetime(Math.max(particle instanceof WakeParticle ? Math.min(age, 60) : age, 0));
        return this;
    }

    @LuaWhitelist
    public LuaParticle power(float power) {
        particle.setPower(power);
        return this;
    }

    @LuaWhitelist
    public LuaParticle scale(float scale) {
        particle.scale(scale);
        return this;
    }

    @LuaWhitelist
    public LuaParticle gravity(float gravity) {
        ((ParticleAccessor) particle).setGravity(gravity);
        return this;
    }

    @LuaWhitelist
    public LuaParticle physics(boolean physics) {
        ((ParticleAccessor) particle).setHasPhysics(physics);
        return this;
    }

    public String toString() {
        return particle.getClass().getSimpleName() + " (Particle)";
    }
}
