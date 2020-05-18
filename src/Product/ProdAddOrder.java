package Product;

import Main.*;

// from my other packages
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.controlsfx.control.textfield.TextFields;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ProdAddOrder {

    // prod table headers
    private static final String[] prodHeaders = new String[]{"订单日期", "送货单号", "\u3000\u3000客户", "产品名称",
            "\u3000\u3000规格", "\u3000\u3000数量", "\u3000\u3000单价", "\u3000\u3000备注", "张家港生产"};

    // all prod property listed
    private static final String[] prodProperty = new String[]{"OrderDate", "Sku", "Customer", "Name",
            "UnitAmount", "Amount", "UnitPrice", "Note", "Remote"};

    public JFXDatePicker orderDatePicker;
    public JFXToggleButton remoteToggle;

    @FXML
    Button prodAddOrderCancelButton;
    @FXML
    Button prodAddOrderCompleteButton;
    @FXML
    Button prodAddOrderContinueButton;
    @FXML
    GridPane prodAddOrderGrid;
    @FXML
    Label prodAddOrderTitle;

    Stage currentStage;
    private ArrayList<Node> prodOrderInputArray;

    /**
     * Called by main controller, pass in the stage for later closing, and init the screen
     *
     * @param currentStage the current stage so it can be closed
     */
    public void initData(Stage currentStage) {
        this.currentStage = currentStage;
        init();
    }

    /**
     * initialize all labels and text fields for add prod order grid
     */
    private void init() {

        prodAddOrderGrid.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ESCAPE)) {
                if (ConfirmBox.display(ConfirmMessage.CANCEL))
                    currentStage.close();
            }
        });

        prodAddOrderCancelButton.setOnAction(actionEvent -> {
            if (ConfirmBox.display(ConfirmMessage.CANCEL))
                currentStage.close();
        });

        prodAddOrderCompleteButton.setOnAction(actionEvent -> addOrder(false));
        prodAddOrderContinueButton.setOnAction(actionEvent -> addOrder(true));
        prodAddOrderCompleteButton.setOnKeyReleased(actionEvent -> {
            if (actionEvent.getCode() == KeyCode.ENTER) addOrder(false);
        });
        prodAddOrderContinueButton.setOnKeyReleased(actionEvent -> {
            if (actionEvent.getCode() == KeyCode.ENTER) addOrder(true);
        });

        prodAddOrderTitle.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        int row = 1;
        int col = 0;

        // setting up all the text field
        prodOrderInputArray = new ArrayList<>();
        for (int i = 0; i < prodProperty.length; i++) {

            // dates, date picker
            if (i == 0) {
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                orderDatePicker.setConverter(new StringConverter<>() {
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
                orderDatePicker.setValue(LocalDate.now());
                prodOrderInputArray.add(orderDatePicker);
                row++;
            } else if (i == 8) {
                prodOrderInputArray.add(remoteToggle);
            } else {
                // regular text field
                JFXTextField newTextField = new JFXTextField();
                if (i == 2) {
                    FinalConstants.updateAutoCompleteProdCustomerName();
                    TextFields.bindAutoCompletion(newTextField, FinalConstants.autoCompleteProdCustomerName);
                } else if (i == 3) {
                    FinalConstants.updateAutoCompleteProdName();
                    TextFields.bindAutoCompletion(newTextField, FinalConstants.autoCompleteProdName);
                }
                newTextField.setPromptText("输入" + prodHeaders[i].replace("\u3000", ""));
                newTextField.setStyle("-fx-font-size: 20px; -fx-alignment: CENTER");
                GridPane.setConstraints(newTextField, col, row++);
                prodOrderInputArray.add(newTextField);
            }

            if ((i + 4) % 3 == 0) {
                // grid constraint
                row = 1;
                col += 1;
            }
        }

        // auto unit price
        TextField unitPrice = (TextField) prodOrderInputArray.get(6);
        unitPrice.setOnKeyTyped(keyEvent -> {
            try {
                unitPrice.setText(String.valueOf(DatabaseUtil.GetProdUnitPrice(((TextField) prodOrderInputArray.get(3)).getText(),
                        ((TextField) prodOrderInputArray.get(2)).getText())));
            } catch (SQLException e) {
                new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                        e.getMessage(), e.getStackTrace(), false);
            }
        });
        unitPrice.setOnMouseClicked(keyEvent -> {
            try {
                System.out.println(((TextField) prodOrderInputArray.get(3)).getText());
                unitPrice.setText(String.valueOf(DatabaseUtil.GetProdUnitPrice(((TextField) prodOrderInputArray.get(3)).getText(),
                        ((TextField) prodOrderInputArray.get(2)).getText())));
            } catch (SQLException e) {
                new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                        e.getMessage(), e.getStackTrace(), false);
            }
        });

        prodAddOrderGrid.setVgap(10);
        prodAddOrderGrid.setHgap(10);
        prodAddOrderGrid.getChildren().addAll(prodOrderInputArray.subList(1, prodOrderInputArray.size() - 1));

    }

    private void addOrder(boolean cont) {
        ProductOrder newOrder = new ProductOrder(SerialNum.getSerialNum(DBOrder.PROD));
        Method setter;

        for (int i = 0; i < prodOrderInputArray.size(); i++) {
            if (i == 0) {
                // date picker
                try {
                    newOrder.setOrderDate(((DatePicker) prodOrderInputArray.get(i)).getValue() == null ? new Date(0, 0, 0) :
                            new Date(((DatePicker) prodOrderInputArray.get(i)).getValue().getYear(),
                                    ((DatePicker) prodOrderInputArray.get(i)).getValue().getMonthValue(),
                                    ((DatePicker) prodOrderInputArray.get(i)).getValue().getDayOfMonth()));
                } catch (NullPointerException ignored) {
                }
            } else if (i == 4 || i == 5 || i == 6) {
                // double
                TextField currentTextField = (TextField) prodOrderInputArray.get(i);
                if (!currentTextField.getText().equals("")) {
                    try {
                        setter = ProductOrder.class.getDeclaredMethod("set" + prodProperty[i], double.class);
                        setter.invoke(newOrder, Double.parseDouble(currentTextField.getText()));
                    } catch (Exception e) {
                        AlertBox.display("错误", prodHeaders[i] + "格式输入错误, 数字默认0");
                    }
                }
            } else if (i == 8) {
                // remote
                newOrder.setRemote(remoteToggle.selectedProperty().get() ? 1 : 0);
            } else {
                // string
                TextField currentTextField = (TextField) prodOrderInputArray.get(i);
                if (!currentTextField.getText().equals("")) {
                    try {
                        setter = ProductOrder.class.getDeclaredMethod("set" + prodProperty[i], String.class);
                        setter.invoke(newOrder, currentTextField.getText());
                    } catch (Exception e) {
                        AlertBox.display("错误", "读取信息错误！");
                        new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                                e.getMessage(), e.getStackTrace(), false);
                    }
                }
            }
        }

        newOrder.setKgAmount();
        newOrder.setTotalPrice();
        if (DatabaseUtil.CheckIfNameExistsInNewestFormula(newOrder.getName())) {
            try {
                int newIndex = DatabaseUtil.GetNewestFormulaIndex(newOrder.getName());
                Formula formula = DatabaseUtil.GetFormulaByIndex(newIndex);
                newOrder.setFormulaIndex(newIndex);
                newOrder.setBasePrice(calcUnitPrice(formula));
            } catch (SQLException e) {
                AlertBox.display("错误", "读取配方错误！");
                new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                        e.getMessage(), e.getStackTrace(), false);
            }
        }

        try {
            DatabaseUtil.AddProdOrder(newOrder);
            if (cont) clearProdOrderFields();
            else currentStage.close();
        } catch (SQLException e) {
            AlertBox.display("错误", "添加产品订单错误！");
            new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            currentStage.close();
        }
    }

    /**
     * Clear all input area for add order
     */
    private void clearProdOrderFields() {
        for (Node node : prodOrderInputArray) {
            if (node instanceof TextField && !node.equals(prodOrderInputArray.get(1))) ((TextField) node).clear();
        }
    }

    /**
     * Calculate the base price
     *
     * @return the base price
     */
    private double calcUnitPrice(Formula formula) {
        double totalSum = 0.0;
        double totalAmount = 0.0;
        if (formula == null) return 0.0;
        for (Formula f : formula.getFormulaList()) {
            totalSum += f.getTotalPrice();
            totalAmount += f.getAmount();
        }
        return Math.round(totalSum / totalAmount * 1.05 * 100.0) / 100.0;
    }


}
