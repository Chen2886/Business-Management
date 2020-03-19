package Material;

import Main.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.FileInputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class MatUnitPriceTable {

    // prod table headers
    private static final String[] headers = new String[] {"原料名称", "单价", "备注"};

    // all matUnitPrice property listed
    private static final String[] property = new String[]{"name", "unitPrice", "note"};

    // all matUnitPrice property method listed
    private static final String[] propertyMethod = new String[]{"Name", "UnitPrice", "Note"};

    // mat section
    public TextField matNameTextField;
    public TextField matPriceTextField;
    public TextField matNoteTextField;
    public Button addMatButton;
    public Button cancelButton;

    @FXML TextField searchBarTextField;
    @FXML ImageView searchImageView;

    // tables
    public TableView<Material.MatUnitPrice> matTable;
    Stage stage;

    // Arraylist
    ObservableList<MatUnitPrice> allUnitPrices;
    ObservableList<MatUnitPrice> tempQuickSearchList;

    /**
     * Public function for main controller to call
     * @param stage the stage so it can be closed
     */
    public void initData(Stage stage) {
        this.stage = stage;
        try {
            allUnitPrices = DatabaseUtil.GetAllMatUnitPrice();
        } catch (SQLException e) {
            allUnitPrices = FXCollections.observableArrayList();
        }
        init();
    }

    private void init() {
        // setting up the image for search bar
        try {
            FileInputStream input = new FileInputStream("src/iconmonstr-magnifier-4-240.png");
            Image searchBarImage = new Image(input);
            searchImageView.setImage(searchBarImage);
        } catch (Exception e) {
            e.printStackTrace();
            HandleError error = new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            error.WriteToLog();
        }

        // quick search
        tempQuickSearchList = FXCollections.observableArrayList(allUnitPrices);
        searchBarTextField.textProperty().addListener((observableValue, oldValue, newValue) -> {

            // if the text field is updated to be empty
            if (newValue == null || newValue.equals("")) {
                matTable.getItems().clear();
                tempQuickSearchList = FXCollections.observableArrayList(allUnitPrices);
            } else {
                // if user deleted char, copying original array
                if (newValue.length() < oldValue.length()) {
                    matTable.getItems().clear();
                    tempQuickSearchList = FXCollections.observableArrayList(allUnitPrices);
                }

                // removing orders that doesn't contain key word
                tempQuickSearchList.removeIf(matUnitPrice -> !matUnitPrice.toString().contains(newValue));
            }
            matTable.setItems(tempQuickSearchList);

        });

        cancelButton.setOnAction(event -> stage.close());
        addMatButton.setOnAction(event -> addMatUnitPrices());

        // array of columns
        Collection<TableColumn<Material.MatUnitPrice, ?>> columnArrayList = new ArrayList<>();

        // setting up first action column
        TableColumn actionColumn = new TableColumn("动作");
        actionColumn.setSortable(false);
        columnArrayList.add(actionColumn);

        // loop to set up all regular columns
        for (int i = 0; i < headers.length; i++) {
            if (i == 1) {
                // Doubles
                TableColumn<MatUnitPrice, Double> newColumn = new TableColumn<>(headers[i]);
                newColumn.setCellValueFactory(new PropertyValueFactory<>(property[i]));
                newColumn.setStyle( "-fx-alignment: CENTER;");
                columnArrayList.add(newColumn);
            }
            else {
                // String
                TableColumn<MatUnitPrice, String> newColumn = new TableColumn<>(headers[i]);
                newColumn.setCellValueFactory(new PropertyValueFactory<>(property[i]));
                newColumn.setStyle( "-fx-alignment: CENTER;");
                columnArrayList.add(newColumn);
            }
        }

        // Setting a call back to handle the first column of action buttons
        Callback<TableColumn<MatUnitPrice, String>, TableCell<MatUnitPrice, String>> cellFactory = new Callback<>() {
            @Override
            public TableCell<MatUnitPrice, String> call(TableColumn<MatUnitPrice, String> matOrderStringTableColumn) {
                TableCell<MatUnitPrice, String> cell = new TableCell<>() {
                    // define new buttons
                    Button edit = new Button("编辑");
                    Button delete = new Button("删除");
                    HBox actionButtons = new HBox(edit, delete);

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            edit.setOnAction(event -> modifyPrice(getTableView().getItems().get(getIndex())));
                            delete.setOnAction(event -> deletePrice(getTableView().getItems().get(getIndex())));

                            edit.getStyleClass().add("actionButtons");
                            delete.getStyleClass().add("actionButtons");
                            actionButtons.setSpacing(5);
                            actionButtons.setAlignment(Pos.CENTER);
                            setGraphic(actionButtons);
                        }
                        setText(null);
                    }
                };
                return cell;
            }
        };

        actionColumn.setCellFactory(cellFactory);

        matTable.getColumns().setAll(columnArrayList);
        matTable.getItems().setAll(allUnitPrices);

    }

    private void addMatUnitPrices() {
        try {

            MatUnitPrice matUnitPrice = new MatUnitPrice();
            matUnitPrice.setName(matNameTextField.getText() == null ? "" : matNameTextField.getText());
            try {
                matUnitPrice.setUnitPrice(Double.parseDouble(matPriceTextField.getText()));
            } catch (Exception e) {
                AlertBox.display("错误", "数字输入错误");
                matUnitPrice.setUnitPrice(0.0);
            }
            matUnitPrice.setNote(matNoteTextField.getText() == null ? "" : matNoteTextField.getText());

            DatabaseUtil.AddMatUnitPrice(matUnitPrice);
            allUnitPrices = DatabaseUtil.GetAllMatUnitPrice();
            matTable.getItems().clear();
            matTable.getItems().setAll(allUnitPrices);
        } catch (SQLException e) {
            AlertBox.display("错误", "无法添加");
            e.printStackTrace();
            HandleError error = new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            error.WriteToLog();
        }
    }

    private void deletePrice(MatUnitPrice matUnitPrice) {
        if (ConfirmBox.display("确认", "确定删除？", "确定", "取消")) {
            try {
                DatabaseUtil.DeleteMatUnitPrice(matUnitPrice.getName());
                allUnitPrices = DatabaseUtil.GetAllMatUnitPrice();
                matTable.getItems().clear();
                matTable.getItems().setAll(allUnitPrices);
            } catch (SQLException e) {
                AlertBox.display("错误", "无法删除");
                e.printStackTrace();
                HandleError error = new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                        e.getMessage(), e.getStackTrace(), false);
                error.WriteToLog();
            }
        }
    }

    private void modifyPrice(MatUnitPrice matUnitPrice) {

    }


}
