package Material;

// from my other packages
import Main.*;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.controlsfx.control.textfield.TextFields;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class MatAddOrderModifySeller {

	// main tab pane
	@FXML
	TabPane MatAddOrderModifySellerTabPane;

	// add mat order
	@FXML
	GridPane matAddOrderGrid;
	@FXML
	Label matAddOrderTitleLabel;
	@FXML
	Button matAddOrderCancelButton;
	@FXML
	Button matAddOrderCompleteButton;
	@FXML
	Button matAddOrderContinueButton;

	// add mat seller
	@FXML
	GridPane matAddSellerGrid;
	@FXML
	Label matAddSellerTitleLabel;
	@FXML
	Button matAddSellerCancelButton;
	@FXML
	Button matAddSellerCompleteButton;
	@FXML
	Button matAddSellerContinueButton;

	// edit mat seller
	@FXML
	GridPane matEditSellerGrid;
	@FXML
	Label matEditSellerTitleLabel;
	@FXML
	Button matEditSellerCancelButton;
	@FXML
	Button matEditSellerCompleteButton;
	@FXML
	Button matEditSellerResetButton;
	@FXML
	ColumnConstraints problematicColumnOne;
	@FXML
	ColumnConstraints problematicColumnTwo;

	Stage currentStage;
	ObservableList<MatSeller> allMatSeller;
	ArrayList<Node> matOrderInputArray;
	ArrayList<TextField> matAddSellerInputArray;
	ArrayList<TextField> matEditSellerInputArray;

	/**
	 * Called by main controller, pass in the stage for later closing, and init the screen
	 *
	 * @param currentStage the current stage so it can be closed
	 */
	public void initData(Stage currentStage) {
		this.currentStage = currentStage;
		init();
	}

	/**
	 * Initialized element:
	 * - title size
	 * - button actions
	 * - get all the mat sellers
	 * - call 3 other init functions to init visual elements
	 */
	public void init() {

		// make sure all title labels centered
		matAddOrderTitleLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		matAddSellerTitleLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		matEditSellerTitleLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

		// if escape key is detected anywhere
		MatAddOrderModifySellerTabPane.setOnKeyPressed(keyEvent -> {
			if (keyEvent.getCode().equals(KeyCode.ESCAPE)) {
				if (ConfirmBox.display(ConfirmMessage.CLOSEWINDOW))
					currentStage.close();
			}
		});

		// mat add order buttons
		matAddOrderCompleteButton.setOnAction(actionEvent -> addOrder());
		matAddOrderContinueButton.setOnAction(actionEvent -> continueOrder());
		matAddOrderCompleteButton.setOnKeyReleased(actionEvent -> {
			if (actionEvent.getCode() == KeyCode.ENTER) addOrder();
		});
		matAddOrderContinueButton.setOnKeyReleased(actionEvent -> {
			if (actionEvent.getCode() == KeyCode.ENTER) continueOrder();
		});
		matAddOrderCancelButton.setOnAction(actionEvent -> {
			if (ConfirmBox.display(ConfirmMessage.CANCEL)) currentStage.close();
		});

		// mat add seller buttons
		matAddSellerCompleteButton.setOnAction(actionEvent -> addSeller(false));
		matAddSellerContinueButton.setOnAction(actionEvent -> addSeller(true));
		matAddSellerCompleteButton.setOnKeyReleased(actionEvent -> {
			if (actionEvent.getCode() == KeyCode.ENTER) addSeller(false);
		});
		matAddSellerContinueButton.setOnKeyReleased(actionEvent -> {
			if (actionEvent.getCode() == KeyCode.ENTER) addSeller(true);
		});
		matAddSellerCancelButton.setOnAction(actionEvent -> {
			if (ConfirmBox.display(ConfirmMessage.CANCEL))
				currentStage.close();
		});

		// get all mat sellers, so other functions can use it
		try {
			allMatSeller = DatabaseUtil.GetAllMatSellers();
		} catch (SQLException e) {
			allMatSeller = FXCollections.observableArrayList();
			new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
					e.getMessage(), e.getStackTrace(), false);
		}

		// init all three tabs
		initAddMatOrder();
		initAddMatSeller();
		initEditMatSeller();
	}

	/**
	 * initialize all labels and text fields for add mat order grid
	 */
	private void initAddMatOrder() {

		// grid constraints
		int row = 1;
		int col = 0;

		// setting up all the labels
		for (int i = 0; i < FinalConstants.matTableHeaders.length; i++) {
			Label newLabel = new Label(FinalConstants.matTableHeaders[i]);
			newLabel.setStyle("-fx-font-size: 20px;" +
					"-fx-alignment: center-right;");

			GridPane.setConstraints(newLabel, col, row++);
			matAddOrderGrid.getChildren().add(newLabel);
			if ((i + 6) % 5 == 0) {
				row = 1;
				col += 2;
			}
		}

		// resetting grid constraints
		row = 1;
		col = 1;

		// setting up all the text field
		matOrderInputArray = new ArrayList<>();
		for (int i = 0; i < FinalConstants.matPropertyHeaders.length; i++) {

			// type of mat, combo box
			if (i == 3) {
				JFXComboBox<String> newComboBox = new JFXComboBox<>();
				newComboBox.setPromptText("选择" + FinalConstants.matTableHeaders[i]);
				newComboBox.getItems().setAll(FinalConstants.matOfType);
				newComboBox.setMaxWidth(Double.MAX_VALUE);
				GridPane.setConstraints(newComboBox, col, row++);
				matOrderInputArray.add(newComboBox);
			}

			// seller, combo box
			else if (i == FinalConstants.matPropertyHeaders.length - 1) {
				JFXComboBox<String> sellerComboBox = new JFXComboBox<>();
				sellerComboBox.setPromptText("选择" + FinalConstants.matTableHeaders[i]);

				// getting all the company names
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
			else if (i == 0 || i == 4 || i == 5 || i == 6) {
				DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
				JFXDatePicker datePicker = new JFXDatePicker();
				datePicker.setPromptText("选择" + FinalConstants.matTableHeaders[i]);
				datePicker.setMaxWidth(Double.MAX_VALUE);

				datePicker.setConverter(new StringConverter<>() {
					@Override
					public String toString(LocalDate localDate) {
						if (localDate == null) return "0/0/0";
						return dateTimeFormatter.format(localDate);
					}

					@Override
					public LocalDate fromString(String string) {
						if (string == null || string.isEmpty()) return null;
						return LocalDate.from(dateTimeFormatter.parse(string));
					}
				});

				GridPane.setConstraints(datePicker, col, row++);
				matOrderInputArray.add(datePicker);
			}

			// regular text field
			else {
				JFXTextField newTextField = new JFXTextField();
				// auto complete for name
				if (i == 2) {
					FinalConstants.updateAutoCompleteMatName();
					TextFields.bindAutoCompletion(newTextField, FinalConstants.autoCompleteMatName);
				}
				newTextField.setMaxWidth(Double.MAX_VALUE);
				newTextField.setPromptText("输入" + FinalConstants.matTableHeaders[i]);
				GridPane.setConstraints(newTextField, col, row++);
				matOrderInputArray.add(newTextField);
			}

			// math to set the constraints to the correct position
			if ((i + 6) % 5 == 0) {
				row = 1;
				col += 2;
			}
		}

		TextField name = (TextField) matOrderInputArray.get(2);
		TextField unitPrice = (TextField) matOrderInputArray.get(10);
		name.textProperty().addListener((observableValue, oldValue, newValue) -> {
			try {
				if (DatabaseUtil.CheckIfNameExistsInMatUnitPrice(newValue))
					unitPrice.setText(String.valueOf(DatabaseUtil.GetMatUnitPrice(newValue)));
				else unitPrice.setText("0.0");
			} catch (SQLException ignored) {
			}
		});

		// * setting up grid properties
		matAddOrderGrid.setVgap(10);
		matAddOrderGrid.setHgap(10);
		matAddOrderGrid.getChildren().addAll(matOrderInputArray);
	}

	/**
	 * initialize all labels and text fields for add mat seller grid
	 */
	private void initAddMatSeller() {

		// initialize the input array
		matAddSellerInputArray = new ArrayList<>();

		// grid pane constraints
		int row = 1;
		int col = 0;

		// setting up all the labels
		for (int i = 0; i < FinalConstants.matSellerTableHeaders.length; i++) {
			Label newLabel = new Label(FinalConstants.matSellerTableHeaders[i]);
			newLabel.setStyle("-fx-font-size: 20px;");
			GridPane.setConstraints(newLabel, col, row++);
			matAddSellerGrid.getChildren().add(newLabel);

			// math to set the constraints to the correct position
			if ((i + 5) % 4 == 0) {
				row = 1;
				col += 2;
			}
		}

		// reset constraint
		row = 1;
		col = 1;

		// setting up seller text field
		for (int i = 0; i < FinalConstants.matSellerTableHeaders.length; i++) {
			TextField newTextField = new TextField();
			newTextField.setMaxWidth(Double.MAX_VALUE);
			newTextField.setAlignment(Pos.CENTER_LEFT);
			newTextField.setPromptText("输入" + FinalConstants.matSellerTableHeaders[i]);
			GridPane.setConstraints(newTextField, col, row++);
			matAddSellerInputArray.add(newTextField);

			// math to set the constraints to the correct position
			if ((i + 5) % 4 == 0) {
				row = 1;
				col += 2;
			}
		}

		// * setting up grid properties
		matAddSellerGrid.setVgap(10);
		matAddSellerGrid.setHgap(10);
		matAddSellerGrid.getChildren().addAll(matAddSellerInputArray);
	}

	/**
	 * initialize the initial screen for edit mat seller
	 */
	private void initEditMatSeller() {

		// changing hgrow due to switching element on screen
		problematicColumnOne.setHgrow(Priority.ALWAYS);
		problematicColumnTwo.setHgrow(Priority.ALWAYS);

		// initial temp title
		Label initialSelectSellerTitle = new Label("选择供应商");
		initialSelectSellerTitle.setStyle("-fx-font-size: 20px;");
		initialSelectSellerTitle.setMaxWidth(Double.MAX_VALUE);
		initialSelectSellerTitle.setAlignment(Pos.CENTER_RIGHT);

		// seller combo box and getting all the company name
		ComboBox<String> sellerComboBox = new ComboBox<>();
		String[] allSellerCompany = new String[allMatSeller.size()];
		for (int j = 0; j < allMatSeller.size(); j++) {
			allSellerCompany[j] = allMatSeller.get(j).getCompanyName();
		}
		sellerComboBox.getItems().setAll(allSellerCompany);

		// initial screen hbox
		HBox selectInitSeller = new HBox(initialSelectSellerTitle, sellerComboBox);
		selectInitSeller.setMaxWidth(Double.MAX_VALUE);
		selectInitSeller.setSpacing(10);
		selectInitSeller.setAlignment(Pos.CENTER);
		GridPane.setConstraints(selectInitSeller, 1, 1, 2, 1);

		// initial screen hbox button, to align things correctly
		Button startEdit = new Button("编辑供应商");
		startEdit.setStyle("-fx-background-color: #bbbdf6;\n" +
				"-fx-font-color: black;\n" +
				"-fx-font-size: 18px;");
		startEdit.setAlignment(Pos.CENTER);
		Button deleteSeller = new Button("删除供应商");
		deleteSeller.setStyle("-fx-background-color: #bbbdf6;\n" +
				"-fx-font-color: black;\n" +
				"-fx-font-size: 18px;");
		deleteSeller.setAlignment(Pos.CENTER);
		HBox buttonHBox = new HBox(startEdit, deleteSeller);
		buttonHBox.setMaxWidth(Double.MAX_VALUE);
		buttonHBox.setAlignment(Pos.CENTER);
		buttonHBox.setSpacing(10);

		GridPane.setConstraints(buttonHBox, 1, 2, 2, 1);
		matEditSellerGrid.getChildren().addAll(selectInitSeller, buttonHBox);

		// complete button
		matEditSellerCompleteButton.setOnAction(actionEvent -> currentStage.close());

		// cancel button
		matEditSellerCancelButton.setOnAction(actionEvent -> currentStage.close());

		// start to edit action button
		startEdit.setOnAction(actionEvent -> {
			if (sellerComboBox.getSelectionModel().getSelectedItem() != null) {
				matEditSellerGrid.getChildren().removeAll(buttonHBox, selectInitSeller);
				editMatSeller(allMatSeller.get(sellerComboBox.getSelectionModel().getSelectedIndex()));
			} else
				AlertBox.display("错误", "选择供应商");
		});

		// delete button
		deleteSeller.setOnAction(actionEvent -> {
			if (sellerComboBox.getSelectionModel().getSelectedItem() != null) {
				deleteMatSeller(allMatSeller.get(sellerComboBox.getSelectionModel().getSelectedIndex()));
			} else
				AlertBox.display("错误", "选择供应商");
		});
	}

	/**
	 * Called when user selected a mat seller to update, sets up the actual edit screen
	 *
	 * @param matSeller the mat seller user selected
	 */
	private void editMatSeller(MatSeller matSeller) {

		// changing hgrow due to switching element on screen
		problematicColumnOne.setHgrow(Priority.NEVER);
		problematicColumnTwo.setHgrow(Priority.NEVER);

		// initialize the input array
		matEditSellerInputArray = new ArrayList<>();

		// grid pane constraints
		int row = 1;
		int col = 0;

		// setting up all the labels
		for (int i = 0; i < FinalConstants.matSellerTableHeaders.length; i++) {
			Label newLabel = new Label(FinalConstants.matSellerTableHeaders[i]);
			newLabel.setStyle("-fx-font-size: 20px;");
			GridPane.setConstraints(newLabel, col, row++);
			matEditSellerGrid.getChildren().add(newLabel);

			// math to set the constraints to the correct position
			if ((i + 5) % 4 == 0) {
				row = 1;
				col += 2;
			}
		}

		// resetting constraint
		row = 1;
		col = 1;

		// setting up seller text field
		for (int i = 0; i < FinalConstants.matSellerTableHeaders.length; i++) {
			TextField newTextField = new TextField();
			newTextField.setMaxWidth(Double.MAX_VALUE);
			newTextField.setAlignment(Pos.CENTER_LEFT);
			newTextField.setPromptText("输入" + FinalConstants.matSellerTableHeaders[i]);

			// getting the existing information of the selected seller
			Method getters;
			try {
				getters = MatSeller.class.getDeclaredMethod("get" + FinalConstants.matSellerPropertyHeaders[i]);
				String value = (String) getters.invoke(matSeller);
				newTextField.setText(value);
			} catch (Exception e) {
				AlertBox.display("错误", "读取所选供应商错误，联系管理员。");
				new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
						e.getMessage(), e.getStackTrace(), false);
			}

			GridPane.setConstraints(newTextField, col, row++);
			matEditSellerInputArray.add(newTextField);

			// math to set the constraints to the correct position
			if ((i + 5) % 4 == 0) {
				row = 1;
				col += 2;
			}
		}

		// reset page to initial state
		matEditSellerResetButton.setOnAction(actionEvent -> {
			matEditSellerGrid.getChildren().clear();
			matEditSellerGrid.getChildren().add(matEditSellerTitleLabel);
			initEditMatSeller();
		});

		// complete button
		matEditSellerCompleteButton.setOnAction(actionEvent -> {
			if (ConfirmBox.display("确认", "是否更新所有使用此供应商的订单？", "是", "否"))
				updateSeller(matSeller);
		});
		matEditSellerCompleteButton.setOnKeyReleased(keyEvent -> {
			if (keyEvent.getCode() == KeyCode.ENTER &&
					ConfirmBox.display("确认", "是否更新所有使用此供应商的订单？", "是", "否"))
				updateSeller(matSeller);
		});


		// cancel button
		matEditSellerCancelButton.setOnAction(actionEvent -> {
			if (ConfirmBox.display(ConfirmMessage.CANCEL)) currentStage.close();
		});
		matEditSellerCancelButton.setOnKeyReleased(actionEvent -> {
			if (actionEvent.getCode() == KeyCode.ENTER &&
					ConfirmBox.display(ConfirmMessage.CANCEL)) currentStage.close();
		});

		// * setting up grid properties
		matEditSellerGrid.setVgap(10);
		matEditSellerGrid.setHgap(10);
		matEditSellerGrid.getChildren().addAll(matEditSellerInputArray);
	}

	/**
	 * Delete the seller selected
	 *
	 * @param matSeller the seller to be deleted
	 */
	private void deleteMatSeller(MatSeller matSeller) {
		if (!ConfirmBox.display("确认", "确认删除" + matSeller.getCompanyName() +
				"？所有使用此供应商的订单也会被更新。", "是", "否")) return;
		try {
			DatabaseUtil.DeleteMatSeller(matSeller.getSellerId());
		} catch (SQLException e) {
			AlertBox.display("错误", "删除错误，联系管理员");
			new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
					e.getMessage(), e.getStackTrace(), false);
		}
	}

	/**
	 * Updates seller in database, both mat management and seller
	 *
	 * @param newSeller the new seller that needs to be updated
	 */
	private void updateSeller(MatSeller newSeller) {

		// set the new seller object with new information
		Method setter;
		for (TextField textField : matEditSellerInputArray) {
			if (textField.getText() != null && !textField.getText().equals("")) {
				try {
					setter = MatSeller.class.getDeclaredMethod("set" + FinalConstants.matSellerPropertyHeaders[matEditSellerInputArray.indexOf(textField)], String.class);
					setter.invoke(newSeller, textField.getText());
				} catch (Exception e) {
					AlertBox.display("错误", "更新错误，联系管理员");
					new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
							e.getMessage(), e.getStackTrace(), false);
				}
			}
		}

		// push to databases
		try {
			DatabaseUtil.UpdateMatSellerInMain(newSeller);
			DatabaseUtil.UpdateMatSellerInSeller(newSeller);
			allMatSeller = DatabaseUtil.GetAllMatSellers();
			currentStage.close();
		} catch (Exception e) {
			AlertBox.display("错误", "更新错误，管理员");
			new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
					e.getMessage(), e.getStackTrace(), false);
		}
	}

	/**
	 * Clear all input area for add order
	 */
	private void clearAddOrderFields() {
		for (int i = 0; i < matOrderInputArray.size(); i++) {
			if (i != 0 && i != 1) {
				if (matOrderInputArray.get(i) instanceof TextField) ((TextField) matOrderInputArray.get(i)).clear();
				if (matOrderInputArray.get(i) instanceof DatePicker)
					((DatePicker) matOrderInputArray.get(i)).setValue(null);
				if (matOrderInputArray.get(i) instanceof ComboBox)
					((ComboBox) matOrderInputArray.get(i)).getSelectionModel().select(null);
			}
		}
	}

	/**
	 * Clear all input area for add order
	 */
	private void clearAddSellerFields() {
		for (TextField textField : matAddSellerInputArray) if (textField != null) textField.clear();
	}

	/**
	 * Given a company name, find the seller within the all seller array list
	 *
	 * @param CompanyName company that needs to be found
	 * @return the specified seller
	 */
	private MatSeller findSeller(String CompanyName) {
		for (MatSeller seller : allMatSeller)
			if (seller.getCompanyName().equals(CompanyName)) return seller;
		return null;
	}

	/**
	 * Obtain all the new information, add order, and push it to database
	 */
	private void addOrder() {

		// initialize a new order
		MatOrder newOrder = new MatOrder(SerialNum.getSerialNum(DBOrder.MAT), "");

		// index from the input array
		int i = 0;

		try {
			newOrder.setOrderDate(((DatePicker) matOrderInputArray.get(i)).getValue() == null ? new Date(0, 0, 0) :
					new Date(((DatePicker) matOrderInputArray.get(i)).getValue().getYear(),
							((DatePicker) matOrderInputArray.get(i)).getValue().getMonthValue(),
							((DatePicker) matOrderInputArray.get(i)).getValue().getDayOfMonth()));
		} catch (NullPointerException ignored) {
		}
		i++;

		String sku = ((TextField) matOrderInputArray.get(i++)).getText();
		newOrder.setSku(sku);

		// mat name
		String nameOfMat = ((TextField) matOrderInputArray.get(i++)).getText();
		newOrder.setName(nameOfMat);

		if (sku.equals("") && nameOfMat.equals("")) {
			if (ConfirmBox.display("错误", "没有输入数据，结束输入？", "是", "否"))
				currentStage.close();
			return;
		}

		// mat type
		try {
			newOrder.setType(((ComboBox) matOrderInputArray.get(i++)).getValue().toString());
		} catch (NullPointerException ignored) {
		}

		try {
			newOrder.setPaymentDate(((DatePicker) matOrderInputArray.get(i)).getValue() == null ? new Date(0, 0, 0) :
					new Date(((DatePicker) matOrderInputArray.get(i)).getValue().getYear(),
							((DatePicker) matOrderInputArray.get(i)).getValue().getMonthValue(),
							((DatePicker) matOrderInputArray.get(i)).getValue().getDayOfMonth()));
		} catch (NullPointerException ignored) {
		}
		i++;

		try {
			newOrder.setArrivalDate(((DatePicker) matOrderInputArray.get(i)).getValue() == null ? new Date(0, 0, 0) :
					new Date(((DatePicker) matOrderInputArray.get(i)).getValue().getYear(),
							((DatePicker) matOrderInputArray.get(i)).getValue().getMonthValue(),
							((DatePicker) matOrderInputArray.get(i)).getValue().getDayOfMonth()));
		} catch (NullPointerException ignored) {
		}
		i++;

		try {
			newOrder.setInvoiceDate(((DatePicker) matOrderInputArray.get(i)).getValue() == null ? new Date(0, 0, 0) :
					new Date(((DatePicker) matOrderInputArray.get(i)).getValue().getYear(),
							((DatePicker) matOrderInputArray.get(i)).getValue().getMonthValue(),
							((DatePicker) matOrderInputArray.get(i)).getValue().getDayOfMonth()));
		} catch (NullPointerException ignored) {
		}
		i++;

		try {
			newOrder.setInvoice(((TextField) matOrderInputArray.get(i++)).getText());
		} catch (NullPointerException ignored) {
		}

		try {
			newOrder.setUnitAmount(Double.parseDouble(((TextField) matOrderInputArray.get(i)).getText().equals("") ? "0.0" : ((TextField) matOrderInputArray.get(i)).getText()));
		} catch (NullPointerException ignored) {

		} catch (Exception e) {
			AlertBox.display("错误", "规格格式输入错误, 数字默认0");
		}
		i++;

		try {
			newOrder.setAmount(Double.parseDouble(((TextField) matOrderInputArray.get(i)).getText().equals("") ? "0.0" : ((TextField) matOrderInputArray.get(i)).getText()));
		} catch (NullPointerException ignored) {

		} catch (Exception e) {
			AlertBox.display("错误", "数量格式输入错误, 数字默认0");
		}
		i++;

		try {
			newOrder.setUnitPrice(Double.parseDouble(((TextField) matOrderInputArray.get(i)).getText().equals("") ? "0.0" : ((TextField) matOrderInputArray.get(i)).getText()));
		} catch (NullPointerException ignored) {

		} catch (Exception e) {
			AlertBox.display("错误", "单价格式输入错误, 数字默认0");
		}
		i++;

		newOrder.setKgAmount();
		newOrder.setTotalPrice();

		try {
			newOrder.setSigned(((TextField) matOrderInputArray.get(i++)).getText());
		} catch (NullPointerException ignored) {
		}

		try {
			newOrder.setSkuSeller(((TextField) matOrderInputArray.get(i++)).getText());
		} catch (NullPointerException ignored) {
		}

		try {
			newOrder.setNote(((TextField) matOrderInputArray.get(i++)).getText());
		} catch (NullPointerException ignored) {
		}

		try {
			newOrder.setSeller(findSeller(((ComboBox) matOrderInputArray.get(i)).getValue().toString()));
		} catch (NullPointerException ignored) {
		}

		try {
			DatabaseUtil.AddMatOrder(newOrder);
			if (DatabaseUtil.CheckIfNameExistsInMatUnitPrice(newOrder.getName())) {
				double oldPrice = DatabaseUtil.GetMatUnitPrice(newOrder.getName());
				if (oldPrice != newOrder.getUnitPrice())
					if (ConfirmBox.display("确认", "此原料已存在，需要更新原料单价表吗？之前价格：" +
							oldPrice + " 新价格：" + newOrder.getUnitPrice(), "是", "否"))
						DatabaseUtil.UpdateMatUnitPrice(newOrder.getName(), newOrder.getUnitPrice());
			} else DatabaseUtil.AddMatUnitPrice(new MatUnitPrice(newOrder.getName(), newOrder.getUnitPrice()));
			currentStage.close();
		} catch (SQLException e) {
			AlertBox.display("错误", "更新错误，联系管理员");
			new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
					e.getMessage(), e.getStackTrace(), false);
		}
	}

	/**
	 * Obtain all the new information, push new order to database, and clear fields
	 */
	private void continueOrder() {

		// initialize a new order
		MatOrder newOrder = new MatOrder(SerialNum.getSerialNum(DBOrder.MAT), "");

		// index from the input array
		int i = 0;

		try {
			newOrder.setOrderDate(((DatePicker) matOrderInputArray.get(i)).getValue() == null ? new Date(0, 0, 0) :
					new Date(((DatePicker) matOrderInputArray.get(i)).getValue().getYear(),
							((DatePicker) matOrderInputArray.get(i)).getValue().getMonthValue(),
							((DatePicker) matOrderInputArray.get(i)).getValue().getDayOfMonth()));
		} catch (NullPointerException ignored) {
		}
		i++;

		String sku = ((TextField) matOrderInputArray.get(i++)).getText();
		newOrder.setSku(sku);

		// mat name
		String nameOfMat = ((TextField) matOrderInputArray.get(i++)).getText();
		newOrder.setName(nameOfMat);

		if (sku.equals("") && nameOfMat.equals("")) {
			if (ConfirmBox.display("错误", "没有输入数据，结束输入？", "是", "否"))
				currentStage.close();
			return;
		}

		// mat type
		try {
			newOrder.setType(((ComboBox) matOrderInputArray.get(i++)).getValue().toString());
		} catch (NullPointerException ignored) {
		}

		try {
			newOrder.setPaymentDate(((DatePicker) matOrderInputArray.get(i)).getValue() == null ? new Date(0, 0, 0) :
					new Date(((DatePicker) matOrderInputArray.get(i)).getValue().getYear(),
							((DatePicker) matOrderInputArray.get(i)).getValue().getMonthValue(),
							((DatePicker) matOrderInputArray.get(i)).getValue().getDayOfMonth()));
		} catch (NullPointerException ignored) {
		}
		i++;

		try {
			newOrder.setArrivalDate(((DatePicker) matOrderInputArray.get(i)).getValue() == null ? new Date(0, 0, 0) :
					new Date(((DatePicker) matOrderInputArray.get(i)).getValue().getYear(),
							((DatePicker) matOrderInputArray.get(i)).getValue().getMonthValue(),
							((DatePicker) matOrderInputArray.get(i)).getValue().getDayOfMonth()));
		} catch (NullPointerException ignored) {
		}
		i++;

		try {
			newOrder.setInvoiceDate(((DatePicker) matOrderInputArray.get(i)).getValue() == null ? new Date(0, 0, 0) :
					new Date(((DatePicker) matOrderInputArray.get(i)).getValue().getYear(),
							((DatePicker) matOrderInputArray.get(i)).getValue().getMonthValue(),
							((DatePicker) matOrderInputArray.get(i)).getValue().getDayOfMonth()));
		} catch (NullPointerException ignored) {
		}
		i++;

		try {
			newOrder.setInvoice(((TextField) matOrderInputArray.get(i++)).getText());
		} catch (NullPointerException ignored) {
		}

		try {
			newOrder.setUnitAmount(Double.parseDouble(((TextField) matOrderInputArray.get(i)).getText().equals("") ? "0.0" : ((TextField) matOrderInputArray.get(i)).getText()));
		} catch (NullPointerException ignored) {

		} catch (Exception e) {
			AlertBox.display("错误", "规格格式输入错误, 数字默认0");
		}
		i++;

		try {
			newOrder.setAmount(Double.parseDouble(((TextField) matOrderInputArray.get(i)).getText().equals("") ? "0.0" : ((TextField) matOrderInputArray.get(i)).getText()));
		} catch (NullPointerException ignored) {

		} catch (Exception e) {
			AlertBox.display("错误", "数量格式输入错误, 数字默认0");
		}
		i++;

		try {
			newOrder.setUnitPrice(Double.parseDouble(((TextField) matOrderInputArray.get(i)).getText().equals("") ? "0.0" : ((TextField) matOrderInputArray.get(i)).getText()));
		} catch (NullPointerException ignored) {

		} catch (Exception e) {
			AlertBox.display("错误", "单价格式输入错误, 数字默认0");
		}
		i++;

		newOrder.setKgAmount();
		newOrder.setTotalPrice();

		try {
			newOrder.setSigned(((TextField) matOrderInputArray.get(i++)).getText());
		} catch (NullPointerException ignored) {
		}

		try {
			newOrder.setSkuSeller(((TextField) matOrderInputArray.get(i++)).getText());
		} catch (NullPointerException ignored) {
		}

		try {
			newOrder.setNote(((TextField) matOrderInputArray.get(i++)).getText());
		} catch (NullPointerException ignored) {
		}

		try {
			newOrder.setSeller(findSeller(((ComboBox) matOrderInputArray.get(i)).getValue().toString()));
		} catch (NullPointerException ignored) {
		}

		try {
			DatabaseUtil.AddMatOrder(newOrder);
			if (DatabaseUtil.CheckIfNameExistsInMatUnitPrice(newOrder.getName())) {
				double oldPrice = DatabaseUtil.GetMatUnitPrice(newOrder.getName());
				if (oldPrice != newOrder.getUnitPrice())
					if (ConfirmBox.display("确认", "此原料已存在，需要更新原料单价表吗？之前价格：" +
							oldPrice + " 新价格：" + newOrder.getUnitPrice(), "是", "否"))
						DatabaseUtil.UpdateMatUnitPrice(newOrder.getName(), newOrder.getUnitPrice());
			} else DatabaseUtil.AddMatUnitPrice(new MatUnitPrice(newOrder.getName(), newOrder.getUnitPrice()));
			clearAddOrderFields();
		} catch (SQLException e) {
			AlertBox.display("错误", "更新错误，联系管理员");
			new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
					e.getMessage(), e.getStackTrace(), false);
		}
	}

	/**
	 * Obtain all the new seller information, add seller, and push it to database
	 */
	private void addSeller(boolean cont) {

		// initialize new seller
		MatSeller newSeller = new MatSeller(SerialNum.getSerialNum(DBOrder.SELLER), "");

		// setting all the new information
		Method setter;
		for (TextField textField : matAddSellerInputArray) {
			if (!textField.getText().equals("")) {
				try {
					setter = MatSeller.class.getDeclaredMethod("set" + FinalConstants.matSellerPropertyHeaders[matAddSellerInputArray.indexOf(textField)], String.class);
					setter.invoke(newSeller, textField.getText());
				} catch (Exception e) {
					AlertBox.display("错误", "添加错误，联系管理员");
					new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
							e.getMessage(), e.getStackTrace(), false);
				}
			}
		}

		try {
			DatabaseUtil.AddMatSeller(newSeller);
			allMatSeller = DatabaseUtil.GetAllMatSellers();
			if (cont) clearAddSellerFields();
			else currentStage.close();
		} catch (Exception e) {
			AlertBox.display("错误", "添加错误");
			new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
					e.getMessage(), e.getStackTrace(), false);
		}
	}

}
