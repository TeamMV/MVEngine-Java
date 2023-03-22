package dev.mv.engine.gui;

import dev.mv.engine.gui.screens.Pager;
import dev.mv.engine.gui.theme.Theme;
import dev.mv.engine.render.shared.DrawContext2D;
import dev.mv.engine.resources.Resource;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

public class GuiRegistry implements Iterable<Gui>, Resource {
    private Map<String, Gui> guiMap;
    private Pager pager;
    private List<Gui> toRender;

    public GuiRegistry() {
        guiMap = new HashMap<>();
        toRender = new ArrayList<>();
    }

    public void addGui(Gui gui) {
        if (guiMap.containsKey(gui.getName())) {
            throw new RuntimeException(new IllegalArgumentException("There is already a GUI registered with that name!"));
        } else {
            guiMap.put(gui.getName(), gui);
        }
    }

    public void removeGui(Gui gui) {
        if (!guiMap.containsKey(gui.getName())) {
            throw new RuntimeException(new IllegalArgumentException("There is no GUI registered with that name!"));
        } else {
            guiMap.remove(gui.getName(), gui);
        }
    }

    public Gui findGui(String name) {
        if (!guiMap.containsKey(name)) {
            throw new RuntimeException(new IllegalArgumentException("There is no GUI registered with the name " + name + "!"));
        } else {
            return guiMap.get(name);
        }
    }

    public Gui[] getGuis() {
        return guiMap.values().toArray(new Gui[0]);
    }

    public void applyRenderer(DrawContext2D drawContext2D) {
        forEach(gui -> gui.applyRenderer(drawContext2D));
    }

    public void applyPager(Pager pager) {
        this.pager = pager;
        this.pager.setRegistry(this);
    }

    public void swap(String from, String to) {
        if (!guiMap.containsKey(to)) return;
        if (pager != null) {
            pager.swap(from, to);
        }

        if (!toRender.contains(findGui(to)))
            toRender.add(findGui(to));
    }


    public void renderGuis() {
        for (Iterator<Gui> it = toRender(); it.hasNext(); ) {
            Gui gui = it.next();
            gui.draw();
            gui.loop();
        }
    }

    public void applyTheme(Theme theme) throws IOException {
        for (Gui gui : this) {
            gui.applyTheme(theme);
        }
    }

    @Override
    public Iterator<Gui> iterator() {
        return new GuiRegistryIterator(guiMap.values());
    }

    public Iterator<Gui> toRender() {
        if (toRender.isEmpty()) {
            return new GuiRegistryIterator(Collections.EMPTY_LIST);
        }
        return new GuiRegistryIterator(toRender);
    }

    public List<Gui> toRenderList() {
        return toRender;
    }

    @Override
    public void forEach(Consumer<? super Gui> action) {
        Iterable.super.forEach(action);
    }

    @Override
    public Spliterator<Gui> spliterator() {
        return Iterable.super.spliterator();
    }

    public void resize(int width, int height) {
        for (Iterator<Gui> it = toRender(); it.hasNext(); ) {
            Gui gui = it.next();
            gui.resize(width, height);
        }
    }

    public void pressKey(int keyCode) {
        for (Iterator<Gui> it = toRender(); it.hasNext(); ) {
            Gui gui = it.next();
            gui.pressKey(keyCode);
        }
    }

    public void typeKey(int keyCode) {
        for (Iterator<Gui> it = toRender(); it.hasNext(); ) {
            Gui gui = it.next();
            gui.typeKey(keyCode);
        }
    }

    public void releaseKey(int keyCode) {
        for (Iterator<Gui> it = toRender(); it.hasNext(); ) {
            Gui gui = it.next();
            gui.releaseKey(keyCode);
        }
    }

    public List<Gui> getGuiList() {
        return guiMap.values().stream().toList();
    }

    private class GuiRegistryIterator implements Iterator<Gui> {
        private boolean hasNext = true;
        private int index = 0;
        private List<Gui> collection;

        public GuiRegistryIterator(Collection<Gui> list) {
            collection = List.copyOf(list);
        }

        @Override
        public boolean hasNext() {
            return index != collection.size();
        }

        @Override
        public Gui next() {
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
}
