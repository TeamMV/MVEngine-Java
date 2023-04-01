package dev.mv.engine.gui.parsing.page;

import dev.mv.engine.exceptions.Exceptions;
import dev.mv.engine.gui.Gui;
import dev.mv.engine.gui.GuiRegistry;
import dev.mv.engine.gui.components.extras.ValueChange;
import dev.mv.engine.gui.event.*;
import dev.mv.engine.gui.functions.GuiMethod;
import dev.mv.engine.gui.functions.GuiScript;
import dev.mv.engine.gui.input.Clickable;
import dev.mv.engine.gui.input.Keyboard;
import dev.mv.engine.gui.input.ScrollInput;
import dev.mv.engine.gui.pages.Page;
import dev.mv.engine.gui.pages.Trigger;
import dev.mv.engine.exceptions.InvalidGuiFileException;
import dev.mv.engine.resources.R;
import dev.mv.utils.Utils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class PageParser {
    private GuiRegistry registry;

    public PageParser() {
    }

    public Page parse(InputStream stream) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(stream);
            document.getDocumentElement().normalize();

            if (!document.getDocumentElement().getTagName().equals("page")) {
                Exceptions.send(new InvalidGuiFileException("Root should be \"page\""));
            }

            Page page = new Page(document.getDocumentElement().getAttribute("name"));

            NodeList tags = document.getDocumentElement().getChildNodes();

            for (int i = 0; i < tags.getLength(); i++) {
                Node tag = tags.item(i);
                if (tag.getNodeType() == Node.ELEMENT_NODE) {
                    if (tag.getNodeName().equals("layouts")) {
                        registry = setupRegistry(tag);
                        page.setRegistry(registry);
                    }
                    if (tag.getNodeName().equals("actions")) {
                        parseActions(tag, page);
                    }
                }
            }

            return page;
        } catch (Exception e) {
            Exceptions.send(e);

            return null;
        }
    }

    private GuiRegistry setupRegistry(Node tag) {
        GuiRegistry ret = new GuiRegistry();
        NodeList nodeList = tag.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if(node.getNodeType() == Node.ELEMENT_NODE) {
                if(node.getNodeName().equals("layout")) {
                    Element element = (Element) node;
                    ret.addGui(R.guis.get("default").findGui(element.getAttribute("name")));
                }
            }
        }

        return ret;
    }

    private void parseActions(Node tag, Page current) {
        NodeList nodeList = tag.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if(node.getNodeType() == Node.ELEMENT_NODE) {
                if(node.getNodeName().equals("trigger")) {
                    Element element = (Element) node;
                    Trigger trigger = parseTrigger(element, current);
                    current.addTrigger(trigger);
                }
            }
        }
    }

    private Trigger parseTrigger(Element tag, Page current) {
        Trigger trigger = new Trigger();
        trigger.setName(tag.getAttribute("name"));
        if(tag.hasAttribute("listen")) {
            parseEventQuery(tag.getAttribute("listen"), trigger);
        }

        if(tag.hasChildNodes()) {
            NodeList nodeList = tag.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if(node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    if(element.getNodeName().equals("redirect")) {
                        trigger.setAction(() -> current.getPager().onlyOpen(element.getAttribute("name")));
                    }
                    if(element.getNodeName().equals("close")) {
                        trigger.setAction(() -> current.getPager().close(element.getAttribute("name")));
                    }
                    if(element.getNodeName().equals("swap")) {
                        trigger.setAction(() -> current.getPager().swap(element.getAttribute("from"), element.getAttribute("to")));
                    }
                    if(element.getNodeName().equals("open")) {
                        if(!element.hasChildNodes()) {
                            trigger.setAction(() -> current.getPager().open(element.getAttribute("name")));
                        } else {
                            NodeList nodeList1 = element.getChildNodes();
                            for (int j = 0; j < nodeList1.getLength(); j++) {
                                Node node1 = nodeList1.item(j);
                                if(node1.getNodeType() == Node.ELEMENT_NODE) {
                                    if(node1.getNodeName().equals("focus")) {
                                        Element focus = (Element) node1;
                                        Trigger leaveTrigger = new Trigger();
                                        parseEventQuery(focus.getAttribute("leave"), leaveTrigger);

                                        String guiName = element.getAttribute("name");
                                        Gui gui = registry.findGui(guiName);
                                        AtomicReference<List<Gui>> beforeOpen = new AtomicReference<>();
                                        leaveTrigger.setAction(() -> {
                                            beforeOpen.get().forEach(Gui::enableAllUpdates);
                                            current.getPager().close(guiName);
                                        });
                                        trigger.setAction(() -> {
                                            current.getPager().open(guiName);
                                            beforeOpen.set(registry.getGuiList());
                                            registry.getGuiList().forEach(Gui::disableAllUpdates);
                                            gui.enableAllUpdates();
                                        });
                                    }
                                }
                            }
                        }
                    }
                    if(element.getNodeName().equals("call")) {
                        String handler = element.getAttribute("handler");
                        String gui = handler.split("\\.")[0];
                        String method = handler.split("\\.")[1];
                        GuiScript[] scripts = registry.findGui(gui).getScripts();
                        String[] raws = Utils.iter(method.substring(method.indexOf('(') + 1, method.indexOf(')')).split(",")).filter(s -> !s.isBlank()).toArray();
                        Class<?>[] paramTypes = new Class<?>[raws.length];
                        for (int j = 0; j < raws.length; j++) {
                            paramTypes[j] = inferType(raws[j]);
                        }
                        for (GuiScript script : scripts) {
                            GuiMethod guiMethod;
                            try {
                                guiMethod = script.findMethod(method.split("\\(")[0], method.indexOf('(') != method.indexOf(')') - 1 ? paramTypes : null, "");
                            } catch (NoSuchMethodException e) {
                                continue;
                            }
                            Object[] params = new Object[paramTypes.length];
                            for (int j = 0; j < paramTypes.length; j++) {
                                if (paramTypes[j] == String.class) {
                                    params[j] = raws[j].substring(1, raws[j].length() - 1);
                                } else if (paramTypes[j] == int.class) {
                                   params[j] = Integer.parseInt(raws[j]);
                                } else if (paramTypes[j] == long.class) {
                                    params[j] = Long.parseLong(raws[j].replaceAll("L", ""));
                                } else if (paramTypes[j] == float.class) {
                                    params[j] = Float.parseFloat(raws[j].replaceAll("f", ""));
                                } else if (paramTypes[j] == double.class) {
                                    params[j] = Double.parseDouble(raws[j]);
                                }
                            }
                            trigger.setAction(() -> guiMethod.invoke(params));
                        }
                    }
                }
            }
        }

        return trigger;
    }

    private Class<?> inferType(String literal) {
        if (literal.startsWith("'|\"") && literal.endsWith("'|\"")) return String.class;
        else {
            if (literal.contains(".") && literal.contains("f")) return float.class;
            if (literal.contains(".")) return double.class;
            if (literal.contains("L")) return long.class;
            return int.class;
        }
    }

    private void parseEventQuery(String query, Trigger trigger) {
        int partIdx = query.indexOf('.');
        String layoutName = query.substring(0, partIdx);
        String elementId = query.substring(partIdx + 1, query.indexOf('@'));
        String event = query.split("@")[1].split("\\[")[0];
        String specs = query.substring(query.lastIndexOf('[') + 1, query.lastIndexOf(']'));
        dev.mv.engine.gui.components.Element target = registry.findGui(layoutName).getRoot().findElementById(elementId);
        EventListener listener = switch (event.toLowerCase()) {
            case "onclick":     if(target instanceof Clickable) yield new ClickListenerImpl(trigger, specs); else throwUnsupportedEvent(event, elementId); yield null;
            case "onkey":       if(target instanceof Keyboard) yield new KeyListenerImpl(trigger, specs); else throwUnsupportedEvent(event, elementId); yield null;
            case "onscroll":    if(target instanceof ScrollInput) yield new ScrollListenerImpl(trigger, specs); else throwUnsupportedEvent(event, elementId); yield null;
            case "onprogress":  if(target instanceof ValueChange) yield new ProgressListenerImpl(trigger, specs); else throwUnsupportedEvent(event, elementId); yield null;
            default: yield null;
        };
        target.attachListener(listener);
    }

    private void throwUnsupportedEvent(String event, String elementId) {
        Exceptions.send(new UnsupportedEventException("There is no \"" + event + "\" on the element \"" + elementId + "\"!"));
    }

    private static class EventListenerImpl {
        protected Trigger trigger;
        protected String specs;

        protected EventListenerImpl(Trigger trigger, String specs) {
            this.trigger = trigger;
            this.specs = specs;
        }
    }

    private static class ClickListenerImpl extends EventListenerImpl implements ClickListener {

        private ClickListenerImpl(Trigger trigger, String specs) {
            super(trigger, specs);
        }

        @Override
        public void onCLick(dev.mv.engine.gui.components.Element element, int button) {
            if(specs.equals("click")) trigger.trigger();
        }

        @Override
        public void onRelease(dev.mv.engine.gui.components.Element element, int button) {
            if(specs.equals("release")) trigger.trigger();
        }
    }

    private static class KeyListenerImpl extends EventListenerImpl implements KeyListener {

        protected KeyListenerImpl(Trigger trigger, String specs) {
            super(trigger, specs);
        }

        @Override
        public void onPress(dev.mv.engine.gui.components.Element element, int keyCode, char keyChar) {
            if(specs.equals("press")) trigger.trigger();
        }

        @Override
        public void onType(dev.mv.engine.gui.components.Element element, int keyCode, char keyChar) {
            if(specs.equals("type")) trigger.trigger();
        }

        @Override
        public void onRelease(dev.mv.engine.gui.components.Element element, int keyCode, char keyChar) {
            if(specs.equals("release")) trigger.trigger();
        }
    }

    private static class ProgressListenerImpl extends EventListenerImpl implements ProgressListener {

        protected ProgressListenerImpl(Trigger trigger, String specs) {
            super(trigger, specs);
        }

        @Override
        public void onIncrement(dev.mv.engine.gui.components.Element e, int currentValue, int totalValue, int percentage) {
            if(specs.equals("increment")) trigger.trigger();
        }

        @Override
        public void onDecrement(dev.mv.engine.gui.components.Element e, int currentValue, int totalValue, int percentage) {
            if(specs.equals("decrement")) trigger.trigger();
        }
    }

    private static class ScrollListenerImpl extends EventListenerImpl implements ScrollListener {

        protected ScrollListenerImpl(Trigger trigger, String specs) {
            super(trigger, specs);
        }

        @Override
        public void onScrollX(dev.mv.engine.gui.components.Element element, int amount) {
            if(specs.equals("x")) trigger.trigger();
        }

        @Override
        public void onScrollY(dev.mv.engine.gui.components.Element element, int amount) {
            if(specs.equals("y")) trigger.trigger();
        }
    }
}
