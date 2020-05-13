package Material;

import Main.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ResourceBundle;

public class MatUnitPriceTable implements Initializable {

    // prod table headers
    private static final String[] headers = new String[]{"原料名称", "单价", "备注"};

    // all matUnitPrice property listed
    private static final String[] property = new String[]{"name", "unitPrice", "note"};

    // all matUnitPrice property method listed
    private static final String[] propertyMethod = new String[]{"Name", "UnitPrice", "Note"};

    // mat section
    public TextField matNameTextField;
    public TextField matPriceTextField;
    public TextField matNoteTextField;
    public Button addMatButton;
    public TextField searchBarTextField;

    // tables
    public TableView<Material.MatUnitPrice> matTable;

    // Arraylist
    ObservableList<MatUnitPrice> allUnitPrices;
    ObservableList<MatUnitPrice> tempQuickSearchList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            allUnitPrices = DatabaseUtil.GetAllMatUnitPrice();
        } catch (SQLException e) {
            allUnitPrices = FXCollections.observableArrayList();
        }
        init();
    }

    public TextField getSearchBarTextField() {
        return searchBarTextField;
    }

    public TableView<MatUnitPrice> getMatTable() {
        return matTable;
    }

    private void init() {

        matNameTextField.setPromptText("输入" + headers[0]);
        matPriceTextField.setPromptText("输入" + headers[1]);
        matNoteTextField.setPromptText("输入" + headers[2]);

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

        addMatButton.setOnAction(event -> {
            addMatUnitPrices();
            FinalConstants.updateAutoCompleteMatName();
        });

        // array of columns
        Collection<TableColumn<Material.MatUnitPrice, ?>> columnArrayList = new ArrayList<>();

        // loop to set up all regular columns
        for (int i = 0; i < headers.length; i++) {
            if (i == 1) {
                // Doubles
                TableColumn<MatUnitPrice, Double> newColumn = new TableColumn<>(headers[i]);
                newColumn.setCellValueFactory(new PropertyValueFactory<>(property[i]));
                newColumn.setStyle("-fx-alignment: CENTER;");
                columnArrayList.add(newColumn);
            } else {
                // String
                TableColumn<MatUnitPrice, String> newColumn = new TableColumn<>(headers[i]);
                newColumn.setCellValueFactory(new PropertyValueFactory<>(property[i]));
                newColumn.setStyle("-fx-alignment: CENTER;");
                columnArrayList.add(newColumn);
            }
        }

        // Setting a call back to handle the first column of action buttons
        Callback<TableColumn<MatUnitPrice, String>, TableCell<MatUnitPrice, String>> cellFactory = new Callback<>() {
            @Override
            public TableCell<MatUnitPrice, String> call(TableColumn<MatUnitPrice, String> matOrderStringTableColumn) {
                return new TableCell<>() {
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
            }
        };

        // if double clicked, enable edit
        matTable.setRowFactory(tv -> {
            TableRow<MatUnitPrice> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    MatUnitPrice order = row.getItem();
                    modifyPrice(order);
                }
            });
            return row;
        });

        matTable.setOnKeyReleased(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.BACK_SPACE || keyEvent.getCode() == KeyCode.DELETE) {
                deletePrice(matTable.getSelectionModel().getSelectedItem());
            }
        });

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
                AlertBox.display("错误", "单价格式输入错误, 数字默认0");
                matUnitPrice.setUnitPrice(0.0);
            }
            matUnitPrice.setNote(matNoteTextField.getText() == null ? "" : matNoteTextField.getText());

            DatabaseUtil.AddMatUnitPrice(matUnitPrice);
            allUnitPrices = DatabaseUtil.GetAllMatUnitPrice();
            matTable.getItems().clear();
            matTable.getItems().setAll(allUnitPrices);
            matTable.refresh();
            if (ConfirmBox.display("确认", "是否更新所有此原料没有单价的订单？", "是", "否"))
                updateAllUnitPrice(matUnitPrice.getName(), matUnitPrice.getUnitPrice());
            AlertBox.display("成功", "添加成功");
            matNameTextField.clear();
            matPriceTextField.clear();
            matNoteTextField.clear();
        } catch (SQLException e) {
            AlertBox.display("错误", "无法添加");
            new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
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
                new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                        e.getMessage(), e.getStackTrace(), false);
            }
        }
    }

    private void modifyPrice(MatUnitPrice matUnitPrice) {
        try {
            FXMLLoader loader = new FXMLLoader();
            FileInputStream fileInputStream = new FileInputStream(new File(Main.fxmlPath + "MatEditUnitPrice.fxml"));
            Parent newScene = loader.load(fileInputStream);
            Stage stage = new Stage();

            MatEditUnitPrice matEditUnitPrice = loader.getController();
            matEditUnitPrice.initData(stage, matUnitPrice);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("配方");

            Scene scene = new Scene(newScene);
            scene.getStylesheets().add("file:///" + Main.styleSheetPath);
            stage.setScene(scene);
            stage.showAndWait();

            allUnitPrices = DatabaseUtil.GetAllMatUnitPrice();
            tempQuickSearchList = FXCollections.observableArrayList(allUnitPrices);
            searchBarTextField.setText("");
            matTable.getItems().clear();
            matTable.getItems().setAll(allUnitPrices);
        } catch (Exception e) {
            AlertBox.display("错误", "窗口错误");
            new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
        }
    }

    public static void updateAllUnitPrice(String name, double price) {
        try {
            DatabaseUtil.UpdateAllMatUnitPrice(name, price);
        } catch (SQLException e) {
            AlertBox.display("错误", "无法更新");
            new HandleError(MatUnitPrice.class.getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
        }
    }
}
