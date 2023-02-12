package org.moon.figura.model.rendertasks;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.moon.figura.lua.LuaNotNil;
import org.moon.figura.lua.LuaWhitelist;
import org.moon.figura.lua.api.world.BlockStateAPI;
import org.moon.figura.lua.docs.LuaMethodDoc;
import org.moon.figura.lua.docs.LuaTypeDoc;
import org.moon.figura.model.PartCustomization;
import org.moon.figura.utils.LuaUtils;

@LuaWhitelist
@LuaTypeDoc(
        name = "BlockTask",
        value = "block_task"
)
public class BlockTask extends RenderTask {

    private BlockState block;
    private int cachedComplexity;

    public BlockTask(String name) {
        super(name);
    }

    @Override
    public boolean render(PartCustomization.Stack stack, MultiBufferSource buffer, int light, int overlay) {
        if (!enabled || block == null || block.isAir())
            return false;

        this.pushOntoStack(stack); //push
        PoseStack poseStack = stack.peek().copyIntoGlobalPoseStack();
        poseStack.scale(16, 16, 16);

        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(block, poseStack, buffer, this.light != null ? this.light : light, this.overlay != null ? this.overlay : overlay);

        stack.pop(); //pop
        return true;
    }

    @Override
    public int getComplexity() {
        return cachedComplexity;
    }

    @LuaWhitelist
    public RenderTask setBlock(String block) {
        return block(LuaUtils.parseBlockState("setBlock", block));
    }

    @LuaWhitelist
    @LuaMethodDoc("block")
    public RenderTask setBlock(@LuaNotNil BlockStateAPI block) {
        return block(LuaUtils.parseBlockState("setBlock", block));
    }

    public RenderTask block(BlockState block) {
        this.block = block;
        Minecraft client = Minecraft.getInstance();
        RandomSource random = client.level != null ? client.level.random : RandomSource.create();

        BakedModel blockModel = client.getBlockRenderer().getBlockModel(this.block);
        cachedComplexity = blockModel.getQuads(this.block, null, random).size();
        for (Direction dir : Direction.values())
            cachedComplexity += blockModel.getQuads(this.block, dir, random).size();

        return this;
    }

    @Override
    public String toString() {
        return name + " (Block Render Task)";
    }
}
