package org.moon.figura.math.vector;

import org.luaj.vm2.LuaDouble;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaFunction;
import org.moon.figura.lua.LuaNotNil;
import org.moon.figura.lua.LuaWhitelist;
import org.moon.figura.lua.docs.LuaTypeDoc;
import org.moon.figura.math.matrix.FiguraMatrix;
import org.moon.figura.utils.caching.CacheUtils;

@LuaWhitelist
@LuaTypeDoc(
        name = "Vector5",
        value = "vector5"
)
public class FiguraVec5 extends FiguraVector<FiguraVec5, FiguraMatrix.DummyMatrix<FiguraVec5>> {

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

    // -- cache -- //

    private final static CacheUtils.Cache<FiguraVec5> CACHE = CacheUtils.getCache(FiguraVec5::new, 300);

    private final static CacheUtils.Cache<FiguraVec5> IMMEDIATE = CacheUtils.getIndifferent(FiguraVec5::new, 15);

    @Override
    @LuaWhitelist
    public FiguraVec5 reset() {
        x = y = z = w = t = 0;
        return this;
    }

    public static FiguraVec5 oneUse(){
        return IMMEDIATE.getFresh();
    }

    public static FiguraVec5 oneUse(double x, double y, double z, double w, double t){
        return IMMEDIATE.getFresh().set(x, y, z, w, t);
    }

    @Override
    public void free() {
        CACHE.offerOld(this);
    }

    public static FiguraVec5 of() {
        return CACHE.getFresh();
    }

    public static FiguraVec5 of(double x, double y, double z, double w, double t) {
        return CACHE.getFresh().set(x, y, z, w, t);
    }

    // -- basic math -- //

    @Override
    @LuaWhitelist
    public FiguraVec5 set(@LuaNotNil FiguraVec5 other) {
        return set(other.x, other.y, other.z, other.w, other.t);
    }

    @LuaWhitelist
    public FiguraVec5 set(double x, double y, double z, double w, double t) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        this.t = t;
        return this;
    }

    @Override
    @LuaWhitelist
    public FiguraVec5 add(@LuaNotNil FiguraVec5 other) {
        return add(other.x, other.y, other.z, other.w, other.t);
    }

    @LuaWhitelist
    public FiguraVec5 add(double x, double y, double z, double w, double t) {
        this.x += x;
        this.y += y;
        this.z += z;
        this.w += w;
        this.t += t;
        return this;
    }

    @Override
    @LuaWhitelist
    public FiguraVec5 sub(@LuaNotNil FiguraVec5 other) {
        return sub(other.x, other.y, other.z, other.w, other.t);
    }

    @LuaWhitelist
    public FiguraVec5 sub(double x, double y, double z, double w, double t) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        this.w -= w;
        this.t -= t;
        return this;
    }

    @Override
    @LuaWhitelist
    public FiguraVec5 offset(double factor) {
        this.x += factor;
        this.y += factor;
        this.z += factor;
        this.w += factor;
        this.t += factor;
        return this;
    }

    @Override
    @LuaWhitelist
    public FiguraVec5 mul(@LuaNotNil FiguraVec5 other) {
        return mul(other.x, other.y, other.z, other.w, other.t);
    }

    @LuaWhitelist
    public FiguraVec5 mul(double x, double y, double z, double w, double t) {
        this.x *= x;
        this.y *= y;
        this.z *= z;
        this.w *= w;
        this.t *= t;
        return this;
    }

    @Override
    @LuaWhitelist
    public FiguraVec5 div(@LuaNotNil FiguraVec5 other) {
        return div(other.x, other.y, other.z, other.w, other.t);
    }

    @LuaWhitelist
    public FiguraVec5 div(double x, double y, double z, double w, double t) {
        this.x /= x;
        this.y /= y;
        this.z /= z;
        this.w /= w;
        this.t /= t;
        return this;
    }

    @Override
    @LuaWhitelist
    public FiguraVec5 reduce(@LuaNotNil FiguraVec5 other) {
        return reduce(other.x, other.y, other.z, other.w, other.t);
    }

    @LuaWhitelist
    public FiguraVec5 reduce(double x, double y, double z, double w, double t) {
        this.x = ((this.x % x) + x) % x;
        this.y = ((this.y % y) + y) % y;
        this.z = ((this.z % z) + z) % z;
        this.w = ((this.w % w) + w) % w;
        this.t = ((this.t % t) + t) % t;
        return this;
    }

    @Override
    @LuaWhitelist
    public FiguraVec5 scale(double factor) {
        this.x *= factor;
        this.y *= factor;
        this.z *= factor;
        this.w *= factor;
        this.t *= factor;
        return this;
    }

    @Override
    @LuaWhitelist
    public double[] unpack() {
        return new double[]{x, y, z, w, t};
    }

    // -- utility methods -- //

    @Override
    public FiguraVec5 transform(FiguraMatrix.DummyMatrix<FiguraVec5> matrix) {
        throw new IllegalStateException("Called bad method, cannot transform a FiguraVec5");
    }

    @Override
    @LuaWhitelist
    public double lengthSquared() {
        return x * x + y * y + z * z + w * w + t * t;
    }

    @Override
    @LuaWhitelist
    public FiguraVec5 copy() {
        return of(x, y, z, w, t);
    }

    @Override
    @LuaWhitelist
    public double dot(@LuaNotNil FiguraVec5 other) {
        return x * other.x + y * other.y + z * other.z + w * other.w + t * other.t;
    }

    @Override
    @LuaWhitelist
    public FiguraVec5 normalize() {
        return super.normalize();
    }

    @Override
    @LuaWhitelist
    public FiguraVec5 normalized() {
        return super.normalized();
    }

    @Override
    @LuaWhitelist
    public FiguraVec5 clampLength(Double minLength, Double maxLength) {
        return super.clampLength(minLength, maxLength);
    }

    @Override
    @LuaWhitelist
    public FiguraVec5 clamped(Double minLength, Double maxLength) {
        return super.clamped(minLength, maxLength);
    }

    @Override
    @LuaWhitelist
    public double length() {
        return super.length();
    }

    @Override
    @LuaWhitelist
    public FiguraVec5 toRad() {
        return super.toRad();
    }

    @Override
    @LuaWhitelist
    public FiguraVec5 toDeg() {
        return super.toDeg();
    }

    @LuaWhitelist
    public FiguraVec5 floor() {
        return FiguraVec5.of(Math.floor(x), Math.floor(y), Math.floor(z), Math.floor(w), Math.floor(t));
    }

    @LuaWhitelist
    public FiguraVec5 ceil() {
        return FiguraVec5.of(Math.ceil(x), Math.ceil(y), Math.ceil(z), Math.ceil(w), Math.ceil(t));
    }

    @LuaWhitelist
    public FiguraVec5 applyFunc(@LuaNotNil LuaFunction function) {
        x = function.call(LuaDouble.valueOf(x)).todouble();
        y = function.call(LuaDouble.valueOf(y)).todouble();
        z = function.call(LuaDouble.valueOf(z)).todouble();
        w = function.call(LuaDouble.valueOf(w)).todouble();
        t = function.call(LuaDouble.valueOf(t)).todouble();
        return this;
    }

    @Override
    public int size() {
        return 5;
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

    @Override
    public double index(int i) {
        return switch (i) {
            case 0 -> x;
            case 1 -> y;
            case 2 -> z;
            case 3 -> w;
            case 4 -> t;
            default -> throw new IndexOutOfBoundsException(i);
        };
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof FiguraVec5 vec && x == vec.x && y == vec.y && z == vec.z && w == vec.w && t == vec.t;
    }

    @Override
    @LuaWhitelist
    public String toString() {
        return getString(x, y, z, w, t);
    }

    // -- metamethods -- //

    @LuaWhitelist
    public static FiguraVec5 __add(FiguraVec5 one, FiguraVec5 other) {
        return one.plus(other);
    }

    @LuaWhitelist
    public static FiguraVec5 __add(FiguraVec5 one, Double other) {
        return one.offseted(other);
    }

    @LuaWhitelist
    public static FiguraVec5 __add(Double one, FiguraVec5 other) {
        return other.offseted(one);
    }

    @LuaWhitelist
    public static FiguraVec5 __sub(FiguraVec5 one, FiguraVec5 other) {
        return one.minus(other);
    }

    @LuaWhitelist
    public static FiguraVec5 __sub(FiguraVec5 one, Double other) {
        return one.offseted(-other);
    }

    @LuaWhitelist
    public static FiguraVec5 __sub(Double one, FiguraVec5 other) {
        return other.scaled(-1).offset(one);
    }

    @LuaWhitelist
    public static FiguraVec5 __mul(FiguraVec5 one, FiguraVec5 other) {
        return one.times(other);
    }

    @LuaWhitelist
    public static FiguraVec5 __mul(FiguraVec5 one, Double other) {
        return one.scaled(other);
    }

    @LuaWhitelist
    public static FiguraVec5 __mul(Double one, FiguraVec5 other) {
        return other.scaled(one);
    }

    @LuaWhitelist
    public FiguraVec5 __div(FiguraVec5 other) {
        return dividedBy(other);
    }

    @LuaWhitelist
    public FiguraVec5 __div(Double other) {
        if(other == 0)
            throw new LuaError(new ArithmeticException("Division by zero"));
        return scaled(1 / other);
    }

    @LuaWhitelist
    public FiguraVec5 __mod(FiguraVec5 other) {
        return mod(other);
    }

    @LuaWhitelist
    public FiguraVec5 __mod(Double other) {
        if (other == 0)
            throw new LuaError("Attempt to reduce vector by 0");
        return mod(oneUse(other, other, other, other, other));
    }

    @LuaWhitelist
    public boolean __eq(FiguraVec5 other) {
        return equals(other);
    }

    @LuaWhitelist
    public FiguraVec5 __unm() {
        return scaled(-1);
    }

    @LuaWhitelist
    public int __len() {
        return size();
    }

    @LuaWhitelist
    public boolean __lt(FiguraVec5 r) {
        return x < r.x && y < r.y && z < r.z && w < r.w && t < r.t;
    }

    @LuaWhitelist
    public boolean __le(FiguraVec5 r) {
        return x <= r.x && y <= r.y && z <= r.z && w <= r.w && t <= r.t;
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
            case '_' -> {
            }
            default -> throw new LuaError("Invalid swizzle component: " + symbol);
        }
    }
}
