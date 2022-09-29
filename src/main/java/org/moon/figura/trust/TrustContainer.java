package org.moon.figura.trust;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import org.moon.figura.lua.FiguraAPIManager;
import org.moon.figura.utils.ColorUtils;
import org.moon.figura.utils.FiguraText;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TrustContainer {

    //fields :p
    public String name;
    private ResourceLocation parentID;
    public boolean visible = true; //used on UI

    //trust -> value map
    private final Map<Trust, Integer> trustSettings;

    private final Map<String, List<? extends TrustOption>> customTrustOptions;

    //the trust themselves
    public enum Trust {
        //trust list
        INIT_INST(0, 32767),
        WORLD_TICK_INST(0, 32767),
        TICK_INST(0, 32767),
        WORLD_RENDER_INST(0, 32767),
        RENDER_INST(0, 32767),
        COMPLEXITY(0, 8191),
        PARTICLES(0, 63),
        SOUNDS(0, 63),
        VOLUME(0, 99),
        BB_ANIMATIONS(0, 255),
        VANILLA_MODEL_EDIT,
        NAMEPLATE_EDIT,
        OFFSCREEN_RENDERING,
        //CUSTOM_RENDER_LAYER,
        CUSTOM_SOUNDS,
        CUSTOM_HEADS;

        //toggle check
        public final boolean isToggle;

        //used only for sliders
        public final Integer min;
        public final Integer max;

        //toggle constructor
        Trust() {
            this(null, null);
        }

        //slider constructor
        Trust(Integer min, Integer max) {
            this.isToggle = min == null || max == null;
            this.min = min;
            this.max = max;
        }

        //infinity check :p
        public boolean checkInfinity(int value) {
            return max != null && value > max && this != VOLUME;
        }

        //transform to boolean
        public boolean asBoolean(int value) {
            return value >= 1;
        }
    }

    // constructors //

    public TrustContainer(String name, ResourceLocation parentID, CompoundTag nbt) {
        this.name = name;
        this.parentID = parentID;

        this.trustSettings = new HashMap<>();
        this.customTrustOptions = new HashMap<>();
        setTrustFromNbt(nbt);
    }

    public TrustContainer(String name, ResourceLocation parentID, Map<Trust, Integer> trust) {
        this.name = name;
        this.parentID = parentID;
        this.trustSettings = new HashMap<>(trust);
        this.customTrustOptions = new HashMap<>();
        TrustContainer parentContainer = getParentGroup();
        TrustManager.CUSTOM_TRUST_OPTIONS.forEach((optsId, tco) -> {
            List<TrustOption> optionMap = new LinkedList<>();
            tco.getTrustOptions().forEach(opt -> {
                optionMap.add(opt.getNewInstance(
                        parentContainer == this ?
                                opt.defaultValue() :
                                parentContainer.getCustom(optsId, opt.getName())
                ));
            });
            customTrustOptions.put(optsId, optionMap);
        });
    }

    // functions //

    //read nbt
    private void setTrustFromNbt(CompoundTag nbt) {
        for (Trust setting : Trust.values()) {
            String trustName = setting.name();

            if (nbt.contains(trustName))
                trustSettings.put(setting, nbt.getInt(trustName));
        }
        if (nbt.contains("customSettings")) {
            CompoundTag customSettings = nbt.getCompound("customSettings");
            TrustManager.CUSTOM_TRUST_OPTIONS.forEach((optionsId, options) -> {
                List<TrustOption> optionList = new LinkedList<>();
                if (customSettings.contains(optionsId)) {
                    CompoundTag settingsSection = customSettings.getCompound(optionsId);
                    options.getTrustOptions().forEach(opt -> {
                        if (settingsSection.contains(opt.getName())) {
                            if (opt instanceof TrustOption.Range r) {
                                optionList.add(r.getNewInstance(settingsSection.getInt(r.getName())));
                            } else if (opt instanceof TrustOption.Toggle t) {
                                optionList.add(t.getNewInstance(settingsSection.getBoolean(t.getName())));
                            }
                        }
                        else {
                            optionList.add(opt.getNewInstance(opt.defaultValue()));
                        }
                    });
                }
                else {
                    options.getTrustOptions().forEach(opt -> {
                        optionList.add(opt.getNewInstance(opt.defaultValue()));
                    });
                }
                customTrustOptions.put(optionsId, optionList);
            });
        }
        else {
            for (Map.Entry<String, TrustCustomOptions> optionsEntry:
                    TrustManager.CUSTOM_TRUST_OPTIONS.entrySet()) {
                List<TrustOption> optionList = new LinkedList<>();
                TrustCustomOptions tco = optionsEntry.getValue();
                for (TrustOption optionEntry:
                        tco.getTrustOptions()) {
                    TrustOption opt = optionEntry;
                    optionList.add(opt.getNewInstance(opt.defaultValue()));
                }
                customTrustOptions.put(tco.getCustomTrustOptionsId(), optionList);
            }
        }
    }

    //write nbt
    public void writeNbt(CompoundTag nbt) {
        //container properties
        nbt.put("name", StringTag.valueOf(this.name));

        if (this.parentID != null)
            nbt.put("parent", StringTag.valueOf(this.parentID.toString()));

        //trust values
        CompoundTag trust = new CompoundTag();
        this.trustSettings.forEach((key, value) -> trust.put(key.name(), IntTag.valueOf(value)));
        CompoundTag customSettings = new CompoundTag();
        this.customTrustOptions.forEach((key, value) -> {
            CompoundTag settingsSection = new CompoundTag();
            value.forEach(ov -> {
                Tag t;
                if (!ov.value.equals(ov.defaultValue())) {
                    if (ov instanceof TrustOption.Range r) {
                        t = IntTag.valueOf(r.value);
                    } else {
                        TrustOption.Toggle tg = (TrustOption.Toggle) ov;
                        t = ByteTag.valueOf(tg.value);
                    }
                    settingsSection.put(ov.getName(), t);
                }
            });
            if (!settingsSection.isEmpty()) customSettings.put(key, settingsSection);
        });
        if(!customSettings.isEmpty()) trust.put("customSettings", customSettings);
        //add to nbt
        nbt.put("trust", trust);
    }

    //get value from trust
    public int get(Trust trust) {
        //get setting
        Integer setting = this.trustSettings.get(trust);
        if (setting != null)
            return setting;

        //if not, then get from parent
        if (parentID != null && TrustManager.get(parentID) != null)
            return TrustManager.get(parentID).get(trust);

        //if no trust found, return -1
        return -1;
    }

    public Object getCustom(String id, String name) {
        for (TrustOption opt:
                customTrustOptions.get(id)) {
            if (opt.getName().equals(name)) return opt.value;
        }
        return null;
    }

    private boolean allCustomDefaults() {
        for (Map.Entry<String, List<? extends TrustOption>> opts:
             customTrustOptions.entrySet()) {
            for (TrustOption opt:
                 opts.getValue()) {
                if (opt.value != opt.defaultValue()) return true;
            }
        }
        return false;
    }

    public MutableComponent getGroupName() {
        return getGroupName(false);
    }

    private MutableComponent getGroupName(boolean changed) {
        if (parentID != null)
            return TrustManager.get(parentID).getGroupName(changed || !getSettings().isEmpty() || allCustomDefaults());

        MutableComponent text = FiguraText.of("trust.group." + name).withStyle(Style.EMPTY.withColor(getGroupColor()));
        if (changed)
            text.append("*");

        return text;
    }

    public int getGroupColor() {
        if (parentID != null)
            return TrustManager.get(parentID).getGroupColor();

        return switch (name) {
            case "blocked" -> ChatFormatting.RED.getColor();
            //case "untrusted" -> ChatFormatting.YELLOW.getColorValue();
            case "trusted" -> ChatFormatting.GREEN.getColor();
            case "friend" -> ColorUtils.Colors.FRAN_PINK.hex;
            case "local" -> ChatFormatting.AQUA.getColor();
            default -> ChatFormatting.WHITE.getColor();
        };
    }

    public TrustContainer getParentGroup() {
        return parentID == null || !parentID.getNamespace().equals("group") ? this : TrustManager.get(parentID).getParentGroup();
    }

    // getters //

    public Map<Trust, Integer> getSettings() {
        return this.trustSettings;
    }

    public Map<String, List<? extends TrustOption>> getCustomSettings() {return this.customTrustOptions;}

    public ResourceLocation getParentID() {
        return this.parentID;
    }

    // setters //

    public void setParent(ResourceLocation parent) {
        this.parentID = parent;
    }
}
