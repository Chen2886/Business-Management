package CustomEditingCells;

import Main.AlertBox;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

import java.util.ArrayList;
import java.util.List;

public abstract class EditingCellWithTextFields<S, T> extends TableCell<S, T> {
    private TextField textField;
    private final Class<T> type;

    public EditingCellWithTextFields(Class<T> type) {
        this.type = type;
    }

    @Override
    public void startEdit() {
        super.startEdit();
        if (textField == null) {
            createTextField();
        }
        setGraphic(textField);
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        Platform.runLater(() -> {
            textField.requestFocus();
            textField.selectAll();
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
                if (textField != null) {
                    textField.setText(getString());
                }
                setGraphic(textField);
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            } else {
                setText(getString());
                setContentDisplay(ContentDisplay.TEXT_ONLY);
            }
        }
    }

    private void createTextField() {
        textField = new TextField(getString());
        textField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
        textField.setOnKeyPressed(t -> {
            if (t.getCode() == KeyCode.ENTER) {
                if (this.type == Double.class) {
                    try {
                        Double val = Double.parseDouble(textField.getText());
                        commitEdit((T) val);
                    } catch (Exception e) {
                        cancelEdit();
                        AlertBox.display("错误", "数字输入错误，即将还原！");
                    }
                } else {
                    commitEdit((T) textField.getText());
                }
            } else if (t.getCode() == KeyCode.ESCAPE) {
                cancelEdit();
            } else if (t.getCode() == KeyCode.TAB) {
                if (this.type == Double.class) {
                    try {
                        Double val = Double.parseDouble(textField.getText());
                        commitEdit((T) val);
                    } catch (Exception e) {
                        cancelEdit();
                        AlertBox.display("错误", "数字输入错误，即将还原！");
                    }
                } else {
                    commitEdit((T) textField.getText());
                }
                TableColumn nextColumn = getNextColumn(!t.isShiftDown());
                if (nextColumn != null) {
                    getTableView().edit(getTableRow().getIndex(), nextColumn);
                }
            }
        });
//        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
//            if (!newValue && textField != null) {
//                try {
//                    commitEdit((T) textField.getText());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
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
