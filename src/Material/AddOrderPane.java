package Material;

import Main.DatabaseUtil;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.util.StringConverter;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class AddOrderPane {
    private static final String[] matOfType = new String[]{"RD", "S", "P", "A", "R", "Pa"};
    private static final String headerStyle = "-fx-border-style: solid;\n" +
            "-fx-border-width: 0 0 1 0;\n" +
            "-fx-border-color: black;\n" +
            "-fx-font: 24 arial;";
    private static final String standardFont = "-fx-font: 15 arial;";

    private ArrayList<Seller> sellerArrayList;
    Order order;

    Region regionOne;
    Region regionTwo;

    VBox nonOptionalVbox;
    VBox optionalVbox;
    VBox addOrderAll;

    GridPane nonOptionalGridPane;
    GridPane optionalGridPane;

    HBox buttonHBox;
    HBox infoEnterHBox;

    // non optional grid
    private TextField skuInput;
    private TextField matNameInput;
    private TextField unitAmountInput;
    private TextField amountInput;
    private TextField unitPriceInput;
    private ComboBox matTypeComboBox;
    private ComboBox sellerComboBox;
    private DatePicker orderDatePicker;

    // optional grid
    private DatePicker paymentDatePicker;
    private DatePicker arrivalDatePicker;
    private DatePicker invoiceDatePicker;
    private TextField invoiceNumInput;
    private TextField signedByInput;
    private TextField skuBySellerInput;
    private TextField noteInput;

    public AddOrderPane() {
        regionOne = new Region();
        HBox.setHgrow(regionOne, Priority.ALWAYS);
        regionTwo = new Region();
        HBox.setHgrow(regionTwo, Priority.ALWAYS);

        nonOptionalVbox = new VBox();
        optionalVbox = new VBox();
        nonOptionalGridPane = new GridPane();
        optionalGridPane = new GridPane();
        buttonHBox = new HBox();
        infoEnterHBox = new HBox();
        addOrderAll = new VBox();
    }

    /**
     * Set up everything for add order
     */
    public void initScene() {

        // non optional styling
        nonOptionalVbox.setSpacing(10);
        nonOptionalVbox.setPadding(new Insets(20, 20, 20, 20));
        nonOptionalVbox.setAlignment(Pos.TOP_LEFT);

        // optional styling
        optionalVbox.setSpacing(10);
        optionalVbox.setPadding(new Insets(20, 20, 20, 20));
        optionalVbox.setAlignment(Pos.TOP_RIGHT);

        // buttons actions
        Button okButton = NewButton("添加订单");
        okButton.setOnAction(e -> createOrder());
        Button clearButton = NewButton("清空");
        GridPane.setHalignment(clearButton, HPos.LEFT);
        clearButton.setOnAction(e -> clearFields());

        // HBox styling that contains both buttons
        buttonHBox.setPadding(new Insets(10, 10, 10, 10));
        buttonHBox.setSpacing(10);
        buttonHBox.getChildren().addAll(clearButton, okButton);

        initNonOptional();
        initOptional();

        // info enter styling
        infoEnterHBox.getChildren().setAll(regionOne, nonOptionalVbox, optionalVbox, regionTwo);
        infoEnterHBox.setSpacing(10);
//        infoEnterHBox.setBorder(new Border(new BorderStroke(Color.BLACK,
//                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1.5),
//                new Insets(0, 10, 10, 10))));

        // main VBox styling
        addOrderAll.getChildren().setAll(infoEnterHBox);
//        addOrderAll.setPadding(new Insets(10, 10, 10, 10));
        addOrderAll.setSpacing(10);
    }

    /**
     * initialize all components in optional grid
     */
    private void initOptional() {

        optionalGridPane.setPadding(new Insets(10, 10, 10, 10));
        optionalGridPane.setVgap(8);
        optionalGridPane.setHgap(10);

        Label rightHeader = new Label("选填内容");
        rightHeader.setMaxWidth(Double.MAX_VALUE);
        rightHeader.setAlignment(Pos.CENTER);
        rightHeader.setStyle(headerStyle);

        int row = 0;

        Label unitPriceLabel = NewLabel("单价:", 0, row);
        unitPriceInput = NewTextField("输入单价", 1, row++);

        Label paymentDateLabel = NewLabel("付款日期:", 0, row);
        paymentDatePicker = NewDatePicker(1, row++);

        Label arrivalDateLabel = NewLabel("到货日期:", 0, row);
        arrivalDatePicker = NewDatePicker(1, row++);

        Label invoiceDateLabel = NewLabel("发票日期:", 0, row);
        invoiceDatePicker = NewDatePicker(1, row++);

        Label invoiceNumLabel = NewLabel("发票编号:", 0, row);
        invoiceNumInput = NewTextField("输入发票编号", 1, row++);

        Label signedByLabel = NewLabel("签收人:", 0, row);
        signedByInput = NewTextField("输入签收人姓名", 1, row++);

        Label skuBySeller = NewLabel("供应商订单编号:", 0, row);
        skuBySellerInput = NewTextField("输入供应商订单编号", 1, row++);

        Label noteLabel = NewLabel("备注:", 0, row);
        noteInput = NewTextField("备注", 1, row++);

        optionalGridPane.getChildren().addAll(unitPriceLabel, unitPriceInput,
                paymentDateLabel, paymentDatePicker, arrivalDateLabel, arrivalDatePicker,
                invoiceDateLabel, invoiceDatePicker, invoiceNumLabel, invoiceNumInput,
                signedByLabel, signedByInput, skuBySeller, skuBySellerInput,
                noteLabel, noteInput);
        optionalVbox.getChildren().setAll(rightHeader, optionalGridPane, buttonHBox);
        buttonHBox.setAlignment(Pos.BASELINE_RIGHT);
    }

    /**
     * initialize all components in non optional grid
     */
    private void initNonOptional() {

        nonOptionalGridPane.setPadding(new Insets(10, 10, 10, 10));
        nonOptionalGridPane.setVgap(8);
        nonOptionalGridPane.setHgap(10);

        Label leftHeader = new Label("必填内容");
        leftHeader.setMaxWidth(Double.MAX_VALUE);
        leftHeader.setAlignment(Pos.CENTER);
        leftHeader.setStyle(headerStyle);

        int row = 0;

        Label skuLabel = NewLabel("订单编号：",0, row);
        skuInput = NewTextField("输入订单编号",1, row++);

        Label matNameLabel = NewLabel("原料名称:", 0, row);
        matNameInput = NewTextField("输入原料名称", 1, row++);

        Label matTypeLabel = NewLabel("原料品种:", 0, row);
        matTypeComboBox = NewCombo(matOfType, 1, row++);

        Label orderDateLabel = NewLabel("订单日期:", 0, row);
        orderDatePicker = NewDatePicker(1, row++);
        orderDatePicker.setValue(LocalDate.now());

        Label unitAmountLabel = NewLabel("规格:", 0, row);
        unitAmountInput = NewTextField("输入规格", 1, row++);

        Label amountLabel = NewLabel("数量:", 0, row);
        amountInput = NewTextField("输入数量", 1, row++);

        Label sellerLabel = NewLabel("选择供应商:", 0, row);
        sellerComboBox = NewCombo(getSellerArray(), 1, row++);

        nonOptionalGridPane.getChildren().setAll(skuLabel, skuInput,
                matNameLabel, matNameInput, matTypeLabel, matTypeComboBox,
                orderDateLabel, orderDatePicker, unitAmountLabel, unitAmountInput, amountLabel, amountInput,
                sellerLabel, sellerComboBox);

        nonOptionalVbox.getChildren().setAll(leftHeader, nonOptionalGridPane);
    }

    /**
     * Clear all element where user can input
     */
    private void clearFields() {
        for (Node element: nonOptionalGridPane.getChildren()) {
            if (element instanceof TextField) {
                if (element.equals(skuInput)) {
                    continue;
                }
                ((TextField) element).clear();
            }
            else if (element instanceof ComboBox) {
                ((ComboBox) element).getSelectionModel().clearSelection();
            }
            else if (element instanceof DatePicker) {
                continue;
            }
        }
        for (Node element: optionalGridPane.getChildren()) {
            if (element instanceof TextField) {
                ((TextField) element).clear();
            }
            else if (element instanceof ComboBox) {
                ((ComboBox) element).getSelectionModel().clearSelection();
            }
            else if (element instanceof DatePicker) {
                ((DatePicker) element).setValue(null);
            }
        }
    }

    /**
     * return the vbox for the entire scene
     * @return the vbox for the entire scene
     */
    public VBox getPane() {
        return addOrderAll;
    }

    /**
     * get all seller's company name
     * @return an array of the company's name
     */
    private String[] getSellerArray() {
        sellerArrayList = new ArrayList<>();
        try {
            sellerArrayList = DatabaseUtil.GetAllSeller();
        }
        catch (SQLException e) {
            HandleError error = new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            error.WriteToLog();
        }
        String[] nameArray = new String[sellerArrayList.size()];

        // populating the array of sellers
        for (int i = 0; i < sellerArrayList.size(); i++) {
            nameArray[i] = sellerArrayList.get(i).getCompanyName();
        }
        return nameArray;
    }

    /**
     * get all values, and insert into database
     */
    private void createOrder() {
        // sku
        String sku = skuInput.getText();
        if (sku.equals("")) {
            AlertBox.display("错误", "没有输入订单编号");
            return;
        }

        // mat name
        String nameOfMat = matNameInput.getText();
        if (nameOfMat.equals("")) {
            AlertBox.display("错误", "没有输入原料名称");
            return;
        }

        // mat type
        String typeOfMat = null;
        try {
            typeOfMat = matTypeComboBox.getValue().toString();
            if (typeOfMat.equals("")) {
                throw new NullPointerException();
            }
        }
        catch (NullPointerException e) {
            AlertBox.display("错误", "没有选择原料品种");
            return;
        }

        // order date day
        Date orderDate = null;
        try {
            String orderDateDay = orderDatePicker.getValue().toString();
            if (orderDateDay.equals("")) {
                throw new NullPointerException();
            }
            orderDate = new Date(orderDatePicker.getValue().getYear(), orderDatePicker.getValue().getMonthValue(),
                    orderDatePicker.getValue().getDayOfMonth());
        }
        catch (NullPointerException e) {
            AlertBox.display("错误", "没有选择订单日期");
            return;
        }

        // unit amount
        double unitAmount = 0;
        String unitAmountText = unitAmountInput.getText();
        if (!unitAmountText.equals("")) {
            try {
                unitAmount = Double.parseDouble(unitAmountText);
            } catch (Exception e) {
                AlertBox.display("错误", "规格输入错误");
                return;
            }
        }
        else {
            AlertBox.display("错误", "规格输入错误");
            return;
        }

        // amount
        double amount = 0;
        String amountText = amountInput.getText();
        if (!amountText.equals("")) {
            try {
                amount = Double.parseDouble(amountText);
            } catch (Exception e) {
                AlertBox.display("错误", "数量输入错误");
                return;
            }
        }
        else {
            AlertBox.display("错误", "数量输入错误");
            return;
        }

        // seller
        Seller sellerSelected = null;
        try {
            sellerComboBox.getValue().toString();
            for (Seller seller : sellerArrayList) {
                if (seller.getCompanyName().equals(sellerComboBox.getValue().toString()))
                    sellerSelected = seller;
            }
        }
        catch (NullPointerException e) {
            AlertBox.display("错误", "没有选择供应商");
            return;
        }

        order = new Order(SerialNum.getSerialNum(), sku, nameOfMat, typeOfMat, orderDate, sellerSelected, unitAmount, amount);

        if (paymentDatePicker.getValue() != null) {
            System.out.println("payment not null");
            order.setPaymentDate(new Date(paymentDatePicker.getValue().getYear(), paymentDatePicker.getValue().getMonthValue(),
                    paymentDatePicker.getValue().getDayOfMonth()));
        }
        else {
            order.setPaymentDate(new Date(0, 0, 0));
        }

        if (arrivalDatePicker.getValue() != null) {
            order.setArrivalDate(new Date(arrivalDatePicker.getValue().getYear(), arrivalDatePicker.getValue().getMonthValue(),
                    arrivalDatePicker.getValue().getDayOfMonth()));
        }
        else {
            order.setArrivalDate(new Date(0, 0, 0));
        }

        if (invoiceDatePicker.getValue() != null) {
            order.setInvoiceDate(new Date(invoiceDatePicker.getValue().getYear(), invoiceDatePicker.getValue().getMonthValue(),
                    invoiceDatePicker.getValue().getDayOfMonth()));
        }
        else {
            order.setInvoiceDate(new Date(0, 0, 0));
        }

        if (!invoiceNumInput.getText().equals("")) {
            System.out.println("invoice not null");
            order.setInvoice(invoiceNumInput.getText());
        }
        if (!signedByInput.getText().equals("")) {
            order.setSigned(signedByInput.getText());
        }
        if (!noteInput.getText().equals("")) {
            order.setNote(noteInput.getText());
        }
        if (!skuBySellerInput.getText().equals("")) {
            order.setSkuSeller(skuBySellerInput.getText());
        }
        if (!unitPriceInput.getText().equals("")) {
            try {
                order.setUnitPrice(Double.parseDouble(unitPriceInput.getText()));
            } catch (IllegalAccessException ignored) {}

            catch (Exception e) {
                AlertBox.display("错误", "单价输入格式错误");
                return;
            }
        }

        clearFields();

        try {
            DatabaseUtil.InsertToMatManagement(order);
            AlertBox.display("成功", "添加成功");
        } catch (SQLException e) {
            HandleError error = new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            error.WriteToLog();
        }
    }

    /**
     * Create a button with specified text
     * @param text text on the button
     * @return javaFx Button
     */
    private Button NewButton(String text) {
        Button returnButton = new Button(text);
        returnButton.setStyle(standardFont);
        returnButton.setMaxWidth(Double.MAX_VALUE);
        return returnButton;
    }

    /**
     * create a label in the gridpane
     * @param text text on the label
     * @param col col in gridpane
     * @param row row in gridpane
     * @return the label
     */
    private Label NewLabel(String text, int col, int row) {
        Label returnLabel = new Label(text);
        returnLabel.setStyle(standardFont);
        GridPane.setConstraints(returnLabel, col, row);
        GridPane.setHalignment(returnLabel, HPos.RIGHT);
        return returnLabel;
    }

    /**
     * create a textfield in the gridpane
     * @param text text in the textfield
     * @param col col in gridpane
     * @param row row in gridpane
     * @return the textfield
     */
    private TextField NewTextField(String text, int col, int row) {
        TextField returnTextField = new TextField();
        returnTextField.setPromptText(text);
        returnTextField.setStyle(standardFont);
        GridPane.setConstraints(returnTextField, col, row);
        returnTextField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                createOrder();
            }
        });
        return returnTextField;
    }

    /**
     * create date picker
     * @param col col in gridpane
     * @param row row in gridpane
     * @return the date picker
     */
    private DatePicker NewDatePicker(int col, int row) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

        DatePicker returnDatePicker = new DatePicker();
        returnDatePicker.setStyle(standardFont);

        returnDatePicker.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                createOrder();
            }
        });

        returnDatePicker.setConverter(new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate localDate) {
                if (localDate==null) {
                    return "没有选择";
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
        GridPane.setConstraints(returnDatePicker, col, row);
        return returnDatePicker;
    }

    /**
     * create new combo box
     * @param text selection in the combobox
     * @param col col in gridpane
     * @param row row in gridpane
     * @return the combo box
     */
    private ComboBox NewCombo(String[] text, int col, int row) {
        ComboBox returnComboBox = new ComboBox();
        returnComboBox.getItems().addAll(text);
        returnComboBox.setStyle(standardFont);
        returnComboBox.setMaxWidth(Double.MAX_VALUE);
        GridPane.setConstraints(returnComboBox, col, row);
        returnComboBox.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                createOrder();
            }
        });
        return returnComboBox;
    }
}
