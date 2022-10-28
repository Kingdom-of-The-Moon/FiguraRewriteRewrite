package org.moon.figura.lua.api.world;

import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.luaj.vm2.LuaTable;
import org.moon.figura.lua.LuaWhitelist;
import org.moon.figura.lua.NbtToLua;
import org.moon.figura.lua.docs.LuaTypeDoc;
import org.moon.figura.math.vector.FiguraVec3;
import org.moon.figura.math.vector.FiguraVec6;
import org.moon.figura.mixin.BlockBehaviourAccessor;
import org.moon.figura.utils.ColorUtils;
import org.moon.figura.utils.LuaUtils;

import java.lang.reflect.Field;
import java.util.*;

@LuaWhitelist
@LuaTypeDoc(
        name = "BlockState",
        value = "blockstate"
)
public class BlockStateAPI {

    public final BlockState blockState;
    private BlockPos pos;

    @LuaWhitelist
    public final String id;
    @LuaWhitelist
    public final LuaTable properties;

    public BlockStateAPI(BlockState blockstate, BlockPos pos) {
        this.blockState = blockstate;
        this.pos = pos;
        this.id = Registry.BLOCK.getKey(blockstate.getBlock()).toString();

        CompoundTag tag = NbtUtils.writeBlockState(blockstate);
        this.properties = (LuaTable) NbtToLua.convert(tag.contains("Properties") ? tag.get("Properties") : null);
    }

    protected BlockPos getBlockPos() {
        return pos == null ? BlockPos.ZERO : pos;
    }

    protected static List<FiguraVec6> voxelShapeToTable(VoxelShape shape) {
        List<FiguraVec6> shapes = new ArrayList<>();
        for (AABB aabb : shape.toAabbs())
            shapes.add(FiguraVec6.of(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ));
        return shapes;
    }

    @LuaWhitelist
    public FiguraVec3 getPos() {
        return FiguraVec3.fromBlockPos(getBlockPos());
    }

    @LuaWhitelist
    public void setPos(Double x, Double y, Double z){
        setPos(LuaUtils.freeVec3("setPos", x, y, z));
    }

    @LuaWhitelist
    public void setPos(FiguraVec3 pos) {
        this.pos = pos.asBlockPos();
        pos.free();
    }

    @LuaWhitelist
    public boolean isTranslucent() {
        return blockState.propagatesSkylightDown(WorldAPI.getCurrentWorld(), getBlockPos());
    }

    @LuaWhitelist
    public int getOpacity() {
        return blockState.getLightBlock(WorldAPI.getCurrentWorld(), getBlockPos());
    }

    @LuaWhitelist
    public FiguraVec3 getMapColor() {
        return ColorUtils.intToRGB(blockState.getMapColor(WorldAPI.getCurrentWorld(), getBlockPos()).col);
    }

    @LuaWhitelist
    public boolean isSolidBlock() {
        return blockState.isRedstoneConductor(WorldAPI.getCurrentWorld(), getBlockPos());
    }

    @LuaWhitelist
    public boolean isFullCube() {
        return blockState.isCollisionShapeFullBlock(WorldAPI.getCurrentWorld(), getBlockPos());
    }

    @LuaWhitelist
    public boolean hasEmissiveLighting() {
        return blockState.emissiveRendering(WorldAPI.getCurrentWorld(), getBlockPos());
    }

    @LuaWhitelist
    public float getHardness() {
        return blockState.getDestroySpeed(WorldAPI.getCurrentWorld(), getBlockPos());
    }

    @LuaWhitelist
    public int getComparatorOutput() {
        return blockState.getAnalogOutputSignal(WorldAPI.getCurrentWorld(), getBlockPos());
    }

    @LuaWhitelist
    public boolean hasBlockEntity() {
        return blockState.hasBlockEntity();
    }

    @LuaWhitelist
    public boolean isOpaque() {
        return blockState.canOcclude();
    }

    @LuaWhitelist
    public boolean emitsRedstonePower() {
        return blockState.isSignalSource();
    }

    @LuaWhitelist
    public int getLuminance() {
        return blockState.getLightEmission();
    }

    @LuaWhitelist
    public float getFriction() {
        return blockState.getBlock().getFriction();
    }

    @LuaWhitelist
    public float getVelocityMultiplier() {
        return blockState.getBlock().getSpeedFactor();
    }

    @LuaWhitelist
    public float getJumpVelocityMultiplier() {
        return blockState.getBlock().getJumpFactor();
    }

    @LuaWhitelist
    public float getBlastResistance() {
        return blockState.getBlock().getExplosionResistance();
    }

    @LuaWhitelist
    public ItemStackAPI asItem() {
        return ItemStackAPI.verify(blockState.getBlock().asItem().getDefaultInstance());
    }

    @LuaWhitelist
    public List<String> getTags() {
        List<String> list = new ArrayList<>();

        Registry<Block> registry = WorldAPI.getCurrentWorld().registryAccess().registryOrThrow(Registry.BLOCK_REGISTRY);
        Optional<ResourceKey<Block>> key = registry.getResourceKey(blockState.getBlock());

        if (key.isEmpty())
            return list;

        for (TagKey<Block> blockTagKey : registry.getHolderOrThrow(key.get()).tags().toList())
            list.add(blockTagKey.location().toString());

        return list;
    }

    @LuaWhitelist
    public String getMaterial() {
        for (Field field : Material.class.getFields()) {
            try {
                if (field.get(null) == blockState.getMaterial())
                    return field.getName();
            } catch (Exception ignored) {}
        }

        return null;
    }

    @LuaWhitelist
    public boolean hasCollision() {
        return ((BlockBehaviourAccessor) blockState.getBlock()).hasCollision();
    }

    @LuaWhitelist
    public List<FiguraVec6> getCollisionShape() {
        return voxelShapeToTable(blockState.getCollisionShape(WorldAPI.getCurrentWorld(), getBlockPos()));
    }

    @LuaWhitelist
    public List<FiguraVec6> getOutlineShape() {
        return voxelShapeToTable(blockState.getShape(WorldAPI.getCurrentWorld(), getBlockPos()));
    }

    @LuaWhitelist
    public Map<String, Object> getSounds() {
        Map<String, Object> sounds = new HashMap<>();
        SoundType snd = blockState.getSoundType();

        sounds.put("pitch", snd.getPitch());
        sounds.put("volume", snd.getVolume());
        sounds.put("break", snd.getBreakSound().getLocation().toString());
        sounds.put("fall", snd.getFallSound().getLocation().toString());
        sounds.put("hit", snd.getHitSound().getLocation().toString());
        sounds.put("plate", snd.getPlaceSound().getLocation().toString());
        sounds.put("step", snd.getStepSound().getLocation().toString());

        return sounds;
    }

    @LuaWhitelist
    public List<String> getFluidTags() {
        List<String> list = new ArrayList<>();
        for (TagKey<Fluid> fluidTagKey : blockState.getFluidState().getTags().toList())
            list.add(fluidTagKey.location().toString());
        return list;
    }

    @LuaWhitelist
    public LuaTable getEntityData() {
        BlockEntity entity = WorldAPI.getCurrentWorld().getBlockEntity(getBlockPos());
        return (LuaTable) NbtToLua.convert(entity != null ? entity.saveWithoutMetadata() : null);
    }

    @LuaWhitelist
    public String toStateString() {
        BlockEntity entity = WorldAPI.getCurrentWorld().getBlockEntity(getBlockPos());
        CompoundTag tag = entity != null ? entity.saveWithoutMetadata() : new CompoundTag();

        return BlockStateParser.serialize(blockState) + tag;
    }

    @LuaWhitelist
    public boolean __eq(BlockStateAPI other) {
        return this.blockState.equals(other.blockState);
    }

    @LuaWhitelist
    public Object __index(String arg) {
        if (arg == null) return null;
        return switch (arg) {
            case "id" -> id;
            case "properties" -> properties;
            default -> null;
        };
    }

    @Override
    public String toString() {
        return id + " (BlockState)";
    }
}
