package org.moon.figura.math.vector;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;
import org.moon.figura.lua.LuaNotNil;
import org.moon.figura.lua.LuaWhitelist;
import org.moon.figura.lua.docs.*;
import org.moon.figura.math.matrix.FiguraMatrix;
import org.moon.figura.utils.caching.CacheUtils;

@LuaWhitelist
@LuaTypeDoc(
        name = "Vector6",
        value = "vector6"
)
public class FiguraVec6 extends FiguraVector<FiguraVec6, FiguraMatrix.DummyMatrix<FiguraVec6>> {

    @LuaWhitelist
    public double x;
    @LuaWhitelist
    public double y;
    @LuaWhitelist
    public double z;
    @LuaWhitelist
    public double w;
    @LuaWhitelist
    public double t;
    @LuaWhitelist
    public double h;

    // -- cache -- //

    private final static CacheUtils.Cache<FiguraVec6> CACHE = CacheUtils.getCache(FiguraVec6::new, 300);

    private final static CacheUtils.Cache<FiguraVec6> IMMEDIATE = CacheUtils.getIndifferent(FiguraVec6::new, 15);

    @Override
    @LuaWhitelist
    public FiguraVec6 reset() {
        x = y = z = w = t = h = 0;
        return this;
    }

    public static FiguraVec6 oneUse(){
        return IMMEDIATE.getFresh();
    }

    public static FiguraVec6 oneUse(double x, double y, double z, double w, double t, double h){
        return IMMEDIATE.getFresh().set(x, y, z, w, t, h);
    }

    @Override
    public void free() {
        CACHE.offerOld(this);
    }

    public static FiguraVec6 of() {
        return CACHE.getFresh();
    }

    public static FiguraVec6 of(double x, double y, double z, double w, double t, double h) {
        return CACHE.getFresh().set(x, y, z, w, t, h);
    }

    // -- basic math -- //

    @Override
    @LuaWhitelist
    public FiguraVec6 set(@LuaNotNil FiguraVec6 other) {
        return set(other.x, other.y, other.z, other.w, other.t, other.h);
    }

    @LuaWhitelist
    public FiguraVec6 set(double x, double y, double z, double w, double t, double h) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        this.t = t;
        this.h = h;
        return this;
    }

    @Override
    @LuaWhitelist
    public FiguraVec6 add(@LuaNotNil FiguraVec6 other) {
        return add(other.x, other.y, other.z, other.w, other.t, other.h);
    }

    @LuaWhitelist
    public FiguraVec6 add(double x, double y, double z, double w, double t, double h) {
        this.x += x;
        this.y += y;
        this.z += z;
        this.w += w;
        this.t += t;
        this.h += h;
        return this;
    }

    @Override
    @LuaWhitelist
    public FiguraVec6 sub(@LuaNotNil FiguraVec6 other) {
        return sub(other.x, other.y, other.z, other.w, other.t, other.h);
    }

    @LuaWhitelist
    public FiguraVec6 sub(double x, double y, double z, double w, double t, double h) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        this.w -= w;
        this.t -= t;
        this.h -= h;
        return this;
    }

    @Override
    @LuaWhitelist
    public FiguraVec6 offset(double factor) {
        return add(factor, factor, factor, factor, factor, factor);
    }

    @Override
    @LuaWhitelist
    public FiguraVec6 mul(@LuaNotNil FiguraVec6 other) {
        return mul(other.x, other.y, other.z, other.w, other.t, other.h);
    }

    @LuaWhitelist
    public FiguraVec6 mul(double x, double y, double z, double w, double t, double h) {
        this.x *= x;
        this.y *= y;
        this.z *= z;
        this.w *= w;
        this.t *= t;
        this.h *= h;
        return this;
    }

    @Override
    @LuaWhitelist
    public FiguraVec6 div(@LuaNotNil FiguraVec6 other) {
        return div(other.x, other.y, other.z, other.w, other.t, other.h);
    }

    @LuaWhitelist
    public FiguraVec6 div(double x, double y, double z, double w, double t, double h) {
        this.x /= x;
        this.y /= y;
        this.z /= z;
        this.w /= w;
        this.t /= t;
        this.h /= h;
        return this;
    }

    @Override
    @LuaWhitelist
    public FiguraVec6 reduce(@LuaNotNil FiguraVec6 other) {
        return reduce(other.x, other.y, other.z, other.w, other.t, other.h);
    }

    @LuaWhitelist
    public FiguraVec6 reduce(double x, double y, double z, double w, double t, double h) {
        this.x = ((this.x % x) + x) % x;
        this.y = ((this.y % y) + y) % y;
        this.z = ((this.z % z) + z) % z;
        this.w = ((this.w % w) + w) % w;
        this.t = ((this.t % t) + t) % t;
        this.h = ((this.h % h) + h) % h;
        return this;
    }

    @Override
    @LuaWhitelist
    public FiguraVec6 scale(double factor) {
        return mul(factor, factor, factor, factor, factor, factor);
    }

    @Override
    @LuaWhitelist
    public double[] unpack() {
        return new double[]{x, y, z, w, t, h};
    }

    // -- utility methods -- //

    @Override
    public FiguraVec6 transform(FiguraMatrix.DummyMatrix<FiguraVec6> matrix) {
        throw new IllegalStateException("Called bad method, cannot transform a FiguraVec6");
    }

    @Override
    @LuaWhitelist
    public double lengthSquared() {
        return x * x + y * y + z * z + w * w + t * t + h * h;
    }

    @Override
    @LuaWhitelist
    public FiguraVec6 copy() {
        return of(x, y, z, w, t, h);
    }

    @Override
    @LuaWhitelist
    public double dot(@LuaNotNil FiguraVec6 other) {
        return x * other.x + y * other.y + z * other.z + w * other.w + t * other.t + h * other.h;
    }

    @Override
    @LuaWhitelist
    public FiguraVec6 normalize() {
        return super.normalize();
    }

    @Override
    @LuaWhitelist
    public FiguraVec6 normalized() {
        return super.normalized();
    }

    @Override
    @LuaWhitelist
    public FiguraVec6 clampLength(Double min, Double max) {
        return super.clampLength(min, max);
    }

    @Override
    @LuaWhitelist
    public FiguraVec6 clamped(Double min, Double max) {
        return super.clamped(min, max);
    }

    @Override
    @LuaWhitelist
    public double length() {
        return super.length();
    }

    @Override
    @LuaWhitelist
    public FiguraVec6 toRad() {
        return super.toRad();
    }

    @Override
    @LuaWhitelist
    public FiguraVec6 toDeg() {
        return super.toDeg();
    }

    @LuaWhitelist
    public FiguraVec6 floor() {
        return FiguraVec6.of(Math.floor(x), Math.floor(y), Math.floor(z), Math.floor(w), Math.floor(t), Math.floor(h));
    }

    @LuaWhitelist
    public FiguraVec6 ceil() {
        return FiguraVec6.of(Math.ceil(x), Math.ceil(y), Math.ceil(z), Math.ceil(w), Math.ceil(t), Math.ceil(h));
    }

    @LuaWhitelist
    public FiguraVec6 applyFunc(@LuaNotNil LuaFunction function) {
        x = function.call(LuaValue.valueOf(x), LuaValue.valueOf(1)).todouble();
        y = function.call(LuaValue.valueOf(y), LuaValue.valueOf(2)).todouble();
        z = function.call(LuaValue.valueOf(z), LuaValue.valueOf(3)).todouble();
        w = function.call(LuaValue.valueOf(w), LuaValue.valueOf(4)).todouble();
        t = function.call(LuaValue.valueOf(t), LuaValue.valueOf(5)).todouble();
        h = function.call(LuaValue.valueOf(h), LuaValue.valueOf(6)).todouble();
        return this;
    }

    @Override
    public int size() {
        return 6;
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

    public double t() {
        return t;
    }

    public double h() {
        return h;
    }

    @Override
    public double index(int i) {
        return switch (i) {
            case 0 -> x;
            case 1 -> y;
            case 2 -> z;
            case 3 -> w;
            case 4 -> t;
            case 5 -> h;
            default -> throw new IndexOutOfBoundsException(i);
        };
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof FiguraVec6 vec && x == vec.x && y == vec.y && z == vec.z && w == vec.w && t == vec.t && h == vec.h;
    }

    // -- metamethods -- //

    @LuaWhitelist
    public static FiguraVec6 __add(@LuaNotNil FiguraVec6 one, @LuaNotNil FiguraVec6 other) {
        return one.plus(other);
    }

    @LuaWhitelist
    public static FiguraVec6 __add(@LuaNotNil FiguraVec6 one, double other) {
        return one.offseted(other);
    }

    @LuaWhitelist
    public static FiguraVec6 __add(double one, @LuaNotNil FiguraVec6 other) {
        return other.offseted(one);
    }

    @LuaWhitelist
    public static FiguraVec6 __sub(@LuaNotNil FiguraVec6 one, @LuaNotNil FiguraVec6 other) {
        return one.minus(other);
    }

    @LuaWhitelist
    public static FiguraVec6 __sub(@LuaNotNil FiguraVec6 one, double other) {
        return one.offseted(-other);
    }

    @LuaWhitelist
    public static FiguraVec6 __sub(double one, @LuaNotNil FiguraVec6 other) {
        return other.scaled(-1).offset(one);
    }

    @LuaWhitelist
    public static FiguraVec6 __mul(@LuaNotNil FiguraVec6 one, @LuaNotNil FiguraVec6 other) {
        return one.times(other);
    }

    @LuaWhitelist
    public static FiguraVec6 __mul(@LuaNotNil FiguraVec6 one, double other) {
        return one.scaled(other);
    }

    @LuaWhitelist
    public static FiguraVec6 __mul(@LuaNotNil Double one, @LuaNotNil FiguraVec6 other) {
        return other.scaled(one);
    }

    @LuaWhitelist
    public FiguraVec6 __div(@LuaNotNil FiguraVec6 other) {
        return dividedBy(other);
    }

    @LuaWhitelist
    public FiguraVec6 __div(double other) {
        if(other == 0)
            throw new LuaError(new ArithmeticException("Division by zero"));
        return scaled(1 / other);
    }

    @LuaWhitelist
    public FiguraVec6 __mod(@LuaNotNil FiguraVec6 other) {
        return mod(other);
    }

    @LuaWhitelist
    public FiguraVec6 __mod(double other) {
        if (other == 0)
            throw new LuaError("Attempt to reduce vector by 0");
        return mod(oneUse(other, other, other, other, other, other));
    }

    @LuaWhitelist
    public boolean __eq(FiguraVec6 other) {
        return equals(other);
    }

    @LuaWhitelist
    public FiguraVec6 __unm() {
        return scaled(-1);
    }

    @LuaWhitelist
    public int __len() {
        return size();
    }

    @LuaWhitelist
    public boolean __lt(@LuaNotNil FiguraVec6 r) {
        return x < r.x && y < r.y && z < r.z && w < r.w && t < r.t && h < r.h;
    }

    @LuaWhitelist
    public boolean __le(@LuaNotNil FiguraVec6 r) {
        return x <= r.x && y <= r.y && z <= r.z && w <= r.w && t <= r.t && h <= r.h;
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
            case '5', 't' -> t;
            case '6', 'h' -> h;
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
            case '5', 't' -> t = value;
            case '6', 'h' -> h = value;
            case '_' -> {
            }
            default -> throw new LuaError("Invalid swizzle component: " + symbol);
        }
    }
}
