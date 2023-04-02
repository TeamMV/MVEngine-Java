package dev.mv.engine.resources;

import dev.mv.engine.exceptions.Exceptions;
import dev.mv.engine.exceptions.InvalidGuiFileException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class AssetFileParser {
    private static Map<String, String> files = new HashMap<>();

    public static void mark(InputStream inputStream) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);
            document.getDocumentElement().normalize();

            if (!document.getDocumentElement().getTagName().equals("assets")) {
                Exceptions.send(new InvalidGuiFileException("Root should be \"assets\""));
            }

            NodeList tags = document.getDocumentElement().getChildNodes();
            for (int i = 0; i < tags.getLength(); i++) {
                Node tag = tags.item(i);
                if (tag.getNodeType() == Node.ELEMENT_NODE) {
                    if (tag.getNodeName().equals("files")) {
                        NodeList files = tag.getChildNodes();
                        for (int j = 0; j < files.getLength(); j++) {
                            Node file = files.item(j);
                            if (file.getNodeType() == Node.ELEMENT_NODE) {
                                if (file.getNodeName().equals("file")) {
                                    Element fileTag = (Element) file;
                                    AssetFileParser.files.put(fileTag.getAttribute("name"), fileTag.getAttribute("src"));
                                }
                            }
                        }
                    }
                    if (tag.getNodeName().equals("textures")) {
                        NodeList textures = tag.getChildNodes();
                        for (int j = 0; j < textures.getLength(); j++) {
                            Node texture = textures.item(j);
                            if (texture.getNodeType() == Node.ELEMENT_NODE) {
                                if (texture.getNodeName().equals("texture")) {
                                    Element textureTag = (Element) texture;
                                    if (textureTag.hasAttribute("x") || textureTag.hasAttribute("y") || textureTag.hasAttribute("width") || textureTag.hasAttribute("height") || textureTag.hasAttribute("texture")) {
                                        ResourceLoader.markTextureRegion(textureTag.getAttribute("resId"), textureTag.getAttribute("texture"), Integer.parseInt(textureTag.getAttribute("x")), Integer.parseInt(textureTag.getAttribute("y")), Integer.parseInt(textureTag.getAttribute("width")), Integer.parseInt(textureTag.getAttribute("height")));
                                    } else {
                                        ResourceLoader.markTexture(textureTag.getAttribute("resId"), files.get(textureTag.getAttribute("file")));
                                    }
                                }
                            }
                        }
                    }
                    if (tag.getNodeName().equals("colors")) {
                        NodeList colors = tag.getChildNodes();
                        for (int j = 0; j < colors.getLength(); j++) {
                            Node color = colors.item(j);
                            if (color.getNodeType() == Node.ELEMENT_NODE) {
                                if (color.getNodeName().equals("color")) {
                                    Element colorTag = (Element) color;
                                    ResourceLoader.markColor(colorTag.getAttribute("resId"), colorTag.getTextContent());
                                }
                            }
                        }

                    }
                    if (tag.getNodeName().equals("fonts")) {
                        NodeList fonts = tag.getChildNodes();
                        for (int j = 0; j < fonts.getLength(); j++) {
                            Node font = fonts.item(j);
                            if (font.getNodeType() == Node.ELEMENT_NODE) {
                                if (font.getNodeName().equals("font")) {
                                    Element fontTag = (Element) font;
                                    ResourceLoader.markFont(fontTag.getAttribute("resId"), files.get(fontTag.getAttribute("pngFile")), files.get(fontTag.getAttribute("fntFile")));
                                }
                            }
                        }
                    }
                    if (tag.getNodeName().equals("models")) {
                        NodeList models = tag.getChildNodes();
                        for (int j = 0; j < models.getLength(); j++) {
                            Node model = models.item(j);
                            if (model.getNodeType() == Node.ELEMENT_NODE) {
                                if (model.getNodeName().equals("model")) {
                                    Element modelTag = (Element) model;
                                    ResourceLoader.markModel(modelTag.getAttribute("resId"), files.get(modelTag.getAttribute("file")));
                                }
                            }
                        }
                    }
                    if (tag.getNodeName().equals("gui")) {
                        NodeList guiTags = tag.getChildNodes();
                        for (int j = 0; j < guiTags.getLength(); j++) {
                            Node guiTag = guiTags.item(j);
                            if (guiTag.getNodeType() == Node.ELEMENT_NODE) {
                                if (guiTag.getNodeName().equals("layouts")) {
                                    NodeList layouts = guiTag.getChildNodes();
                                    for (int k = 0; k < layouts.getLength(); k++) {
                                        Node layout = layouts.item(k);
                                        if (layout.getNodeType() == Node.ELEMENT_NODE) {
                                            if (layout.getNodeName().equals("layout")) {
                                                Element layoutTag = (Element) layout;
                                                ResourceLoader.markLayout(layoutTag.getAttribute("resId"), files.get(layoutTag.getAttribute("file")));
                                            }
                                        }
                                    }
                                }
                                if (guiTag.getNodeName().equals("themes")) {
                                    NodeList themes = guiTag.getChildNodes();
                                    for (int k = 0; k < themes.getLength(); k++) {
                                        Node theme = themes.item(k);
                                        if (theme.getNodeType() == Node.ELEMENT_NODE) {
                                            if (theme.getNodeName().equals("theme")) {
                                                Element themeTag = (Element) theme;
                                                ResourceLoader.markTheme(themeTag.getAttribute("resId"), files.get(themeTag.getAttribute("file")));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            Exceptions.send(e);
        }
    }
}
