package org.moon.figura.math.vector;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;
import org.moon.figura.lua.LuaNotNil;
import org.moon.figura.lua.LuaWhitelist;
import org.moon.figura.lua.docs.LuaTypeDoc;
import org.moon.figura.math.matrix.FiguraMat2;
import org.moon.figura.utils.caching.CacheUtils;

@LuaWhitelist
@LuaTypeDoc(
        name = "Vector2",
        value = "vector2"
)
public class FiguraVec2 extends FiguraVector<FiguraVec2, FiguraMat2> {

    @LuaWhitelist
    public double x;
    @LuaWhitelist
    public double y;

    // -- cache -- //

    private final static CacheUtils.Cache<FiguraVec2> CACHE = CacheUtils.getCache(FiguraVec2::new, 300);

    private final static CacheUtils.Cache<FiguraVec2> IMMEDIATE = CacheUtils.getIndifferent(FiguraVec2::new, 15);

    @Override
    @LuaWhitelist
    public FiguraVec2 reset() {
        x = y = 0;
        return this;
    }

    public static FiguraVec2 oneUse(){
        return IMMEDIATE.getFresh();
    }

    public static FiguraVec2 oneUse(double x, double y){
        return IMMEDIATE.getFresh().set(x, y);
    }

    @Override
    public void free() {
        CACHE.offerOld(this);
    }

    public static FiguraVec2 of() {
        return CACHE.getFresh();
    }

    public static FiguraVec2 of(double x, double y) {
        return CACHE.getFresh().set(x, y);
    }

    // -- basic math -- //

    @Override
    @LuaWhitelist
    public FiguraVec2 set(@LuaNotNil FiguraVec2 other) {
        return set(other.x, other.y);
    }

    @LuaWhitelist
    public FiguraVec2 set(double x, double y) {
        this.x = x;
        this.y = y;
        return this;
    }

    @Override
    @LuaWhitelist
    public FiguraVec2 add(@LuaNotNil FiguraVec2 other) {
        return add(other.x, other.y);
    }

    @LuaWhitelist
    public FiguraVec2 add(double x, double y) {
        this.x += x;
        this.y += y;
        return this;
    }

    @Override
    @LuaWhitelist
    public FiguraVec2 sub(@LuaNotNil FiguraVec2 other) {
        return sub(other.x, other.y);
    }

    @LuaWhitelist
    public FiguraVec2 sub(double x, double y) {
        this.x -= x;
        this.y -= y;
        return this;
    }

    @Override
    @LuaWhitelist
    public FiguraVec2 offset(@LuaNotNil double factor) {
        this.x += factor;
        this.y += factor;
        return this;
    }

    @Override
    @LuaWhitelist
    public FiguraVec2 mul(@LuaNotNil FiguraVec2 other) {
        return mul(other.x, other.y);
    }

    @LuaWhitelist
    public FiguraVec2 mul(double x, double y) {
        this.x *= x;
        this.y *= y;
        return this;
    }

    @Override
    @LuaWhitelist
    public FiguraVec2 div(@LuaNotNil FiguraVec2 other) {
        return div(other.x, other.y);
    }

    @LuaWhitelist
    public FiguraVec2 div(double x, double y) {
        this.x /= x;
        this.y /= y;
        return this;
    }

    @Override
    @LuaWhitelist
    public FiguraVec2 reduce(@LuaNotNil FiguraVec2 other) {
        return reduce(other.x, other.y);
    }

    @LuaWhitelist
    public FiguraVec2 reduce(double x, double y) {
        this.x = ((this.x % x) + x) % x;
        this.y = ((this.y % y) + y) % y;
        return this;
    }

    @Override
    @LuaWhitelist
    public FiguraVec2 scale(@LuaNotNil double factor) {
        this.x *= factor;
        this.y *= factor;
        return this;
    }

    @Override
    @LuaWhitelist
    public double[] unpack() {
        return new double[]{x, y};
    }

    // -- utility methods -- //

    @Override
    @LuaWhitelist
    public FiguraVec2 transform(@LuaNotNil FiguraMat2 mat) {
        return set(
                mat.v11 * x + mat.v12 * y,
                mat.v21 * x + mat.v22 * y
        );
    }

    @Override
    @LuaWhitelist
    public double lengthSquared() {
        return x * x + y * y;
    }

    @Override
    @LuaWhitelist
    public FiguraVec2 copy() {
        return of(x, y);
    }

    @Override
    @LuaWhitelist
    public double dot(@LuaNotNil FiguraVec2 other) {
        return x * other.x + y * other.y;
    }

    @Override
    @LuaWhitelist
    public FiguraVec2 normalize() {
        return super.normalize();
    }

    @Override
    @LuaWhitelist
    public FiguraVec2 normalized() {
        return super.normalized();
    }

    @Override
    @LuaWhitelist
    public FiguraVec2 clampLength(Double minLength, Double maxLength) {
        return super.clampLength(minLength, maxLength);
    }

    @Override
    @LuaWhitelist
    public FiguraVec2 clamped(Double minLength, Double maxLength) {
        return super.clamped(minLength, maxLength);
    }

    @Override
    @LuaWhitelist
    public double length() {
        return super.length();
    }

    @Override
    @LuaWhitelist
    public FiguraVec2 toRad() {
        return super.toRad();
    }

    @Override
    @LuaWhitelist
    public FiguraVec2 toDeg() {
        return super.toDeg();
    }

    @LuaWhitelist
    public FiguraVec2 floor() {
        return FiguraVec2.of(Math.floor(x), Math.floor(y));
    }

    @LuaWhitelist
    public FiguraVec2 ceil() {
        return FiguraVec2.of(Math.ceil(x), Math.ceil(y));
    }

    @LuaWhitelist
    public FiguraVec2 applyFunc(@LuaNotNil LuaFunction function) {
        x = function.call(LuaValue.valueOf(x), LuaValue.valueOf(1)).todouble();
        y = function.call(LuaValue.valueOf(y), LuaValue.valueOf(2)).todouble();
        return this;
    }

    @Override
    public int size() {
        return 2;
    }

    public double x() {
        return x;
    }
    public double y() {
        return y;
    }

    @Override
    public double index(int i) {
        return switch (i) {
            case 0 -> x;
            case 1 -> y;
            default -> throw new IndexOutOfBoundsException(i);
        };
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof FiguraVec2 vec && x == vec.x && y == vec.y;
    }

    // -- metamethods -- //

    @LuaWhitelist
    public static FiguraVec2 __add(@LuaNotNil FiguraVec2 one, @LuaNotNil FiguraVec2 other) {
        return one.plus(other);
    }

    @LuaWhitelist
    public static FiguraVec2 __add(@LuaNotNil FiguraVec2 one, @LuaNotNil Double other) {
        return one.offseted(other);
    }

    @LuaWhitelist
    public static FiguraVec2 __add(@LuaNotNil Double one, @LuaNotNil FiguraVec2 other) {
        return other.offseted(one);
    }

    @LuaWhitelist
    public static FiguraVec2 __sub(@LuaNotNil FiguraVec2 one, @LuaNotNil FiguraVec2 other) {
        return one.minus(other);
    }

    @LuaWhitelist
    public static FiguraVec2 __sub(@LuaNotNil FiguraVec2 one, @LuaNotNil Double other) {
        return one.offseted(-other);
    }

    @LuaWhitelist
    public static FiguraVec2 __sub(@LuaNotNil Double one, @LuaNotNil FiguraVec2 other) {
        return other.scaled(-1).offset(one);
    }

    @LuaWhitelist
    public static FiguraVec2 __mul(@LuaNotNil FiguraVec2 one, @LuaNotNil FiguraVec2 other) {
        return one.times(other);
    }

    @LuaWhitelist
    public static FiguraVec2 __mul(@LuaNotNil FiguraVec2 one, @LuaNotNil Double other) {
        return one.scaled(other);
    }

    @LuaWhitelist
    public static FiguraVec2 __mul(@LuaNotNil Double one, @LuaNotNil FiguraVec2 other) {
        return other.scaled(one);
    }

    @LuaWhitelist
    public FiguraVec2 __div(@LuaNotNil FiguraVec2 other) {
        return dividedBy(other);
    }

    @LuaWhitelist
    public FiguraVec2 __div(@LuaNotNil Double other) {
        if(other == 0)
            throw new LuaError(new ArithmeticException("Division by zero"));
        return scaled(1 / other);
    }

    @LuaWhitelist
    public FiguraVec2 __mod(@LuaNotNil FiguraVec2 other) {
        return mod(other);
    }

    @LuaWhitelist
    public FiguraVec2 __mod(@LuaNotNil Double other) {
        if (other == 0)
            throw new LuaError("Attempt to reduce vector by 0");
        return mod(oneUse(other, other));
    }

    @LuaWhitelist
    public boolean __eq(FiguraVec2 other) {
        return equals(other);
    }

    @LuaWhitelist
    public FiguraVec2 __unm() {
        return scaled(-1);
    }

    @LuaWhitelist
    public int __len() {
        return size();
    }

    @LuaWhitelist
    public boolean __lt(@LuaNotNil FiguraVec2 r) {
        return x < r.x && y < r.y;
    }

    @LuaWhitelist
    public boolean __le(@LuaNotNil FiguraVec2 r) {
        return x <= r.x && y <= r.y;
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
            case '_' -> 0d;
            default -> null;
        };
    }

    @Override
    protected void setSwizzleComponent(char symbol, double value) {
        switch (symbol) {
            case '1', 'x', 'r' -> x = value;
            case '2', 'y', 'g' -> y = value;
            case '_' -> {
            }
            default -> throw new LuaError("Invalid swizzle component: " + symbol);
        }
    }
}
