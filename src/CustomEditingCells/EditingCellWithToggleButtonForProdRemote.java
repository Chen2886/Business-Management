package CustomEditingCells;

import Main.AlertBox;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import javafx.application.Platform;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;

import java.util.ArrayList;
import java.util.List;

public class EditingCellWithToggleButtonForProdRemote<S, T> extends TableCell<S, T> {
    private JFXToggleButton toggleButton;

    public EditingCellWithToggleButtonForProdRemote() {
    }

    @Override
    public void startEdit() {
        super.startEdit();
        if (toggleButton == null) {
            createTextField();
        }
        setGraphic(toggleButton);
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        Platform.runLater(() -> {
            toggleButton.requestFocus();
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
                if (toggleButton != null) {
                    toggleButton.setText(getString());
                }
                setGraphic(toggleButton);
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            } else {
                setText(getString());
                setContentDisplay(ContentDisplay.TEXT_ONLY);
            }
        }
    }

    private void createTextField() {
        toggleButton = new JFXToggleButton();
        toggleButton.selectedProperty().set(getText().equals("是"));
        toggleButton.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
        toggleButton.setOnKeyPressed(t -> {
            t.consume();
            if (t.getCode() == KeyCode.ENTER) {
                commitEdit((T) (toggleButton.selectedProperty().get() ? "是" : "否"));
            } else if (t.getCode() == KeyCode.ESCAPE) {
                cancelEdit();
            } else if (t.getCode() == KeyCode.TAB) {
                commitEdit((T) (toggleButton.selectedProperty().get() ? "是" : "否"));
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
