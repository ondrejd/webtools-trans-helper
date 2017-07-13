/**
 * @author Ondřej Doněk <ondrejd@gmail.com>
 * @link https://github.com/ondrejd/webtools-trans-helper for the canonical source repository
 * @license https://www.gnu.org/licenses/gpl-3.0.en.html GNU General Public License 3.0
 */

package com.ondrejd;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
    
    /**
     * Load data from XML file.
     * @return List of data rows.
     */
    public static ObservableList<TranslationString> loadData() {
        String fileName1 = "/home/ondrejd/Workspace/StringsAll/Ethwork/strings.xml";
        System.out.println(fileName1);
        File file1 = new File(fileName1);
        
        try {
            if(file1.exists()) {
                loadXml(file1);
            }
        } catch(Exception e) {
            e.printStackTrace();
            //data.clear();
        }
        
        String fileName2 = "/home/ondrejd/Workspace/StringsAll/Intrace/strings.xml";
        System.out.println(fileName2);
        File file2 = new File(fileName2);
        try {
            if(file2.exists()) {
                loadXml(file2);
            }
        } catch(Exception e) {
            e.printStackTrace();
            //data.clear();
        }
        
        String fileName3 = "/home/ondrejd/Workspace/StringsAll/Qute/strings.xml";
        System.out.println(fileName3);
        File file3 = new File(fileName3);
        try {
            if(file3.exists()) {
                loadXml(file3);
            }
        } catch(Exception e) {
            e.printStackTrace();
            //data.clear();
        }
        
        String fileName4 = "/home/ondrejd/Workspace/StringsAll/WebTools/strings.xml";
        System.out.println(fileName4);
        File file4 = new File(fileName4);
        try {
            if(file4.exists()) {
                loadXml(file4);
            }
        } catch(Exception e) {
            e.printStackTrace();
            //data.clear();
        }
        
        return data;
    }

    /**
     * Loads XML file.
     * @param file
     */
    private static void loadXml(File file) throws Exception {
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
            Boolean editable = true;
            if(stringElm.hasAttribute("editable")) {
                if(stringElm.getAttribute("editable").equals("false")) {
                    editable = false;
                }
            }
            // Create data row
            data.add(new TranslationString(name, text, fileName, editable));
        }
        //System.out.println("Read file: " + file.getAbsolutePath());
        //System.out.println("Data length: " + data.size());
    }
}
