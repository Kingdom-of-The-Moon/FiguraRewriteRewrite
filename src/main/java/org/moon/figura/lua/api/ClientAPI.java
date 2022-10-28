package org.moon.figura.lua.api;

import com.mojang.blaze3d.platform.Window;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.world.phys.Vec3;
import org.luaj.vm2.LuaError;
import org.moon.figura.FiguraMod;
import org.moon.figura.lua.LuaNotNil;
import org.moon.figura.lua.LuaWhitelist;
import org.moon.figura.lua.docs.LuaTypeDoc;
import org.moon.figura.math.vector.FiguraVec2;
import org.moon.figura.math.vector.FiguraVec3;
import org.moon.figura.utils.MathUtils;
import org.moon.figura.utils.TextUtils;
import org.moon.figura.utils.Version;

import java.util.ArrayList;
import java.util.List;

@LuaWhitelist
@LuaTypeDoc(
        name = "ClientAPI",
        value = "client"
)
public class ClientAPI {

    public static final ClientAPI INSTANCE = new ClientAPI();
    private static final boolean hasIris = FabricLoader.getInstance().isModLoaded("iris");

    @LuaWhitelist
    public static int getFPS() {
        String s = getFPSString();
        if (s.length() == 0)
            return 0;
        return Integer.parseInt(s.split(" ")[0]);
    }

    @LuaWhitelist
    public static String getFPSString() {
        return Minecraft.getInstance().fpsString;
    }

    @LuaWhitelist
    public static boolean isPaused() {
        return Minecraft.getInstance().isPaused();
    }

    @LuaWhitelist
    public static String getVersion() {
        return Minecraft.getInstance().getLaunchedVersion();
    }

    @LuaWhitelist
    public static String getVersionType() {
        return Minecraft.getInstance().getVersionType();
    }

    @LuaWhitelist
    public static String getServerBrand() {
        if (Minecraft.getInstance().player == null)
            return null;

        return Minecraft.getInstance().getSingleplayerServer() == null ? Minecraft.getInstance().player.getServerBrand() : "Integrated";
    }

    @LuaWhitelist
    public static String getChunkStatistics() {
        return Minecraft.getInstance().levelRenderer.getChunkStatistics();
    }

    @LuaWhitelist
    public static String getEntityStatistics() {
        return Minecraft.getInstance().levelRenderer.getEntityStatistics();
    }

    @LuaWhitelist
    public static String getSoundStatistics() {
        return Minecraft.getInstance().getSoundManager().getDebugString();
    }

    @LuaWhitelist
    public static int getEntityCount() {
        if (Minecraft.getInstance().level == null)
            return 0;

        return Minecraft.getInstance().level.getEntityCount();
    }

    @LuaWhitelist
    public static String getParticleCount() {
        return Minecraft.getInstance().particleEngine.countParticles();
    }

    @LuaWhitelist
    public static String getCurrentEffect() {
        if (Minecraft.getInstance().gameRenderer.currentEffect() == null)
            return null;

        return Minecraft.getInstance().gameRenderer.currentEffect().getName();
    }

    @LuaWhitelist
    public static String getJavaVersion() {
        return System.getProperty("java.version");
    }

    @LuaWhitelist
    public static long getUsedMemory() {
        return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
    }

    @LuaWhitelist
    public static long getMaxMemory() {
        return Runtime.getRuntime().maxMemory();
    }

    @LuaWhitelist
    public static long getAllocatedMemory() {
        return Runtime.getRuntime().totalMemory();
    }

    @LuaWhitelist
    public static boolean isWindowFocused() {
        return Minecraft.getInstance().isWindowActive();
    }

    @LuaWhitelist
    public static boolean isHudEnabled() {
        return Minecraft.renderNames();
    }

    @LuaWhitelist
    public static boolean isDebugOverlayEnabled() {
        return Minecraft.getInstance().options.renderDebug;
    }

    @LuaWhitelist
    public static FiguraVec2 getWindowSize() {
        Window window = Minecraft.getInstance().getWindow();
        return FiguraVec2.of(window.getWidth(), window.getHeight());
    }

    @LuaWhitelist
    public static double getFOV() {
        return Minecraft.getInstance().options.fov().get();
    }

    @LuaWhitelist
    public static long getSystemTime() {
        return System.currentTimeMillis();
    }

    @LuaWhitelist
    public static FiguraVec2 getMousePos() {
        MouseHandler mouse = Minecraft.getInstance().mouseHandler;
        return FiguraVec2.of(mouse.xpos(), mouse.ypos());
    }

    @LuaWhitelist
    public static FiguraVec2 getScaledWindowSize() {
        Window window = Minecraft.getInstance().getWindow();
        return FiguraVec2.of(window.getGuiScaledWidth(), window.getGuiScaledHeight());
    }

    @LuaWhitelist
    public static double getGuiScale() {
        return Minecraft.getInstance().getWindow().getGuiScale();
    }

    @LuaWhitelist
    public static FiguraVec3 getCameraPos() {
        Vec3 pos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        return FiguraVec3.fromVec3(pos);
    }

    @LuaWhitelist
    public static FiguraVec3 getCameraRot() {
        double f = 180d / Math.PI;
        return MathUtils.quaternionToYXZ(Minecraft.getInstance().gameRenderer.getMainCamera().rotation()).mul(f, -f, f); //degrees, and negate y
    }

    @LuaWhitelist
    public static int getTextWidth(@LuaNotNil String text) {
        return TextUtils.getWidth(TextUtils.splitText(TextUtils.tryParseJson(text), "\n"), Minecraft.getInstance().font);
    }

    @LuaWhitelist
    public static int getTextHeight(@LuaNotNil String text) {
        return Minecraft.getInstance().font.lineHeight * TextUtils.splitText(TextUtils.tryParseJson(text), "\n").size();
    }

    @LuaWhitelist
    public static String getActiveLang() {
        return Minecraft.getInstance().options.languageCode;
    }

    @LuaWhitelist
    public static boolean hasIris() {
        return hasIris;
    }

    @LuaWhitelist
    public static boolean hasIrisShader() {
        return hasIris && net.irisshaders.iris.api.v0.IrisApi.getInstance().isShaderPackInUse();
    }

    @LuaWhitelist
    public static boolean hasResource(@LuaNotNil String path) {
        try {
            ResourceLocation resource = new ResourceLocation(path);
            return Minecraft.getInstance().getResourceManager().getResource(resource).isPresent();
        } catch (Exception ignored) {
            return false;
        }
    }

    @LuaWhitelist
    public static List<String> getActiveResourcePacks() {
        List<String> list = new ArrayList<>();

        for (Pack pack : Minecraft.getInstance().getResourcePackRepository().getSelectedPacks())
            list.add(pack.getTitle().getString());

        return list;
    }

    @LuaWhitelist
    public static String getFiguraVersion() {
        return FiguraMod.VERSION;
    }

    @LuaWhitelist
    public static int compareVersions(@LuaNotNil String ver1, @LuaNotNil String ver2) {
        Version v1 = new Version(ver1);
        Version v2 = new Version(ver2);

        if (v1.invalid)
            throw new LuaError("Cannot parse version " + "\"" + ver1 + "\"");
        if (v2.invalid)
            throw new LuaError("Cannot parse version " + "\"" + ver1 + "\"");

        return v1.compareTo(v2);
    }

    @Override
    public String toString() {
        return "ClientAPI";
    }
}
