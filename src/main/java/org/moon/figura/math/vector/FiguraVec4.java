package org.moon.figura.math.vector;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;
import org.moon.figura.lua.LuaNotNil;
import org.moon.figura.lua.LuaWhitelist;
import org.moon.figura.lua.docs.LuaTypeDoc;
import org.moon.figura.math.matrix.FiguraMat4;
import org.moon.figura.utils.caching.CacheUtils;

@LuaWhitelist
@LuaTypeDoc(
        name = "Vector4",
        value = "vector4"
)
public class FiguraVec4 extends FiguraVector<FiguraVec4, FiguraMat4> {

    @LuaWhitelist
    public double x;
    @LuaWhitelist
    public double y;
    @LuaWhitelist
    public double z;
    @LuaWhitelist
    public double w;

    // -- cache -- //

    private final static CacheUtils.Cache<FiguraVec4> CACHE = CacheUtils.getCache(FiguraVec4::new, 300);

    private final static CacheUtils.Cache<FiguraVec4> IMMEDIATE = CacheUtils.getIndifferent(FiguraVec4::new, 15);

    @Override
    @LuaWhitelist
    public FiguraVec4 reset() {
        x = y = z = w = 0;
        return this;
    }

    public static FiguraVec4 oneUse(){
        return IMMEDIATE.getFresh();
    }

    public static FiguraVec4 oneUse(double x, double y, double z, double w){
        return IMMEDIATE.getFresh().set(x, y, z, w);
    }

    @Override
    public void free() {
        CACHE.offerOld(this);
    }

    public static FiguraVec4 of() {
        return CACHE.getFresh();
    }

    public static FiguraVec4 of(double x, double y, double z, double w) {
        return CACHE.getFresh().set(x, y, z, w);
    }

    // -- basic math -- //

    @Override
    @LuaWhitelist
    public FiguraVec4 set(@LuaNotNil FiguraVec4 other) {
        return set(other.x, other.y, other.z, other.w);
    }

    @LuaWhitelist
    public FiguraVec4 set(double x, double y, double z, double w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        return this;
    }

    @Override
    @LuaWhitelist
    public FiguraVec4 add(@LuaNotNil FiguraVec4 other) {
        return add(other.x, other.y, other.z, other.w);
    }

    @LuaWhitelist
    public FiguraVec4 add(double x, double y, double z, double w) {
        this.x += x;
        this.y += y;
        this.z += z;
        this.w += w;
        return this;
    }

    @Override
    @LuaWhitelist
    public FiguraVec4 sub(@LuaNotNil FiguraVec4 other) {
        return sub(other.x, other.y, other.z, other.w);
    }

    @LuaWhitelist
    public FiguraVec4 sub(double x, double y, double z, double w) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        this.w -= w;
        return this;
    }

    @Override
    @LuaWhitelist
    public FiguraVec4 offset(@LuaNotNil double factor) {
        this.x += factor;
        this.y += factor;
        this.z += factor;
        this.w += factor;
        return this;
    }

    @Override
    @LuaWhitelist
    public FiguraVec4 mul(@LuaNotNil FiguraVec4 other) {
        return mul(other.x, other.y, other.z, other.w);
    }

    @LuaWhitelist
    public FiguraVec4 mul(double x, double y, double z, double w) {
        this.x *= x;
        this.y *= y;
        this.z *= z;
        this.w *= w;
        return this;
    }

    @Override
    @LuaWhitelist
    public FiguraVec4 div(@LuaNotNil FiguraVec4 other) {
        return div(other.x, other.y, other.z, other.w);
    }

    @LuaWhitelist
    public FiguraVec4 div(double x, double y, double z, double w) {
        this.x /= x;
        this.y /= y;
        this.z /= z;
        this.w /= w;
        return this;
    }

    @Override
    @LuaWhitelist
    public FiguraVec4 reduce(@LuaNotNil FiguraVec4 other) {
        return reduce(other.x, other.y, other.z, other.w);
    }

    @LuaWhitelist
    public FiguraVec4 reduce(double x, double y, double z, double w) {
        this.x = ((this.x % x) + x) % x;
        this.y = ((this.y % y) + y) % y;
        this.z = ((this.z % z) + z) % z;
        this.w = ((this.w % w) + w) % w;
        return this;
    }

    @Override
    @LuaWhitelist
    public FiguraVec4 scale(@LuaNotNil double factor) {
        this.x *= factor;
        this.y *= factor;
        this.z *= factor;
        this.w *= factor;
        return this;
    }

    @Override
    @LuaWhitelist
    public double[] unpack() {
        return new double[]{x, y, z, w};
    }

    // -- utility methods -- //

    @Override
    @LuaWhitelist
    public FiguraVec4 transform(@LuaNotNil FiguraMat4 mat) {
        return set(
                mat.v11 * x + mat.v12 * y + mat.v13 * z + mat.v14 * w,
                mat.v21 * x + mat.v22 * y + mat.v23 * z + mat.v24 * w,
                mat.v31 * x + mat.v32 * y + mat.v33 * z + mat.v34 * w,
                mat.v41 * x + mat.v42 * y + mat.v43 * z + mat.v44 * w
        );
    }

    @Override
    @LuaWhitelist
    public double lengthSquared() {
        return x * x + y * y + z * z + w * w;
    }

    @Override
    @LuaWhitelist
    public FiguraVec4 copy() {
        return of(x, y, z, w);
    }

    @Override
    @LuaWhitelist
    public double dot(@LuaNotNil FiguraVec4 other) {
        return x * other.x + y * other.y + z * other.z + w * other.w;
    }

    @Override
    @LuaWhitelist
    public FiguraVec4 normalize() {
        return super.normalize();
    }

    @Override
    @LuaWhitelist
    public FiguraVec4 normalized() {
        return super.normalized();
    }

    @Override
    @LuaWhitelist
    public FiguraVec4 clampLength(Double minLength, Double maxLength) {
        return super.clampLength(minLength, maxLength);
    }

    @Override
    @LuaWhitelist
    public FiguraVec4 clamped(Double minLength, Double maxLength) {
        return super.clamped(minLength, maxLength);
    }

    @Override
    @LuaWhitelist
    public double length() {
        return super.length();
    }

    @Override
    @LuaWhitelist
    public FiguraVec4 toRad() {
        return super.toRad();
    }

    @Override
    @LuaWhitelist
    public FiguraVec4 toDeg() {
        return super.toDeg();
    }

    @LuaWhitelist
    public FiguraVec4 floor() {
        return FiguraVec4.of(Math.floor(x), Math.floor(y), Math.floor(z), Math.floor(w));
    }

    @LuaWhitelist
    public FiguraVec4 ceil() {
        return FiguraVec4.of(Math.ceil(x), Math.ceil(y), Math.ceil(z), Math.ceil(w));
    }

    @LuaWhitelist
    public FiguraVec4 applyFunc(@LuaNotNil LuaFunction function) {
        x = function.call(LuaValue.valueOf(x), LuaValue.valueOf(1)).todouble();
        y = function.call(LuaValue.valueOf(y), LuaValue.valueOf(2)).todouble();
        z = function.call(LuaValue.valueOf(z), LuaValue.valueOf(3)).todouble();
        w = function.call(LuaValue.valueOf(w), LuaValue.valueOf(4)).todouble();
        return this;
    }

    @Override
    public int size() {
        return 4;
    }

    public double x() {
        return x;
    }
    public double y() {
        return y;
    }
    public double z() {
        return z;
    }
    public double w() {
        return w;
    }

    @Override
    public double index(int i) {
        return switch (i) {
            case 0 -> x;
            case 1 -> y;
            case 2 -> z;
            case 3 -> w;
            default -> throw new IndexOutOfBoundsException(i);
        };
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof FiguraVec4 vec && x == vec.x && y == vec.y && z == vec.z && w == vec.w;
    }

    // -- metamethods -- //

    @LuaWhitelist
    public static FiguraVec4 __add(@LuaNotNil FiguraVec4 one, @LuaNotNil FiguraVec4 other) {
        return one.plus(other);
    }

    @LuaWhitelist
    public static FiguraVec4 __add(@LuaNotNil FiguraVec4 one, double other) {
        return one.offseted(other);
    }

    @LuaWhitelist
    public static FiguraVec4 __add(double one, @LuaNotNil FiguraVec4 other) {
        return other.offseted(one);
    }

    @LuaWhitelist
    public static FiguraVec4 __sub(@LuaNotNil FiguraVec4 one, @LuaNotNil FiguraVec4 other) {
        return one.minus(other);
    }

    @LuaWhitelist
    public static FiguraVec4 __sub(@LuaNotNil FiguraVec4 one, double other) {
        return one.offseted(-other);
    }

    @LuaWhitelist
    public static FiguraVec4 __sub(double one, @LuaNotNil FiguraVec4 other) {
        return other.scaled(-1).offset(one);
    }

    @LuaWhitelist
    public static FiguraVec4 __mul(@LuaNotNil FiguraVec4 one, @LuaNotNil FiguraVec4 other) {
        return one.times(other);
    }

    @LuaWhitelist
    public static FiguraVec4 __mul(@LuaNotNil FiguraVec4 one, double other) {
        return one.scaled(other);
    }

    @LuaWhitelist
    public static FiguraVec4 __mul(double one, @LuaNotNil FiguraVec4 other) {
        return other.scaled(one);
    }

    @LuaWhitelist
    public FiguraVec4 __div(@LuaNotNil FiguraVec4 other) {
        return dividedBy(other);
    }

    @LuaWhitelist
    public FiguraVec4 __div(double other) {
        if(other == 0)
            throw new LuaError(new ArithmeticException("Division by zero"));
        return scaled(1 / other);
    }

    @LuaWhitelist
    public FiguraVec4 __mod(@LuaNotNil FiguraVec4 other) {
        return mod(other);
    }

    @LuaWhitelist
    public FiguraVec4 __mod(double other) {
        if (other == 0)
            throw new LuaError("Attempt to reduce vector by 0");
        return mod(oneUse(other, other, other, other));
    }

    @LuaWhitelist
    public boolean __eq(FiguraVec4 other) {
        return equals(other);
    }

    @LuaWhitelist
    public FiguraVec4 __unm() {
        return scaled(-1);
    }

    @LuaWhitelist
    public int __len() {
        return size();
    }

    @LuaWhitelist
    public boolean __lt(@LuaNotNil FiguraVec4 r) {
        return x < r.x && y < r.y && z < r.z && w < r.w;
    }

    @LuaWhitelist
    public boolean __le(@LuaNotNil FiguraVec4 r) {
        return x <= r.x && y <= r.y && z <= r.z && w <= r.w;
    }

    @LuaWhitelist
    public String __tostring() {
        return toString();
    }

    @Override
    protected Double getSwizzleComponent(char symbol) {
        return switch (symbol) {
            case '1', 'x', 'r' -> x;
            case '2', 'y', 'g' -> y;
            case '3', 'z', 'b' -> z;
            case '4', 'w', 'a' -> w;
            case '_' -> 0d;
            default -> null;
        };
    }

    @Override
    protected void setSwizzleComponent(char symbol, double value) {
        switch (symbol) {
            case '1', 'x', 'r' -> x = value;
            case '2', 'y', 'g' -> y = value;
            case '3', 'z', 'b' -> z = value;
            case '4', 'w', 'a' -> w = value;
            case '_' -> {
            }
            default -> throw new LuaError("Invalid swizzle component: " + symbol);
        }
    }
}
