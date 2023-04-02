package dev.mv.engine.gui.paging;

import dev.mv.engine.gui.Gui;
import dev.mv.engine.gui.GuiRegistry;
import dev.mv.engine.gui.components.Element;
import dev.mv.engine.gui.paging.transitions.Transition;
import dev.mv.engine.render.shared.Window;
import dev.mv.engine.resources.R;
import dev.mv.utils.async.PromiseNull;
import dev.mv.utils.generic.pair.Pair;

import java.util.HashMap;
import java.util.Map;

public class Pager {
    private final int FRAMES = 20;
    private Map<String, Pair<Transition, Float>> transitions;
    private GuiRegistry registry;
    private Map<String, Gui> open;

    public Pager() {
        open = new HashMap();
    }

    public void setRegistry(GuiRegistry registry) {
        this.registry = registry;
        transitions = new HashMap<>();
        prepare();
    }

    public void map(Map<String, Pair<Transition, Float>> transitionMapping) {
        transitions = transitionMapping;
    }

    private void prepare() {
        if (registry == null) return;
        for (Gui gui : registry) {
            Transition transition = null;
            Pair<Transition, Float> transitionPair = transitions.get(gui.getName());
            if(transitionPair != null) {
                transition = transitionPair.a;
            }
            if(transition == null) return;
            for (Element element : gui.elements()) {
                Window window = element.getWindow();
                for (int i = 0; i < FRAMES; i++) {
                    if (transition.isLinear()) {
                        element.setX(element.getX() + transition.getXChange());
                        element.setY(element.getY() + transition.getYChange());
                        element.getInitialState().originX = window.getWidth() / 2;
                        element.getInitialState().originY = window.getHeight() / 2;
                        element.getInitialState().rotation += transition.getRotationChange();
                        element.getBaseColor().setAlpha(element.getBaseColor().getAlpha() + transition.getAlphaChange());
                        element.getOutlineColor().setAlpha(element.getOutlineColor().getAlpha() + transition.getAlphaChange());
                        element.getTextColor().setAlpha(element.getTextColor().getAlpha() + transition.getAlphaChange());
                        element.getExtraColor().setAlpha(element.getExtraColor().getAlpha() + transition.getAlphaChange());
                    } else {
                        element.setX(element.getX() * transition.getXChange());
                        element.setY(element.getY() * transition.getYChange());
                        element.getInitialState().originX = window.getWidth() / 2;
                        element.getInitialState().originY = window.getHeight() / 2;
                        element.getInitialState().rotation *= transition.getRotationChange();
                        element.getBaseColor().setAlpha(element.getBaseColor().getAlpha() * transition.getAlphaChange());
                        element.getOutlineColor().setAlpha(element.getOutlineColor().getAlpha() * transition.getAlphaChange());
                        element.getTextColor().setAlpha(element.getTextColor().getAlpha() * transition.getAlphaChange());
                        element.getExtraColor().setAlpha(element.getExtraColor().getAlpha() * transition.getAlphaChange());
                    }
                }
            }
        }
    }

    public void open(String name) {
        open.put(name, registry.findGui(name));
        Gui gui = registry.findGui(name);
        System.out.println(gui);
        gui.enableAllUpdates();

        gui.resize(gui.getRoot().getWindow().getWidth(), gui.getRoot().getWindow().getHeight());

        new PromiseNull((resolverNull, rejector) -> {
            Pair<Transition, Float> transitionToPair = transitions.get(name);
            if (transitionToPair == null) {
                R.guis.get("default").toRenderList().add(gui);
                return;
            }

            Transition transitionTo = transitionToPair.a;

            if(transitionTo == null) {
                R.guis.get("default").toRenderList().add(gui);
                return;
            }

            float time = transitionToPair.b;

            if (transitionTo == null) return;

            for (int i = 0; i < FRAMES; i++) {
                if (transitionTo != null && !open.containsKey(name)) {
                    for (Element element : registry.findGui(name).getRoot()) {
                        Window window = element.getWindow();
                        if (transitionTo.isLinear()) {
                            element.setX(element.getX() - transitionTo.getXChange());
                            element.setY(element.getY() - transitionTo.getYChange());
                            element.getInitialState().originX = window.getWidth() / 2;
                            element.getInitialState().originY = window.getHeight() / 2;
                            element.getInitialState().rotation -= transitionTo.getRotationChange();
                            element.getBaseColor().setAlpha(element.getBaseColor().getAlpha() - transitionTo.getAlphaChange());
                            element.getOutlineColor().setAlpha(element.getOutlineColor().getAlpha() - transitionTo.getAlphaChange());
                            element.getTextColor().setAlpha(element.getTextColor().getAlpha() - transitionTo.getAlphaChange());
                            element.getExtraColor().setAlpha(element.getExtraColor().getAlpha() - transitionTo.getAlphaChange());
                        } else {
                            element.setX(element.getX() / transitionTo.getXChange());
                            element.setY(element.getY() / transitionTo.getYChange());
                            element.getInitialState().originX = window.getWidth() / 2;
                            element.getInitialState().originY = window.getHeight() / 2;
                            element.getInitialState().rotation /= transitionTo.getRotationChange();
                            element.getBaseColor().setAlpha(element.getBaseColor().getAlpha() / transitionTo.getAlphaChange());
                            element.getOutlineColor().setAlpha(element.getOutlineColor().getAlpha() / transitionTo.getAlphaChange());
                            element.getTextColor().setAlpha(element.getTextColor().getAlpha() / transitionTo.getAlphaChange());
                            element.getExtraColor().setAlpha(element.getExtraColor().getAlpha() / transitionTo.getAlphaChange());
                        }
                    }
                }
            }

            R.guis.get("default").toRenderList().add(registry.findGui(name));
        });
    }

    public void close(String name) {
        open.remove(name);
        R.guis.get("default").findGui(name).disableAllUpdates();
        new PromiseNull((resolverNull, rejector) -> {
            Transition transition = null;
            Pair<Transition, Float> transitionPair = transitions.get(name);
            if (transitionPair != null)
                transition = transitionPair.a;
            else {
                R.guis.get("default").toRenderList().remove(registry.findGui(name));
                return;
            }

            if (transition == null) {
                R.guis.get("default").toRenderList().remove(registry.findGui(name));
                return;
            }

            float time = transitionPair.b;

            for (int i = 0; i < FRAMES; i++) {
                for (Element element : registry.findGui(name).getRoot()) {
                    Window window = element.getWindow();
                    if (transition.isLinear()) {
                        element.setX(element.getX() + transition.getXChange());
                        element.setY(element.getY() + transition.getYChange());
                        element.getInitialState().originX = window.getWidth() / 2;
                        element.getInitialState().originY = window.getHeight() / 2;
                        element.getInitialState().rotation += transition.getRotationChange();
                        element.getBaseColor().setAlpha(element.getBaseColor().getAlpha() + transition.getAlphaChange());
                        element.getOutlineColor().setAlpha(element.getOutlineColor().getAlpha() + transition.getAlphaChange());
                        element.getTextColor().setAlpha(element.getTextColor().getAlpha() + transition.getAlphaChange());
                        element.getExtraColor().setAlpha(element.getExtraColor().getAlpha() + transition.getAlphaChange());
                    } else {
                        element.setX(element.getX() * transition.getXChange());
                        element.setY(element.getY() * transition.getYChange());
                        element.getInitialState().originX = window.getWidth() / 2;
                        element.getInitialState().originY = window.getHeight() / 2;
                        element.getInitialState().rotation *= transition.getRotationChange();
                        element.getBaseColor().setAlpha(element.getBaseColor().getAlpha() * transition.getAlphaChange());
                        element.getOutlineColor().setAlpha(element.getOutlineColor().getAlpha() * transition.getAlphaChange());
                        element.getTextColor().setAlpha(element.getTextColor().getAlpha() * transition.getAlphaChange());
                        element.getExtraColor().setAlpha(element.getExtraColor().getAlpha() * transition.getAlphaChange());
                    }
                }

                try {
                    Thread.sleep((long) (time * 1000L / FRAMES));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            R.guis.get("default").toRenderList().remove(registry.findGui(name));
        });
    }

    public void onlyOpen(String name)  {
        open.keySet().forEach(this::close);
        open(name);
    }

    public void swap(String from, String to) {
        open(to);
        close(from);
    }
}