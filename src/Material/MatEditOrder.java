package Material;

// from my other packages

import Main.*;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.controlsfx.control.textfield.TextFields;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@Deprecated
public class MatEditOrder {

	
	@FXML
	GridPane MatEditOrderGrid;
	@FXML
	Label editOrderTitleLabel;
	@FXML
	Button cancelButton;
	@FXML
	Button completeButton;

	Stage currentStage;
	MatOrder selectedOrder;
	ArrayList<Node> inputArrayList;

	/**
	 * Called by main controller to give the selected order
	 *
	 * @param selectedOrder the order that was selected, to fill the information
	 * @param currentStage  the stage, so it can be closed later
	 */
	public void initData(MatOrder selectedOrder, Stage currentStage) {
		this.selectedOrder = selectedOrder;
		this.currentStage = currentStage;
		init();
	}

	/**
	 * Initialize all the element on the screen
	 */
	public void init() {
		editOrderTitleLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

		cancelButton.setOnAction(actionEvent -> {
			if (ConfirmBox.display(ConfirmMessage.CANCEL))
				currentStage.close();
		});

		completeButton.setOnAction(actionEvent -> UpdateOrder());

		int row = 1;
		int col = 0;

		// setting up all the labels
		ArrayList<Label> labelArrayList = new ArrayList<>();
		for (int i = 0; i < FinalConstants.matTableHeaders.length; i++) {
			Label newLabel = new Label(FinalConstants.matTableHeaders[i]);
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
		for (int i = 0; i < FinalConstants.matPropertyHeaders.length; i++) {

			// type of mat, combo box
			if (i == 3) {
				ComboBox<String> newComboBox = new ComboBox<>();
				newComboBox.getItems().setAll(FinalConstants.matOfType);
				newComboBox.getSelectionModel().select(GetIndexOfMatType(selectedOrder.getType()));
				newComboBox.setMaxWidth(Double.MAX_VALUE);
				GridPane.setConstraints(newComboBox, col, row++);
				inputArrayList.add(newComboBox);
			}

			// seller, combo box
			else if (i == FinalConstants.matPropertyHeaders.length - 1) {
				ComboBox<String> sellerComboBox = new ComboBox<>();

				int currentSellerIndex = -1;
				String[] allSellerCompany = new String[FinalConstants.allMatSellers.size()];
				for (int j = 0; j < FinalConstants.allMatSellers.size(); j++) {
					if (FinalConstants.allMatSellers.get(j).equals(selectedOrder.getSeller())) currentSellerIndex = j;
					allSellerCompany[j] = FinalConstants.allMatSellers.get(j).getCompanyName();
				}
				sellerComboBox.getItems().setAll(allSellerCompany);
				sellerComboBox.getSelectionModel().select(currentSellerIndex);
				sellerComboBox.setMaxWidth(Double.MAX_VALUE);
				GridPane.setConstraints(sellerComboBox, col, row++);
				inputArrayList.add(sellerComboBox);
			}

			// dates, date picker
			else if (i == 0 || i == 4 || i == 5 || i == 6) {
				DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
				DatePicker datePicker = new DatePicker();
				Method getters;

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

				try {
					getters = MatOrder.class.getDeclaredMethod("get" + Character.toUpperCase(FinalConstants.matPropertyHeaders[i].charAt(0)) + FinalConstants.matPropertyHeaders[i].substring(1));
					Date dateVal = (Date) getters.invoke(selectedOrder);
					if (dateVal != null && !dateVal.equals(new Date(0, 0, 0))) {
						datePicker.setValue(LocalDate.of(dateVal.getY(), dateVal.getM(), dateVal.getD()));
					}
				} catch (Exception e) {
					AlertBox.display("错误", "读取信息错误！");
					new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
							e.getMessage(), e.getStackTrace(), false);
				}

			}

			// regular text field
			else {
				TextField newTextField = new TextField();
				Method getters;
				if (i == 2) TextFields.bindAutoCompletion(newTextField, FinalConstants.autoCompleteMatName);
				try {
					getters = MatOrder.class.getDeclaredMethod("get" + Character.toUpperCase(FinalConstants.matPropertyHeaders[i].charAt(0)) + FinalConstants.matPropertyHeaders[i].substring(1));
					newTextField.setText(String.valueOf(getters.invoke(selectedOrder) == null ? "" : getters.invoke(selectedOrder)));
				} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
					AlertBox.display("错误", "读取信息错误！");
					new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
							e.getMessage(), e.getStackTrace(), false);
				}
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
	 *
	 * @param type given type
	 * @return the index of type given
	 */
	private int GetIndexOfMatType(String type) {
		for (int i = 0; i < FinalConstants.matOfType.length; i++) {
			if (FinalConstants.matOfType[i].equals(type.toUpperCase())) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Obtain all the new information, update order, and push it to database
	 */
	private void UpdateOrder() {
		MatOrder newOrder = new MatOrder(selectedOrder.getSerialNum(), selectedOrder.getSku());
		int i = 0;

		try {
			newOrder.setOrderDate(((DatePicker) inputArrayList.get(i)).getValue() == null ? new Date(0, 0, 0) :
					new Date(((DatePicker) inputArrayList.get(i)).getValue().getYear(),
							((DatePicker) inputArrayList.get(i)).getValue().getMonthValue(),
							((DatePicker) inputArrayList.get(i)).getValue().getDayOfMonth()));
		} catch (NullPointerException ignored) {
		}
		i++;

		String sku = ((TextField) inputArrayList.get(i++)).getText();
		if (sku.equals("")) {
			AlertBox.display("错误", "没有输入订单编号");
			return;
		} else {
			newOrder.setSku(sku);
		}

		// mat name
		String nameOfMat = ((TextField) inputArrayList.get(i++)).getText();
		if (nameOfMat.equals("")) {
			AlertBox.display("错误", "没有输入原料名称");
			return;
		} else {
			newOrder.setName(nameOfMat);
		}

		// mat type
		try {
			newOrder.setType(((ComboBox) inputArrayList.get(i++)).getValue().toString());
		} catch (NullPointerException ignored) {
		}

		try {
			newOrder.setPaymentDate(((DatePicker) inputArrayList.get(i)).getValue() == null ? new Date(0, 0, 0) :
					new Date(((DatePicker) inputArrayList.get(i)).getValue().getYear(),
							((DatePicker) inputArrayList.get(i)).getValue().getMonthValue(),
							((DatePicker) inputArrayList.get(i)).getValue().getDayOfMonth()));
		} catch (NullPointerException ignored) {
		}
		i++;

		try {
			newOrder.setArrivalDate(((DatePicker) inputArrayList.get(i)).getValue() == null ? new Date(0, 0, 0) :
					new Date(((DatePicker) inputArrayList.get(i)).getValue().getYear(),
							((DatePicker) inputArrayList.get(i)).getValue().getMonthValue(),
							((DatePicker) inputArrayList.get(i)).getValue().getDayOfMonth()));
		} catch (NullPointerException ignored) {
		}
		i++;

		try {
			newOrder.setInvoiceDate(((DatePicker) inputArrayList.get(i)).getValue() == null ? new Date(0, 0, 0) :
					new Date(((DatePicker) inputArrayList.get(i)).getValue().getYear(),
							((DatePicker) inputArrayList.get(i)).getValue().getMonthValue(),
							((DatePicker) inputArrayList.get(i)).getValue().getDayOfMonth()));
		} catch (NullPointerException ignored) {
		}
		i++;

		try {
			newOrder.setInvoice(((TextField) inputArrayList.get(i++)).getText());
		} catch (NullPointerException ignored) {
		}

		try {
			newOrder.setUnitAmount(Double.parseDouble(((TextField) inputArrayList.get(i)).getText().equals("") ? "0.0" : ((TextField) inputArrayList.get(i)).getText()));
		} catch (NullPointerException ignored) {

		} catch (Exception e) {
			AlertBox.display("错误", "规格格式输入错误, 数字默认0");
		}
		i++;

		try {
			newOrder.setAmount(Double.parseDouble(((TextField) inputArrayList.get(i)).getText().equals("") ? "0.0" : ((TextField) inputArrayList.get(i)).getText()));
		} catch (NullPointerException ignored) {

		} catch (Exception e) {
			AlertBox.display("错误", "数量格式输入错误, 数字默认0");
		}
		i++;

		try {
			newOrder.setUnitPrice(Double.parseDouble(((TextField) inputArrayList.get(i)).getText().equals("") ? "0.0" : ((TextField) inputArrayList.get(i)).getText()));
		} catch (NullPointerException ignored) {

		} catch (Exception e) {
			AlertBox.display("错误", "单价格式输入错误, 数字默认0");
		}
		i++;

		newOrder.setKgAmount();
		newOrder.setTotalPrice();

		try {
			newOrder.setSigned(((TextField) inputArrayList.get(i++)).getText());
		} catch (NullPointerException ignored) {
		}

		try {
			newOrder.setSkuSeller(((TextField) inputArrayList.get(i++)).getText());
		} catch (NullPointerException ignored) {
		}

		try {
			newOrder.setNote(((TextField) inputArrayList.get(i++)).getText());
		} catch (NullPointerException ignored) {
		}

		try {
			newOrder.setSeller(FindSeller(((ComboBox) inputArrayList.get(i)).getValue().toString()));
		} catch (NullPointerException ignored) {
		}

		try {
			DatabaseUtil.UpdateMatOrder(newOrder);
			currentStage.close();
		} catch (SQLException e) {
			AlertBox.display("错误", "无法更新");
		}

	}

	/**
	 * Given a company name, find the seller within the all seller array list
	 *
	 * @param CompanyName company that needs to be found
	 * @return the specified seller
	 */
	private MatSeller FindSeller(String CompanyName) {
		for (MatSeller seller : FinalConstants.allMatSellers) {
			if (seller.getCompanyName().equals(CompanyName)) return seller;
		}
		return new MatSeller(SerialNum.getSerialNum(DBOrder.SELLER), "NOT FOUND");
	}

}
