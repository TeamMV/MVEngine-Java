package dev.mv.engine.gui.components.layouts;

import dev.mv.engine.gui.components.Choice;
import dev.mv.engine.gui.components.Element;
import dev.mv.engine.gui.components.extras.IgnoreDraw;
import dev.mv.engine.render.shared.DrawContext2D;
import dev.mv.engine.render.shared.Window;

import java.util.function.Predicate;

public class ChoiceGroup extends AbstractLayout implements IgnoreDraw {
    int currentChoice = 0;

    public ChoiceGroup(Window window, Element parent) {
        super(window, parent);
    }

    @Override
    public void draw(DrawContext2D draw) {
        for (Element element : elements) {
            element.draw(draw);
        }
    }

    //disable add and remove of normal elements

    public void addElement(Element e) {

    }

    public void addElements(Element[] e) {

    }

    public void removeElement(Element e) {

    }

    public void removeElements(Element[] e) {

    }

    public void removeElements(Predicate<? super Element> predicate) {

    }

    //choice adding

    public void addChoice(Choice choice) {
        elements.add(choice);
        choice.setParent(this);
        choice.setGui(gui);
    }

    public void removeChoice(Choice choice) {
        elements.remove(choice);
        choice.setParent(null);
        choice.setGui(null);
    }

    public int getCurrentChoice() {
        return currentChoice;
    }

    public void setCurrentChoice(int currentChoice) {
        this.currentChoice = currentChoice;
        uncheckAllExcept(currentChoice);
        ((Choice) elements.get(currentChoice)).check();
    }

    public void setCurrentChoice(Choice choice) {
        if (elements.contains(choice)) {
            setCurrentChoice(elements.indexOf(choice));
        }
    }

    public void uncheckAll() {
        for (Element element : elements) {
            ((Choice) element).uncheck();
        }
    }

    public void uncheckAllExcept(int index) {
        int i = 0;
        for (Element element : elements) {
            if (index == i++) continue;
            ((Choice) element).uncheck();
        }
    }

    @Override
    public Element[] toRender() {
        return elements();
    }
}
