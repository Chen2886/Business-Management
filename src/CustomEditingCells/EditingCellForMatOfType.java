package CustomEditingCells;

import Main.FinalConstants;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;

import java.util.ArrayList;
import java.util.List;

public abstract class EditingCellForMatOfType<S, T> extends TableCell<S, T> {
    private javafx.scene.control.ComboBox<String> ComboBox;

    public EditingCellForMatOfType() {
    }

    @Override
    public void startEdit() {
        super.startEdit();
        if (ComboBox == null) {
            createComboBox();
        }
        setGraphic(ComboBox);
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        Platform.runLater(() -> {
            ComboBox.requestFocus();
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
                if (ComboBox != null) {
                    ComboBox.getSelectionModel().select(getText());
                }
                setGraphic(ComboBox);
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            } else {
                setText(getString());
                setContentDisplay(ContentDisplay.TEXT_ONLY);
            }
        }
    }

    private void createComboBox() {
        ObservableList<String> matOfType = FXCollections.observableArrayList(FinalConstants.matOfType);
        ComboBox = new ComboBox<>(matOfType);
        ComboBox.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
        ComboBox.setOnKeyPressed(t -> {
            if (t.getCode() == KeyCode.ENTER) {
                commitEdit((T) ComboBox.getSelectionModel().getSelectedItem());
                t.consume();
            } else if (t.getCode() == KeyCode.ESCAPE) {
                cancelEdit();
            } else if (t.getCode() == KeyCode.TAB) {
                commitEdit((T) ComboBox.getSelectionModel().getSelectedItem());
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
