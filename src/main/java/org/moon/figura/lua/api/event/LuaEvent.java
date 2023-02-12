package org.moon.figura.lua.api.event;

import com.google.common.collect.HashMultimap;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.Varargs;
import org.moon.figura.FiguraMod;
import org.moon.figura.lua.LuaNotNil;
import org.moon.figura.lua.LuaWhitelist;
import org.moon.figura.lua.docs.LuaMetamethodDoc;
import org.moon.figura.lua.docs.LuaMetamethodDoc.LuaMetamethodOverload;
import org.moon.figura.lua.docs.LuaTypeDoc;

import java.util.Deque;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedDeque;

@LuaWhitelist
@LuaTypeDoc(
        name = "Event",
        value = "event"
)
public class LuaEvent {

    private static final int MAX_FUNCTIONS = 1024;

    private final boolean piped;

    private final Deque<LuaFunction> functions = new ConcurrentLinkedDeque<>();
    private final Deque<LuaFunction> queue = new ConcurrentLinkedDeque<>();
    private final Deque<LuaFunction> removalQueue = new ConcurrentLinkedDeque<>();
    private final HashMultimap<String, LuaFunction> names = HashMultimap.create();

    public LuaEvent() {
        this(false);
    }

    public LuaEvent(boolean piped) {
        this.piped = piped;
    }

    //Add all waiting functions from the queues
    protected void flushQueue() {
        for (LuaFunction function : removalQueue)
            functions.removeFirstOccurrence(function);
        removalQueue.clear();

        for (LuaFunction function : queue)
            functions.addLast(function);
        queue.clear();
    }

    //Calls all the functions in the order they were registered, using the given args for all calls.
    //If piped, the result of one function is passed through to the next, repeatedly, eventually returning the result.
    public Varargs call(Varargs args) {
        flushQueue();
        Varargs vars = args;
        for (LuaFunction function : functions) {
            FiguraMod.pushProfiler(function.name());
            if (piped) {
                vars = function.invoke(vars);
            } else {
                Varargs value = function.invoke(args);
                if (value.arg(1).isboolean() && value.arg(1).checkboolean())
                    vars = value;
            }
            FiguraMod.popProfiler();
        }
        return vars;
    }

    @LuaWhitelist
    public LuaEvent register(@LuaNotNil LuaFunction func, String name) {
        if (__len() >= MAX_FUNCTIONS)
            throw new LuaError("Reached maximum limit of " + MAX_FUNCTIONS + " functions in one event!");
        queue.addLast(func);
        if (name != null)
            names.put(name, func);
        return this;
    }

    @LuaWhitelist
    public void clear() {
        functions.clear();
        queue.clear();
        removalQueue.clear();
        names.clear();
    }

    @LuaWhitelist
    public int remove(@LuaNotNil String name) {
        flushQueue();

        int removed = 0;

        Set<LuaFunction> set = names.removeAll(name);
        for (LuaFunction function : set) {
            if (removalQueue.add(function))
                removed++;
        }

        return removed;
    }

    @LuaWhitelist
    @LuaMetamethodDoc(overloads = {
            @LuaMetamethodOverload(
                    types = {int.class, LuaEvent.class}
            )
    })
    public int __len() {
        return functions.size() + queue.size();
    }

    @Override
    public String toString() {
        return "Event";
    }
}
