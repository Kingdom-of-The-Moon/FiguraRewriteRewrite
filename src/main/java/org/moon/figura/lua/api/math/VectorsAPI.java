package org.moon.figura.lua.api.math;

import org.luaj.vm2.LuaError;
import org.moon.figura.lua.LuaWhitelist;
import org.moon.figura.lua.docs.LuaTypeDoc;
import org.moon.figura.math.vector.*;
import org.moon.figura.utils.ColorUtils;
import org.moon.figura.utils.LuaUtils;
import org.moon.figura.utils.MathUtils;

@LuaWhitelist
@LuaTypeDoc(
        name = "VectorsAPI",
        value = "vectors"
)
public class VectorsAPI {

    public static final VectorsAPI INSTANCE = new VectorsAPI();

    @LuaWhitelist
    public static FiguraVec2 vec2(){
        return FiguraVec2.of();
    }

    @LuaWhitelist
    public static FiguraVec2 vec2(double x, double y) {
        return FiguraVec2.of(x, y);
    }

    @LuaWhitelist
    public static FiguraVec2 vec(double x, double y) {
        return vec2(x, y);
    }

    @LuaWhitelist
    public static FiguraVec3 vec3(){
        return FiguraVec3.of();
    }

    @LuaWhitelist
    public static FiguraVec3 vec3(double x, double y, double z) {
        return FiguraVec3.of(x, y, z);
    }

    @LuaWhitelist
    public static FiguraVec3 vec(double x, double y, double z) {
        return vec3(x, y, z);
    }

    @LuaWhitelist
    public static FiguraVec4 vec4(){
        return FiguraVec4.of();
    }

    @LuaWhitelist
    public static FiguraVec4 vec4(double x, double y, double z, double w) {
        return FiguraVec4.of(x, y, z, w);
    }

    @LuaWhitelist
    public static FiguraVec4 vec(double x, double y, double z, double w) {
        return vec4(x, y, z, w);
    }

    @LuaWhitelist
    public static FiguraVec5 vec5(){
        return FiguraVec5.of();
    }

    @LuaWhitelist
    public static FiguraVec5 vec5(double x, double y, double z, double w, double t) {
        return FiguraVec5.of(x, y, z, w, t);
    }

    @LuaWhitelist
    public static FiguraVec5 vec(double x, double y, double z, double w, double t) {
        return vec5(x, y, z, w, t);
    }

    @LuaWhitelist
    public static FiguraVec6 vec6(){
        return FiguraVec6.of();
    }

    @LuaWhitelist
    public static FiguraVec6 vec6(double x, double y, double z, double w, double t, double h) {
        return FiguraVec6.of(x, y, z, w, t, h);
    }

    @LuaWhitelist
    public static FiguraVec6 vec(double x, double y, double z, double w, double t, double h) {
        return vec6(x, y, z, w, t, h);
    }

    // -- colors -- //

    @LuaWhitelist
    public static int rgbToInt(double r, double g, double b){
        return rgbToInt(LuaUtils.freeVec3("rgbToInt", r, g, b));
    }

    @LuaWhitelist
    public static int rgbToInt(FiguraVec3 rgb) {
        return ColorUtils.rgbToInt(rgb);
    }

    @LuaWhitelist
    public static FiguraVec3 intToRGB(int color) {
        return ColorUtils.intToRGB(color);
    }

    @LuaWhitelist
    public static FiguraVec3 hexToRGB(String hex) {
        return ColorUtils.intToRGB(ColorUtils.userInputHex(hex, FiguraVec3.of()));
    }

    @LuaWhitelist
    public static FiguraVec3 hsvToRGB(double h, double s, double v){
        return hsvToRGB(LuaUtils.freeVec3("hsvToRGB", h, s, v));
    }

    @LuaWhitelist
    public static FiguraVec3 hsvToRGB(FiguraVec3 hsv) {
        return ColorUtils.hsvToRGB(hsv);
    }

    @LuaWhitelist
    public static FiguraVec3 rgbToHSV(double r, double g, double b){
        return rgbToHSV(LuaUtils.freeVec3("rgbToHSV", r, g, b));
    }

    @LuaWhitelist
    public static FiguraVec3 rgbToHSV(FiguraVec3 rgb) {
        return ColorUtils.rgbToHSV(rgb);
    }

    @LuaWhitelist
    public static String rgbToHex(double r, double g, double b){
        return rgbToHex(LuaUtils.freeVec3("rgbToHex", r, g, b));
    }

    @LuaWhitelist
    public static String rgbToHex(FiguraVec3 rgb) {
        return ColorUtils.rgbToHex(rgb);
    }

    // -- math utils -- //

    @LuaWhitelist
    public static FiguraVec3 rotateAroundAxis(double angle, double x, double y, double z, double axisX, double axisY, double axisZ){
        return rotateAroundAxis(angle, LuaUtils.freeVec3("rotateAroundAxis", x, y, z), LuaUtils.freeVec3("rotateAroundAxis", axisX, axisY, axisZ));
    }

    @LuaWhitelist
    public static FiguraVec3 rotateAroundAxis(double angle, FiguraVec3 vec, double axisX, double axisY, double axisZ){
        return rotateAroundAxis(angle, vec, LuaUtils.freeVec3("rotateAroundAxis", axisX, axisY, axisZ));
    }

    @LuaWhitelist
    public static FiguraVec3 rotateAroundAxis(double angle, double x, double y, double z, FiguraVec3 axis){
        return rotateAroundAxis(angle, LuaUtils.freeVec3("rotateAroundAxis", x, y, z), axis);
    }

    @LuaWhitelist
    public static FiguraVec3 rotateAroundAxis(double angle, FiguraVec3 vec, FiguraVec3 axis){

        return MathUtils.rotateAroundAxis(vec, axis, angle);
    }

    @LuaWhitelist
    public static FiguraVec3 toCameraSpace(double x, double y, double z){
        return toCameraSpace(LuaUtils.freeVec3("toCameraSpace", x, y, z));
    }

    @LuaWhitelist
    public static FiguraVec3 toCameraSpace(FiguraVec3 vec) {
        return MathUtils.toCameraSpace(vec);
    }

    @LuaWhitelist
    public static FiguraVec4 worldToScreenSpace(double x, double y, double z){
        return worldToScreenSpace(LuaUtils.freeVec3("worldToScreenSpace", x, y, z));
    }

    @LuaWhitelist
    public static FiguraVec4 worldToScreenSpace(FiguraVec3 vec) {
        return MathUtils.worldToScreenSpace(vec);
    }

    @Override
    public String toString() {
        return "VectorsAPI";
    }
}
