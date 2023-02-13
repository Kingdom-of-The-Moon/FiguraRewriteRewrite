package org.moon.figura.lua;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

public class ReadOnlyLuaTable extends LuaTable {

    public ReadOnlyLuaTable(LuaValue table) {
        presize(table.length(), 0);
        for (Varargs n = table.next(LuaValue.NIL); !n.arg1().isnil(); n = table
                .next(n.arg1())) {
            LuaValue key = n.arg1();
            LuaValue value = n.arg(2);
            super.rawset(key, value.istable() ? value == table ? this : new ReadOnlyLuaTable(value) : value);
        }
    }

    private LuaValue err() {
        return error("table is read-only");
    }

    public LuaValue setmetatable(LuaValue metatable) {
        return err();
    }

    public void set(int key, LuaValue value) {
        err();
    }

    public void rawset(int key, LuaValue value) {
        err();
    }

    public void rawset(LuaValue key, LuaValue value) {
        err();
    }

    public LuaValue remove(int pos) {
        return err();
    }
}
