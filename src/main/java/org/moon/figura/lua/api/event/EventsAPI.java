package org.moon.figura.lua.api.event;

import org.luaj.vm2.LuaFunction;
import org.moon.figura.lua.LuaWhitelist;
import org.moon.figura.lua.docs.LuaMetamethodDoc;
import org.moon.figura.lua.docs.LuaMetamethodDoc.LuaMetamethodOverload;
import org.moon.figura.lua.docs.LuaTypeDoc;

import java.util.List;

@LuaWhitelist
@LuaTypeDoc(
        name = "EventsAPI",
        value = "events"
)
public class EventsAPI {

    //Unsure on how to do the docs for these fields. Maybe we keep the @LuaFieldDoc, just don't allow them to be
    //whitelisted and accessed automatically?
    //Maybe in the __index comment we give a docs list of the events?

    @LuaWhitelist
    public final LuaEvent ENTITY_INIT = new LuaEvent();
    @LuaWhitelist
    public final LuaEvent TICK = new LuaEvent();
    @LuaWhitelist
    public final LuaEvent WORLD_TICK = new LuaEvent();
    @LuaWhitelist
    public final LuaEvent RENDER = new LuaEvent();
    @LuaWhitelist
    public final LuaEvent POST_RENDER = new LuaEvent();
    @LuaWhitelist
    public final LuaEvent WORLD_RENDER = new LuaEvent();
    @LuaWhitelist
    public final LuaEvent POST_WORLD_RENDER = new LuaEvent();
    @LuaWhitelist
    public final LuaEvent CHAT_SEND_MESSAGE = new LuaEvent(true);
    @LuaWhitelist
    public final LuaEvent CHAT_RECEIVE_MESSAGE = new LuaEvent();
    @LuaWhitelist
    public final LuaEvent SKULL_RENDER = new LuaEvent();
    @LuaWhitelist
    public final LuaEvent MOUSE_SCROLL = new LuaEvent();
    @LuaWhitelist
    public final LuaEvent MOUSE_MOVE = new LuaEvent();
    @LuaWhitelist
    public final LuaEvent KEY_PRESS = new LuaEvent();
    @LuaWhitelist
    public final LuaEvent MOUSE_PRESS = new LuaEvent();
    @LuaWhitelist
    public final LuaEvent USE_ITEM = new LuaEvent();
    @LuaWhitelist
    public List<LuaEvent> getEvents() {
        return List.of(
                ENTITY_INIT,
                TICK,
                WORLD_TICK,
                RENDER,
                POST_RENDER,
                WORLD_RENDER,
                POST_WORLD_RENDER,
                CHAT_SEND_MESSAGE,
                CHAT_RECEIVE_MESSAGE,
                SKULL_RENDER,
                MOUSE_SCROLL,
                MOUSE_MOVE,
                MOUSE_PRESS,
                KEY_PRESS,
                USE_ITEM
        );
    }

    @LuaWhitelist
    @LuaMetamethodDoc(overloads = @LuaMetamethodOverload(
            types = {LuaEvent.class, EventsAPI.class, String.class},
            comment = "events.__index.comment1"
    ))
    public LuaEvent __index(String key) {
        if (key == null) return null;
        return switch (key) {
            case "ENTITY_INIT" -> ENTITY_INIT;
            case "TICK" -> TICK;
            case "WORLD_TICK" -> WORLD_TICK;
            case "RENDER" -> RENDER;
            case "POST_RENDER" -> POST_RENDER;
            case "WORLD_RENDER" -> WORLD_RENDER;
            case "POST_WORLD_RENDER" -> POST_WORLD_RENDER;
            case "CHAT_SEND_MESSAGE" -> CHAT_SEND_MESSAGE;
            case "CHAT_RECEIVE_MESSAGE" -> CHAT_RECEIVE_MESSAGE;
            case "SKULL_RENDER" -> SKULL_RENDER;
            case "MOUSE_SCROLL" -> MOUSE_SCROLL;
            case "MOUSE_MOVE" -> MOUSE_MOVE;
            case "MOUSE_PRESS" -> MOUSE_PRESS;
            case "KEY_PRESS" -> KEY_PRESS;
            case "USE_ITEM" -> USE_ITEM;
            default -> null;
        };
    }

    @LuaWhitelist
    public void __newindex(String key, LuaFunction func) {
        if (key == null)
            return;

        LuaEvent event = __index(key.toUpperCase());
        if (event != null)
            event.register(func, null);
    }

    @Override
    public String toString() {
        return "EventsAPI";
    }
}
