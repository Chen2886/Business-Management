import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.apache.xmlbeans.impl.soap.Text;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class MatAddOrderController {

	// table headers
	private static final String[] tableHeaders = new String[] {"订单号", "原料名称", "类别", "订单日期", "付款日期",
			"到达日期", "发票日期", "发票编号", "规格", "数量", "单价", "签收人", "供应商订单编号", "备注", "供应商"};

	// all property listed
	private static final String[] propertyHeaders = new String[]{"sku", "name", "type", "orderDate", "paymentDate",
			"arrivalDate", "invoiceDate", "invoice", "unitAmount", "amount", "unitPrice", "signed", "skuSeller", "note", "seller"};
	// all types
	private static final String[] matOfType = new String[]{"RD", "S", "P", "A", "R", "PA"};

	@FXML GridPane MatEditOrderGrid;
	@FXML Label editOrderTitleLabel;
	@FXML Button matCancelButton;
	@FXML Button matCompleteButton;
	@FXML Button matContinueButton;

	Stage currentStage;
	ObservableList<MatSeller> allSeller;
	ArrayList<Node> inputArrayList;

	/**
	 * Initialize all the element on the screen
	 */
	public void init() {
		editOrderTitleLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

		matCancelButton.setOnAction(actionEvent -> {
			if (ConfirmBox.display("确认", "确定取消？此订单不会被保存", "确认", "取消"))
				currentStage.close();
		});

		matCompleteButton.setOnAction(actionEvent -> {
			AddOrder();
		});

		matContinueButton.setOnAction(actionEvent -> {
			ContinueOrder();
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
			else if (i == 3 || i == 4 || i == 5 || i == 6) {
				DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
				DatePicker datePicker = new DatePicker();
				Method getters;

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
				inputArrayList.add(datePicker);
			}

			// regular text field
			else {
				TextField newTextField = new TextField();
				Method getters;
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
	 * Obtain all the new information, update order, and push it to database
	 */
	private void AddOrder() {
		MatOrder newOrder = new MatOrder(MatSerialNum.getMatSerialNum(), "");
		int i = 0;

		String sku = ((TextField) inputArrayList.get(i++)).getText();
		newOrder.setSku(sku);

		// mat name
		String nameOfMat = ((TextField) inputArrayList.get(i++)).getText();
		newOrder.setName(nameOfMat);

		if (sku.equals("") || nameOfMat.equals("")) {
			if (ConfirmBox.display("数据错误","没有输入数据，结束输入？", "是", "否" )) currentStage.close();
			else return;
		}

		// mat type
		try {
			newOrder.setType(((ComboBox) inputArrayList.get(i++)).getValue().toString());
		} catch (NullPointerException ignored) {}

		try {
			newOrder.setOrderDate(((DatePicker) inputArrayList.get(i)).getValue() == null ? null :
					new Date(((DatePicker) inputArrayList.get(i)).getValue().getYear(),
							((DatePicker) inputArrayList.get(i)).getValue().getMonthValue(),
							((DatePicker) inputArrayList.get(i)).getValue().getDayOfMonth()));
		} catch (NullPointerException ignored) {}
			i++;
		try {
			newOrder.setPaymentDate(((DatePicker) inputArrayList.get(i)).getValue() == null ? null :
					new Date(((DatePicker) inputArrayList.get(i)).getValue().getYear(),
							((DatePicker) inputArrayList.get(i)).getValue().getMonthValue(),
							((DatePicker) inputArrayList.get(i)).getValue().getDayOfMonth()));
		} catch (NullPointerException ignored) {}
			i++;
		try {
			newOrder.setArrivalDate(((DatePicker) inputArrayList.get(i)).getValue() == null ? null :
					new Date(((DatePicker) inputArrayList.get(i)).getValue().getYear(),
							((DatePicker) inputArrayList.get(i)).getValue().getMonthValue(),
							((DatePicker) inputArrayList.get(i)).getValue().getDayOfMonth()));
		} catch (NullPointerException ignored) {}
			i++;

		try {
			newOrder.setInvoiceDate(((DatePicker) inputArrayList.get(i)).getValue() == null ? null :
					new Date(((DatePicker) inputArrayList.get(i)).getValue().getYear(),
							((DatePicker) inputArrayList.get(i)).getValue().getMonthValue(),
							((DatePicker) inputArrayList.get(i)).getValue().getDayOfMonth()));
		} catch (NullPointerException ignored) {}
			i++;

		try {
			newOrder.setInvoice(((TextField) inputArrayList.get(i++)).getText());
		} catch (NullPointerException ignored) {}

		try {
			newOrder.setUnitAmount(Double.parseDouble(((TextField) inputArrayList.get(i)).getText().equals("") ? "0.0" : ((TextField) inputArrayList.get(i)).getText()));
			i++;
			newOrder.setAmount(Double.parseDouble(((TextField) inputArrayList.get(i)).getText().equals("") ? "0.0" : ((TextField) inputArrayList.get(i)).getText()));
			i++;
			newOrder.setUnitPrice(Double.parseDouble(((TextField) inputArrayList.get(i)).getText().equals("") ? "0.0" : ((TextField) inputArrayList.get(i)).getText()));
			i++;
			newOrder.setKgAmount();
			newOrder.setTotalPrice();
		} catch (Exception e) {
			AlertBox.display("错误", "数字格式输入错误, 数字为0");
		}

		try {
			newOrder.setSigned(((TextField) inputArrayList.get(i++)).getText());
		} catch (NullPointerException ignored) {}

		try {
			newOrder.setSkuSeller(((TextField) inputArrayList.get(i++)).getText());
		} catch (NullPointerException ignored) {}

		try {
			newOrder.setNote(((TextField) inputArrayList.get(i++)).getText());
		} catch (NullPointerException ignored) {}

		try {
			newOrder.setSeller(FindSeller(((ComboBox) inputArrayList.get(i)).getValue().toString()));
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

		String sku = ((TextField) inputArrayList.get(i++)).getText();
		newOrder.setSku(sku);

		// mat name
		String nameOfMat = ((TextField) inputArrayList.get(i++)).getText();
		newOrder.setName(nameOfMat);

		if (sku.equals("") && nameOfMat.equals("")) currentStage.close();

		// mat type
		try {
			newOrder.setType(((ComboBox) inputArrayList.get(i++)).getValue().toString());
		} catch (NullPointerException ignored) {}

		try {
			newOrder.setOrderDate(((DatePicker) inputArrayList.get(i)).getValue() == null ? new Date(0, 0, 0) :
					new Date(((DatePicker) inputArrayList.get(i)).getValue().getYear(),
							((DatePicker) inputArrayList.get(i)).getValue().getMonthValue(),
							((DatePicker) inputArrayList.get(i)).getValue().getDayOfMonth()));
		} catch (NullPointerException ignored) {}
		i++;
		try {
			newOrder.setPaymentDate(((DatePicker) inputArrayList.get(i)).getValue() == null ? new Date(0, 0, 0) :
					new Date(((DatePicker) inputArrayList.get(i)).getValue().getYear(),
							((DatePicker) inputArrayList.get(i)).getValue().getMonthValue(),
							((DatePicker) inputArrayList.get(i)).getValue().getDayOfMonth()));
		} catch (NullPointerException ignored) {}
		i++;
		try {
			newOrder.setArrivalDate(((DatePicker) inputArrayList.get(i)).getValue() == null ? new Date(0, 0, 0) :
					new Date(((DatePicker) inputArrayList.get(i)).getValue().getYear(),
							((DatePicker) inputArrayList.get(i)).getValue().getMonthValue(),
							((DatePicker) inputArrayList.get(i)).getValue().getDayOfMonth()));
		} catch (NullPointerException ignored) {}
		i++;

		try {
			newOrder.setInvoiceDate(((DatePicker) inputArrayList.get(i)).getValue() == null ? new Date(0, 0, 0) :
					new Date(((DatePicker) inputArrayList.get(i)).getValue().getYear(),
							((DatePicker) inputArrayList.get(i)).getValue().getMonthValue(),
							((DatePicker) inputArrayList.get(i)).getValue().getDayOfMonth()));
		} catch (NullPointerException ignored) {}
		i++;

		try {
			newOrder.setInvoice(((TextField) inputArrayList.get(i++)).getText());
		} catch (NullPointerException ignored) {}

		try {
			newOrder.setUnitAmount(Double.parseDouble(((TextField) inputArrayList.get(i)).getText().equals("") ? "0.0" : ((TextField) inputArrayList.get(i)).getText()));
			i++;
			newOrder.setAmount(Double.parseDouble(((TextField) inputArrayList.get(i)).getText().equals("") ? "0.0" : ((TextField) inputArrayList.get(i)).getText()));
			i++;
			newOrder.setUnitPrice(Double.parseDouble(((TextField) inputArrayList.get(i)).getText().equals("") ? "0.0" : ((TextField) inputArrayList.get(i)).getText()));
			i++;
			newOrder.setKgAmount();
			newOrder.setTotalPrice();
		} catch (Exception e) {
			AlertBox.display("错误", "数字格式输入错误, 数字为0");
		}

		try {
			newOrder.setSigned(((TextField) inputArrayList.get(i++)).getText());
		} catch (NullPointerException ignored) {}

		try {
			newOrder.setSkuSeller(((TextField) inputArrayList.get(i++)).getText());
		} catch (NullPointerException ignored) {}

		try {
			newOrder.setNote(((TextField) inputArrayList.get(i++)).getText());
		} catch (NullPointerException ignored) {}

		try {
			newOrder.setSeller(FindSeller(((ComboBox) inputArrayList.get(i)).getValue().toString()));
		} catch (NullPointerException ignored) {}

		try {
			DatabaseUtil.AddMatOrder(newOrder);
			clearFields();
		} catch (SQLException e) {
			AlertBox.display("错误", "无法更新");
		}
	}

	/**
	 * Clear all input area
	 */
	private void clearFields() {
		for (int i = 0; i < inputArrayList.size(); i++) {
			if (i != 0 && i != 3) {
				if (inputArrayList.get(i) instanceof TextField) ((TextField) inputArrayList.get(i)).clear();
				if (inputArrayList.get(i) instanceof DatePicker) ((DatePicker) inputArrayList.get(i)).setValue(null);
				if (inputArrayList.get(i) instanceof ComboBox) ((ComboBox) inputArrayList.get(i)).getSelectionModel().select(null);
			}
		}
	}


	/**
	 * Given a company name, find the seller within the all seller array list
	 * @param CompanyName company that needs to be found
	 * @return the specified seller
	 */
	private MatSeller FindSeller (String CompanyName) {
		for (MatSeller seller : allSeller) {
			if (seller.getCompanyName().equals(CompanyName)) return seller;
		}
		return new MatSeller(MatSellerId.getMatSellerId(), "NOT FOUND");
	}

	public void initData(Stage currentStage) {
		this.currentStage = currentStage;
		init();
	}

}
