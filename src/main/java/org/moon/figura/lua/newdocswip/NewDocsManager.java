package org.moon.figura.lua.newdocswip;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaUserdata;
import org.luaj.vm2.LuaValue;
import org.moon.figura.FiguraMod;
import org.moon.figura.animation.Animation;
import org.moon.figura.avatar.Avatar;
import org.moon.figura.lua.FiguraAPIManager;
import org.moon.figura.lua.FiguraLuaRuntime;
import org.moon.figura.lua.MethodWrapper;
import org.moon.figura.lua.api.*;
import org.moon.figura.lua.api.action_wheel.Action;
import org.moon.figura.lua.api.action_wheel.ActionWheelAPI;
import org.moon.figura.lua.api.action_wheel.Page;
import org.moon.figura.lua.api.entity.EntityAPI;
import org.moon.figura.lua.api.entity.LivingEntityAPI;
import org.moon.figura.lua.api.entity.PlayerAPI;
import org.moon.figura.lua.api.event.EventsAPI;
import org.moon.figura.lua.api.event.LuaEvent;
import org.moon.figura.lua.api.keybind.FiguraKeybind;
import org.moon.figura.lua.api.keybind.KeybindAPI;
import org.moon.figura.lua.api.math.MatricesAPI;
import org.moon.figura.lua.api.math.VectorsAPI;
import org.moon.figura.lua.api.nameplate.EntityNameplateCustomization;
import org.moon.figura.lua.api.nameplate.NameplateAPI;
import org.moon.figura.lua.api.nameplate.NameplateCustomization;
import org.moon.figura.lua.api.nameplate.NameplateCustomizationGroup;
import org.moon.figura.lua.api.particle.LuaParticle;
import org.moon.figura.lua.api.particle.ParticleAPI;
import org.moon.figura.lua.api.ping.PingAPI;
import org.moon.figura.lua.api.ping.PingFunction;
import org.moon.figura.lua.api.sound.LuaSound;
import org.moon.figura.lua.api.sound.SoundAPI;
import org.moon.figura.lua.api.vanilla_model.VanillaGroupPart;
import org.moon.figura.lua.api.vanilla_model.VanillaModelAPI;
import org.moon.figura.lua.api.vanilla_model.VanillaModelPart;
import org.moon.figura.lua.api.world.BiomeAPI;
import org.moon.figura.lua.api.world.BlockStateAPI;
import org.moon.figura.lua.api.world.ItemStackAPI;
import org.moon.figura.lua.api.world.WorldAPI;
import org.moon.figura.lua.docs.LuaFieldDoc;
import org.moon.figura.lua.docs.LuaTypeDoc;
import org.moon.figura.math.matrix.FiguraMat2;
import org.moon.figura.math.matrix.FiguraMat3;
import org.moon.figura.math.matrix.FiguraMat4;
import org.moon.figura.math.matrix.FiguraMatrix;
import org.moon.figura.math.vector.*;
import org.moon.figura.model.FiguraModelPart;
import org.moon.figura.model.rendering.texture.FiguraTexture;
import org.moon.figura.model.rendertasks.BlockTask;
import org.moon.figura.model.rendertasks.ItemTask;
import org.moon.figura.model.rendertasks.RenderTask;
import org.moon.figura.model.rendertasks.TextTask;
import org.moon.figura.utils.FiguraText;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;
import static net.minecraft.ChatFormatting.UNDERLINE;
import static net.minecraft.ChatFormatting.YELLOW;
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


    private static final List<Class<?>> classes = new ArrayList<>();

    private static final List<ClassDoc> docs = new ArrayList<>();

    private static final Map<String, List<Class<?>>> GLOBAL_CHILDREN = new HashMap<>() {{
        put("action_wheel", List.of(
            ActionWheelAPI.class,
            Page.class,
            Action.class
        ));

        put("animations", List.of(
            Animation.class
        ));

        put("nameplate", List.of(
            NameplateAPI.class,
            NameplateCustomization.class,
            EntityNameplateCustomization.class,
            NameplateCustomizationGroup.class
        ));

        put("world", List.of(
            WorldAPI.class,
            BiomeAPI.class,
            BlockStateAPI.class,
            ItemStackAPI.class
        ));

        put("vanilla_model", List.of(
            VanillaModelAPI.class,
            VanillaModelPart.class,
            VanillaGroupPart.class
        ));

        put("models", List.of(
            FiguraModelPart.class,
            RenderTask.class,
            BlockTask.class,
            ItemTask.class,
            TextTask.class
        ));

        put("player", List.of(
            EntityAPI.class,
            LivingEntityAPI.class,
            PlayerAPI.class
        ));

        put("events", List.of(
            EventsAPI.class,
            LuaEvent.class
        ));

        put("keybind", List.of(
            KeybindAPI.class,
            FiguraKeybind.class
        ));

        put("vectors", List.of(
            VectorsAPI.class,
            FiguraVec2.class,
            FiguraVec3.class,
            FiguraVec4.class,
            FiguraVec5.class,
            FiguraVec6.class
        ));

        put("matrices", List.of(
            MatricesAPI.class,
            FiguraMat2.class,
            FiguraMat3.class,
            FiguraMat4.class
        ));

        put("client", List.of(
            ClientAPI.class
        ));

        put("host", List.of(
            HostAPI.class
        ));

        put("avatar", List.of(
            AvatarAPI.class
        ));

        put("particles", List.of(
            ParticleAPI.class,
            LuaParticle.class
        ));

        put("sounds", List.of(
            SoundAPI.class,
            LuaSound.class
        ));

        put("renderer", List.of(
            RendererAPI.class
        ));

        put("pings", List.of(
            PingAPI.class,
            PingFunction.class
        ));

        put("textures", List.of(
            TextureAPI.class,
            FiguraTexture.class
        ));
    }};

    private static List<Doc> allDocs                   = new ArrayList<>();
    private static Map<Class<?>, ClassDoc> classDocMap = new HashMap<>();
    private static Map<Method, MethodDoc> methodDocMap = new HashMap<>();

    private static BaseDoc root;

    private static FiguraLuaRuntime runtime;

    public static void init() {
        runtime = new FiguraLuaRuntime(new Avatar(UUID.nameUUIDFromBytes(new byte[]{0, 0, 0, 0})), new HashMap<>());
        for (Map.Entry<Class<?>, String> entry : NAME_MAP.entrySet()) {
            String name;
            FiguraMod.LOGGER.info("Checking type name for {}", entry.getKey());
            if (!(name = runtime.typeManager.setTypeName(entry.getKey(), entry.getValue())).equals(entry.getValue()))
                FiguraMod.LOGGER.warn("Already named type: {}, current name: {}, expected: {}", entry.getKey(), name, entry.getKey());
        }
        runtime.init(null);
        for (Class<?> clas : FiguraAPIManager.WHITELISTED_CLASSES)
            if (!classDocMap.containsKey(clas) && clas.isAnnotationPresent(LuaTypeDoc.class)){
                ClassDoc doc = new ClassDoc(clas, null);
                classDocMap.put(clas, doc);
            }
        root = new BaseDoc("new_docs");
        BaseDoc globals = new BaseDoc("globals", root);
        for (var entry : GLOBAL_CHILDREN.entrySet()) {
            try {
                globals.addChild(new GlobalDoc(NewGlobals.class.getDeclaredField(entry.getKey()), entry.getValue(), globals));
            } catch (NoSuchFieldException e) {
                globals.addChild(new GlobalDoc(entry.getKey(), entry.getValue().get(0), entry.getValue(), globals));
                FiguraMod.LOGGER.warn("Couldn't initialise global {}: ", entry.getKey(), e);
            }
        }
        root.addChild(globals);
        BaseDoc extensions = new BaseDoc("extended_globals", root);
        root.addChild(extensions);
        for(ClassDoc doc : classDocMap.values())
            doc.initFieldAndMethods();
    }

    public static LiteralArgumentBuilder<FabricClientCommandSource> getCommand() {
        return root.createCommand();
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

        public LiteralArgumentBuilder<FabricClientCommandSource> createCommand() {
            var cmd = literal(name);
            for (var child : children)
                cmd.then(child.createCommand());
            if (executes)
                cmd.executes(this);
            return cmd;
        }

        public void addChild(Doc child) {
            children.add(child);
        }

        @Override
        public int run(CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
            print();
            return 0;
        }

        protected MutableComponent getBullet(Object text){
            return FiguraText.of(BULLET_KEY, text);
        }

        protected MutableComponent getSyntax(){
            return Component.empty();
        }

        protected void print() {
            FiguraMod.sendChatMessage(FiguraText.of(
                BODY_KEY,
                HEADER,
                getBullet(FiguraText.of(getType())).withStyle(CHLOE_PURPLE.style),
                getBullet(getName()),
                getSyntax(),
                DESC,
                FiguraText.of("docs." + descriptionKey)
            ).withStyle(MAYA_BLUE.style));
        }

        protected Component getName() {
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
            if(!(b) && name.indexOf('_') != -1){
                return null;
            } else if (b) {
                return name.toLowerCase();
            }else {
                Matcher matcher;
                for(Pattern step : steps){
                    matcher = step.matcher(name);
                    while (matcher.find()){
                        name = matcher.replaceAll(matcher.group(1) + "_" + matcher.group(2).toLowerCase());
                        matcher = step.matcher(name);
                    }
                }
                return name;
            }
        }
    }

    static class BaseDoc extends Doc {

        BaseDoc(String name) {
            this.name = name;
            parent = null;
        }

        BaseDoc(String name, Doc parent) {
            this.parent = parent;
            this.name = name;
        }
    }

    static class GlobalDoc extends Doc {
        private final Doc type;

        public GlobalDoc(Field field, List<Class<?>> classes, Doc parent) {
            this.parent = parent;
            var fda = field.getDeclaredAnnotation(LuaTypeDoc.class);
            this.name = fda == null || fda.name().isEmpty()? toSnakeCase(field.getName()) : fda.name();
            this.type = classDocMap.get(field.getType());
            initSubs(classes);
        }

        public GlobalDoc(String name, Class<?> type, List<Class<?>> value, Doc parent) {
            this.parent = parent;
            this.name = name;
            this.type = classDocMap.get(type);
            initSubs(classes);
        }

        void initSubs(List<Class<?>> classes){
            for (Class<?> clas : classes) {
                if (clas.isAnnotationPresent(LuaTypeDoc.class)) {
                    ClassDoc doc;
                    if (classDocMap.containsKey(clas)) {
                        doc = classDocMap.get(clas);
                        doc.parent = this;
                    } else {
                        doc = new ClassDoc(clas, this);
                        classDocMap.put(clas, doc);
                    }
                    addChild(doc);
                }
            }
        }

        @Override
        public String getType() {
            return "docs.text.field";
        }
    }

    static class ClassDoc extends Doc {
        private final Class<?> clas;
        public ClassDoc superClassDoc;

        public ClassDoc(Class<?> clas, Doc parent) {
            this.clas = clas;
            this.name = clas.getAnnotation(LuaTypeDoc.class).name();
            this.descriptionKey = clas.getAnnotation(LuaTypeDoc.class).value();
            Class<?> t;
            if ((t = clas.getSuperclass()).isAnnotationPresent(LuaTypeDoc.class))
                if (classDocMap.containsKey(t))
                    superClassDoc = classDocMap.get(t);
                else {
                    ClassDoc doc = new ClassDoc(t, parent);
                    classDocMap.put(t, doc);
                    superClassDoc = doc;
                }
            this.parent = parent;
        }

        void initFieldAndMethods() {
            boolean wl = clas.getAnnotation(LuaTypeDoc.class).whitelist();
            for (Field field : clas.getDeclaredFields())
                if (field.isAnnotationPresent(LuaFieldDoc.class) ^ wl)
                    addChild(new FieldDoc(field, this));
            LuaTable index = runtime.typeManager.getIndexFor(clas);
            if(index != null)
                for (LuaFunction wrapper : Arrays.stream(index.keys()).map(index::rawget).toArray(LuaFunction[]::new))
                    addChild(new MethodDoc(wrapper, this));
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
            this.field = field;
            this.parent = parent;
            var fda = field.getDeclaredAnnotation(LuaTypeDoc.class);
            this.name = fda == null || fda.name().isEmpty()? field.getName() : fda.name();
            this.type = classDocMap.get(field.getType());
            descriptionKey = toSnakeCase(name);
            if(parent instanceof ClassDoc)
                descriptionKey = parent.descriptionKey + "." + descriptionKey;
            children = List.of();
        }

        @Override
        public String getType() {
            return "docs.text.field";
        }

        @Override
        protected Component getName() {
            return Component.literal(type.name).withStyle(YELLOW, UNDERLINE).append(" " + name);
        }
    }

    static class MethodDoc extends Doc {
        private final LuaFunction wrapper;

        public MethodDoc(LuaFunction wrapper, Doc parent) {
            this.wrapper = wrapper;
            this.parent = parent;
            this.name = wrapper.name();
            descriptionKey = toSnakeCase(name);
            if(parent instanceof ClassDoc)
                descriptionKey = parent.descriptionKey + "." + descriptionKey;
        }

        @Override
        protected MutableComponent getSyntax() {
            final String text;
            if(wrapper instanceof MethodWrapper figuraFunction){
                text = String.join("\n • ", figuraFunction.toString().split("\n"));
            } else {
                text = name + "(...)";
            }
            return Component.literal(text);
        }

        @Override
        protected String getType() {
            return "docs.text.function";
        }
    }
}
