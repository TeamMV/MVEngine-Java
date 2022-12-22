package dev.mv.engine.gui.parsing.gui;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class GuiConfig {
    private String layoutPath;

    public GuiConfig(String configFilePath) throws IOException {
        try {
            File configFile = new File(this.getClass().getResource(configFilePath).toURI());
        } catch (URISyntaxException e) {
            throw new IOException("File path is not correct!");
        }
    }

    private void parse(File configFile) {
        try{
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(configFile);
            document.getDocumentElement().normalize();

            if (!document.getDocumentElement().getTagName().equals("gui")) {
                throw new RuntimeException(new XMLSignatureException("Top element inside a gui layout file should be 'gui'!"));
            }

            NodeList tags = document.getDocumentElement().getChildNodes();



        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new RuntimeException(e);
        }
    }

    public String getLayoutPath() {
        return layoutPath;
    }
}
