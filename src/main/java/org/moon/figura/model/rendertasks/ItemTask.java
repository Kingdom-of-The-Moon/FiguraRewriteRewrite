package org.moon.figura.model.rendertasks;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import org.luaj.vm2.LuaError;
import org.moon.figura.lua.LuaNotNil;
import org.moon.figura.lua.LuaWhitelist;
import org.moon.figura.lua.api.world.ItemStackAPI;
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
    private ItemTransforms.TransformType renderType = ItemTransforms.TransformType.NONE;
    private boolean left = false;
    private int cachedComplexity;

    @Override
    public boolean render(PartCustomization.Stack stack, MultiBufferSource buffer, int light, int overlay) {
        if (!enabled || item == null || item.isEmpty())
            return false;

        this.pushOntoStack(stack);
        PoseStack poseStack = stack.peek().copyIntoGlobalPoseStack();
        poseStack.scale(-16, 16, -16);

        Minecraft.getInstance().getItemRenderer().renderStatic(
                null, item, renderType, left,
                poseStack, buffer, null,
                emissive ? LightTexture.FULL_BRIGHT : light, overlay, 0);

        stack.pop();
        return true;
    }

    @Override
    public int getComplexity() {
        return cachedComplexity;
    }

    @LuaWhitelist
    public RenderTask item(String itemId){
        return item(LuaUtils.parseItemStack("item", itemId));
    }

    @LuaWhitelist
    public RenderTask item(@LuaNotNil ItemStackAPI item){
        return item(LuaUtils.parseItemStack("item", item));
    }

    public RenderTask item(ItemStack item) {
        this.item = item;
        Minecraft client = Minecraft.getInstance();
        RandomSource random = client.level != null ? client.level.random : RandomSource.create();
        cachedComplexity = client.getItemRenderer().getModel(this.item, null, null, 0).getQuads(null, null, random).size();
        return this;
    }

    @LuaWhitelist
    public String getRenderType() {
        return this.renderType.name();
    }

    @LuaWhitelist
    public RenderTask renderType(@LuaNotNil String renderType) {
        try {
            this.renderType = ItemTransforms.TransformType.valueOf(renderType.toUpperCase());
            this.left = this.renderType == ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND || this.renderType == ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND;
            return this;
        } catch (Exception ignored) {
            throw new LuaError("Illegal RenderType: \"" + renderType + "\".");
        }
    }

    @Override
    public String toString() {
        return "Item Render Task";
    }
}
