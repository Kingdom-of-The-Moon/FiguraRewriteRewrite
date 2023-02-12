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
import org.moon.figura.permissions.Permissions;
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
                owner.noPermissions.remove(Permissions.PARTICLES);
            } else {
                owner.noPermissions.add(Permissions.PARTICLES);
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
    public LuaParticle setPos(double x, double y, double z) {
        return setPos(FiguraVec3.oneUse(x, y, z));
    }

    @LuaWhitelist
    @LuaMethodDoc("pos")
    public LuaParticle setPos(@LuaNotNil FiguraVec3 pos) {
        particle.setPos(pos.x, pos.y, pos.z);

        ParticleAccessor p = (ParticleAccessor) particle;
        p.setXo(pos.x);
        p.setYo(pos.y);
        p.setZo(pos.z);
        this.pos = pos.copy();
        return this;
    }

    @LuaWhitelist
    public FiguraVec3 getVelocity() {
        return vel.copy();
    }

    @LuaWhitelist
    public LuaParticle setVelocity(double x, double y, double z) {
        return setVelocity(FiguraVec3.oneUse(x, y, z));
    }

    @LuaWhitelist
    @LuaMethodDoc("velocity")
    public LuaParticle setVelocity(@LuaNotNil FiguraVec3 velocity) {
        particle.setParticleSpeed(velocity.x, velocity.y, velocity.z);
        this.vel = velocity.copy();
        return this;
    }

    @LuaWhitelist
    public FiguraVec4 getColor() {
        return color.copy();
    }

    @LuaWhitelist
    public LuaParticle setColor(Double r, Double g, Double b, Double a) {
        return setColor(LuaUtils.freeVec4("color", r, g, b, a, 1, 1, 1, 1));
    }

    @LuaWhitelist
    public LuaParticle setColor(FiguraVec3 rgb) {
        return setColor(FiguraVec4.oneUse(rgb.x, rgb.y, rgb.z, 1));
    }

    @LuaWhitelist
    @LuaMethodDoc("color")
    public LuaParticle setColor(FiguraVec4 rgba) {
        particle.setColor((float) rgba.x, (float) rgba.y, (float) rgba.z);
        ((ParticleAccessor) particle).setParticleAlpha((float) rgba.w);
        this.color = rgba.copy();
        return this;
    }

    @LuaWhitelist
    public int getLifetime() {
        return particle.getLifetime();
    }

    @LuaWhitelist
    @LuaMethodDoc("lifetime")
    public LuaParticle setLifetime(int age) {
        particle.setLifetime(Math.max(particle instanceof WakeParticle ? Math.min(age, 60) : age, 0));
        return this;
    }

    @LuaWhitelist
    public float getPower() {
        return power;
    }

    @LuaWhitelist
    @LuaMethodDoc("power")
    public LuaParticle setPower(float power) {
        particle.setPower(power);
        this.power = power;
        return this;
    }

    @LuaWhitelist
    public float getScale() {
        return scale;
    }

    @LuaWhitelist
    @LuaMethodDoc("scale")
    public LuaParticle setScale(float scale) {
        particle.scale(scale);
        this.scale = scale;
        return this;
    }

    @LuaWhitelist
    public float getGravity() {
        return ((ParticleAccessor) particle).getGravity();
    }

    @LuaWhitelist
    @LuaMethodDoc("gravity")
    public LuaParticle setGravity(float gravity) {
        ((ParticleAccessor) particle).setGravity(gravity);
        return this;
    }

    @LuaWhitelist
    public boolean hasPhysics() {
        return ((ParticleAccessor) particle).getHasPhysics();
    }

    @LuaWhitelist
    @LuaMethodDoc("physics")
    public LuaParticle setPhysics(boolean physics) {
        ((ParticleAccessor) particle).setHasPhysics(physics);
        return this;
    }

    public String toString() {
        return name + " (Particle)";
    }
}
