package org.moon.figura.lua.newdocswip;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaUserdata;
import org.luaj.vm2.LuaValue;
import org.moon.figura.FiguraMod;
import org.moon.figura.avatar.Avatar;
import org.moon.figura.lua.FiguraAPIManager;
import org.moon.figura.lua.FiguraLuaRuntime;
import org.moon.figura.lua.LuaWhitelist;
import org.moon.figura.lua.MethodWrapper;
import org.moon.figura.lua.docs.LuaFieldDoc;
import org.moon.figura.lua.docs.LuaMethodDoc;
import org.moon.figura.lua.docs.LuaTypeDoc;
import org.moon.figura.math.matrix.FiguraMatrix;
import org.moon.figura.math.vector.FiguraVector;
import org.moon.figura.utils.FiguraText;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;
import static net.minecraft.ChatFormatting.*;
import static org.moon.figura.utils.ColorUtils.Colors.*;

public class NewDocsManager {

    //class name map
    private static final Map<Class<?>, String> NAME_MAP = new HashMap<>() {{
        //Built in type names, even for things that don't have docs
        put(Double.class, "Number");
        put(double.class, "Number");
        put(Float.class, "Number");
        put(float.class, "Number");
        put(Number.class, "Number");

        put(Integer.class, "Integer");
        put(int.class, "Integer");
        put(Long.class, "Integer");
        put(long.class, "Integer");

        put(void.class, "nil");

        put(String.class, "String");

        put(Object.class, "Any");
        put(LuaUserdata.class, "Userdata");

        put(Boolean.class, "Boolean");
        put(boolean.class, "Boolean");

        //Lua things
        put(LuaFunction.class, "Function");
        put(LuaTable.class, "Table");
        put(LuaValue.class, "AnyType");

        //converted things
        put(Map.class, "Table");
        put(HashMap.class, "Table");
        put(List.class, "Table");
        put(ArrayList.class, "Table");

        //Figura types
        put(FiguraVector.class, "Vector");
        put(FiguraMatrix.class, "Matrix");
    }};

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private static final List<Doc> allDocs                   = new ArrayList<>();
    private static final Map<Class<?>, ClassDoc> classDocMap = new HashMap<>();

    private static BaseDoc root;

    private static FiguraLuaRuntime runtime;

    public static void init() {
        allDocs.clear();
        classDocMap.clear();
        root = new BaseDoc("docs_new");
        runtime = new FiguraLuaRuntime(new Avatar(UUID.nameUUIDFromBytes(new byte[]{0, 0, 0, 0})), new HashMap<>());
        runtime.typeManager.generateMetatableFor(NewGlobals.class);
        for (Map.Entry<Class<?>, String> entry : NAME_MAP.entrySet())
            runtime.typeManager.setTypeName(entry.getKey(), entry.getValue());
        runtime.init(null);
        var types = new BaseDoc("types", root);
        for (Class<?> clas : FiguraAPIManager.WHITELISTED_CLASSES)
            if (!classDocMap.containsKey(clas) && clas.isAnnotationPresent(LuaTypeDoc.class)) new ClassDoc(clas, types);
        for(ClassDoc doc : classDocMap.values()) doc.initFieldAndMethods();
        System.out.println(types.children.stream().map(classDoc -> classDoc.getName().getString()).sorted().collect(Collectors.joining(", ")));

        ClassDoc globals = new ClassDoc(NewGlobals.class, root);
        globals.initFieldAndMethods();
        classDocMap.remove(globals.clas);
    }

    public static void updateDescriptions() {
        for(Doc doc : allDocs){
            if (!(doc instanceof MethodDoc || doc instanceof FieldDoc)) {
                continue;
            }
            Doc parent = doc.parent;
            String nameKey = Doc.toSnakeCase(doc.name);
            String descriptionKey;
            ArrayList<String> list = new ArrayList<>();
            if(parent instanceof ClassDoc){
                do {
                    descriptionKey = parent.descriptionKey + "." + nameKey;
                    list.add(descriptionKey);
                    parent = ((ClassDoc)parent).superClassDoc;
                } while (parent != null && FiguraText.of("docs." + descriptionKey).getString().equals("figura.docs." + descriptionKey));
                if(parent == null && FiguraText.of("docs." + descriptionKey).getString().equals("figura.docs." + descriptionKey)) {
                    FiguraMod.LOGGER.warn("No doc string found for {}'s field {}, checked: {}", doc.parent.name, doc.name, list);
                    continue;
                }
                doc.descriptionKey = descriptionKey;
            }

        }
    }

    public static LiteralCommandNode<FabricClientCommandSource> getCommand() {
        return root.createCommand();
    }

    public static MutableComponent getTypeNameText(Class<?> clas){
        var type = Component.literal(runtime.typeManager.getTypeName(clas)).withStyle(YELLOW);
        if(classDocMap.containsKey(clas)){
            type = type.withStyle(UNDERLINE).withStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, classDocMap.get(clas).getCommandPath())));
        }
        return type;
    }

    abstract static class Doc implements Command<FabricClientCommandSource> {
        public static final String BODY_KEY = "doc_template.body";
        public static final MutableComponent HEADER = FiguraText.of("doc_template.header", FiguraText.of()).withStyle(FRAN_PINK.style).withStyle(UNDERLINE);
        public static final MutableComponent DESC = FiguraText.of("doc_template.desc", FiguraText.of()).withStyle(CHLOE_PURPLE.style);
        public static final String BULLET_KEY = "doc_template.bullet";
        public Doc parent;
        public String name;
        public List<Doc> children = new ArrayList<>();
        public boolean executes = true;
        protected String descriptionKey;
        protected LiteralCommandNode<FabricClientCommandSource> command;

        public LiteralCommandNode<FabricClientCommandSource> createCommand() {
            var cmd = literal(name);
            for (Doc child : children)
                cmd.then(child.createCommand());
            if (executes)
                cmd.executes(this);
            command = cmd.build();
            return command;
        }

        public void addChild(Doc child) {
            children.add(child);
        }

        @Override
        public int run(CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
            FiguraMod.sendChatMessage(getPrintText());
            return 0;
        }

        protected MutableComponent getBullet(Object text){
            return FiguraText.of(BULLET_KEY, text);
        }

        protected MutableComponent getSyntax(){
            return Component.empty();
        }

        protected MutableComponent getPrintText() {
            MutableComponent name = getName();
            MutableComponent firstBullet = FiguraText.of(getType());
            MutableComponent secondBullet;
            if (this instanceof ClassDoc classDoc && Modifier.isAbstract(classDoc.clas.getModifiers())) {
                firstBullet.append(" ").append(name.withStyle(MAYA_BLUE.style)).append(Component.literal(" (abstract)").withStyle(SKYE_BLUE.style));
                secondBullet = Component.empty();
                for(Class<?> clas : classDocMap.keySet()){
                    if(classDoc.clas.isAssignableFrom(clas) && clas != classDoc.clas){
                        if(!secondBullet.getString().isBlank())
                            secondBullet.append(", ");
                        secondBullet.append(getTypeNameText(clas));
                    }
                }
                secondBullet = FiguraText.of("docs.text.subtypes").append(": ").append(secondBullet.getString().isBlank() ? FiguraText.of("docs.no_subtypes") : secondBullet);
            } else
                secondBullet = name.copy();
             return FiguraText.of(
                    BODY_KEY,
                    HEADER,
                    getBullet(firstBullet).append(":").withStyle(CHLOE_PURPLE.style),
                    getBullet(secondBullet),
                    getSyntax(),
                    DESC,
                    FiguraText.of("docs." + descriptionKey)
            ).withStyle(MAYA_BLUE.style);
        }

        protected MutableComponent getName() {
            return Component.literal(name);
        }

        protected String getType(){
            return "";
        }

        private static final List<Pattern> steps = Stream.of(
            "([^A-Z_\\d])([A-Z]{2,}(?![a-z_0\\d$])|[A-Z]+$)",
            "([^_])([A-Z][a-z]+)",
            "([a-z])([A-Z])"
        ).map(Pattern::compile).toList();

        protected static String toSnakeCase(String name){
            boolean b = name.toLowerCase().equals(name) || name.toUpperCase().equals(name);
            if(!(b) && name.indexOf('_') != -1) return null;
            else if (b) return name.toLowerCase();
            else {
                Matcher matcher;
                for(Pattern step : steps){
                    matcher = step.matcher(name);
                    while (matcher.find()){
                        name = matcher.replaceFirst(matcher.group(1) + "_" + matcher.group(2).toLowerCase());
                        matcher = step.matcher(name);
                    }
                }
                return name;
            }
        }

        protected String getCommandPath() {
            CommandDispatcher<FabricClientCommandSource> dispatcher = ClientCommandManager.getActiveDispatcher();
            if(dispatcher != null)
                return "/" + dispatcher.getPath(this.command).parallelStream().collect(Collectors.joining(" "));
            else
                return "";
        }
    }

    static class BaseDoc extends Doc {

        BaseDoc(String name) {
            this(name, null);
        }

        BaseDoc(String name, Doc parent) {
            allDocs.add(this);
            this.name = name;
            this.parent = parent;
            if (parent != null) parent.addChild(this);
        }
    }

    static class ClassDoc extends Doc {
        private final Class<?> clas;
        public ClassDoc superClassDoc;

        public ClassDoc(Class<?> clas, Doc parent) {
            allDocs.add(this);
            this.clas = clas;
            classDocMap.put(clas, this);
            this.name = clas.getAnnotation(LuaTypeDoc.class).name();
            this.descriptionKey = clas.getAnnotation(LuaTypeDoc.class).value();
            Class<?> t = clas.getSuperclass();
            if ((t).isAnnotationPresent(LuaTypeDoc.class))
                if (classDocMap.containsKey(t))
                    superClassDoc = classDocMap.get(t);
                else {
                    ClassDoc doc = new ClassDoc(t, parent);
                    classDocMap.put(t, doc);
                    superClassDoc = doc;
                }
            this.parent = parent;
            parent.addChild(this);
        }

        void initFieldAndMethods() {
            boolean bl = clas.getAnnotation(LuaTypeDoc.class).blacklist();
            for (Field field : clas.getDeclaredFields())
                if (field.isAnnotationPresent(LuaWhitelist.class) && field.isAnnotationPresent(LuaFieldDoc.class) == bl)
                    addChild(new FieldDoc(field, this));
            LuaTable index = runtime.typeManager.getIndexFor(clas);
            if(index != null)
                for (LuaValue value : Arrays.stream(index.keys()).map(index::rawget).toArray(LuaValue[]::new)){
                    if (
                            value instanceof LuaFunction wrapper && (
                                    !(wrapper instanceof MethodWrapper methodWrapper) || methodWrapper.getMethods().stream().anyMatch(method -> method.isAnnotationPresent(LuaMethodDoc.class)) == bl
                            )
                    ) addChild(new MethodDoc(wrapper, this));
                }
            else
                FiguraMod.LOGGER.warn("Index table for class {} does not exist, maybe it has not been initialised properly?", clas);
        }

        @Override
        protected String getType() {
            return "docs.text.type";
        }
    }

    static class FieldDoc extends Doc {
        private final Doc type;
        private final Field field;

        FieldDoc(Field field, Doc parent) {
            allDocs.add(this);
            this.field = field;
            this.parent = parent;
            var fda = field.getDeclaredAnnotation(LuaTypeDoc.class);
            this.name = fda == null || fda.name().isEmpty()? field.getName() : fda.name();
            this.type = classDocMap.get(field.getType());
            String nameKey = toSnakeCase(name);
            ArrayList<String> list = new ArrayList<>();
            children = List.of();
        }

        @Override
        public String getType() {
            return "docs.text.field";
        }

        @Override
        protected MutableComponent getName() {
            return Component.literal(type != null? type.name : runtime.typeManager.getTypeName(field.getType())).withStyle(YELLOW, UNDERLINE).append(" " + name);
        }

        public boolean isEditable(){
            return !Modifier.isFinal(field.getModifiers());
        }
    }

    static class MethodDoc extends Doc {
        private final LuaFunction wrapper;

        public MethodDoc(LuaFunction wrapper, Doc parent) {
            allDocs.add(this);
            this.wrapper = wrapper;
            this.parent = parent;
            this.name = wrapper.name();
            String nameKey = toSnakeCase(name);
            ArrayList<String> list = new ArrayList<>();
            children = List.of();
        }

        @Override
        protected MutableComponent getSyntax() {
            MutableComponent syntax = Component.literal("\n").append(getBullet(FiguraText.of("docs.text.syntax")).append(":\n\t").withStyle(CHLOE_PURPLE.style));
            if(wrapper instanceof MethodWrapper figuraFunction){
                var prefix = Component.empty();
                if(parent instanceof ClassDoc type){
                    prefix = Component.translatable(
                        "<%s>",
                        getTypeNameText(type.clas)
                    ).withStyle(YELLOW).append(
                            Component.literal(figuraFunction.isStatic? "." : ":").withStyle(FRAN_PINK.style).withStyle(BOLD)
                    );
                }
                for (Iterator<Method> iterator = figuraFunction.getMethods().stream().sorted(Comparator.comparingInt(Method::getParameterCount)).iterator(); iterator.hasNext(); ) {
                    Method method = iterator.next();
                    MutableComponent argText = Component.empty();
                    for (Parameter param : method.getParameters()) {
                        if(!argText.getString().isBlank())
                            argText.append(", ");
                        argText.append(getTypeNameText(param.getType())).append(Component.literal(" " + param.getName()).withStyle(WHITE));
                    }

                    syntax.append(getBullet(prefix)).append(Component.literal(name).withStyle(MAYA_BLUE.style)).append(
                            Component.translatable(
                                    "(%s) → %s %s",
                                    argText,
                                    FiguraText.of("docs.text.returns").withStyle(MAYA_BLUE.style),
                                    getTypeNameText(method.getReturnType())
                            ).withStyle(FRAN_PINK.style)
                    ).append("\n\t");
                }
                return syntax;
            } else {
                return syntax.append(getBullet(name + "(...)"));
            }
        }

        @Override
        protected String getType() {
            return "docs.text.function";
        }
    }
}
