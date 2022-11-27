package org.moon.figura.lua.newdocswip;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.UseAnim;
import org.jetbrains.annotations.NotNull;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaUserdata;
import org.luaj.vm2.LuaValue;
import org.moon.figura.FiguraMod;
import org.moon.figura.animation.Animation;
import org.moon.figura.avatar.Avatar;
import org.moon.figura.lua.FiguraAPIManager;
import org.moon.figura.lua.FiguraLuaRuntime;
import org.moon.figura.lua.LuaWhitelist;
import org.moon.figura.lua.MethodWrapper;
import org.moon.figura.lua.docs.FiguraListDocs;
import org.moon.figura.lua.docs.LuaFieldDoc;
import org.moon.figura.lua.docs.LuaMethodDoc;
import org.moon.figura.lua.docs.LuaTypeDoc;
import org.moon.figura.math.matrix.FiguraMatrix;
import org.moon.figura.math.vector.FiguraVector;
import org.moon.figura.mixin.input.KeyMappingAccessor;
import org.moon.figura.mixin.render.GameRendererAccessor;
import org.moon.figura.model.ParentType;
import org.moon.figura.model.rendering.texture.EntityRenderMode;
import org.moon.figura.model.rendering.texture.FiguraTextureSet;
import org.moon.figura.model.rendering.texture.RenderTypes;
import org.moon.figura.utils.ColorUtils;
import org.moon.figura.utils.FiguraText;
import org.moon.figura.utils.TextUtils;

import java.lang.reflect.*;
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

        put(Object.class, "Something");
        put(LuaUserdata.class, "Userdata");

        put(Boolean.class, "Boolean");
        put(boolean.class, "Boolean");

        //Lua things
        put(LuaFunction.class, "Function");
        put(LuaTable.class, "Table");
        put(LuaValue.class, "Any");

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
        runtime.typeManager.generateMetatableFor(NewMathDocs.class);
        for (Map.Entry<Class<?>, String> entry : NAME_MAP.entrySet())
            runtime.typeManager.setTypeName(entry.getKey(), entry.getValue());
        runtime.init(null);

        BaseDoc types = new BaseDoc("types", root);
        for (Class<?> clas : FiguraAPIManager.WHITELISTED_CLASSES)
            if (!classDocMap.containsKey(clas) && clas.isAnnotationPresent(LuaTypeDoc.class))
                new ClassDoc(clas, types);
        for(ClassDoc doc : classDocMap.values())
            doc.initFieldsAndMethods();

        BaseDoc enums = new BaseDoc("enums", root);
        for (Class<?> enumClass : new Class[]{
                RenderTypes.class,
                FiguraTextureSet.OverrideType.class,
                Animation.PlayState.class,
                Animation.LoopMode.class,
                EntityRenderMode.class
        }) new EnumDoc(enumClass, enums);
        new EnumDoc<>(Pose.class, enums, "EntityPose", null);
        new EnumDoc<>(UseAnim.class, enums, "UseAction", null);
        new EnumDoc<>(ItemTransforms.TransformType.class, enums, "ItemRenderPositions", "item_render_type");
        new EnumDoc<>(ParentType.class, enums){ protected MutableComponent getTextForElement(@NotNull Enum<ParentType> elem){
            MutableComponent comp = Component.empty().append(Component.literal(elem.name()).withStyle(WHITE));
            for (String alias : ((ParentType) elem).aliases) {
                comp.append(" | ").append(Component.literal(alias).withStyle(GRAY));
            }
            return comp;
        }};
        new EnumDoc<>(ColorUtils.Colors.class, enums){ protected MutableComponent getTextForElement(@NotNull Enum<ColorUtils.Colors> elem){
            MutableComponent comp = Component.empty().append(Component.literal(elem.name()).withStyle(WHITE));
            for (String alias : ((ColorUtils.Colors) elem).alias)
                comp.append(" | ").append(Component.literal(alias).withStyle(GRAY));
            return comp;
        }};
        new EnumDoc<>(PlayerModelPart.class, enums){ protected MutableComponent getTextForElement(@NotNull Enum<PlayerModelPart> elem){
            return Component.empty().append(Component.literal(elem.name()).withStyle(WHITE));
        }};
        new ListDoc(enums, "KeyIDs"){public List<MutableComponent> getList() {
            return KeyMappingAccessor.getAll().keySet().stream().map(Component::literal).toList();
        }};
        new ListDoc(enums, "Keybind"){public List<MutableComponent> getList() {
            return FiguraListDocs.KEYBINDS.stream().map(Component::literal).toList();
        }};
        new ListDoc(enums, "PostEffects"){
            Set<String> effects = new LinkedHashSet<>() {{
                for (ResourceLocation effect : GameRendererAccessor.getEffects()) {
                    String[] split = effect.getPath().split("/");
                    String name = split[split.length - 1];
                    add(name.split("\\.")[0]);
                }
            }};
            public List<MutableComponent> getList() {
                return effects.stream().map(Component::literal).toList();
            }
        };

        ClassDoc globals = new ClassDoc(NewGlobals.class, root);
        globals.initFieldsAndMethods();
        ClassDoc math = new ClassDoc(NewMathDocs.class, globals);
        math.initFieldsAndMethods();

        for(Doc doc : globals.children)
            doc.parent = root;
        classDocMap.remove(globals.clas);
    }

    public static void updateDescriptions() {
        for(Doc doc : allDocs){
            if (doc instanceof MethodDoc || doc instanceof FieldDoc) {
                Doc parent = doc.parent;
                String type = doc instanceof MethodDoc ? "method" : "field";
                String nameKey = Doc.toSnakeCase(doc.name);
                String descriptionKey;
                ArrayList<String> list = new ArrayList<>();
                if (parent instanceof ClassDoc) {
                    do {
                        descriptionKey = parent.descriptionKey + "." + nameKey;
                        list.add(descriptionKey);
                        parent = ((ClassDoc) parent).superClassDoc;
                    } while (parent != null && FiguraText.of("docs." + descriptionKey).getString().equals("figura.docs." + descriptionKey));
                    if (parent == null && FiguraText.of("docs." + descriptionKey).getString().equals("figura.docs." + descriptionKey)) {
                        FiguraMod.LOGGER.warn("No doc string found for {}'s {} {}, checked: {}", doc.parent.name, type, doc.name, list);
                    }
                } else if (parent == root) {
                    descriptionKey = "globals." + nameKey;
                    if (FiguraText.of("docs." + descriptionKey).getString().equals("figura.docs." + descriptionKey)) {
                        FiguraMod.LOGGER.warn("No doc string found for global {} {}, checked: {}", type, doc.name, "figura.docs." + descriptionKey);
                    }
                } else {
                    descriptionKey = nameKey;
                }
                doc.descriptionKey = descriptionKey;
            } else if(doc instanceof ListDoc listDoc){
                listDoc.updateMaxWidth();
            }

        }
    }

    public static LiteralCommandNode<FabricClientCommandSource> getCommand() {
        return root.createCommand();
    }

    public static @NotNull MutableComponent getTypeNameText(Class<?> clas){
        MutableComponent type = Component.literal(runtime.typeManager.getTypeName(clas)).withStyle(YELLOW);
        if(classDocMap.containsKey(clas)){
            type = type.withStyle(UNDERLINE).withStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, classDocMap.get(clas).getCommandPath())));
        }
        return Component.empty().append(type).withStyle(RESET);
    }

    abstract static class Doc implements Command<FabricClientCommandSource> {
        public static final String BODY_KEY = "doc_template.body";
        public static final MutableComponent HEADER = FiguraText.of("doc_template.header", FiguraText.of()).withStyle(FRAN_PINK.style).withStyle(UNDERLINE);
        public static final MutableComponent GLOBAL = FiguraText.of("docs.text.global");
        public static final MutableComponent KEY = FiguraText.of("docs.text.key");
        public static final MutableComponent VALUE = FiguraText.of("docs.text.value");
        public static final MutableComponent DESC = FiguraText.of("doc_template.desc", FiguraText.of()).withStyle(CHLOE_PURPLE.style);
        public static final String BULLET_KEY = "doc_template.bullet";
        public Doc parent;
        public String name;
        public List<Doc> children = new ArrayList<>();
        public boolean executes = true;
        protected String descriptionKey;
        protected LiteralCommandNode<FabricClientCommandSource> command;

        public Doc(Doc parent, String name){
            this.name = name;
            allDocs.add(this);
            this.parent = parent;
            if(parent != null)
                parent.addChild(this);
        }

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
            MutableComponent firstBullet = (this.parent == root && !(this instanceof BaseDoc) ? GLOBAL.copy().append(" ") : Component.empty()).append(FiguraText.of(getType()));
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
            if(this instanceof FieldDoc fieldDoc && fieldDoc.field.getGenericType() instanceof ParameterizedType parameterizedType) {
                Iterator<Type> iter = Arrays.stream(parameterizedType.getActualTypeArguments()).iterator();
                if (iter.hasNext()) {
                    secondBullet.append("\n\t");
                    secondBullet.append(getBullet(KEY).append(" ").append(getTypeNameText((Class<?>) iter.next())));
                }
                if (iter.hasNext()) {
                    secondBullet.append("\n\t");
                    secondBullet.append(getBullet(VALUE).append(" ").append(getTypeNameText((Class<?>) iter.next())));
                }
            }
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
            else if (!name.isBlank()){
                Matcher matcher;
                for(Pattern step : steps){
                    matcher = step.matcher(name);
                    while (matcher.find()){
                        name = matcher.replaceFirst(matcher.group(1) + "_" + matcher.group(2).toLowerCase());
                        matcher = step.matcher(name);
                    }
                }
                if(Character.isUpperCase(name.charAt(0)))
                    name = name.substring(0, 1).toLowerCase() + name.substring(1);
                return name;
            } else
                return name;
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
            super(parent, name);
            this.name = name;
        }
    }

    static class ClassDoc extends Doc {
        private final Class<?> clas;
        public ClassDoc superClassDoc;

        public ClassDoc(Class<?> clas, Doc parent) {
            super(parent, clas.getAnnotation(LuaTypeDoc.class).value());
            this.clas = clas;
            classDocMap.put(clas, this);
            this.descriptionKey = clas.getAnnotation(LuaTypeDoc.class).value();
            Class<?> t = clas.getSuperclass();
            if (t.isAnnotationPresent(LuaTypeDoc.class))
                if (classDocMap.containsKey(t))
                    superClassDoc = classDocMap.get(t);
                else {
                    ClassDoc doc = new ClassDoc(t, parent);
                    classDocMap.put(t, doc);
                    superClassDoc = doc;
                }
        }

        void initFieldsAndMethods() {
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
        private final Field field;

        FieldDoc(Field field, Doc parent){
            this(field, parent, field.getDeclaredAnnotation(LuaFieldDoc.class));
        }

        private FieldDoc(Field field, Doc parent, LuaFieldDoc ass){
            this(field, parent, ass == null || ass.value().isBlank()? field.getName() : ass.value());
        }

        private FieldDoc(Field field, Doc parent, String name) {
            super(parent, name);
            this.field = field;
            children = List.of();
        }

        @Override
        public String getType() {
            return "docs.text.field";
        }

        @Override
        protected MutableComponent getName() {
            return Component.empty().append(getTypeNameText(field.getType())).append(" " + name);
        }

        public boolean isEditable(){
            return !Modifier.isFinal(field.getModifiers());
        }
    }

    static class MethodDoc extends Doc {
        private final LuaFunction wrapper;

        MethodDoc(LuaFunction wrapper, Doc parent){
            this(wrapper, parent, wrapper.name());
        }

        private MethodDoc(LuaFunction wrapper, Doc parent, String name) {
            super(parent, name);
            this.wrapper = wrapper;
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

    /*
    * - Enums
    * RenderTypes
    * FiguraTextureSet.OverrideType
    * Pose
    * ItemTransforms.TransformType
    * Animation.PlayState
    * Animation.LoopMode
    * UseAnim
    * EntityRenderMode
    *
    * - CustomEnum
    * ParentType
    * PlayerModelPart
    * ColorUtils.Colors
    *
[23:04:50] [Render thread/INFO] (Minecraft) [CHAT] \n•*+•* Figura Docs *•+*•\n\n• Type TransformType:\n  • NONE                           \n  • THIRD_PERSON_LEFT_HAND \n  • THIRD_PERSON_RIGHT_HAND\n  • FIRST_PERSON_LEFT_HAND \n  • FIRST_PERSON_RIGHT_HAND\n  • HEAD                           \n  • GUI                             \n  • GROUND                        \n  • FIXED                          \n  \n\n• Description:\n  • figura.docs.enum.transform_types

    * - SpecialTypes
    * GameRendererAccessor.getEffects (GameRendererAccessor.getEffects() ResourceLocation[])
    * FiguraListDocs.KEYBINDS
    * KEY_IDS  (KeyMappingAccessor.getAll().keySet() Set<String>)
    * */

    static abstract class ListDoc extends Doc {
        private int maxWidth;

        public ListDoc(Doc parent, String name) {
            super(parent, name);
        }

        abstract public List<MutableComponent> getList();

        @Override
        protected MutableComponent getPrintText() {
            MutableComponent name = getName();
            MutableComponent firstBullet = FiguraText.of(getType()).append(" ").append(name.copy());
            MutableComponent secondBullet = Component.empty();
            for(FormattedText line : getPaddedList()) {
                secondBullet.append(getBullet(TextUtils.formattedTextToText(line))).append("\n\t");
            }
            return FiguraText.of(
                    BODY_KEY,
                    HEADER,
                    getBullet(firstBullet).append(":").withStyle(CHLOE_PURPLE.style),
                    secondBullet,
                    getSyntax(),
                    DESC,
                    FiguraText.of("docs." + descriptionKey)
            ).withStyle(MAYA_BLUE.style);
        }

        @Override
        protected String getType() {
            return "docs.text.enum";
        }

        public void updateMaxWidth(){
            maxWidth = 0;
            for(MutableComponent text : getList()){
                maxWidth = Math.max(Minecraft.getInstance().font.width(text), maxWidth);
            }
        }

        private List<FormattedText> getPaddedList(){
            return getList().stream().map(enu -> Minecraft.getInstance().font.substrByWidth(enu.append(" ".repeat(maxWidth)), maxWidth)).toList();
        }
    }

    static class EnumDoc<T extends Enum<T>> extends ListDoc {

        private final List<Enum<T>> values;

        public EnumDoc(@NotNull Class<T> enumClass, Doc parent){
            this(enumClass, parent, enumClass.getDeclaredAnnotation(LuaTypeDoc.class));
        }

        private EnumDoc(@NotNull Class<T> enumClass, Doc parent, LuaTypeDoc annotation){
            this(
                enumClass, parent,
                annotation == null || annotation.name().isBlank() ? enumClass.getSimpleName() : annotation.name(),
                annotation == null || annotation.value().isBlank() ? null : annotation.value()
            );
        }

        public EnumDoc(@NotNull Class<T> enumClass, Doc parent, String name, String value){
            super(parent, name);
            this.descriptionKey = "enum." + (value == null ? toSnakeCase(name) : value);
            if(!descriptionKey.endsWith("s"))
                descriptionKey = descriptionKey + "s";
            values = List.of(enumClass.getEnumConstants());
        }

        protected MutableComponent getTextForElement(@NotNull Enum<T> elem){
            return Component.literal(elem.name()).withStyle(WHITE);
        }

        @Override
        public List<MutableComponent> getList() {
            return values.stream().map(this::getTextForElement).toList();
        }

    }
}
