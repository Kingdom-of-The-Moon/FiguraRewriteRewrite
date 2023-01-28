package org.moon.figura.lua.api.entity;

import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import org.moon.figura.lua.LuaWhitelist;
import org.moon.figura.lua.api.world.ItemStackAPI;
import org.moon.figura.lua.docs.LuaMethodDoc;
import org.moon.figura.lua.docs.LuaTypeDoc;
import org.moon.figura.mixin.LivingEntityAccessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@LuaWhitelist
@LuaTypeDoc(
        name = "LivingEntityAPI",
        value = "living_entity"
)
public class LivingEntityAPI<T extends LivingEntity> extends EntityAPI<T> {

    public LivingEntityAPI(T entity) {
        super(entity);
    }

    @LuaWhitelist
    public double getBodyYaw(Float delta) {
        checkEntity();
        if (delta == null) delta = 1f;
        return Mth.lerp(delta, entity.yBodyRotO, entity.yBodyRot);
    }

    @LuaWhitelist
    public ItemStackAPI getHeldItem(boolean offhand) {
        checkEntity();
        return ItemStackAPI.verify(offhand ? entity.getOffhandItem() : entity.getMainHandItem());
    }

    @LuaWhitelist
    public ItemStackAPI getActiveItem() {
        checkEntity();
        return ItemStackAPI.verify(entity.getUseItem());
    }

    @LuaWhitelist
    public int getActiveItemTime() {
        checkEntity();
        return entity.getTicksUsingItem();
    }

    @LuaWhitelist
    public float getHealth() {
        checkEntity();
        return entity.getHealth();
    }

    @LuaWhitelist
    public float getMaxHealth() {
        checkEntity();
        return entity.getMaxHealth();
    }

    @LuaWhitelist
    public float getArmor() {
        checkEntity();
        return entity.getArmorValue();
    }

    @LuaWhitelist
    public float getDeathTime() {
        checkEntity();
        return entity.deathTime;
    }

    @LuaWhitelist
    @LuaMethodDoc("")
    public List<Map<String, Object>> getStatusEffects() {
        checkEntity();
        List<Map<String, Object>> list = new ArrayList<>();

        for (MobEffectInstance effect : entity.getActiveEffects()) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", effect.getEffect().getDescriptionId());
            map.put("amplifier", effect.getAmplifier());
            map.put("duration", effect.getDuration());
            map.put("visible", effect.isVisible());

            list.add(map);
        }

        return list;
    }

    @LuaWhitelist
    public int getArrowCount() {
        checkEntity();
        return entity.getArrowCount();
    }

    @LuaWhitelist
    public int getStingerCount() {
        checkEntity();
        return entity.getStingerCount();
    }

    @LuaWhitelist
    public boolean isLeftHanded() {
        checkEntity();
        return entity.getMainArm() == HumanoidArm.LEFT;
    }

    @LuaWhitelist
    public boolean isUsingItem() {
        checkEntity();
        return entity.isUsingItem();
    }

    @LuaWhitelist
    public String getActiveHand() {
        checkEntity();
        return entity.getUsedItemHand().name();
    }

    @LuaWhitelist
    public boolean isClimbing() {
        checkEntity();
        return entity.onClimbable();
    }

    @LuaWhitelist
    public int getSwingTime() {
      checkEntity();
      return entity.swingTime;
    }

    @LuaWhitelist
    public boolean isSwingingArm() {
      checkEntity();
      return entity.swinging;
    }

    @LuaWhitelist
    public String getSwingArm() {
      checkEntity();
      return entity.swinging ? entity.swingingArm.name() : null;
    }

    @LuaWhitelist
    public int getSwingDuration() {
      checkEntity();
      return ((LivingEntityAccessor) entity).getSwingDuration();
    }

    @LuaWhitelist
    public float getAbsorptionAmount() {
        checkEntity();
        return entity.getAbsorptionAmount();
    }

    @LuaWhitelist
    public boolean isSensitiveToWater() {
        checkEntity();
        return entity.isSensitiveToWater();
    }

    @LuaWhitelist
    public String getEntityCategory() {
        checkEntity();

        MobType mobType = entity.getMobType(); //why it is not an enum
        if (mobType == MobType.ARTHROPOD)
            return "ARTHROPOD";
        if (mobType == MobType.UNDEAD)
            return "UNDEAD";
        if (mobType == MobType.WATER)
            return "WATER";
        if (mobType == MobType.ILLAGER)
            return "ILLAGER";

        return "UNDEFINED";
    }

    @LuaWhitelist
    public boolean isGliding() {
        checkEntity();
        return entity.isFallFlying();
    }

    @LuaWhitelist
    public boolean isBlocking() {
        checkEntity();
        return entity.isBlocking();
    }

    @LuaWhitelist
    public boolean isVisuallySwimming() {
        checkEntity();
        return entity.isVisuallySwimming();
    }

    @LuaWhitelist
    public boolean riptideSpinning() {
        checkEntity();
        return entity.isAutoSpinAttack();
    }

    @Override
    public String toString() {
        checkEntity();
        return (entity.hasCustomName() ? entity.getCustomName().getString() + " (" + getType() + ")" : getType() ) + " (LivingEntity)";
    }
}
