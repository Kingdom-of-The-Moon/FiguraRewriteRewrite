package org.moon.figura.lua;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.Nullable;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

public abstract sealed class MethodWrapper extends VarArgFunction {

    private static final int vis = Modifier.PUBLIC | Modifier.PRIVATE | Modifier.PROTECTED;
    protected final boolean isStatic;
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
            default -> MultiMethodWrapper.of(manager, methods);
        };
        wrapper.name = methods[0].getName();
        return wrapper;
    }

    public sealed abstract static class Single extends MethodWrapper {
        protected final Method method;


        protected final Parameter[] parameters;

        public Single(LuaTypeManager manager, Method mth) {
            super(manager, mth.getDeclaringClass(), Modifier.isStatic(mth.getModifiers()));
            method = mth;
            parameters = method.getParameters();
            boolean t = true;
        }

        public static Single of(LuaTypeManager manager, Method method) {
            Parameter[] params = method.getParameters();
            if (params.length == 0)
                return new NoArgs(manager, method);
            if (method.isVarArgs())
                return new VarArg(manager, method);
            return new Fixed(manager, method);

//            return new MySadStub(manager, method);
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
                if (v.size() != parameters.length) {
                    List<Parameter> parList = Lists.newArrayList(parameters).subList(v.size(), parameters.length);
                    StringBuilder builder = new StringBuilder();
                    builder.append("Unfilled parameters ");
                    for (Parameter param : parList)
                        builder.append(param.getName()).append(" ");
                    builder.append("for function ");
                    builder.append(manager.getTypeName(clazz));
                    builder.append(isStatic ? "." : ":").append(name);
                    throw new LuaError(builder.toString());
                }
                Object[] params = new Object[parameters.length];
                for (int i = 0; i < parameters.length; i++) {
                    Parameter param = parameters[i];
                    if (!manager.checkType(v.peek(), param.getType()))
                        throw new LuaError("Invalid parameter %s for function %s expected %s, got %s (%s)".formatted(
                                param.getName(),
                                name,
                                manager.getTypeName(param.getType()),
                                v.peek(),
                                v.peek() == null ? "null" : manager.getTypeName(v.poll().getClass())
                        ));
                    params[i] = manager.luaToJava(v.poll(), param.getType());
                }
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
                if (v.size() < parameters.length - 1) {
                    List<Parameter> parList = Lists.newArrayList(parameters).subList(v.size(), parameters.length);
                    StringBuilder builder = new StringBuilder();
                    builder.append("Unfilled parameters ");
                    for (Parameter param : parList)
                        builder.append(param.getName()).append(" ");
                    builder.append("for function ");
                    builder.append(name);
                    throw new LuaError(builder.toString());
                }
                Object[] params = new Object[parameters.length];
                for (int i = 0; i < parameters.length - 1; i++) {
                    Parameter param = parameters[i];
                    if (!manager.checkType(v.peek(), param.getType()))
                        throw new LuaError("Invalid parameter %s for function %s expected %s, got %s (%s)".formatted(
                                param.getName(),
                                name,
                                manager.getTypeName(param.getType()),
                                v.peek(),
                                v.peek() == null ? "null" : manager.getTypeName(v.poll().getClass())
                        ));
                    params[i] = manager.luaToJava(v.poll(), param.getType());
                }
                if (!v.isEmpty()) {
                    LuaValue extra = LuaValue.listOf(v.toArray(new LuaValue[]{}));
                    Parameter param = parameters[parameters.length - 1];
                    if (!manager.checkType(extra, param.getType()))
                        throw new LuaError("Invalid parameter %s for function %s expected %s, got %s (%s)".formatted(
                                param.getName(),
                                name,
                                manager.getTypeName(param.getType()),
                                extra,
                                manager.getTypeName(extra.getClass())
                        ));
                    params[params.length - 1] = manager.luaToJava(extra, param.getType());
                }
                return wrapCall(caller, params);
            }
        }

        private static final class MySadStub extends Single {
            public MySadStub(LuaTypeManager luaTypeManager, Method mth) {
                super(luaTypeManager, mth);
            }

            @Override
            public Varargs invoke(Varargs args) {
                throw new LuaError(new NotImplementedException("mewthod %s is not implemented ;-;".formatted(method)));
            }
        }
    }

    public static final class MultiMethodWrapper extends MethodWrapper {

        protected final Method[] methods;
        protected final MethodTree tree;

        protected MultiMethodWrapper(LuaTypeManager manager, Method... methods) {
            super(manager, methods[0].getDeclaringClass(), Modifier.isStatic(methods[0].getModifiers()));
            this.methods = methods;
            for (Method method : methods) {
                if (isStatic != Modifier.isStatic(method.getModifiers()))
                    throw new IllegalStateException("Overloads must all have same STATIC attribute, found %s %s.%s and %s overload".formatted(
                            isStatic ? "static" : "non-static",
                            clazz.getSimpleName(),
                            method.getName(),
                            isStatic ? "non-static" : "static"
                    ));
                if (method.isVarArgs())
                    throw new IllegalStateException("Vararg method detected as one of overloads in %s.%s".formatted(
                            clazz.getSimpleName(),
                            method.getName()
                    ));
            }
            tree = new MethodTree(methods);
        }

        public static MultiMethodWrapper of(LuaTypeManager manager, Method... methods) {
            return new MultiMethodWrapper(manager, methods);
        }

        @Override
        public Varargs invoke(Varargs args) {
            Queue<LuaValue> v = new ArrayDeque<>(manager.varargToList(args));
            List<LuaValue> values = new ArrayList<>();
            Object caller = getCaller(v);
            List<MethodTree.Node> nodes = new ArrayList<>();
            nodes.add(tree.root);
            while (!v.isEmpty()) {
                List<MethodTree.Node> nextNodes = new ArrayList<>();
                LuaValue value = v.poll();
                for (MethodTree.Node branch : nodes)
                    for (Parameter param : branch.children.keySet())
                        if (manager.checkType(value, param.getType()))
                            nextNodes.add(branch.children.get(param));
                if(!nextNodes.isEmpty())
                    values.add(value);
                nodes = nextNodes;
            }
            MethodTree.Node match = null;
            for (MethodTree.Node node : nodes)
                if (node.method != null)
                    if (match != null)
                        throw new LuaError(new RuntimeException("wawa"));
                    else
                        match = node;
            if (match == null) {
                StringBuilder builder = new StringBuilder("No viable alternative found, argument").append(args.narg() == 1 ? " " : "s ");
                for (LuaValue val : manager.varargToList(args))
                    builder.append(val).append(" ");
                builder.append("do not match any of the");
                builder.append(this);
                throw new LuaError(builder.toString());
            }
            Method method = match.method;
            Object[] params = new Object[values.size()];
            for (int i = values.size() - 1; i >= 0; i --){
                params[i] = manager.luaToJava(values.get(i), match.param.getType());
                match = match.parent;
            }
            return wrapCall(method, caller, params);
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
            LuaValue v = queue.isEmpty() ? LuaValue.NIL : queue.poll();
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
        String typeName = method.getReturnType().getSimpleName();
        stringBuilder.append("void".equals(typeName) ? "nil" : typeName).append(' ');
        stringBuilder.append(clazz.getSimpleName()).append(isStatic ? '.' : ':');
        stringBuilder.append(method.getName());
        stringBuilder.append(Arrays.stream(parameters)
                .map(Parameter::getType)
                .map(Class::getSimpleName)
                .collect(Collectors.joining(", ", "(", ")")));
        if (method.getExceptionTypes().length > 0) {
            stringBuilder.append(Arrays.stream(method.getExceptionTypes())
                    .map(Class::getSimpleName)
                    .collect(Collectors.joining(",", " throws ", "")));
        }
    }

    @Override
    public String tojstring() {
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
            }
        }
    }
}