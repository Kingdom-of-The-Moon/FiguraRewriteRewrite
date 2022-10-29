package org.moon.figura.math.vector;

import org.luaj.vm2.LuaError;
import org.moon.figura.lua.FiguraLuaPrinter;
import org.moon.figura.lua.LuaNotNil;
import org.moon.figura.lua.LuaWhitelist;
import org.moon.figura.math.matrix.FiguraMatrix;
import org.moon.figura.utils.MathUtils;
import org.moon.figura.utils.caching.CachedType;

@LuaWhitelist
public abstract class FiguraVector<T extends FiguraVector<T, M>, M extends FiguraMatrix<M, T>> implements CachedType<T> {

    public abstract double lengthSquared();
    public double length() {
        return Math.sqrt(lengthSquared());
    }

    public abstract T copy();
    public abstract double dot(T other);

    public abstract T set(T other);
    public abstract T add(T other);
    public abstract T sub(T other);
    public abstract T offset(double factor);
    public abstract T mul(T other);
    public abstract T transform(M mat);
    public abstract T div(T other);
    public abstract T reduce(T other);
    public abstract T scale(double factor);
    public abstract double[] unpack();

    @SuppressWarnings("unchecked")
    public T normalize() {
        double len = length();
        if (len > 0)
            scale(1 / len);
        return (T) this;
    }
    @SuppressWarnings("unchecked")
    public T clampLength(Double minLength, Double maxLength) {
        if (minLength == null) minLength = 0d;
        if (maxLength == null) maxLength = Double.POSITIVE_INFINITY;
        double len = length();
        if (len == 0)
            return (T) this;
        if (len < minLength) {
            scale(minLength / len);
        } else if (len > maxLength) {
            scale(maxLength / len);
        }
        return (T) this;
    }

    public T clamped(Double minLength, Double maxLength) {
        return copy().clampLength(minLength, maxLength);
    }

    public T plus(T other) {
        return copy().add(other);
    }

    public T minus(T other) {
        return copy().sub(other);
    }

    public T offseted(double factor) {
        return copy().offset(factor);
    }

    public T times(T other) {
        return copy().mul(other);
    }

    public T dividedBy(T other) {
        return copy().div(other);
    }

    public T mod(T other) {
        return copy().reduce(other);
    }

    public T scaled(double factor) {
        return copy().scale(factor);
    }

    public T normalized() {
        return copy().normalize();
    }

    public T toRad() {
        return copy().scale(Math.PI/180);
    }

    public T toDeg() {
        return copy().scale(180/Math.PI);
    }

    public abstract int size();
    public abstract double index(int i);
    public abstract boolean equals(Object other);

    //Return 0 by default.
    public double x() {
        return 0;
    }
    public double y() {
        return 0;
    }
    public double z() {
        return 0;
    }
    public double w() {
        return 0;
    }
    public double t() {
        return 0;
    }
    public double h() {
        return 0;
    }

    public abstract String toString();

    protected static String getString(Double... d) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");

        for (int i = 0; i < d.length; i++) {
            sb.append(FiguraLuaPrinter.df.format(d[i]));
            if (i < d.length - 1)
                sb.append(", ");
        }

        return sb.append("}").toString();
    }


    @LuaWhitelist
    public Double __index(int i){
        return getSwizzleComponent(Integer.toString(i).charAt(0));
    }

    @LuaWhitelist
    public Object __index(@LuaNotNil String arg) {
        int len = arg.length();
        if (len == 1) return getSwizzleComponent(arg.charAt(0));

        if (len < 2 || len > 6)
            return null;
        double[] values = new double[len];
        boolean fail = false;
        for (int i = 0; i < len; i++) {
            Double d = getSwizzleComponent(arg.charAt(i));
            if(d == null){
                fail = true;
                break;
            }
            values[i] = d;
        }
        return fail ? null : MathUtils.sizedVector(values);
    }

    @LuaWhitelist
    public void __newindex(int i, double v){
        setSwizzleComponent(Integer.toString(i).charAt(0), v);
    }

    @LuaWhitelist
    public void __newindex(@LuaNotNil String i, double v){
        if(i.length() != 1){
            throw new LuaError("Invalid call to __newindex - can only set one axis to a number");
        }
        setSwizzleComponent(i.charAt(0), v);
    }

    @LuaWhitelist
    public void __newindex(@LuaNotNil String swizzle, @LuaNotNil FiguraVector<?, ?> vector){
        if(swizzle.length() == vector.size()){
            T copy = copy();
            double[] vals = unpack();
            for (int i = 0; i < swizzle.length(); i++) {
                copy.setSwizzleComponent(swizzle.charAt(i), vals[i]);
            }
            set(copy);
        }
        throw new LuaError("Invalid call to __newindex - vector swizzles must be the same size.");
    }

    protected abstract Double getSwizzleComponent(char symbol);

    protected abstract void setSwizzleComponent(char symbol, double value);
}
