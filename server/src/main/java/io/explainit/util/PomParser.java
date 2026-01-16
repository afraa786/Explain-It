package io.explainit.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.nio.file.Path;
import java.util.*;

public class PomParser {
    
    private static final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    
    static {
        try {
            dbFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            dbFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            dbFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        } catch (Exception e) {
            // Ignore
        }
    }
    
    public static Map<String, String> parsePomProperties(Path pomPath) {
        Map<String, String> properties = new HashMap<>();
        try {
            DocumentBuilder db = dbFactory.newDocumentBuilder();
            Document doc = db.parse(pomPath.toFile());
            
            Element root = doc.getDocumentElement();
            
            // Extract java.version
            NodeList propertyNodes = doc.getElementsByTagName("properties");
            if (propertyNodes.getLength() > 0) {
                Element propsElement = (Element) propertyNodes.item(0);
                NodeList children = propsElement.getChildNodes();
                for (int i = 0; i < children.getLength(); i++) {
                    if (children.item(i) instanceof Element) {
                        Element child = (Element) children.item(i);
                        properties.put(child.getTagName(), child.getTextContent());
                    }
                }
            }
        } catch (Exception e) {
            // Return empty properties on error
        }
        return properties;
    }
    
    public static List<Dependency> parsePomDependencies(Path pomPath) {
        List<Dependency> dependencies = new ArrayList<>();
        try {
            DocumentBuilder db = dbFactory.newDocumentBuilder();
            Document doc = db.parse(pomPath.toFile());
            
            NodeList depNodes = doc.getElementsByTagName("dependency");
            for (int i = 0; i < depNodes.getLength(); i++) {
                Element depElement = (Element) depNodes.item(i);
                String groupId = getElementText(depElement, "groupId");
                String artifactId = getElementText(depElement, "artifactId");
                String version = getElementText(depElement, "version");
                
                if (!groupId.isEmpty() && !artifactId.isEmpty()) {
                    dependencies.add(new Dependency(groupId, artifactId, version));
                }
            }
        } catch (Exception e) {
            // Return empty list on error
        }
        return dependencies;
    }
    
    public static String getSpringBootVersion(Path pomPath) {
        try {
            DocumentBuilder db = dbFactory.newDocumentBuilder();
            Document doc = db.parse(pomPath.toFile());
            
            NodeList parentNodes = doc.getElementsByTagName("parent");
            if (parentNodes.getLength() > 0) {
                Element parentElement = (Element) parentNodes.item(0);
                String artifactId = getElementText(parentElement, "artifactId");
                if ("spring-boot-starter-parent".equals(artifactId)) {
                    return getElementText(parentElement, "version");
                }
            }
        } catch (Exception e) {
            // Return empty on error
        }
        return "";
    }
    
    private static String getElementText(Element element, String tagName) {
        NodeList nodes = element.getElementsByTagName(tagName);
        if (nodes.getLength() > 0) {
            return nodes.item(0).getTextContent().trim();
        }
        return "";
    }
    
    public static class Dependency {
        public String groupId;
        public String artifactId;
        public String version;
        
        public Dependency(String groupId, String artifactId, String version) {
            this.groupId = groupId;
            this.artifactId = artifactId;
            this.version = version;
        }
        
        @Override
        public String toString() {
            return groupId + ":" + artifactId + ":" + version;
        }
    }
}
