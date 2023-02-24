package dev.mv.engine.gui.parsing.theme;

import dev.mv.engine.MVEngine;
import dev.mv.engine.gui.components.animations.ElementAnimation;
import dev.mv.engine.gui.parsing.InvalidGuiFileException;
import dev.mv.engine.gui.parsing.GuiConfig;
import dev.mv.engine.gui.theme.Theme;
import dev.mv.engine.render.shared.Color;
import dev.mv.utils.Utils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.util.function.IntUnaryOperator;
import java.util.function.UnaryOperator;

public class ThemeParser {
    private File file;
    private String themePath;

    public ThemeParser(GuiConfig guiConfig) {
        themePath = guiConfig.getThemePath();
    }

    public Theme parse(String name) throws IOException {
        this.file = new File(themePath + name);
        if(!file.exists()) {
            throw new IOException("Could not find file \"" + name + "\" inside the given themePath!");
        }
        Theme returnTheme = new Theme();

        try{
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(file);
            document.getDocumentElement().normalize();

            if (!document.getDocumentElement().getTagName().equals("theme")) {
                MVEngine.Exceptions.__throw__(new InvalidGuiFileException("Root should be \"theme\""));
            }

            NodeList tags = document.getDocumentElement().getChildNodes();

            for(int i = 0; i < tags.getLength(); i++) {
                Node tag = tags.item(i);
                if(tag.getNodeType() == Node.ELEMENT_NODE) {
                    if(tag.getNodeName().equals("colors")) {
                        parseColors(tag, returnTheme);
                    }
                    if(tag.getNodeName().equals("general")) {
                        parseGeneral(tag, returnTheme);
                    }
                    if(tag.getNodeName().equals("animations")) {
                        parseAnimations(tag, returnTheme);
                    }
                }
            }
        } catch (Exception e) {
            MVEngine.Exceptions.__throw__(e);
        }

        return returnTheme;
    }

    private void parseColors(Node colorTag, Theme theme) {
        NodeList tags = colorTag.getChildNodes();
        for(int i = 0; i < tags.getLength(); i++) {
            Node tag = tags.item(i);
            if(tag.getNodeType() == Node.ELEMENT_NODE) {
                if(tag.getNodeName().equals("enabled")) {
                    NodeList enabledColors = tag.getChildNodes();
                    for(int j = 0; j < enabledColors.getLength(); j++) {
                        Node enabledColor = enabledColors.item(j);
                        if(enabledColor.getNodeType() == Node.ELEMENT_NODE) {
                            switch (enabledColor.getNodeName()) {
                                case "base" -> theme.setBaseColor(parseColor(enabledColor.getTextContent()));
                                case "outline" -> theme.setOutlineColor(parseColor(enabledColor.getTextContent()));
                                case "text" -> theme.setText_base(parseColor(enabledColor.getTextContent()));
                                case "extra" -> theme.setExtraColor(parseColor(enabledColor.getTextContent()));
                            }
                        }
                    }
                }
                if(tag.getNodeName().equals("disabled")) {
                    NodeList disabledColors = tag.getChildNodes();
                    for(int j = 0; j < disabledColors.getLength(); j++) {
                        Node enabledColor = disabledColors.item(j);
                        if(enabledColor.getNodeType() == Node.ELEMENT_NODE) {
                            switch (enabledColor.getNodeName()) {
                                case "base" -> theme.setDisabledBaseColor(parseColor(enabledColor.getTextContent()));
                                case "outline" -> theme.setDisabledOutlineColor(parseColor(enabledColor.getTextContent()));
                                case "text" -> theme.setDisabledTextColor(parseColor(enabledColor.getTextContent()));
                                case "extra" -> theme.setDiabledExtraColor(parseColor(enabledColor.getTextContent()));
                            }
                        }
                    }
                }
                if(tag.getNodeName().equals("shouldUseTextColor")) {
                    NodeList useTextColors = tag.getChildNodes();
                    for(int j = 0; j < useTextColors.getLength(); j++) {
                        Node useTextColor = useTextColors.item(j);
                        if(useTextColor.getNodeType() == Node.ELEMENT_NODE) {
                            switch (useTextColor.getNodeName()) {
                                case "checkbox" -> theme.setShouldCheckboxUseTextColor(Boolean.parseBoolean(useTextColor.getTextContent()));
                                case "choice" -> theme.setShouldChoiceUseTextColor(Boolean.parseBoolean(useTextColor.getTextContent()));
                                case "inputBox" -> theme.setShouldPasswordInputBoxButtonUseTextColor(Boolean.parseBoolean(useTextColor.getTextContent()));
                            }
                        }
                    }
                }
            }
        }
    }

    private void parseGeneral(Node generalTag, Theme theme) {
        NodeList tags = generalTag.getChildNodes();
        for(int i = 0; i < tags.getLength(); i++) {
            Node tag = tags.item(i);
            if (tag.getNodeType() == Node.ELEMENT_NODE) {
                if(tag.getNodeName().equals("edges")) {
                    NodeList edges = tag.getChildNodes();
                    for(int j = 0; j < edges.getLength(); j++) {
                        Node edgeProp = edges.item(j);
                        if (edgeProp.getNodeType() == Node.ELEMENT_NODE) {
                            switch (edgeProp.getNodeName()) {
                                case "style" -> theme.setEdgeStyle(Theme.EdgeStyle.valueOf(edgeProp.getTextContent()));
                                case "radius" -> theme.setEdgeRadius(Integer.parseInt(edgeProp.getTextContent()));
                            }
                        }
                    }
                }
                if(tag.getNodeName().equals("outline")) {
                    NodeList outlines = tag.getChildNodes();
                    for(int j = 0; j < outlines.getLength(); j++) {
                        Node outlineProp = outlines.item(j);
                        if (outlineProp.getNodeType() == Node.ELEMENT_NODE) {
                            switch (outlineProp.getNodeName()) {
                                case "thickness" -> theme.setOutlineThickness(Integer.parseInt(outlineProp.getTextContent()));
                                case "use" -> theme.setHasOutline(Boolean.parseBoolean(outlineProp.getTextContent()));
                            }
                        }
                    }
                }
                if(tag.getNodeName().equals("guiAssets")) {
                    NodeList assets = tag.getChildNodes();
                    for(int j = 0; j < assets.getLength(); j++) {
                        Node assetProp = assets.item(j);
                        if (assetProp.getNodeType() == Node.ELEMENT_NODE) {
                            if(assetProp.getNodeName().equals("path")) {
                                theme.setGuiAssetPath(assetProp.getTextContent());
                            }
                            if(assetProp.getNodeName().equals("icons")) {
                                NodeList icons = assetProp.getChildNodes();
                                for (int k = 0; k < icons.getLength(); k++) {
                                    Node iconProp = icons.item(k);
                                    if (iconProp.getNodeType() == Node.ELEMENT_NODE) {
                                        switch (iconProp.getNodeName()) {
                                            case "width" -> theme.setGuiAssetsIconWidth(Integer.parseInt(iconProp.getTextContent()));
                                            case "height" -> theme.setGuiAssetsIconHeight(Integer.parseInt(iconProp.getTextContent()));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void parseAnimations(Node animationTag, Theme theme) {
        UnaryOperator<ElementAnimation.AnimationState> inOperator = (v) -> v;
        UnaryOperator<ElementAnimation.AnimationState> outOperator = (v) -> v;

        NodeList tags = animationTag.getChildNodes();
        for (int i = 0; i < tags.getLength(); i++) {
            Node tag = tags.item(i);
            if(tag.getNodeType() == Node.ELEMENT_NODE) {
                if(tag.getNodeName().equals("frameCount")) {
                    theme.setAnimationFrames(Integer.parseInt(tag.getTextContent()));
                }
                if(tag.getNodeName().equals("times")) {
                    NodeList times = tag.getChildNodes();
                    for (int j = 0; j < times.getLength(); j++) {
                        Node time = times.item(j);
                        if(time.getNodeType() == Node.ELEMENT_NODE) {
                            switch (time.getNodeName()) {
                                case "in" -> theme.setAnimationInTime(Integer.parseInt(time.getTextContent()));
                                case "out" -> theme.setAnimationOutTime(Integer.parseInt(time.getTextContent()));
                            }
                        }
                    }
                }
                if(tag.getNodeName().equals("animation")) {
                    NodeList inOut = tag.getChildNodes();
                    for (int j = 0; j < inOut.getLength(); j++) {
                        Node inOutTag = inOut.item(j);
                        if(inOutTag.getNodeType() == Node.ELEMENT_NODE) {
                            if(inOutTag.getNodeName().equals("in")) {
                                inOperator = parseAnimationProcedure(inOutTag);
                            }
                            if(inOutTag.getNodeName().equals("out")) {
                                outOperator = parseAnimationProcedure(inOutTag);
                            }
                        }
                    }
                }
            }
        }

        final UnaryOperator<ElementAnimation.AnimationState> finalInOperator = inOperator;
        final UnaryOperator<ElementAnimation.AnimationState> finalOutOperator = outOperator;
        theme.setAnimator(new ElementAnimation() {
            @Override
            public AnimationState transform(int frame, int totalFrames, AnimationState lastState) {
                return finalInOperator.apply(lastState);
            }

            @Override
            public AnimationState transformBack(int frame, int totalFrames, AnimationState lastState) {
                return finalOutOperator.apply(lastState);
            }
        });
    }

    private UnaryOperator<ElementAnimation.AnimationState> parseAnimationProcedure(Node animationTag) {
        final IntUnaryOperator[] intOperators = new IntUnaryOperator[7];
        IntUnaryOperator widthOperator =    (v) -> v;
        IntUnaryOperator heightOperator =   (v) -> v;
        IntUnaryOperator xOperator =        (v) -> v;
        IntUnaryOperator yOperator =        (v) -> v;
        IntUnaryOperator rotationOperator = (v) -> v;
        IntUnaryOperator originXOperator =  (v) -> v;
        IntUnaryOperator originYOperator =  (v) -> v;

        final UnaryOperator<Color>[] colorOperators = (UnaryOperator<Color>[]) new UnaryOperator[4];
        UnaryOperator<Color> baseColorOperator =    (v) -> v;
        UnaryOperator<Color> outlineColorOperator = (v) -> v;
        UnaryOperator<Color> textColorOperator =    (v) -> v;
        UnaryOperator<Color> extraColorOperator =   (v) -> v;

        NodeList operations = animationTag.getChildNodes();
        for (int i = 0; i < operations.getLength(); i++) {
            Node operation = operations.item(i);
            if(operation.getNodeName().equals("add")) {
                Element operationElement = (Element) operation;
                int value = Integer.parseInt(operationElement.getAttribute("value"));
                NodeList props = operation.getChildNodes();
                for (int j = 0; j < props.getLength(); j++) {
                    Node prop = props.item(j);
                    if(prop.getNodeType() == Node.ELEMENT_NODE) {
                        Element propElement = (Element) prop;
                        if (propElement.getTagName().equals("property")) {
                            switch (propElement.getAttribute("name")) {
                                case "width" ->         widthOperator =     (width) -> width + value;
                                case "height" ->        heightOperator =    (height) -> height + value;
                                case "x" ->             xOperator =         (x) -> x + value;
                                case "y" ->             yOperator =         (y) -> y + value;
                                case "rotation" ->      rotationOperator =  (rotation) -> rotation + value;
                                case "originX" ->       originXOperator =   (originX) -> originX + value;
                                case "originY" ->       originYOperator =   (originY) -> originY + value;
                            }
                        }
                        if(propElement.getTagName().equals("color")) {
                            switch (propElement.getAttribute("name")) {
                                case "base" ->      baseColorOperator =     generateColorOperator(propElement.getAttribute("value"), value, true);
                                case "outline" ->   outlineColorOperator =  generateColorOperator(propElement.getAttribute("value"), value, true);
                                case "text" ->      textColorOperator =     generateColorOperator(propElement.getAttribute("value"), value, true);
                                case "extra" ->     extraColorOperator =    generateColorOperator(propElement.getAttribute("value"), value, true);
                            }
                        }
                    }
                }
            }
            if(operation.getNodeName().equals("substract")) {
                Element operationElement = (Element) operation;
                int value = Integer.parseInt(operationElement.getAttribute("value"));
                NodeList props = operation.getChildNodes();
                for (int j = 0; j < props.getLength(); j++) {
                    Node prop = props.item(j);
                    if(prop.getNodeType() == Node.ELEMENT_NODE) {
                        Element propElement = (Element) prop;
                        if (propElement.getTagName().equals("property")) {
                            switch (propElement.getAttribute("name")) {
                                case "width" ->         widthOperator =     (width) -> width - value;
                                case "height" ->        heightOperator =    (height) -> height - value;
                                case "x" ->             xOperator =         (x) -> x - value;
                                case "y" ->             yOperator =         (y) -> y - value;
                                case "rotation" ->      rotationOperator =  (rotation) -> rotation - value;
                                case "originX" ->       originXOperator =   (originX) -> originX - value;
                                case "originY" ->       originYOperator =   (originY) -> originY - value;
                            }
                        }
                        if(propElement.getTagName().equals("color")) {
                            switch (propElement.getAttribute("name")) {
                                case "base" ->      baseColorOperator =     generateColorOperator(propElement.getAttribute("value"), value, false);
                                case "outline" ->   outlineColorOperator =  generateColorOperator(propElement.getAttribute("value"), value, false);
                                case "text" ->      textColorOperator =     generateColorOperator(propElement.getAttribute("value"), value, false);
                                case "extra" ->     extraColorOperator =    generateColorOperator(propElement.getAttribute("value"), value, false);
                            }
                        }
                    }
                }
            }
        }

        intOperators[0] = widthOperator;
        intOperators[1] = heightOperator;
        intOperators[2] = xOperator;
        intOperators[3] = yOperator;
        intOperators[4] = rotationOperator;
        intOperators[5] = originXOperator;
        intOperators[6] = originYOperator;

        colorOperators[0] = baseColorOperator;
        colorOperators[1] = outlineColorOperator;
        colorOperators[2] = textColorOperator;
        colorOperators[3] = extraColorOperator;

        return (state) -> {
            state.width =       intOperators[0].applyAsInt(state.width);
            state.height =      intOperators[1].applyAsInt(state.height);
            state.posX =        intOperators[2].applyAsInt(state.posX);
            state.posY =        intOperators[3].applyAsInt(state.posY);
            state.rotation =    intOperators[4].applyAsInt(state.rotation);
            state.originX =     intOperators[5].applyAsInt(state.originX);
            state.originY =     intOperators[6].applyAsInt(state.originY);
            Utils.ifNotNull(state.baseColor).then((c) ->    state.baseColor =       colorOperators[0].apply(c));
            Utils.ifNotNull(state.outlineColor).then((c) -> state.outlineColor =    colorOperators[1].apply(c));
            Utils.ifNotNull(state.textColor).then((c) ->    state.textColor =       colorOperators[2].apply(c));
            Utils.ifNotNull(state.extraColor).then((c) ->   state.extraColor =      colorOperators[3].apply(c));
            return state;
        };
    }

    private UnaryOperator<Color> generateColorOperator(String value, int amount, boolean shouldAdd) {
        return switch (value) {
            case "red" ->       (color) -> {color.setRed(color.getRed() + (shouldAdd ? amount : -amount)); return color;};
            case "green" ->     (color) -> {color.setGreen(color.getGreen() + (shouldAdd ? amount : -amount)); return color;};
            case "blue" ->      (color) -> {color.setBlue(color.getBlue() + (shouldAdd ? amount : -amount)); return color;};
            case "alpha" ->     (color) -> {color.setAlpha(color.getAlpha() + (shouldAdd ? amount : -amount)); return color;};
            default -> (v) -> v;
        };
    }

    private Color parseColor(String color) {
        if (color.startsWith("#")) {
            color = color.replaceAll("#", "");
            if (!color.matches("-?[0-9a-fA-F]+")) {
                MVEngine.Exceptions.__throw__(new InvalidGuiFileException("GUI | " + this.file.getName().split(".xml")[0] + " Color parser: # colors must be hexadecimal characters!"));
            }
            String[] colors = color.split("(?<=\\G.{2})");
            if (colors.length < 3 || colors.length > 4) {
                MVEngine.Exceptions.__throw__(new InvalidGuiFileException("GUI | " + this.file.getName().split(".xml")[0] + " Color parser: # colors must contain 6 or 8 characters!"));
            }
            int r = Integer.parseInt(colors[0], 16);
            int g = Integer.parseInt(colors[1], 16);
            int b = Integer.parseInt(colors[2], 16);
            int a = 255;
            if (colors.length == 4) {
                a = Integer.parseInt(colors[3], 16);
            }
            return new Color(r, g, b, a);
        } else if (color.startsWith("0x")) {
            color = color.replaceAll("0x", "");
            if (!color.matches("-?[0-9a-fA-F]+")) {
                MVEngine.Exceptions.__throw__(new InvalidGuiFileException("GUI | " + this.file.getName().split(".xml")[0] + " Color parser: 0x colors must be hexadecimal characters!"));
            }
            String[] colors = color.split("(?<=\\G.{2})");
            if (colors.length < 3 || colors.length > 4) {
                MVEngine.Exceptions.__throw__(new InvalidGuiFileException("GUI | " + this.file.getName().split(".xml")[0] + " Color parser: 0x colors must contain 6 or 8 characters!"));
            }
            int r = Integer.parseInt(colors[0], 16);
            int g = Integer.parseInt(colors[1], 16);
            int b = Integer.parseInt(colors[2], 16);
            int a = 255;
            if (colors.length == 4) {
                a = Integer.parseInt(colors[3], 16);
            }
            return new Color(r, g, b, a);
        } else {
            String split = ",";
            if (color.contains(" ") && color.contains(",")) {
                color = color.replaceAll(" ", "");
            } else if (color.contains(" ")) {
                split = " ";
            }
            String[] colors = color.replaceAll(" ", "").split(split);
            if (colors.length < 3 || colors.length > 4) {
                MVEngine.Exceptions.__throw__(new InvalidGuiFileException("GUI | " + this.file.getName().split(".xml")[0] + " Color parser: colors must contain 3 or 4 sets of numbers!"));
            }
            int r = Integer.parseInt(colors[0]);
            int g = Integer.parseInt(colors[1]);
            int b = Integer.parseInt(colors[2]);
            int a = 255;
            if (colors.length == 4) {
                a = Integer.parseInt(colors[3]);
            }
            return new Color(r, g, b, a);
        }
    }
}
