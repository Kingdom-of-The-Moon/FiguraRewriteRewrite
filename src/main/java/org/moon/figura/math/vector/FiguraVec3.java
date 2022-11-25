package org.moon.figura.math.vector;

import com.mojang.math.Vector3f;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;
import org.moon.figura.lua.LuaNotNil;
import org.moon.figura.lua.LuaWhitelist;
import org.moon.figura.lua.docs.LuaTypeDoc;
import org.moon.figura.math.matrix.FiguraMat3;
import org.moon.figura.utils.caching.CacheUtils;

@LuaWhitelist
@LuaTypeDoc(
        name = "Vector3",
        value = "vector3"
)
public class FiguraVec3 extends FiguraVector<FiguraVec3, FiguraMat3> {

    @LuaWhitelist
    public double x;
    @LuaWhitelist
    public double y;
    @LuaWhitelist
    public double z;

    // -- cache -- //

    private final static CacheUtils.Cache<FiguraVec3> CACHE = CacheUtils.getCache(FiguraVec3::new, 300);

    private final static CacheUtils.Cache<FiguraVec3> IMMEDIATE = CacheUtils.getIndifferent(FiguraVec3::new, 15);

    @Override
    @LuaWhitelist
    public FiguraVec3 reset() {
        x = y = z = 0;
        return this;
    }

    public static FiguraVec3 oneUse(){
        return IMMEDIATE.getFresh();
    }

    public static FiguraVec3 oneUse(double x, double y, double z){
        return IMMEDIATE.getFresh().set(x, y, z);
    }

    @Override
    public void free() {
        CACHE.offerOld(this);
    }

    public static FiguraVec3 of() {
        return CACHE.getFresh();
    }

    public static FiguraVec3 of(double x, double y, double z) {
        return CACHE.getFresh().set(x, y, z);
    }

    // -- basic math -- //

    @Override
    @LuaWhitelist
    public FiguraVec3 set(@LuaNotNil FiguraVec3 other) {
        return set(other.x, other.y, other.z);
    }

    @LuaWhitelist
    public FiguraVec3 set(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    @Override
    @LuaWhitelist
    public FiguraVec3 add(@LuaNotNil FiguraVec3 other) {
        return add(other.x, other.y, other.z);
    }

    @LuaWhitelist
    public FiguraVec3 add(double x, double y, double z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    @Override
    @LuaWhitelist
    public FiguraVec3 sub(@LuaNotNil FiguraVec3 other) {
        return sub(other.x, other.y, other.z);
    }

    @LuaWhitelist
    public FiguraVec3 sub(double x, double y, double z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        return this;
    }

    @Override
    @LuaWhitelist
    public FiguraVec3 offset(@LuaNotNil double factor) {
        this.x += factor;
        this.y += factor;
        this.z += factor;
        return this;
    }

    @Override
    @LuaWhitelist
    public FiguraVec3 mul(@LuaNotNil FiguraVec3 other) {
        return mul(other.x, other.y, other.z);
    }

    @LuaWhitelist
    public FiguraVec3 mul(double x, double y, double z) {
        this.x *= x;
        this.y *= y;
        this.z *= z;
        return this;
    }

    @Override
    @LuaWhitelist
    public FiguraVec3 div(@LuaNotNil FiguraVec3 other) {
        return div(other.x, other.y, other.z);
    }

    @LuaWhitelist
    public FiguraVec3 div(double x, double y, double z) {
        this.x /= x;
        this.y /= y;
        this.z /= z;
        return this;
    }

    @Override
    @LuaWhitelist
    public FiguraVec3 reduce(@LuaNotNil FiguraVec3 other) {
        return reduce(other.x, other.y, other.z);
    }

    @LuaWhitelist
    public FiguraVec3 reduce(double x, double y, double z) {
        this.x = ((this.x % x) + x) % x;
        this.y = ((this.y % y) + y) % y;
        this.z = ((this.z % z) + z) % z;
        return this;
    }

    @Override
    @LuaWhitelist
    public FiguraVec3 scale(@LuaNotNil double factor) {
        this.x *= factor;
        this.y *= factor;
        this.z *= factor;
        return this;
    }

    @Override
    @LuaWhitelist
    public double[] unpack() {
        return new double[]{x, y, z};
    }

    // -- utility methods -- //

    @Override
    @LuaWhitelist
    public FiguraVec3 transform(@LuaNotNil FiguraMat3 mat) {
        return set(
                mat.v11 * x + mat.v12 * y + mat.v13 * z,
                mat.v21 * x + mat.v22 * y + mat.v23 * z,
                mat.v31 * x + mat.v32 * y + mat.v33 * z
        );
    }

    @Override
    @LuaWhitelist
    public double lengthSquared() {
        return x * x + y * y + z * z;
    }

    @Override
    @LuaWhitelist
    public FiguraVec3 copy() {
        return of(x, y, z);
    }

    @Override
    @LuaWhitelist
    public double dot(@LuaNotNil FiguraVec3 other) {
        return x * other.x + y * other.y + z * other.z;
    }

    @Override
    @LuaWhitelist
    public FiguraVec3 normalize() {
        return super.normalize();
    }

    @Override
    @LuaWhitelist
    public FiguraVec3 normalized() {
        return super.normalized();
    }

    @Override
    @LuaWhitelist
    public FiguraVec3 clampLength(Double minLength, Double maxLength) {
        return super.clampLength(minLength, maxLength);
    }

    @Override
    @LuaWhitelist
    public FiguraVec3 clamped(Double minLength, Double maxLength) {
        return super.clamped(minLength, maxLength);
    }

    @Override
    @LuaWhitelist
    public double length() {
        return super.length();
    }

    @Override
    @LuaWhitelist
    public FiguraVec3 toRad() {
        return super.toRad();
    }

    @Override
    @LuaWhitelist
    public FiguraVec3 toDeg() {
        return super.toDeg();
    }

    @LuaWhitelist
    public FiguraVec3 floor() {
        return FiguraVec3.of(Math.floor(x), Math.floor(y), Math.floor(z));
    }

    @LuaWhitelist
    public FiguraVec3 ceil() {
        return FiguraVec3.of(Math.ceil(x), Math.ceil(y), Math.ceil(z));
    }

    @LuaWhitelist
    public FiguraVec3 applyFunc(@LuaNotNil LuaFunction function) {
        x = function.call(LuaValue.valueOf(x), LuaValue.valueOf(1)).todouble();
        y = function.call(LuaValue.valueOf(y), LuaValue.valueOf(2)).todouble();
        z = function.call(LuaValue.valueOf(z), LuaValue.valueOf(3)).todouble();
        return this;
    }

    @Override
    public int size() {
        return 3;
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

    @Override
    public double index(int i) {
        return switch (i) {
            case 0 -> x;
            case 1 -> y;
            case 2 -> z;
            default -> throw new IndexOutOfBoundsException(i);
        };
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof FiguraVec3 vec && x == vec.x && y == vec.y && z == vec.z;
    }

    // -- vec3 specific -- //

    @LuaWhitelist
    public FiguraVec3 cross(@LuaNotNil FiguraVec3 other) {
        double nx = y * other.z - z * other.y;
        double ny = z * other.x - x * other.z;
        double nz = x * other.y - y * other.x;
        set(nx, ny, nz);
        return this;
    }

    @LuaWhitelist
    public FiguraVec3 crossed(@LuaNotNil FiguraVec3 other) {
        double nx = y * other.z - z * other.y;
        double ny = z * other.x - x * other.z;
        double nz = x * other.y - y * other.x;
        return FiguraVec3.of(nx, ny, nz);
    }

    @LuaWhitelist
    public FiguraVec4 augmented() {
        return FiguraVec4.of(x, y, z, 1);
    }

    public BlockPos asBlockPos() {
        return new BlockPos(x, y, z);
    }
    public static FiguraVec3 fromBlockPos(BlockPos pos) {
        return of(pos.getX(), pos.getY(), pos.getZ());
    }

    public Vec3 asVec3() {
        return new Vec3(x, y, z);
    }
    public static FiguraVec3 fromVec3(Vec3 vec) {
        return of(vec.x, vec.y, vec.z);
    }

    public Vector3f asVec3f() {
        return new Vector3f((float) x, (float) y, (float) z);
    }
    public static FiguraVec3 fromVec3f(Vector3f vec) {
        return of(vec.x(), vec.y(), vec.z());
    }

    // -- metamethods -- //

    @LuaWhitelist
    public static FiguraVec3 __add(@LuaNotNil FiguraVec3 one, @LuaNotNil FiguraVec3 other) {
        return one.plus(other);
    }

    @LuaWhitelist
    public static FiguraVec3 __add(@LuaNotNil FiguraVec3 one, @LuaNotNil double other) {
        return one.offseted(other);
    }

    @LuaWhitelist
    public static FiguraVec3 __add(@LuaNotNil double one, @LuaNotNil FiguraVec3 other) {
        return other.offseted(one);
    }

    @LuaWhitelist
    public static FiguraVec3 __sub(@LuaNotNil FiguraVec3 one, @LuaNotNil FiguraVec3 other) {
        return one.minus(other);
    }

    @LuaWhitelist
    public static FiguraVec3 __sub(@LuaNotNil FiguraVec3 one, double other) {
        return one.offseted(-other);
    }

    @LuaWhitelist
    public static FiguraVec3 __sub(double one, @LuaNotNil FiguraVec3 other) {
        return other.scaled(-1).offset(one);
    }

    @LuaWhitelist
    public static FiguraVec3 __mul(@LuaNotNil FiguraVec3 one, @LuaNotNil FiguraVec3 other) {
        return one.times(other);
    }

    @LuaWhitelist
    public static FiguraVec3 __mul(@LuaNotNil FiguraVec3 one, double other) {
        return one.scaled(other);
    }

    @LuaWhitelist
    public static FiguraVec3 __mul(double one, @LuaNotNil FiguraVec3 other) {
        return other.scaled(one);
    }

    @LuaWhitelist
    public FiguraVec3 __div(@LuaNotNil FiguraVec3 other) {
        return dividedBy(other);
    }

    @LuaWhitelist
    public FiguraVec3 __div(double other) {
        if(other == 0)
            throw new LuaError(new ArithmeticException("Division by zero"));
        return scaled(1 / other);
    }

    @LuaWhitelist
    public FiguraVec3 __mod(@LuaNotNil FiguraVec3 other) {
        return mod(other);
    }

    @LuaWhitelist
    public FiguraVec3 __mod(double other) {
        if (other == 0)
            throw new LuaError("Attempt to reduce vector by 0");
        return mod(oneUse(other, other, other));
    }

    @LuaWhitelist
    public boolean __eq(FiguraVec3 other) {
        return equals(other);
    }

    @LuaWhitelist
    public FiguraVec3 __unm() {
        return scaled(-1);
    }

    @LuaWhitelist
    public int __len() {
        return size();
    }

    @LuaWhitelist
    public boolean __lt(@LuaNotNil FiguraVec3 r) {
        return x < r.x && y < r.y && z < r.z;
    }

    @LuaWhitelist
    public boolean __le(@LuaNotNil FiguraVec3 r) {
        return x <= r.x && y <= r.y && z <= r.z;
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
            case '_' -> {
            }
            default -> throw new LuaError("Invalid swizzle component: " + symbol);
        }
    }
}
