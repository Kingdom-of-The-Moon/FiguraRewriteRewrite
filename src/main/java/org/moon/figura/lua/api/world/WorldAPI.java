package org.moon.figura.lua.api.world;

import com.mojang.brigadier.StringReader;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Marker;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.moon.figura.avatar.Avatar;
import org.moon.figura.avatar.AvatarManager;
import org.moon.figura.lua.LuaNotNil;
import org.moon.figura.lua.LuaWhitelist;
import org.moon.figura.lua.ReadOnlyLuaTable;
import org.moon.figura.lua.api.entity.EntityAPI;
import org.moon.figura.lua.api.entity.PlayerAPI;
import org.moon.figura.lua.docs.LuaTypeDoc;
import org.moon.figura.math.vector.FiguraVec3;
import org.moon.figura.utils.EntityUtils;
import org.moon.figura.utils.LuaUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@LuaWhitelist
@LuaTypeDoc(
        name = "WorldAPI",
        value = "world"
)
public class WorldAPI {

    public static final WorldAPI INSTANCE = new WorldAPI();

    public static Level getCurrentWorld() {
        return Minecraft.getInstance().level;
    }

    @LuaWhitelist
    public static BiomeAPI getBiome(Double x, Double y, Double z){
        return getBiome(LuaUtils.freeVec3("getBiome", x, y, z));
    }

    @LuaWhitelist
    public static BiomeAPI getBiome(@LuaNotNil FiguraVec3 pos) {
        BiomeAPI result = new BiomeAPI(getCurrentWorld().getBiome(pos.asBlockPos()).value(), pos.asBlockPos());
        pos.free();
        return result;
    }

    @LuaWhitelist
    public static BlockStateAPI getBlockState(Double x, Double y, Double z){
        return getBlockState(LuaUtils.freeVec3("getBlockState", x, y, z));
    }

    @LuaWhitelist
    public static BlockStateAPI getBlockState(@LuaNotNil FiguraVec3 pos) {
        BlockPos blockPos = pos.asBlockPos();
        pos.free();
        Level world = getCurrentWorld();
        if (world.getChunkAt(blockPos) == null)
            return new BlockStateAPI(Blocks.AIR.defaultBlockState(), blockPos);
        return new BlockStateAPI(world.getBlockState(blockPos), blockPos);
    }

    public static int getRedstonePower(Double x, Double y, Double z){
        return getRedstonePower(LuaUtils.freeVec3("getRedstonePower", x, y, z));
    }

    @LuaWhitelist
    public static int getRedstonePower(@LuaNotNil FiguraVec3 pos) {
        BlockPos blockPos = pos.asBlockPos();
        pos.free();
        if (getCurrentWorld().getChunkAt(blockPos) == null)
            return 0;
        return getCurrentWorld().getBestNeighborSignal(blockPos);
    }

    @LuaWhitelist
    public static int getStrongRedstonePower(Double x, Double y, Double z){
        return getStrongRedstonePower(LuaUtils.freeVec3("getStrongRedstonePower", x, y, z));
    }

    @LuaWhitelist
    public static int getStrongRedstonePower(@LuaNotNil FiguraVec3 pos) {
        BlockPos blockPos = pos.asBlockPos();
        pos.free();
        if (getCurrentWorld().getChunkAt(blockPos) == null)
            return 0;
        return getCurrentWorld().getDirectSignalTo(blockPos);
    }

    @LuaWhitelist
    public static double getTime(double delta) {
        return getCurrentWorld().getGameTime() + delta;
    }

    @LuaWhitelist
    public static double getTimeOfDay(double delta) {
        return getCurrentWorld().getDayTime() + delta;
    }

    @LuaWhitelist
    public static int getMoonPhase() {
        return getCurrentWorld().getMoonPhase();
    }

    @LuaWhitelist
    public static double getRainGradient(Float delta) {
        if (delta == null) delta = 1f;
        return getCurrentWorld().getRainLevel(delta);
    }

    @LuaWhitelist
    public static boolean isThundering() {
        return getCurrentWorld().isThundering();
    }

    @LuaWhitelist
    public static int getLightLevel(Double x, Double y, Double z){
        return getLightLevel(LuaUtils.freeVec3("getLightLevel", x, y, z));
    }

    @LuaWhitelist
    public static int getLightLevel(FiguraVec3 pos) {
        BlockPos blockPos = pos.asBlockPos();
        pos.free();
        Level world = getCurrentWorld();
        if (world.getChunkAt(blockPos) == null)
            return 0;
        world.updateSkyBrightness();
        return world.getLightEngine().getRawBrightness(blockPos, world.getSkyDarken());
    }

    @LuaWhitelist
    public static int getSkyLightLevel(Double x, Double y, Double z){
        return getSkyLightLevel(LuaUtils.freeVec3("getSkyLightLevel", x, y, z));
    }

    @LuaWhitelist
    public static int getSkyLightLevel(FiguraVec3 pos) {
        BlockPos blockPos = pos.asBlockPos();
        pos.free();
        Level world = getCurrentWorld();
        if (world.getChunkAt(blockPos) == null)
            return 0;
        return world.getBrightness(LightLayer.SKY, blockPos);
    }

    @LuaWhitelist
    public static int getBlockLightLevel(Double x, Double y, Double z){
        return getBlockLightLevel(LuaUtils.freeVec3("getBlockLightLevel", x, y, z));
    }

    @LuaWhitelist
    public static int getBlockLightLevel(FiguraVec3 pos) {
        BlockPos blockPos = pos.asBlockPos();
        pos.free();
        Level world = getCurrentWorld();
        if (world.getChunkAt(blockPos) == null)
            return 0;
        return world.getBrightness(LightLayer.BLOCK, blockPos);
    }

    @LuaWhitelist
    public static boolean isOpenSky(Double x, Double y, Double z){
        return isOpenSky(LuaUtils.freeVec3("isOpenSky", x, y, z));
    }

    @LuaWhitelist
    public static boolean isOpenSky(@LuaNotNil FiguraVec3 pos) {
        BlockPos blockPos = pos.asBlockPos();
        pos.free();
        Level world = getCurrentWorld();
        if (world.getChunkAt(blockPos) == null)
            return false;
        return world.canSeeSky(blockPos);
    }

    @LuaWhitelist
    public static String getDimension() {
        Level world = getCurrentWorld();
        return world.dimension().location().toString();
    }

    @LuaWhitelist
    public static Map<String, EntityAPI<?>> getPlayers() {
        HashMap<String, EntityAPI<?>> playerList = new HashMap<>();
        for (Player player : getCurrentWorld().players())
            playerList.put(player.getName().getString(), PlayerAPI.wrap(player));
        return playerList;
    }

    @LuaWhitelist
    public static EntityAPI<?> getEntity(@LuaNotNil String uuid) {
        try {
            return EntityAPI.wrap(EntityUtils.getEntityByUUID(UUID.fromString(uuid)));
        } catch (Exception ignored) {
            throw new LuaError("Invalid UUID");
        }
    }

    //@LuaWhitelist
    public HashMap<String, Object> raycastBlock(boolean fluid, Double x, Double y, Double z){
        return raycastBlock(fluid, LuaUtils.freeVec3("addParticle", x, y, z));
    }

    //@LuaWhitelist
    public HashMap<String, Object> raycastBlock(boolean fluid, FiguraVec3 vec){
        return raycastBlock(fluid, vec, FiguraVec3.oneUse());
    }

    //@LuaWhitelist
    public HashMap<String, Object> raycastBlock(boolean fluid, Double x, Double y, Double z, Double axisX, Double axisY, Double axisZ){
        return raycastBlock(fluid, LuaUtils.freeVec3("addParticle", x, y, z), LuaUtils.freeVec3("addParticle", axisX, axisY, axisZ));
    }

    //@LuaWhitelist
    public HashMap<String, Object> raycastBlock(boolean fluid, FiguraVec3 vec, Double axisX, Double axisY, Double axisZ){
        return raycastBlock(fluid, vec, LuaUtils.freeVec3("addParticle", axisX, axisY, axisZ));
    }

    //@LuaWhitelist
    public HashMap<String, Object> raycastBlock(boolean fluid, Double x, Double y, Double z, FiguraVec3 axis){
        return raycastBlock(fluid, LuaUtils.freeVec3("addParticle", x, y, z), axis);
    }

    //@LuaWhitelist
    public HashMap<String, Object> raycastBlock(boolean fluid, FiguraVec3 start, FiguraVec3 end) {
        if (true) return null;

        BlockHitResult result = getCurrentWorld().clip(new ClipContext(start.asVec3(), end.asVec3(), ClipContext.Block.OUTLINE, fluid ? ClipContext.Fluid.NONE : ClipContext.Fluid.ANY, new Marker(EntityType.MARKER, getCurrentWorld())));

        if (result == null || result.getType() == HitResult.Type.MISS)
            return null;

        HashMap<String, Object> map = new HashMap<>();
        BlockPos pos = result.getBlockPos();
        map.put("block", getBlockState((double) pos.getX(), (double) pos.getY(), (double) pos.getZ()));
        map.put("direction", result.getDirection().getName());
        map.put("pos", FiguraVec3.fromVec3(result.getLocation()));

        return map;
    }

    //@LuaWhitelist
    public HashMap<String, Object> raycastEntity(Double x, Double y, Double z){
        return raycastEntity(LuaUtils.freeVec3("addParticle", x, y, z));
    }

    //@LuaWhitelist
    public HashMap<String, Object> raycastEntity(FiguraVec3 vec){
        return raycastEntity(vec, FiguraVec3.oneUse());
    }

    //@LuaWhitelist
    public HashMap<String, Object> raycastEntity(Double x, Double y, Double z, Double axisX, Double axisY, Double axisZ){
        return raycastEntity(LuaUtils.freeVec3("addParticle", x, y, z), LuaUtils.freeVec3("addParticle", axisX, axisY, axisZ));
    }

    //@LuaWhitelist
    public HashMap<String, Object> raycastEntity(FiguraVec3 vec, Double axisX, Double axisY, Double axisZ){
        return raycastEntity(vec, LuaUtils.freeVec3("addParticle", axisX, axisY, axisZ));
    }

    //@LuaWhitelist
    public HashMap<String, Object> raycastEntity(Double x, Double y, Double z, FiguraVec3 axis){
        return raycastEntity(LuaUtils.freeVec3("addParticle", x, y, z), axis);
    }

    //@LuaWhitelist
    public HashMap<String, Object> raycastEntity(FiguraVec3 start, FiguraVec3 end) {
        if (true) return null;

        EntityHitResult result = ProjectileUtil.getEntityHitResult(new Marker(EntityType.MARKER, getCurrentWorld()), start.asVec3(), end.asVec3(), new AABB(start.asVec3(), end.asVec3()), entity -> true, Double.MAX_VALUE);

        if (result == null)
            return null;

        HashMap<String, Object> map = new HashMap<>();
        map.put("entity", EntityAPI.wrap(result.getEntity()));
        map.put("pos", FiguraVec3.fromVec3(result.getLocation()));

        return map;
    }

    @LuaWhitelist
    public static Map<String, LuaTable> avatarVars() {
        HashMap<String, LuaTable> varList = new HashMap<>();
        for (Avatar avatar : AvatarManager.getLoadedAvatars()) {
            LuaTable tbl = avatar.luaRuntime == null ? new LuaTable() : avatar.luaRuntime.avatar_meta.storedStuff;
            varList.put(avatar.owner.toString(), new ReadOnlyLuaTable(tbl));
        }
        return varList;
    }

    @LuaWhitelist
    public static BlockStateAPI newBlock(String block){
        return newBlock(block, 0, 0, 0);
    }

    @LuaWhitelist
    public static BlockStateAPI newBlock(String block, double x, double y, double z){
        return newBlock(block, LuaUtils.freeVec3("newBlock", x, y, z));
    }

    @LuaWhitelist
    public static BlockStateAPI newBlock(String block, FiguraVec3 pos) {
        BlockPos blockPos = pos.asBlockPos();
        try {
            BlockState blockState = BlockStateArgument.block(new CommandBuildContext(RegistryAccess.BUILTIN.get())).parse(new StringReader(block)).getState();
            return new BlockStateAPI(blockState, blockPos);
        } catch (Exception e) {
            throw new LuaError("Could not parse block state from string: " + block);
        }
    }

    @LuaWhitelist
    public static ItemStackAPI newItem(@LuaNotNil String item){
        return newItem(item, 1);
    }

    @LuaWhitelist
    public static ItemStackAPI newItem(@LuaNotNil String item, @LuaNotNil int count){
        return newItem(item, count, 0);
    }

    @LuaWhitelist
    public static ItemStackAPI newItem(@LuaNotNil String item, @LuaNotNil int count, int damage) {
        try {
            ItemStack itemStack = ItemArgument.item(new CommandBuildContext(RegistryAccess.BUILTIN.get())).parse(new StringReader(item)).createItemStack(1, false);
            itemStack.setCount(count);
            itemStack.setDamageValue(damage);
            return new ItemStackAPI(itemStack);
        } catch (Exception e) {
            throw new LuaError("Could not parse item stack from string: " + item);
        }
    }

    @LuaWhitelist
    public static boolean exists() {
        return getCurrentWorld() != null;
    }

    @Override
    public String toString() {
        return "WorldAPI";
    }
}
