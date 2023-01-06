package org.moon.figura.lua.api;

import org.moon.figura.animation.Animation;
import org.moon.figura.avatar.Avatar;
import org.moon.figura.lua.LuaWhitelist;
import org.moon.figura.lua.docs.LuaTypeDoc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@LuaWhitelist
@LuaTypeDoc(
        name = "AnimationAPI",
        value = "animations"
)
public class AnimationAPI {

    private final Map<String, Map<String, Animation>> animTable;
    private final Avatar avatar;

    public AnimationAPI(Avatar avatar) {
        this.avatar = avatar;
        animTable = generateAnimTable(avatar);
    }

    private static Map<String, Map<String, Animation>> generateAnimTable(Avatar avatar) {
        HashMap<String, Map<String, Animation>> root = new HashMap<>();
        for (Animation animation : avatar.animations.values()) {
            //get or create animation table
            Map<String, Animation> animations = root.get(animation.modelName);
            if (animations == null)
                animations = new HashMap<>();

            //put animation on the model table
            animations.put(animation.name, animation);
            root.put(animation.modelName, animations);
        }
        return root;
    }

    @LuaWhitelist
    public List<Animation> getAnimations() {
        List<Animation> list = new ArrayList<>();
        for (Map<String, Animation> value : animTable.values())
            list.addAll(value.values());
        return list;
    }

    @LuaWhitelist
    public List<Animation> getPlaying() {
        List<Animation> list = new ArrayList<>();
        for (Animation animation : avatar.animations.values())
            if (animation.playState == Animation.PlayState.PLAYING)
                list.add(animation);
        return list;
    }

    @LuaWhitelist
    public void stopAll() {
        for (Animation animation : avatar.animations.values())
            animation.stop();
    }

    @LuaWhitelist
    public Map<String, Animation> __index(String val) {
        return val == null ? null : animTable.get(val);
    }

    @Override
    public String toString() {
        return "AnimationsAPI";
    }
}
