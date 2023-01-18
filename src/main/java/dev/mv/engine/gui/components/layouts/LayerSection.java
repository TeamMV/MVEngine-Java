package dev.mv.engine.gui.components.layouts;

import dev.mv.engine.gui.components.Element;
import dev.mv.engine.gui.components.extras.IgnoreDraw;
import dev.mv.engine.render.shared.DrawContext2D;
import dev.mv.engine.render.shared.Window;
import dev.mv.utils.Utils;
import org.jetbrains.annotations.Range;

public class LayerSection extends AbstractLayout implements IgnoreDraw {
    protected int layerToRenderOn = 0;

    public LayerSection(Window window, Element parent) {
        super(window, parent);
    }

    public LayerSection(Window window, int width, int height, Element parent) {
        super(window, width, height, parent);
    }

    public LayerSection(Window window, int x, int y, int width, int height, Element parent) {
        super(window, x, y, width, height, parent);
    }

    @Override
    public void draw(DrawContext2D draw) {
        for(Element element : this) {
            element.draw(draw);
        }
    }

    public void prioritize() {
        if(gui == null) return;
        if(gui.getLayers().get(layerToRenderOn).contains(this)) return;
        gui.getLayers().get(layerToRenderOn).add(this);
    }

    public void dismiss() {
        if(gui == null) return;
        if(!gui.getLayers().get(layerToRenderOn).contains(this)) return;
        gui.getLayers().get(layerToRenderOn).remove(this);
    }

    @Override
    public Element[] toRender() {
        return elements();
    }

    public int getLayerToRenderOn() {
        return layerToRenderOn;
    }

    public void setLayerToRenderOn(@Range(from=0, to=10) int layerToRenderOn) {
        this.layerToRenderOn = Utils.clamp(layerToRenderOn, 0, 10);
    }
}
