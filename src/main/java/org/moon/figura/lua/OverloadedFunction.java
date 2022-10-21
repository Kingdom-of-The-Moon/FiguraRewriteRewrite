package org.moon.figura.lua;

import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

public class OverloadedFunction extends VarArgFunction {
    private final List<Method> methods;
    private final LuaTypeManager typeManager;

    public OverloadedFunction(List<Method> methods, LuaTypeManager typeManager) {
        this.methods = methods;
        this.typeManager = typeManager;
    }

    @Override
    public Varargs onInvoke(Varargs args) {
        for (Method m:
                methods) {
            try {
                boolean isStatic = Modifier.isStatic(m.getModifiers());
                Object parent = isStatic ? null : args.arg(1);
                Varargs fArgs = isStatic ? args : args.subargs(2);
                Object[] overloadArgs = typeManager.matchOverload(m, fArgs);
                return typeManager.javaToLua(m.invoke(parent, overloadArgs));
            }
            catch (LuaToJavaConversionError | MatchOverloadFailed e) {
                e.printStackTrace();
            }
            catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                break;
            }
        }
        throw new RuntimeException("No suitable overload found.");
    }
}
