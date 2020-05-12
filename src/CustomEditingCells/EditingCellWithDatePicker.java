package CustomEditingCells;

import Main.AlertBox;
import com.jfoenix.controls.JFXDatePicker;
import javafx.application.Platform;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.input.KeyCode;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public abstract class EditingCellWithDatePicker<S, T> extends TableCell<S, T> {
    private JFXDatePicker datePicker;

    public EditingCellWithDatePicker() {
    }

    @Override
    public void startEdit() {
        super.startEdit();
        if (datePicker == null) {
            createDatePicker();
        }
        setGraphic(datePicker);
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        Platform.runLater(() -> {
            datePicker.requestFocus();
        });
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        setText(getItem().toString());
        setContentDisplay(ContentDisplay.TEXT_ONLY);
    }

    @Override
    public void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            if (isEditing()) {
                if (datePicker != null) {
                    datePicker.setValue(LocalDate.parse(getItem().toString()));
                }
                setGraphic(datePicker);
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            } else {
                setText(getString());
                setContentDisplay(ContentDisplay.TEXT_ONLY);
            }
        }
    }

    private void createDatePicker() {
        datePicker = new JFXDatePicker();
        datePicker.setConverter(new StringConverter<>() {

            private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

            @Override
            public String toString(LocalDate object) {
                if (object == null) return "";
                else return dateTimeFormatter.format(object);
            }

            @Override
            public LocalDate fromString(String string) {
                if(string == null || string.trim().isEmpty()) return null;
                return LocalDate.parse(string, dateTimeFormatter);
            }
        });
        datePicker.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
        datePicker.setOnKeyPressed(t -> {
            if (t.getCode() == KeyCode.ENTER) {
                if (datePicker.getValue() == null) {
                    AlertBox.display("错误", "日期格式输入错误");
                    datePicker.setValue(LocalDate.now());
                    return;
                }
                commitEdit((T) new Main.Date(datePicker.getValue().getYear(),
                        datePicker.getValue().getMonthValue(),
                        datePicker.getValue().getDayOfMonth()));
                t.consume();
            } else if (t.getCode() == KeyCode.ESCAPE) {
                cancelEdit();
            } else if (t.getCode() == KeyCode.TAB) {
                if (datePicker.getValue() == null) {
                    AlertBox.display("错误", "日期格式输入错误");
                    datePicker.setValue(LocalDate.now());
                    return;
                }
                commitEdit((T) new Main.Date(datePicker.getValue().getYear(),
                        datePicker.getValue().getMonthValue(),
                        datePicker.getValue().getDayOfMonth()));
                t.consume();
                TableColumn nextColumn = getNextColumn(!t.isShiftDown());
                if (nextColumn != null) {
                    getTableView().edit(getTableRow().getIndex(), nextColumn);
                }
            }
        });
    }
    private String getString() {
        return getItem() == null ? "" : getItem().toString();
    }

    /**
     *
     * @param forward true gets the column to the right, false the column to the left of the current column
     * @return
     */
    private TableColumn<S, ?> getNextColumn(boolean forward) {
        List<TableColumn<S, ?>> columns = new ArrayList<>();
        for (TableColumn<S, ?> column : getTableView().getColumns()) {
            columns.addAll(getLeaves(column));
        }
        //There is no other column that supports editing.
        if (columns.size() < 2) {
            return null;
        }
        int currentIndex = columns.indexOf(getTableColumn());
        int nextIndex = currentIndex;
        if (forward) {
            nextIndex++;
            if (nextIndex > columns.size() - 1) {
                nextIndex = 0;
            }
        } else {
            nextIndex--;
            if (nextIndex < 0) {
                nextIndex = columns.size() - 1;
            }
        }
        return columns.get(nextIndex);
    }

    private List<TableColumn<S, ?>> getLeaves(TableColumn<S, ?> root) {
        List<TableColumn<S, ?>> columns = new ArrayList<>();
        if (root.getColumns().isEmpty()) {
            //We only want the leaves that are editable.
            if (root.isEditable()) {
                columns.add(root);
            }
            return columns;
        } else {
            for (TableColumn<S, ?> column : root.getColumns()) {
                columns.addAll(getLeaves(column));
            }
            return columns;
        }
    }
}
