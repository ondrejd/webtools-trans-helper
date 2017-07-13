/**
 * @author Ondřej Doněk <ondrejd@gmail.com>
 * @link https://github.com/ondrejd/webtools-trans-helper for the canonical source repository
 * @license https://www.gnu.org/licenses/gpl-3.0.en.html GNU General Public License 3.0
 */

package com.ondrejd;

import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.prefs.*;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.*;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TextField;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.StringConverter;

public class FXMLDocumentController implements Initializable {
    public final static String ALL_FILES = "Všechny soubory";

    private static final String SELECTED_FILE = "selected_file";
    private static final String SELECTED_FILE_DEFAULT = "WebTools/strings.xml";

    private ObservableList<TranslationString> data;
    private FilteredList<TranslationString> filteredData;
    private Preferences prefs;
    
    @FXML
    private ComboBox filesComboBox;
    @FXML
    private TableView<TranslationString> table;
    @FXML
    private TableColumn<TranslationString, String> nameTCol;
    @FXML
    private TableColumn<TranslationString, String> textTCol;
    @FXML
    private TableColumn<TranslationString, String> fileTCol;
    @FXML
    private TextField nameTextField;
    @FXML
    private CheckBox showFileColumnCheckBox;
    
    /**
     * @return Currently selected file.
     */
    private String getSelectedFile() {
        return filesComboBox.getValue().toString();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Set up months combobox
        ObservableList<String> files = FXCollections.observableArrayList(
                ALL_FILES,
                "Ethwork/strings.xml",
                "Intrace/strings.xml",
                "Qute/strings.xml",
                "WebTools/strings.xml");
        filesComboBox.setItems(files);
        filesComboBox.getSelectionModel().selectLast();

        // Load data
        data = XmlDataSource.load();

        // Set up data table
        String _fileName = getSelectedFile();
        filteredData = new FilteredList<>(data, n -> {
            return _fileName.equals(ALL_FILES) ? true : _fileName.equals(n.getFile());
        });
        table.setEditable(true);
        table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        table.getSelectionModel().setCellSelectionEnabled(true);
        table.setItems(filteredData);

        // Set up data table columns
        nameTCol.setCellValueFactory(new PropertyValueFactory<TranslationString,String>("name"));
        textTCol.setCellValueFactory(new PropertyValueFactory<TranslationString,String>("text"));
        fileTCol.setCellValueFactory(new PropertyValueFactory<TranslationString,String>("file"));

        // Focus table
        focusTable();
    }

    /**
     * Save data to XML file - called from {@link ondrejd.Costs}.
     */
    public void saveData() {
        // Save data
        XmlDataSource.save(data);
        // Save user preferences
        try {
            prefs.put(SELECTED_FILE, getSelectedFile());
            prefs.flush();
        } catch(BackingStoreException e) {
            //e.printStackTrace();
            //System.out.print("Exception occured when saving user preferences!");
        }
    }
    
    /**
     * Set table on focus.
     */
    private void focusTable() {
        Platform.runLater(new Runnable() {
            public void run() {
                table.requestFocus();
            }
        });
    }

    /**
     * @param fileName
     */
    private void filterByFile(String fileName) {
        filteredData.setPredicate(n -> {
            return fileName.equals(ALL_FILES) ? true : fileName.equals(n.getFile());
        });
        focusTable();
    }
    
    /**
     * @param name 
     */
    private void filterByName(String name) {
        filteredData.setPredicate(n -> {
            return name.equals(n.getName());
        });
        focusTable();
    }
    
    @FXML
    private void handleFilesComboBoxAction(ActionEvent event) {
        filterByFile(getSelectedFile());
    }
    
    @FXML
    private void handleNameTextViewAction(ActionEvent event) {
       // TODO 
       //String name = "";
       //filterByName(name)
    }

    @FXML
    private void handleFilterByNameAction(ActionEvent event) {
        TranslationString row = table.getSelectionModel().getSelectedItem();
        String name = row.getName();
        nameTextField.setText(name);
        filterByName(name);
    }
    
    @FXML
    private void handleCancelFilterByName(ActionEvent event) {
        nameTextField.setText("");
        handleFilesComboBoxAction(event);
    }
    
    @FXML
    private void handleShowFileColumnCheckBox(ActionEvent event) {
        // TODO
    }

    @FXML
    private void handleSaveAction(ActionEvent event) {
        // TODO
    }
    
}
