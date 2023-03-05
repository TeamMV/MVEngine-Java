package dev.mv.engine.gui.screens;

import dev.mv.engine.gui.Gui;
import dev.mv.engine.gui.GuiRegistry;
import dev.mv.engine.gui.components.Element;
import dev.mv.engine.gui.components.layouts.AbstractLayout;
import dev.mv.engine.gui.screens.transitions.Transition;
import dev.mv.engine.render.shared.Window;
import dev.mv.utils.async.PromiseNull;
import dev.mv.utils.generic.Pair;

import java.util.Map;

public class Pager {
    private final int FRAMES = 20;
    private Map<String, Pair<Transition, Float>> transitions;
    private GuiRegistry registry;
    private String current;
    private Window window;

    public Pager(Window window) {
        this.window = window;
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

    public void swap(String from, String to) {
        new PromiseNull((resolverNull, rejector) -> {
            Transition transitionFrom = null;
            Pair<Transition, Float> transitionFromPair = transitions.get(from);
            if (transitionFromPair != null)
                transitionFrom = transitionFromPair.a;
            Pair<Transition, Float> transitionToPair = transitions.get(to);
            Transition transitionTo = transitionToPair.a;

            float time = transitionToPair.b;

            if(transitionFrom == null || transitionTo == null) return;

            for (int i = 0; i < FRAMES; i++) {
                if (transitionFrom != null && current != null)
                    for (Element element : registry.findGui(current).getRoot()) {
                        if (transitionFrom.isLinear()) {
                            element.setX(element.getX() + transitionFrom.getXChange());
                            element.setY(element.getY() + transitionFrom.getYChange());
                            if (element instanceof AbstractLayout) {
                                element.setWidth(transitionTo.getScaleChange());
                                element.setHeight(transitionTo.getScaleChange());
                            } else {

                                element.setWidth(element.getWidth() + transitionTo.getScaleChange());
                                element.setHeight(element.getHeight() + transitionTo.getScaleChange());
                            }
                            element.getInitialState().originX = window.getWidth() / 2;
                            element.getInitialState().originY = window.getHeight() / 2;
                            element.getInitialState().rotation += transitionFrom.getRotationChange();
                            element.getBaseColor().setAlpha(element.getBaseColor().getAlpha() + transitionFrom.getAlphaChange());
                            element.getOutlineColor().setAlpha(element.getOutlineColor().getAlpha() + transitionFrom.getAlphaChange());
                            element.getTextColor().setAlpha(element.getTextColor().getAlpha() + transitionFrom.getAlphaChange());
                            element.getExtraColor().setAlpha(element.getExtraColor().getAlpha() + transitionFrom.getAlphaChange());
                        } else {
                            element.setX(element.getX() * transitionFrom.getXChange());
                            element.setY(element.getY() * transitionFrom.getYChange());
                            if (element instanceof AbstractLayout) {
                                element.setWidth(transitionTo.getScaleChange());
                                element.setHeight(transitionTo.getScaleChange());
                            } else {

                                element.setWidth(element.getWidth() * transitionTo.getScaleChange());
                                element.setHeight(element.getHeight() * transitionTo.getScaleChange());
                            }
                            element.getInitialState().originX = window.getWidth() / 2;
                            element.getInitialState().originY = window.getHeight() / 2;
                            element.getInitialState().rotation *= transitionFrom.getRotationChange();
                            element.getBaseColor().setAlpha(element.getBaseColor().getAlpha() * transitionFrom.getAlphaChange());
                            element.getOutlineColor().setAlpha(element.getOutlineColor().getAlpha() * transitionFrom.getAlphaChange());
                            element.getTextColor().setAlpha(element.getTextColor().getAlpha() * transitionFrom.getAlphaChange());
                            element.getExtraColor().setAlpha(element.getExtraColor().getAlpha() * transitionFrom.getAlphaChange());
                        }
                    }
                for (Element element : registry.findGui(to).getRoot()) {
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
                try {
                    Thread.sleep((long) (time * 1000L / FRAMES));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            if (!registry.toRenderList().isEmpty()) {
                if (from != null) {
                    registry.toRenderList().remove(registry.findGui(from));
                }
            }
            if (current != null)
                registry.findGui(current).disableAllUpdates();
            current = to;
            registry.findGui(current).enableAllUpdates();
        });
    }
}
