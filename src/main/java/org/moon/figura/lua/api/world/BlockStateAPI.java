package org.moon.figura.lua.api.world;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
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
import org.moon.figura.lua.ReadOnlyLuaTable;
import org.moon.figura.lua.docs.LuaMethodDoc;
import org.moon.figura.lua.docs.LuaTypeDoc;
import org.moon.figura.math.vector.FiguraVec3;
import org.moon.figura.mixin.BlockBehaviourAccessor;
import org.moon.figura.utils.ColorUtils;

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
        this.properties = new ReadOnlyLuaTable(tag.contains("Properties") ? NbtToLua.convert(tag.get("Properties")) : new LuaTable());
    }

    protected BlockPos getBlockPos() {
        return pos == null ? BlockPos.ZERO : pos;
    }

    protected static List<List<FiguraVec3>> voxelShapeToTable(VoxelShape shape) {
        List<List<FiguraVec3>> shapes = new ArrayList<>();
        for (AABB aabb : shape.toAabbs())
            shapes.add(List.of(FiguraVec3.of(aabb.minX, aabb.minY, aabb.minZ), FiguraVec3.of(aabb.maxX, aabb.maxY, aabb.maxZ)));
        return shapes;
    }

    @LuaWhitelist
    public String getID() {
        return id;
    }

    @LuaWhitelist
    public LuaTable getProperties() {
        return properties;
    }

    @LuaWhitelist
    public FiguraVec3 getPos() {
        return FiguraVec3.fromBlockPos(getBlockPos());
    }

    @LuaWhitelist
    public BlockStateAPI setPos(Double x, Double y, Double z) {
        return setPos(FiguraVec3.oneUse(x, y, z));
    }

    @LuaWhitelist
    @LuaMethodDoc("pos")
    public BlockStateAPI setPos(FiguraVec3 pos) {
        this.pos = pos.asBlockPos();
        return this;
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
    public List<List<FiguraVec3>> getCollisionShape() {
        return voxelShapeToTable(blockState.getCollisionShape(WorldAPI.getCurrentWorld(), getBlockPos()));
    }

    @LuaWhitelist
    public List<List<FiguraVec3>> getOutlineShape() {
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
        sounds.put("place", snd.getPlaceSound().getLocation().toString());
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
    public HashMap<String, Set<String>> getTextures() {
        HashMap<String, Set<String>> map = new HashMap<>();

        RenderShape renderShape = blockState.getRenderShape();
        if (renderShape != RenderShape.MODEL)
            return map;

        BakedModel bakedModel = Minecraft.getInstance().getBlockRenderer().getBlockModel(blockState);
        RandomSource randomSource = RandomSource.create();
        long seed = 42L;

        for (Direction direction : Direction.values())
            map.put(direction.name(), getTexturesForFace(blockState, direction, randomSource, bakedModel, seed));
        map.put("NONE", getTexturesForFace(blockState, null, randomSource, bakedModel, seed));

        return map;
    }

    private static Set<String> getTexturesForFace(BlockState blockState, Direction direction, RandomSource randomSource, BakedModel bakedModel, long seed) {
        randomSource.setSeed(seed);
        List<BakedQuad> quads = bakedModel.getQuads(blockState, direction, randomSource);
        Set<String> textures = new HashSet<>();

        for (BakedQuad quad : quads) {
            ResourceLocation location = quad.getSprite().getName(); // do not close it
            textures.add(location.getNamespace() + ":textures/" + location.getPath());
        }

        return textures;
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
