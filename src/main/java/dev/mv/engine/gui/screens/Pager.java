package dev.mv.engine.gui.screens;

import dev.mv.engine.gui.Gui;
import dev.mv.engine.gui.GuiRegistry;
import dev.mv.engine.gui.components.Element;
import dev.mv.engine.gui.components.layouts.AbstractLayout;
import dev.mv.engine.gui.screens.transitions.Transition;
import dev.mv.engine.render.shared.Window;
import dev.mv.utils.async.PromiseNull;
import dev.mv.utils.generic.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
                        if (element instanceof AbstractLayout) {
                            element.setWidth(transition.getScaleChange());
                            element.setHeight(transition.getScaleChange());
                        } else {
                            element.setWidth(element.getWidth() + transition.getScaleChange());
                            element.setHeight(element.getHeight() + transition.getScaleChange());
                        }
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
                        if (element instanceof AbstractLayout) {
                            element.setWidth(transition.getScaleChange());
                            element.setHeight(transition.getScaleChange());
                        } else {

                            element.setWidth(element.getWidth() * transition.getScaleChange());
                            element.setHeight(element.getHeight() * transition.getScaleChange());
                        }
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
        registry.findGui(name).enableAllUpdates();

        new PromiseNull((resolverNull, rejector) -> {
            Pair<Transition, Float> transitionToPair = transitions.get(name);
            if (transitionToPair == null) {
                registry.toRenderList().add(registry.findGui(name));
                return;
            }

            Transition transitionTo = transitionToPair.a;

            if(transitionTo == null) {
                registry.toRenderList().add(registry.findGui(name));
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
                            if (element instanceof AbstractLayout) {
                                element.setWidth(-transitionTo.getScaleChange());
                                element.setHeight(-transitionTo.getScaleChange());
                            } else {

                                element.setWidth(element.getWidth() - transitionTo.getScaleChange());
                                element.setHeight(element.getHeight() - transitionTo.getScaleChange());
                            }
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
                            if (element instanceof AbstractLayout) {
                                element.setWidth(-transitionTo.getScaleChange());
                                element.setHeight(-transitionTo.getScaleChange());
                            } else {

                                element.setWidth(element.getWidth() / transitionTo.getScaleChange());
                                element.setHeight(element.getHeight() / transitionTo.getScaleChange());
                            }
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

            registry.toRenderList().add(registry.findGui(name));
        });
    }

    public void close(String name) {
        open.remove(name);
        registry.findGui(name).disableAllUpdates();
        new PromiseNull((resolverNull, rejector) -> {
            Transition transition = null;
            Pair<Transition, Float> transitionPair = transitions.get(name);
            if (transitionPair != null)
                transition = transitionPair.a;
            else {
                registry.toRenderList().remove(registry.findGui(name));
                return;
            }

            if (transition == null) {
                registry.toRenderList().remove(registry.findGui(name));
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

            registry.toRenderList().remove(registry.findGui(name));
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
