package Material;

// from my other packages
import Main.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class MatSearchOrder {

    // table headers
    private static final String[] tableHeaders = new String[] {"订单号", "原料名称", "类别", "订单开始日期", "订单结束日期",
            "付款日期", "到达日期", "发票日期", "发票编号", "规格", "数量", "单价", "签收人", "供应商订单编号", "备注", "供应商"};

    // all property listed
    private static final String[] propertyHeaders = new String[]{"sku", "name", "type", "orderStartDate", "orderEndDate", "paymentDate",
            "arrivalDate", "invoiceDate", "invoice", "unitAmount", "amount", "unitPrice", "signed", "skuSeller", "note", "seller"};
    // all types
    private static final String[] matOfType = new String[]{"RD", "S", "P", "A", "R", "PA"};

    private MainScreen mainScreen;

    @FXML GridPane MatEditOrderGrid;
    @FXML Label editOrderTitleLabel;
    @FXML Button cancelButton;
    @FXML Button completeButton;

    Stage currentStage;
    ObservableList<MatSeller> allSeller;
    ArrayList<Node> inputArrayList;

    /**
     * Called by other Main Controller to set stage
     * @param currentStage stage passed by main controller to close later
     * @param mainScreen the main screen controller, so this can call back to fill order
     */
    public void initData(Stage currentStage, MainScreen mainScreen) {
        this.currentStage = currentStage;
        this.mainScreen = mainScreen;
        init();
    }

    /**
     * Initialize all the element on the screen
     */
    public void init() {
        editOrderTitleLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        cancelButton.setOnAction(actionEvent -> currentStage.close());

        completeButton.setOnAction(actionEvent -> {
            try {
                ObservableList<MatOrder> newList = searchOrders();
                mainScreen.setMatSearchList(newList == null ? FXCollections.observableArrayList() : newList);
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
        ArrayList<Label> labelArrayList = new ArrayList<>();
        for(int i = 0; i < tableHeaders.length; i++) {
            Label newLabel = new Label(tableHeaders[i]);
            newLabel.setStyle("-fx-font-size: 20px;" +
                    "-fx-alignment: center-right;");

            newLabel.setMaxWidth(Double.MAX_VALUE);
            GridPane.setConstraints(newLabel, col, row++);
            labelArrayList.add(newLabel);
            if ((i + 7) % 6 == 0) {
                row = 1;
                col += 2;
            }
        }

        row = 1;
        col = 1;
        // setting up all the text field
        inputArrayList = new ArrayList<>();
        for(int i = 0; i < propertyHeaders.length; i++) {

            // type of mat, combo box
            if (i == 2) {
                ComboBox<String> newComboBox = new ComboBox<>();
                newComboBox.getItems().setAll(matOfType);
                newComboBox.setMaxWidth(Double.MAX_VALUE);
                GridPane.setConstraints(newComboBox, col, row++);
                inputArrayList.add(newComboBox);
            }

            // seller, combo box
            else if (i == propertyHeaders.length - 1) {
                ComboBox<String> sellerComboBox = new ComboBox<>();
                try {
                    allSeller = DatabaseUtil.GetAllMatSellers();
                } catch (SQLException e) {
                    sellerComboBox.setMaxWidth(Double.MAX_VALUE);
                    inputArrayList.add(sellerComboBox);
                    allSeller = FXCollections.observableArrayList();
                }

                String[] allSellerCompany = new String[allSeller.size()];
                for (int j = 0; j < allSeller.size(); j++) {
                    allSellerCompany[j] = allSeller.get(j).getCompanyName();
                }
                sellerComboBox.getItems().setAll(allSellerCompany);
                sellerComboBox.setMaxWidth(Double.MAX_VALUE);
                GridPane.setConstraints(sellerComboBox, col, row++);
                inputArrayList.add(sellerComboBox);
            }

            // dates, date picker
            else if (i == 3 || i == 4 || i == 5 || i == 6 || i == 7) {
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
                inputArrayList.add(datePicker);
            }

            // regular text field
            else {
                TextField newTextField = new TextField();
                newTextField.setMaxWidth(Double.MAX_VALUE);
                GridPane.setConstraints(newTextField, col, row++);
                inputArrayList.add(newTextField);

            }

            if ((i + 7) % 6 == 0) {
                row = 1;
                col += 2;
            }
        }

        // * setting up grid properties
        MatEditOrderGrid.setVgap(10);
        MatEditOrderGrid.setHgap(10);
        MatEditOrderGrid.getChildren().addAll(labelArrayList);
        MatEditOrderGrid.getChildren().addAll(inputArrayList);
    }

    /**
     * Generate a command, use database function to execute
     * @return the new list
     */
    private ObservableList<MatOrder> searchOrders() {
        String FinalCommand = "SELECT * FROM materialManagement WHERE ";
        ArrayList<String> SQLCommand = new ArrayList<>();

        for (int i = 0; i < inputArrayList.size(); i++) {
            // orderDate start
            if (i == 3) {
                // order date year

                DatePicker startDate = (DatePicker) inputArrayList.get(i);
                DatePicker endDate = (DatePicker) inputArrayList.get(i + 1);

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
            else if (i == 4) {
            }
            // other dates
            else if (i == 5 || i == 6 || i == 7) {
                if (((DatePicker) inputArrayList.get(i)).getValue() != null) {
                    SQLCommand.add(String.format("%s = '%s'", propertyHeaders[i] + "Year", ((DatePicker) inputArrayList.get(i)).getValue().getYear()));
                    SQLCommand.add(String.format("%s = '%s'", propertyHeaders[i] + "Month", ((DatePicker) inputArrayList.get(i)).getValue().getMonthValue()));
                    SQLCommand.add(String.format("%s = '%s'", propertyHeaders[i] + "Day", ((DatePicker) inputArrayList.get(i)).getValue().getDayOfMonth()));
                }
            }
            // combo box mat type
            else if (i == 2) {
                try {
                    if (!((ComboBox) inputArrayList.get(i)).getValue().toString().equals(""))
                        SQLCommand.add(String.format("%s = '%s'", "type", ((ComboBox) inputArrayList.get(i)).getValue().toString()));
                } catch (Exception ignored) {}
            }
            else if (i == inputArrayList.size() - 1) {
                try {
                    String companyName = ((ComboBox) inputArrayList.get(i)).getValue().toString();
                    for (MatSeller seller: allSeller) {
                        if (seller.getCompanyName().equals(companyName))
                            SQLCommand.add(String.format("%s = '%s'", "sellerId", seller.getSellerId()));
                    }
                } catch (Exception ignored) {}
            }
            // string/numbers
            else {
                String value = ((TextField) inputArrayList.get(i)).getText();
                if (!value.equals("")) {
                    SQLCommand.add(String.format("%s = '%s'", propertyHeaders[i], value));
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
            return DatabaseUtil.ExecuteMatOrderCommand(FinalCommand);
        } catch (Exception e) {
            return null;
        }
    }

}
