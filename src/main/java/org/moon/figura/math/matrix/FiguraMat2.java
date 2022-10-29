package org.moon.figura.math.matrix;

import org.luaj.vm2.LuaError;
import org.moon.figura.lua.LuaNotNil;
import org.moon.figura.lua.LuaWhitelist;
import org.moon.figura.lua.docs.LuaTypeDoc;
import org.moon.figura.math.vector.FiguraVec2;
import org.moon.figura.utils.LuaUtils;
import org.moon.figura.utils.caching.CacheStack;
import org.moon.figura.utils.caching.CacheUtils;

@LuaWhitelist
@LuaTypeDoc(
        name = "Matrix2",
        value = "matrix2"
)
public class FiguraMat2 extends FiguraMatrix<FiguraMat2, FiguraVec2> {

    //Values are named as v(ROW)(COLUMN), both 1-indexed like in actual math
    public double v11, v12, v21, v22;

    @Override
    public CacheUtils.Cache<FiguraMat2> getCache() {
        return CACHE;
    }
    private static final CacheUtils.Cache<FiguraMat2> CACHE = CacheUtils.getCache(FiguraMat2::new, 250);
    public static FiguraMat2 of() {
        return CACHE.getFresh();
    }
    public static FiguraMat2 of(double n11, double n21,
                                double n12, double n22) {
        return of().set(n11, n21, n12, n22);
    }
    public static class Stack extends CacheStack<FiguraMat2, FiguraMat2> {
        public Stack() {
            this(CACHE);
        }
        public Stack(CacheUtils.Cache<FiguraMat2> cache) {
            super(cache);
        }
        @Override
        protected void modify(FiguraMat2 valueToModify, FiguraMat2 modifierArg) {
            valueToModify.rightMultiply(modifierArg);
        }
        @Override
        protected void copy(FiguraMat2 from, FiguraMat2 to) {
            to.set(from);
        }
    }

    @Override
    public void resetIdentity() {
        v12 = v21 = 0;
        v11 = v22 = 1;
    }

    @Override
    protected double calculateDeterminant() {
        return v11 * v22 - v12 * v21;
    }

    @Override
    @LuaWhitelist
    public FiguraMat2 copy() {
        return of(v11, v21, v12, v22);
    }

    @Override
    public boolean equals(FiguraMat2 o) {
        return
                v11 == o.v11 && v12 == o.v12 &&
                v21 == o.v21 && v22 == o.v22;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof FiguraMat2 o)
            return equals(o);
        return false;
    }

    @Override
    public String toString() {
        return getString(v11, v12, v21, v22);
    }

    @Override
    @LuaWhitelist
    public FiguraVec2 getColumn(int col) {
        return switch (col) {
            case 1 -> FiguraVec2.of(v11, v21);
            case 2 -> FiguraVec2.of(v12, v22);
            default -> throw new LuaError("Column must be 1 to " + cols());
        };
    }

    @Override
    @LuaWhitelist
    public FiguraVec2 getRow(int row) {
        return switch (row) {
            case 1 -> FiguraVec2.of(v11, v12);
            case 2 -> FiguraVec2.of(v21, v22);
            default -> throw new LuaError("Row must be 1 to " + rows());
        };
    }

    @Override
    public int rows() {
        return 2;
    }

    @Override
    public int cols() {
        return 2;
    }

    @Override
    @LuaWhitelist
    public FiguraMat2 set(@LuaNotNil FiguraMat2 o) {
        return set(o.v11, o.v21, o.v12, o.v22);
    }

    public FiguraMat2 set(double n11, double n21,
                          double n12, double n22) {
        v11 = n11;
        v12 = n12;
        v21 = n21;
        v22 = n22;
        invalidate();
        return this;
    }

    @Override
    @LuaWhitelist
    public FiguraMat2 multiply(@LuaNotNil FiguraMat2 o) {
        double nv11 = o.v11 * v11 + o.v12 * v21;
        double nv12 = o.v11 * v12 + o.v12 * v22;

        double nv21 = o.v21 * v11 + o.v22 * v21;
        double nv22 = o.v21 * v12 + o.v22 * v22;

        v11 = nv11;
        v12 = nv12;
        v21 = nv21;
        v22 = nv22;
        invalidate();
        return this;
    }

    @Override
    @LuaWhitelist
    public FiguraMat2 rightMultiply(@LuaNotNil FiguraMat2 o) {
        double nv11 = v11 * o.v11 + v12 * o.v21;
        double nv12 = v11 * o.v12 + v12 * o.v22;

        double nv21 = v21 * o.v11 + v22 * o.v21;
        double nv22 = v21 * o.v12 + v22 * o.v22;

        v11 = nv11;
        v12 = nv12;
        v21 = nv21;
        v22 = nv22;
        invalidate();
        return this;
    }

    @Override
    @LuaWhitelist
    public FiguraMat2 transpose() {
        double temp;
        temp = v12; v12 = v21; v21 = temp;
        cachedInverse = null; //transposing doesn't invalidate the determinant
        return this;
    }

    @Override
    @LuaWhitelist
    public FiguraMat2 transposed() {
        return super.transposed();
    }

    @Override
    @LuaWhitelist
    public FiguraMat2 invert() {
        double det = det();
        return set(
                v22 / det,
                v12 / det,
                v21 / det,
                v11 / det
        );
    }

    @Override
    @LuaWhitelist
    public FiguraMat2 inverted() {
        return super.inverted();
    }

    @Override
    @LuaWhitelist
    public double det() {
        return super.det();
    }

    @Override
    @LuaWhitelist
    public FiguraMat2 reset() {
        return super.reset();
    }

    @Override
    @LuaWhitelist
    public FiguraMat2 add(@LuaNotNil FiguraMat2 o) {
        v11 += o.v11;
        v12 += o.v12;
        v21 += o.v21;
        v22 += o.v22;
        invalidate();
        return this;
    }

    @Override
    @LuaWhitelist
    public FiguraMat2 sub(@LuaNotNil FiguraMat2 o) {
        v11 -= o.v11;
        v12 -= o.v12;
        v21 -= o.v21;
        v22 -= o.v22;
        invalidate();
        return this;
    }

    @LuaWhitelist
    public FiguraMat2 scale(double x, double y) {
        v11 *= x;
        v12 *= x;
        v21 *= y;
        v22 *= y;
        invalidate();
        return this;
    }

    @LuaWhitelist
    public FiguraMat2 scale(@LuaNotNil FiguraVec2 vec) {
        return scale(vec.x, vec.y);
    }


    @LuaWhitelist
    public FiguraMat2 rotate(Double degrees) {
        degrees = Math.toRadians(degrees);
        double c = Math.cos(degrees);
        double s = Math.sin(degrees);

        double nv11 = c * v11 - s * v21;
        double nv12 = c * v12 - s * v22;

        v21 = c * v21 + s * v11;
        v22 = c * v22 + s * v12;

        v11 = nv11;
        v12 = nv12;
        return this;
    }

    @LuaWhitelist
    public FiguraMat3 augmented() {
        FiguraMat3 result = FiguraMat3.of();
        result.set(v11, v21, 0, v12, v22, 0, 0, 0, 1);
        return result;
    }

    public double apply(FiguraVec2 vec) {
        FiguraVec2 result = this.times(vec);
        double ret = result.x;
        result.free();
        return ret;
    }

    @LuaWhitelist
    public double apply(double x) {
        return apply(FiguraVec2.oneUse(x, 1));
    }

    @LuaWhitelist
    public double applyDir(double x) {
        return apply(FiguraVec2.oneUse(x, 0));
    }

    //-----------------------------METAMETHODS-----------------------------------//

    @LuaWhitelist
    public FiguraMat2 __add(@LuaNotNil FiguraMat2 mat) {
        return this.plus(mat);
    }
    @LuaWhitelist
    public FiguraMat2 __sub(@LuaNotNil FiguraMat2 mat) {
        return this.minus(mat);
    }
    @LuaWhitelist
    public Object __mul(@LuaNotNil Object o) {
        if (o instanceof FiguraMat2 mat)
            return mat.times(this);
        else if (o instanceof FiguraVec2 vec)
            return this.times(vec);
        else if (o instanceof Number n)
            return this.copy().scale(n.doubleValue(), n.doubleValue());

        throw new LuaError("Invalid types to Matrix2 __mul: " + o.getClass().getSimpleName());
    }
    @LuaWhitelist
    public boolean __eq(Object o) {
        return this.equals(o);
    }
    @LuaWhitelist
    public int __len() {
        return 2;
    }
    @LuaWhitelist
    public String __tostring() {
        return this.toString();
    }
    @LuaWhitelist
    public Object __index(String string) {
        if (string == null)
            return null;
        return switch (string) {
            case "1", "c1" -> this.getColumn(1);
            case "2", "c2" -> this.getColumn(2);

            case "r1" -> this.getRow(1);
            case "r2" -> this.getRow(2);

            case "v11" -> this.v11;
            case "v12" -> this.v12;
            case "v21" -> this.v21;
            case "v22" -> this.v22;
            default -> null;
        };
    }

    @LuaWhitelist
    public void __newindex(String string, Object value) {
        if (string == null) return;
        if (value instanceof FiguraVec2 vec2) {
            switch (string) {
                case "1", "c1" -> {
                    v11 = vec2.x; v21 = vec2.y;
                }
                case "2", "c2" -> {
                    v12 = vec2.x; v22 = vec2.y;
                }
                case "r1" -> {
                    v11 = vec2.x; v12 = vec2.y;
                }
                case "r2" -> {
                    v21 = vec2.x; v22 = vec2.y;
                }
            }
            return;
        }
        if (value instanceof Number num) {
            switch (string) {
                case "v11" -> this.v11 = num.doubleValue();
                case "v12" -> this.v12 = num.doubleValue();
                case "v21" -> this.v21 = num.doubleValue();
                case "v22" -> this.v22 = num.doubleValue();
            }
            return;
        }
        throw new LuaError("Illegal arguments to Matrix2 __newindex: " + string + ", " + value);
    }
}
