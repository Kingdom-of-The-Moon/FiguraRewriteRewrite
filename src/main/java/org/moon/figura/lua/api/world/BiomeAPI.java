package org.moon.figura.lua.api.world;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import org.moon.figura.lua.LuaWhitelist;
import org.moon.figura.lua.docs.LuaMethodDoc;
import org.moon.figura.lua.docs.LuaTypeDoc;
import org.moon.figura.math.vector.FiguraVec3;
import org.moon.figura.utils.ColorUtils;
import org.moon.figura.utils.LuaUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@LuaWhitelist
@LuaTypeDoc(
        name = "Biome",
        value = "biome"
)
public class BiomeAPI {

    private final Biome biome;
    private BlockPos pos;

    @LuaWhitelist
    public final String id;

    public BiomeAPI(Biome biome, BlockPos pos) {
        this.biome = biome;
        this.pos = pos;
        this.id = WorldAPI.getCurrentWorld().registryAccess().registry(Registry.BIOME_REGISTRY).get().getKey(biome).toString();
    }

    protected BlockPos getBlockPos() {
        return pos == null ? BlockPos.ZERO : pos;
    }

    @LuaWhitelist
    public FiguraVec3 getPos() {
        return FiguraVec3.fromBlockPos(getBlockPos());
    }

    @LuaWhitelist
    public void setPos(Double x, Double y, Double z) {
        setPos(LuaUtils.freeVec3("setPos", x, y, z));
    }

    @LuaWhitelist
    public void setPos(FiguraVec3 pos) {
        this.pos = pos.asBlockPos();
    }

    @LuaWhitelist
    public BiomeAPI pos(Double x, Double y, Double z) {
        return pos(LuaUtils.freeVec3("pos", x, y, z));
    }

    @LuaWhitelist
    @LuaMethodDoc("setPos")
    public BiomeAPI pos(FiguraVec3 pos) {
        this.pos = pos.asBlockPos();
        return this;
    }

    @LuaWhitelist
    public List<String> getTags() {
        List<String> list = new ArrayList<>();

        Registry<Biome> registry = WorldAPI.getCurrentWorld().registryAccess().registryOrThrow(Registry.BIOME_REGISTRY);
        Optional<ResourceKey<Biome>> key = registry.getResourceKey(biome);

        if (key.isEmpty())
            return list;

        for (TagKey<Biome> biomeTagKey : registry.getHolderOrThrow(key.get()).tags().toList())
            list.add(biomeTagKey.location().toString());

        return list;
    }

    @LuaWhitelist
    public float getTemperature() {
        return biome.getBaseTemperature();
    }

    @LuaWhitelist
    public String getPrecipitation() {
        return biome.getPrecipitation().name();
    }

    @LuaWhitelist
    public FiguraVec3 getSkyColor() {
        return ColorUtils.intToRGB(biome.getSkyColor());
    }

    @LuaWhitelist
    public FiguraVec3 getFoliageColor() {
        return ColorUtils.intToRGB(biome.getFoliageColor());
    }

    @LuaWhitelist
    public FiguraVec3 getGrassColor() {
        BlockPos pos = getBlockPos();
        return ColorUtils.intToRGB(biome.getGrassColor(pos.getX(), pos.getY()));
    }

    @LuaWhitelist
    public FiguraVec3 getFogColor() {
        return ColorUtils.intToRGB(biome.getFogColor());
    }

    @LuaWhitelist
    public FiguraVec3 getWaterColor() {
        return ColorUtils.intToRGB(biome.getWaterColor());
    }

    @LuaWhitelist
    public FiguraVec3 getWaterFogColor() {
        return ColorUtils.intToRGB(biome.getWaterFogColor());
    }

    @LuaWhitelist
    public float getDownfall() {
        return biome.getDownfall();
    }

    @LuaWhitelist
    public boolean isHot() {
        return biome.shouldSnowGolemBurn(getBlockPos());
    }

    @LuaWhitelist
    public boolean isCold() {
        return biome.coldEnoughToSnow(getBlockPos());
    }

    @LuaWhitelist
    public boolean __eq(BiomeAPI other) {
        return this.biome.equals(other.biome);
    }

    @LuaWhitelist
    public Object __index(String arg) {
        return "id".equals(arg) ? id : null;
    }

    @Override
    public String toString() {
        return id + " (Biome)";
    }
}
