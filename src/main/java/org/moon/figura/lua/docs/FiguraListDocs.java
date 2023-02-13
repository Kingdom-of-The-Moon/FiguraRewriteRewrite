package org.moon.figura.lua.docs;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.UseAnim;
import org.moon.figura.FiguraMod;
import org.moon.figura.animation.Animation;
import org.moon.figura.mixin.input.KeyMappingAccessor;
import org.moon.figura.mixin.render.GameRendererAccessor;
import org.moon.figura.model.ParentType;
import org.moon.figura.model.rendering.EntityRenderMode;
import org.moon.figura.model.rendering.texture.FiguraTextureSet;
import org.moon.figura.model.rendering.texture.RenderTypes;
import org.moon.figura.utils.ColorUtils;
import org.moon.figura.utils.FiguraText;

import java.util.*;
import java.util.function.Supplier;

/**
 * Adds docs for functions that have specific set of String names.
 */
public class FiguraListDocs {

    //-- types --//

    public static final LinkedHashSet<String> KEYBINDS = new LinkedHashSet<>();
    private static final LinkedHashMap<String, List<String>> PARENT_TYPES = new LinkedHashMap<>() {{
        for (ParentType value : ParentType.values())
            put(value.name(), Arrays.asList(value.aliases));
    }};
    private static final LinkedHashSet<String> RENDER_TYPES = new LinkedHashSet<>() {{
        for (RenderTypes value : RenderTypes.values())
            add(value.name());
    }};
    private static final LinkedHashSet<String> TEXTURE_TYPES = new LinkedHashSet<>() {{
        for (FiguraTextureSet.OverrideType value : FiguraTextureSet.OverrideType.values())
            add(value.name());
    }};
    private static final LinkedHashSet<String> ENTITY_POSES = new LinkedHashSet<>() {{
        for (Pose value : Pose.values())
            add(value.name());
    }};
    private static final LinkedHashSet<String> ITEM_DISPLAY_MODES = new LinkedHashSet<>() {{
        for (ItemTransforms.TransformType value : ItemTransforms.TransformType.values())
            add(value.name());
    }};
    private static final LinkedHashSet<String> POST_EFFECTS = new LinkedHashSet<>() {{
        for (ResourceLocation effect : GameRendererAccessor.getEffects()) {
            String[] split = effect.getPath().split("/");
            String name = split[split.length - 1];
            add(name.split("\\.")[0]);
        }
    }};
    private static final LinkedHashSet<String> PLAY_STATES = new LinkedHashSet<>() {{
        for (Animation.PlayState value : Animation.PlayState.values())
            add(value.name());
    }};
    private static final LinkedHashSet<String> LOOP_MODES = new LinkedHashSet<>() {{
        for (Animation.LoopMode value : Animation.LoopMode.values())
            add(value.name());
    }};
    private static final LinkedHashMap<String, List<String>> COLORS = new LinkedHashMap<>() {{
        for (ColorUtils.Colors value : ColorUtils.Colors.values())
            put(value.name(), Arrays.asList(value.alias));
    }};
    private static final LinkedHashSet<String> PLAYER_MODEL_PARTS = new LinkedHashSet<>() {{
        for (PlayerModelPart value : PlayerModelPart.values()) {
            String name = value.name();
            add(name.endsWith("_LEG") ? name.substring(0, name.length() - 4) : name);
        }
    }};
    private static final LinkedHashSet<String> USE_ACTIONS = new LinkedHashSet<>() {{
        for (UseAnim value : UseAnim.values())
            add(value.name());
    }};
    private static final LinkedHashSet<String> RENDER_MODES = new LinkedHashSet<>() {{
        for (EntityRenderMode value : EntityRenderMode.values())
            add(value.name());
    }};

    private enum ListDoc {
        KEYBINDS(() -> FiguraListDocs.KEYBINDS, "Keybinds", "keybinds"),
        PARENT_TYPES(() -> FiguraListDocs.PARENT_TYPES, "ParentTypes", "parent_types"),
        RENDER_TYPES(() -> FiguraListDocs.RENDER_TYPES, "RenderTypes", "render_types"),
        TEXTURE_TYPES(() -> FiguraListDocs.TEXTURE_TYPES, "TextureTypes", "texture_types"),
        KEY_IDS(() -> new LinkedHashSet<>() {{this.addAll(KeyMappingAccessor.getAll().keySet());}}, "KeyIDs", "key_ids"),
        ENTITY_POSES(() -> FiguraListDocs.ENTITY_POSES, "EntityPoses", "entity_poses"),
        ITEM_RENDER_TYPES(() -> FiguraListDocs.ITEM_DISPLAY_MODES, "ItemDisplayModes", "item_display_modes"),
        POST_EFFECTS(() -> FiguraListDocs.POST_EFFECTS, "PostEffects", "post_effects"),
        PLAY_STATES(() -> FiguraListDocs.PLAY_STATES, "PlayStates", "play_states"),
        LOOP_MODES(() -> FiguraListDocs.LOOP_MODES, "LoopModes", "loop_modes"),
        COLORS(() -> FiguraListDocs.COLORS, "Colors", "colors"),
        PLAYER_MODEL_PARTS(() -> FiguraListDocs.PLAYER_MODEL_PARTS, "PlayerModelParts", "player_model_parts"),
        USE_ACTIONS(() -> FiguraListDocs.USE_ACTIONS, "UseActions", "use_actions"),
        RENDER_MODES(() -> FiguraListDocs.RENDER_MODES, "RenderModes", "render_modes");

        private final Supplier<Object> supplier;
        private final String name, id;

        ListDoc(Supplier<Object> supplier, String name, String id) {
            this.supplier = supplier;
            this.name = name;
            this.id = id;
        }

        private Collection<?> get() {
            Object obj = supplier.get();
            if (obj instanceof LinkedHashSet<?> set)
                return set;
            else if (obj instanceof Map<?, ?> map)
                return map.entrySet();
            else
                throw new UnsupportedOperationException("Invalid object " + obj);
        }

        private JsonElement generateJson(boolean translate) {
            JsonObject object = new JsonObject();

            //list properties
            object.addProperty("name", name);
            object.addProperty("description", translate ? Language.getInstance().getOrDefault(FiguraText.of("docs.enum." + id).getString()) : FiguraMod.MOD_ID + "." + "docs.enum." + id);

            //list entries
            Collection<?> coll = get();
            if (coll.size() == 0)
                return object;

            JsonArray entries = new JsonArray();
            for (Object o : coll) {
                //noinspection rawtypes
                if (o instanceof Map.Entry e) {
                    entries.add(e.getKey().toString());
                    //noinspection unchecked
                    for (String s : (List<String>) e.getValue())
                        entries.add(s);
                } else {
                    entries.add(o.toString());
                }
            }

            object.add("entries", entries);
            return object;
        }

        private LiteralArgumentBuilder<FabricClientCommandSource> generateCommand() {
            //command
            LiteralArgumentBuilder<FabricClientCommandSource> command = LiteralArgumentBuilder.literal(id);

            //display everything
            
            //add collection as child for easy navigation
            Collection<?> coll = get();
            for (Object o : coll) {
                String text = o instanceof Map.Entry e ? e.getKey().toString() : o.toString();
                LiteralArgumentBuilder<FabricClientCommandSource> entry = LiteralArgumentBuilder.literal(text);
                entry.executes(context -> {
                    FiguraMod.sendChatMessage(Component.literal(text).withStyle(ColorUtils.Colors.FRAN_PINK.style));
                    return 1;
                });

                if (o instanceof Map.Entry e) {
                    for (String s : (List<String>) e.getValue()) {
                        LiteralArgumentBuilder<FabricClientCommandSource> child = LiteralArgumentBuilder.literal(s);
                        child.executes(context -> {
                            FiguraMod.sendChatMessage(Component.literal(s).withStyle(ColorUtils.Colors.FRAN_PINK.style));
                            return 1;
                        });
                        entry.then(child);
                    }
                }

                command.then(entry);
            }

            //return
            return command;
        }
    }

    // -- doc methods -- //

    public static LiteralArgumentBuilder<FabricClientCommandSource> getCommand() {
        //self
        LiteralArgumentBuilder<FabricClientCommandSource> root = LiteralArgumentBuilder.literal("enums");
        
        for (ListDoc value : ListDoc.values())
            root.then(value.generateCommand());

        return root;
    }

    public static JsonElement toJson(boolean translate) {
        JsonArray array = new JsonArray();
        for (ListDoc value : ListDoc.values())
            array.add(value.generateJson(translate));
        return array;
    }
}
