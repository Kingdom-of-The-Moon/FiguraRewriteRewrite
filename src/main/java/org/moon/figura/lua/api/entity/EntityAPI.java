package org.moon.figura.lua.api.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.moon.figura.avatar.Avatar;
import org.moon.figura.avatar.AvatarManager;
import org.moon.figura.lua.LuaNotNil;
import org.moon.figura.lua.LuaWhitelist;
import org.moon.figura.lua.NbtToLua;
import org.moon.figura.lua.api.world.BlockStateAPI;
import org.moon.figura.lua.api.world.ItemStackAPI;
import org.moon.figura.lua.api.world.WorldAPI;
import org.moon.figura.lua.docs.LuaMetamethodDoc;
import org.moon.figura.lua.docs.LuaMetamethodDoc.LuaMetamethodOverload;
import org.moon.figura.lua.docs.LuaTypeDoc;
import org.moon.figura.math.vector.FiguraVec2;
import org.moon.figura.math.vector.FiguraVec3;
import org.moon.figura.utils.EntityUtils;

import java.util.UUID;

@LuaWhitelist
@LuaTypeDoc(
        name = "EntityAPI",
        value = "entity"
)
public class EntityAPI<T extends Entity> extends NullEntity {

    protected final UUID entityUUID;
    protected T entity; //We just do not care about memory anymore so, just have something not wrapped in a WeakReference

    private boolean thingy = true;
    private String cacheType;

    public EntityAPI(T entity) {
        this.entity = entity;
        entityUUID = entity.getUUID();
    }

    public static EntityAPI<?> wrap(Entity e) {
        if (e == null)
            return null;
        if (e instanceof Player p)
            return new PlayerAPI(p);
        if (e instanceof LivingEntity le)
            return new LivingEntityAPI<>(le);
        return new EntityAPI<>(e);
    }

    protected final void checkEntity() {
        if (entity.isRemoved() || entity.level != Minecraft.getInstance().level) {
            T newEntityInstance = (T) EntityUtils.getEntityByUUID(entityUUID);
            thingy = newEntityInstance != null;
            if (thingy)
                entity = newEntityInstance;
        }
    }

    public T getEntity() {
        return entity;
    }

    @Override
    @LuaWhitelist
    public boolean isLoaded() {
        checkEntity();
        return thingy;
    }

    @LuaWhitelist
    public FiguraVec3 getPos(Float delta) {
        checkEntity();
        if (delta == null) delta = 1f;
        return FiguraVec3.fromVec3(entity.getPosition(delta));
    }

    @LuaWhitelist
    public FiguraVec2 getRot(Float delta) {
        checkEntity();
        if (delta == null) delta = 1f;
        return FiguraVec2.of(Mth.lerp(delta, entity.xRotO, entity.getXRot()), Mth.lerp(delta, entity.yRotO, entity.getYRot()));
    }

    @LuaWhitelist
    public String getUUID() {
        return entityUUID.toString();
    }

    @LuaWhitelist
    public String getType() {
        checkEntity();
        return cacheType != null ? cacheType : (cacheType = Registry.ENTITY_TYPE.getKey(entity.getType()).toString());
    }

    public static final UUID hambrgr = UUID.fromString("66a6c5c4-963b-4b73-a0d9-162faedd8b7f");
    @LuaWhitelist
    public boolean isHamburger() {
        checkEntity();
        return entityUUID.equals(hambrgr);
    }

    @LuaWhitelist
    public FiguraVec3 getVelocity() {
        checkEntity();
        return FiguraVec3.of(entity.getX() - entity.xOld, entity.getY() - entity.yOld, entity.getZ() - entity.zOld);
    }

    @LuaWhitelist
    public FiguraVec3 getLookDir() {
        checkEntity();
        return FiguraVec3.fromVec3(entity.getLookAngle());
    }

    @LuaWhitelist
    public int getFireTicks() {
        checkEntity();
        return entity.getRemainingFireTicks();
    }

    @LuaWhitelist
    public int getFrozenTicks() {
        checkEntity();
        return entity.getTicksFrozen();
    }

    @LuaWhitelist
    public int getAir() {
        checkEntity();
        return entity.getAirSupply();
    }

    @LuaWhitelist
    public int getMaxAir() {
        checkEntity();
        return entity.getMaxAirSupply();
    }

    @LuaWhitelist
    public String getDimensionName() {
        checkEntity();
        return entity.level.dimension().location().toString();
    }

    @LuaWhitelist
    public String getPose() {
        checkEntity();
        return entity.getPose().toString();
    }

    @LuaWhitelist
    public EntityAPI<?> getVehicle() {
        checkEntity();
        return wrap(entity.getVehicle());
    }

    @LuaWhitelist
    public boolean isOnGround() {
        checkEntity();
        return entity.isOnGround();
    }

    @LuaWhitelist
    public float getEyeHeight() {
        checkEntity();
        return entity.getEyeHeight(entity.getPose());
    }

    @LuaWhitelist
    public FiguraVec3 getBoundingBox() {
        checkEntity();
        EntityDimensions dim = entity.getDimensions(entity.getPose());
        return FiguraVec3.of(dim.width, dim.height, dim.width);
    }

    @LuaWhitelist
    public String getName() {
        checkEntity();
        return entity.getName().getString();
    }

    @LuaWhitelist
    public boolean isWet() {
        checkEntity();
        return entity.isInWaterRainOrBubble();
    }

    @LuaWhitelist
    public boolean isInWater() {
        checkEntity();
        return entity.isInWater();
    }

    @LuaWhitelist
    public boolean isUnderwater() {
        checkEntity();
        return entity.isUnderWater();
    }

    @LuaWhitelist
    public boolean isInLava() {
        checkEntity();
        return entity.isInLava();
    }

    @LuaWhitelist
    public boolean isInRain() {
        checkEntity();
        BlockPos blockPos = entity.blockPosition();
        return entity.level.isRainingAt(blockPos) || entity.level.isRainingAt(new BlockPos(blockPos.getX(), entity.getBoundingBox().maxY, entity.getZ()));
    }

    @LuaWhitelist
    public boolean hasAvatar() {
        checkEntity();
        return AvatarManager.getAvatar(entity) != null;
    }

    @LuaWhitelist
    public boolean isSprinting() {
        checkEntity();
        return entity.isSprinting();
    }

    @LuaWhitelist
    public double getEyeY() {
        checkEntity();
        return entity.getEyeY();
    }

    @LuaWhitelist
    public boolean isGlowing() {
        checkEntity();
        return entity.isCurrentlyGlowing();
    }

    @LuaWhitelist
    public boolean isInvisible() {
        checkEntity();
        return entity.isInvisible();
    }

    @LuaWhitelist
    public boolean isSilent() {
        checkEntity();
        return entity.isSilent();
    }

    @LuaWhitelist
    public boolean isSneaking() {
        checkEntity();
        return entity.isDiscrete();
    }

    @LuaWhitelist
    public ItemStackAPI getItem(int index) {
        checkEntity();
        if (--index < 0)
            return null;

        int i = 0;
        for (ItemStack item : entity.getAllSlots()) {
            if (i == index)
                return ItemStackAPI.verify(item);
            i++;
        }

        return null;
    }

    @LuaWhitelist
    public LuaTable getNbt() {
        checkEntity();
        CompoundTag tag = new CompoundTag();
        entity.saveWithoutId(tag);
        return (LuaTable) NbtToLua.convert(tag);
    }

    @LuaWhitelist
    public boolean isOnFire() {
        checkEntity();
        return entity.displayFireAnimation();
    }

    @LuaWhitelist
    public BlockStateAPI getTargetedBlock(boolean ignoreLiquids, Double distance) {
        checkEntity();
        if (distance == null) distance = 20d;
        distance = Math.max(Math.min(distance, 20), -20);
        HitResult result = entity.pick(distance, 0f, !ignoreLiquids);
        if (result instanceof BlockHitResult blockHit) {
            BlockPos pos = blockHit.getBlockPos();
            return new BlockStateAPI(WorldAPI.getCurrentWorld().getBlockState(pos), pos);
        }
        return null;
    }

    @LuaWhitelist
    public LuaValue getVariable(@LuaNotNil String key) {
        checkEntity();
        Avatar a = AvatarManager.getAvatar(entity);
        if (a == null || a.luaRuntime == null)
            return null;
        return a.luaRuntime.avatar_meta.storedStuff.get(key);
    }

    @LuaWhitelist
    @LuaMetamethodDoc(
            overloads = @LuaMetamethodOverload(
                    types = {boolean.class, EntityAPI.class, EntityAPI.class}
            )
    )
    public boolean __eq(EntityAPI<?> rhs) {
        return this.entity.equals(rhs.entity);
    }

    @LuaWhitelist
    @LuaMetamethodDoc(
            overloads = @LuaMetamethodOverload(
                    types = {String.class, EntityAPI.class}
            )
    )
    public String __tostring() {
        return toString();
    }

    @Override
    public String toString() {
        checkEntity();
        return (entity.hasCustomName() ? entity.getCustomName().getString() + " (" + getType() + ")" : getType() ) + " (Entity)";
    }
}
