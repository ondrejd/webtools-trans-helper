/**
 * @author Ondřej Doněk <ondrejd@gmail.com>
 * @link https://github.com/ondrejd/webtools-trans-helper for the canonical source repository
 * @license https://www.gnu.org/licenses/gpl-3.0.en.html GNU General Public License 3.0
 */

package com.ondrejd;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XmlDataSource {
    private static final ObservableList<TranslationString> data = FXCollections.<TranslationString>observableArrayList();
    private static final ObservableList<String> xmlFiles = FXCollections.<String>observableArrayList(
            "/home/ondrejd/Workspace/StringsAll/Ethwork/strings.xml",
            "/home/ondrejd/Workspace/StringsAll/Intrace/strings.xml",
            "/home/ondrejd/Workspace/StringsAll/Qute/strings.xml",
            "/home/ondrejd/Workspace/StringsAll/WebTools/strings.xml"
    );

    public static void appendData(ObservableList<TranslationString> d) {
        data.addAll(d);
    }
    
    /**
     * Load data from XML file.
     * @return List of data rows.
     */
    public static ObservableList<TranslationString> load() {
        xmlFiles.forEach(n -> {
            System.out.println("Processing file: " + n);
            ObservableList<TranslationString> d = FXCollections.<TranslationString>observableArrayList();
            File file = new File(n);
    
            try {
                if(file.exists()) {
                    d = loadXmlFile(file);
                }
            } catch(Exception e) {
                e.printStackTrace();
                d.clear();
            }

            data.addAll(d);
        });
        
        return data;
    }

    /**
     * Loads XML file.
     * @param file
     */
    private static ObservableList<TranslationString> loadXmlFile(File file) throws Exception {
        // Prepare data
        ObservableList<TranslationString> fileData = FXCollections.<TranslationString>observableArrayList();
        // Set filename
        String fileName = file.getParentFile().getName() + "/" + file.getName();
        // Load document
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(file);
        // Optional, but recommended, read link below
        // http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
        doc.getDocumentElement().normalize();
        // Get all strings
        NodeList stringsNodes = doc.getElementsByTagName("string");
        // Go ghrough all strings
        for(int i = 0; i < stringsNodes.getLength(); i++) {
            // Get element
            Element stringElm = (Element) stringsNodes.item(i);
            // Get data:
            // name
            String name = stringElm.getAttribute("name");
            // text
            String text = stringElm.getTextContent();
            // editable
            Boolean translatable = true;
            if(stringElm.hasAttribute("translatable")) {
                if(stringElm.getAttribute("translatable").equals("false")) {
                    translatable = false;
                }
            }
            // Create data row
            fileData.add(new TranslationString(name, text, fileName, translatable));
        }

        return fileData;
    }

    /**
     * Save data.
     * @param data 
     */
    public static void save(ObservableList<TranslationString> data) {
        xmlFiles.forEach(fileName -> {
            saveXmlFile(new File(fileName), new FilteredList<>(data, n -> {
                return (n.getFile().contains(fileName));
            }));
        });
    }
    
    /**
     * Save XML file.
     * @param File XML file.
     * @param data Data we want to save.
     */
    private static void saveXmlFile(File file, ObservableList<TranslationString> data) {
        try {
            // Create document
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            // Root element
            Element root = doc.createElement("resources");
            doc.appendChild(root);
            // Go through the strings and construct corresponding XML
            for(int i = 0; i < data.size(); i++) {
                Element stringElm = doc.createElement("string");

                if(data.get(i).isTranslatable() != true) {
                    stringElm.setAttribute("translatable", "false");
                }

                stringElm.setAttribute("name", data.get(i).getName());
                stringElm.setTextContent(data.get(i).getText());
                root.appendChild(stringElm);
            }
            // Write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(file);
            transformer.transform(source, result);
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }
    }
}
