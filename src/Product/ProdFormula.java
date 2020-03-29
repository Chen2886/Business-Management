package Product;

import Main.AlertBox;
import Main.ConfirmBox;
import Main.DatabaseUtil;
import Main.HandleError;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;

public class ProdFormula {

    private static String[] propertyMethodName = new String[]{"Name", "Amount", "UnitPrice", "TotalPrice"};
    private static String[] property = new String[]{"name", "amount", "unitPrice", "totalPrice"};
    private static String[] header = new String[]{"原料名称", "数量", "单价", "金额"};
    private static String[] formulaInfoHeader = new String[]{"配方名称", "成本价"};
    private static String[] formulaInfoProperty = new String[]{"Name", "BasePrice"};

    @FXML HBox formulaInfoHBox;
    @FXML Button cancelButton;
    @FXML Button saveButton;
    @FXML Button saveNewButton;
    @FXML TableView<Formula> formulaTable;
    @FXML TableView<FormulaItem> formulaItemTable;
    @FXML HBox infoHBox;

    Button addItemButton;

    ArrayList<TextField> inputArray;
    ArrayList<TextField> formulaInfoInputArray;
    ArrayList<TableColumn<FormulaItem, ?>> itemColumnList;
    ArrayList<TableColumn<Formula, ?>> formulaColumnList;

    Stage currentStage;
    ProductOrder selectedOrder;
    Formula formula;
    boolean isNewFormula;

    /**
     * Called by main controller to give the selected order
     *
     * @param selectedOrder the order that was selected, to fill the information
     * @param currentStage  the stage, so it can be closed later
     */
    public void initData(ProductOrder selectedOrder, Stage currentStage) {
        this.selectedOrder = selectedOrder;
        this.currentStage = currentStage;

        // getting current formula
        try {

            // if this is a product that doesn't have existing formula
            if (selectedOrder.getFormulaIndex() != -1)
                formula = DatabaseUtil.GetFormulaByIndex(selectedOrder.getFormulaIndex());
            else {
                if (DatabaseUtil.CheckIfNameExistsInNewestFormula(selectedOrder.getName())) {
                    int newIndex = DatabaseUtil.GetNewestFormulaIndex(selectedOrder.getName());
                    formula = DatabaseUtil.GetFormulaByIndex(newIndex);
                    selectedOrder.setFormulaIndex(newIndex);
                    DatabaseUtil.UpdateProdOrder(selectedOrder);
                } else formula = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            HandleError error = new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            error.WriteToLog();
            selectedOrder.setFormulaIndex(-1);
            formula = null;
        }

        itemColumnList = new ArrayList<>();
        formulaColumnList = new ArrayList<>();
        init();
    }

    /**
     * Initialize everything on the screen
     */
    private void init() {

        initItemTable();
        initFormulaTable();
        initInfoHBox();
        initFormulaInfoHBox();
        calcUnitPrice();

        cancelButton.setOnAction(event -> {
            if (ConfirmBox.display("确认", "确定关闭窗口？进度即将丢失", "是", "否"))
                currentStage.close();
        });
        saveNewButton.setOnAction(event -> saveNewFormula());
        saveButton.setOnAction(event -> saveFormula());

        if (formula == null) {
            formula = new Formula(selectedOrder.getName());
            isNewFormula = true;
        } else {
            isNewFormula = false;
        }

    }

    /**
     * Init top formula into hbox
     */
    private void initFormulaInfoHBox() {
        // populating the info hbox
        formulaInfoInputArray = new ArrayList<>();
        Method getter;
        for (int i = 0; i < formulaInfoHeader.length; i++) {
            Label newLabel = new Label(formulaInfoHeader[i]);
            newLabel.setStyle("-fx-font-size: 20px;" +
                    "-fx-alignment: center-right;");
            formulaInfoHBox.getChildren().add(newLabel);

            TextField newTextField = new TextField();
            newTextField.setPromptText("输入" + formulaInfoHeader[i]);
            try {
                getter = ProductOrder.class.getDeclaredMethod("get" + formulaInfoProperty[i]);
                newTextField.setText(String.valueOf(getter.invoke(selectedOrder)));
            } catch (Exception e) {e.printStackTrace();}
            formulaInfoHBox.getChildren().add(newTextField);
            formulaInfoInputArray.add(newTextField);
        }

        // auto price
        TextField totalPrice = formulaInfoInputArray.get(1);
        for (int i = 1; i < formulaInfoInputArray.size(); i++) {
            TextField textField = formulaInfoInputArray.get(i);
            textField.setOnKeyTyped(event -> {
                try {
                    totalPrice.setText(String.valueOf(Double.parseDouble(formulaInfoInputArray.get(1).getText()) *
                            Double.parseDouble(formulaInfoInputArray.get(2).getText())));
                } catch (Exception ignored) {}
            });
            textField.setOnMouseClicked(event -> {
                try {
                    totalPrice.setText(String.valueOf(Double.parseDouble(formulaInfoInputArray.get(1).getText()) *
                            Double.parseDouble(formulaInfoInputArray.get(2).getText())));
                } catch (Exception ignored) {}
            });
        }
    }

    /**
     * Initialize info hbox
     */
    private void initInfoHBox() {
        // populating the info hbox
        inputArray = new ArrayList<>();
        for (String s : header) {
            Label newLabel = new Label(s);
            newLabel.setStyle("-fx-font-size: 20px;" +
                    "-fx-alignment: center-right;");
            infoHBox.getChildren().add(newLabel);

            TextField newTextField = new TextField();
            newTextField.setPromptText("输入" + s);
            infoHBox.getChildren().add(newTextField);
            inputArray.add(newTextField);
        }

        // info hbox add button
        addItemButton = new Button("添加");
        addItemButton.setStyle("-fx-font-size: 18px;");
        infoHBox.getChildren().add(addItemButton);
        addItemButton.setOnAction(event -> addItem());

        // auto price
        TextField totalPrice = inputArray.get(3);
        for (int i = 1; i < inputArray.size(); i++) {
            TextField textField = inputArray.get(i);
            textField.setOnKeyTyped(event -> {
                try {
                    totalPrice.setText(String.valueOf(Double.parseDouble(inputArray.get(1).getText()) *
                            Double.parseDouble(inputArray.get(2).getText())));
                } catch (Exception ignored) {}
            });
            textField.setOnMouseClicked(event -> {
                try {
                    totalPrice.setText(String.valueOf(Double.parseDouble(inputArray.get(1).getText()) *
                            Double.parseDouble(inputArray.get(2).getText())));
                } catch (Exception ignored) {}
            });
        }

        // auto unit price
        TextField unitPrice = inputArray.get(2);
        unitPrice.setOnKeyTyped(keyEvent -> {
            try {
                unitPrice.setText(String.valueOf(DatabaseUtil.GetMatUnitPrice(inputArray.get(0).getText())));
            } catch (Exception e) {
                e.printStackTrace();
                HandleError error = new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                        e.getMessage(), e.getStackTrace(), false);
                error.WriteToLog();
            }
        });
        unitPrice.setOnMouseClicked(keyEvent -> {
            try {
                unitPrice.setText(String.valueOf(DatabaseUtil.GetMatUnitPrice(inputArray.get(0).getText())));
            } catch (Exception e) {
                e.printStackTrace();
                HandleError error = new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                        e.getMessage(), e.getStackTrace(), false);
                error.WriteToLog();
            }
        });
        
    }

    /**
     * Initialize the item table
     */
    private void initItemTable() {

        // init table
        // setting up first action column
        TableColumn<FormulaItem, String> actionColumn = new TableColumn<>("动作");
        actionColumn.setSortable(false);
        actionColumn.setMinWidth(180);
        itemColumnList.add(actionColumn);

        // loop to set up all regular columns
        for (int i = 0; i < property.length; i++) {
            if (i == 1 || i == 2 || i == 3) {
                // Doubles
                TableColumn<FormulaItem, Double> newColumn = new TableColumn<>(header[i]);
                newColumn.setCellValueFactory(new PropertyValueFactory<>(property[i]));
                newColumn.setStyle("-fx-alignment: CENTER;");
                newColumn.setMinWidth(100);
                itemColumnList.add(newColumn);
            } else {
                // String
                TableColumn<FormulaItem, String> newColumn = new TableColumn<>(header[i]);
                newColumn.setCellValueFactory(new PropertyValueFactory<>(property[i]));
                newColumn.setStyle("-fx-alignment: CENTER;");
                newColumn.setMinWidth(140);
                itemColumnList.add(newColumn);
            }
        }

        // Setting a call back to handle the first column of action buttons
        Callback<TableColumn<FormulaItem, String>, TableCell<FormulaItem, String>> cellFactory = new Callback<>() {
            @Override
            public TableCell<FormulaItem, String> call(TableColumn<FormulaItem, String> formulaItem) {
                TableCell<FormulaItem, String> cell = new TableCell<>() {
                    // define new buttons
                    Button edit = new Button("添加配方");
                    Button delete = new Button("删除");
                    HBox actionButtons = new HBox(edit, delete);

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            delete.setOnAction(event -> {
                                if (ConfirmBox.display("确认", "确定删除原料？", "是", "否"))
                                    removeItemFromList(getTableView().getItems().get(getIndex()));
                            });
                            edit.setOnAction(event -> {
                                convertItemToFormula(getTableView().getItems().get(getIndex()));
                            });

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

        formulaItemTable.getColumns().setAll(itemColumnList);
        if (formula != null) formulaItemTable.getItems().setAll(formula.getSimpleItemList());
    }

    /**
     * Convert an item to Formula
     */
    private void convertItemToFormula(FormulaItem item) {
        Formula newFormula = FormulaItem.convertToFormula(item);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../../fxml/ProdFormulaEdit.fxml"));
            Parent newScene = loader.load();
            Stage stage = new Stage();

            ProdFormulaEdit prodFormulaEdit = loader.getController();
            prodFormulaEdit.initData(item, newFormula, stage, formulaItemTable, formulaTable);

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("编辑配方");
            stage.setScene(new Scene(newScene));
            stage.showAndWait();
            calcUnitPrice();
        } catch (Exception e) {
            AlertBox.display("错误", "窗口错误");
            e.printStackTrace();
            HandleError error = new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            error.WriteToLog();
        }
    }

    /**
     * Convert an item to Formula
     */
    private void viewFormula(Formula formula) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../../fxml/ProdFormulaEdit.fxml"));
            Parent newScene = loader.load();
            Stage stage = new Stage();

            ProdFormulaEdit prodFormulaEdit = loader.getController();
            prodFormulaEdit.initData(null, formula, stage, formulaItemTable, formulaTable);

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("编辑配方");
            stage.setScene(new Scene(newScene));
            stage.show();
        } catch (Exception e) {
            AlertBox.display("错误", "窗口错误");
            e.printStackTrace();
            HandleError error = new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            error.WriteToLog();
        }
    }

    /**
     * Initialize the formula table
     */
    private void initFormulaTable() {

        // init table
        // setting up first action column
        TableColumn<Formula, String> actionColumn = new TableColumn<>("动作");
        actionColumn.setSortable(false);
        actionColumn.setMinWidth(180);
        formulaColumnList.add(actionColumn);

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

        // Setting a call back to handle the first column of action buttons
        Callback<TableColumn<Formula, String>, TableCell<Formula, String>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Formula, String> call(TableColumn<Formula, String> formulaItem) {
                TableCell<Formula, String> cell = new TableCell<>() {
                    // define new buttons
                    Button edit = new Button("查看/编辑");
                    Button delete = new Button("删除");
                    HBox actionButtons = new HBox(edit, delete);

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            delete.setOnAction(event -> {
                                if (ConfirmBox.display("确认", "确定删除原料？", "是", "否"))
                                    removeFormulaFromList(getTableView().getItems().get(getIndex()));
                            });
                            edit.setOnAction(event -> {
                                viewFormula(getTableView().getItems().get(getIndex()));
                            });

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

        formulaTable.getColumns().setAll(formulaColumnList);
        if (formula != null) formulaTable.getItems().setAll(formula.getFormulaList());
    }

    /**
     * public function for other controller to call, to add to the list, and refresh table
     * @param item the item to be added to list
     */
    public void addItemToList(FormulaItem item) {
        formula.addItem(item);
        formulaItemTable.getItems().clear();
        formulaItemTable.getItems().setAll(formula.getSimpleItemList());
        calcUnitPrice();
    }

    /**
     * public function for other controller to call, to add to the list, and refresh table
     * @param formula the formula to be added to list
     */
    public void addFormulaToList(Formula formula) {
        formula.addFormula(formula);
        formulaTable.getItems().clear();
        formulaTable.getItems().setAll(formula.getFormulaList());
    }

    /**
     * public function for other controller to call, to remove item from the list, and refresh table
     * @param item the item to be removed to list
     */
    public void removeItemFromList(FormulaItem item) {
        formula.removeItem(item);
        formulaItemTable.getItems().clear();
        formulaItemTable.getItems().setAll(formula.getSimpleItemList());
        calcUnitPrice();
    }

    /**
     * public function for other controller to call, to remove item from the list, and refresh table
     * @param inputFormula the formula to be removed to list
     */
    public void removeFormulaFromList(Formula inputFormula) {
        formula.removeFormula(inputFormula);
        formulaTable.getItems().clear();
        formulaTable.getItems().setAll(inputFormula.getFormulaList());
        calcUnitPrice();
    }

    /**
     * Add Simple item to the product
     */
    private void addItem() {
        FormulaItem formulaItem = new FormulaItem();
        Method setter;
        boolean empty = true;

        for(int i = 0; i < header.length; i++) {
            TextField currentTextField = inputArray.get(i);
            if (i == 0) {
                if (currentTextField.getText() != null && !currentTextField.getText().equals("")) {
                    empty = false;
                    try {
                        setter = FormulaItem.class.getDeclaredMethod("set" + propertyMethodName[i], String.class);
                        setter.invoke(formulaItem, currentTextField.getText());
                    } catch (Exception e) {
                        e.printStackTrace();
                        HandleError error = new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                                e.getMessage(), e.getStackTrace(), false);
                        error.WriteToLog();
                    }
                }
            } else if (i == 1 || i == 2) {
                if (currentTextField.getText() != null && !currentTextField.getText().equals("")) {
                    empty = false;
                    try {
                        setter = FormulaItem.class.getDeclaredMethod("set" + propertyMethodName[i], double.class);
                        setter.invoke(formulaItem, Double.parseDouble(currentTextField.getText()));
                    } catch (Exception e) {
                        e.printStackTrace();
                        HandleError error = new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                                e.getMessage(), e.getStackTrace(), false);
                        error.WriteToLog();
                    }
                }
            }
        }

        formulaItem.setTotalPrice();
        if (!empty) addItemToList(formulaItem);
        for(TextField textField : inputArray) textField.clear();

        calcUnitPrice();
    }

    /**
     * Save the formula to the selected order, and push formula to database
     */
    private void saveNewFormula() {
        if (!isNewFormula && !ConfirmBox.display("确认", "确定另存为此配方，以后此产品默认此新配方？之前产品的配方不会被更该", "是", "否"))
            return;
        try {

            ArrayList<FormulaItem> newItemList = new ArrayList<>(formulaItemTable.getItems());
            ArrayList<Formula> newFormulaList = new ArrayList<>(formulaTable.getItems());
            formula.setSimpleItemList(newItemList);
            formula.setFormulaList(newFormulaList);
            selectedOrder.setBasePrice(calcUnitPrice());

            int index = DatabaseUtil.AddFormula(formula);
            selectedOrder.setFormulaIndex(index);
            DatabaseUtil.UpdateProdOrder(selectedOrder);

            if (DatabaseUtil.CheckIfNameExistsInNewestFormula(selectedOrder.getName()))
                DatabaseUtil.UpdateNewestFormula(true, selectedOrder.getName(), index);
            else DatabaseUtil.UpdateNewestFormula(false, selectedOrder.getName(), index);

            DatabaseUtil.UpdateProdOrder(selectedOrder);

            currentStage.close();
        } catch (SQLException e) {
            e.printStackTrace();
            HandleError error = new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            error.WriteToLog();
        }
    }

    /**
     * Save the formula to the selected order, and push formula to database
     */
    private void saveFormula() {
        if (isNewFormula) {
            AlertBox.display("错误", "没有已存在的配方，选择另存为");
            return;
        }
        if (!ConfirmBox.display("确认", "确定更新此配方？所有使用此配方的产品即将被更新", "是", "否"))
            return;
        try {
            ArrayList<FormulaItem> newItemList = new ArrayList<>(formulaItemTable.getItems());
            ArrayList<Formula> newFormulaList = new ArrayList<>(formulaTable.getItems());
            formula.setSimpleItemList(newItemList);
            formula.setFormulaList(newFormulaList);

            selectedOrder.setBasePrice(calcUnitPrice());

            DatabaseUtil.UpdateFormula(formula, selectedOrder.getFormulaIndex());
            if (DatabaseUtil.CheckIfNameExistsInNewestFormula(selectedOrder.getName()))
                DatabaseUtil.UpdateNewestFormula(true, selectedOrder.getName(), selectedOrder.getFormulaIndex());
            else DatabaseUtil.UpdateNewestFormula(false, selectedOrder.getName(), selectedOrder.getFormulaIndex());

            DatabaseUtil.UpdateProdOrder(selectedOrder);

            currentStage.close();
        } catch (SQLException e) {
            e.printStackTrace();
            HandleError error = new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            error.WriteToLog();
        }
    }

    /**
     * Calculate the base price
     * @return the base price
     */
    private double calcUnitPrice() {
        double totalSum = 0.0;
        double totalAmount = 0.0;
        for (Formula formula : formula.getFormulaList()) {
            totalSum += formula.getTotalPrice();
            totalAmount += formula.getAmount();
        }
        for (FormulaItem formulaItem : formula.getSimpleItemList()) {
            totalSum += formulaItem.getTotalPrice();
            totalAmount += formulaItem.getAmount();
        }
        double returnVal = Math.round(totalSum / totalAmount * 1.05 * 100.0) / 100.0;
        formulaInfoInputArray.get(1).setText(String.valueOf(returnVal));
        return returnVal;
    }

}
