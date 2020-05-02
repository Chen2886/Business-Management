package Product;

import Main.*;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class ProdFormulaEdit {

    private static String[] propertyMethodName = new String[]{"Name", "Amount", "UnitPrice", "TotalPrice"};
    private static String[] property = new String[]{"name", "amount", "unitPrice", "totalPrice"};
    private static String[] header = new String[]{"原料名称", "数量", "单价", "金额"};
    private static String[] formulaInfoHeader = new String[]{"配方名称", "数量", "单价", "金额"};

    @FXML HBox formulaInfoHBox;
    @FXML Button cancelButton;
    @FXML Button saveButton;
    @FXML TableView<Formula> formulaTable;
    @FXML TableView<FormulaItem> formulaItemTable;
    @FXML HBox infoHBox;

    Button addItemButton;

    ArrayList<TextField> inputArray;
    ArrayList<TextField> formulaInfoInputArray;
    ArrayList<TableColumn<FormulaItem, ?>> itemColumnList;
    ArrayList<TableColumn<Formula, ?>> formulaColumnList;

    TableView<FormulaItem> parentItemTableView;
    TableView<Formula> parentFormulaTableView;
    FormulaItem parentItem;

    Stage currentStage;
    Formula formula;
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    /**
     * Called by main controller to give the selected order
     *
     * @param formula the formula that needs to be edited, to fill the information
     * @param currentStage  the stage, so it can be closed later
     */
    public void initData(FormulaItem parentItem, Formula formula, Stage currentStage, TableView<FormulaItem> parentItemTableView,
                         TableView<Formula> parentFormulaTableView) {
        this.parentItem = parentItem;
        this.formula = formula;
        this.currentStage = currentStage;
        itemColumnList = new ArrayList<>();
        formulaColumnList = new ArrayList<>();

        this.parentFormulaTableView = parentFormulaTableView;
        this.parentItemTableView = parentItemTableView;
        init();
    }

    /**
     * Initialize everything on the screen
     */
    private void init() {

        initItemTable();
        initFormulaTable();
        initFormulaInfoHBox();
        initInfoHBox();

        currentStage.setMinHeight(screenSize.height * 0.9);
        currentStage.setMinWidth(screenSize.width * 0.6);
        currentStage.setOnCloseRequest(event -> {
            AlertBox.display("错误", "使用右下角按钮。");
            event.consume();
        });

        cancelButton.setOnAction(event -> {
            if (ConfirmBox.display("确认", "确定关闭窗口？进度即将丢失", "是", "否"))
                currentStage.close();
        });

        saveButton.setOnAction(event -> saveFormula());

    }

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
            try {
                getter = Formula.class.getDeclaredMethod("get" + propertyMethodName[i]);
                newTextField.setText(String.valueOf(getter.invoke(formula)));
            } catch (Exception e) {e.printStackTrace();}
            formulaInfoHBox.getChildren().add(newTextField);
            formulaInfoInputArray.add(newTextField);
        }

        // auto price
        TextField totalPrice = formulaInfoInputArray.get(formulaInfoInputArray.size() - 1);
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
        TextField totalPrice = inputArray.get(inputArray.size() - 1);
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
            textField.textProperty().addListener(((observableValue, old, newVal) -> {
                if (newVal == null || newVal.equals("")) {
                    totalPrice.setText("0.0");
                } else {
                    try {
                        totalPrice.setText(String.valueOf(Double.parseDouble(inputArray.get(1).getText()) *
                                Double.parseDouble(inputArray.get(2).getText())));
                    } catch (Exception ignored) {}
                }
            }));
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
            FXMLLoader loader = new FXMLLoader();
            FileInputStream fileInputStream = new FileInputStream(new File(Main.fxmlPath + "ProdFormulaEdit.fxml"));
            Parent newScene = loader.load(fileInputStream);
            Stage stage = new Stage();

            ProdFormulaEdit prodFormulaEdit = loader.getController();
            prodFormulaEdit.initData(item, newFormula, stage, formulaItemTable, formulaTable);

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("编辑配方");

            Scene scene = new Scene(newScene);
            scene.getStylesheets().add("file:///" + Main.styleSheetPath);
            stage.setScene(scene);

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
     * @param inputFormula the formula to be added to list
     */
    public void addFormulaToList(Formula inputFormula) {
        formula.addFormula(inputFormula);
        formulaTable.getItems().clear();
        formulaTable.getItems().setAll(inputFormula.getFormulaList());
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
     * @param formula the formula to be removed to list
     */
    public void removeFormulaFromList(Formula formula) {
        formula.removeFormula(formula);
        formulaTable.getItems().clear();
        formulaTable.getItems().setAll(formula.getFormulaList());
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
    private void saveFormula() {

        formula.setName(formulaInfoInputArray.get(0).getText());
        formula.setAmount(Double.parseDouble(formulaInfoInputArray.get(1).getText()));
        formula.setUnitPrice(calcUnitPrice());
        formula.setTotalPrice();

        if (parentItem == null) {
            // edit
            parentFormulaTableView.getItems().remove(formula);
            parentFormulaTableView.getItems().add(formula);
        } else {
            // turn item into formula
            parentItemTableView.getItems().remove(parentItem);
            parentFormulaTableView.getItems().add(formula);
        }

        currentStage.close();
    }

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
        formulaInfoInputArray.get(3).setText(String.valueOf(returnVal));
        return returnVal;
    }
}
