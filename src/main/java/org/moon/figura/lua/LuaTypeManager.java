package org.moon.figura.lua;

import org.luaj.vm2.*;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;
import org.moon.figura.lua.docs.FiguraDocsManager;
import org.moon.figura.lua.docs.LuaTypeDoc;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.Function;

/**
 * One LuaTypeManager per LuaRuntime, so that people can be allowed to edit the metatables within.
 */
public class LuaTypeManager {

    private final Map<Class<?>, LuaTable> metatables = new HashMap<>();

    public void generateMetatableFor(Class<?> clazz) {
        if (metatables.containsKey(clazz))
            return;
        if (!clazz.isAnnotationPresent(LuaWhitelist.class))
            throw new IllegalArgumentException("Tried to generate metatable for un-whitelisted class " + clazz.getName() + "!");

        //Ensure that all whitelisted superclasses are loaded before this one

        Map<String, List<Method>> overloadedMethods = new HashMap<>();

        try {
            generateMetatableFor(clazz.getSuperclass());
        } catch (IllegalArgumentException ignored) {}

        LuaTable metatable = new LuaTable();

        LuaTable indexTable = new LuaTable();
        Class<?> currentClass = clazz;
        while (currentClass.isAnnotationPresent(LuaWhitelist.class)) {
            for (Method method : currentClass.getDeclaredMethods()) {
                if (!method.isAnnotationPresent(LuaWhitelist.class)) {
                    continue;
                }
                String name = method.getName();
                if (name.startsWith("__")) { //metamethods
                    if (metatable.rawget(name) == LuaValue.NIL) { //Only add the most recently declared metamethod, in the most specific subclass.
                        if (name.equals("__index")) {
                            //Custom __index implementation. First checks the regular __index table, and if it gets NIL, then calls the custom-defined __index function.
                            metatable.set("__index", new TwoArgFunction() {
                                final LuaFunction wrappedIndexer = getWrapper(method);
                                @Override
                                public LuaValue call(LuaValue arg1, LuaValue arg2) {
                                    LuaValue result = indexTable.get(arg2);
                                    if (result == LuaValue.NIL)
                                        result = wrappedIndexer.call(arg1, arg2);
                                    return result;
                                }
                            });
                        } else {
                            metatable.set(name, getWrapper(method));
                        }
                    }
                } else { //regular methods
                    if (!overloadedMethods.containsKey(name)) overloadedMethods.put(name, new LinkedList<>());
                    overloadedMethods.get(name).add(method);
                    //indexTable.set(name, getWrapper(method));
                }
            }
            currentClass = currentClass.getSuperclass();
        }

        for (String methodName:
             overloadedMethods.keySet()) {
            indexTable.set(methodName, new OverloadedFunction(overloadedMethods.get(methodName), this));
        }

        if (metatable.rawget("__index") == LuaValue.NIL)
            metatable.set("__index", indexTable);

        //if we don't have a special toString, then have our toString give the type name from the annotation
        if (metatable.rawget("__tostring") == LuaValue.NIL) {
            metatable.set("__tostring", new OneArgFunction() {
                private final LuaString val = LuaString.valueOf(clazz.getName());
                @Override
                public LuaValue call(LuaValue arg) {
                    return val;
                }
            });
        }

        //if we don't have a special __index, then have our indexer look in the next metatable up in the java inheritance.
        if (indexTable.rawget("__index") == LuaValue.NIL) {
            LuaTable superclassMetatable = metatables.get(clazz.getSuperclass());
            if (superclassMetatable != null) {
                LuaTable newMetatable = new LuaTable();
                newMetatable.set("__index", superclassMetatable.get("__index"));
                indexTable.setmetatable(newMetatable);
            }
        }

        metatables.put(clazz, metatable);
    }

    public void dumpMetatables(LuaTable table) {
        for (Map.Entry<Class<?>, LuaTable> entry : metatables.entrySet()) {
            if (!entry.getKey().isAnnotationPresent(LuaTypeDoc.class))
                continue;
            String name = entry.getKey().getAnnotation(LuaTypeDoc.class).name();
            if (table.get(name) != LuaValue.NIL)
                throw new IllegalStateException("Two classes have the same type name: " + name);
            table.set(name, entry.getValue());
        }
    }

    private final Map<Class<?>, String> namesCache = new HashMap<>();
    public String getTypeName(Class<?> clazz) {
        return namesCache.computeIfAbsent(clazz, someClass -> {
            if (someClass.isAnnotationPresent(LuaTypeDoc.class))
                return someClass.getAnnotation(LuaTypeDoc.class).name();
            return someClass.getSimpleName();
        });
    }

    private static boolean[] getRequiredNotNil(Method method) {
        Parameter[] params = method.getParameters();
        boolean[] result = new boolean[params.length];
        for (int i = 0; i < params.length; i++)
            if (params[i].isAnnotationPresent(LuaNotNil.class))
                result[i] = true;
        return result;
    }

    public VarArgFunction getWrapper(Method method) {
        return new VarArgFunction() {

            private final boolean isStatic = Modifier.isStatic(method.getModifiers());
            private Object caller;


            private final Class<?> clazz = method.getDeclaringClass();
            private final Class<?>[] argumentTypes = method.getParameterTypes();
            private final Object[] actualArgs = new Object[argumentTypes.length];
            private final boolean[] requiredNotNil = getRequiredNotNil(method);

            @Override
            public Varargs invoke(Varargs args) {

                if (!isStatic)
                    caller = args.checkuserdata(1, clazz);

                //Fill in actualArgs from args
                for (int i = 0; i < argumentTypes.length; i++) {
                    int argIndex = i + (isStatic ? 1 : 2);
                    boolean nil = args.isnil(argIndex);
                    if (nil && requiredNotNil[i])
                        throw new LuaError("bad argument: " + method.getName() + " " + argIndex + " do not allow nil values, expected " + FiguraDocsManager.getNameFor(argumentTypes[i]));
                    if (argIndex <= args.narg() && !nil) {
                        try {
                            actualArgs[i] = switch (argumentTypes[i].getName()) {
                                case "java.lang.Number", "java.lang.Double", "double" -> args.checkdouble(argIndex);
                                case "java.lang.String" -> args.checkjstring(argIndex);
                                case "java.lang.Boolean", "boolean" -> args.toboolean(argIndex);
                                case "java.lang.Float", "float" -> (float) args.checkdouble(argIndex);
                                case "java.lang.Integer", "int" -> args.checkint(argIndex);
                                case "java.lang.Long", "long" -> args.checklong(argIndex);
                                case "org.luaj.vm2.LuaTable" -> args.checktable(argIndex);
                                case "org.luaj.vm2.LuaFunction" -> args.checkfunction(argIndex);
                                case "org.luaj.vm2.LuaValue" -> args.arg(argIndex);
                                case "java.lang.Object" -> luaToJava(args.arg(argIndex));
                                default -> argumentTypes[i].getName().startsWith("[") ? luaVarargToJava(args, argIndex, argumentTypes[i]) : args.checkuserdata(argIndex, argumentTypes[i]);
                            };
                        } catch (LuaError err) {
                            String expectedType = FiguraDocsManager.getNameFor(argumentTypes[i]);
                            String actualType;
                            if (args.arg(argIndex).type() == LuaValue.TUSERDATA)
                                actualType = FiguraDocsManager.getNameFor(args.arg(argIndex).checkuserdata().getClass());
                            else
                                actualType = args.arg(argIndex).typename();
                            throw new LuaError("Invalid argument " + argIndex + " to function " + method.getName() + ". Expected " + expectedType + ", but got " + actualType);
                        }
                    } else {
                        actualArgs[i] = switch (argumentTypes[i].getName()) {
                            case "double" -> 0D;
                            case "int" -> 0;
                            case "long" -> 0L;
                            case "float" -> 0f;
                            case "boolean" -> false;
                            default -> null;
                        };
                    }
                }

                //Invoke the wrapped method
                Object result;
                try {
                    result = method.invoke(caller, actualArgs);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw e.getCause() instanceof LuaError l ? l : new LuaError(e.getCause());
                }

                //Convert the return value
                return result instanceof Varargs v ? v : javaToLua(result);
            }

            @Override
            public String tojstring() {
                return "function: " + method.getName();
            }
        };
    }

    private LuaValue wrap(Object instance) {
        Class<?> clazz = instance.getClass();
        LuaTable metatable = metatables.get(clazz);
        while (metatable == null) {
            clazz = clazz.getSuperclass();
            if (clazz == Object.class)
                throw new RuntimeException("Attempt to wrap illegal type " + instance.getClass().getName() + " (not registered in LuaTypeManager's \"metatables\" map)!");
            metatable = metatables.get(clazz);
        }

        LuaUserdata result = new LuaUserdata(instance);
        result.setmetatable(metatable);
        return result;
    }

    private LuaValue wrapMap(Map<?, ?> map) {
        LuaTable table = new LuaTable();

        for (Map.Entry<?, ?> entry : map.entrySet()) {
            LuaValue key = javaToLua(entry.getKey()).arg1();
            LuaValue val = javaToLua(entry.getValue()).arg1();
            table.set(key, val);
        }

        return table;
    }

    private LuaValue wrapList(List<?> list) {
        LuaTable table = new LuaTable();

        for (int i = 0; i < list.size(); i++)
            table.set(i + 1, javaToLua(list.get(i)).arg1());

        return table;
    }

    private LuaTable wrapCollection(Collection<?> collection) {
        LuaTable vals = new LuaTable();
        for (Object o:
                collection) {
            vals.add(javaToLua(o).arg1());
        }
        return vals;
    }

    private Varargs wrapArray(Object array) {
        int len = Array.getLength(array);
        LuaValue[] args = new LuaValue[len];

        for (int i = 0; i < len; i++)
            args[i] = javaToLua(Array.get(array, i)).arg1();

        return LuaValue.varargsOf(args);
    }

    public Object luaVarargToJava(Varargs args, int argIndex, Class<?> argumentType) {
        if (args.arg(argIndex).istable()) {
            return luaVarargToJava(args.checktable(argIndex).unpack(), 1, argumentType);
        } else {
            Object[] obj = new Object[args.narg() - argIndex + 1];
            for (int start = argIndex; argIndex <= args.narg(); argIndex++) {
                obj[argIndex - start] = switch (argumentType.getName()) {
                    case "[Ljava.lang.Number;", "[Ljava.lang.Double;", "[D" -> args.checkdouble(argIndex);
                    case "[Ljava.lang.String;" -> args.checkjstring(argIndex);
                    case "[Ljava.lang.Boolean;", "[B" -> args.toboolean(argIndex);
                    case "[Ljava.lang.Float;", "[F" -> (float) args.checkdouble(argIndex);
                    case "[Ljava.lang.Integer;", "[I" -> args.checkint(argIndex);
                    case "[Ljava.lang.Long;", "[J" -> args.checklong(argIndex);
                    case "[Lorg.luaj.vm2.LuaTable;" -> args.checktable(argIndex);
                    case "[Lorg.luaj.vm2.LuaFunction;" -> args.checkfunction(argIndex);
                    case "[Lorg.luaj.vm2.LuaValue;" -> args.arg(argIndex);
                    case "[Ljava.lang.Object;" -> luaToJava(args.arg(argIndex));
                    default -> args.checkuserdata(argIndex, argumentType);
                };
            }
            return Arrays.copyOf(obj, obj.length, (Class<? extends Object[]>) argumentType);
        }
    }

    //we need to allow string being numbers here
    //however in places like pings and print we should keep strings as strings
    public Object luaToJava(LuaValue val) {
        if (val.istable())
            return val.checktable();
        else if (val.isnumber())
            if (val instanceof LuaInteger i) //dumb
                return i.checkint();
            else if (val.isint() && val instanceof LuaString s) //very dumb
                return s.checkint();
            else
                return val.checkdouble();
        else if (val.isstring())
            return val.checkjstring();
        else if (val.isboolean())
            return val.checkboolean();
        else if (val.isfunction())
            return val.checkfunction();
        else if (val.isuserdata())
            return val.checkuserdata(Object.class);
        else
            return null;
    }

    public Varargs javaToLua(Object val) {
        if (val == null) return LuaValue.NIL;
        else if (val instanceof Varargs a) return a;
        else if (val.getClass().isArray()) return wrapArray(val);
        else if (val instanceof Collection<?> objs) return wrapCollection(objs);
        else if (val instanceof Map<?,?> map) return wrapMap(map);
        else if (val instanceof Double d) return LuaValue.valueOf(d);
        else if (val instanceof String s) return LuaValue.valueOf(s);
        else if (val instanceof Boolean b) return LuaValue.valueOf(b);
        else if (val instanceof Integer i) return LuaValue.valueOf(i);
        else if (val instanceof Float f) return LuaValue.valueOf(f);
        else if (val instanceof Byte b) return LuaValue.valueOf(b);
        else if (val instanceof Long l) return LuaValue.valueOf(l);
        else if (val instanceof Character c) return LuaValue.valueOf(c);
        else if (val instanceof Short s) return LuaValue.valueOf(s);
        return wrap(val);
    }

    private enum LuaType {
        STRING(l -> l.tojstring()),
        BOOLEAN(l -> l.toboolean()),
        BYTE(l -> l.tobyte()),
        SHORT(l -> l.toshort()),
        INTEGER(l -> l.toint()),
        FLOAT(l -> l.tofloat()),
        LONG(l -> l.tolong()),
        DOUBLE(l -> l.todouble());

        private final Function<LuaValue, Object> returnFunction;

        LuaType(Function<LuaValue, Object> func) {
            returnFunction = func;
        }

        public Object get(LuaValue val) {
            return returnFunction.apply(val);
        }
    }

    protected static Map<Class, LuaType> typesToLua = new HashMap<>() {{
        put(String.class, LuaType.STRING);
        put(Boolean.TYPE, LuaType.BOOLEAN);
        put(Byte.TYPE, LuaType.BYTE);
        put(Short.TYPE, LuaType.SHORT);
        put(Integer.TYPE, LuaType.INTEGER);
        put(Float.TYPE, LuaType.FLOAT);
        put(Long.TYPE, LuaType.LONG);
        put(Double.TYPE, LuaType.DOUBLE);
    }};

    public Object[] matchOverload(Method overload, Varargs args) throws LuaToJavaConversionError, MatchOverloadFailed {
        Class<?>[] parameters = overload.getParameterTypes();
        List<Object> params = new ArrayList<>();
        boolean lastParameterVarargs = overload.isVarArgs();
        if ((args.narg() != parameters.length && !lastParameterVarargs) ||
                (args.narg() < parameters.length)) throw new MatchOverloadFailed(overload, args);
        for (int i = 0; i < parameters.length; i++) {
            Class parameter = parameters[i];
            LuaValue lValue = args.arg(i+1);
            Object v = null;
            try {
                v = fromLuaValue(lValue, parameter);
            }
            catch (LuaToJavaConversionError e) {
                if (i == parameters.length - 1 && lastParameterVarargs) {
                    int l = args.narg() - i;
                    Class componentType = parameter.getComponentType();
                    Object remainingVarArgs = Array.newInstance(componentType, l);
                    for (int j = 0; j < l; j++) {
                        LuaValue lV = args.arg(parameters.length+j);
                        Object vA = fromLuaValue(lV, componentType);
                        Array.set(remainingVarArgs, j, vA);
                    }
                    v = remainingVarArgs;
                }
            }
            params.add(v);
        }
        return params.toArray();
    }

    public Object fromLuaValue(LuaValue val, Class type) throws LuaToJavaConversionError {
        if (val.isnil()) return null;
        try {
            if (type.isInstance(val)) {
                return val;
            }
            if (val.isuserdata()) {
                Object ud = wrap(val);
                if (type.isInstance(ud)) {
                    return ud;
                }
            }
            LuaType tp = typesToLua.get(type);
            if (tp != null) {
                return tp.get(val);
            }
            if (val.istable()) {
                if (type.isArray()) {
                    LuaTable table = val.checktable();
                    Class objType = type.getComponentType();

                    Object arr = Array.newInstance(objType, table.length());
                    for (int i = 1; i <= table.length(); i++) {
                        Object v = fromLuaValue(table.get(i), objType);
                        Array.set(arr, i-1, v);
                    }
                    return arr;
                }
                if (Collection.class.isAssignableFrom(type)) {
                    List<Object> list = new ArrayList<>();
                    LuaTable table = val.checktable();

                    Class objType = (Class)(((ParameterizedType) type.getGenericSuperclass()).getActualTypeArguments()[0]);
                    for (int i = 1; i <= table.length(); i++) {
                        Object v = fromLuaValue(table.get(i), objType);
                        list.add(v);
                    }
                    return list;
                }
                if (Map.class.isAssignableFrom(type)) {
                    Map<Object, Object> map = new HashMap<>();
                    LuaTable table = val.checktable();
                    Type[] genericTypes = ((ParameterizedType) type.getGenericSuperclass()).getActualTypeArguments();

                    Class keyClass = (Class) genericTypes[0];
                    Class objClass = (Class) genericTypes[1];

                    for (LuaValue key:
                            table.keys()) {
                        Object k = fromLuaValue(key, keyClass);
                        Object v = fromLuaValue(table.get(key), objClass);
                        map.put(k,v);
                    }
                    return map;
                }
            }
        }
        catch (LuaError ignored) {}
        throw new LuaToJavaConversionError(val.type(), val.typename(), type);
    }
}
