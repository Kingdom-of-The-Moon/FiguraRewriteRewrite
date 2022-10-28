package org.moon.figura.lua.api.world;

import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.luaj.vm2.LuaTable;
import org.moon.figura.lua.LuaWhitelist;
import org.moon.figura.lua.NbtToLua;
import org.moon.figura.lua.docs.LuaFieldDoc;
import org.moon.figura.lua.docs.LuaTypeDoc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@LuaWhitelist
@LuaTypeDoc(
        name = "ItemStack",
        value = "itemstack"
)
public class ItemStackAPI {

    public final ItemStack itemStack;

    /**
     * Checks whether the given ItemStack is null, empty. If it is, returns air. If it isn't,
     * returns a new ItemStack for that item.
     * @param itemStack The ItemStack to check if it's a valid stack.
     * @return Null if the stack was invalid, or a wrapper for the stack if it was valid.
     */
    public static ItemStackAPI verify(ItemStack itemStack) {
        if (itemStack == null || itemStack == ItemStack.EMPTY)
            itemStack = Items.AIR.getDefaultInstance();
        return new ItemStackAPI(itemStack);
    }

    @LuaWhitelist
    public final String id;
    @LuaWhitelist
    public final LuaTable tag;

    public ItemStackAPI(ItemStack itemStack) {
        this.itemStack = itemStack;
        this.id = Registry.ITEM.getKey(itemStack.getItem()).toString();
        this.tag = (LuaTable) NbtToLua.convert(itemStack.getTag() != null ? itemStack.getTag() : null);
    }

    @LuaWhitelist
    public int getCount() {
        return itemStack.getCount();
    }

    @LuaWhitelist
    public int getDamage() {
        return itemStack.getDamageValue();
    }

    @LuaWhitelist
    public int getCooldown() {
        return itemStack.getPopTime();
    }

    @LuaWhitelist
    public boolean hasGlint() {
        return itemStack.hasFoil();
    }

    @LuaWhitelist
    public List<String> getTags() {
        List<String> list = new ArrayList<>();

        Registry<Item> registry = WorldAPI.getCurrentWorld().registryAccess().registryOrThrow(Registry.ITEM_REGISTRY);
        Optional<ResourceKey<Item>> key = registry.getResourceKey(itemStack.getItem());

        if (key.isEmpty())
            return list;

        for (TagKey<Item> itemTagKey : registry.getHolderOrThrow(key.get()).tags().toList())
            list.add(itemTagKey.location().toString());

        return list;
    }

    @LuaWhitelist
    public boolean isBlockItem() {
        return itemStack.getItem() instanceof BlockItem;
    }

    @LuaWhitelist
    public boolean isFood() {
        return itemStack.isEdible();
    }

    @LuaWhitelist
    public String getUseAction() {
        return itemStack.getUseAnimation().name();
    }

    @LuaWhitelist
    public String getName() {
        return itemStack.getHoverName().getString();
    }

    @LuaWhitelist
    public int getMaxCount() {
        return itemStack.getMaxStackSize();
    }

    @LuaWhitelist
    public String getRarity() {
        return itemStack.getRarity().name();
    }

    @LuaWhitelist
    public boolean isEnchantable() {
        return itemStack.isEnchantable();
    }

    @LuaWhitelist
    public int getMaxDamage() {
        return itemStack.getMaxDamage();
    }

    @LuaWhitelist
    public boolean isDamageable() {
        return itemStack.isDamageableItem();
    }

    @LuaWhitelist
    public boolean isStackable() {
        return itemStack.isStackable();
    }

    @LuaWhitelist
    public int getRepairCost() {
        return itemStack.getBaseRepairCost();
    }

    @LuaWhitelist
    public int getUseDuration() {
        return itemStack.getUseDuration();
    }

    @LuaWhitelist
    public String toStackString() {
        ItemStack stack = itemStack;
        String ret = Registry.ITEM.getKey(stack.getItem()).toString();

        CompoundTag nbt = stack.getTag();
        if (nbt != null)
            ret += nbt.toString();

        return ret;
    }

    @LuaWhitelist
    public boolean __eq(ItemStackAPI other) {
        return ItemStack.matches(this.itemStack, other.itemStack);
    }

    @LuaWhitelist
    public Object __index(String arg) {
        if (arg == null) return null;
        return switch (arg) {
            case "id" -> id;
            case "tag" -> tag;
            default -> null;
        };
    }

    @Override
    public String toString() {
        return id + " x" + getCount() + " (ItemStack)";
    }
}
