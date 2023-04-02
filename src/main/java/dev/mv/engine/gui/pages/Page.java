package dev.mv.engine.gui.pages;

import dev.mv.engine.gui.GuiRegistry;
import dev.mv.engine.gui.paging.Pager;
import dev.mv.engine.render.shared.DrawContext2D;
import dev.mv.engine.resources.Resource;

import java.util.ArrayList;
import java.util.List;

public class Page implements Resource {
    private Pager pager;
    private GuiRegistry registry;
    private DrawContext2D drawContext2D;
    private String name;
    private List<Trigger> triggers;

    public Page(String name) {
        this.name = name;
        pager = new Pager();
        triggers = new ArrayList<>();
    }

    public Pager getPager() {
        return pager;
    }

    public GuiRegistry getRegistry() {
        return registry;
    }

    public void setRegistry(GuiRegistry registry) {
        this.registry = registry;
        registry.applyPager(pager);
        if(drawContext2D != null) {
            registry.applyRenderer(drawContext2D);
        }
    }

    public void applyRenderer(DrawContext2D drawContext2D) {
        this.drawContext2D = drawContext2D;
        if(registry != null) {
            registry.applyRenderer(drawContext2D);
        }
    }

    public void addTrigger(Trigger trigger) {
        triggers.add(trigger);
    }

    public void removeTrigger(Trigger trigger) {
        triggers.remove(trigger);
    }

    public Trigger[] getTriggers() {
        return triggers.toArray(new Trigger[0]);
    }

    public List<Trigger> getTriggerList() {
        return triggers;
    }
}
