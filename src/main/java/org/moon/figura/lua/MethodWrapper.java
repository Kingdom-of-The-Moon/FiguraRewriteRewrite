package org.moon.figura.lua;

import com.google.common.collect.Lists;
import com.google.common.primitives.Primitives;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

public abstract sealed class MethodWrapper extends VarArgFunction {

    private static final int vis = Modifier.PUBLIC | Modifier.PRIVATE | Modifier.PROTECTED;
    public final boolean isStatic;
    protected final Class<?> clazz;


    protected final LuaTypeManager manager;

    protected MethodWrapper(LuaTypeManager manager, Class<?> cla, boolean isStatic) {
        this.isStatic = isStatic;
        this.manager = manager;
        clazz = cla;
    }

    public static MethodWrapper of(LuaTypeManager manager, Method... methods) {
        MethodWrapper wrapper = switch (methods.length) {
            case 0 -> throw new RuntimeException();
            case 1 -> Single.of(manager, methods[0]);
            default -> Multi.of(manager, methods);
        };
        wrapper.name = methods[0].getName();
        return wrapper;
    }

    abstract public List<Method> getMethods();

    public sealed abstract static class Single extends MethodWrapper {
        protected final Method method;


        protected final Parameter[] parameters;

        public Single(LuaTypeManager manager, Method mth) {
            super(manager, mth.getDeclaringClass(), Modifier.isStatic(mth.getModifiers()));
            method = mth;
            parameters = method.getParameters();
        }

        public static Single of(LuaTypeManager manager, Method method) {
            Parameter[] params = method.getParameters();
            if (params.length == 0)
                return new NoArgs(manager, method);
            if (method.isVarArgs())
                return new VarArg(manager, method);
            return new Fixed(manager, method);
        }

        @Override
        public String toString() {
            try {
                StringBuilder sb = new StringBuilder();
                getUsage(sb, method, parameters);
                return sb.toString();
            } catch (Exception e) {
                return "<" + e + ">";
            }
        }

        protected Varargs wrapCall(Object caller, Object... args) {
            return super.wrapCall(method, caller, args);
        }

        @Override
        public List<Method> getMethods() {
            return List.of(method);
        }

        @Override
        public Varargs invoke(Varargs args) {
            throw new LuaError(new NotImplementedException("what?"));
        }

        private static final class NoArgs extends Single {

            public NoArgs(LuaTypeManager luaTypeManager, Method method) {
                super(luaTypeManager, method);
            }

            @Override
            public Varargs invoke(Varargs args) {
                return wrapCall(isStatic ? null : args.checkuserdata(1, clazz));
            }
        }

        private static final class Fixed extends Single {
            public Fixed(LuaTypeManager manager, Method method) {
                super(manager, method);
            }

            @Override
            public Varargs invoke(Varargs args) {
                Queue<LuaValue> v = new ArrayDeque<>(manager.varargToList(args));
                Object caller = getCaller(v);
                Object[] params = getParams(v, parameters.length);
                return wrapCall(caller, params);
            }

        }

        private static final class VarArg extends Single {
            public VarArg(LuaTypeManager manager, Method method) {
                super(manager, method);
            }

            @Override
            public Varargs invoke(Varargs args) {
                Queue<LuaValue> v = new ArrayDeque<>(manager.varargToList(args));
                Object caller = getCaller(v);
                Object[] params = getParams(v, parameters.length - 1);
                if (!v.isEmpty()) {
                    LuaValue value = LuaValue.listOf(v.toArray(new LuaValue[]{}));
                    Parameter param = parameters[parameters.length - 1];
                    if (!manager.checkType(value, param.getType()) || value.isnil() && param.isAnnotationPresent(LuaNotNil.class))
                        throw new LuaError("Invalid parameter %s for function %s expected: %s, got %s (%s)".formatted(param.getName(), name, manager.getTypeName(param.getType()), value, manager.getTypeName(value.getClass())));
                    params[parameters.length - 1] = manager.luaToJava(value, param.getType());
                } else
                    params[parameters.length - 1] = Array.newInstance(parameters[parameters.length - 1].getType().arrayType(), 0);
                return wrapCall(caller, params);
            }
        }

        protected Object[] getParams(Queue<LuaValue> v, int num) {
            Object[] params = new Object[parameters.length];
            for (int i = 0; i < num; i++) {
                Parameter param = parameters[i];
                @NotNull LuaValue value = v.peek() == null ? NIL : v.poll();
                if (!manager.checkType(value, param.getType()) || value.isnil() && param.isAnnotationPresent(LuaNotNil.class))
                    throw new LuaError("Invalid parameter %s for function %s: expected %s, got %s (%s)".formatted(param.getName(), name, manager.getTypeName(param.getType()), value, manager.getTypeName(value.getClass())));
                params[i] = manager.luaToJava(NIL.equals(value) ? getDefault(param.getType()) : value, param.getType());
            }
            return params;
        }
    }

    public static final class Multi extends MethodWrapper {

        private final Method[] methods;
        private final MethodTree tree;

        private Multi(LuaTypeManager manager, Method... methods) {
            super(manager, methods[0].getDeclaringClass(), Modifier.isStatic(methods[0].getModifiers()));
            this.methods = methods;
            for (Method method : methods) {
                if (isStatic != Modifier.isStatic(method.getModifiers()))
                    throw new IllegalStateException("Overloads must all have same STATIC attribute, found %s %s.%s and %s overload".formatted(
                            isStatic ? "static" : "non-static", clazz.getSimpleName(), method.getName(), isStatic ? "non-static" : "static"));
                if (method.isVarArgs())
                    throw new IllegalStateException("Vararg method detected as one of overloads in %s.%s".formatted(clazz.getSimpleName(), method.getName()));
            }
            tree = new MethodTree(methods);
        }

        public static Multi of(LuaTypeManager manager, Method... methods) {
            return new Multi(manager, methods);
        }

        @Override
        public List<Method> getMethods() {
            return List.of(methods);
        }

        @Override
        public Varargs invoke(Varargs args) {
            Queue<LuaValue> v = new ArrayDeque<>(manager.varargToList(args));
            List<LuaValue> values = new ArrayList<>();
            Object caller = getCaller(v);
            List<MethodTree.Node> nodes = new ArrayList<>();
            nodes.add(tree.root);
            if (!v.isEmpty()) {
                while (!v.isEmpty()) {
                    List<MethodTree.Node> nextNodes = new ArrayList<>();
                    LuaValue value = v.poll();
                    assert value != null;
                    for (MethodTree.Node branch : nodes) {
                        var b = branch.children.entrySet().stream().filter(
                                entry -> manager.checkType(value, entry.getKey().getType()) && !(value.isnil() && entry.getKey().isAnnotationPresent(LuaNotNil.class))
                        );
                        if (branch.hasStrNum && value.isstring() && value.isnumber())
                            b = b.filter(entry -> manager.checkTypeStrict(value, entry.getKey().getType()));
                        nextNodes.addAll(b.map(Map.Entry::getValue).toList());
                    }
                    if (!nextNodes.isEmpty())
                        values.add(value);
                    nodes = nextNodes;
                }
            }
            exhaustEmptyAlternatives(values, nodes);
            MethodTree.Node match = null;
            for (MethodTree.Node node : nodes)
                if (node.method != null)
                    if (match != null)
                        throw new LuaError(new RuntimeException("wawa"));
                    else
                        match = node;
            if (match == null) {
                StringBuilder builder = new StringBuilder("No viable alternative found for argument").append(args.narg() == 1 ? " " : "s ");
                for (LuaValue val : manager.varargToList(args))
                    builder.append(
                            val.isuserdata() ? manager.getTypeName(val.checkuserdata().getClass()) : val.typename()
                    ).append(" ").append(val).append(" ");
                throw new LuaError(builder.toString());
            }
            Method method = match.method;
            Object[] params = new Object[values.size()];
            for (int i = values.size() - 1; i >= 0; i--) {
                LuaValue value = values.get(i);
                if (value == null || value.isnil())
                    value = getDefault(match.param.getType());
                params[i] = manager.luaToJava(value, match.param.getType());
                match = match.parent;
            }
            return wrapCall(method, caller, params);
        }

        private void exhaustEmptyAlternatives(List<LuaValue> values, List<MethodTree.Node> nodes) {
            MethodTree.Node node;
            while (nodes.size() == 1 && (node = nodes.get(0)).method == null) {
                List<Parameter> params = node.children.keySet().stream().filter(param -> !param.isAnnotationPresent(LuaNotNil.class)).toList();
                Parameter p;
                if (params.size() != 1) {
                    nodes.clear();
                    return;
                }
                if ((p = params.get(0)).isAnnotationPresent(LuaNotNil.class))
                    throw new LuaError("attempt to call " + name + " with nil as Non-Nil parameter " + p.getName());
                values.add(getDefault(p.getType()));
                nodes.set(0, node.children.get(p));
            }
        }

        @Override
        public String toString() {
            try {
                StringBuilder sb = new StringBuilder();
                for (Method method : methods)
                    getUsage(sb.append("\n"), method, method.getParameters());
                return sb.toString();
            } catch (Exception e) {
                return "<" + e + ">";
            }
        }

    }

    protected LuaValue getDefault(Class<?> type) {
        if (type.isPrimitive())
            return switch (type.getName()) {
                case "float", "double" -> LuaValue.valueOf(0d);
                case "int", "byte", "long", "short" -> LuaValue.valueOf(0);
                case "boolean" -> LuaValue.valueOf(false);
                default -> throw new RuntimeException("Unregistered Primitive type: " + type.getName());
            };
        return NIL;
    }

    protected Varargs wrapCall(Method method, Object caller, Object... args) {
        final Object result;
        try {
            result = method.invoke(caller, args);
        } catch (InvocationTargetException e) {
            throw e.getCause() instanceof LuaError l ? l : new LuaError(e.getCause());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        if (result instanceof Varargs v)
            return v;
        else if (result != null && result.getClass().isArray())
            return manager.wrapArray(result);
        return manager.javaToLua(result);
    }

    @Nullable
    protected Object getCaller(Queue<LuaValue> queue) {
        Object caller;
        if (isStatic)
            caller = null;
        else {
            LuaValue v = queue.isEmpty() ? NIL : queue.poll();
            if (!manager.checkType(v, clazz) || v == null) {
                throw new LuaError("function %s expected %s as it's caller, got %s (%s)".formatted(
                        name,
                        manager.getTypeName(clazz),
                        v,
                        v == null ? "null" : manager.getTypeName(v.getClass())
                ));
            } else
                caller = manager.luaToJava(v, clazz);
        }
        return caller;
    }

    public void getUsage(StringBuilder stringBuilder, Method method, Parameter[] parameters) {
        stringBuilder.append(Modifier.toString(method.getModifiers() & ~vis)).append(stringBuilder.isEmpty() ? "" : ' ');
        String typeName = manager.getTypeName(method.getReturnType());
        stringBuilder.append("void".equals(typeName) ? "nil" : typeName).append(' ');
        stringBuilder.append(manager.getTypeName(clazz)).append(isStatic ? '.' : ':');
        stringBuilder.append(method.getName());
        stringBuilder.append(Arrays.stream(parameters)
                .map(Parameter::getType)
                .map(manager::getTypeName)
                .collect(Collectors.joining(", ", "(", ")")));
        if (method.getExceptionTypes().length > 0) {
            stringBuilder.append(Arrays.stream(method.getExceptionTypes())
                    .map(manager::getTypeName)
                    .collect(Collectors.joining(",", " throws ", "")));
        }
    }

    @Override
    public String name() {
        return name;
    }

    public static class MethodTree {
        Node root;

        public MethodTree(Method... methods) {
            root = new Node(null, null);
            for (Method method : methods) {
                root.add(method);
            }
        }

        public static class Node {
            public final Node parent;
            public Method method;
            public final Map<Parameter, Node> children = new HashMap<>();
            public Parameter param;
            public boolean hasStrNum = false;

            public Node(Node parent, Parameter param) {
                this.parent = parent;
                this.param = param;
            }

            public void add(Method method) {
                add(Lists.newArrayList(method.getParameters()), method);
            }

            private void add(List<Parameter> parameters, Method method) {
                if (parameters.size() == 0)
                    if (this.method == null)
                        this.method = method;
                    else
                        throw new IllegalStateException("Method overload cannot have two methods with same argument list");
                else
                    children.compute(
                            parameters.get(0), (k, v) -> v == null ? new Node(this, k) : v
                    ).add(parameters.subList(1, parameters.size()), method);
                Set<Class<?>> types = children.keySet().stream().map(Parameter::getType).filter(cl -> Primitives.allWrapperTypes().contains(cl) || Primitives.allPrimitiveTypes().contains(cl) || String.class.equals(cl)).collect(Collectors.toSet());
                if (types.size() == 2)
                    if (types.contains(String.class))
                        hasStrNum = true;
                    else
                        throw new RuntimeException("More Wawa");
            }
        }
    }
}