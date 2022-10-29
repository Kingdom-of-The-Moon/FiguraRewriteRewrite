package org.moon.figura.lua;

import org.luaj.vm2.*;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.moon.figura.lua.docs.LuaTypeDoc;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * One LuaTypeManager per LuaRuntime, so that people can be allowed to edit the metatables within.
 */
public class LuaTypeManager {

    private final static Class<LuaWhitelist> whitelist = LuaWhitelist.class;

    private final Map<Class<?>, LuaTable> metatables = new HashMap<>();

    private final Map<Class<?>, String> namesCache = new HashMap<>();

    public void generateMetatableFor(Class<?> clazz) {
        if (metatables.containsKey(clazz))
            return;
        if (!clazz.isAnnotationPresent(whitelist))
            throw new IllegalArgumentException("Tried to generate metatable for un-whitelisted class " + clazz.getName() + "!");

        //Ensure that all whitelisted superclasses are loaded before this one

        Class<?> superC = clazz.getSuperclass();
        if (superC.isAnnotationPresent(whitelist))
            generateMetatableFor(clazz.getSuperclass());

        Map<String, List<Method>> overloads = new HashMap<>();

        LuaTable metatable = new LuaTable();

        LuaTable indexTable = new LuaTable();
        Class<?> currentClass = clazz;
        while (currentClass.isAnnotationPresent(whitelist)) {
            woe:
            for (Method method : currentClass.getDeclaredMethods()) {
                if (!method.isAnnotationPresent(whitelist) || method.isSynthetic())
                    continue;
                String name = method.getName();
                List<Method> methods = overloads.compute(name, (k, v) -> v == null ? new LinkedList<>() : v);
                for (int i = 0; i < methods.size(); i ++) {
                    Method method1 = methods.get(i);
                    if (Arrays.equals(method1.getParameterTypes(), method.getParameterTypes())) {
                        if (!method.getReturnType().isAssignableFrom(method1.getReturnType()))
                            methods.set(i, method);
                        continue woe;
                    }
                }
                methods.add(method);
            }
            currentClass = currentClass.getSuperclass();
        }

        for (String methodName : overloads.keySet()) {
            Method[] methods = filterOverrides(overloads.get(methodName));

            if("__index".equals(methodName)){
                metatable.set("__index", new TwoArgFunction() {
                    final LuaFunction indexer = MethodWrapper.of(LuaTypeManager.this, methods);

                    @Override
                    public LuaValue call(LuaValue arg1, LuaValue arg2) {
                        if (!arg1.isuserdata(methods[0].getDeclaringClass())) return NIL;
                        LuaValue result = indexTable.get(arg2);
                        return result != LuaValue.NIL ? result : indexer.call(arg1, arg2);
                    }
                });
                continue;
            }
            (methodName.startsWith("__") ? metatable : indexTable).set(methodName, MethodWrapper.of(this, methods));
        }

        if (metatable.rawget("__index") == LuaValue.NIL)
            metatable.set("__index", indexTable);

        //if we don't have a special toString, then have our toString give the type name from the annotation
        if (metatable.rawget("__tostring") == LuaValue.NIL) {
            metatable.set("__tostring", new OneArgFunction() {
                private final LuaString val = LuaString.valueOf(getTypeName(clazz));

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

    private static Method[] filterOverrides(List<Method> overloads) {
        return overloads.toArray(Method[]::new);
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

    public String getTypeName(Class<?> clazz) {
        return namesCache.computeIfAbsent(clazz, someClass -> {
            if (someClass == null)
                return LuaValue.NIL.typename();
            if (someClass.isAnnotationPresent(LuaTypeDoc.class))
                return someClass.getAnnotation(LuaTypeDoc.class).name();
            return someClass.getSimpleName();
        });
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
            LuaValue key = javaToLua(entry.getKey());
            LuaValue val = javaToLua(entry.getValue());
            table.set(key, val);
        }

        return table;
    }

    private LuaValue wrapList(List<?> list) {
        LuaTable table = new LuaTable();

        for (int i = 0; i < list.size(); i++)
            table.set(i + 1, javaToLua(list.get(i)));

        return table;
    }

    private LuaTable wrapCollection(Collection<?> collection) {
        LuaTable values = new LuaTable();

        for (Object o : collection)
            values.add(javaToLua(o));

        return values;
    }

    public Varargs wrapArray(Object array) {
        int len = Array.getLength(array);
        LuaValue[] args = new LuaValue[len];

        for (int i = 0; i < len; i++)
            args[i] = javaToLua(Array.get(array, i));

        return LuaValue.varargsOf(args);
    }

    //we need to allow string being numbers here
    //however in places like pings and print we should keep strings as strings
    public Object luaToJava(LuaValue value) {
        if (value.isnil())
            return null;
        else if (value.istable())
            return value.checktable();
        else if (value.isint())
            return value.checkint();
        else if (value.isnumber())
            return value.checkdouble();
        else if (value.isstring())
            return value.checkjstring();
        else if (value.isboolean())
            return value.checkboolean();
        else if (value.isfunction())
            return value.checkfunction();
        else if (value.isuserdata())
            return value.checkuserdata(Object.class);
        else
            return null;
    }

    public boolean checkTypeStrict(LuaValue value, Class<?> type) {
        if (type == null || value == null || value.isnil())
            return true;
        if(type.isAssignableFrom(value.getClass()))
            return true;
        LuaType luaType = luaToJavaTypes.get(type);
        if (luaType != null)
            return luaType.checkStrict(value);
        return checkType(value, type);
    }

    public boolean checkType(LuaValue value, Class<?> type) {
        if (type == null || value == null || value.isnil())
            return true;
        if(type.isAssignableFrom(value.getClass()))
            return true;
        LuaType luaType = luaToJavaTypes.get(type);
        if (luaType != null)
            return luaType.check(value);
        if (value.istable() && (type.isArray() || Collection.class.isAssignableFrom(type) || Map.class.isAssignableFrom(type))) {
            LuaTable table = value.checktable();
            if (type.isArray()) {
                Class<?> arrayType = type.arrayType();
                for (LuaValue key : table.keys()) {
                    if (!key.isint())
                        return false;
                    if (checkType(table.get(key), arrayType))
                        return false;
                }
                return true;
            }
            if (Collection.class.isAssignableFrom(type)) {
                Class<?> valType = (Class<?>) ((ParameterizedType) type.getGenericSuperclass()).getActualTypeArguments()[0];
                for (LuaValue key : table.keys()) {
                    if (!key.isint())
                        return false;
                    if (checkType(table.get(key), valType))
                        return false;
                }
                return true;
            }
            if (Map.class.isAssignableFrom(type)) {
                Class<?>[] types = (Class<?>[]) ((ParameterizedType) type.getGenericSuperclass()).getActualTypeArguments();
                for (LuaValue key : table.keys()) {
                    if (checkType(key, types[0]))
                        return false;
                    if (checkType(table.get(key), types[1]))
                        return false;
                }
                return true;
            }
        }
        return metatables.containsKey(type) && value.isuserdata(type);
    }

    public Object luaToJava(LuaValue value, Class<?> type) {
        if (value == null)
            return null;
        Class<? extends LuaValue> valueClass = value.getClass();
        if (type == null)
            return luaToJava(value);
        if (value.isnil())
            return null;
        if (type == Object.class)
            if (value.isuserdata())
                return value.checkuserdata();
            else if (LuaString.class.isAssignableFrom(valueClass))
                return value.checkjstring();
            else if (LuaInteger.class.isAssignableFrom(valueClass))
                return value.checkint();
            else if (LuaNumber.class.isAssignableFrom(valueClass))
                return value.checkdouble();
            else if (LuaBoolean.class.isAssignableFrom(valueClass))
                return value.checkboolean();
            else
                return null;
        if (type.isAssignableFrom(valueClass))
            return type.cast(value);
        if (value.isuserdata(type))
            return value.checkuserdata(type);
        LuaType luaToJava = luaToJavaTypes.get(type);
        if (luaToJava != null)
            return luaToJava.get(value);
        if (value.istable()) {
            LuaTable table = value.checktable();
            if (type.isArray()) {
                Class<?> objType = type.getComponentType();

                Object[] arr = new Object[table.length()];
                for (int i = 0, len = table.length(); i < len; i++) {
                    Object v = luaToJava(table.get(i + 1), objType);
                    arr[i] = v;
                }
                return Arrays.copyOf(arr, arr.length, (Class<? extends Object[]>) type);
            }
            if (Collection.class.isAssignableFrom(type)) {
                List<Object> list = new ArrayList<>();

                Class<?> objType = (Class<?>) (((ParameterizedType) type.getGenericSuperclass()).getActualTypeArguments()[0]);
                for (int i = 1; i <= table.length(); i++)
                    list.add(luaToJava(table.get(i), objType));
                return list;
            }
            if (Map.class.isAssignableFrom(type)) {
                Map<Object, Object> map = new HashMap<>();
                Type[] genericTypes = ((ParameterizedType) type.getGenericSuperclass()).getActualTypeArguments();

                Class<?> keyClass = (Class<?>) genericTypes[0];
                Class<?> objClass = (Class<?>) genericTypes[1];

                for (LuaValue key : table.keys()) {
                    Object k = luaToJava(key, keyClass);
                    Object v = luaToJava(table.get(key), objClass);
                    map.put(k, v);
                }
                return map;
            }
        }
        return null;
    }

    public LuaValue javaToLua(Object val) {
        if (val == null)
            return LuaValue.NIL;
        if (val instanceof LuaValue l)
            return l;
        if (val instanceof Double d)
            return LuaValue.valueOf(d);
        if (val instanceof String s)
            return LuaValue.valueOf(s);
        if (val instanceof Boolean b)
            return LuaValue.valueOf(b);
        if (val instanceof Integer i)
            return LuaValue.valueOf(i);
        if (val instanceof Float f)
            return LuaValue.valueOf(f);
        if (val instanceof Byte b)
            return LuaValue.valueOf(b);
        if (val instanceof Long l)
            return LuaValue.valueOf(l);
        if (val instanceof Character c)
            return LuaValue.valueOf(c);
        if (val instanceof Short s)
            return LuaValue.valueOf(s);
        if (val instanceof Map<?, ?> map)
            return wrapMap(map);
        if (val instanceof List<?> list)
            return wrapList(list);
        if (val instanceof Collection<?> col)
            return wrapCollection(col);
        else
            return wrap(val);
    }

    public List<LuaValue> varargToList(Varargs vararg) {

        List<LuaValue> values = new ArrayList<>();
        for (int i = 1; i <= vararg.narg(); i++) {
            values.add(vararg.arg(i));
        }
        return values;
    }

    public boolean canInfer(Class<?> type) {
        return luaToJavaTypes.containsKey(type) && !type.isPrimitive();
    }

    private enum LuaType {
        BYTE(LuaValue::tobyte, LuaValue::isint, val -> val.isint() && val instanceof LuaNumber),
        SHORT(LuaValue::toshort, LuaValue::isint, val -> val.isint() && val instanceof LuaNumber),
        LONG(LuaValue::tolong, LuaValue::isint, val -> val.isint() && val instanceof LuaNumber),
        FLOAT(value -> (float)value.checkdouble(), LuaValue::isnumber, val -> val.isnumber() && val instanceof LuaNumber),
        BOOLEAN(LuaValue::checkboolean, LuaValue::isboolean, LuaValue::isboolean),
        DOUBLE(LuaValue::checkdouble, LuaValue::isnumber, val -> val.isnumber() && val instanceof LuaNumber),
        INTEGER(LuaValue::checkint, LuaValue::isint, val -> val.isint() && val instanceof LuaNumber),
        STRING(LuaValue::checkjstring, LuaValue::isstring, val -> val.isstring() && val instanceof LuaString);

        private final Function<LuaValue, Object> cast;
        private final Predicate<LuaValue> check;
        private final Predicate<LuaValue> strict;

        LuaType(Function<LuaValue, Object> cast, Predicate<LuaValue> check, Predicate<LuaValue> strict) {
            this.cast = cast;
            this.check = check;
            this.strict = strict;
        }

        public Object get(LuaValue val) {
            if (check.test(val))
                return cast.apply(val);
            else
                return null;
        }

        public boolean check(LuaValue val) {
            return check.test(val);
        }

        public boolean checkStrict(LuaValue val) {
            return strict.test(val);
        }
    }

    protected static Map<Class<?>, LuaType> luaToJavaTypes = new HashMap<>() {{
        put(String.class, LuaType.STRING);
        put(Boolean.TYPE, LuaType.BOOLEAN);
        put(Byte.TYPE, LuaType.BYTE);
        put(Short.TYPE, LuaType.SHORT);
        put(Integer.TYPE, LuaType.INTEGER);
        put(Float.TYPE, LuaType.FLOAT);
        put(Long.TYPE, LuaType.LONG);
        put(Double.TYPE, LuaType.DOUBLE);
        put(Boolean.class, LuaType.BOOLEAN);
        put(Byte.class, LuaType.BYTE);
        put(Short.class, LuaType.SHORT);
        put(Integer.class, LuaType.INTEGER);
        put(Float.class, LuaType.FLOAT);
        put(Long.class, LuaType.LONG);
        put(Double.class, LuaType.DOUBLE);
    }};

}
