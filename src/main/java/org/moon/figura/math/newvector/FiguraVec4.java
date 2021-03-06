package org.moon.figura.math.newvector;

import org.luaj.vm2.LuaDouble;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaFunction;
import org.moon.figura.math.newmatrix.FiguraMat4;
import org.moon.figura.newlua.LuaType;
import org.moon.figura.newlua.LuaWhitelist;
import org.moon.figura.newlua.docs.LuaFunctionOverload;
import org.moon.figura.newlua.docs.LuaMetamethodDoc;
import org.moon.figura.newlua.docs.LuaMethodDoc;
import org.moon.figura.utils.MathUtils;
import org.moon.figura.utils.caching.CacheUtils;
import org.moon.figura.utils.caching.CachedType;

@LuaType(typeName = "vec4")
public class FiguraVec4 extends FiguraVector<FiguraVec4, FiguraMat4> {

    private final static CacheUtils.Cache<FiguraVec4> CACHE = CacheUtils.getCache(FiguraVec4::new, 400);
    public double x, y, z, w;

    public static FiguraVec4 of() {
        return CACHE.getFresh();
    }

    public static FiguraVec4 of(double x, double y, double z, double w) {
        return CACHE.getFresh().set(x, y, z, w);
    }

    @Override
    @LuaWhitelist
    @LuaMethodDoc(
            overloads = @LuaFunctionOverload,
            description = "vector_n.length_squared"
    )
    public double lengthSquared() {
        return x*x + y*y + z*z + w*w;
    }

    @Override
    @LuaWhitelist
    @LuaMethodDoc(
            overloads = @LuaFunctionOverload,
            description = "vector_n.copy"
    )
    public FiguraVec4 copy() {
        return of(x, y, z, w);
    }

    @Override
    @LuaWhitelist
    @LuaMethodDoc(
            overloads = @LuaFunctionOverload(
                    argumentTypes = FiguraVec4.class,
                    argumentNames = "vec"
            ),
            description = "vector_n.dot"
    )
    public double dot(FiguraVec4 other) {
        return x*other.x + y*other.y + z*other.z + w*other.w;
    }

    @Override
    public boolean equals(FiguraVec4 other) {
        return x == other.x && y == other.y && z == other.z && w == other.w;
    }

    @Override
    public FiguraVec4 set(FiguraVec4 other) {
        return set(other.x, other.y, other.z, other.w);
    }

    public FiguraVec4 set(double x, double y, double z, double w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        return this;
    }

    @LuaWhitelist
    @LuaMethodDoc(
            overloads = {
                    @LuaFunctionOverload(
                            argumentTypes = FiguraVec4.class,
                            argumentNames = "vec"
                    ),
                    @LuaFunctionOverload(
                            argumentTypes = {Double.class, Double.class, Double.class, Double.class},
                            argumentNames = {"x", "y", "z", "w"}
                    )
            },
            description = "vector_n.set"
    )
    public FiguraVec4 set(Object x, double y, double z, double w) {
        if (x instanceof FiguraVec4 vec)
            return set(vec);
        if (x instanceof Number n)
            return set(n.doubleValue(), y, z, w);
        throw new LuaError("Illegal type to set(): " + x.getClass().getSimpleName());
    }

    @Override
    public FiguraVec4 add(FiguraVec4 other) {
        return add(other.x, other.y, other.z, other.w);
    }

    public FiguraVec4 add(double x, double y, double z, double w) {
        this.x += x;
        this.y += y;
        this.z += z;
        this.w += w;
        return this;
    }

    @LuaWhitelist
    @LuaMethodDoc(
            overloads = {
                    @LuaFunctionOverload(
                            argumentTypes = FiguraVec4.class,
                            argumentNames = "vec"
                    ),
                    @LuaFunctionOverload(
                            argumentTypes = {Double.class, Double.class, Double.class, Double.class},
                            argumentNames = {"x", "y", "z", "w"}
                    )
            },
            description = "vector_n.add"
    )
    public FiguraVec4 add(Object x, double y, double z, double w) {
        if (x instanceof FiguraVec4 vec)
            return add(vec);
        if (x instanceof Number n)
            return add(n.doubleValue(), y, z, w);
        throw new LuaError("Illegal type to add(): " + x.getClass().getSimpleName());
    }

    @Override
    public FiguraVec4 subtract(FiguraVec4 other) {
        return subtract(other.x, other.y, other.z, other.w);
    }

    public FiguraVec4 subtract(double x, double y, double z, double w) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        this.w -= w;
        return this;
    }

    @LuaWhitelist
    @LuaMethodDoc(
            overloads = {
                    @LuaFunctionOverload(
                            argumentTypes = FiguraVec4.class,
                            argumentNames = "vec"
                    ),
                    @LuaFunctionOverload(
                            argumentTypes = {Double.class, Double.class, Double.class, Double.class},
                            argumentNames = {"x", "y", "z", "w"}
                    )
            },
            description = "vector_n.sub"
    )
    public FiguraVec4 sub(Object x, double y, double z, double w) {
        if (x instanceof FiguraVec4 vec)
            return subtract(vec);
        if (x instanceof Number n)
            return subtract(n.doubleValue(), y, z, w);
        throw new LuaError("Illegal type to sub(): " + x.getClass().getSimpleName());
    }

    @Override
    public FiguraVec4 multiply(FiguraVec4 other) {
        return multiply(other.x, other.y, other.z, other.w);
    }

    @Override
    @LuaWhitelist
    @LuaMethodDoc(
            overloads = @LuaFunctionOverload(
                    argumentTypes = FiguraMat4.class,
                    argumentNames = "mat"
            ),
            description = "vector_n.transform"
    )
    public FiguraVec4 transform(FiguraMat4 mat) {
        return set(
                mat.v11*x+mat.v12*y+mat.v13*z+mat.v14*w,
                mat.v21*x+mat.v22*y+mat.v23*z+mat.v24*w,
                mat.v31*x+mat.v32*y+mat.v33*z+mat.v34*w,
                mat.v41*x+mat.v42*y+mat.v43*z+mat.v44*w
        );
    }

    public FiguraVec4 multiply(double x, double y, double z, double w) {
        this.x *= x;
        this.y *= y;
        this.z *= z;
        this.w *= w;
        return this;
    }

    @LuaWhitelist
    @LuaMethodDoc(
            overloads = {
                    @LuaFunctionOverload(
                            argumentTypes = FiguraVec4.class,
                            argumentNames = "vec"
                    ),
                    @LuaFunctionOverload(
                            argumentTypes = {Double.class, Double.class, Double.class, Double.class},
                            argumentNames = {"x", "y", "z", "w"}
                    )
            },
            description = "vector_n.mul"
    )
    public FiguraVec4 mul(Object x, double y, double z, double w) {
        if (x instanceof FiguraVec4 vec)
            return multiply(vec);
        if (x instanceof Number n)
            return multiply(n.doubleValue(), y, z, w);
        throw new LuaError("Illegal type to mul(): " + x.getClass().getSimpleName());
    }

    @Override
    public FiguraVec4 divide(FiguraVec4 other) {
        return divide(other.x, other.y, other.z, other.w);
    }

    public FiguraVec4 divide(double x, double y, double z, double w) {
        this.x /= x;
        this.y /= y;
        this.z /= z;
        this.w /= w;
        return this;
    }

    @LuaWhitelist
    @LuaMethodDoc(
            overloads = {
                    @LuaFunctionOverload(
                            argumentTypes = FiguraVec4.class,
                            argumentNames = "vec"
                    ),
                    @LuaFunctionOverload(
                            argumentTypes = {Double.class, Double.class, Double.class, Double.class},
                            argumentNames = {"x", "y", "z", "w"}
                    )
            },
            description = "vector_n.div"
    )
    public FiguraVec4 div(Object x, double y, double z, double w) {
        if (x instanceof FiguraVec4 vec)
            return divide(vec);
        if (x instanceof Number n)
            return divide(n.doubleValue(), y, z, w);
        throw new LuaError("Illegal type to div(): " + x.getClass().getSimpleName());
    }

    @Override
    public FiguraVec4 reduce(FiguraVec4 other) {
        return reduce(other.x, other.y, other.z, other.w);
    }

    public FiguraVec4 reduce(double x, double y, double z, double w) {
        this.x = ((this.x % x) + x) % x;
        this.y = ((this.y % y) + y) % y;
        this.z = ((this.z % z) + z) % z;
        this.w = ((this.w % w) + w) % w;
        return this;
    }

    @LuaWhitelist
    @LuaMethodDoc(
            overloads = {
                    @LuaFunctionOverload(
                            argumentTypes = FiguraVec4.class,
                            argumentNames = "vec"
                    ),
                    @LuaFunctionOverload(
                            argumentTypes = {Double.class, Double.class, Double.class, Double.class},
                            argumentNames = {"x", "y", "z", "w"}
                    )
            },
            description = "vector_n.reduce"
    )
    public FiguraVec4 reduce(Object x, double y, double z, double w) {
        if (x instanceof FiguraVec4 vec)
            return reduce(vec);
        if (x instanceof Number n)
            return reduce(n.doubleValue(), y, z, w);
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
    public FiguraVec4 scale(double factor) {
        this.x *= factor;
        this.y *= factor;
        this.z *= factor;
        this.w *= factor;
        return this;
    }

    @Override
    @LuaWhitelist
    @LuaMethodDoc(
            overloads = @LuaFunctionOverload,
            description = "vector_n.reset"
    )
    public FiguraVec4 reset() {
        x = y = z = w = 0;
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
    public FiguraVec4 clamped(Double min, Double max) {
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
    public FiguraVec4 clampLength(Double min, Double max) {
        return super.clampLength(min, max);
    }
    @LuaWhitelist
    @LuaMethodDoc(
            overloads = @LuaFunctionOverload,
            description = "vector_n.floor"
    )
    public FiguraVec4 floor() {
        return FiguraVec4.of(Math.floor(x), Math.floor(y), Math.floor(z), Math.floor(w));
    }
    @LuaWhitelist
    @LuaMethodDoc(
            overloads = @LuaFunctionOverload,
            description = "vector_n.ceil"
    )
    public FiguraVec4 ceil() {
        return FiguraVec4.of(Math.ceil(x), Math.ceil(y), Math.ceil(z), Math.ceil(w));
    }
    @LuaWhitelist
    @LuaMethodDoc(
            overloads = @LuaFunctionOverload(
                    argumentTypes = LuaFunction.class,
                    argumentNames = "func"
            ),
            description = "vector_n.apply_func"
    )
    public FiguraVec4 applyFunc(LuaFunction function) {
        x = function.call(LuaDouble.valueOf(x)).todouble();
        y = function.call(LuaDouble.valueOf(y)).todouble();
        z = function.call(LuaDouble.valueOf(z)).todouble();
        w = function.call(LuaDouble.valueOf(w)).todouble();
        return this;
    }
    @LuaWhitelist
    @LuaMethodDoc(
            overloads = @LuaFunctionOverload,
            description = "vector_n.normalized"
    )
    public FiguraVec4 normalized() {
        return super.normalized();
    }
    @LuaWhitelist
    @LuaMethodDoc(
            overloads = @LuaFunctionOverload,
            description = "vector_n.normalize"
    )
    public FiguraVec4 normalize() {
        return super.normalize();
    }
    @LuaWhitelist
    public String toString() {
        return "{" + x + "," + y + "," + z + "," + w + "}";
    }

    @LuaWhitelist
    @LuaMethodDoc(
            overloads = @LuaFunctionOverload,
            description = "vector_n.to_rad"
    )
    public FiguraVec4 toRad() {
        return super.toRad();
    }

    @LuaWhitelist
    @LuaMethodDoc(
            overloads = @LuaFunctionOverload,
            description = "vector_n.to_deg"
    )
    public FiguraVec4 toDeg() {
        return super.toDeg();
    }


    /*
    Metamethods
     */

    @LuaWhitelist
    @LuaMetamethodDoc(
            overloads = @LuaMetamethodDoc.LuaMetamethodOverload(
                    types = {FiguraVec4.class, FiguraVec4.class, FiguraVec4.class}
            )
    )
    public FiguraVec4 __add(FiguraVec4 other) {
        return plus(other);
    }
    @LuaWhitelist
    @LuaMetamethodDoc(
            overloads = @LuaMetamethodDoc.LuaMetamethodOverload(
                    types = {FiguraVec4.class, FiguraVec4.class, FiguraVec4.class}
            )
    )
    public FiguraVec4 __sub(FiguraVec4 other) {
        return minus(other);
    }
    @LuaWhitelist
    @LuaMetamethodDoc(
            overloads = {
                    @LuaMetamethodDoc.LuaMetamethodOverload(
                            types = {FiguraVec4.class, FiguraVec4.class, FiguraVec4.class}
                    ),
                    @LuaMetamethodDoc.LuaMetamethodOverload(
                            types = {FiguraVec4.class, FiguraVec4.class, Double.class}
                    ),
                    @LuaMetamethodDoc.LuaMetamethodOverload(
                            types = {FiguraVec4.class, Double.class, FiguraVec4.class}
                    )
            }
    )
    public static FiguraVec4 __mul(Object a, Object b) {
        if (a instanceof FiguraVec4 vec) {
            if (b instanceof FiguraVec4 vec2) {
                return vec.times(vec2);
            } else if (b instanceof Double d) {
                return vec.scaled(d);
            }
        } else if (a instanceof Double d) {
            return ((FiguraVec4) b).scaled(d);
        }
        throw new LuaError("Invalid types to __mul: " + a.getClass().getSimpleName() + ", " + b.getClass().getSimpleName());
    }
    @LuaWhitelist
    @LuaMetamethodDoc(
            overloads = {
                    @LuaMetamethodDoc.LuaMetamethodOverload(
                            types = {FiguraVec4.class, FiguraVec4.class, FiguraVec4.class}
                    ),
                    @LuaMetamethodDoc.LuaMetamethodOverload(
                            types = {FiguraVec4.class, FiguraVec4.class, Double.class}
                    )
            }
    )
    public FiguraVec4 __div(Object rhs) {
        if (rhs instanceof Double d) {
            if (d == 0)
                throw new LuaError("Attempt to divide vector by 0");
            return scaled(1/d);
        } else if (rhs instanceof FiguraVec4 vec) {
            return dividedBy(vec);
        }
        throw new LuaError("Invalid types to __div: " + getClass().getSimpleName() + ", " + rhs.getClass().getSimpleName());
    }

    @LuaWhitelist
    @LuaMetamethodDoc(
            overloads = {
                    @LuaMetamethodDoc.LuaMetamethodOverload(
                            types = {FiguraVec4.class, FiguraVec4.class, FiguraVec4.class}
                    ),
                    @LuaMetamethodDoc.LuaMetamethodOverload(
                            types = {FiguraVec4.class, FiguraVec4.class, Double.class}
                    )
            }
    )
    public FiguraVec4 __mod(Object rhs) {
        if (rhs instanceof Double d) {
            if (d == 0)
                throw new LuaError("Attempt to reduce vector by 0");
            FiguraVec4 modulus = of(d, d, d, d);
            FiguraVec4 result = mod(modulus);
            modulus.free();
            return result;
        } else if (rhs instanceof FiguraVec4 vec) {
            return mod(vec);
        }
        throw new LuaError("Invalid types to __mod: " + getClass().getSimpleName() + ", " + rhs.getClass().getSimpleName());
    }

    @LuaWhitelist
    @LuaMetamethodDoc(
            overloads = @LuaMetamethodDoc.LuaMetamethodOverload(
                    types = {Boolean.class, FiguraVec4.class, FiguraVec4.class}
            )
    )
    public boolean __eq(FiguraVec4 other) {
        return equals(other);
    }

    @LuaWhitelist
    @LuaMetamethodDoc(
            overloads = @LuaMetamethodDoc.LuaMetamethodOverload(
                    types = {FiguraVec4.class, FiguraVec4.class}
            )
    )
    public FiguraVec4 __unm() {
        return scaled(-1);
    }

    @LuaWhitelist
    @LuaMetamethodDoc(
            overloads = @LuaMetamethodDoc.LuaMetamethodOverload(
                    types = {Integer.class, FiguraVec4.class}
            )
    )
    public int __len() {
        return 4;
    }

    @LuaWhitelist
    @LuaMetamethodDoc(
            overloads = @LuaMetamethodDoc.LuaMetamethodOverload(
                    types = {Boolean.class, FiguraVec4.class, FiguraVec4.class}
            )
    )
    public boolean __lt(FiguraVec4 r) {
        return x < r.x && y < r.y && z < r.z && w < r.w;
    }

    @LuaWhitelist
    @LuaMetamethodDoc(
            overloads = @LuaMetamethodDoc.LuaMetamethodOverload(
                    types = {Boolean.class, FiguraVec4.class, FiguraVec4.class}
            )
    )
    public boolean __le(FiguraVec4 r) {
        return x <= r.x && y <= r.y && z <= r.z && w <= r.w;
    }

    @LuaWhitelist
    @LuaMetamethodDoc(
            overloads = @LuaMetamethodDoc.LuaMetamethodOverload(
                    types = {String.class, FiguraVec4.class}
            )
    )
    public String __tostring() {
        return toString();
    }

    @LuaWhitelist
    @LuaMetamethodDoc(
            overloads = {
                    @LuaMetamethodDoc.LuaMetamethodOverload(
                            types = {Double.class, FiguraVec4.class, Integer.class}
                    ),
                    @LuaMetamethodDoc.LuaMetamethodOverload(
                            types = {Double.class, FiguraVec4.class, String.class}
                    ),
                    @LuaMetamethodDoc.LuaMetamethodOverload(
                            types = {FiguraVector.class, FiguraVec4.class, String.class},
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
                case '_' -> 0;
                default -> {fail = true; yield 0;}
            };
        return fail ? null : MathUtils.sizedVector(vals);
    }

    @LuaWhitelist
    @LuaMetamethodDoc(
            overloads = {
                    @LuaMetamethodDoc.LuaMetamethodOverload(
                            types = {void.class, FiguraVec4.class, Integer.class, Double.class}
                    ),
                    @LuaMetamethodDoc.LuaMetamethodOverload(
                            types = {void.class, FiguraVec4.class, String.class, Double.class}
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
            }
            return;
        }
        throw new LuaError("Illegal key " + str + " to __newindex()");
    }

}
