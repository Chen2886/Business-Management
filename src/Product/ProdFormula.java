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

import java.io.*;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class ProdFormula implements Serializable {

    private static String[] propertyMethodName = new String[] {"ItemName", "Amount", "UnitPrice", "TotalPrice"};
    private static String[] property = new String[] {"itemName", "amount", "unitPrice", "totalPrice"};
    private static String[] header = new String[] {"原料名称", "数量", "单价", "金额"};

    @FXML Button addFormulaButton;
    @FXML Button cancelButton;
    @FXML Button saveButton;
    @FXML Button saveNewButton;
    @FXML TableView<Formula> formulaTable;
    @FXML TableView<FormulaItem> formulaItemTable;
    @FXML Label prodFormulaTitleLabel;
    @FXML HBox infoHBox;

    Button addItemButton;

    ArrayList<TextField> inputArray;
    ArrayList<TableColumn<FormulaItem, ?>> itemColumnList;
    ArrayList<TableColumn<Formula, ?>> formulaColumnList;

    Stage currentStage;
    ProductOrder selectedOrder;
    Formula formula;

    /**
     * Called by main controller to give the selected order
     * @param selectedOrder the order that was selected, to fill the information
     * @param currentStage the stage, so it can be closed later
     */
    public void initData(ProductOrder selectedOrder, Stage currentStage) {
        this.selectedOrder = selectedOrder;
        this.currentStage = currentStage;
        formula = selectedOrder.getFormula();
        itemColumnList = new ArrayList<>();
        init();
    }

    /**
     * Initialize everything on the screen
     */
    private void init() {

        initItemTable();
        initFormulaTable();

        if (selectedOrder.getFormula() == null) {
            // no formula given found

            // TODO: Find formula

            // TODO: If formula found, populate table

            // TODO: If formula not found, create new formula, save to database

            cancelButton.setOnAction(event -> {
                if (ConfirmBox.display("确认", "确定关闭窗口？所有进度即将丢失", "是", "否"))
                    currentStage.close();
            });

            addFormulaButton.setOnAction(event -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("ProdFormulaAddItem.fxml"));
                    Parent newScene = loader.load();
                    Stage stage = new Stage();

                    ProdFormulaAddItem prodFormulaAddItem = loader.getController();
                    prodFormulaAddItem.initData(stage);
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.setTitle("添加原料");
                    stage.setScene(new Scene(newScene));
                    stage.show();
                } catch (Exception e) {
                    AlertBox.display("错误", "窗口错误");
                    e.printStackTrace();
                    HandleError error = new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                            e.getMessage(), e.getStackTrace(), false);
                    error.WriteToLog();
                }
            });

            saveNewButton.setOnAction(event -> saveNewFormula());

        } else {
            // TODO: formula given, populate table
        }
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
                        setter = Formula.class.getDeclaredMethod("set" + propertyMethodName[i], String.class);
                        setter.invoke(formulaItem, currentTextField.getText());
                    } catch (Exception ignored) {
                        ignored.printStackTrace();
                    }
                }
            } else if (i == 1 || i == 2) {
                if (currentTextField.getText() != null && !currentTextField.getText().equals("")) {
                    empty = false;
                    try {
                        setter = Formula.class.getDeclaredMethod("set" + propertyMethodName[i], double.class);
                        setter.invoke(formulaItem, Double.parseDouble(currentTextField.getText()));
                    } catch (Exception ignored) {
                        ignored.printStackTrace();
                    }
                }
            }
        }

        formulaItem.setTotalPrice();
        if (!empty) addItemToList(formulaItem);
        for(TextField textField : inputArray) textField.clear();
    }

    /**
     * Save the new formula
     */
    private void saveNewFormula() {

        if (formula == null) {
            AlertBox.display("错误", "没有配方，选择保存");
            return;
        }

        if (!ConfirmBox.display("确认","确定另存为？此订单即将改变","是","否")) return;

        // TODO: new screen should not have save new

        // try to create folder
        String folder = selectedOrder.getName() + '-' + selectedOrder.getOrderDate().toStringWithoutSlash() + '-' +
                GregorianCalendar.getInstance().get(Calendar.HOUR_OF_DAY) + ":" +
                GregorianCalendar.getInstance().get(Calendar.MINUTE);

        File file = new File("Formula/" + folder + "/");
        boolean dirSuccess = file.mkdir();
        formula.setFolderPath("Formula/" + folder + "/");
        selectedOrder.setFormula(formula);


        if (dirSuccess) {
            // creation successful

            // push to Database, so it can be read in the future
            try {
                DatabaseUtil.UpdateNewstFormula(selectedOrder.getName(), formula);
            } catch (SQLException e) {
                e.printStackTrace();
                HandleError error = new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                        e.getMessage(), e.getStackTrace(), false);
                error.WriteToLog();
            }

            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(formula.getFolderPath() + "Main.csv", false));
                bw.write("");
                for (Formula item : formulaItemTable.getItems()) {
                    bw.write(item.toString());
                }
                bw.close();
            } catch (IOException e) {
                AlertBox.display("错误", "写入错误");
                e.printStackTrace();
                HandleError error = new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                        e.getMessage(), e.getStackTrace(), false);
                error.WriteToLog();
            }
        }
        else {
            // creation failed, folder probably already exists
            AlertBox.display("错误", "无法另存为，文件夹已存在！");
        }
    }

    /**
     * Set up the table view
     */
    private void initItemTable() {

        // init table
        // setting up first action column
        TableColumn<FormulaItem, String> actionColumn = new TableColumn<>("动作");
        actionColumn.setSortable(false);
        itemColumnList.add(actionColumn);

        // loop to set up all regular columns
        for (int i = 0; i < property.length; i++) {
            if (i == 1 || i == 2 || i == 3) {
                // Doubles
                TableColumn<Formula, Double> newColumn = new TableColumn<>(header[i]);
                newColumn.setCellValueFactory(new PropertyValueFactory<>(property[i]));
                newColumn.setStyle("-fx-alignment: CENTER;");
                newColumn.setMinWidth(100);
                itemColumnList.add(newColumn);
            } else {
                // String
                TableColumn<Formula, String> newColumn = new TableColumn<>(header[i]);
                newColumn.setCellValueFactory(new PropertyValueFactory<>(property[i]));
                newColumn.setStyle("-fx-alignment: CENTER;");
                newColumn.setMinWidth(100);
                itemColumnList.add(newColumn);
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
                                    removeItemFromList();
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
        formulaItemTable.getItems().setAll(formula.getFormulaList());

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
        totalPrice.setOnKeyTyped(event -> {
            try {
                totalPrice.setText(String.valueOf(Double.parseDouble(inputArray.get(1).getText()) *
                        Double.parseDouble(inputArray.get(2).getText())));
            } catch (Exception ignored) {}
        });
        totalPrice.setOnMouseClicked(event -> {
            try {
                totalPrice.setText(String.valueOf(Double.parseDouble(inputArray.get(1).getText()) *
                        Double.parseDouble(inputArray.get(2).getText())));
            } catch (Exception ignored) {}
        });
    }


    /**
     * Set up the table view
     */
    private void initFormulaTable() {

        // init table
        // setting up first action column
        TableColumn<Formula, String> actionColumn = new TableColumn<>("动作");
        actionColumn.setSortable(false);
        itemColumnList.add(actionColumn);

        // loop to set up all regular columns
        for (int i = 0; i < property.length; i++) {
            if (i == 1 || i == 2 || i == 3) {
                // Doubles
                TableColumn<Formula, Double> newColumn = new TableColumn<>(header[i]);
                newColumn.setCellValueFactory(new PropertyValueFactory<>(property[i]));
                newColumn.setStyle("-fx-alignment: CENTER;");
                newColumn.setMinWidth(100);
                itemColumnList.add(newColumn);
            } else {
                // String
                TableColumn<Formula, String> newColumn = new TableColumn<>(header[i]);
                newColumn.setCellValueFactory(new PropertyValueFactory<>(property[i]));
                newColumn.setStyle("-fx-alignment: CENTER;");
                newColumn.setMinWidth(100);
                itemColumnList.add(newColumn);
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
                                    removeItemFromList();
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
        formulaItemTable.getItems().setAll(formula.getFormulaList());

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
        totalPrice.setOnKeyTyped(event -> {
            try {
                totalPrice.setText(String.valueOf(Double.parseDouble(inputArray.get(1).getText()) *
                        Double.parseDouble(inputArray.get(2).getText())));
            } catch (Exception ignored) {}
        });
        totalPrice.setOnMouseClicked(event -> {
            try {
                totalPrice.setText(String.valueOf(Double.parseDouble(inputArray.get(1).getText()) *
                        Double.parseDouble(inputArray.get(2).getText())));
            } catch (Exception ignored) {}
        });
    }

    /**
     * public function for other controller to call, to add to the list, and refresh table
     * @param item the item to be added to list
     */
    public void addItemToList(FormulaItem item) {
        formula.addItem(item);
        formulaItemTable.getItems().clear();
        formulaItemTable.getItems().setAll(formula.getFormulaList());
    }

    /**
     * public function for other controller to call, to remove item from the list, and refresh table
     * @param item the item to be removed to list
     */
    public void removeItemFromList(FormulaItem item) {
        formula.removeItem(item);
        formulaItemTable.getItems().clear();
        formulaItemTable.getItems().setAll(formula.getFormulaList());
    }
}
