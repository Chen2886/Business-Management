package Material;

// from my other packages
import CustomEditingCells.*;
import Main.*;

import com.jfoenix.controls.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.controlsfx.control.textfield.TextFields;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;

public class MatAddOrder {

	// mat table headers
	private static final String[] matHeaders = new String[]{"订单日期", "订单号", "原料名称", "类别", "付款日期",
			"到达日期", "发票日期", "发票编号", "规格", "数量", "公斤", "单价", "总价", "签收人", "供应商订单编号", "供应商",
			"联系人", "手机", "座机", "传真", "供应商账号", "供应商银行地址", "供应商地址", "备注"};

	// all mat property listed
	private static final String[] matProperty = new String[]{"orderDate", "sku", "name", "type", "paymentDate",
			"arrivalDate", "invoiceDate", "invoice", "unitAmount", "amount", "kgAmount", "unitPrice", "totalPrice",
			"signed", "skuSeller", "company", "contact", "mobile", "land", "fax", "account",
			"bank", "address", "note"};

	public VBox infoInputVBox;
	public ScrollPane scrollPane;
	public ImageView backButton;
	public TableView<MatOrder> matTableView;
	@FXML
	Label matAddOrderTitleLabel;
	@FXML
	Button matAddOrderContinueButton;

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

		allMatSeller = FinalConstants.updateAllMatSellers();

		// make sure all title labels centered
		matAddOrderTitleLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

		// mat add order buttons
		matAddOrderContinueButton.setOnAction(actionEvent -> continueOrder());
		matAddOrderContinueButton.setOnKeyReleased(actionEvent -> {
			if (actionEvent.getCode() == KeyCode.ENTER) continueOrder();
		});
		backButton.setOnMouseClicked(event -> {
			try {
				FXMLLoader loader = new FXMLLoader();
				InputStream fileInputStream = getClass().getResourceAsStream(Main.fxmlPath + "MainScreen.fxml");
				Parent newScene = loader.load(fileInputStream);
				Main.mainStage.setTitle("订单管理系统");

				Scene scene = new Scene(newScene);
				scene.getStylesheets().add(Main.class.getResource(Main.styleSheetPath).toURI().toString());
				Main.mainStage.setScene(scene);
			} catch (Exception e) {
				AlertBox.display("错误", "主页窗口错误！");
				new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
						e.getMessage(), e.getStackTrace(), false);
			}
		});

		// init all three tabs
		initAddMatOrder();
		fillMatTable(FinalConstants.updateAllMatOrders());
	}

	/**
	 * Filling of the material table
	 *
	 * @param selectedMatOrders the orders specified
	 */
	public void fillMatTable(ObservableList<MatOrder> selectedMatOrders) {

		// array of columns
		Collection<TableColumn<MatOrder, ?>> orderColumnArrayList = new ArrayList<>();

		// Regular String callback
		Callback<TableColumn<MatOrder, String>, TableCell<MatOrder, String>> stringEditableFactory =
				p -> new EditingCellWithTextFields<>(String.class) {};

		// Mat name callback with autocomplete
		Callback<TableColumn<MatOrder, String>, TableCell<MatOrder, String>> matNameEditableFactory =
				p -> new EditingCellForMatName<>() {};

		// Double callback
		Callback<TableColumn<MatOrder, Double>, TableCell<MatOrder, Double>> doubleEditableFactory =
				p -> new EditingCellWithTextFields<>(Double.class) {};

		// Mat of Type combo Box callback
		Callback<TableColumn<MatOrder, String>, TableCell<MatOrder, String>> matOfTypeEditableFactory =
				p -> new EditingCellForMatOfType<>() {};

		// Date Picker callback
		Callback<TableColumn<MatOrder, Date>, TableCell<MatOrder, Date>> datePickerEditableFactory =
				p -> new EditingCellWithDatePicker<>() {};

		// Mat Seller callback
		Callback<TableColumn<MatOrder, String>, TableCell<MatOrder, String>> matSellerEditableFactory =
				p -> new EditingCellForMatSeller<>() {};

		// loop to set up all regular columns
		for (int i = 0; i < matHeaders.length; i++) {
			if (i == 8 || i == 9 || i == 10 || i == 11 || i == 12) {
				// Doubles
				TableColumn<MatOrder, Double> newColumn = new TableColumn<>(matHeaders[i]);
				newColumn.setCellValueFactory(new PropertyValueFactory<>(matProperty[i]));
				newColumn.setStyle("-fx-alignment: CENTER;");
				orderColumnArrayList.add(newColumn);

				// Exclude kgAmount and TotalPrice because they are automatic
				if (i != 10 && i != 12) {
					int matPropertyIndex = i;
					newColumn.setCellFactory(doubleEditableFactory);
					newColumn.setOnEditCommit(event -> {

						if (event.getNewValue().equals(Double.MAX_VALUE)) {
							AlertBox.display("错误", "数字输入格式错误！");
							return;
						}

						MatOrder editingOrder = event.getRowValue();
						try {
							Method setter;
							setter = MatOrder.class.getDeclaredMethod("set" +
									Character.toUpperCase(matProperty[matPropertyIndex].charAt(0)) +
									matProperty[matPropertyIndex].substring(1), double.class);
							setter.invoke(editingOrder, event.getNewValue());
							editingOrder.setKgAmount();
							editingOrder.setTotalPrice();
							DatabaseUtil.UpdateMatOrder(editingOrder);
							matTableView.refresh();
						} catch (SQLException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
							e.printStackTrace();
							AlertBox.display("错误", "编辑订单错误！(数字）");
							new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
									e.getMessage(), e.getStackTrace(), false);
						}
					});
				}
			} else if (i == 0 || i == 4 || i == 5 || i == 6) {
				// Main.Date
				TableColumn<MatOrder, Date> newColumn = new TableColumn<>(matHeaders[i]);
				newColumn.setCellValueFactory(new PropertyValueFactory<>(matProperty[i]));
				newColumn.setStyle("-fx-alignment: CENTER;");
				newColumn.setMinWidth(110);
				orderColumnArrayList.add(newColumn);

				int matPropertyIndex = i;
				newColumn.setCellFactory(datePickerEditableFactory);
				newColumn.setOnEditCommit(event -> {
					MatOrder editingOrder = event.getRowValue();
					try {
						Method setter;
						setter = MatOrder.class.getDeclaredMethod("set" +
								Character.toUpperCase(matProperty[matPropertyIndex].charAt(0)) +
								matProperty[matPropertyIndex].substring(1), Date.class);
						setter.invoke(editingOrder, event.getNewValue());
						DatabaseUtil.UpdateMatOrder(editingOrder);
						matTableView.refresh();
					} catch (SQLException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
						AlertBox.display("错误", "编辑订单错误！(日期）");
						e.printStackTrace();
						new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
								e.getMessage(), e.getStackTrace(), false);
					}
				});
			} else if (i == 13) {
				// signed by increase column width
				TableColumn<MatOrder, String> newColumn = new TableColumn<>(matHeaders[i]);
				newColumn.setCellValueFactory(new PropertyValueFactory<>(matProperty[i]));
				newColumn.setStyle("-fx-alignment: CENTER;");
				newColumn.setMinWidth(60);
				orderColumnArrayList.add(newColumn);
				int matPropertyIndex = i;

				// Set up for editable table view
				newColumn.setCellFactory(stringEditableFactory);
				newColumn.setOnEditCommit(event -> {
					MatOrder editingOrder = event.getRowValue();
					try {
						Method setter;
						setter = MatOrder.class.getDeclaredMethod("set" +
								Character.toUpperCase(matProperty[matPropertyIndex].charAt(0)) +
								matProperty[matPropertyIndex].substring(1), String.class);
						setter.invoke(editingOrder, event.getNewValue());
						DatabaseUtil.UpdateMatOrder(editingOrder);
						matTableView.refresh();
					} catch (SQLException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
						AlertBox.display("错误", "编辑订单错误！(文字）");
						new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
								e.getMessage(), e.getStackTrace(), false);
					}
				});
			} else {
				// String
				TableColumn<MatOrder, String> newColumn = new TableColumn<>(matHeaders[i]);
				newColumn.setCellValueFactory(new PropertyValueFactory<>(matProperty[i]));
				newColumn.setStyle("-fx-alignment: CENTER;");
				orderColumnArrayList.add(newColumn);

				if (i == 7) newColumn.setMinWidth(110);

				// Set up for editable table view
				// NOTES: Not allowing to edit sellers, Mat Of Type and Seller needs combo Box
				// matName needs autocomplete
				if ((i <= 14 || i == 23) && i != 3 && i != 2) {
					int matPropertyIndex = i;
					newColumn.setCellFactory(stringEditableFactory);
					newColumn.setOnEditCommit(event -> {
						MatOrder editingOrder = event.getRowValue();
						try {
							Method setter;
							setter = MatOrder.class.getDeclaredMethod("set" +
									Character.toUpperCase(matProperty[matPropertyIndex].charAt(0)) +
									matProperty[matPropertyIndex].substring(1), String.class);
							setter.invoke(editingOrder, event.getNewValue());
							DatabaseUtil.UpdateMatOrder(editingOrder);
							matTableView.refresh();
						} catch (SQLException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
							AlertBox.display("错误", "编辑订单错误！(文字）");
							new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
									e.getMessage(), e.getStackTrace(), false);
						}
					});
				} else if (i == 15) {
					newColumn.setCellFactory(matSellerEditableFactory);
					newColumn.setOnEditCommit(event -> {
						MatOrder editingOrder = event.getRowValue();

						MatSeller selectedSeller = new MatSeller(SerialNum.getSerialNum(DBOrder.SELLER), "temp");
						for (MatSeller matSeller : FinalConstants.updateAllMatSellers()) {
							if (matSeller.getCompanyName().equals(event.getNewValue()))
								selectedSeller = matSeller;
						}
						editingOrder.setSeller(selectedSeller);
						try {
							DatabaseUtil.UpdateMatOrder(editingOrder);
						} catch (SQLException e) {
							AlertBox.display("错误", "编辑订单错误！");
							new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
									e.getMessage(), e.getStackTrace(), false);
						}
						matTableView.refresh();
					});
				} else if (i == 3) {
					int matPropertyIndex = i;
					newColumn.setCellFactory(matOfTypeEditableFactory);
					newColumn.setOnEditCommit(event -> {
						MatOrder editingOrder = event.getRowValue();
						try {
							Method setter;
							setter = MatOrder.class.getDeclaredMethod("set" +
									Character.toUpperCase(matProperty[matPropertyIndex].charAt(0)) +
									matProperty[matPropertyIndex].substring(1), String.class);
							setter.invoke(editingOrder, event.getNewValue());
							DatabaseUtil.UpdateMatOrder(editingOrder);
							matTableView.refresh();
						} catch (SQLException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
							AlertBox.display("错误", "编辑订单错误！(文字）");
							new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
									e.getMessage(), e.getStackTrace(), false);
						}
					});
				} else if (i == 2) {
					newColumn.setCellFactory(matNameEditableFactory);
					newColumn.setOnEditCommit(event -> {
						MatOrder editingOrder = event.getRowValue();
						try {
							editingOrder.setName(event.getNewValue());
							double unitPrice = DatabaseUtil.GetMatUnitPrice(event.getNewValue());
							editingOrder.setUnitPrice(unitPrice);
							editingOrder.setKgAmount();
							editingOrder.setTotalPrice();
							DatabaseUtil.UpdateMatOrder(editingOrder);
							matTableView.refresh();
						} catch (SQLException e) {
							AlertBox.display("错误", "编辑订单错误！");
							new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
									e.getMessage(), e.getStackTrace(), false);
						}
					});
				}
			}
		}

		// if backspace or delete, delete the order
		matTableView.setOnKeyPressed(keyEvent -> {
			if (keyEvent.getCode() == KeyCode.BACK_SPACE || keyEvent.getCode() == KeyCode.DELETE) {
				deleteMatOrder(matTableView.getSelectionModel().getSelectedItem());
			}
		});

		// able to select each cell
		matTableView.setEditable(true);
		matTableView.getSelectionModel().cellSelectionEnabledProperty().set(true);

		// filling the table
		matTableView.getColumns().setAll(orderColumnArrayList);
		matTableView.getItems().clear();
		matTableView.getItems().setAll(selectedMatOrders);
		matTableView.refresh();
	}

	/**
	 * initialize all labels and text fields for add mat order grid
	 */
	private void initAddMatOrder() {

		FinalConstants.setButtonImagesAndCursor(backButton, FinalConstants.backWhite, FinalConstants.backBlack);

		infoInputVBox.setMaxWidth(Double.MAX_VALUE);

		// setting up all the text field
		matOrderInputArray = new ArrayList<>();

		Node buttonHBox = infoInputVBox.getChildren().get(1);
		infoInputVBox.getChildren().remove(buttonHBox);

		for (int i = 0; i < FinalConstants.matTableHeaders.length; i++) {

			// type of mat, combo box
			if (i == 3) {
				JFXComboBox<String> newComboBox = new JFXComboBox<>();
				newComboBox.setPromptText("输入" + matHeaders[i]);
				newComboBox.getItems().setAll(FinalConstants.matOfType);
				newComboBox.setMaxWidth(Double.MAX_VALUE);
				newComboBox.getStyleClass().add("white-jfx-combo-box");
				matOrderInputArray.add(newComboBox);
				infoInputVBox.getChildren().add(newComboBox);
				newComboBox.setPadding(new Insets(0, 25, 0, 25));
//				VBox temp = new VBox(newLabel, newComboBox);
//				temp.setSpacing(5);
//				temp.setPadding(new Insets(0, 25, 0, 25));
//				infoInputVBox.getChildren().add(temp);
			} else if (i == FinalConstants.matPropertyHeaders.length - 1) {
				// seller, combo box
				JFXComboBox<String> sellerComboBox = new JFXComboBox<>();
				sellerComboBox.setPromptText("输入" + matHeaders[i]);
				// getting all the company names
				String[] allSellerCompany = new String[allMatSeller.size()];
				for (int j = 0; j < allMatSeller.size(); j++) {
					allSellerCompany[j] = allMatSeller.get(j).getCompanyName();
				}

				sellerComboBox.getItems().setAll(allSellerCompany);
				sellerComboBox.setMaxWidth(Double.MAX_VALUE);
				sellerComboBox.getStyleClass().add("white-jfx-combo-box");
				matOrderInputArray.add(sellerComboBox);
				infoInputVBox.getChildren().add(sellerComboBox);
				sellerComboBox.setPadding(new Insets(0, 25, 0, 25));
//				VBox temp = new VBox(newLabel, sellerComboBox);
//				temp.setPadding(new Insets(0, 25, 0, 25));
//				temp.setSpacing(5);
//				infoInputVBox.getChildren().add(temp);
			} else if (i == 0 || i == 4 || i == 5 || i == 6) {
				// dates, date picker
				DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
				JFXDatePicker datePicker = new JFXDatePicker();
				datePicker.setPromptText("输入" + matHeaders[i]);
				datePicker.setDefaultColor(Color.WHITE);
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
				datePicker.getStyleClass().add("white-jfx-date-picker");
				matOrderInputArray.add(datePicker);
				infoInputVBox.getChildren().add(datePicker);
				datePicker.setPadding(new Insets(0, 25, 0, 25));
//				VBox temp = new VBox(newLabel, datePicker);
//				temp.setPadding(new Insets(0, 25, 0, 25));
//				temp.setSpacing(5);
//				infoInputVBox.getChildren().add(temp);
			} else {
				// regular text field
				JFXTextField newTextField = new JFXTextField();
				newTextField.setPromptText("输入" + matHeaders[i]);
				// auto complete for name
				if (i == 2) {
					FinalConstants.updateAutoCompleteMatName();
					TextFields.bindAutoCompletion(newTextField, FinalConstants.autoCompleteMatName);
				}
				newTextField.setMaxWidth(Double.MAX_VALUE);
				newTextField.getStyleClass().add("white-jfx-text-field");
				matOrderInputArray.add(newTextField);
				infoInputVBox.getChildren().add(newTextField);
				newTextField.setPadding(new Insets(0, 25, 0, 25));
//				VBox temp = new VBox(newLabel, newTextField);
//				temp.setPadding(new Insets(0, 25, 0, 25));
//				temp.setSpacing(5);
//				infoInputVBox.getChildren().add(temp);
			}
		}

		TextField name = (TextField) matOrderInputArray.get(2);
		TextField unitPrice = (TextField) matOrderInputArray.get(11);
		name.textProperty().addListener((observableValue, oldValue, newValue) -> {
			try {
				if (DatabaseUtil.CheckIfNameExistsInMatUnitPrice(newValue))
					unitPrice.setText(String.valueOf(DatabaseUtil.GetMatUnitPrice(newValue)));
				else unitPrice.setText("0.0");
			} catch (SQLException ignored) {
			}
		});

		TextField unitAmount = (TextField) matOrderInputArray.get(8);
		TextField amount = (TextField) matOrderInputArray.get(9);
		TextField kgAmount = (TextField) matOrderInputArray.get(10);
		TextField totalPrice = (TextField) matOrderInputArray.get(12);

		unitAmount.textProperty().addListener((observable -> {
			try {
				kgAmount.setText(String.valueOf(Double.parseDouble(unitAmount.getText()) *
						Double.parseDouble(amount.getText())));
			} catch (Exception e) {
			}
		}));
		amount.textProperty().addListener((observable -> {
			try {
				kgAmount.setText(String.valueOf(Double.parseDouble(unitAmount.getText()) *
						Double.parseDouble(amount.getText())));
			} catch (Exception e) {
			}
		}));
		unitPrice.textProperty().addListener((observable -> {
			try {
				totalPrice.setText(String.valueOf(Double.parseDouble(unitPrice.getText()) *
						Double.parseDouble(kgAmount.getText())));
			} catch (Exception e) {
			}
		}));
		kgAmount.textProperty().addListener((observable -> {
			try {
				totalPrice.setText(String.valueOf(Double.parseDouble(unitPrice.getText()) *
						Double.parseDouble(kgAmount.getText())));
			} catch (Exception e) {
			}
		}));

		infoInputVBox.getChildren().add(buttonHBox);
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
			matTableView.getItems().setAll(FinalConstants.updateAllMatOrders());
			matTableView.refresh();
		} catch (SQLException e) {
			AlertBox.display("错误", "更新错误，联系管理员");
			new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
					e.getMessage(), e.getStackTrace(), false);
		}
	}

	/**
	 * Helper function to delete order
	 *
	 * @param selectedOrder the order to be deleted
	 */
	private void deleteMatOrder(MatOrder selectedOrder) {
		if (ConfirmBox.display("确认", "确定删除？", "是", "否")) {
			try {
				DatabaseUtil.DeleteMatOrder(selectedOrder.getSerialNum());
				matTableView.getItems().setAll(FinalConstants.updateAllMatOrders());
			} catch (SQLException e) {
				AlertBox.display("错误", "无法删除原料订单！");
				new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
						e.getMessage(), e.getStackTrace(), false);
			}
		}
	}

}
