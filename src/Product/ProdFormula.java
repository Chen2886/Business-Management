package Product;

import Main.*;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.controlsfx.control.textfield.TextFields;

import java.awt.*;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;

public class ProdFormula {

    private static String[] propertyMethodName = new String[]{"Name", "Amount", "UnitPrice", "TotalPrice"};
    private static String[] property = new String[]{"name", "amount", "unitPrice", "totalPrice"};
    private static String[] header = new String[]{"原料名称", "数量", "单价", "金额"};
    private static String[] formulaInfoHeader = new String[]{"配方名称", "成本价"};
    private static String[] formulaInfoProperty = new String[]{"Name", "BasePrice"};

    public Button defaultButton;
    @FXML
    HBox formulaTopHBox;
    @FXML
    Button cancelButton;
    @FXML
    Button overrideButton;
    @FXML
    Button saveNewButton;
    @FXML
    TableView<Formula> formulaTable;
    @FXML
    HBox formulaInfoInputBottomHBox;

    Button addItemButton;

    ArrayList<TextField> inputArray;
    ArrayList<TextField> formulaInfoInputArray;
    ArrayList<TableColumn<Formula, ?>> formulaColumnList;

    Stage currentStage;
    ProductOrder selectedOrder;
    Formula formula;
    boolean isNewFormula;
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    /**
     * Called by main controller to give the selected order
     *
     * @param selectedOrder the order that was selected, to fill the information
     * @param currentStage  the stage, so it can be closed later
     */
    public void initData(ProductOrder selectedOrder, Stage currentStage) {
        this.selectedOrder = selectedOrder;
        this.currentStage = currentStage;
        formulaColumnList = new ArrayList<>();
        // getting current formula
        try {
            // if this is a product that doesn't have existing formula
            if (selectedOrder.getFormulaIndex() != -1) {
                formula = DatabaseUtil.GetFormulaByIndex(selectedOrder.getFormulaIndex());
                if (selectedOrder.getBasePrice() == 0.0) {
                    selectedOrder.setBasePrice(calcBasePrice());
                    DatabaseUtil.UpdateProdOrder(selectedOrder);
                }
            } else {
                if (DatabaseUtil.CheckIfNameExistsInNewestFormula(selectedOrder.getName())) {
                    int newIndex = DatabaseUtil.GetNewestFormulaIndex(selectedOrder.getName());
                    formula = DatabaseUtil.GetFormulaByIndex(newIndex);
                    selectedOrder.setFormulaIndex(newIndex);
                    selectedOrder.setBasePrice(calcBasePrice());
                    DatabaseUtil.UpdateProdOrder(selectedOrder);
                } else formula = null;
            }
        } catch (Exception e) {
            new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            selectedOrder.setFormulaIndex(-1);
            formula = null;
        }
        init();
    }

    /**
     * Initialize everything on the screen
     */
    private void init() {
        initFormulaTable();
        initInputBottomHBox();
        initTopHBox();
        calcBasePrice();

        currentStage.setMinHeight(screenSize.height * 0.9);
        currentStage.setMinWidth(screenSize.width * 0.6);
        currentStage.setOnCloseRequest(event -> {
            if (ConfirmBox.display("确认", "确定关闭窗口？进度即将丢失", "是", "否"))
                currentStage.close();
            event.consume();
        });

        cancelButton.setOnAction(event -> {
            if (ConfirmBox.display("确认", "确定关闭窗口？进度即将丢失", "是", "否"))
                currentStage.close();
        });
        saveNewButton.setOnAction(event -> saveNewFormula());
        overrideButton.setOnAction(event -> overrideCurrentFormula());
        defaultButton.setOnAction(event -> saveDefaultFormula());
        if (formula == null) {
            formula = new Formula(selectedOrder.getName());
            isNewFormula = true;
            defaultButton.setVisible(false);
        } else {
            isNewFormula = false;
        }
    }

    /**
     * Init top formula into hbox
     */
    private void initTopHBox() {
        // populating the info hbox
        formulaInfoInputArray = new ArrayList<>();
        Method getter;

        // adding the text fields and labels
        for (int i = 0; i < formulaInfoHeader.length; i++) {

            // new label
            Label newLabel = new Label(formulaInfoHeader[i]);
            newLabel.setStyle("-fx-font-size: 20px; -fx-alignment: center-right;");

            // new text field
            JFXTextField newTextField = new JFXTextField();
            newTextField.setDisable(true);
            try {
                getter = ProductOrder.class.getDeclaredMethod("get" + formulaInfoProperty[i]);
                newTextField.setText(String.valueOf(getter.invoke(selectedOrder)));
            } catch (Exception e) {
                AlertBox.display("错误", "读取信息错误！");
                new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                        e.getMessage(), e.getStackTrace(), false);
            }

            // add to Hbox
            formulaTopHBox.getChildren().add(newLabel);
            formulaTopHBox.getChildren().add(newTextField);

            // add text fields to array
            formulaInfoInputArray.add(newTextField);
        }
    }

    /**
     * Initialize info hbox
     */
    private void initInputBottomHBox() {
        // populating the info hbox
        inputArray = new ArrayList<>();
        for (String s : header) {

            // new text field
            JFXTextField newTextField = new JFXTextField();
            if (s.equals("原料名称")) TextFields.bindAutoCompletion(newTextField, FinalConstants.autoCompleteMatName);
            newTextField.setPromptText("输入" + s);

            // add to the HBox
            formulaInfoInputBottomHBox.getChildren().add(newTextField);

            // add to array
            inputArray.add(newTextField);
        }

        // info hbox add button
        addItemButton = new JFXButton("添加");
        addItemButton.getStyleClass().add("actionButtons");
        formulaInfoInputBottomHBox.getChildren().add(addItemButton);

        addItemButton.setOnAction(event -> {
            addItem();
            defaultButton.setVisible(false);
        });
        addItemButton.setOnKeyReleased(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                addItem();
                defaultButton.setVisible(false);
            }
        });


        // auto price
        TextField totalPrice = inputArray.get(3);
        EventHandler autoTotalPriceEventHandler = event -> {
            try {
                totalPrice.setText(String.valueOf(Double.parseDouble(inputArray.get(1).getText()) *
                        Double.parseDouble(inputArray.get(2).getText())));
            } catch (Exception ignored) {
            }
        };
        for (int i = 1; i < inputArray.size(); i++) {
            TextField textField = inputArray.get(i);
            textField.setOnKeyTyped(autoTotalPriceEventHandler);
            textField.setOnMouseClicked(autoTotalPriceEventHandler);
        }

        TextField nameTextField = inputArray.get(0);
        nameTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                try {
                    inputArray.get(2).setText(String.valueOf(DatabaseUtil.GetMatUnitPrice(nameTextField.getText())));
                } catch (SQLException e) {
                    inputArray.get(2).setText("0");
                    new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                            e.getMessage(), e.getStackTrace(), false);
                }
            }
        });
    }

    /**
     * Initialize the formula table
     */
    private void initFormulaTable() {

        // init table

        // loop to set up all regular columns
        for (int i = 0; i < property.length; i++) {
            if (i == 1 || i == 2 || i == 3) {
                // Doubles
                TableColumn<Formula, Double> newColumn = new TableColumn<>(header[i]);
                newColumn.setCellValueFactory(new PropertyValueFactory<>(property[i]));
                newColumn.setStyle("-fx-alignment: CENTER;");
                newColumn.setMinWidth(100);
                formulaColumnList.add(newColumn);
            } else {
                // String
                TableColumn<Formula, String> newColumn = new TableColumn<>(header[i]);
                newColumn.setCellValueFactory(new PropertyValueFactory<>(property[i]));
                newColumn.setStyle("-fx-alignment: CENTER;");
                newColumn.setMinWidth(130);
                formulaColumnList.add(newColumn);
            }
        }

        formulaTable.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.BACK_SPACE || keyEvent.getCode() == KeyCode.DELETE) {
                removeFormulaFromList(formulaTable.getSelectionModel().getSelectedItem());
            }
        });
        formulaTable.setRowFactory(param -> {
            TableRow<Formula> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    viewFormula(row.getItem());
                }
            });
            return row;
        });
        
        formulaTable.getColumns().setAll(formulaColumnList);
        if (formula != null) formulaTable.getItems().setAll(formula.getFormulaList());
    }

    /**
     * Pop up window to edit/add formula within formula
     */
    private void viewFormula(Formula formula) {
        try {

            FXMLLoader loader = new FXMLLoader();
            InputStream fileInputStream = getClass().getResourceAsStream(Main.fxmlPath + "ProdFormulaEdit.fxml");
            Parent newScene = loader.load(fileInputStream);
            Stage stage = new Stage();

            ProdFormulaEdit prodFormulaEdit = loader.getController();
            prodFormulaEdit.initData(null, formula, stage, formulaTable);

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("编辑配方");

            Scene scene = new Scene(newScene);
            scene.getStylesheets().add(getClass().getResource(Main.styleSheetPath).toURI().toString());
            stage.setScene(scene);
            stage.showAndWait();
            formulaTable.refresh();
            calcBasePrice();
        } catch (Exception e) {
            AlertBox.display("错误", "窗口错误");
            new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
        }
    }

    /**
     * Add Simple item to the product
     */
    private void addItem() {
        Formula formula = new Formula();
        Method setter;
        boolean empty = true;

        for (int i = 0; i < header.length; i++) {
            TextField currentTextField = inputArray.get(i);
            if (i == 0) {
                if (currentTextField.getText() != null && !currentTextField.getText().equals("")) {
                    empty = false;
                    try {
                        setter = Formula.class.getDeclaredMethod("set" + propertyMethodName[i], String.class);
                        setter.invoke(formula, currentTextField.getText());
                    } catch (Exception e) {
                        new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                                e.getMessage(), e.getStackTrace(), false);
                    }
                }
            } else if (i == 1 || i == 2) {
                if (currentTextField.getText() != null && !currentTextField.getText().equals("")) {
                    empty = false;
                    try {
                        setter = Formula.class.getDeclaredMethod("set" + propertyMethodName[i], double.class);
                        setter.invoke(formula, Double.parseDouble(currentTextField.getText()));
                    } catch (Exception e) {
                        new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                                e.getMessage(), e.getStackTrace(), false);
                    }
                }
            }
        }

        formula.setTotalPrice();
        if (!empty) addFormula(formula);
        for (TextField textField : inputArray) textField.clear();

        calcBasePrice();
    }

    /**
     * Add formula to the tableView
     *
     * @param newFormula the formula to be added
     */
    public void addFormula(Formula newFormula) {
        formula.addFormula(newFormula);
        formulaTable.getItems().clear();
        formulaTable.getItems().setAll(formula.getFormulaList());
        calcBasePrice();
    }

    /**
     * public function for other controller to call, to add to the list, and refresh table
     *
     * @param formula the formula to be added to list
     */
    public void addFormulaToList(Formula formula) {
        formula.addFormula(formula);
        formulaTable.getItems().clear();
        formulaTable.getItems().setAll(formula.getFormulaList());
    }

    /**
     * public function for other controller to call, to remove item from the list, and refresh table
     *
     * @param inputFormula the formula to be removed to list
     */
    public void removeFormulaFromList(Formula inputFormula) {
        formula.removeFormula(inputFormula);
        formulaTable.getItems().clear();
        formulaTable.getItems().setAll(formula.getFormulaList());
        calcBasePrice();
    }

    /**
     * Save the formula to the selected order, and push formula to database
     */
    private void saveNewFormula() {
        if (!isNewFormula && !ConfirmBox.display("确认", "确定另存为？之前配方不会被更该。", "是", "否"))
            return;
        try {

            ArrayList<Formula> newFormulaList = new ArrayList<>(formulaTable.getItems());
            formula.setFormulaList(newFormulaList);
            selectedOrder.setBasePrice(calcBasePrice());

            int index = DatabaseUtil.AddFormula(formula);
            selectedOrder.setFormulaIndex(index);
            DatabaseUtil.UpdateProdOrder(selectedOrder);

            DatabaseUtil.UpdateProdOrder(selectedOrder);
            defaultButton.setVisible(true);
            currentStage.close();
        } catch (SQLException e) {
            new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
        }
    }

    /**
     * Save the formula to the selected order, and push formula to database
     */
    private void overrideCurrentFormula() {
        if (isNewFormula) {
            saveNewFormula();
            return;
        }
        if (!ConfirmBox.display("确认", "确定更新此配方？所有使用此配方的产品即将被更新。", "是", "否"))
            return;
        if (ConfirmBox.display("确认", "是否设为以后次产品的默认订单？", "是", "否"))
            saveDefaultFormula();
        try {
            double newBasePrice = calcBasePrice();
            ArrayList<Formula> newFormulaList = new ArrayList<>(formulaTable.getItems());
            formula.setFormulaList(newFormulaList);
            selectedOrder.setBasePrice(newBasePrice);

            DatabaseUtil.UpdateFormula(formula, selectedOrder.getFormulaIndex());
            DatabaseUtil.UpdateProdOrder(selectedOrder);
            DatabaseUtil.UpdateAllProdOrderNewBasePrice(selectedOrder.getFormulaIndex(), newBasePrice);
            defaultButton.setVisible(true);
            currentStage.close();
        } catch (SQLException e) {
            new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
        }
    }

    /**
     * Set current formula to be default for this product
     */
    private void saveDefaultFormula() {
        try {
            if (DatabaseUtil.CheckIfNameExistsInNewestFormula(selectedOrder.getName()))
                DatabaseUtil.UpdateNewestFormula(true, selectedOrder.getName(), selectedOrder.getFormulaIndex());
            else DatabaseUtil.UpdateNewestFormula(false, selectedOrder.getName(), selectedOrder.getFormulaIndex());
            AlertBox.display("成功", "设置成功");
            currentStage.close();
        } catch (SQLException e) {
            AlertBox.display("错误", "设置默认错误");
            new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
        }
    }

    /**
     * Calculate the base price
     *
     * @return the base price
     */
    private double calcBasePrice() {
        double totalSum = 0.0;
        double totalAmount = 0.0;
        if (formula == null) return 0.0;
        for (Formula formula : formula.getFormulaList()) {
            totalSum += formula.getTotalPrice();
            totalAmount += formula.getAmount();
        }
        double returnVal = Math.round(totalSum / totalAmount * 1.05 * 100.0) / 100.0;
        try {
            if (returnVal != 0) formulaInfoInputArray.get(1).setText(String.valueOf(returnVal));
        } catch (Exception ignored) {
        }
        return returnVal;
    }

}
