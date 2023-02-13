package org.moon.figura.model.rendertasks;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import org.luaj.vm2.LuaError;
import org.moon.figura.lua.LuaNotNil;
import org.moon.figura.lua.LuaWhitelist;
import org.moon.figura.lua.api.world.ItemStackAPI;
import org.moon.figura.lua.docs.LuaMethodDoc;
import org.moon.figura.lua.docs.LuaTypeDoc;
import org.moon.figura.model.PartCustomization;
import org.moon.figura.utils.LuaUtils;

@LuaWhitelist
@LuaTypeDoc(
        name = "ItemTask",
        value = "item_task"
)
public class ItemTask extends RenderTask {

    private ItemStack item;
    private ItemTransforms.TransformType displayMode = ItemTransforms.TransformType.NONE;
    private boolean left = false;
    private int cachedComplexity;

    public ItemTask(String name) {
        super(name);
    }

    @Override
    public boolean render(PartCustomization.Stack stack, MultiBufferSource buffer, int light, int overlay) {
        if (!enabled || item == null || item.isEmpty())
            return false;

        this.pushOntoStack(stack);
        PoseStack poseStack = stack.peek().copyIntoGlobalPoseStack();
        poseStack.scale(-16, 16, -16);

        Minecraft.getInstance().getItemRenderer().renderStatic(
                null, item, displayMode, left,
                poseStack, buffer, null,
                this.light != null ? this.light : light, this.overlay != null ? this.overlay : overlay, 0);

        stack.pop();
        return true;
    }

    @Override
    public int getComplexity() {
        return cachedComplexity;
    }


    // -- lua -- //


    @LuaWhitelist
    public ItemTask setItem(String itemId) {
        return item(LuaUtils.parseItemStack("item", itemId));
    }

    @LuaWhitelist
    @LuaMethodDoc("item")
    public ItemTask setItem(@LuaNotNil ItemStackAPI item) {
        return item(LuaUtils.parseItemStack("item", item));
    }

    public ItemTask item(ItemStack item) {
        this.item = item;
        Minecraft client = Minecraft.getInstance();
        RandomSource random = client.level != null ? client.level.random : RandomSource.create();
        cachedComplexity = client.getItemRenderer().getModel(this.item, null, null, 0).getQuads(null, null, random).size();
        return this;
    }
    
    @LuaWhitelist
    public String getDisplayMode() {
        return this.displayMode.name();
    }

    @LuaWhitelist
    @LuaMethodDoc("displayMode")
    public ItemTask setDisplayMode(@LuaNotNil String displayMode) {
        try {
            this.displayMode = ItemTransforms.TransformType.valueOf(displayMode.toUpperCase());
            this.left = this.displayMode == ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND || this.displayMode == ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND;
            return this;
        } catch (Exception ignored) {
            throw new LuaError("Illegal display mode: \"" + displayMode + "\".");
        }
    }

    @Override
    public String toString() {
        return name + " (Item Render Task)";
    }
}
