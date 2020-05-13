package Product;

import Main.*;
import Material.MatUnitPrice;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ResourceBundle;

public class ProdUnitPriceTable implements Initializable {

    // prod table headers
    private static final String[] headers = new String[]{"日期", "送货单号", "客户名称", "产品名称", "单价", "备注"};

    // all matUnitPrice property listed
    private static final String[] property = new String[]{"date", "sku", "customer", "name", "unitPrice", "note"};

    // all matUnitPrice property method listed
    private static final String[] propertyMethod = new String[]{"Date", "Sku", "Customer", "Name", "UnitPrice", "Note"};

    // mat section
    public Button cancelButton;
    public HBox prodInfoHBoxTop;
    public HBox matInfoHBoxBottom;

    @FXML
    TextField searchBarTextField;
    @FXML
    ImageView searchImageView;

    // tables
    public TableView<ProdUnitPrice> prodTable;

    // Arraylist
    ObservableList<ProdUnitPrice> allUnitPrices;
    ObservableList<ProdUnitPrice> tempQuickSearchList;
    ArrayList<Node> inputArrayList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            allUnitPrices = DatabaseUtil.GetAllProdUnitPrice();
        } catch (SQLException e) {
            allUnitPrices = FXCollections.observableArrayList();
        }
        init();
    }

    public TextField getSearchBarTextField() {
        return searchBarTextField;
    }

    public TableView<ProdUnitPrice> getProdTable() {
        return prodTable;
    }

    private void init() {

        // quick search
        tempQuickSearchList = FXCollections.observableArrayList(allUnitPrices);
        searchBarTextField.textProperty().addListener((observableValue, oldValue, newValue) -> {

            // if the text field is updated to be empty
            if (newValue == null || newValue.equals("")) {
                prodTable.getItems().clear();
                tempQuickSearchList = FXCollections.observableArrayList(allUnitPrices);
            } else {
                // if user deleted char, copying original array
                if (newValue.length() < oldValue.length()) {
                    prodTable.getItems().clear();
                    tempQuickSearchList = FXCollections.observableArrayList(allUnitPrices);
                }

                // removing orders that doesn't contain key word
                tempQuickSearchList.removeIf(prodUnitPrice -> !prodUnitPrice.toString().contains(newValue));
            }
            prodTable.setItems(tempQuickSearchList);

        });

        // array of columns
        Collection<TableColumn<ProdUnitPrice, ?>> columnArrayList = new ArrayList<>();

        // loop to set up all regular columns
        for (int i = 0; i < headers.length; i++) {
            if (i == 0) {
                TableColumn<ProdUnitPrice, Date> newColumn = new TableColumn<>(headers[i]);
                newColumn.setCellValueFactory(new PropertyValueFactory<>(property[i]));
                newColumn.setStyle("-fx-alignment: CENTER;");
                newColumn.setMinWidth(110);
                columnArrayList.add(newColumn);
            } else if (i == 4) {
                // Doubles
                TableColumn<ProdUnitPrice, Double> newColumn = new TableColumn<>(headers[i]);
                newColumn.setCellValueFactory(new PropertyValueFactory<>(property[i]));
                newColumn.setStyle("-fx-alignment: CENTER;");
                columnArrayList.add(newColumn);
            } else {
                // String
                TableColumn<ProdUnitPrice, String> newColumn = new TableColumn<>(headers[i]);
                newColumn.setCellValueFactory(new PropertyValueFactory<>(property[i]));
                newColumn.setStyle("-fx-alignment: CENTER;");
                columnArrayList.add(newColumn);
            }
        }

        // set up all the text fields and labels
        inputArrayList = new ArrayList<>();
        for (int i = 0; i < headers.length; i++) {

            if (i == 0) {

                Label newLabel = new Label(headers[i] + ":");
                newLabel.setStyle("-fx-font-size: 20px;" +
                        "-fx-alignment: center-right;");
                prodInfoHBoxTop.getChildren().add(newLabel);

                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                JFXDatePicker datePicker = new JFXDatePicker();
                datePicker.setMaxWidth(Double.MAX_VALUE);

                datePicker.setConverter(new StringConverter<LocalDate>() {
                    @Override
                    public String toString(LocalDate localDate) {
                        if (localDate == null) {
                            return "0/0/0";
                        }
                        return dateTimeFormatter.format(localDate);
                    }

                    @Override
                    public LocalDate fromString(String string) {
                        if (string == null || string.isEmpty()) {
                            return null;
                        }
                        return LocalDate.from(dateTimeFormatter.parse(string));
                    }
                });

                inputArrayList.add(datePicker);
                prodInfoHBoxTop.getChildren().add(datePicker);
            } else {
                JFXTextField newTextField = new JFXTextField();
                newTextField.setMaxWidth(Double.MAX_VALUE);
                newTextField.setPromptText("输入" + headers[i]);
                inputArrayList.add(newTextField);
                prodInfoHBoxTop.getChildren().add(newTextField);
            }

        }

        Button addButton = new Button("添加");
        addButton.setStyle("-fx-font-size: 18px;");
        prodInfoHBoxTop.getChildren().add(addButton);

        addButton.setOnAction(event -> {
            addProdUnitPrices();
            FinalConstants.updateAutoCompleteProdName();
        });


        // if double clicked, enable edit
        prodTable.setRowFactory(tv -> {
            TableRow<ProdUnitPrice> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    ProdUnitPrice order = row.getItem();
                    modifyPrice(order);
                }
            });
            return row;
        });

        prodTable.setOnKeyReleased(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.BACK_SPACE || keyEvent.getCode() == KeyCode.DELETE) {
                deletePrice(prodTable.getSelectionModel().getSelectedItem());
            }
        });

        prodTable.getColumns().setAll(columnArrayList);
        prodTable.getItems().setAll(allUnitPrices);

    }

    private void addProdUnitPrices() {
        try {

            ProdUnitPrice prodUnitPrice = new ProdUnitPrice();
            prodUnitPrice.setSerialNum(SerialNum.getSerialNum(DBOrder.PRODPRICE));

            Method setter;

            for (int i = 0; i < inputArrayList.size(); i++) {
                if (i == 0) {
                    // date picker
                    try {
                        prodUnitPrice.setDate(((DatePicker) inputArrayList.get(i)).getValue() == null ? new Date(0, 0, 0) :
                                new Date(((DatePicker) inputArrayList.get(i)).getValue().getYear(),
                                        ((DatePicker) inputArrayList.get(i)).getValue().getMonthValue(),
                                        ((DatePicker) inputArrayList.get(i)).getValue().getDayOfMonth()));
                    } catch (NullPointerException ignored) {
                    }
                } else if (i == 4) {
                    // double
                    TextField currentTextField = (TextField) inputArrayList.get(i);
                    if (!currentTextField.getText().equals("")) {
                        try {
                            setter = ProdUnitPrice.class.getDeclaredMethod("set" + propertyMethod[i], double.class);
                            setter.invoke(prodUnitPrice, Double.parseDouble(currentTextField.getText()));
                        } catch (Exception ignored) {
                            AlertBox.display("错误", "单价格式输入错误, 数字默认0");
                        }
                    }
                } else {
                    // string
                    TextField currentTextField = (TextField) inputArrayList.get(i);
                    if (!currentTextField.getText().equals("")) {
                        try {
                            setter = ProdUnitPrice.class.getDeclaredMethod("set" + propertyMethod[i], String.class);
                            setter.invoke(prodUnitPrice, currentTextField.getText());
                        } catch (Exception ignored) {
                        }
                    }
                }
            }

            DatabaseUtil.AddProdUnitPrice(prodUnitPrice);
            if (ConfirmBox.display("确认", "需要更新所有没有单价的订单吗？", "是", "否"))
                updateAllUnitPrice(prodUnitPrice.getName(), prodUnitPrice.getCustomer(), prodUnitPrice.getUnitPrice());

            allUnitPrices = DatabaseUtil.GetAllProdUnitPrice();
            prodTable.getItems().clear();
            prodTable.getItems().setAll(allUnitPrices);
            for (Node node : inputArrayList) {
                try {
                    ((TextField) node).clear();
                } catch (Exception e) {
                    ((DatePicker) node).setValue(null);
                }
            }
        } catch (SQLException e) {
            AlertBox.display("错误", "无法添加");
            new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
        }
    }

    private void deletePrice(ProdUnitPrice prodUnitPrice) {
        if (ConfirmBox.display("确认", "确定删除？", "确定", "取消")) {
            try {
                DatabaseUtil.DeleteProdUnitPrice(prodUnitPrice.getName(), prodUnitPrice.getCustomer());
                allUnitPrices = DatabaseUtil.GetAllProdUnitPrice();
                prodTable.getItems().clear();
                prodTable.getItems().setAll(allUnitPrices);
            } catch (SQLException e) {
                AlertBox.display("错误", "无法删除");
                new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                        e.getMessage(), e.getStackTrace(), false);
            }
        }
    }

    private void modifyPrice(ProdUnitPrice prodUnitPrice) {
        try {

            FXMLLoader loader = new FXMLLoader();
            InputStream fileInputStream = getClass().getResourceAsStream(Main.fxmlPath + "ProdEditUnitPriceTable.fxml");
            Parent newScene = loader.load(fileInputStream);
            Stage stage = new Stage();

            ProdEditUnitPriceTable prodEditUnitPriceTable = loader.getController();
            prodEditUnitPriceTable.initData(stage, prodUnitPrice);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("配方");

            Scene scene = new Scene(newScene);
            scene.getStylesheets().add(getClass().getResource(Main.styleSheetPath).toURI().toString());
            stage.setScene(scene);
            stage.showAndWait();

            allUnitPrices = DatabaseUtil.GetAllProdUnitPrice();
            tempQuickSearchList = FXCollections.observableArrayList(allUnitPrices);
            searchBarTextField.setText("");
            prodTable.getItems().clear();
            prodTable.getItems().setAll(allUnitPrices);
        } catch (Exception e) {
            AlertBox.display("错误", "窗口错误");
            new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
        }
    }

    public static void updateAllUnitPrice(String name, String customer, double price) {
        try {
            DatabaseUtil.UpdateAllProdUnitPrice(name, customer, price);
        } catch (SQLException e) {
            AlertBox.display("错误", "无法更新");
            new HandleError(MatUnitPrice.class.getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
        }
    }
}
