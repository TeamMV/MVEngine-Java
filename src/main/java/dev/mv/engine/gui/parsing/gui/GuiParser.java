package dev.mv.engine.gui.parsing.gui;

import dev.mv.engine.MVEngine;
import dev.mv.engine.gui.Gui;
import dev.mv.engine.gui.GuiRegistry;
import dev.mv.engine.gui.components.*;
import dev.mv.engine.gui.components.layouts.ChoiceGroup;
import dev.mv.engine.gui.components.layouts.CollapseMenu;
import dev.mv.engine.gui.components.layouts.HorizontalLayout;
import dev.mv.engine.gui.components.layouts.VerticalLayout;
import dev.mv.engine.gui.functions.GuiScript;
import dev.mv.engine.gui.functions.Language;
import dev.mv.engine.gui.input.Clickable;
import dev.mv.engine.gui.parsing.GuiConfig;
import dev.mv.engine.gui.parsing.InvalidGuiFileException;
import dev.mv.engine.render.shared.DrawContext2D;
import dev.mv.engine.render.shared.Window;
import dev.mv.engine.resources.R;
import lombok.SneakyThrows;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class GuiParser {
    private Window window;
    private String layoutPath;
    private String[] layouts;
    private Map<String, String> variables;
    private Map<String, Reference> references;

    private Map<String, String> currentRefLookup;

    private class Reference{
        private Element pointer;
        private String[] params;

        private Reference(Element pointer, String[] params) {
            this.pointer = pointer;
            this.params = params;
        }

        public Element getPointer() {
            return pointer;
        }

        public String[] getParams() {
            return params;
        }
    }

    public GuiParser(GuiConfig guiConfig) {
        layoutPath = guiConfig.getLayoutPath();
        layouts = guiConfig.getLayouts();
        variables = new HashMap<>();
        references = new HashMap<>();
        currentRefLookup = new HashMap<>();
    }

    public GuiRegistry parse(Window window, DrawContext2D drawContext2D) {
        this.window = window;

        GuiRegistry returnRegistry = new GuiRegistry();
        for(String layout : layouts) {
            returnRegistry.addGui(parse(layout, drawContext2D));
        }

        currentRefLookup.clear();

        return returnRegistry;
    }

    public Gui parse(String path, DrawContext2D drawContext2D) {
        try{
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(getClass().getResourceAsStream(layoutPath + path));
            document.getDocumentElement().normalize();

            if (!document.getDocumentElement().getTagName().equals("gui")) {
                MVEngine.Exceptions.__throw__(new InvalidGuiFileException("Root should be \"gui\""));
            }

            Gui gui = new Gui(drawContext2D, window, document.getDocumentElement().getAttribute("name"));

            NodeList tags = document.getDocumentElement().getChildNodes();

            for(int i = 0; i < tags.getLength(); i++) {
                Node tag = tags.item(i);
                if(tag.getNodeType() == Node.ELEMENT_NODE) {
                    if (tag.getNodeName().equals("script")) {
                        gui.addScript(parseScript(tag));
                    }
                    if(tag.getNodeName().equals("variables")) {
                        parseVariables(tag);
                    }
                    if(tag.getNodeName().equals("references")) {
                        parseReferences(tag);
                    }
                    if(tag.getNodeName().equals("elements")) {
                        NodeList elementList = tag.getChildNodes();
                        for (int j = 0; j < elementList.getLength(); j++) {
                            Node element = elementList.item(j);
                            if(element.getNodeType() == Node.ELEMENT_NODE) {
                                dev.mv.engine.gui.components.Element e = parseElement((Element) element);
                                if(e != null) {
                                    gui.addElement(e);
                                }
                            }
                        }
                    }
                }
            }

            return gui;
        } catch (Exception e) {
            MVEngine.Exceptions.__throw__(e);
            return null;
        }
    }

    @SneakyThrows
    public GuiScript parseScript(Node scriptTag) {
        Element script = (Element) scriptTag;
        return new GuiScript(Language.fromString(script.getAttribute("lang")), script.getAttribute("src"));
    }

    private void parseVariables(Node variablesTag) {
        NodeList variables = variablesTag.getChildNodes();
        for (int i = 0; i < variables.getLength(); i++) {
            Node variableTag = variables.item(i);
            if(variableTag.getNodeType() == Node.ELEMENT_NODE) {
                if(variableTag.getNodeName().equals("var")) {
                    parseVariable((Element) variableTag);
                }
            }
        }
    }

    private void parseVariable(Element var) {
        variables.put(var.getAttribute("name"), var.getTextContent());
    }

    private String getVariable(String query) {
        if(query == null) return "";
        if(query.startsWith("$VAR(") && query.endsWith(")")) {
            return variables.get(query.substring(5, query.length() - 1));
        }

        return null;
    }

    private String getParam(String query) {
        if(query == null) return "";
        if(query.startsWith("$PARAM(") && query.endsWith(")")) {
            return currentRefLookup.get(query.substring(7, query.length() - 1));
        }

        return null;
    }

    private void parseReferences(Node referencesTag) {
        NodeList references = referencesTag.getChildNodes();
        for (int i = 0; i < references.getLength(); i++) {
            Node referenceTag = references.item(i);
            if(referenceTag.getNodeType() == Node.ELEMENT_NODE) {
                if(referenceTag.getNodeName().equals("ref")) {
                    parseReference((Element) referenceTag);
                }
            }
        }
    }

    private void parseReference(Element ref) {
        String[] params = null;
        if(ref.hasAttribute("params")) {
            params = Arrays.stream(getStringAttrib(ref.getAttribute("params")).replace("[", "").replace("]", "").split(",")).map(this::getStringAttrib).toList().toArray(new String[0]);
        }
        NodeList tags = ref.getChildNodes();
        Element element = null;
        for (int i = 0; i < tags.getLength(); i++) {
            Node tag = tags.item(i);
            if(tag.getNodeType() == Node.ELEMENT_NODE) {
                element = (Element) tag;
                break;
            }
        }
        try {
            references.put(ref.getAttribute("name"), new Reference(element, params));
        } catch (NullPointerException e) {
            MVEngine.Exceptions.__throw__(new InvalidGuiFileException("\"ref\" tag with name \"" + ref.getAttribute("name") + "\" contains an invalid gui element tag!"));
        }
    }

    private ParsedClickMethod parseClickMethod(String query, Class<? extends Clickable> clazz) {
        ParsedClickMethod method = new ParsedClickMethod();
        method.name = query.split("\\(")[0];
        String[] params = query.split("\\(")[1].split("\\)")[0].replaceAll("//s+", "").split(",");
        method.types = new Class<?>[params.length];
        method.params = new Object[params.length];
        for (int i = 0; i < params.length; i++) {
            String param = params[i];
            if (param.equals("this")) {
                method.addSelf = true;
                method.types[i] = clazz;
                method.params[i] = null;
                method.selfPos = i;
                continue;
            }
            if (param.matches("\\$VAR\\([a-zA-Z0-9 ]+\\)")) {
                param = getVariable(param);
            }
            Class<?> type = inferType(param);
            method.types[i] = type;
            if (type == String.class) {
                method.params[i] = param.substring(1, param.length() - 1);
            }
            else if (type == int.class) {
                method.params[i] = Integer.parseInt(param);
            }
            else if (type == long.class) {
                method.params[i] = Long.parseLong(param.replaceAll("L", ""));
            }
            else if (type == float.class) {
                method.params[i] = Float.parseFloat(param.replaceAll("f", ""));
            }
            else if (type == double.class) {
                method.params[i] = Double.parseDouble(param);
            }
        }
        return method;
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

    private dev.mv.engine.gui.components.Element parseElement(Element elementTag) {
        dev.mv.engine.gui.components.Element e = switch (elementTag.getNodeName()) {
            default -> {MVEngine.Exceptions.__throw__(new InvalidGuiFileException("\"" + elementTag.getNodeName() + "\" is not a valid gui element tag!")); yield null;}
            case "textLine" ->          parseTextLine(elementTag);
            case "button" ->            parseButton(elementTag);
            case "imageButton" ->       parseImageButton(elementTag);
            case "checkbox" ->          parseCheckbox(elementTag);
            case "progressBar" ->       parseProgressBar(elementTag);
            case "input" ->             parseInput(elementTag);
            case "separator" ->         parseSeparator(elementTag);
            case "space" ->             parseSpace(elementTag);
            case "verticalLayout" ->    parseVerticalLayout(elementTag);
            case "horizontalLayout" ->  parseHorizontalLayout(elementTag);
            case "collapseMenu" ->      parseCollapseMenu(elementTag);
            case "choiceGroup" ->       parseChoiceGroup(elementTag);
            case "ref" ->               resolveRef(elementTag);
            case "var" ->               {parseVariable(elementTag); yield null;}
            case "choice" -> {MVEngine.Exceptions.__throw__(new InvalidGuiFileException("\"choice\" tag can't stand outside of a \"choiceGroup\" tag!")); yield null;}
        };

        if(e == null) {
            return null;
        }

        e.setId(elementTag.getAttribute("id"));
        if(elementTag.hasAttribute("tags")) {
            Arrays.asList(getStringAttrib(elementTag.getAttribute("tags")).replace("[", "").replace("]", "").split(",")).stream().map(this::getStringAttrib).forEach(e::addTag);
        }
        return e;
    }

    private int getIntAttrib(String attrib) {
        if(attrib.isBlank()) return 0;
        if(attrib.startsWith("$VAR(") && attrib.endsWith(")")) {
            return Integer.parseInt(getVariable(attrib));
        } else if(attrib.startsWith("$PARAM(") && attrib.endsWith(")")) {
            return Integer.parseInt(getParam(attrib));
        } else {
            return Integer.parseInt(attrib);
        }
    }

    private String getStringAttrib(String attrib) {
        if(attrib.startsWith("$VAR(") && attrib.endsWith(")")) {
            return getVariable(attrib);
        } else if(attrib.startsWith("$PARAM(") && attrib.endsWith(")")) {
            return getParam(attrib);
        } else {
            return attrib;
        }
    }

    private boolean getBooleanAttrib(String attrib) {
        if(attrib.isBlank()) return false;
        if(attrib.startsWith("$VAR(") && attrib.endsWith(")")) {
            return Boolean.parseBoolean(getVariable(attrib));
        } else if(attrib.startsWith("$PARAM(") && attrib.endsWith(")")) {
            return Boolean.parseBoolean(getParam(attrib));
        } else {
            return Boolean.parseBoolean(attrib);
        }
    }

    private dev.mv.engine.gui.components.Element resolveRef(Element tag) {
        currentRefLookup.clear();

        if(!references.containsKey(tag.getAttribute("name"))) {
            MVEngine.Exceptions.__throw__(new InvalidGuiFileException("\"ref\" tag with the name \"" + tag.getAttribute("name") + "\" is not declared yet!"));
            return null;
        }

        if(tag.hasAttribute("params") && references.get(tag.getAttribute("name")).getParams() == null) {
            MVEngine.Exceptions.__throw__(new InvalidGuiFileException("\"ref\" tag with the name \"" + tag.getAttribute("name") + "\" has params, but it's declaration not!"));
            return null;
        }

        if(!tag.hasAttribute("params") && references.get(tag.getAttribute("name")).getParams() != null) {
            MVEngine.Exceptions.__throw__(new InvalidGuiFileException("\"ref\" tag with the name \"" + tag.getAttribute("name") + "\" has no params, but it's declaration does!"));
            return null;
        }

        if(tag.hasAttribute("params")) {
            String[] args = Arrays.stream(getStringAttrib(tag.getAttribute("params")).replace("[", "").replace("]", "").split(",")).map(this::getStringAttrib).toList().toArray(new String[0]);
            String[] params = references.get(tag.getAttribute("name")).getParams();

            if(args.length != params.length) {
                MVEngine.Exceptions.__throw__(new InvalidGuiFileException("\"ref\" tag with the name \"" + tag.getAttribute("name") + "\" has a different amount of params than it's declaration!"));
            }

            for (int i = 0; i < args.length; i++) {
                currentRefLookup.put(params[i], args[i]);
            }

            return parseElement(references.get(tag.getAttribute("name")).getPointer());
        }

        return parseElement(references.get(tag.getAttribute("name")).getPointer());
    }

    //elements
    private TextLine parseTextLine(Element tag) {
        TextLine line = new TextLine(window,
            tag.hasAttribute("x") ? getIntAttrib(tag.getAttribute("x")) : 0,
            tag.hasAttribute("y") ? getIntAttrib(tag.getAttribute("y")) : 0,
            getIntAttrib(tag.getAttribute("height")));
        line.setText(tag.getTextContent());
        return line;
    }

    private Button parseButton(Element tag) {
        Button button = new Button(window,
            tag.hasAttribute("x") ? getIntAttrib(tag.getAttribute("x")) : 0,
            tag.hasAttribute("y") ? getIntAttrib(tag.getAttribute("y")) : 0,
            getIntAttrib(tag.getAttribute("width")),
            getIntAttrib(tag.getAttribute("height")));
        button.setText(tag.getTextContent());
        if (tag.hasAttribute("onclick") || tag.hasAttribute("onClick")) {
            String methodStr = tag.hasAttribute("onclick") ? tag.getAttribute("onclick") : tag.getAttribute("onClick");
            ParsedClickMethod method = parseClickMethod(methodStr, Button.class);
            if (method.addSelf) method.params[method.selfPos] = button;
            button.setClickMethod(method.name, method.types, method.params);
        }
        return button;
    }

    private ImageButton parseImageButton(Element tag) {
        ImageButton imageButton = new ImageButton(window,
            tag.hasAttribute("x") ? getIntAttrib(tag.getAttribute("x")) : 0,
            tag.hasAttribute("y") ? getIntAttrib(tag.getAttribute("y")) : 0,
            getIntAttrib(tag.getAttribute("width")),
            getIntAttrib(tag.getAttribute("height")));
        if(tag.hasAttribute("texture")) {
            imageButton.setTexture(R.getTexture(getStringAttrib(tag.getAttribute("texture"))));
        }
        return imageButton;
    }

    private Checkbox parseCheckbox(Element tag) {
        Checkbox checkbox = new Checkbox(window,
            tag.hasAttribute("x") ? getIntAttrib(tag.getAttribute("x")) : 0,
            tag.hasAttribute("y") ? getIntAttrib(tag.getAttribute("y")) : 0,
            getIntAttrib(tag.getAttribute("width")),
            getIntAttrib(tag.getAttribute("height")));
        checkbox.setText(getStringAttrib(tag.getTextContent()));
        return checkbox;
    }

    private ProgressBar parseProgressBar(Element tag) {
        ProgressBar progressBar = new ProgressBar(window,
            tag.hasAttribute("x") ? getIntAttrib(tag.getAttribute("x")) : 0,
            tag.hasAttribute("y") ? getIntAttrib(tag.getAttribute("y")) : 0,
            getIntAttrib(tag.getAttribute("width")),
            getIntAttrib(tag.getAttribute("height")));
        if(tag.hasAttribute("maxValue")) {
            progressBar.setTotalValue(getIntAttrib(tag.getAttribute("maxValue")));
        }
        if(tag.hasAttribute("currentValue")) {
            progressBar.setCurrentValue(getIntAttrib(tag.getAttribute("currentValue")));
        }

        return progressBar;
    }

    private InputBox parseInputBox(Element tag) {
        InputBox inputBox = new InputBox(window,
            tag.hasAttribute("x") ? getIntAttrib(tag.getAttribute("x")) : 0,
            tag.hasAttribute("y") ? getIntAttrib(tag.getAttribute("y")) : 0,
            getIntAttrib(tag.getAttribute("width")),
            getIntAttrib(tag.getAttribute("height")));
        inputBox.setPlaceholderText(getStringAttrib(tag.getTextContent()));
        if(tag.hasAttribute("limit")) {
            inputBox.setLimit(getIntAttrib(tag.getAttribute("limit")));
        }
        return inputBox;
    }

    private NumericInputBox parseNumericInputBox(Element tag) {
        NumericInputBox numericInputBox = new NumericInputBox(window,
            tag.hasAttribute("x") ? getIntAttrib(tag.getAttribute("x")) : 0,
            tag.hasAttribute("y") ? getIntAttrib(tag.getAttribute("y")) : 0,
            getIntAttrib(tag.getAttribute("width")),
            getIntAttrib(tag.getAttribute("height")));
        numericInputBox.setPlaceholderText(getStringAttrib(tag.getTextContent()));
        if(tag.hasAttribute("limit")) {
            numericInputBox.setLimit(getIntAttrib(tag.getAttribute("limit")));
        }
        return numericInputBox;
    }

    private PasswordInputBox parsePasswordInputBox(Element tag) {
        PasswordInputBox passwordInputBox = new PasswordInputBox(window,
            tag.hasAttribute("x") ? getIntAttrib(tag.getAttribute("x")) : 0,
            tag.hasAttribute("y") ? getIntAttrib(tag.getAttribute("y")) : 0,
            getIntAttrib(tag.getAttribute("width")),
            getIntAttrib(tag.getAttribute("height")));
        passwordInputBox.setPlaceholderText(getStringAttrib(tag.getTextContent()));
        if(tag.hasAttribute("limit")) {
            passwordInputBox.setLimit(getIntAttrib(tag.getAttribute("limit")));
        }
        return passwordInputBox;
    }

    private dev.mv.engine.gui.components.Element parseInput(Element tag) {
        return switch (tag.getAttribute("type")) {
            default -> null;
            case "string" -> parseInputBox(tag);
            case "number" -> parseNumericInputBox(tag);
            case "password" -> parsePasswordInputBox(tag);
        };
    }

    private Separator parseSeparator(Element tag) {
        Separator separator = new Separator(window,
            tag.hasAttribute("x") ? getIntAttrib(tag.getAttribute("x")) : 0,
            tag.hasAttribute("y") ? getIntAttrib(tag.getAttribute("y")) : 0,
            getIntAttrib(tag.getAttribute("width")),
            getIntAttrib(tag.getAttribute("height")));
        return separator;
    }

    private Space parseSpace(Element tag) {
        Space separator = new Space(window,
            tag.hasAttribute("x") ? getIntAttrib(tag.getAttribute("x")) : 0,
            tag.hasAttribute("y") ? getIntAttrib(tag.getAttribute("y")) : 0,
            getIntAttrib(tag.getAttribute("width")),
            getIntAttrib(tag.getAttribute("height")));
        return separator;
    }

    //layouts
    private VerticalLayout parseVerticalLayout(Element tag) {
        VerticalLayout layout = new VerticalLayout(window,
            tag.hasAttribute("x") ? getIntAttrib(tag.getAttribute("x")) : 0,
            tag.hasAttribute("y") ? getIntAttrib(tag.getAttribute("y")) : 0);

        if(tag.hasAttribute("spacing")) {
            layout.setSpacing(getIntAttrib(tag.getAttribute("spacing")));
        }
        if(tag.hasAttribute("align")) {
            layout.alignContent(VerticalLayout.Align.valueOf(getStringAttrib(tag.getAttribute("align"))));
        }
        if(tag.hasAttribute("showFrame")) {
            if(getBooleanAttrib(tag.getAttribute("showFrame"))) {
                layout.showFrame();
            }
        }
        if(tag.hasAttribute("padding")) {
            String padding = getStringAttrib(tag.getAttribute("padding"));
            if(padding.startsWith("[") && padding.endsWith("]")) {
                int[] values = Arrays.stream(padding.replace("[", "").replace("]", "").split(",")).mapToInt(this::getIntAttrib).toArray();
                if(values.length == 1) {
                    values = new int[] {values[0], values[0], values[0], values[0]};
                }
                if(values.length == 2) {
                    int h = values[0];
                    int v = values[1];
                    values = new int[4];
                    values[0] = h;
                    values[1] = h;
                    values[2] = v;
                    values[3] = v;
                }
                layout.setPadding(values[0], values[1], values[2], values[3]);
            } else {
                int padd = Integer.parseInt(padding);
                layout.setPadding(padd, padd, padd, padd);
            }
        }

        if(tag.hasChildNodes()) {
            NodeList nodeList = tag.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node element = nodeList.item(i);
                if(element.getNodeType() == Node.ELEMENT_NODE) {
                    layout.addElement(parseElement((Element) element));
                }
            }
        }

        return layout;
    }

    private HorizontalLayout parseHorizontalLayout(Element tag) {
        HorizontalLayout layout = new HorizontalLayout(window,
            tag.hasAttribute("x") ? getIntAttrib(tag.getAttribute("x")) : 0,
            tag.hasAttribute("y") ? getIntAttrib(tag.getAttribute("y")) : 0);

        if(tag.hasAttribute("spacing")) {
            layout.setSpacing(getIntAttrib(tag.getAttribute("spacing")));
        }
        if(tag.hasAttribute("align")) {
            layout.alignContent(HorizontalLayout.Align.valueOf(getStringAttrib(tag.getAttribute("align"))));
        }
        if(tag.hasAttribute("showFrame")) {
            if(getBooleanAttrib(tag.getAttribute("showFrame"))) {
                layout.showFrame();
            }
        }
        if(tag.hasAttribute("padding")) {
            String padding = getStringAttrib(tag.getAttribute("padding"));
            if(padding.startsWith("[") && padding.endsWith("]")) {
                int[] values = Arrays.stream(padding.replace("[", "").replace("]", "").split(",")).mapToInt(this::getIntAttrib).toArray();
                if(values.length == 1) {
                    values = new int[] {values[0], values[0], values[0], values[0]};
                }
                if(values.length == 2) {
                    int h = values[0];
                    int v = values[1];
                    values = new int[4];
                    values[0] = h;
                    values[1] = h;
                    values[2] = v;
                    values[3] = v;
                }
                layout.setPadding(values[0], values[1], values[2], values[3]);
            } else {
                int padd = Integer.parseInt(padding);
                layout.setPadding(padd, padd, padd, padd);
            }
        }

        if(tag.hasChildNodes()) {
            NodeList nodeList = tag.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node element = nodeList.item(i);
                if(element.getNodeType() == Node.ELEMENT_NODE) {
                    layout.addElement(parseElement((Element) element));
                }
            }
        }

        return layout;
    }

    private CollapseMenu parseCollapseMenu(Element tag) {
        CollapseMenu layout = new CollapseMenu(window,
            tag.hasAttribute("x") ? getIntAttrib(tag.getAttribute("x")) : 0,
            tag.hasAttribute("y") ? getIntAttrib(tag.getAttribute("y")) : 0,
            getIntAttrib(tag.getAttribute("width")),
            getIntAttrib(tag.getAttribute("height")), null);

        layout.setText(getStringAttrib(tag.getAttribute("title")));

        if(tag.hasChildNodes()) {
            NodeList nodeList = tag.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node element = nodeList.item(i);
                if(element.getNodeType() == Node.ELEMENT_NODE) {
                    layout.addElement(parseElement((Element) element));
                }
            }
        }

        return layout;
    }

    private Choice parseChoice(Element tag) {
        Choice choice = new Choice(window,
            tag.hasAttribute("x") ? getIntAttrib(tag.getAttribute("x")) : 0,
            tag.hasAttribute("y") ? getIntAttrib(tag.getAttribute("y")) : 0,
            getIntAttrib(tag.getAttribute("width")),
            getIntAttrib(tag.getAttribute("height")));
        choice.setText(getStringAttrib(tag.getTextContent()));
        return choice;
    }

    private ChoiceGroup parseChoiceGroup(Element tag) {
        ChoiceGroup choiceGroup = new ChoiceGroup(window, null);

        if(tag.hasChildNodes()) {
            NodeList nodeList = tag.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node element = nodeList.item(i);
                if(element.getNodeType() == Node.ELEMENT_NODE) {
                    if(element.getNodeName().equals("choice")) {
                        choiceGroup.addChoice(parseChoice((Element) element));
                    }
                }
            }
        }

        return choiceGroup;
    }
}
