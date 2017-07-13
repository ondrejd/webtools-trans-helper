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
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.StringConverter;

public class FXMLDocumentController implements Initializable {
    public final static String ALL_FILES = "Všechny soubory";

    private ObservableList<DataRow> data;
    private FilteredList<DataRow> filteredData;
    
    @FXML
    private ComboBox filesComboBox;
    @FXML
    private TableView<DataRow> table;
    @FXML
    private TableColumn<DataRow, ColoredValue<String>> nameTCol;
    @FXML
    private TableColumn<DataRow, ColoredValue<String>> textTCol;

    /**
     * Helper class that holds record about color change (because of undo).
     */
    private class ColorChange {
        private final Integer row;
        private final String column;
        private final ColoredValue.ColorType oldColor;
        private final ColoredValue.ColorType newColor;
        public ColorChange(Integer row, String column, ColoredValue.ColorType oldColor, ColoredValue.ColorType  newColor) {
            this.row = row;
            this.column = column;
            this.oldColor = oldColor;
            this.newColor = newColor;
        }
        public Integer getRow() {
            return row;
        }
        public String getColumn() {
            return column;
        }
        public ColoredValue.ColorType getOldColor() {
            return oldColor;
        }
        public ColoredValue.ColorType getNewColor() {
            return newColor;
        }
    }

    /**
     * Create table cell.
     * @param <T> Used type.
     * @param format Used format.
     * @param supplier Method of {@link DataRow} that supplies value.
     * @return Table cell.
     */
    private <T> TableCell<DataRow, ColoredValue<T>> createTableCell(String format, Function<String, T> supplier) {
        TextFieldTableCell<DataRow, ColoredValue<T>> cell = new TextFieldTableCell<>();
        cell.setConverter(new StringConverter<ColoredValue<T>>() {
            @Override
            public String toString(ColoredValue<T> item) {
                return item == null ? "" : String.format(format, item.getValue());
            }
            @Override
            public ColoredValue<T> fromString(String string) {
                T value = supplier.apply(string);
                ColoredValue.ColorType c = ColoredValue.ColorType.NOCOLOR;
                return new ColoredValue<>(value, c);
            }
        });

        ChangeListener<ColoredValue.ColorType> valListener = (obs, oldState, newState) -> {
            if (newState == ColoredValue.ColorType.YELLOW) {
                cell.setStyle("-fx-background-color: yellow ;");
            } else if (newState == ColoredValue.ColorType.RED) {
                cell.setStyle("-fx-background-color: red ;");
            } else if (newState == ColoredValue.ColorType.GREEN) {
                cell.setStyle("-fx-background-color: #cbe2ae ;");
            } else {
                cell.setStyle("");
            }
        };
        
        cell.itemProperty().addListener((obs, oldItem, newItem) -> {
            if (oldItem != null) {
                oldItem.colorProperty().removeListener(valListener);
            }
            if (newItem == null) {
                cell.setStyle("");
            } else {
                if (newItem.getColor() == ColoredValue.ColorType.YELLOW) {
                    cell.setStyle("-fx-background-color: yellow ;");
                } else if (newItem.getColor() == ColoredValue.ColorType.RED) {
                    cell.setStyle("-fx-background-color: red ;");
                } else if (newItem.getColor() == ColoredValue.ColorType.GREEN) {
                    cell.setStyle("-fx-background-color: #cbe2ae ;");
                } else {
                    cell.setStyle("");
                }
                newItem.colorProperty().addListener(valListener);
            }
        });

        return cell ;
    }
    
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
        data = XmlDataSource.loadData();

        // Set up data table
        //filteredData = new FilteredList<>(data);
        String _fileName = getSelectedFile();
        filteredData = new FilteredList<>(data, n -> {
            return _fileName.equals(ALL_FILES) ? true : _fileName.equals(n.fileProperty().getValue().getValue());
        });
        table.setEditable(true);
        table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        table.getSelectionModel().setCellSelectionEnabled(true);
        table.setItems(filteredData);

        // Set up data table columns
        nameTCol.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        nameTCol.setCellFactory(tc -> createTableCell("%s", String::new));
        nameTCol.setOnEditCommit(
            new EventHandler<TableColumn.CellEditEvent<DataRow, ColoredValue<String>>>() {
                @Override
                public void handle(TableColumn.CellEditEvent<DataRow, ColoredValue<String>> e) {
                    ColoredValue<String> oldVal = e.getOldValue();
                    int idx = e.getTablePosition().getRow();
                    DataRow row = (DataRow) e.getTableView().getItems().get(idx);
                    row.setName(e.getNewValue());
                    // Undo, refresh, focus
                    //undo.add(new UndoAction(UndoActions.UPDATE, idx, placeTCol.getId(), oldVal));
                    e.getTableView().refresh();
                    focusTable();
                }
            }
        );
        textTCol.setCellValueFactory(cellData -> cellData.getValue().textProperty());
        textTCol.setCellFactory(tc -> createTableCell("%s", String::new));
        textTCol.setOnEditCommit(
            new EventHandler<TableColumn.CellEditEvent<DataRow, ColoredValue<String>>>() {
                @Override
                public void handle(TableColumn.CellEditEvent<DataRow, ColoredValue<String>> e) {
                    ColoredValue<String> oldVal = e.getOldValue();
                    int idx = e.getTablePosition().getRow();
                    DataRow row = (DataRow) e.getTableView().getItems().get(idx);
                    row.setText(e.getNewValue());
                    // Undo, refresh, focus
                    //undo.add(new UndoAction(UndoActions.UPDATE, idx, placeTCol.getId(), oldVal));
                    e.getTableView().refresh();
                    focusTable();
                }
            }
        );

        // Focus table
        focusTable();
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
    
    @FXML
    private void handleFilesComboBoxAction(ActionEvent event) {
        switchFile(getSelectedFile());
    }

    /**
     * @param fileName
     */
    private void switchFile(String fileName) {
        filteredData.setPredicate(n -> {
            return fileName.equals(ALL_FILES) ? true : fileName.equals(n.fileProperty().getValue().getValue());
        });
        focusTable();
    }

    @FXML
    private void handleUndoAction(ActionEvent event) {
        // TODO
    }

    @FXML
    private void handleSaveAction(ActionEvent event) {
        // TODO
    }
    
}
