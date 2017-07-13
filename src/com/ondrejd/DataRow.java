/**
 * @author Ondřej Doněk <ondrejd@gmail.com>
 * @link https://github.com/ondrejd/webtools-trans-helper for the canonical source repository
 * @license https://www.gnu.org/licenses/gpl-3.0.en.html GNU General Public License 3.0
 */

package com.ondrejd;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class DataRow {
    private final ObjectProperty<ColoredValue<String>> name = new SimpleObjectProperty<>();
    private final ObjectProperty<ColoredValue<String>> text = new SimpleObjectProperty<>();
    private final ObjectProperty<ColoredValue<String>> file = new SimpleObjectProperty<>();
    
    public DataRow(String name, String text, String file) {
        setName(new ColoredValue<>(name));
        setText(new ColoredValue<>(text));
        setFile(new ColoredValue<>(file));
    }
    
    //name
    public final ObjectProperty<ColoredValue<String>> nameProperty() {
        return this.name;
    }
    
    public final ColoredValue<String> getName() {
        return this.nameProperty().get();
    }
    
    public final void setName(final ColoredValue<String> name) {
        this.nameProperty().set(name);
    }
    
    //text
    public final ObjectProperty<ColoredValue<String>> textProperty() {
        return this.text;
    }
    
    public final ColoredValue<String> getText() {
        return this.textProperty().get();
    }
    
    public final void setText(final ColoredValue<String> text) {
        this.textProperty().set(text);
    }
    
    //file
    public final ObjectProperty<ColoredValue<String>> fileProperty() {
        return this.file;
    }
    
    public final ColoredValue<String> getFile() {
        return this.fileProperty().get();
    }
    
    public final void setFile(final ColoredValue<String> file) {
        this.fileProperty().set(file);
    }
}
