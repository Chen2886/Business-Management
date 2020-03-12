package Product;

import Main.*;

// from my other packages
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ProdAddOrderModifySellerController {

    // prod table headers
    // TODO: Add Formula
    private static final String[] prodHeaders = new String[] {"订单日期", "送货单号", "客户", "产品名称",
            "规格", "数量", "单价", "备注"};

    // all prod property listed
    private static final String[] prodProperty = new String[]{"OrderDate", "Sku", "Customer", "Name",
            "UnitAmount", "Amount", "UnitPrice", "Note"};

    @FXML Button prodAddOrderCancelButton;
    @FXML Button prodAddOrderCompleteButton;
    @FXML Button prodAddOrderContinueButton;
    @FXML GridPane prodAddOrderGrid;
    @FXML Label prodAddOrderTitle;

    Stage currentStage;
    private ArrayList<Node> prodOrderInputArray;

    /**
     * Called by main controller, pass in the stage for later closing, and init the screen
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
                if (ConfirmBox.display("确认", "确定关闭窗口？进度不会被保存", "是", "否"))
                    currentStage.close();
            }
        });

        prodAddOrderCancelButton.setOnAction(actionEvent -> {
            if (ConfirmBox.display("确认", "确定取消？此订单不会被保存", "确认", "取消"))
                currentStage.close();
        });

        prodAddOrderCompleteButton.setOnAction(actionEvent -> {
            addOrder(false);
        });

        prodAddOrderContinueButton.setOnAction(actionEvent -> {
            addOrder(true);
        });

        prodAddOrderTitle.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        int row = 1;
        int col = 0;

        // setting up all the labels
        ArrayList<Label> prodOrderLabelArray = new ArrayList<>();
        for(int i = 0; i < prodHeaders.length; i++) {
            Label newLabel = new Label(prodHeaders[i]);
            newLabel.setStyle("-fx-font-size: 20px;" +
                    "-fx-alignment: center-right;");
            // newLabel.setMaxWidth(Double.MAX_VALUE);
            GridPane.setConstraints(newLabel, col, row++);
            prodOrderLabelArray.add(newLabel);
            if ((i + 5) % 4 == 0) {
                row = 1;
                col += 2;
            }
        }

        row = 1;
        col = 1;

        // setting up all the text field
        prodOrderInputArray = new ArrayList<>();
        for(int i = 0; i < prodProperty.length; i++) {

            // dates, date picker
            if (i == 0) {
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                DatePicker datePicker = new DatePicker();

                datePicker.setConverter(new StringConverter<>() {
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


                GridPane.setConstraints(datePicker, col, row++);
                prodOrderInputArray.add(datePicker);
            }

            // regular text field
            else {
                TextField newTextField = new TextField();
                newTextField.setPromptText("输入" + prodHeaders[i]);
                GridPane.setConstraints(newTextField, col, row++);
                prodOrderInputArray.add(newTextField);

            }

            if ((i + 5) % 4 == 0) {
                row = 1;
                col += 2;
            }
        }

        prodAddOrderGrid.setVgap(10);
        prodAddOrderGrid.setHgap(10);
        prodAddOrderGrid.getChildren().addAll(prodOrderLabelArray);
        prodAddOrderGrid.getChildren().addAll(prodOrderInputArray);

    }

    private void addOrder(boolean cont) {
        ProductOrder newOrder = new ProductOrder(ProdSerialNum.getProdSerialNum());
        Method setter;

        for (int i = 0; i < prodOrderInputArray.size(); i++) {
            if (i == 0) {
                // date picker
                try {
                    newOrder.setOrderDate(((DatePicker) prodOrderInputArray.get(i)).getValue() == null ? new Date(0, 0, 0) :
                            new Date(((DatePicker) prodOrderInputArray.get(i)).getValue().getYear(),
                                    ((DatePicker) prodOrderInputArray.get(i)).getValue().getMonthValue(),
                                    ((DatePicker) prodOrderInputArray.get(i)).getValue().getDayOfMonth()));
                } catch (NullPointerException ignored) {}
            }
            else if (i == 4 || i == 5 || i == 6) {
                // double
                TextField currentTextField = (TextField) prodOrderInputArray.get(i);
                if (!currentTextField.getText().equals("")) {
                    try {
                        setter = ProductOrder.class.getDeclaredMethod("set" + prodProperty[i], double.class);
                        setter.invoke(newOrder, Double.parseDouble(currentTextField.getText()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            else {
                // string
                TextField currentTextField = (TextField) prodOrderInputArray.get(i);
                if (!currentTextField.getText().equals("")) {
                    try {
                        setter = ProductOrder.class.getDeclaredMethod("set" + prodProperty[i], String.class);
                        setter.invoke(newOrder, currentTextField.getText());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        newOrder.setKgAmount();
        newOrder.setTotalPrice();

        try {
            DatabaseUtil.AddProdOrder(newOrder);
            if (cont) clearProdOrderFields();
            else currentStage.close();
        } catch (Exception e) {
            AlertBox.display("错误", "添加错误");
            e.printStackTrace();
            HandleError error = new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            error.WriteToLog();
            currentStage.close();
        }
    }

    /**
     * Clear all input area for add order
     */
    private void clearProdOrderFields() {
        for (int i = 0; i < prodOrderInputArray.size(); i++) {
            if (prodOrderInputArray.get(i) instanceof TextField) ((TextField) prodOrderInputArray.get(i)).clear();
        }
    }


}
