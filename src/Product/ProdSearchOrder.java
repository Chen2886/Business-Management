package Product;

import Main.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;


public class ProdSearchOrder {

    // prod table headers
    private static final String[] prodHeaders = new String[] {"开始订单日期", "结束订单日期", "送货单号", "\u3000\u3000客户", "产品名称",
            "\u3000\u3000规格", "\u3000\u3000数量", "\u3000\u3000单价", "\u3000\u3000备注"};

    // all prod property listed
    private static final String[] prodProperty = new String[]{"OrderDateStart", "OrderDateEnd", "Sku", "Customer", "Name",
            "UnitAmount", "Amount", "UnitPrice", "Note"};

    @FXML GridPane prodSearchOrderGrid;
    @FXML Label prodSearchOrderTitle;
    @FXML Button prodSearchOrderCancelButton;
    @FXML Button prodSearchOrderCompleteButton;

    private MainScreen mainScreen;
    Stage currentStage;
    private ArrayList<Node> prodOrderInputArray;

    /**
     * Called by main controller, pass in the stage for later closing, and init the screen
     * @param currentStage the current stage so it can be closed
     */
    public void initData(Stage currentStage, MainScreen mainScreen) {
        this.currentStage = currentStage;
        this.mainScreen = mainScreen;
        init();
    }

    /**
     * initialize all labels and text fields for add prod order grid
     */
    private void init() {

        prodSearchOrderTitle.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        prodSearchOrderGrid.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ESCAPE)) currentStage.close();
        });

        prodSearchOrderCancelButton.setOnAction(actionEvent -> currentStage.close());

        prodSearchOrderCompleteButton.setOnAction(actionEvent -> {
            try {
                ObservableList<ProductOrder> newList = searchOrders();
                mainScreen.setProdSearchList(newList == null ? FXCollections.observableArrayList() : newList);
                currentStage.close();
            } catch (Exception e) {
                e.printStackTrace();
                HandleError error = new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                        e.getMessage(), e.getStackTrace(), false);
                error.WriteToLog();
                currentStage.close();
            }
        });

        int row = 1;
        int col = 0;

        // setting up all the labels
        ArrayList<Label> prodOrderLabelArray = new ArrayList<>();
        for(int i = 0; i < prodHeaders.length; i++) {
            Label newLabel = new Label(prodHeaders[i]);
            newLabel.setStyle("-fx-font-size: 20px;" +
                    "-fx-alignment: center-right;");
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
            if (i == 0 || i == 1) {
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
                newTextField.setPromptText("输入" + prodHeaders[i].replace("\u3000", ""));
                GridPane.setConstraints(newTextField, col, row++);
                prodOrderInputArray.add(newTextField);
            }

            if ((i + 5) % 4 == 0) {
                row = 1;
                col += 2;
            }
        }

        prodSearchOrderGrid.setVgap(10);
        prodSearchOrderGrid.setHgap(10);
        prodSearchOrderGrid.getChildren().addAll(prodOrderLabelArray);
        prodSearchOrderGrid.getChildren().addAll(prodOrderInputArray);
    }

    /**
     * Generate a command, use database function to execute
     * @return the new list
     */
    private ObservableList<ProductOrder> searchOrders() {
        String FinalCommand = "SELECT * FROM productManagement WHERE ";
        ArrayList<String> SQLCommand = new ArrayList<>();

        for (int i = 0; i < prodOrderInputArray.size(); i++) {
            // orderDate start
            if (i == 0) {
                // order date year

                DatePicker startDate = (DatePicker) prodOrderInputArray.get(i);
                DatePicker endDate = (DatePicker) prodOrderInputArray.get(i + 1);

                try {
                    if (startDate.getValue() == null && endDate.getValue() != null) {
                        AlertBox.display("错误","没有输入开始日期");
                        throw new NullPointerException();
                    }
                    else if (startDate.getValue() != null && endDate.getValue() == null) {
                        AlertBox.display("错误","没有输入结束日期");
                        throw new NullPointerException();
                    }
                    else if (startDate.getValue() == null && endDate.getValue() == null) {
                        throw new NullPointerException();
                    }
                    else if (startDate.getValue() != null && endDate.getValue() != null) {
                        if (startDate.getValue().getYear() > endDate.getValue().getYear()) {
                            AlertBox.display("错误", "开始日期小于结束日期");
                            throw new NullPointerException();
                        } else if (startDate.getValue().getYear() == endDate.getValue().getYear()) {
                            if (startDate.getValue().getMonthValue() > endDate.getValue().getMonthValue()) {
                                AlertBox.display("错误", "开始日期小于结束日期");
                                throw new NullPointerException();
                            } else if (startDate.getValue().getMonthValue() == endDate.getValue().getMonthValue()) {
                                // month okay
                                if (startDate.getValue().getDayOfMonth() > endDate.getValue().getDayOfMonth()) {
                                    AlertBox.display("错误", "开始日期小于结束日期");
                                    throw new NullPointerException();
                                }
                            }
                        }

                        int[][] input = new int[2][3];
                        input[0][0] = startDate.getValue().getYear();
                        input[0][1] = startDate.getValue().getMonthValue();
                        input[0][2] = startDate.getValue().getDayOfMonth();

                        input[1][0] = endDate.getValue().getYear();
                        input[1][1] = endDate.getValue().getMonthValue();
                        input[1][2] = endDate.getValue().getDayOfMonth();

                        int startDateNum = input[0][0] * 10000 + input[0][1] * 100 + input[0][2];
                        int endDateNum = input[1][0] * 10000 + input[1][1] * 100 + input[1][2];

                        SQLCommand.add(String.format("(orderDateYear * 10000 + orderDateMonth * 100 + orderDateDay) >= %d " +
                                "AND (orderDateYear * 10000 + orderDateMonth * 100 + orderDateDay) <= %d", startDateNum, endDateNum));

                    }
                    else {
                        AlertBox.display("错误","开始或结束日期错误");
                        throw new NullPointerException();
                    }
                }
                catch (NullPointerException ignored) { }
            }
            // orderDate end, skip
            else if (i == 1) {
                continue;
            }
            // string/numbers
            else {
                String value = ((TextField) prodOrderInputArray.get(i)).getText();
                if (!value.equals("")) {
                    SQLCommand.add(String.format("%s = '%s'", prodProperty[i], value));
                }
            }
        }

        if (SQLCommand.size() == 0) {
            currentStage.close();
        } else {
            for (int i = 0; i < SQLCommand.size(); i++) {
                if (i == SQLCommand.size() - 1) {
                    FinalCommand += SQLCommand.get(i);
                }
                else {
                    FinalCommand += SQLCommand.get(i) + " AND ";
                }
            }
        }

        try {
            return DatabaseUtil.ExecuteProdOrderCommand(FinalCommand);
        } catch (Exception e) {
            return null;
        }
    }


}
