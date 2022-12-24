package dev.mv.engine.gui;

import dev.mv.engine.gui.components.Element;
import dev.mv.engine.gui.theme.Theme;
import dev.mv.engine.render.shared.DrawContext2D;

import java.util.*;
import java.util.function.Consumer;

public class GuiRegistry implements Iterable<Gui> {
    private Map<String, Gui> guiMap;

    public GuiRegistry() {
        guiMap = new HashMap<>();
    }

    public void addGui(Gui gui) {
        if(guiMap.containsKey(gui.getName())) {
            throw new RuntimeException(new IllegalArgumentException("There is already a GUI registered with that name!"));
        } else {
            guiMap.put(gui.getName(), gui);
        }
    }

    public void removeGui(Gui gui) {
        if(!guiMap.containsKey(gui.getName())) {
            throw new RuntimeException(new IllegalArgumentException("There is no GUI registered with that name!"));
        } else {
            guiMap.remove(gui.getName(), gui);
        }
    }

    public Gui findGui(String name) {
        if(!guiMap.containsKey(name)) {
            throw new RuntimeException(new IllegalArgumentException("There is no GUI registered with that name!"));
        } else {
            return guiMap.get(name);
        }
    }

    public Gui[] getGuis() {
        return guiMap.values().toArray(new Gui[0]);
    }

    public void renderGuis() {
        for(Gui gui : this) {
            gui.draw();
            gui.loop();
        }
    }

    public void applyTheme(Theme theme) {
        for(Gui gui : this) {

        }
    }

    private class GuiRegistryIterator implements Iterator<Gui> {
        private boolean hasNext = true;
        private int index = 0;
        private List<Gui> collection;

        public GuiRegistryIterator() {
            collection = List.of(guiMap.values().toArray(new Gui[0]));
        }

        @Override
        public boolean hasNext() {
            return hasNext;
        }

        @Override
        public Gui next() {
            if(index + 2 >= collection.size()) {
                hasNext = false;
            }
            return collection.get(index++);
        }

        @Override
        public void remove() {
            collection.remove(index - 1);
        }

        @Override
        public void forEachRemaining(Consumer<? super Gui> action) {
            Iterator.super.forEachRemaining(action);
        }
    }

    @Override
    public Iterator<Gui> iterator() {
        return new GuiRegistryIterator();
    }

    @Override
    public void forEach(Consumer<? super Gui> action) {
        Iterable.super.forEach(action);
    }

    @Override
    public Spliterator<Gui> spliterator() {
        return Iterable.super.spliterator();
    }
}
