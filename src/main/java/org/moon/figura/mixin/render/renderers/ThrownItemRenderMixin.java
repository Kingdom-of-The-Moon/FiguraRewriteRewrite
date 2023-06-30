package org.moon.figura.mixin.render.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import org.moon.figura.FiguraMod;
import org.moon.figura.avatar.Avatar;
import org.moon.figura.avatar.AvatarManager;
import org.moon.figura.lua.api.entity.EntityAPI;
import org.moon.figura.model.rendering.PartFilterScheme;
import org.moon.figura.permissions.Permissions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ThrownItemRenderer.class)
public class ThrownItemRenderMixin {
    @Inject(at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;scale(FFF)V"), method = "render", cancellable = true)
    private <T extends Entity & ItemSupplier> void render(T entity, float yaw, float tickDelta, PoseStack poseStack, MultiBufferSource multiBufferSource, int light, CallbackInfo ci) {
        ThrowableItemProjectile projectile = entity instanceof ThrowableItemProjectile proj ? proj : null;
        Entity owner =  projectile != null? projectile.getOwner() : null;
        if (owner == null)
            return;

        Avatar avatar = AvatarManager.getAvatar(owner);
        if (avatar == null || avatar.permissions.get(Permissions.VANILLA_MODEL_EDIT) == 0)
            return;

        FiguraMod.pushProfiler(FiguraMod.MOD_ID);
        FiguraMod.pushProfiler(avatar);
        FiguraMod.pushProfiler("arrowRender");

        FiguraMod.pushProfiler("event");
        boolean bool = avatar.thrownItemRenderEvent(tickDelta, EntityAPI.wrap(projectile));

        FiguraMod.popPushProfiler("render");
        if (bool || avatar.renderProjectile(poseStack, multiBufferSource, tickDelta, light, PartFilterScheme.THROWN_ITEM)) {
            poseStack.popPose();
            ci.cancel();
        }

        FiguraMod.popProfiler(4);
    }
}
