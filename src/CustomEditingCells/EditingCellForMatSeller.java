package CustomEditingCells;

import Main.AlertBox;
import Main.DatabaseUtil;
import Main.HandleError;
import Material.MatSeller;
import com.jfoenix.controls.JFXComboBox;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.input.KeyCode;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class EditingCellForMatSeller<S, T> extends TableCell<S, T> {
    private JFXComboBox<String> ComboBox;

    public EditingCellForMatSeller() {
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
        ObservableList<MatSeller> sellerList = FXCollections.observableArrayList();
        try {
            sellerList.setAll(DatabaseUtil.GetAllMatSellers());
        } catch (SQLException e) {
            AlertBox.display("错误", "读取供应商错误！");
            new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
        }

        ObservableList<String> sellerNameList = FXCollections.observableArrayList();
        for (MatSeller seller : sellerList) sellerNameList.add(seller.getCompanyName());

        ComboBox = new JFXComboBox<>(sellerNameList);
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
