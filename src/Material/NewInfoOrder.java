package Material;

import Main.DatabaseUtil;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class NewInfoOrder {

	private static final String[] matOfType = new String[]{"RD", "S", "P", "A", "R", "Pa"};
	private static final String headerStyle = "-fx-border-style: solid;\n" +
			"-fx-border-width: 0 0 1 0;\n" +
			"-fx-border-color: black;\n" +
			"-fx-font: 24 arial;";
	private static final String smallFont = "-fx-font: 16 arial;";
	private final static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

	//Create variable
	Order order;
	int serialNum;
	private ArrayList<Seller> sellerArrayList;

	// main scene component
	private VBox mainVBox;
	private HBox infoEnterHBox;
	private VBox leftVBox;
	private VBox rightVBox;
	private HBox buttonHBox;
	private GridPane leftGridPane;
	private GridPane rightGridPane;

	private Region leftRegion;
	private Region rightRegion;

	// left grid
	private TextField skuInput;
	private TextField matNameInput;
	private TextField unitAmountInput;
	private TextField amountInput;
	private TextField unitPriceInput;
	private ComboBox matTypeComboBox;
	private ComboBox sellerComboBox;
	private DatePicker orderDatePicker;

	// right grid
	private DatePicker paymentDatePicker;
	private DatePicker arrivalDatePicker;
	private DatePicker invoiceDatePicker;
	private TextField invoiceNumInput;
	private TextField signedByInput;
	private TextField skuBySellerInput;
	private TextField noteInput;

	public NewInfoOrder() {
		leftRegion = new Region();
		HBox.setHgrow(leftRegion, Priority.ALWAYS);
		rightRegion = new Region();
		HBox.setHgrow(rightRegion, Priority.ALWAYS);
		mainVBox = new VBox();
		infoEnterHBox = new HBox();
		leftGridPane = new GridPane();
		rightGridPane = new GridPane();
		leftVBox = new VBox();
		rightVBox = new VBox();
	}

	public Order display(Order selectedOrder) {
		serialNum = selectedOrder.getSerialNum();
		Stage window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle("填写新的信息");

		//Create two buttons
		Button yesButton = NewButton("确定");
		Button noButton = NewButton("取消");

		buttonHBox = new HBox(yesButton, noButton);
		buttonHBox.setSpacing(10);
		buttonHBox.setAlignment(Pos.CENTER_RIGHT);
		buttonHBox.setPadding(new Insets(10, 10, 10, 10));

		//Clicking will set answer and close window
		yesButton.setOnAction(e -> {
			GetFieldVal();
			window.close();
		});
		noButton.setOnAction(e -> {
			order = null;
			window.close();
			return;
		});

		initLeftGrid(selectedOrder);
		initRightGrid(selectedOrder);

		leftVBox.setSpacing(10);
		leftVBox.setPadding(new Insets(20, 20, 20, 20));
		leftVBox.setAlignment(Pos.TOP_LEFT);
		rightVBox.setSpacing(10);
		rightVBox.setPadding(new Insets(20, 20, 20, 20));
		rightVBox.setAlignment(Pos.TOP_RIGHT);

		infoEnterHBox = new HBox(leftRegion, leftVBox, rightVBox, rightRegion);
		infoEnterHBox.setSpacing(10);

		mainVBox = new VBox(infoEnterHBox);
		mainVBox.setSpacing(10);

		window.setMinWidth((int) (screenSize.width * 0.6));
		window.setMinHeight((int) (screenSize.height * 0.6));
		Scene scene = new Scene(mainVBox);
		window.setScene(scene);
		window.showAndWait();

		//Make sure to return answer
		return order;
	}

	private void initRightGrid(Order order) {
		rightGridPane.setPadding(new Insets(10, 10, 10, 10));
		rightGridPane.setVgap(8);
		rightGridPane.setHgap(10);

		Label rightHeader = new Label("选填内容");
		rightHeader.setMaxWidth(Double.MAX_VALUE);
		rightHeader.setAlignment(Pos.CENTER);
		rightHeader.setStyle(headerStyle);

		int row = 0;

		Label unitPriceLabel = NewLabel("单价:", 0, row);
		unitPriceInput = NewTextField(String.valueOf(order.getUnitPrice()), 1, row++);

		Label paymentDateLabel = NewLabel("付款日期:", 0, row);
		paymentDatePicker = NewGetDate(1, row++);
		if (order.getPaymentDate() != null && !order.getPaymentDate().equals(new Date(0, 0, 0)))
			paymentDatePicker.setValue(LocalDate.of(order.getPaymentDate().getYear(), order.getPaymentDate().getMonth(),
					order.getPaymentDate().getDay()));

		Label arrivalDateLabel = NewLabel("到货日期:", 0, row);
		arrivalDatePicker = NewGetDate(1, row++);
		if (order.getArrivalDate() != null && !order.getArrivalDate().equals(new Date(0, 0, 0)))
		arrivalDatePicker.setValue(LocalDate.of(order.getArrivalDate().getYear(), order.getArrivalDate().getMonth(),
				order.getArrivalDate().getDay()));

		Label invoiceDateLabel = NewLabel("发票日期:", 0, row);
		invoiceDatePicker = NewGetDate(1, row++);
		if (order.getInvoiceDate() != null && !order.getInvoiceDate().equals(new Date(0, 0, 0)))
		invoiceDatePicker.setValue(LocalDate.of(order.getInvoiceDate().getYear(), order.getInvoiceDate().getMonth(),
				order.getInvoiceDate().getDay()));

		Label invoiceNumLabel = NewLabel("发票编号:", 0, row);
		invoiceNumInput = NewTextField(order.getInvoice(), 1, row++);

		Label signedByLabel = NewLabel("签收人:", 0, row);
		signedByInput = NewTextField(order.getSigned(), 1, row++);

		Label skuBySeller = NewLabel("供应商订单编号:", 0, row);
		skuBySellerInput = NewTextField(order.getSkuSeller(), 1, row++);

		Label noteLabel = NewLabel("备注:", 0, row);
		noteInput = NewTextField(order.getNote(), 1, row);

		rightGridPane.getChildren().addAll(unitPriceLabel, unitPriceInput,
				paymentDateLabel, paymentDatePicker, arrivalDateLabel, arrivalDatePicker,
				invoiceDateLabel, invoiceDatePicker, invoiceNumLabel, invoiceNumInput,
				signedByLabel, signedByInput, skuBySeller, skuBySellerInput,
				noteLabel, noteInput);
		rightVBox.getChildren().setAll(rightHeader, rightGridPane, buttonHBox);
		buttonHBox.setAlignment(Pos.BASELINE_RIGHT);
	}

	private void initLeftGrid(Order order) {
		leftGridPane.setPadding(new Insets(10, 10, 10, 10));
		leftGridPane.setVgap(8);
		leftGridPane.setHgap(10);

		Label leftHeader = new Label("必填内容");
		leftHeader.setMaxWidth(Double.MAX_VALUE);
		leftHeader.setAlignment(Pos.CENTER);
		leftHeader.setStyle(headerStyle);

		int row = 0;

		Label skuLabel = NewLabel("订单编号:", 0, row);
		skuInput = NewTextField(order.getSku(), 1, row++);

		Label matNameLabel = NewLabel("原料名称:", 0, row);
		matNameInput = NewTextField(order.getName(), 1, row++);

		Label matTypeLabel = NewLabel("原料品种:", 0, row);
		matTypeComboBox = NewCombo(order.getType(), matOfType, 1, row++);

		Label orderDateLabel = NewLabel("订单日期:", 0, row);
		orderDatePicker = NewGetDate(1, row++);
		orderDatePicker.setValue(LocalDate.of(order.getOrderDate().getYear(), order.getOrderDate().getMonth(),
				order.getOrderDate().getDay()));

		Label unitAmountLabel = NewLabel("规格:", 0, row);
		unitAmountInput = NewTextField(String.valueOf(order.getUnitAmount()), 1, row++);

		Label amountLabel = NewLabel("数量:", 0, row);
		amountInput = NewTextField(String.valueOf(order.getAmount()), 1, row++);



		Label sellerLabel = NewLabel("选择供应商:", 0, row);
		sellerComboBox = NewCombo(order.getSeller(), getSellerArray(), 1, row++);

		leftGridPane.getChildren().setAll(skuLabel, skuInput, matNameLabel, matNameInput, matTypeLabel, matTypeComboBox,
				orderDateLabel, orderDatePicker, unitAmountLabel, unitAmountInput, amountLabel, amountInput,
				sellerLabel, sellerComboBox);

		leftVBox.getChildren().setAll(leftHeader, leftGridPane);
	}

	private void GetFieldVal() {
		order = null;

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

		order = new Order(serialNum, sku, nameOfMat, typeOfMat, orderDate, sellerSelected, unitAmount, amount);

		if (!unitPriceInput.getText().equals("")) {
			try {
				order.setUnitPrice(Double.parseDouble(unitPriceInput.getText()));
			} catch (IllegalAccessException ignored) {
			} catch (Exception e) {
				AlertBox.display("错误", "单价输入错误");
			}
		}

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

		System.out.println(invoiceNumInput.getText());
		if (invoiceNumInput.getText() != null && !invoiceNumInput.getText().equals("")) {
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
	}

	private ComboBox NewCombo(String currentMat, String[] text, int col, int row) {
		ComboBox returnComboBox = new ComboBox();
		returnComboBox.setStyle(smallFont);
		returnComboBox.getItems().setAll(text);
		returnComboBox.setMaxWidth(Double.MAX_VALUE);
		GridPane.setConstraints(returnComboBox, col, row);
		returnComboBox.getSelectionModel().select(GetIndexOfMatType(currentMat));
		return returnComboBox;
	}

	private int GetIndexOfMatType(String type) {
		for (int i = 0; i < matOfType.length; i++) {
			if (matOfType[i].equals(type)) {
				return i;
			}
		}
		return -1;
	}

	private ComboBox NewCombo(Seller currentSeller, String[] text, int col, int row) {
		ComboBox returnComboBox = new ComboBox();
		returnComboBox.setStyle(smallFont);
		returnComboBox.setMaxWidth(Double.MAX_VALUE);
		GridPane.setConstraints(returnComboBox, col, row);
		boolean exists = false;
		for (Seller seller : sellerArrayList) {
			if (currentSeller.equals(seller)) {
				exists = true;
				break;
			}
		}
		if (!exists) {
			try {
				DatabaseUtil.InsertToSeller("seller", currentSeller);
				sellerArrayList.add(currentSeller);
				String[] updatedList = new String[sellerArrayList.size()];
				for (int i = 0; i < updatedList.length; i++) {
					updatedList[i] = sellerArrayList.get(i).getCompanyName();
				}
				returnComboBox.getItems().addAll(updatedList);
			} catch (SQLException e) {
				HandleError error = new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
						e.getMessage(), e.getStackTrace(), false);
				error.WriteToLog();
			}
		}
		else {
			returnComboBox.getItems().addAll(text);
		}
		returnComboBox.getSelectionModel().select(sellerArrayList.indexOf(currentSeller));
		return returnComboBox;
	}

	private Label NewLabel(String text, int col, int row) {
		Label returnLabel = new Label(text);
		returnLabel.setStyle(smallFont);
		leftGridPane.setConstraints(returnLabel, col, row);
		leftGridPane.setHalignment(returnLabel, HPos.RIGHT);
		return returnLabel;
	}

	private TextField NewTextField(String text, int col, int row) {
		TextField returnTextField = new TextField();
		returnTextField.setText(text);
		returnTextField.setStyle(smallFont);
		GridPane.setConstraints(returnTextField, col, row);
		return returnTextField;
	}

	private Button NewButton(String text) {
		Button returnButton = new Button(text);
		returnButton.setStyle(smallFont);
		returnButton.setMaxWidth(Double.MAX_VALUE);
		return returnButton;
	}

	private DatePicker NewGetDate(int col, int row) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

		DatePicker returnDatePicker = new DatePicker();
		returnDatePicker.setStyle(smallFont);

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
}