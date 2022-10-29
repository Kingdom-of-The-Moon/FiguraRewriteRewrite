package org.moon.figura.lua.api.math;

import org.luaj.vm2.LuaError;
import org.moon.figura.lua.LuaNotNil;
import org.moon.figura.lua.LuaWhitelist;
import org.moon.figura.lua.docs.LuaMethodDoc;
import org.moon.figura.lua.docs.LuaMethodOverload;
import org.moon.figura.lua.docs.LuaTypeDoc;
import org.moon.figura.math.matrix.FiguraMat2;
import org.moon.figura.math.matrix.FiguraMat3;
import org.moon.figura.math.matrix.FiguraMat4;
import org.moon.figura.math.vector.FiguraVec2;
import org.moon.figura.math.vector.FiguraVec3;
import org.moon.figura.math.vector.FiguraVec4;
import org.moon.figura.utils.LuaUtils;

@LuaWhitelist
@LuaTypeDoc(
        name = "MatricesAPI",
        value = "matrices"
)
public class MatricesAPI {

    public static final MatricesAPI INSTANCE = new MatricesAPI();

    @LuaWhitelist
    public static FiguraMat2 mat2(){
        return FiguraMat2.of();
    }

    @LuaWhitelist
    public static FiguraMat2 mat2(@LuaNotNil FiguraVec2 col1, @LuaNotNil FiguraVec2 col2) {
        return FiguraMat2.of(
                col1.x, col1.y,
                col2.x, col2.y
        );
    }

    @LuaWhitelist
    public static FiguraMat3 mat3(){
        return FiguraMat3.of();
    }

    @LuaWhitelist
    public static FiguraMat3 mat3(FiguraVec3 col1, FiguraVec3 col2, FiguraVec3 col3) {
        return FiguraMat3.of(
                col1.x, col1.y, col1.z,
                col2.x, col2.y, col2.z,
                col3.x, col3.y, col3.z
        );
    }

    @LuaWhitelist
    public static FiguraMat4 mat4(){
        return FiguraMat4.of();
    }

    @LuaWhitelist
    public static FiguraMat4 mat4(FiguraVec4 col1, FiguraVec4 col2, FiguraVec4 col3, FiguraVec4 col4) {
        return FiguraMat4.of(
                col1.x, col1.y, col1.z, col1.w,
                col2.x, col2.y, col2.z, col2.w,
                col3.x, col3.y, col3.z, col3.w,
                col4.x, col4.y, col4.z, col4.w
        );
    }

    //-- ROTATION MATRICES --//
    @LuaWhitelist
    public static FiguraMat2 rotation2(double angle) {
        FiguraMat2 mat = FiguraMat2.of();
        mat.rotate(angle);
        return mat;
    }

    @LuaWhitelist
    public static FiguraMat3 rotation3(Double x, Double y, Double z){
        return rotation3(LuaUtils.freeVec3("rotation3", x, y, z));
    }

    @LuaWhitelist
    public static FiguraMat3 rotation3(@LuaNotNil FiguraVec3 vec) {
        FiguraMat3 result = FiguraMat3.of();
        result.rotateZYX(vec);
        return result;
    }

    @LuaWhitelist
    public static FiguraMat3 xRotation3(double degrees) {
        FiguraMat3 result = FiguraMat3.of();
        result.rotateX(degrees);
        return result;
    }

    @LuaWhitelist
    public static FiguraMat3 yRotation3(double degrees) {
        FiguraMat3 result = FiguraMat3.of();
        result.rotateY(degrees);
        return result;
    }

    @LuaWhitelist
    public static FiguraMat3 zRotation3(double degrees) {
        FiguraMat3 result = FiguraMat3.of();
        result.rotateZ(degrees);
        return result;
    }

    @LuaWhitelist
    public static FiguraMat4 rotation4(Double x, Double y, Double z){
        return rotation4(LuaUtils.freeVec3("rotation3", x, y, z));
    }

    @LuaWhitelist
    public static FiguraMat4 rotation4(@LuaNotNil FiguraVec3 vec) {
        FiguraMat4 result = FiguraMat4.of();
        result.rotateZYX(vec);
        return result;
    }

    @LuaWhitelist
    public static FiguraMat4 xRotation4(double degrees) {
        FiguraMat4 result = FiguraMat4.of();
        result.rotateX(degrees);
        return result;
    }

    @LuaWhitelist
    public static FiguraMat4 yRotation4(double degrees) {
        FiguraMat4 result = FiguraMat4.of();
        result.rotateY(degrees);
        return result;
    }

    @LuaWhitelist
    public static FiguraMat4 zRotation4(double degrees) {
        FiguraMat4 result = FiguraMat4.of();
        result.rotateZ(degrees);
        return result;
    }

    //-- SCALE MATRICES --//
    @LuaWhitelist
    public static FiguraMat2 scale2(Double x, Double y){
        return scale2(LuaUtils.freeVec2("scale2", x, y, 1, 1));
    }

    @LuaWhitelist
    public static FiguraMat2 scale2(@LuaNotNil FiguraVec2 vec) {
        FiguraMat2 result = FiguraMat2.of();
        result.scale(vec);
        return result;
    }

    @LuaWhitelist
    public static FiguraMat3 scale3(Double x, Double y, Double z){
        return scale3(LuaUtils.freeVec3("scale3", x, y, z, 1, 1, 1));
    }

    @LuaWhitelist
    public static FiguraMat3 scale3(@LuaNotNil FiguraVec3 vec) {
        FiguraMat3 result = FiguraMat3.of();
        result.scale(vec);
        return result;
    }

    @LuaWhitelist
    public static FiguraMat4 scale4(Double x, Double y, Double z){
        return scale4(LuaUtils.freeVec3("scale4", x, y, z, 1, 1, 1));
    }

    @LuaWhitelist
    public static FiguraMat4 scale4(@LuaNotNil FiguraVec3 vec) {
        FiguraMat4 result = FiguraMat4.of();
        result.scale(vec);
        return result;
    }

    //-- TRANSLATION MATRICES --//
    @LuaWhitelist
    public static FiguraMat3 translate3(Double x, Double y){
        return translate3(LuaUtils.parseVec2("translate3", x, y));
    }

    @LuaWhitelist
    public static FiguraMat3 translate3(@LuaNotNil FiguraVec2 vec) {
        FiguraMat3 result = FiguraMat3.of();
        result.translate(vec);
        return result;
    }

    @LuaWhitelist
    public static FiguraMat4 translate4(Double x, Double y, Double z){
        return translate4(LuaUtils.parseVec3("translate4", x, y, z));
    }

    @LuaWhitelist
    public static FiguraMat4 translate4(@LuaNotNil FiguraVec3 vec) {
        FiguraMat4 result = FiguraMat4.of();
        result.translate(vec);
        return result;
    }

    @Override
    public String toString() {
        return "MatricesAPI";
    }
}
