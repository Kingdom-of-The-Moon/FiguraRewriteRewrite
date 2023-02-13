package org.moon.figura.lua.api;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.brigadier.StringReader;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.arguments.SlotArgument;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import org.luaj.vm2.LuaError;
import org.moon.figura.FiguraMod;
import org.moon.figura.avatar.Avatar;
import org.moon.figura.avatar.AvatarManager;
import org.moon.figura.config.Config;
import org.moon.figura.lua.LuaNotNil;
import org.moon.figura.lua.LuaWhitelist;
import org.moon.figura.lua.api.world.ItemStackAPI;
import org.moon.figura.lua.docs.LuaMethodDoc;
import org.moon.figura.lua.docs.LuaTypeDoc;
import org.moon.figura.math.vector.FiguraVec3;
import org.moon.figura.mixin.LivingEntityAccessor;
import org.moon.figura.mixin.gui.ChatScreenAccessor;
import org.moon.figura.model.rendering.texture.FiguraTexture;
import org.moon.figura.utils.ColorUtils;
import org.moon.figura.utils.LuaUtils;
import org.moon.figura.utils.TextUtils;

import java.util.*;

@LuaWhitelist
@LuaTypeDoc(
        name = "HostAPI",
        value = "host"
)
public class HostAPI {

    private final Avatar owner;
    private final boolean isHost;
    private final Minecraft minecraft;

    @LuaWhitelist
    public boolean unlockCursor = false;
    public Integer chatColor;

    public HostAPI(Avatar owner) {
        this.owner = owner;
        this.minecraft = Minecraft.getInstance();
        this.isHost = owner.isHost;
    }

    @LuaWhitelist
    public boolean isHost() {
        return isHost;
    }

    @LuaWhitelist
    public boolean isCursorUnlocked() {
        return unlockCursor;
    }

    @LuaWhitelist
    public HostAPI setUnlockCursor(boolean bool) {
        unlockCursor = bool;
        return this;
    }

    @LuaWhitelist
    public HostAPI setTitleTimes(FiguraVec3 titleTimes) {
        return setTitleTimes((int) titleTimes.x, (int) titleTimes.y, (int) titleTimes.z);
    }

    @LuaWhitelist
    @LuaMethodDoc("titleTimes")
    public HostAPI setTitleTimes(@LuaNotNil int fadeInTime, int stayTime, int fadeOutTime) {
        if (!isHost()) return null;
        this.minecraft.gui.setTimes(fadeInTime, stayTime, fadeOutTime);
        return this;
    }

    @LuaWhitelist
    public HostAPI clearTitle() {
        if (isHost())
            this.minecraft.gui.clear();
        return this;
    }

    @LuaWhitelist
    @LuaMethodDoc("title")
    public HostAPI setTitle(@LuaNotNil String text) {
        if (isHost())
            this.minecraft.gui.setTitle(TextUtils.tryParseJson(text));
        return this;
    }

    @LuaWhitelist
    @LuaMethodDoc("subtitle")
    public HostAPI setSubtitle(@LuaNotNil String text) {
        if (isHost())
            this.minecraft.gui.setSubtitle(TextUtils.tryParseJson(text));
        return this;
    }

    @LuaWhitelist
    @LuaMethodDoc("actionbar")
    public HostAPI setActionbar(@LuaNotNil String text, boolean animated) {
        if (isHost())
            this.minecraft.gui.setOverlayMessage(TextUtils.tryParseJson(text), animated);
        return this;
    }

    @LuaWhitelist
    public HostAPI sendChatMessage(@LuaNotNil String message) {
        if (!isHost() || !Config.CHAT_MESSAGES.asBool()) return this;
        LocalPlayer player = this.minecraft.player;
        if (player != null) player.chatSigned(message, null);
        return this;
    }

    @LuaWhitelist
    public HostAPI sendChatCommand(@LuaNotNil String command) {
        if (!isHost() || !Config.CHAT_MESSAGES.asBool()) return this;
        LocalPlayer player = this.minecraft.player;
        if (player != null) player.commandSigned(command.startsWith("/") ? command.substring(1) : command, null);
        return this;
    }

    @LuaWhitelist
    public HostAPI appendChatHistory(@LuaNotNil String message) {
        if (isHost())
            this.minecraft.gui.getChat().addRecentChat(message);
        return this;
    }

    @LuaWhitelist
    public HostAPI swingArm(boolean offhand) {
        if (isHost() && this.minecraft.player != null)
            this.minecraft.player.swing(offhand ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
        return this;
    }

    @LuaWhitelist
    public ItemStackAPI getSlot(String slot) {
        if (!isHost()) return null;
        try {
            return getSlot(SlotArgument.slot().parse(new StringReader(slot)));
        } catch (Exception e) {
            throw new LuaError("Unable to get slot \"" + slot + "\"");
        }
    }

    @LuaWhitelist
    public ItemStackAPI getSlot(int slot) {
        if (!isHost()) return null;
        Entity e = this.owner.luaRuntime.getUser();
        return ItemStackAPI.verify(e.getSlot(slot).get());
    }

    @LuaWhitelist
    @LuaMethodDoc(value = "badge", key = "")
    public HostAPI setBadge(int index, boolean value, boolean pride) {
        if (!isHost()) return null;
        if (!FiguraMod.DEBUG_MODE)
            throw new LuaError("Congrats, you found this debug easter egg!");

        Pair<BitSet, BitSet> badges = AvatarManager.getBadges(owner.owner);
        if (badges == null)
            return this;

        BitSet set = pride ? badges.getFirst() : badges.getSecond();
        set.set(index, value);
        return this;
    }

    @LuaWhitelist
    public Integer getChatColor() {
        return isHost() ? this.chatColor : null;
    }

    @LuaWhitelist
    public HostAPI setChatColor(@LuaNotNil double r, double g, double b) {
        return setChatColor(LuaUtils.freeVec3("setChatColor", r, g, b));
    }

    @LuaWhitelist
    @LuaMethodDoc("chatColor")
    public HostAPI setChatColor(FiguraVec3 color) {
        if (isHost()) this.chatColor = color == null ? null : ColorUtils.rgbToInt(color);
        return this;
    }

    @LuaWhitelist
    public String getChatText() {
        if (isHost() && this.minecraft.screen instanceof ChatScreen chat)
            return ((ChatScreenAccessor) chat).getInput().getValue();

        return null;
    }

    @LuaWhitelist
    @LuaMethodDoc("chatText")
    public HostAPI setChatText(@LuaNotNil String text) {
        if (isHost() && Config.CHAT_MESSAGES.asBool() && this.minecraft.screen instanceof ChatScreen chat)
            ((ChatScreenAccessor) chat).getInput().setValue(text);
        return this;
    }

    @LuaWhitelist
    public String getScreen() {
        if (!isHost() || this.minecraft.screen == null)
            return null;
        return this.minecraft.screen.getClass().getName();
    }

    @LuaWhitelist
    public boolean isChatOpen() {
        return isHost() && this.minecraft.screen instanceof ChatScreen;
    }

    @LuaWhitelist
    public boolean isContainerOpen() {
        return isHost() && this.minecraft.screen instanceof AbstractContainerScreen;
    }

    @LuaWhitelist
    public HostAPI saveTexture(@LuaNotNil FiguraTexture texture) {
        if (isHost()) {
            try {
                texture.saveCache();
            } catch (Exception e) {
                throw new LuaError(e.getMessage());
            }
        }
        return this;
    }

    @LuaWhitelist
    public FiguraTexture screenshot(String name) {
        if (!isHost())
            return null;

        String screenshot = name == null ? "screenshot" : name;
        NativeImage img = Screenshot.takeScreenshot(this.minecraft.getMainRenderTarget());
        return owner.luaRuntime.texture.register(screenshot, img, true);
    }

    @LuaWhitelist
    public boolean isAvatarUploaded() {
        return isHost() && AvatarManager.localUploaded;
    }

    @LuaWhitelist
    public List<Map<String, Object>> getStatusEffects() {
        List<Map<String, Object>> list = new ArrayList<>();

        LocalPlayer player = this.minecraft.player;
        if (!isHost() || player == null)
            return list;

        for (MobEffectInstance effect : player.getActiveEffects()) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", effect.getEffect().getDescriptionId());
            map.put("amplifier", effect.getAmplifier());
            map.put("duration", effect.getDuration());
            map.put("visible", effect.isVisible());

            list.add(map);
        }

        return list;
    }

    @LuaWhitelist
    public String getClipboard() {
        return isHost() ? this.minecraft.keyboardHandler.getClipboard() : null;
    }

    @LuaWhitelist
    @LuaMethodDoc("clipboard")
    public HostAPI setClipboard(@LuaNotNil String text) {
        if (isHost()) this.minecraft.keyboardHandler.setClipboard(text);
        return this;
    }

    @LuaWhitelist
    public float getAttackCharge() {
        LocalPlayer player = this.minecraft.player;
        if (isHost() && player != null)
            return player.getAttackStrengthScale(0f);
        return 0f;
    }

    @LuaWhitelist
    public boolean isJumping() {
        LocalPlayer player = this.minecraft.player;
        if (isHost() && player != null)
            return ((LivingEntityAccessor) player).isJumping();
        return false;
    }

    @LuaWhitelist
    public boolean isFlying() {
        LocalPlayer player = this.minecraft.player;
        if (isHost() && player != null)
            return player.getAbilities().flying;
        return false;
    }

    @LuaWhitelist
    public double getReachDistance() {
        return this.minecraft.gameMode == null ? 0 : this.minecraft.gameMode.getPickRange();
    }

    public Object __index(String arg) {
        if ("unlockCursor".equals(arg))
            return unlockCursor;
        return null;
    }

    @LuaWhitelist
    public void __newindex(@LuaNotNil String key, Object value) {
        if ("unlockCursor".equals(key))
            unlockCursor = (Boolean) value;
        else throw new LuaError("Cannot assign value on key \"" + key + "\"");
    }

    @Override
    public String toString() {
        return "HostAPI";
    }
}
