package org.moon.figura.lua.api.action_wheel;

import org.luaj.vm2.LuaError;
import org.moon.figura.lua.LuaNotNil;
import org.moon.figura.lua.LuaWhitelist;
import org.moon.figura.lua.docs.LuaMethodDoc;
import org.moon.figura.lua.docs.LuaTypeDoc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@LuaWhitelist
@LuaTypeDoc(
        name = "Page",
        value = "wheel_page"
)
public class Page {

    private final String title;

    private final HashMap<Integer, Action> actionsMap = new HashMap<>();

    private int slotsShift = 0;

    @LuaWhitelist
    public boolean keepSlots = false;

    public Page(String title) {
        this.title = title;
    }

    public int getSize() {
        Action[] actions = slots();
        int i = actions.length;
        while (i > 0 && actions[i - 1] == null) {
            i--;
        }
        return Math.max(i, 2);
    }

    public int getGreatestSlot() {
        int greatest = 0;
        for (Integer i : actionsMap.keySet())
            greatest = Math.max(greatest, i);
        return greatest;
    }

    public int getGroupCount() {
        return getGreatestSlot() / 8 + 1;
    }

    public Action[] slots() {
        return slots(slotsShift);
    }

    public Action[] slots(int shift) {
        Action[] page = new Action[8];
        for (int i = 0; i < 8; i++) {
            page[i] = actionsMap.get(i + 8 * shift);
        }
        return page;
    }

    private int checkIndex(Integer index) {
        //check and fix index
        if (index != null) {
            if (index < 1)
                throw new LuaError("Index must be greater than 0!");

            return index - 1;
        }

        //if no index is given, get the first null slot
        int i = 0;
        while (actionsMap.get(i) != null)
            i++;

        return i;
    }


    // -- lua stuff -- //


    @LuaWhitelist
    public boolean shouldKeepSlots() {
        return keepSlots;
    }

    @LuaWhitelist
    public Page setKeepSlots(boolean bool) {
        keepSlots = bool;
        return this;
    }

    @LuaWhitelist
    public String getTitle() {
        return title;
    }

    @LuaWhitelist
    public Action newAction(Integer index) {
        //set the action
        Action action = new Action();
        this.actionsMap.put(checkIndex(index), action);

        //return the action
        return action;
    }

    @LuaWhitelist
    public Action getAction(int index) {
        if (index < 1)
            throw new LuaError("Index must be greater than 0!");
        return this.actionsMap.get(index - 1);
    }

    @LuaWhitelist
    @LuaMethodDoc("action")
    public Page setAction(Integer index, Action action) {
        if (index == null || index == -1)
            //"why -1 is accepted" you might say
            //because -1 is more elegant for this, as it will return the latest available index
            //same as how lua substring works, but not exactly
            index = this.checkIndex(null) + 1;
        if (index < 1)
            throw new LuaError("Index must be greater than 0!");
        this.actionsMap.put(index - 1, action);
        return this;
    }

    @LuaWhitelist
    public int getSlotsShift() {
        return this.slotsShift + 1;
    }

    @LuaWhitelist
    @LuaMethodDoc("slotsShift")
    public Page setSlotsShift(int shift) {
        slotsShift = Math.min(Math.max(shift - 1, 0), getGroupCount() - 1);
        return this;
    }
    
    @LuaWhitelist
    public HashMap<Integer, Action> getActions(){
        HashMap<Integer, Action> map = new HashMap<>();
        for (Map.Entry<Integer, Action> entry : actionsMap.entrySet())
            map.put(entry.getKey() + 1, entry.getValue());
        return map;
    }

    @LuaWhitelist
    public Object getActions(int shift) {
        if (shift < 1)
            throw new LuaError("Shift must be greater than 0!");
        return Arrays.asList(slots(shift - 1));
    }

    @LuaWhitelist
    public Object __index(String arg) {
        return "keepSlots".equals(arg) ? keepSlots : null;
    }

    @LuaWhitelist
    public void __newindex(@LuaNotNil String key, boolean value) {
        if ("keepSlots".equals(key))
            keepSlots = value;
        else throw new LuaError("Cannot assign value on key \"" + key + "\"");
    }

    @Override
    public String toString() {
        return title != null ? title + " (Action Wheel Page)" : "Action Wheel Page";
    }
}
