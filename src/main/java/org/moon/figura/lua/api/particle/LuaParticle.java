package org.moon.figura.lua.api.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.WakeParticle;
import org.moon.figura.avatar.Avatar;
import org.moon.figura.lua.LuaNotNil;
import org.moon.figura.lua.LuaWhitelist;
import org.moon.figura.lua.docs.LuaMethodDoc;
import org.moon.figura.lua.docs.LuaTypeDoc;
import org.moon.figura.math.vector.FiguraVec3;
import org.moon.figura.math.vector.FiguraVec4;
import org.moon.figura.mixin.particle.ParticleAccessor;
import org.moon.figura.trust.Trust;
import org.moon.figura.utils.LuaUtils;

@LuaWhitelist
@LuaTypeDoc(
        name = "Particle",
        value = "particle"
)
public class LuaParticle {

    private final String name;
    private final Avatar owner;
    private final Particle particle;

    private FiguraVec3 pos = FiguraVec3.of();
    private FiguraVec3 vel = FiguraVec3.of();
    private FiguraVec4 color = FiguraVec4.of(1, 1, 1, 1);
    private float power, scale = 1f;

    public LuaParticle(String name, Particle particle, Avatar owner) {
        this.name = name;
        this.particle = particle;
        this.owner = owner;
    }

    @LuaWhitelist
    public LuaParticle spawn() {
        if (!Minecraft.getInstance().isPaused()) {
            if (owner.particlesRemaining.use()) {
                ParticleAPI.getParticleEngine().figura$spawnParticle(particle, owner.owner);
                owner.trustIssues.remove(Trust.PARTICLES);
            } else {
                owner.trustIssues.add(Trust.PARTICLES);
            }
        }
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
    public FiguraVec3 getPos() {
        return pos.copy();
    }

    @LuaWhitelist
    public void setPos(double x, double y, double z) {
        setPos(FiguraVec3.oneUse(x, y, z));
    }

    @LuaWhitelist
    public void setPos(@LuaNotNil FiguraVec3 pos) {
        particle.setPos(pos.x, pos.y, pos.z);

        ParticleAccessor p = (ParticleAccessor) particle;
        p.setXo(pos.x);
        p.setYo(pos.y);
        p.setZo(pos.z);
        this.pos = pos.copy();
    }

    @LuaWhitelist
    public LuaParticle pos(double x, double y, double z) {
        return pos(FiguraVec3.oneUse(x, y, z));
    }

    @LuaWhitelist
    @LuaMethodDoc("setPos")
    public LuaParticle pos(@LuaNotNil FiguraVec3 pos) {
        setPos(pos);
        return this;
    }

    @LuaWhitelist
    public FiguraVec3 getVelocity() {
        return vel.copy();
    }

    @LuaWhitelist
    public void setVelocity(double x, double y, double z) {
        setVelocity(FiguraVec3.oneUse(x, y, z));
    }

    @LuaWhitelist
    public void setVelocity(@LuaNotNil FiguraVec3 velocity) {
        particle.setParticleSpeed(velocity.x, velocity.y, velocity.z);
        this.vel = velocity.copy();
    }

    @LuaWhitelist
    public LuaParticle velocity(double x, double y, double z) {
        return velocity(FiguraVec3.oneUse(x, y, z));
    }

    @LuaWhitelist
    @LuaMethodDoc("setVelocity")
    public LuaParticle velocity(@LuaNotNil FiguraVec3 velocity) {
        setVelocity(velocity);
        return this;
    }

    @LuaWhitelist
    public FiguraVec4 getColor() {
        return color.copy();
    }

    @LuaWhitelist
    public void setColor(Double r, Double g, Double b, Double a) {
        setColor(LuaUtils.freeVec4("color", r, g, b, a, 1, 1, 1, 1));
    }

    @LuaWhitelist
    public void setColor(FiguraVec3 rgb) {
        setColor(FiguraVec4.oneUse(rgb.x, rgb.y, rgb.z, 1));
    }

    @LuaWhitelist
    public void setColor(FiguraVec4 rgba) {
        particle.setColor((float) rgba.x, (float) rgba.y, (float) rgba.z);
        ((ParticleAccessor) particle).setParticleAlpha((float) rgba.w);
        this.color = rgba.copy();
    }

    @LuaWhitelist
    public LuaParticle color(Double r, Double g, Double b, Double a) {
        return color(LuaUtils.freeVec4("color", r, g, b, a, 1, 1, 1, 1));
    }

    @LuaWhitelist
    public LuaParticle color(FiguraVec3 rgb) {
        return color(FiguraVec4.oneUse(rgb.x, rgb.y, rgb.z, 1));
    }

    @LuaWhitelist
    @LuaMethodDoc("setColor")
    public LuaParticle color(FiguraVec4 rgba) {
        setColor(rgba);
        return this;
    }

    @LuaWhitelist
    public int getLifetime() {
        return particle.getLifetime();
    }

    @LuaWhitelist
    public void setLifetime(int age) {
        particle.setLifetime(Math.max(particle instanceof WakeParticle ? Math.min(age, 60) : age, 0));
    }

    @LuaWhitelist
    @LuaMethodDoc("setLifetime")
    public LuaParticle lifetime(int age) {
        setLifetime(age);
        return this;
    }

    @LuaWhitelist
    public float getPower() {
        return power;
    }

    @LuaWhitelist
    public void setPower(float power) {
        particle.setPower(power);
        this.power = power;
    }

    @LuaWhitelist
    @LuaMethodDoc("setPower")
    public LuaParticle power(float power) {
        setPower(power);
        return this;
    }

    @LuaWhitelist
    public float getScale() {
        return scale;
    }

    @LuaWhitelist
    public void setScale(float scale) {
        particle.scale(scale);
        this.scale = scale;
    }

    @LuaWhitelist
    @LuaMethodDoc("setScale")
    public LuaParticle scale(float scale) {
        setScale(scale);
        return this;
    }

    @LuaWhitelist
    public float getGravity() {
        return ((ParticleAccessor) particle).getGravity();
    }

    @LuaWhitelist
        public void setGravity(float gravity) {
        ((ParticleAccessor) particle).setGravity(gravity);
    }

    @LuaWhitelist
    @LuaMethodDoc("setGravity")
    public LuaParticle gravity(float gravity) {
        setGravity(gravity);
        return this;
    }

    @LuaWhitelist
    public boolean hasPhysics() {
        return ((ParticleAccessor) particle).getHasPhysics();
    }

    @LuaWhitelist
    public void setPhysics(boolean physics) {
        ((ParticleAccessor) particle).setHasPhysics(physics);
    }

    @LuaWhitelist
    @LuaMethodDoc("setPhysics")
    public LuaParticle physics(boolean physics) {
        setPhysics(physics);
        return this;
    }

    public String toString() {
        return name + " (Particle)";
    }
}
