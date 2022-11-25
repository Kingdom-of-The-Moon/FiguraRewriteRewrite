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
import org.moon.figura.mixin.gui.ChatScreenAccessor;
import org.moon.figura.model.rendering.texture.FiguraTexture;
import org.moon.figura.utils.ColorUtils;
import org.moon.figura.utils.LuaUtils;
import org.moon.figura.utils.TextUtils;

import java.util.BitSet;

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
    public void setTitleTimes(FiguraVec3 titleTimes){
        setTitleTimes((int) titleTimes.x, (int) titleTimes.y, (int) titleTimes.z);
    }

    @LuaWhitelist
    public void setTitleTimes(int fadeInTime, int stayTime, int fadeOutTime) {
        if (!isHost()) return;
        this.minecraft.gui.setTimes(fadeInTime, stayTime, fadeOutTime);
    }

    @LuaWhitelist
    public void clearTitle() {
        if (isHost())
            this.minecraft.gui.clear();
    }

    @LuaWhitelist
    public void setTitle(@LuaNotNil String text) {
        if (isHost())
            this.minecraft.gui.setTitle(TextUtils.tryParseJson(text));
    }

    @LuaWhitelist
    public void setSubtitle(@LuaNotNil String text) {
        if (isHost())
            this.minecraft.gui.setSubtitle(TextUtils.tryParseJson(text));
    }

    @LuaWhitelist
    public void setActionBar(@LuaNotNil String text){
        setActionbar(text, false);
    }

    @LuaWhitelist
    public void setActionbar(@LuaNotNil String text, boolean animated) {
        if (isHost())
            this.minecraft.gui.setOverlayMessage(TextUtils.tryParseJson(text), animated);
    }

    @LuaWhitelist
    public void sendChatMessage(@LuaNotNil String message) {
        if (!isHost() || !Config.CHAT_MESSAGES.asBool()) return;
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) player.chatSigned(message, null);
    }

    @LuaWhitelist
    public void sendChatCommand(@LuaNotNil String command) {
        if (!isHost() || !Config.CHAT_MESSAGES.asBool()) return;
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) player.commandSigned(command.startsWith("/") ? command.substring(1) : command, null);
    }

    @LuaWhitelist
    public void appendChatHistory(@LuaNotNil String message) {
        if (isHost() && Config.CHAT_MESSAGES.asBool())
            this.minecraft.gui.getChat().addRecentChat(message);
    }

    @LuaWhitelist
    public void swingArm(){
        swingArm(false);
    }

    @LuaWhitelist
    public void swingArm(boolean offhand) {
        if (isHost() && Minecraft.getInstance().player != null)
            Minecraft.getInstance().player.swing(offhand ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
    }

    @LuaWhitelist
    public ItemStackAPI getSlot(String slot){
        if(!isHost()) return null;
        try {
            return getSlot(SlotArgument.slot().parse(new StringReader(slot)));
        } catch (Exception e){
            throw new LuaError("Unable to get slot \"" + slot + "\"");
        }
    }

    @LuaWhitelist
    public ItemStackAPI getSlot(int slot){
        if (!isHost()) return null;
        Entity e = this.owner.luaRuntime.getUser();
        return ItemStackAPI.verify(e.getSlot(slot).get());
    }

    @LuaWhitelist
    @LuaMethodDoc("")
    public void setBadge(int index, boolean value, boolean pride) {
        if (!isHost()) return;
        if (!FiguraMod.DEBUG_MODE)
            throw new LuaError("Congrats, you found this debug easter egg!");

        Pair<BitSet, BitSet> badges = AvatarManager.getBadges(owner.owner);
        if (badges == null)
            return;

        BitSet set = pride ? badges.getFirst() : badges.getSecond();
        set.set(index, value);
    }

    @LuaWhitelist
    public Integer getChatColor() {
        if (isHost())
            return this.chatColor;

        return null;
    }

    @LuaWhitelist
    public void setChatColor(){
        this.chatColor = null;
    }

    @LuaWhitelist
    public void setChatColor(Double r, Double g, Double b){
        setChatColor(LuaUtils.freeVec3("setChatColor", r, g, b));
    }

    @LuaWhitelist
    public void setChatColor(FiguraVec3 color) {
        if (isHost())
            this.chatColor = ColorUtils.rgbToInt(color);
    }

    @LuaWhitelist
    public String getChatText() {
        if (isHost() && Minecraft.getInstance().screen instanceof ChatScreen chat)
            return ((ChatScreenAccessor) chat).getInput().getValue();

        return null;
    }

    @LuaWhitelist
    public void setChatText(@LuaNotNil String text) {
        if (isHost() && Config.CHAT_MESSAGES.asBool() && Minecraft.getInstance().screen instanceof ChatScreen chat)
            ((ChatScreenAccessor) chat).getInput().setValue(text);
    }

    @LuaWhitelist
    public String getScreen() {
        if (!isHost() || Minecraft.getInstance().screen == null)
            return null;
        return Minecraft.getInstance().screen.getClass().getName();
    }

    @LuaWhitelist
    public boolean isChatOpen() {
        return isHost() && Minecraft.getInstance().screen instanceof ChatScreen;
    }

    @LuaWhitelist
    public boolean isContainerOpen() {
        return isHost() && Minecraft.getInstance().screen instanceof AbstractContainerScreen;
    }

    @LuaWhitelist
    public void saveTexture(@LuaNotNil FiguraTexture texture) {
        if (isHost()) {
            try {
                texture.saveCache();
            } catch (Exception e) {
                throw new LuaError(e.getMessage());
            }
        }
    }

    @LuaWhitelist
    public FiguraTexture screenshot(@LuaNotNil String name) {
        if (!isHost())
            return null;

        String screenshot = name == null ? "screenshot" : name;
        NativeImage img = Screenshot.takeScreenshot(Minecraft.getInstance().getMainRenderTarget());
        return owner.luaRuntime.texture.register(name, img, true);
    }

    @LuaWhitelist
    public Object __index(String arg) {
        if ("unlockCursor".equals(arg))
            return unlockCursor;
        return null;
    }

    @LuaWhitelist
    public void __newindex(String key, Object value) {
        if ("unlockCursor".equals(key))
            unlockCursor = (Boolean) value;
    }

    @Override
    public String toString() {
        return "HostAPI";
    }
}
