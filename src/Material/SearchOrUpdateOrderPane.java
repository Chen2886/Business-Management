package Material;

import Main.DatabaseUtil;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.StringConverter;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;

public class SearchOrUpdateOrderPane {

	private static final String[] matOfType = new String[]{"RD", "S", "P", "A", "R", "Pa"};
	private static final String headerStyle = "-fx-border-style: solid;\n" +
			"-fx-border-width: 0 0 1 0;\n" +
			"-fx-border-color: black;\n" +
			"-fx-font: 24 arial;";
	private static final String standardFont = "-fx-font: 16 arial;";

	// main scene component
	private VBox searchOrderAll;
	private HBox infoEnterHBox;
	private HBox buttonHBox;
	private GridPane orderGridPane;

	private Region regionOne;
	private Region regionTwo;

	private Button searchButton;
	private Button clearButton;

	// left
	private TextField skuInput;
	private TextField matNameInput;
	private TextField unitAmountInput;
	private TextField amountInput;
	private TextField unitPriceInput;
	private ComboBox matTypeComboBox;
	private ComboBox sellerComboBox;
	private DatePicker startDate;
	private DatePicker endDate;

	//right
	private DatePicker paymentDatePicker;
	private DatePicker arrivalDatePicker;
	private DatePicker invoiceDatePicker;
	private TextField invoiceNumInput;
	private TextField signedByInput;
	private TextField skuBySellerInput;
	private TextField noteInput;

	// order
	private ArrayList<Seller> sellerArrayList;
	private Order selectedOrder;

	public SearchOrUpdateOrderPane() {
		regionOne = new Region();
		HBox.setHgrow(regionOne, Priority.ALWAYS);
		regionTwo = new Region();
		HBox.setHgrow(regionTwo, Priority.ALWAYS);

		orderGridPane = new GridPane();
		buttonHBox = new HBox();
	}

	public void initScene() {

		// search Button
		searchButton = NewButton("搜索订单");

		// clear Button
		clearButton = NewButton("清空");
		GridPane.setHalignment(clearButton, HPos.LEFT);
		clearButton.setOnAction(e -> clearFields());

		// button HBox styling
		buttonHBox.setPadding(new Insets(10, 10, 10, 10));
		buttonHBox.setSpacing(10);
		buttonHBox.getChildren().addAll(clearButton, searchButton);
		buttonHBox.setAlignment(Pos.BOTTOM_RIGHT);
		GridPane.setConstraints(buttonHBox, 7, 3);

		initGrid();

		VBox orderGridPaneAndButtonVBox = new VBox(orderGridPane);

		infoEnterHBox = new HBox(regionOne, orderGridPaneAndButtonVBox, regionTwo);
		infoEnterHBox.setSpacing(10);

		searchOrderAll = new VBox(infoEnterHBox);
		searchOrderAll.setSpacing(10);
	}

	private void initGrid() {
		String[] year = new String[20];
		int counter = 0;
		for (int i = Calendar.getInstance().get(Calendar.YEAR) - 10; i < Calendar.getInstance().get(Calendar.YEAR) + 10; i++) {
			year[counter++] = String.valueOf(i);
		}

		String[] day = new String[31];
		for (int i = 1; i <= 31; i++) {
			day[i - 1] = String.valueOf(i);
		}

		String[] month = new String[12];
		for (int i = 1; i <= 12; i++) {
			month[i - 1] = String.valueOf(i);
		}

		orderGridPane.setPadding(new Insets(10, 10, 10, 10));
		orderGridPane.setVgap(8);
		orderGridPane.setHgap(10);

		int row = 0;

		Label skuLabel = NewLabel("订单编号:", 0,row);
		skuInput = NewTextField("输入订单编号", 1, row++);

		Label matNameLabel = NewLabel("原料名称:", 0, row);
		matNameInput = NewTextField("输入原料名称", 1, row++);

		Label matTypeLabel = NewLabel("原料品种:", 0, row);
		matTypeComboBox = NewCombo(matOfType, 1, row++);

		Label startLabel = NewLabel("输入开始日期", 0, row);
		startDate = NewDatePicker(1, row++);
		startDate.setValue(null);

		Label endLabel = NewLabel("输入结束日期", 0, row);
		endDate = NewDatePicker(1, row);
		endDate.setValue(null);

		row = 0;

		Label unitAmountLabel = NewLabel("规格:", 2, row);
		unitAmountInput = NewTextField("输入规格", 3, row++);

		Label amountLabel = NewLabel("数量:", 2, row);
		amountInput = NewTextField("输入数量", 3, row++);

		Label unitPriceLabel = NewLabel("单价:", 2, row);
		unitPriceInput = NewTextField("输入单价", 3, row++);

		Label sellerLabel = NewLabel("选择供应商:", 2, row);
		sellerComboBox = NewCombo(getSellerArray(), 3, row);

		row = 0;

		Label paymentDateLabel = NewLabel("付款日期:", 4, row);
		paymentDatePicker = NewDatePicker(5, row++);

		Label arrivalDateLabel = NewLabel("到货日期:", 4, row);
		arrivalDatePicker = NewDatePicker(5, row++);

		Label invoiceDateLabel = NewLabel("发票日期:", 4, row);
		invoiceDatePicker = NewDatePicker(5, row++);

		Label invoiceNumLabel = NewLabel("发票编号:", 4, row);
		invoiceNumInput = NewTextField("输入发票编号", 5, row++);

		row = 0;

		Label signedByLabel = NewLabel("签收人:", 6, row);
		signedByInput = NewTextField("输入签收人姓名", 7, row++);

		Label skuBySeller = NewLabel("供应商订单编号:", 6, row);
		skuBySellerInput = NewTextField("输入供应商订单编号", 7, row++);

		Label noteLabel = NewLabel("备注:", 6, row);
		noteInput = NewTextField("备注", 7, row++);

		orderGridPane.getChildren().setAll(skuLabel, skuInput,
				matNameLabel, matNameInput, matTypeLabel, matTypeComboBox,
				startLabel, startDate, endLabel, endDate, unitAmountLabel, unitAmountInput, amountLabel, amountInput,
				unitPriceLabel, unitPriceInput, sellerLabel, sellerComboBox, paymentDateLabel, paymentDatePicker,
				arrivalDateLabel, arrivalDatePicker, invoiceDateLabel, invoiceDatePicker,
				invoiceNumLabel, invoiceNumInput, signedByLabel, signedByInput, skuBySeller, skuBySellerInput,
				noteLabel, noteInput, buttonHBox);
	}

	/**
	 * Clear all element where user can input
	 */
	public void clearFields() {
		for (Node element: orderGridPane.getChildren()) {
			if (element instanceof TextField) {
				((TextField) element).clear();
			}
			else if (element instanceof ComboBox) {
				((ComboBox) element).getSelectionModel().clearSelection();
			}
			else if (element instanceof DatePicker) {
				if (element.equals(startDate) || element.equals(endDate)) {
					((DatePicker) element).setValue(null);
				}
				else {
					((DatePicker) element).setValue(null);
				}
			}
		}
	}

	public VBox getPane() {
		return searchOrderAll;
	}

	public GridPane getOrderGridPane() {
		return orderGridPane;
	}

	/**
	 * Get all the field values
	 * @return an SQL command
	 */
	public String GetFieldVal() {
		String returnVal = "WHERE ";
		ArrayList<String> SQLCommand = new ArrayList<>();
		
		// seller
		try {
			sellerComboBox.getValue().toString();
			for (Seller seller : sellerArrayList) {
				if (seller.getCompanyName().equals(sellerComboBox.getValue().toString()))
					SQLCommand.add(String.format("%s = '%d'", "sellerId", seller.getSellerId()));
			}
		}
		catch (NullPointerException ignored) {
		}

		// name
		String nameOfMat = matNameInput.getText();
		if (!nameOfMat.equals("")) {
			SQLCommand.add(String.format("%s = '%s'", "name", nameOfMat));
		}

		// type of mat
		try {
			String typeOfMat = matTypeComboBox.getValue().toString();
			if (!typeOfMat.equals("")) {
				SQLCommand.add(String.format("%s = '%s'", "type", typeOfMat));
			}
		}
		catch (NullPointerException ignored) {
		}

		// order date year
		try {
			if (startDate.getValue() == null && endDate.getValue() != null) {
				AlertBox.display("错误","没有输入结束日期");
				throw new NullPointerException();
			}
			else if (startDate.getValue() != null && endDate.getValue() == null) {
				AlertBox.display("错误","没有输入开始日期");
				throw new NullPointerException();
			}
			else if (startDate.getValue() == null && endDate.getValue() == null) {
				throw new NullPointerException();
			}
			else if (startDate.getValue() != null && endDate.getValue() != null) {
				if (startDate.getValue().getYear() > endDate.getValue().getYear()) {
					AlertBox.display("错误", "开始日期小于结束日期");
					return null;
				} else if (startDate.getValue().getYear() == endDate.getValue().getYear()) {
					if (startDate.getValue().getMonthValue() > endDate.getValue().getMonthValue()) {
						AlertBox.display("错误", "开始日期小于结束日期");
						return null;
					} else if (startDate.getValue().getMonthValue() == endDate.getValue().getMonthValue()) {
						// month okay
						if (startDate.getValue().getDayOfMonth() > endDate.getValue().getDayOfMonth()) {
							AlertBox.display("错误", "开始日期小于结束日期");
							return null;
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
		catch (NullPointerException ignored) {
		}

		// unitAmount
		String unitAmount = unitAmountInput.getText();
		if (!unitAmount.equals("")) {
			try {
				Double.parseDouble(unitAmount);
			} catch (Exception e) {
				AlertBox.display("错误", "规格输入错误");
				return null;
			}
			SQLCommand.add(String.format("%s = '%s'", "unitAmount", unitAmount));
		}

		// amount
		String amount = amountInput.getText();
		if (!amount.equals("")) {
			try {
				Double.parseDouble(amount);
			} catch (Exception e) {
				AlertBox.display("错误", "数量输入错误");
				return null;
			}
			SQLCommand.add(String.format("%s = '%s'", "amount", amount));
		}

		// unitPrice
		String unitPrice = unitPriceInput.getText();
		if (!unitPrice.equals("")) {
			try {
				Double.parseDouble(unitPrice);
			} catch (Exception e) {
				AlertBox.display("错误", "单价输入错误");
				return null;
			}
			SQLCommand.add(String.format("%s = '%s'", "unitPrice", unitPrice));
		}

		// paymentDate
		if (paymentDatePicker.getValue() != null) {
			SQLCommand.add(String.format("%s = '%s'", "paymentDate", new Date(paymentDatePicker.getValue().getYear(), paymentDatePicker.getValue().getMonthValue(),
					paymentDatePicker.getValue().getDayOfMonth()).toString()));
		}

		// arrivalDate
		if (arrivalDatePicker.getValue() != null) {
			SQLCommand.add(String.format("%s = '%s'", "arrivalDate", new Date(arrivalDatePicker.getValue().getYear(), arrivalDatePicker.getValue().getMonthValue(),
					arrivalDatePicker.getValue().getDayOfMonth()).toString()));
		}

		// invoiceDate
		if (invoiceDatePicker.getValue() != null) {
			SQLCommand.add(String.format("%s = '%s'", "invoiceDate", new Date(invoiceDatePicker.getValue().getYear(), invoiceDatePicker.getValue().getMonthValue(),
					invoiceDatePicker.getValue().getDayOfMonth()).toString()));
		}

		// invoiceNum
		if (!invoiceNumInput.getText().equals("")) {
			SQLCommand.add(String.format("%s = '%s'", "invoice", invoiceNumInput.getText()));
		}

		// signedBy
		if (!signedByInput.getText().equals("")) {
			SQLCommand.add(String.format("%s = '%s'", "signed", signedByInput.getText()));
		}

		// noteInput
		if (!noteInput.getText().equals("")) {
			SQLCommand.add(String.format("%s = '%s'", "note", noteInput.getText()));
		}

		// skuBySeller
		if (!skuBySellerInput.getText().equals("")) {
			SQLCommand.add(String.format("%s = '%s'", "skuSeller", skuBySellerInput.getText()));
		}

		if (SQLCommand.size() == 0) {
			return "";
		}
		else {
			for (int i = 0; i < SQLCommand.size(); i++) {
				if (i == SQLCommand.size() - 1) {
					returnVal += SQLCommand.get(i);
				}
				else {
					returnVal += SQLCommand.get(i) + " AND ";
				}
			}
		}

		System.out.println(returnVal);
		return returnVal;
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
		return returnComboBox;
	}

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

	public Button getSearchButton() {
		return searchButton;
	}
}