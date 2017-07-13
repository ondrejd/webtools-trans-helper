/**
 * @author Ondřej Doněk <ondrejd@gmail.com>
 * @link https://github.com/ondrejd/webtools-trans-helper for the canonical source repository
 * @license https://www.gnu.org/licenses/gpl-3.0.en.html GNU General Public License 3.0
 */

package com.ondrejd;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

public class TranslationString {
    private final SimpleStringProperty name;
    private final SimpleStringProperty text;
    private final SimpleStringProperty file;
    private final SimpleBooleanProperty translatable;

    /**
     * Constructor
     * @param name
     * @param text
     * @param file
     * @param translatable 
     */
    public TranslationString(String name, String text, String file, Boolean translatable) {
        this.name = new SimpleStringProperty(name);
        this.text = new SimpleStringProperty(text);
        this.file = new SimpleStringProperty(file);
        this.translatable = new SimpleBooleanProperty(translatable);
    }

    /**
     * @return Boolean
     */
    public Boolean isTranslatable() {
        return translatable.get();
    }

    /**
     * @return String
     */
    public String getName() {
        return name.get();
    }

    /**
     * @param name
     */
    public void setName(String name) {
        this.name.set(name);
    }

    /**
     * @return String
     */
    public String getText() {
        return text.get();
    }

    /**
     * @param text
     */
    public void setText(String text) {
        this.text.set(text);
    }

    /**
     * @return String
     */
    public String getFile() {
        return file.get();
    }

    /**
     * @param file
     */
    public void setFile(String file) {
        this.file.set(file);
    }
}
