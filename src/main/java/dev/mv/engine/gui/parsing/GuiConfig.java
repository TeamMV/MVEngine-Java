package dev.mv.engine.gui.parsing;

import dev.mv.engine.MVEngine;
import dev.mv.engine.gui.parsing.InvalidGuiFileException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

public class GuiConfig {
    private String layoutPath;
    private String themePath;
    File configFile;

    public GuiConfig(String configFilePath) throws IOException {
        try {
            configFile = new File(this.getClass().getResource(configFilePath).toURI());
            parse(configFile);
        } catch (URISyntaxException e) {
            MVEngine.Exceptions.Throw(new IOException("Could not find file \"" + configFilePath + "\""));
        } catch (InvalidGuiFileException e) {
            MVEngine.Exceptions.Throw(e);
        }
    }

    private void parse(File inputStream) throws InvalidGuiFileException {
        try{
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);
            document.getDocumentElement().normalize();

            if (!document.getDocumentElement().getTagName().split(":")[0].equals("mvt")) {
                MVEngine.Exceptions.Throw(new InvalidGuiFileException("Namespace should be \"mvt\""));
            }
            if (!document.getDocumentElement().getTagName().split(":")[1].equals("config")) {
                MVEngine.Exceptions.Throw(new InvalidGuiFileException("Root should be \"config\""));
            }

            NodeList tags = document.getDocumentElement().getChildNodes();
            for (int i = 0; i < tags.getLength(); i++) {
                Node tag = tags.item(i);
                if(tag.getNodeType() == Node.ELEMENT_NODE)  {
                    if(tag.getNodeName().equals("themePath")) {
                        if(tag.hasChildNodes()) {
                            NodeList nodeList = tag.getChildNodes();
                            Node relative = null;
                            for (int j = 0; j < nodeList.getLength(); j++) {
                                Node node = nodeList.item(j);
                                if (node.getNodeType() == Node.ELEMENT_NODE) {
                                    if(relative != null) {
                                        throw new InvalidGuiFileException("If \"themePath\" tag has child tags, it should be at most 1 \"relative\" tag!");
                                    }
                                    relative = node;
                                }
                            }
                            if(!relative.getNodeName().equals("relative")) {
                                System.err.println(relative.getNodeName());
                                throw new InvalidGuiFileException("If \"themePath\" tag has child tags, it should be at most 1 \"relative\" tag!");
                            } else {
                                themePath = configFile.getParent() + relative.getTextContent();
                            }
                        } else {
                            themePath = tag.getTextContent();
                        }
                        themePath = themePath.replaceAll("[\n *]", "");
                    }
                    if(tag.getNodeName().equals("layoutPath")) {
                        if(tag.hasChildNodes()) {
                            NodeList nodeList = tag.getChildNodes();
                            Node relative = null;
                            for (int j = 0; j < nodeList.getLength(); j++) {
                                Node node = nodeList.item(j);
                                if (node.getNodeType() == Node.ELEMENT_NODE) {
                                    if(relative != null) {
                                        throw new InvalidGuiFileException("If \"layoutPath\" tag has child tags, it should be at most 1 \"relative\" tag!");
                                    }
                                    relative = node;
                                }
                            }
                            if(!relative.getNodeName().equals("relative")) {
                                throw new InvalidGuiFileException("If \"layoutPath\" tag has child tags, it should be at most 1 \"relative\" tag!");
                            } else {
                                layoutPath = configFile.getParent() + relative.getTextContent();
                            }
                        } else {
                            layoutPath = tag.getTextContent();
                        }
                        layoutPath = layoutPath.replaceAll("[\n *]", "");
                    }
                }
            }



        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new RuntimeException(e);
        }
    }

    public String getLayoutPath() {
        return layoutPath;
    }

    public String getThemePath() {
        return themePath;
    }
}
