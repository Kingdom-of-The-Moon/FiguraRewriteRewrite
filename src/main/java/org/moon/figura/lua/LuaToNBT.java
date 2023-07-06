package org.moon.figura.lua;

import net.minecraft.nbt.CompoundTag;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import java.util.Objects;

public class LuaToNBT {
    public static CompoundTag convert(LuaTable table) {
        LuaValue[] keys = table.keys();
        CompoundTag nbt = new CompoundTag();
        for (LuaValue key : keys) {
            if (Objects.equals(key.typename(), "string")) {
                String stringKey = key.toString();
                LuaValue value = table.get(key);
                switch (value.type()) {
                    case LuaValue.TBOOLEAN:
                        nbt.putBoolean(stringKey, value.toboolean());
                    case LuaValue.TINT:
                        nbt.putInt(stringKey, value.toint());
                    case LuaValue.TNUMBER:
                        nbt.putDouble(stringKey, value.todouble());
                    case LuaValue.TTABLE:
                        nbt.put(stringKey, convert(value.checktable()));
                    case LuaValue.TSTRING:
                        nbt.putString(stringKey, value.toString());
                }
            }
        }
        return nbt;
    }
}
