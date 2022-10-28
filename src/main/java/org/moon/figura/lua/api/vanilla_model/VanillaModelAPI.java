package org.moon.figura.lua.api.vanilla_model;

import org.moon.figura.model.ParentType;
import org.moon.figura.model.VanillaModelProvider;
import org.moon.figura.lua.LuaWhitelist;
import org.moon.figura.lua.docs.LuaFieldDoc;
import org.moon.figura.lua.docs.LuaTypeDoc;

@LuaWhitelist
@LuaTypeDoc(
        name = "VanillaModelAPI",
        value = "vanilla_model"
)
public class VanillaModelAPI {

    // -- body -- //

    @LuaWhitelist
    public final VanillaModelPart HEAD;
    @LuaWhitelist
    public final VanillaModelPart BODY;
    @LuaWhitelist
    public final VanillaModelPart LEFT_ARM;
    @LuaWhitelist
    public final VanillaModelPart RIGHT_ARM;
    @LuaWhitelist
    public final VanillaModelPart LEFT_LEG;
    @LuaWhitelist
    public final VanillaModelPart RIGHT_LEG;

    @LuaWhitelist
    public final VanillaModelPart HAT;
    @LuaWhitelist
    public final VanillaModelPart JACKET;
    @LuaWhitelist
    public final VanillaModelPart LEFT_SLEEVE;
    @LuaWhitelist
    public final VanillaModelPart RIGHT_SLEEVE;
    @LuaWhitelist
    public final VanillaModelPart LEFT_PANTS;
    @LuaWhitelist
    public final VanillaModelPart RIGHT_PANTS;

    // -- cape -- //

    @LuaWhitelist
    public final VanillaModelPart CAPE_MODEL;
    @LuaWhitelist
    public final VanillaModelPart FAKE_CAPE;

    // -- armor -- //

    @LuaWhitelist
    public final VanillaModelPart HELMET_ITEM;

    @LuaWhitelist
    public final VanillaModelPart HELMET_HEAD;
    @LuaWhitelist
    public final VanillaModelPart HELMET_HAT;

    @LuaWhitelist
    public final VanillaModelPart CHESTPLATE_BODY;
    @LuaWhitelist
    public final VanillaModelPart CHESTPLATE_LEFT_ARM;
    @LuaWhitelist
    public final VanillaModelPart CHESTPLATE_RIGHT_ARM;

    @LuaWhitelist
    public final VanillaModelPart LEGGINGS_BODY;
    @LuaWhitelist
    public final VanillaModelPart LEGGINGS_LEFT_LEG;
    @LuaWhitelist
    public final VanillaModelPart LEGGINGS_RIGHT_LEG;

    @LuaWhitelist
    public final VanillaModelPart BOOTS_LEFT_LEG;
    @LuaWhitelist
    public final VanillaModelPart BOOTS_RIGHT_LEG;

    // -- elytra -- //

    @LuaWhitelist
    public final VanillaModelPart LEFT_ELYTRA;
    @LuaWhitelist
    public final VanillaModelPart RIGHT_ELYTRA;

    // -- held items -- //

    @LuaWhitelist
    public final VanillaModelPart LEFT_ITEM;
    @LuaWhitelist
    public final VanillaModelPart RIGHT_ITEM;

    // -- parrots -- //

    @LuaWhitelist
    public final VanillaModelPart LEFT_PARROT;
    @LuaWhitelist
    public final VanillaModelPart RIGHT_PARROT;


    // -- groups -- //


    @LuaWhitelist
    public final VanillaGroupPart ALL;

    @LuaWhitelist
    public final VanillaGroupPart PLAYER;
    @LuaWhitelist
    public final VanillaGroupPart OUTER_LAYER;
    @LuaWhitelist
    public final VanillaGroupPart INNER_LAYER;

    @LuaWhitelist
    public final VanillaGroupPart CAPE;

    @LuaWhitelist
    public final VanillaGroupPart ARMOR;
    @LuaWhitelist
    public final VanillaGroupPart HELMET;
    @LuaWhitelist
    public final VanillaGroupPart CHESTPLATE;
    @LuaWhitelist
    public final VanillaGroupPart LEGGINGS;
    @LuaWhitelist
    public final VanillaGroupPart BOOTS;

    @LuaWhitelist
    public final VanillaGroupPart ELYTRA;

    @LuaWhitelist
    public final VanillaGroupPart HELD_ITEMS;

    @LuaWhitelist
    public final VanillaGroupPart PARROTS;

    public VanillaModelAPI() {
        // -- body -- //

        HEAD = new VanillaModelPart("HEAD", ParentType.Head, VanillaModelProvider.HEAD.func);
        BODY = new VanillaModelPart("BODY", ParentType.Body, VanillaModelProvider.BODY.func);
        LEFT_ARM = new VanillaModelPart("LEFT_ARM", ParentType.LeftArm, VanillaModelProvider.LEFT_ARM.func);
        RIGHT_ARM = new VanillaModelPart("RIGHT_ARM", ParentType.RightArm, VanillaModelProvider.RIGHT_ARM.func);
        LEFT_LEG = new VanillaModelPart("LEFT_LEG", ParentType.LeftLeg, VanillaModelProvider.LEFT_LEG.func);
        RIGHT_LEG = new VanillaModelPart("RIGHT_LEG", ParentType.RightLeg, VanillaModelProvider.RIGHT_LEG.func);

        HAT = new VanillaModelPart("HAT", ParentType.Head, VanillaModelProvider.HAT.func);
        JACKET = new VanillaModelPart("JACKET", ParentType.Body, VanillaModelProvider.JACKET.func);
        LEFT_SLEEVE = new VanillaModelPart("LEFT_SLEEVE", ParentType.LeftArm, VanillaModelProvider.LEFT_SLEEVE.func);
        RIGHT_SLEEVE = new VanillaModelPart("RIGHT_SLEEVE", ParentType.RightArm, VanillaModelProvider.RIGHT_SLEEVE.func);
        LEFT_PANTS = new VanillaModelPart("LEFT_PANTS", ParentType.LeftLeg, VanillaModelProvider.LEFT_PANTS.func);
        RIGHT_PANTS = new VanillaModelPart("RIGHT_PANTS", ParentType.RightLeg, VanillaModelProvider.RIGHT_PANTS.func);

        // -- cape -- //

        CAPE_MODEL = new VanillaModelPart("CAPE_MODEL", ParentType.Cape, VanillaModelProvider.CAPE.func);
        FAKE_CAPE = new VanillaModelPart("FAKE_CAPE", ParentType.Cape, VanillaModelProvider.FAKE_CAPE.func);

        // -- armor -- //

        HELMET_ITEM = new VanillaModelPart("HELMET_ITEM", ParentType.Head, null);

        HELMET_HEAD = new VanillaModelPart("HELMET_HEAD", ParentType.Head, VanillaModelProvider.HEAD.func);
        HELMET_HAT = new VanillaModelPart("HELMET_HAT", ParentType.Head, VanillaModelProvider.HAT.func);

        CHESTPLATE_BODY = new VanillaModelPart("CHESTPLATE_BODY", ParentType.Body, VanillaModelProvider.BODY.func);
        CHESTPLATE_LEFT_ARM = new VanillaModelPart("CHESTPLATE_LEFT_ARM", ParentType.LeftArm, VanillaModelProvider.LEFT_ARM.func);
        CHESTPLATE_RIGHT_ARM = new VanillaModelPart("CHESTPLATE_RIGHT_ARM", ParentType.RightArm, VanillaModelProvider.RIGHT_ARM.func);

        LEGGINGS_BODY = new VanillaModelPart("LEGGINGS_BODY", ParentType.Body, VanillaModelProvider.BODY.func);
        LEGGINGS_LEFT_LEG = new VanillaModelPart("LEGGINGS_LEFT_LEG", ParentType.LeftLeg, VanillaModelProvider.LEFT_LEG.func);
        LEGGINGS_RIGHT_LEG = new VanillaModelPart("LEGGINGS_RIGHT_LEG", ParentType.RightLeg, VanillaModelProvider.RIGHT_LEG.func);

        BOOTS_LEFT_LEG = new VanillaModelPart("BOOTS_LEFT_LEG", ParentType.LeftLeg, VanillaModelProvider.LEFT_LEG.func);
        BOOTS_RIGHT_LEG = new VanillaModelPart("BOOTS_RIGHT_LEG", ParentType.RightLeg, VanillaModelProvider.RIGHT_LEG.func);

        // -- elytra -- //

        LEFT_ELYTRA = new VanillaModelPart("LEFT_ELYTRA", ParentType.LeftElytra, VanillaModelProvider.LEFT_ELYTRON.func);
        RIGHT_ELYTRA = new VanillaModelPart("RIGHT_ELYTRA", ParentType.RightElytra, VanillaModelProvider.RIGHT_ELYTRON.func);

        // -- held items -- //

        LEFT_ITEM = new VanillaModelPart("LEFT_ITEM", ParentType.LeftArm, null);
        RIGHT_ITEM = new VanillaModelPart("RIGHT_ITEM", ParentType.RightArm, null);

        // -- parrots -- //

        LEFT_PARROT = new VanillaModelPart("LEFT_PARROT", ParentType.Body, null);
        RIGHT_PARROT = new VanillaModelPart("RIGHT_PARROT", ParentType.Body, null);


        // -- groups -- //


        INNER_LAYER = new VanillaGroupPart("INNER_LAYER", HEAD, BODY, LEFT_ARM, RIGHT_ARM, LEFT_LEG, RIGHT_LEG);
        OUTER_LAYER = new VanillaGroupPart("OUTER_LAYER", HAT, JACKET, LEFT_SLEEVE, RIGHT_SLEEVE, LEFT_PANTS, RIGHT_PANTS);
        PLAYER = new VanillaGroupPart("PLAYER", INNER_LAYER, OUTER_LAYER);

        CAPE = new VanillaGroupPart("CAPE", CAPE_MODEL, FAKE_CAPE);

        HELMET = new VanillaGroupPart("HELMET", HELMET_ITEM, HELMET_HEAD, HELMET_HAT);
        CHESTPLATE = new VanillaGroupPart("CHESTPLATE", CHESTPLATE_BODY, CHESTPLATE_LEFT_ARM, CHESTPLATE_RIGHT_ARM);
        LEGGINGS = new VanillaGroupPart("LEGGINGS", LEGGINGS_BODY, LEGGINGS_LEFT_LEG, LEGGINGS_RIGHT_LEG);
        BOOTS = new VanillaGroupPart("BOOTS", BOOTS_LEFT_LEG, BOOTS_RIGHT_LEG);
        ARMOR = new VanillaGroupPart("ARMOR", HELMET, CHESTPLATE, LEGGINGS, BOOTS);

        ELYTRA = new VanillaGroupPart("ELYTRA", LEFT_ELYTRA, RIGHT_ELYTRA);

        HELD_ITEMS = new VanillaGroupPart("HELD_ITEMS", LEFT_ITEM, RIGHT_ITEM);

        PARROTS = new VanillaGroupPart("PARROTS", LEFT_PARROT, RIGHT_PARROT);

        ALL = new VanillaGroupPart("ALL", PLAYER, CAPE, ARMOR, ELYTRA, HELD_ITEMS, PARROTS);
    }

    @LuaWhitelist
    public Object __index(String key) {
        if (key == null) return null;
        return switch (key) {
            case "HEAD" -> HEAD;
            case "BODY" -> BODY;
            case "LEFT_ARM" -> LEFT_ARM;
            case "RIGHT_ARM" -> RIGHT_ARM;
            case "LEFT_LEG" -> LEFT_LEG;
            case "RIGHT_LEG" -> RIGHT_LEG;
            case "HAT" -> HAT;
            case "JACKET" -> JACKET;
            case "LEFT_SLEEVE" -> LEFT_SLEEVE;
            case "RIGHT_SLEEVE" -> RIGHT_SLEEVE;
            case "LEFT_PANTS" -> LEFT_PANTS;
            case "RIGHT_PANTS" -> RIGHT_PANTS;
            case "CAPE_MODEL" -> CAPE_MODEL;
            case "FAKE_CAPE" -> FAKE_CAPE;
            case "HELMET_ITEM" -> HELMET_ITEM;
            case "HELMET_HEAD" -> HELMET_HEAD;
            case "HELMET_HAT" -> HELMET_HAT;
            case "CHESTPLATE_BODY" -> CHESTPLATE_BODY;
            case "CHESTPLATE_LEFT_ARM" -> CHESTPLATE_LEFT_ARM;
            case "CHESTPLATE_RIGHT_ARM" -> CHESTPLATE_RIGHT_ARM;
            case "LEGGINGS_BODY" -> LEGGINGS_BODY;
            case "LEGGINGS_LEFT_LEG" -> LEGGINGS_LEFT_LEG;
            case "LEGGINGS_RIGHT_LEG" -> LEGGINGS_RIGHT_LEG;
            case "BOOTS_LEFT_LEG" -> BOOTS_LEFT_LEG;
            case "BOOTS_RIGHT_LEG" -> BOOTS_RIGHT_LEG;
            case "LEFT_ELYTRA" -> LEFT_ELYTRA;
            case "RIGHT_ELYTRA" -> RIGHT_ELYTRA;
            case "LEFT_ITEM" -> LEFT_ITEM;
            case "RIGHT_ITEM" -> RIGHT_ITEM;
            case "LEFT_PARROT" -> LEFT_PARROT;
            case "RIGHT_PARROT" -> RIGHT_PARROT;
            case "INNER_LAYER" -> INNER_LAYER;
            case "OUTER_LAYER" -> OUTER_LAYER;
            case "PLAYER" -> PLAYER;
            case "CAPE" -> CAPE;
            case "HELMET" -> HELMET;
            case "CHESTPLATE" -> CHESTPLATE;
            case "LEGGINGS" -> LEGGINGS;
            case "BOOTS" -> BOOTS;
            case "ARMOR" -> ARMOR;
            case "ELYTRA" -> ELYTRA;
            case "HELD_ITEMS" -> HELD_ITEMS;
            case "PARROTS" -> PARROTS;
            case "ALL" -> ALL;
            default -> null;
        };
    }

    @Override
    public String toString() {
        return "VanillaModelAPI";
    }
}
