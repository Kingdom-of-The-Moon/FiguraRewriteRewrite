package org.moon.figura.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.moon.figura.avatar.Avatar;
import org.moon.figura.avatar.AvatarManager;
import org.moon.figura.config.Config;
import org.moon.figura.model.rendering.texture.EntityRenderMode;
import org.moon.figura.utils.ui.UIHelper;

public class PaperDoll {

    private static Long lastActivityTime = 0L;

    public static void render(PoseStack stack) {
        Minecraft minecraft = Minecraft.getInstance();
        LivingEntity entity = minecraft.getCameraEntity() instanceof LivingEntity e ? e : null;
        Avatar avatar;

        if (!Config.HAS_PAPERDOLL.asBool() ||
                entity == null ||
                !Minecraft.renderNames() ||
                minecraft.options.renderDebug ||
                (Config.FIRST_PERSON_PAPERDOLL.asBool() && !minecraft.options.getCameraType().isFirstPerson()) ||
                entity.isSleeping())
            return;

        //check if should stay always on
        if (!Config.PAPERDOLL_ALWAYS_ON.asBool() && (avatar = AvatarManager.getAvatar(entity)) != null && avatar.luaRuntime != null && !avatar.luaRuntime.renderer.forcePaperdoll) {
            //if action - reset activity time and enable can draw
            if (entity.isSprinting() ||
                    entity.isCrouching() ||
                    entity.isAutoSpinAttack() ||
                    entity.isVisuallySwimming() ||
                    entity.isFallFlying() ||
                    entity.isBlocking() ||
                    entity.onClimbable() ||
                    (entity instanceof Player p && p.getAbilities().flying))
                lastActivityTime = System.currentTimeMillis();

            //if activity time is greater than duration - return
            else if (System.currentTimeMillis() - lastActivityTime > 1000L)
                return;
        }

        //draw
        float screenWidth = Minecraft.getInstance().getWindow().getWidth();
        float screenHeight = Minecraft.getInstance().getWindow().getHeight();
        float guiScale = (float) Minecraft.getInstance().getWindow().getGuiScale();

        float scale = Config.PAPERDOLL_SCALE.asFloat();
        float x = scale * 25f;
        float y = scale * 45f;
        x += (Config.PAPERDOLL_X.asFloat() / 100f) * screenWidth / guiScale;
        y += (Config.PAPERDOLL_Y.asFloat() / 100f) * screenHeight / guiScale;

        UIHelper.drawEntity(
                x, y,
                scale * 30f,
                Config.PAPERDOLL_PITCH.asFloat(), Config.PAPERDOLL_YAW.asFloat(),
                entity, stack, EntityRenderMode.PAPERDOLL
        );
    }
}
