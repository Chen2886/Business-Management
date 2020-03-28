package Product;

import Main.*;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ProdEditUnitPriceTable {

    // prod table headers
    private static final String[] headers = new String[] {"日期", "送货单号", "客户名称", "产品名称", "单价", "备注"};

    // all matUnitPrice property listed
    private static final String[] property = new String[]{"date", "sku", "customer", "name", "unitPrice", "note"};

    // all matUnitPrice property method listed
    private static final String[] propertyMethod = new String[]{"Date", "Sku", "Customer", "Name", "UnitPrice", "Note"};

    ArrayList<Node> inputArrayList;

    public HBox matInfoHBoxTop;
    public HBox matInfoHBoxBottom;
    public Button cancelButton;
    public Button completeButton;

    Stage stage;
    ProdUnitPrice prodUnitPrice;

    public void initData(Stage stage, ProdUnitPrice prodUnitPrice) {
        this.stage = stage;
        this.prodUnitPrice = prodUnitPrice;
        init();
    }

    private void init() {

        // button actions
        cancelButton.setOnAction(event -> {
            if (ConfirmBox.display("确认", "确定关闭窗口？单价不会被保存", "是", "否"))
                stage.close();
        });

        Method getter;
        inputArrayList = new ArrayList<>();

        // set up all the text fields and labels
        for (int i = 0; i < 3; i++) {

            Label newLabel = new Label(headers[i]);
            newLabel.setStyle("-fx-font-size: 20px;" +
                    "-fx-alignment: center-right;");
            matInfoHBoxTop.getChildren().add(newLabel);

            if (i == 0) {
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                DatePicker datePicker = new DatePicker();
                datePicker.setMaxWidth(Double.MAX_VALUE);

                datePicker.setConverter(new StringConverter<LocalDate>() {
                    @Override
                    public String toString(LocalDate localDate) {
                        if (localDate==null) {
                            return "0/0/0";
                        }
                        return dateTimeFormatter.format(localDate);
                    }

                    @Override
                    public LocalDate fromString(String string) {
                        if (string==null || string.isEmpty()) {
                            return null;
                        }
                        return LocalDate.from(dateTimeFormatter.parse(string));
                    }
                });

                datePicker.setValue(LocalDate.of(prodUnitPrice.getDate().getY(), prodUnitPrice.getDate().getM(),
                        prodUnitPrice.getDate().getD()));

                inputArrayList.add(datePicker);
                matInfoHBoxTop.getChildren().add(datePicker);
            } else {
                try {
                    TextField newTextField = new TextField();
                    newTextField.setMaxWidth(Double.MAX_VALUE);
                    newTextField.setPromptText("输入" + headers[i]);
                    inputArrayList.add(newTextField);
                    matInfoHBoxTop.getChildren().add(newTextField);

                    getter = ProdUnitPrice.class.getDeclaredMethod("get" + propertyMethod[i]);
                    newTextField.setText(String.valueOf(getter.invoke(prodUnitPrice)));
                } catch (Exception e) {
                    e.printStackTrace();
                    HandleError error = new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                            e.getMessage(), e.getStackTrace(), false);
                    error.WriteToLog();
                }
            }

        }

        for (int i = 3; i < headers.length; i++) {
            Label newLabel = new Label(headers[i]);
            newLabel.setStyle("-fx-font-size: 20px;" +
                    "-fx-alignment: center-right;");
            matInfoHBoxBottom.getChildren().add(newLabel);

            TextField newTextField = new TextField();
            newTextField.setMaxWidth(Double.MAX_VALUE);
            newTextField.setPromptText("输入" + headers[i]);
            inputArrayList.add(newTextField);
            matInfoHBoxBottom.getChildren().add(newTextField);

            try {
                getter = ProdUnitPrice.class.getDeclaredMethod("get" + propertyMethod[i]);
                newTextField.setText(String.valueOf(getter.invoke(prodUnitPrice)));
            } catch (Exception e) {
                e.printStackTrace();
                HandleError error = new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                        e.getMessage(), e.getStackTrace(), false);
                error.WriteToLog();
            }
        }

        completeButton.setOnAction(event -> updateUnitPrice());

    }

    private void updateUnitPrice() {
        // prod table headers

        Method setter;
        ProdUnitPrice newProdUnitPrice = new ProdUnitPrice();
        newProdUnitPrice.setSerialNum(prodUnitPrice.getSerialNum());
        // set up all the text fields and labels
        for (int i = 0; i < 3; i++) {

            if (i == 0) {
                DatePicker datePicker = (DatePicker) inputArrayList.get(i);
                newProdUnitPrice.setDate((datePicker.getValue() == null ? new Date(0, 0, 0) :
                        new Date(datePicker.getValue().getYear(),
                                datePicker.getValue().getMonthValue(),
                                datePicker.getValue().getDayOfMonth())));
            } else {
                try {
                    setter = ProdUnitPrice.class.getDeclaredMethod("set" + propertyMethod[i], String.class);
                    setter.invoke(newProdUnitPrice, ((TextField) (inputArrayList.get(i))).getText());
                } catch (Exception e) {
                    e.printStackTrace();
                    HandleError error = new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                            e.getMessage(), e.getStackTrace(), false);
                    error.WriteToLog();
                }
            }

        }

        for (int i = 3; i < headers.length; i++) {
            if (i == 4) {
                try {
                    setter = ProdUnitPrice.class.getDeclaredMethod("set" + propertyMethod[i], double.class);
                    setter.invoke(newProdUnitPrice, Double.parseDouble(((TextField) (inputArrayList.get(i))).getText()));
                } catch (Exception e) {
                    e.printStackTrace();
                    HandleError error = new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                            e.getMessage(), e.getStackTrace(), false);
                    error.WriteToLog();
                }
            } else {
                try {
                    setter = ProdUnitPrice.class.getDeclaredMethod("set" + propertyMethod[i], String.class);
                    setter.invoke(newProdUnitPrice, ((TextField) (inputArrayList.get(i))).getText());
                } catch (Exception e) {
                    e.printStackTrace();
                    HandleError error = new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                            e.getMessage(), e.getStackTrace(), false);
                    error.WriteToLog();
                }
            }
        }

        try {
            DatabaseUtil.UpdateProdUnitPrice(newProdUnitPrice);
            stage.close();
        } catch (SQLException e) {
            AlertBox.display("错误", "无法更新");
            e.printStackTrace();
            HandleError error = new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            error.WriteToLog();
        }

    }


}
