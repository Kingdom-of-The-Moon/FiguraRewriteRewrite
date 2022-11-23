package org.moon.figura.lua.newdocswip;

import org.moon.figura.lua.LuaWhitelist;
import org.moon.figura.lua.docs.LuaFieldDoc;
import org.moon.figura.lua.docs.LuaMethodDoc;
import org.moon.figura.lua.docs.LuaMethodOverload;
import org.moon.figura.lua.docs.LuaTypeDoc;
import org.moon.figura.math.vector.*;

/**
 * Adds docs for the functions added to Lua's math library.
 */
@LuaTypeDoc(
        name = "math",
        value = "math"
)
@LuaWhitelist
public class NewMathDocs {

    @LuaWhitelist
    public double playerScale;

    @LuaWhitelist
    public double worldScale;

    @LuaWhitelist
    public static FiguraVec6 lerp(FiguraVec6 a, FiguraVec6 b, double t) { return null; }

    @LuaWhitelist
    public static FiguraVec5 lerp(FiguraVec5 a, FiguraVec5 b, double t) { return null; }

    @LuaWhitelist
    public static FiguraVec4 lerp(FiguraVec4 a, FiguraVec4 b, double t) { return null; }

    @LuaWhitelist
    public static FiguraVec3 lerp(FiguraVec3 a, FiguraVec3 b, double t) { return null; }

    @LuaWhitelist
    public static FiguraVec2 lerp(FiguraVec2 a, FiguraVec2 b, double t) { return null; }

    @LuaWhitelist
    public static double lerp(double a, double b, double t) { return 0; }

    @LuaWhitelist
    public static double clamp(double value, double min, double max) { return 0; }

    @LuaWhitelist
    public static double round(double value) { return 0; }

    @LuaWhitelist
    public static double map(double value, double oldMin, double oldMax, double newMin, double newMax) { return 0; }

    @LuaWhitelist
    public static double shorAngle(double from, double to) { return 0; }

    @LuaWhitelist
    public static double lerpAngle(double a, double b, double t) { return 0; }

    @LuaWhitelist
    public static double sign(double value) { return 0; }
}
