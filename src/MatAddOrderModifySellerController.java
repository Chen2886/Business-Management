import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class MatAddOrderModifySellerController {

	// table headers
	private static final String[] tableHeaders = new String[] {"订单号", "原料名称", "类别", "订单日期", "付款日期",
			"到达日期", "发票日期", "发票编号", "规格", "数量", "单价", "签收人", "供应商订单编号", "备注", "供应商"};

	// all property listed
	private static final String[] propertyHeaders = new String[]{"sku", "name", "type", "orderDate", "paymentDate",
			"arrivalDate", "invoiceDate", "invoice", "unitAmount", "amount", "unitPrice", "signed", "skuSeller", "note", "seller"};

	// all seller property listed
	private static final String[] sellerPropertyHeaders = new String[]{"CompanyName", "ContactName", "Mobile", "LandLine", "Fax",
			"AccountNum", "BankAddress", "Address"};

	// seller table headers
	private static final String[] sellerTableHeaders = new String[]{"供应商",
			"联系人", "手机", "座机", "传真", "供应商账号", "供应商银行地址", "供应商地址"};

	// all types
	private static final String[] matOfType = new String[]{"RD", "S", "P", "A", "R", "PA"};

	// material section
	// add mat order
	@FXML GridPane matAddOrderGrid;
	@FXML Label matAddOrderTitleLabel;
	@FXML Button matCancelButton;
	@FXML Button matCompleteButton;
	@FXML Button matContinueButton;

	// add mat seller
	@FXML GridPane matAddSellerGrid;
	@FXML Label matAddSellerTitleLabel;
	@FXML Button matAddSellerCancelButton;
	@FXML Button matAddSellerCompleteButton;
	@FXML Button matAddSellerContinueButton;

	// edit mat seller
	@FXML GridPane matEditSellerGrid;
	@FXML Label matEditSellerTitleLabel;
	@FXML Button matEditSellerCancelButton;
	@FXML Button matEditSellerCompleteButton;
	@FXML ColumnConstraints problematicColumnOne;
	@FXML ColumnConstraints problematicColumnTwo;

	Stage currentStage;
	ObservableList<MatSeller> allMatSeller;
	ArrayList<Node> matOrderInputArray;
	ArrayList<TextField> matAddSellerInputArray;
	ArrayList<TextField> matEditSellerInputArray;

	/**
	 * Initialize all the element on the screen
	 */
	public void init() {
		matAddOrderTitleLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		matAddSellerTitleLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		matEditSellerTitleLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

		try {
			allMatSeller = DatabaseUtil.GetAllMatSellers();
		} catch (SQLException e) {
			allMatSeller = FXCollections.observableArrayList();
			e.printStackTrace();
			HandleError error = new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
					e.getMessage(), e.getStackTrace(), false);
			error.WriteToLog();
		}

		matCancelButton.setOnAction(actionEvent -> {
			if (ConfirmBox.display("确认", "确定取消？此订单不会被保存", "确认", "取消"))
				currentStage.close();
		});

		matAddSellerCancelButton.setOnAction(actionEvent -> {
			if (ConfirmBox.display("确认", "确定取消？此供应商不会被保存", "确认", "取消"))
				currentStage.close();
		});

		matCompleteButton.setOnAction(actionEvent -> {
			AddOrder();
		});

		matAddSellerCompleteButton.setOnAction(actionEvent -> {
			AddSeller();
		});

		matContinueButton.setOnAction(actionEvent -> {
			ContinueOrder();
		});

		matAddSellerContinueButton.setOnAction(actionEvent -> {
			ContinueSeller();
		});

		initAddMatOrder();
		initAddMatSeller();
		initEditMatSeller();
	}

	/**
	 * initialize all labels and text fields for add mat order grid
	 */
	private void initAddMatOrder() {
		int row = 1;
		int col = 0;

		// setting up all the labels
		ArrayList<Label> matOrderLabelArray = new ArrayList<>();
		for(int i = 0; i < tableHeaders.length; i++) {
			Label newLabel = new Label(tableHeaders[i]);
			newLabel.setStyle("-fx-font-size: 20px;" +
					"-fx-alignment: center-right;");

			newLabel.setMaxWidth(Double.MAX_VALUE);
			GridPane.setConstraints(newLabel, col, row++);
			matOrderLabelArray.add(newLabel);
			if ((i + 6) % 5 == 0) {
				row = 1;
				col += 2;
			}
		}

		row = 1;
		col = 1;
		// setting up all the text field
		matOrderInputArray = new ArrayList<>();
		for(int i = 0; i < propertyHeaders.length; i++) {

			// type of mat, combo box
			if (i == 2) {
				ComboBox<String> newComboBox = new ComboBox<>();
				newComboBox.getItems().setAll(matOfType);
				newComboBox.setMaxWidth(Double.MAX_VALUE);
				GridPane.setConstraints(newComboBox, col, row++);
				matOrderInputArray.add(newComboBox);
			}

			// seller, combo box
			else if (i == propertyHeaders.length - 1) {
				ComboBox<String> sellerComboBox = new ComboBox<>();

				String[] allSellerCompany = new String[allMatSeller.size()];
				for (int j = 0; j < allMatSeller.size(); j++) {
					allSellerCompany[j] = allMatSeller.get(j).getCompanyName();
				}

				sellerComboBox.getItems().setAll(allSellerCompany);
				sellerComboBox.setMaxWidth(Double.MAX_VALUE);
				GridPane.setConstraints(sellerComboBox, col, row++);
				matOrderInputArray.add(sellerComboBox);
			}

			// dates, date picker
			else if (i == 3 || i == 4 || i == 5 || i == 6) {
				DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
				DatePicker datePicker = new DatePicker();

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


				GridPane.setConstraints(datePicker, col, row++);
				matOrderInputArray.add(datePicker);
			}

			// regular text field
			else {
				TextField newTextField = new TextField();
				newTextField.setMaxWidth(Double.MAX_VALUE);
				newTextField.setPromptText("输入" + tableHeaders[i]);
				GridPane.setConstraints(newTextField, col, row++);
				matOrderInputArray.add(newTextField);

			}

			if ((i + 6) % 5 == 0) {
				row = 1;
				col += 2;
			}
		}

		// * setting up grid properties
		matAddOrderGrid.setVgap(10);
		matAddOrderGrid.setHgap(10);
		matAddOrderGrid.getChildren().addAll(matOrderLabelArray);
		matAddOrderGrid.getChildren().addAll(matOrderInputArray);
	}

	/**
	 * initialize all labels and text fields for add mat seller grid
	 */
	private void initAddMatSeller() {
		// * setting up seller labels
		// setting up all the labels
		ArrayList<Label> matSellerLabelArray = new ArrayList<>();
		matAddSellerInputArray = new ArrayList<>();
		int row = 1;
		int col = 0;
		for(int i = 0; i < sellerTableHeaders.length; i++) {
			Label newLabel = new Label(sellerTableHeaders[i]);
			newLabel.setStyle("-fx-font-size: 20px;");
			GridPane.setConstraints(newLabel, col, row++);
			matSellerLabelArray.add(newLabel);
			if ((i + 5) % 4 == 0) {
				row = 1;
				col += 2;
			}
		}

		row = 1;
		col = 1;
		// * setting up seller text field
		for (int i = 0; i <sellerTableHeaders.length; i++) {
			TextField newTextField = new TextField();
			newTextField.setMaxWidth(Double.MAX_VALUE);
			newTextField.setAlignment(Pos.CENTER_LEFT);
			newTextField.setPromptText("输入" + sellerTableHeaders[i]);
			GridPane.setConstraints(newTextField, col, row++);
			matAddSellerInputArray.add(newTextField);
			if ((i + 5) % 4 == 0) {
				row = 1;
				col += 2;
			}
		}

		// * setting up grid properties
		matAddSellerGrid.setVgap(10);
		matAddSellerGrid.setHgap(10);
		matAddSellerGrid.getChildren().addAll(matSellerLabelArray);
		matAddSellerGrid.getChildren().addAll(matAddSellerInputArray);
	}

	/**
	 * initialize all labels and text fields for edit mat order grid
	 */
	private void initEditMatSeller() {

		problematicColumnOne.setHgrow(Priority.ALWAYS);
		problematicColumnTwo.setHgrow(Priority.ALWAYS);

		// label
		Label initialSelectSellerTitle = new Label("选择供应商");
		initialSelectSellerTitle.setStyle("-fx-font-size: 20px;");
		initialSelectSellerTitle.setMaxWidth(Double.MAX_VALUE);
		initialSelectSellerTitle.setAlignment(Pos.CENTER_RIGHT);

		// seller combo box
		ComboBox<String> sellerComboBox = new ComboBox<>();
		String[] allSellerCompany = new String[allMatSeller.size()];
		for (int j = 0; j < allMatSeller.size(); j++) {
			allSellerCompany[j] = allMatSeller.get(j).getCompanyName();
		}
		sellerComboBox.getItems().setAll(allSellerCompany);

		HBox selectInitSeller = new HBox(initialSelectSellerTitle, sellerComboBox);
		selectInitSeller.setMaxWidth(Double.MAX_VALUE);
		selectInitSeller.setSpacing(10);
		selectInitSeller.setAlignment(Pos.CENTER);
		GridPane.setConstraints(selectInitSeller, 1, 1, 2, 1);

		Button startEdit = new Button("编辑供应商");
		startEdit.setStyle("-fx-background-color: #bbbdf6;\n" +
				"-fx-font-color: black;\n" +
				"-fx-font-size: 18px;");
		startEdit.setAlignment(Pos.CENTER);
		HBox uselessHBoxForButton = new HBox(startEdit);
		uselessHBoxForButton.setMaxWidth(Double.MAX_VALUE);
		uselessHBoxForButton.setAlignment(Pos.CENTER);
		GridPane.setConstraints(uselessHBoxForButton, 1, 2, 2, 1);

		startEdit.setOnAction(actionEvent -> {
			if (sellerComboBox.getSelectionModel().getSelectedItem() != null) {
				matEditSellerGrid.getChildren().removeAll(uselessHBoxForButton, selectInitSeller);
				updateMatSeller(allMatSeller.get(sellerComboBox.getSelectionModel().getSelectedIndex()));
			}
		});

		matEditSellerGrid.getChildren().addAll(selectInitSeller, uselessHBoxForButton);
	}

	private void updateMatSeller(MatSeller matSeller) {
		System.out.println(matSeller.toString(false));

		problematicColumnOne.setHgrow(Priority.NEVER);
		problematicColumnTwo.setHgrow(Priority.NEVER);

		// * setting up seller labels
		// setting up all the labels
		ArrayList<Label> matEditSellerLabelArray = new ArrayList<>();
		matEditSellerInputArray = new ArrayList<>();
		int row = 1;
		int col = 0;
		for(int i = 0; i < sellerTableHeaders.length; i++) {
			Label newLabel = new Label(sellerTableHeaders[i]);
			newLabel.setStyle("-fx-font-size: 20px;");
			GridPane.setConstraints(newLabel, col, row++);
			matEditSellerLabelArray.add(newLabel);
			if ((i + 5) % 4 == 0) {
				row = 1;
				col += 2;
			}
		}

		row = 1;
		col = 1;
		// * setting up seller text field
		for (int i = 0; i <sellerTableHeaders.length; i++) {
			TextField newTextField = new TextField();
			newTextField.setMaxWidth(Double.MAX_VALUE);
			newTextField.setAlignment(Pos.CENTER_LEFT);
			newTextField.setPromptText("输入" + sellerTableHeaders[i]);

			Method getters;
			try {
				getters = MatSeller.class.getDeclaredMethod("get" + sellerPropertyHeaders[i]);
				String value = (String) getters.invoke(matSeller);
				newTextField.setText(value);
			} catch (Exception e) {
				AlertBox.display("错误", "摘取信息错误");
				e.printStackTrace();
				HandleError error = new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
						e.getMessage(), e.getStackTrace(), false);
				error.WriteToLog();
			}

			GridPane.setConstraints(newTextField, col, row++);

			matEditSellerInputArray.add(newTextField);
			if ((i + 5) % 4 == 0) {
				row = 1;
				col += 2;
			}
		}

		matEditSellerCancelButton.setOnAction(actionEvent -> {
			matEditSellerGrid.getChildren().clear();
			matEditSellerGrid.getChildren().add(matEditSellerTitleLabel);
			initEditMatSeller();
		});

		matEditSellerCompleteButton.setOnAction(actionEvent -> {
			if (ConfirmBox.display("确认", "所有使用此供应商的订单会被更新，是否继续？", "继续", "取消"))
				UpdateSeller(matSeller);
		});

		// * setting up grid properties
		matEditSellerGrid.setVgap(10);
		matEditSellerGrid.setHgap(10);
		matEditSellerGrid.getChildren().addAll(matEditSellerLabelArray);
		matEditSellerGrid.getChildren().addAll(matEditSellerInputArray);

	}

	private void UpdateSeller(MatSeller matSeller) {
		System.out.println("yp");
	}

	/**
	 * get the index value given the type
	 * @param type
	 * @return
	 */
	private int GetIndexOfMatType(String type) {
		for (int i = 0; i < matOfType.length; i++) {
			if (matOfType[i].equals(type.toUpperCase())) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Clear all input area for add order
	 */
	private void clearAddOrderFields() {
		for (int i = 0; i < matOrderInputArray.size(); i++) {
			if (i != 0 && i != 3) {
				if (matOrderInputArray.get(i) instanceof TextField) ((TextField) matOrderInputArray.get(i)).clear();
				if (matOrderInputArray.get(i) instanceof DatePicker) ((DatePicker) matOrderInputArray.get(i)).setValue(null);
				if (matOrderInputArray.get(i) instanceof ComboBox) ((ComboBox) matOrderInputArray.get(i)).getSelectionModel().select(null);
			}
		}
	}

	/**
	 * Clear all input area for add order
	 */
	private void clearAddSellerFields() {
		for (int i = 0; i < matAddSellerInputArray.size(); i++)
			if (matAddSellerInputArray.get(i) != null) matAddSellerInputArray.get(i).clear();
	}

	/**
	 * Given a company name, find the seller within the all seller array list
	 * @param CompanyName company that needs to be found
	 * @return the specified seller
	 */
	private MatSeller FindSeller (String CompanyName) {
		for (MatSeller seller : allMatSeller) {
			if (seller.getCompanyName().equals(CompanyName)) return seller;
		}
		return new MatSeller(MatSellerId.getMatSellerId(), "NOT FOUND");
	}

	public void initData(Stage currentStage) {
		this.currentStage = currentStage;
		init();
	}

	private void AddSeller() {
		MatSeller newSeller = new MatSeller(MatSellerId.getMatSellerId(), "");
		Method setter;

		for (TextField textField : matAddSellerInputArray) {
			if (!textField.getText().equals("")) {
				try {
					setter = MatSeller.class.getDeclaredMethod("set" + sellerPropertyHeaders[matAddSellerInputArray.indexOf(textField)], String.class);
					setter.invoke(newSeller, textField.getText());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		System.out.println(newSeller.toString());

		try {
			DatabaseUtil.AddMatSeller(newSeller);
			allMatSeller = DatabaseUtil.GetAllMatSellers();
			currentStage.close();
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
	 * Obtain all the new information, update order, and push it to database
	 */
	private void AddOrder() {
		MatOrder newOrder = new MatOrder(MatSerialNum.getMatSerialNum(), "");
		int i = 0;

		String sku = ((TextField) matOrderInputArray.get(i++)).getText();
		newOrder.setSku(sku);

		// mat name
		String nameOfMat = ((TextField) matOrderInputArray.get(i++)).getText();
		newOrder.setName(nameOfMat);

		if (sku.equals("") || nameOfMat.equals("")) {
			if (ConfirmBox.display("数据错误","没有输入数据，结束输入？", "是", "否" )) currentStage.close();
			else return;
		}

		// mat type
		try {
			newOrder.setType(((ComboBox) matOrderInputArray.get(i++)).getValue().toString());
		} catch (NullPointerException ignored) {}

		try {
			newOrder.setOrderDate(((DatePicker) matOrderInputArray.get(i)).getValue() == null ? new Date(0, 0, 0) :
					new Date(((DatePicker) matOrderInputArray.get(i)).getValue().getYear(),
							((DatePicker) matOrderInputArray.get(i)).getValue().getMonthValue(),
							((DatePicker) matOrderInputArray.get(i)).getValue().getDayOfMonth()));
		} catch (NullPointerException ignored) {}
		i++;
		try {
			newOrder.setPaymentDate(((DatePicker) matOrderInputArray.get(i)).getValue() == null ? new Date(0, 0, 0)  :
					new Date(((DatePicker) matOrderInputArray.get(i)).getValue().getYear(),
							((DatePicker) matOrderInputArray.get(i)).getValue().getMonthValue(),
							((DatePicker) matOrderInputArray.get(i)).getValue().getDayOfMonth()));
		} catch (NullPointerException ignored) {}
		i++;
		try {
			newOrder.setArrivalDate(((DatePicker) matOrderInputArray.get(i)).getValue() == null ? new Date(0, 0, 0)  :
					new Date(((DatePicker) matOrderInputArray.get(i)).getValue().getYear(),
							((DatePicker) matOrderInputArray.get(i)).getValue().getMonthValue(),
							((DatePicker) matOrderInputArray.get(i)).getValue().getDayOfMonth()));
		} catch (NullPointerException ignored) {}
		i++;

		try {
			newOrder.setInvoiceDate(((DatePicker) matOrderInputArray.get(i)).getValue() == null ? new Date(0, 0, 0)  :
					new Date(((DatePicker) matOrderInputArray.get(i)).getValue().getYear(),
							((DatePicker) matOrderInputArray.get(i)).getValue().getMonthValue(),
							((DatePicker) matOrderInputArray.get(i)).getValue().getDayOfMonth()));
		} catch (NullPointerException ignored) {}
		i++;

		try {
			newOrder.setInvoice(((TextField) matOrderInputArray.get(i++)).getText());
		} catch (NullPointerException ignored) {}

		try {
			newOrder.setUnitAmount(Double.parseDouble(((TextField) matOrderInputArray.get(i)).getText().equals("") ? "0.0" : ((TextField) matOrderInputArray.get(i)).getText()));
			i++;
			newOrder.setAmount(Double.parseDouble(((TextField) matOrderInputArray.get(i)).getText().equals("") ? "0.0" : ((TextField) matOrderInputArray.get(i)).getText()));
			i++;
			newOrder.setUnitPrice(Double.parseDouble(((TextField) matOrderInputArray.get(i)).getText().equals("") ? "0.0" : ((TextField) matOrderInputArray.get(i)).getText()));
			i++;
			newOrder.setKgAmount();
			newOrder.setTotalPrice();
		} catch (Exception e) {
			AlertBox.display("错误", "数字格式输入错误, 数字为0");
		}

		try {
			newOrder.setSigned(((TextField) matOrderInputArray.get(i++)).getText());
		} catch (NullPointerException ignored) {}

		try {
			newOrder.setSkuSeller(((TextField) matOrderInputArray.get(i++)).getText());
		} catch (NullPointerException ignored) {}

		try {
			newOrder.setNote(((TextField) matOrderInputArray.get(i++)).getText());
		} catch (NullPointerException ignored) {}

		try {
			newOrder.setSeller(FindSeller(((ComboBox) matOrderInputArray.get(i)).getValue().toString()));
		} catch (NullPointerException ignored) {}

		try {
			DatabaseUtil.AddMatOrder(newOrder);
			currentStage.close();
		} catch (SQLException e) {
			AlertBox.display("错误", "无法更新");
		}
	}

	/**
	 * Obtain all the new information, update order, and push it to database
	 */
	private void ContinueOrder() {
		MatOrder newOrder = new MatOrder(MatSerialNum.getMatSerialNum(), "");
		int i = 0;

		String sku = ((TextField) matOrderInputArray.get(i++)).getText();
		newOrder.setSku(sku);

		// mat name
		String nameOfMat = ((TextField) matOrderInputArray.get(i++)).getText();
		newOrder.setName(nameOfMat);

		if (sku.equals("") && nameOfMat.equals("")) currentStage.close();

		// mat type
		try {
			newOrder.setType(((ComboBox) matOrderInputArray.get(i++)).getValue().toString());
		} catch (NullPointerException ignored) {}

		try {
			newOrder.setOrderDate(((DatePicker) matOrderInputArray.get(i)).getValue() == null ? new Date(0, 0, 0) :
					new Date(((DatePicker) matOrderInputArray.get(i)).getValue().getYear(),
							((DatePicker) matOrderInputArray.get(i)).getValue().getMonthValue(),
							((DatePicker) matOrderInputArray.get(i)).getValue().getDayOfMonth()));
		} catch (NullPointerException ignored) {}
		i++;
		try {
			newOrder.setPaymentDate(((DatePicker) matOrderInputArray.get(i)).getValue() == null ? new Date(0, 0, 0) :
					new Date(((DatePicker) matOrderInputArray.get(i)).getValue().getYear(),
							((DatePicker) matOrderInputArray.get(i)).getValue().getMonthValue(),
							((DatePicker) matOrderInputArray.get(i)).getValue().getDayOfMonth()));
		} catch (NullPointerException ignored) {}
		i++;
		try {
			newOrder.setArrivalDate(((DatePicker) matOrderInputArray.get(i)).getValue() == null ? new Date(0, 0, 0) :
					new Date(((DatePicker) matOrderInputArray.get(i)).getValue().getYear(),
							((DatePicker) matOrderInputArray.get(i)).getValue().getMonthValue(),
							((DatePicker) matOrderInputArray.get(i)).getValue().getDayOfMonth()));
		} catch (NullPointerException ignored) {}
		i++;

		try {
			newOrder.setInvoiceDate(((DatePicker) matOrderInputArray.get(i)).getValue() == null ? new Date(0, 0, 0) :
					new Date(((DatePicker) matOrderInputArray.get(i)).getValue().getYear(),
							((DatePicker) matOrderInputArray.get(i)).getValue().getMonthValue(),
							((DatePicker) matOrderInputArray.get(i)).getValue().getDayOfMonth()));
		} catch (NullPointerException ignored) {}
		i++;

		try {
			newOrder.setInvoice(((TextField) matOrderInputArray.get(i++)).getText());
		} catch (NullPointerException ignored) {}

		try {
			newOrder.setUnitAmount(Double.parseDouble(((TextField) matOrderInputArray.get(i)).getText().equals("") ? "0.0" : ((TextField) matOrderInputArray.get(i)).getText()));
			i++;
			newOrder.setAmount(Double.parseDouble(((TextField) matOrderInputArray.get(i)).getText().equals("") ? "0.0" : ((TextField) matOrderInputArray.get(i)).getText()));
			i++;
			newOrder.setUnitPrice(Double.parseDouble(((TextField) matOrderInputArray.get(i)).getText().equals("") ? "0.0" : ((TextField) matOrderInputArray.get(i)).getText()));
			i++;
			newOrder.setKgAmount();
			newOrder.setTotalPrice();
		} catch (Exception e) {
			AlertBox.display("错误", "数字格式输入错误, 数字为0");
		}

		try {
			newOrder.setSigned(((TextField) matOrderInputArray.get(i++)).getText());
		} catch (NullPointerException ignored) {}

		try {
			newOrder.setSkuSeller(((TextField) matOrderInputArray.get(i++)).getText());
		} catch (NullPointerException ignored) {}

		try {
			newOrder.setNote(((TextField) matOrderInputArray.get(i++)).getText());
		} catch (NullPointerException ignored) {}

		try {
			newOrder.setSeller(FindSeller(((ComboBox) matOrderInputArray.get(i)).getValue().toString()));
		} catch (NullPointerException ignored) {}

		try {
			DatabaseUtil.AddMatOrder(newOrder);
			clearAddOrderFields();
		} catch (SQLException e) {
			AlertBox.display("错误", "无法更新");
		}
	}

	private void ContinueSeller() {
		MatSeller newSeller = new MatSeller(MatSellerId.getMatSellerId(), "");
		Method setter;

		for (TextField textField : matAddSellerInputArray) {
			if (!textField.getText().equals("")) {
				try {
					setter = MatSeller.class.getDeclaredMethod("set" + sellerPropertyHeaders[matAddSellerInputArray.indexOf(textField)], String.class);
					setter.invoke(newSeller, textField.getText());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		System.out.println(newSeller.toString());

		try {
			DatabaseUtil.AddMatSeller(newSeller);
			allMatSeller = DatabaseUtil.GetAllMatSellers();
			clearAddSellerFields();
		} catch (Exception e) {
			AlertBox.display("错误", "添加错误");
			e.printStackTrace();
			HandleError error = new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
					e.getMessage(), e.getStackTrace(), false);
			error.WriteToLog();
		}
	}

}
