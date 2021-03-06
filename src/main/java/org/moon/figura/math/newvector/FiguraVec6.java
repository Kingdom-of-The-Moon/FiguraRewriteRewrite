package org.moon.figura.math.newvector;

import org.luaj.vm2.LuaDouble;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaFunction;
import org.moon.figura.math.newmatrix.FiguraMatrix;
import org.moon.figura.newlua.LuaType;
import org.moon.figura.newlua.LuaWhitelist;
import org.moon.figura.newlua.docs.LuaFunctionOverload;
import org.moon.figura.newlua.docs.LuaMetamethodDoc;
import org.moon.figura.newlua.docs.LuaMethodDoc;
import org.moon.figura.utils.MathUtils;
import org.moon.figura.utils.caching.CacheUtils;

@LuaType(typeName = "vec6")
public class FiguraVec6 extends FiguraVector<FiguraVec6, FiguraMatrix.DummyMatrix<FiguraVec6>> {

    private final static CacheUtils.Cache<FiguraVec6> CACHE = CacheUtils.getCache(FiguraVec6::new, 300);
    public double x, y, z, w, t, h;

    public static FiguraVec6 of() {
        return CACHE.getFresh();
    }

    public static FiguraVec6 of(double x, double y, double z, double w, double t, double h) {
        return CACHE.getFresh().set(x, y, z, w, t, h);
    }

    @Override
    @LuaWhitelist
    @LuaMethodDoc(
            overloads = @LuaFunctionOverload,
            description = "vector_n.length_squared"
    )
    public double lengthSquared() {
        return x*x + y*y + z*z + w*w + t*t + h*h;
    }

    @Override
    @LuaWhitelist
    @LuaMethodDoc(
            overloads = @LuaFunctionOverload,
            description = "vector_n.copy"
    )
    public FiguraVec6 copy() {
        return of(x, y, z, w, t, h);
    }

    @Override
    @LuaWhitelist
    @LuaMethodDoc(
            overloads = @LuaFunctionOverload(
                    argumentTypes = FiguraVec6.class,
                    argumentNames = "vec"
            ),
            description = "vector_n.dot"
    )
    public double dot(FiguraVec6 other) {
        return x*other.x + y*other.y + z*other.z + w*other.w + t*other.t + h*other.h;
    }

    @Override
    public boolean equals(FiguraVec6 other) {
        return x == other.x && y == other.y && z == other.z && w == other.w && t == other.t && h == other.h;
    }

    @Override
    public FiguraVec6 set(FiguraVec6 other) {
        return set(other.x, other.y, other.z, other.w, other.t, other.h);
    }

    public FiguraVec6 set(double x, double y, double z, double w, double t, double h) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        this.t = t;
        this.h = h;
        return this;
    }

    @LuaWhitelist
    @LuaMethodDoc(
            overloads = {
                    @LuaFunctionOverload(
                            argumentTypes = FiguraVec6.class,
                            argumentNames = "vec"
                    ),
                    @LuaFunctionOverload(
                            argumentTypes = {Double.class, Double.class, Double.class, Double.class, Double.class, Double.class},
                            argumentNames = {"x", "y", "z", "w", "t", "h"}
                    )
            },
            description = "vector_n.set"
    )
    public FiguraVec6 set(Object x, double y, double z, double w, double t, double h) {
        if (x instanceof FiguraVec6 vec)
            return set(vec);
        if (x instanceof Number n)
            return set(n.doubleValue(), y, z, w, t, h);
        throw new LuaError("Illegal type to set(): " + x.getClass().getSimpleName());
    }

    @Override
    public FiguraVec6 add(FiguraVec6 other) {
        return add(other.x, other.y, other.z, other.w, other.t, other.h);
    }

    public FiguraVec6 add(double x, double y, double z, double w, double t, double h) {
        this.x += x;
        this.y += y;
        this.z += z;
        this.w += w;
        this.t += t;
        this.h += h;
        return this;
    }

    @LuaWhitelist
    @LuaMethodDoc(
            overloads = {
                    @LuaFunctionOverload(
                            argumentTypes = FiguraVec6.class,
                            argumentNames = "vec"
                    ),
                    @LuaFunctionOverload(
                            argumentTypes = {Double.class, Double.class, Double.class, Double.class, Double.class, Double.class},
                            argumentNames = {"x", "y", "z", "w", "t", "h"}
                    )
            },
            description = "vector_n.add"
    )
    public FiguraVec6 add(Object x, double y, double z, double w, double t, double h) {
        if (x instanceof FiguraVec6 vec)
            return add(vec);
        if (x instanceof Number n)
            return add(n.doubleValue(), y, z, w, t, h);
        throw new LuaError("Illegal type to add(): " + x.getClass().getSimpleName());
    }

    @Override
    public FiguraVec6 subtract(FiguraVec6 other) {
        return subtract(other.x, other.y, other.z, other.w, other.t, other.h);
    }

    public FiguraVec6 subtract(double x, double y, double z, double w, double t, double h) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        this.w -= w;
        this.t -= t;
        this.h -= h;
        return this;
    }

    @LuaWhitelist
    @LuaMethodDoc(
            overloads = {
                    @LuaFunctionOverload(
                            argumentTypes = FiguraVec6.class,
                            argumentNames = "vec"
                    ),
                    @LuaFunctionOverload(
                            argumentTypes = {Double.class, Double.class, Double.class, Double.class, Double.class, Double.class},
                            argumentNames = {"x", "y", "z", "w", "t", "h"}
                    )
            },
            description = "vector_n.sub"
    )
    public FiguraVec6 sub(Object x, double y, double z, double w, double t, double h) {
        if (x instanceof FiguraVec6 vec)
            return subtract(vec);
        if (x instanceof Number n)
            return subtract(n.doubleValue(), y, z, w, t, h);
        throw new LuaError("Illegal type to sub(): " + x.getClass().getSimpleName());
    }

    @Override
    public FiguraVec6 multiply(FiguraVec6 other) {
        return multiply(other.x, other.y, other.z, other.w, other.t, other.h);
    }

    @Override
    public FiguraVec6 transform(FiguraMatrix.DummyMatrix<FiguraVec6> matrix) {
        throw new IllegalStateException("Called bad method, cannot transform a FiguraVec6");
    }

    public FiguraVec6 multiply(double x, double y, double z, double w, double t, double h) {
        this.x *= x;
        this.y *= y;
        this.z *= z;
        this.w *= w;
        this.t *= t;
        this.h *= h;
        return this;
    }

    @LuaWhitelist
    @LuaMethodDoc(
            overloads = {
                    @LuaFunctionOverload(
                            argumentTypes = FiguraVec6.class,
                            argumentNames = "vec"
                    ),
                    @LuaFunctionOverload(
                            argumentTypes = {Double.class, Double.class, Double.class, Double.class, Double.class, Double.class},
                            argumentNames = {"x", "y", "z", "w", "t", "h"}
                    )
            },
            description = "vector_n.mul"
    )
    public FiguraVec6 mul(Object x, double y, double z, double w, double t, double h) {
        if (x instanceof FiguraVec6 vec)
            return multiply(vec);
        if (x instanceof Number n)
            return multiply(n.doubleValue(), y, z, w, t, h);
        throw new LuaError("Illegal type to mul(): " + x.getClass().getSimpleName());
    }

    @Override
    public FiguraVec6 divide(FiguraVec6 other) {
        return divide(other.x, other.y, other.z, other.w, other.t, other.h);
    }

    public FiguraVec6 divide(double x, double y, double z, double w, double t, double h) {
        this.x /= x;
        this.y /= y;
        this.z /= z;
        this.w /= w;
        this.t /= t;
        this.h /= h;
        return this;
    }

    @LuaWhitelist
    @LuaMethodDoc(
            overloads = {
                    @LuaFunctionOverload(
                            argumentTypes = FiguraVec6.class,
                            argumentNames = "vec"
                    ),
                    @LuaFunctionOverload(
                            argumentTypes = {Double.class, Double.class, Double.class, Double.class, Double.class, Double.class},
                            argumentNames = {"x", "y", "z", "w", "t", "h"}
                    )
            },
            description = "vector_n.div"
    )
    public FiguraVec6 div(Object x, double y, double z, double w, double t, double h) {
        if (x instanceof FiguraVec6 vec)
            return divide(vec);
        if (x instanceof Number n)
            return divide(n.doubleValue(), y, z, w, t, h);
        throw new LuaError("Illegal type to div(): " + x.getClass().getSimpleName());
    }

    @Override
    public FiguraVec6 reduce(FiguraVec6 other) {
        return reduce(other.x, other.y, other.z, other.w, other.t, other.h);
    }

    public FiguraVec6 reduce(double x, double y, double z, double w, double t, double h) {
        this.x = ((this.x % x) + x) % x;
        this.y = ((this.y % y) + y) % y;
        this.z = ((this.z % z) + z) % z;
        this.w = ((this.w % w) + w) % w;
        this.t = ((this.t % t) + t) % t;
        this.h = ((this.h % h) + h) % h;
        return this;
    }

    @LuaWhitelist
    @LuaMethodDoc(
            overloads = {
                    @LuaFunctionOverload(
                            argumentTypes = FiguraVec6.class,
                            argumentNames = "vec"
                    ),
                    @LuaFunctionOverload(
                            argumentTypes = {Double.class, Double.class, Double.class, Double.class, Double.class, Double.class},
                            argumentNames = {"x", "y", "z", "w", "t", "h"}
                    )
            },
            description = "vector_n.reduce"
    )
    public FiguraVec6 reduce(Object x, double y, double z, double w, double t, double h) {
        if (x instanceof FiguraVec6 vec)
            return reduce(vec);
        if (x instanceof Number n)
            return reduce(n.doubleValue(), y, z, w, t, h);
        throw new LuaError("Illegal type to reduce(): " + x.getClass().getSimpleName());
    }

    @Override
    @LuaWhitelist
    @LuaMethodDoc(
            overloads = @LuaFunctionOverload(
                    argumentTypes = Double.class,
                    argumentNames = "factor"
            ),
            description = "vector_n.scale"
    )
    public FiguraVec6 scale(double factor) {
        this.x *= factor;
        this.y *= factor;
        this.z *= factor;
        this.w *= factor;
        this.t *= factor;
        this.h *= factor;
        return this;
    }

    @Override
    public FiguraVec6 reset() {
        x = y = z = w = t = h = 0;
        return this;
    }

    @Override
    //DO NOT WHITELIST THIS ONE!
    public void free() {
        CACHE.offerOld(this);
    }


    /*
    Additional methods, mirroring super
     */
    @LuaWhitelist
    @LuaMethodDoc(
            overloads = @LuaFunctionOverload,
            description = "vector_n.length"
    )
    public double length() {
        return super.length();
    }
    @LuaWhitelist
    @LuaMethodDoc(
            overloads = @LuaFunctionOverload(
                    argumentTypes = {Double.class, Double.class},
                    argumentNames = {"minLength", "maxLength"}
            ),
            description = "vector_n.clamped"
    )
    public FiguraVec6 clamped(Double min, Double max) {
        return super.clamped(min, max);
    }
    @LuaWhitelist
    @LuaMethodDoc(
            overloads = @LuaFunctionOverload(
                    argumentTypes = {Double.class, Double.class},
                    argumentNames = {"minLength", "maxLength"}
            ),
            description = "vector_n.clamp_length"
    )
    public FiguraVec6 clampLength(Double min, Double max) {
        return super.clampLength(min, max);
    }
    @LuaWhitelist
    @LuaMethodDoc(
            overloads = @LuaFunctionOverload,
            description = "vector_n.floor"
    )
    public FiguraVec6 floor() {
        return FiguraVec6.of(Math.floor(x), Math.floor(y), Math.floor(z), Math.floor(w), Math.floor(t), Math.floor(h));
    }
    @LuaWhitelist
    @LuaMethodDoc(
            overloads = @LuaFunctionOverload,
            description = "vector_n.ceil"
    )
    public FiguraVec6 ceil() {
        return FiguraVec6.of(Math.ceil(x), Math.ceil(y), Math.ceil(z), Math.ceil(w), Math.ceil(t), Math.ceil(h));
    }
    @LuaWhitelist
    @LuaMethodDoc(
            overloads = @LuaFunctionOverload(
                    argumentTypes = LuaFunction.class,
                    argumentNames = "func"
            ),
            description = "vector_n.apply_func"
    )
    public FiguraVec6 applyFunc(LuaFunction function) {
        x = function.call(LuaDouble.valueOf(x)).todouble();
        y = function.call(LuaDouble.valueOf(y)).todouble();
        z = function.call(LuaDouble.valueOf(z)).todouble();
        w = function.call(LuaDouble.valueOf(w)).todouble();
        t = function.call(LuaDouble.valueOf(t)).todouble();
        h = function.call(LuaDouble.valueOf(h)).todouble();
        return this;
    }
    @LuaWhitelist
    @LuaMethodDoc(
            overloads = @LuaFunctionOverload,
            description = "vector_n.normalized"
    )
    public FiguraVec6 normalized() {
        return super.normalized();
    }
    @LuaWhitelist
    @LuaMethodDoc(
            overloads = @LuaFunctionOverload,
            description = "vector_n.normalize"
    )
    public FiguraVec6 normalize() {
        return super.normalize();
    }
    @LuaWhitelist
    @LuaMethodDoc(
            overloads = @LuaFunctionOverload(
                    argumentTypes = FiguraVec6.class,
                    argumentNames = "vec"
            ),
            description = "vector_n.reset"
    )
    public static FiguraVec6 reset(FiguraVec6 vec) { //get around method conflict, need to return this for chaining
        vec.reset();
        return vec;
    }
    @LuaWhitelist
    public String toString() {
        return "{" + x + "," + y + "," + z + "," + w + "," + t + "," + h + "}";
    }

    @LuaWhitelist
    @LuaMethodDoc(
            overloads = @LuaFunctionOverload,
            description = "vector_n.to_rad"
    )
    public FiguraVec6 toRad() {
        return super.toRad();
    }

    @LuaWhitelist
    @LuaMethodDoc(
            overloads = @LuaFunctionOverload,
            description = "vector_n.to_deg"
    )
    public FiguraVec6 toDeg() {
        return super.toDeg();
    }


    /*
    Metamethods
     */

    @LuaWhitelist
    @LuaMetamethodDoc(
            overloads = @LuaMetamethodDoc.LuaMetamethodOverload(
                    types = {FiguraVec6.class, FiguraVec6.class, FiguraVec6.class}
            )
    )
    public FiguraVec6 __add(FiguraVec6 other) {
        return plus(other);
    }
    @LuaWhitelist
    @LuaMetamethodDoc(
            overloads = @LuaMetamethodDoc.LuaMetamethodOverload(
                    types = {FiguraVec6.class, FiguraVec6.class, FiguraVec6.class}
            )
    )
    public FiguraVec6 __sub(FiguraVec6 other) {
        return minus(other);
    }
    @LuaWhitelist
    @LuaMetamethodDoc(
            overloads = {
                    @LuaMetamethodDoc.LuaMetamethodOverload(
                            types = {FiguraVec6.class, FiguraVec6.class, FiguraVec6.class}
                    ),
                    @LuaMetamethodDoc.LuaMetamethodOverload(
                            types = {FiguraVec6.class, FiguraVec6.class, Double.class}
                    ),
                    @LuaMetamethodDoc.LuaMetamethodOverload(
                            types = {FiguraVec6.class, Double.class, FiguraVec6.class}
                    )
            }
    )
    public static FiguraVec6 __mul(Object a, Object b) {
        if (a instanceof FiguraVec6 vec) {
            if (b instanceof FiguraVec6 vec2) {
                return vec.times(vec2);
            } else if (b instanceof Double d) {
                return vec.scaled(d);
            }
        } else if (a instanceof Double d) {
            return ((FiguraVec6) b).scaled(d);
        }
        throw new LuaError("Invalid types to __mul: " + a.getClass().getSimpleName() + ", " + b.getClass().getSimpleName());
    }
    @LuaWhitelist
    @LuaMetamethodDoc(
            overloads = {
                    @LuaMetamethodDoc.LuaMetamethodOverload(
                            types = {FiguraVec6.class, FiguraVec6.class, FiguraVec6.class}
                    ),
                    @LuaMetamethodDoc.LuaMetamethodOverload(
                            types = {FiguraVec6.class, FiguraVec6.class, Double.class}
                    )
            }
    )
    public FiguraVec6 __div(Object rhs) {
        if (rhs instanceof Double d) {
            if (d == 0)
                throw new LuaError("Attempt to divide vector by 0");
            return scaled(1/d);
        } else if (rhs instanceof FiguraVec6 vec) {
            return dividedBy(vec);
        }
        throw new LuaError("Invalid types to __div: " + getClass().getSimpleName() + ", " + rhs.getClass().getSimpleName());
    }

    @LuaWhitelist
    @LuaMetamethodDoc(
            overloads = {
                    @LuaMetamethodDoc.LuaMetamethodOverload(
                            types = {FiguraVec6.class, FiguraVec6.class, FiguraVec6.class}
                    ),
                    @LuaMetamethodDoc.LuaMetamethodOverload(
                            types = {FiguraVec6.class, FiguraVec6.class, Double.class}
                    )
            }
    )
    public FiguraVec6 __mod(Object rhs) {
        if (rhs instanceof Double d) {
            if (d == 0)
                throw new LuaError("Attempt to reduce vector by 0");
            FiguraVec6 modulus = of(d, d, d, d, d, d);
            FiguraVec6 result = mod(modulus);
            modulus.free();
            return result;
        } else if (rhs instanceof FiguraVec6 vec) {
            return mod(vec);
        }
        throw new LuaError("Invalid types to __mod: " + getClass().getSimpleName() + ", " + rhs.getClass().getSimpleName());
    }

    @LuaWhitelist
    @LuaMetamethodDoc(
            overloads = @LuaMetamethodDoc.LuaMetamethodOverload(
                    types = {Boolean.class, FiguraVec6.class, FiguraVec6.class}
            )
    )
    public boolean __eq(FiguraVec6 other) {
        return equals(other);
    }

    @LuaWhitelist
    @LuaMetamethodDoc(
            overloads = @LuaMetamethodDoc.LuaMetamethodOverload(
                    types = {FiguraVec6.class, FiguraVec6.class}
            )
    )
    public FiguraVec6 __unm() {
        return scaled(-1);
    }

    @LuaWhitelist
    @LuaMetamethodDoc(
            overloads = @LuaMetamethodDoc.LuaMetamethodOverload(
                    types = {Integer.class, FiguraVec6.class}
            )
    )
    public int __len() {
        return 6;
    }

    @LuaWhitelist
    @LuaMetamethodDoc(
            overloads = @LuaMetamethodDoc.LuaMetamethodOverload(
                    types = {Boolean.class, FiguraVec6.class, FiguraVec6.class}
            )
    )
    public boolean __lt(FiguraVec6 r) {
        return x < r.x && y < r.y && z < r.z && w < r.w && t < r.t && h < r.h;
    }

    @LuaWhitelist
    @LuaMetamethodDoc(
            overloads = @LuaMetamethodDoc.LuaMetamethodOverload(
                    types = {Boolean.class, FiguraVec6.class, FiguraVec6.class}
            )
    )
    public boolean __le(FiguraVec6 r) {
        return x <= r.x && y <= r.y && z <= r.z && w <= r.w && t <= r.t && h <= r.h;
    }

    @LuaWhitelist
    @LuaMetamethodDoc(
            overloads = @LuaMetamethodDoc.LuaMetamethodOverload(
                    types = {String.class, FiguraVec6.class}
            )
    )
    public String __tostring() {
        return toString();
    }

    @LuaWhitelist
    @LuaMetamethodDoc(
            overloads = {
                    @LuaMetamethodDoc.LuaMetamethodOverload(
                            types = {Double.class, FiguraVec6.class, Integer.class}
                    ),
                    @LuaMetamethodDoc.LuaMetamethodOverload(
                            types = {Double.class, FiguraVec6.class, String.class}
                    ),
                    @LuaMetamethodDoc.LuaMetamethodOverload(
                            types = {FiguraVector.class, FiguraVec6.class, String.class},
                            comment = "vector_n.comments.swizzle"
                    )
            }
    )
    public Object __index(Object arg) {
        if (arg == null)
            return null;
        String str = arg.toString();
        int len = str.length();
        if (len == 1) return switch(str) {
            case "1", "x", "r" -> x;
            case "2", "y", "g" -> y;
            case "3", "z", "b" -> z;
            case "4", "w", "a" -> w;
            case "5", "t" -> t;
            case "6", "h" -> h;
            default -> null;
        };

        if (len > 6)
            return null;
        double[] vals = new double[len];
        boolean fail = false;
        for (int i = 0; i < len; i++)
            vals[i] = switch (str.charAt(i)) {
                case '1', 'x', 'r' -> x;
                case '2', 'y', 'g' -> y;
                case '3', 'z', 'b' -> z;
                case '4', 'w', 'a' -> w;
                case '5', 't' -> t;
                case '6', 'h' -> h;
                case '_' -> 0;
                default -> {fail = true; yield 0;}
            };
        return fail ? null : MathUtils.oldSizedVector(vals);
    }

    @LuaWhitelist
    @LuaMetamethodDoc(
            overloads = {
                    @LuaMetamethodDoc.LuaMetamethodOverload(
                            types = {void.class, FiguraVec6.class, Integer.class, Double.class}
                    ),
                    @LuaMetamethodDoc.LuaMetamethodOverload(
                            types = {void.class, FiguraVec6.class, String.class, Double.class}
                    )//,
//                    @LuaMetamethodDoc.LuaMetamethodOverload(
//                            types = {void.class, FiguraVec6.class, String.class, FiguraVector.class}
//                    )
            }
    )
    public void __newindex(Object key, Object value) {
        String str = key.toString();
        int len = str.length();
        if (len == 1 && value instanceof Number n)  {
            switch(str) {
                case "1", "x", "r" -> x = n.doubleValue();
                case "2", "y", "g" -> y = n.doubleValue();
                case "3", "z", "b" -> z = n.doubleValue();
                case "4", "w", "a" -> w = n.doubleValue();
                case "5", "t" -> t = n.doubleValue();
                case "6", "h" -> h = n.doubleValue();
            }
            return;
        }
        throw new LuaError("Illegal key " + str + " to __newindex()");
    }

}
