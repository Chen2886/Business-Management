package Main;

// from my other packages
import Material.*;
import Product.*;

import javafx.collections.*;
import javafx.fxml.*;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.util.Callback;

import java.io.FileInputStream;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ResourceBundle;


public class MainScreen implements Initializable {

	// mat table headers
	private static final String[] matHeaders = new String[] {"订单日期", "订单号", "原料名称", "类别",  "付款日期",
			"到达日期", "发票日期", "发票编号", "规格", "数量", "公斤", "单价", "总价", "签收人", "供应商订单编号", "供应商",
			"联系人", "手机", "座机", "传真", "供应商账号", "供应商银行地址", "供应商地址", "备注"};

	// all mat property listed
	private static final String[] matProperty = new String[]{"orderDate", "sku", "name", "type",  "paymentDate",
			"arrivalDate", "invoiceDate", "invoice", "unitAmount", "amount", "kgAmount", "unitPrice", "totalPrice",
			"signed", "skuSeller", "company", "contact", "mobile", "land", "fax", "account",
			"bank", "address", "note"};

	// prod table headers
	// TODO: Add Formula
	private static final String[] prodHeaders = new String[] {"订单日期", "送货单号", "客户", "产品名称",
			"规格", "数量", "公斤", "单价", "金额", "成本价", "备注"};

	// all prod property listed
	private static final String[] prodProperty = new String[]{"orderDate", "sku", "customer", "name",
			"unitAmount", "amount", "kgAmount", "unitPrice", "totalPrice", "basePrice", "note"};

    private ObservableList<MatOrder> allMatOrderList;
	private ObservableList<MatOrder> tempQuickSearchMatOrderList;

	private ObservableList<ProductOrder> allProdOrderList;
	private ObservableList<ProductOrder> tempQuickSearchProdOrderList;

	@FXML TabPane mainTabPane;
	@FXML Tab matTab;
	@FXML Tab prodTab;
	@FXML TableView<ProductOrder> prodTableView;
	@FXML TableView<MatOrder> matTableView;
	@FXML Button searchButton;
	@FXML Button addButton;
	@FXML Button quitButton;
	@FXML Button resetButton;
	@FXML Button excelButton;
	@FXML TextField searchBarTextField;
	@FXML ImageView searchImageView;

	/**
	 * Call to fill the table with all orders, set up actions for all buttons, set up search bars, set up image view
	 * @param url N/A
	 * @param resourceBundle N/A
	 */
	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {

		resetButton.setText("重置\n表格");
		searchButton.setText("精确\n搜索");
		addButton.setText("添加\n编辑");
		excelButton.setText("生成\n表格");

		// setting up the image for search bar
		try {
			FileInputStream input = new FileInputStream("src/iconmonstr-magnifier-4-240.png");
			Image searchBarImage = new Image(input);
			searchImageView.setImage(searchBarImage);
		} catch (Exception e) {
			e.printStackTrace();
			HandleError error = new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
					e.getMessage(), e.getStackTrace(), false);
			error.WriteToLog();
		}

		// filling the mat table

		try {
			allMatOrderList = DatabaseUtil.GetAllMatOrders();
			tempQuickSearchMatOrderList = FXCollections.observableArrayList();
			tempQuickSearchMatOrderList.addAll(allMatOrderList);
			fillMatTable(allMatOrderList);
			allProdOrderList = DatabaseUtil.GetAllProdOrders();
			tempQuickSearchProdOrderList = FXCollections.observableArrayList();
			tempQuickSearchProdOrderList.addAll(allProdOrderList);
			fillProdTable(allProdOrderList);
		} catch (SQLException e) {
			e.printStackTrace();
			HandleError error = new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
					e.getMessage(), e.getStackTrace(), false);
			error.WriteToLog();
			AlertBox.display("错误", "无法读取数据！");
		}

		// precision search mat/prod orders
		searchButton.setOnAction(event -> {
			// if selected tab is material
			if (mainTabPane.getSelectionModel().getSelectedItem().equals(matTab)) searchMatOrder();
			// if selected tab is product
			else searchProdOrder();
		});

		// add mat/prod orders
		addButton.setOnAction(event -> {
			// if selected tab is material
			if (mainTabPane.getSelectionModel().getSelectedItem().equals(matTab)) addMatOrder();
			// if selected tab is product
			else addProdOrder();
		});

		// quit the application
		quitButton.setOnAction(actionEvent -> System.exit(1));

		// reset both table
		resetButton.setOnAction(actionEvent -> resetTable());

		// excel button
		excelButton.setOnAction(event -> {
			// if selected tab is material
			if (mainTabPane.getSelectionModel().getSelectedItem().equals(matTab)) generateMatExcel();
				// if selected tab is product
			else generateProdExcel();
		});

		// listener for search bar text field
		searchBarTextField.textProperty().addListener((observableValue, oldValue, newValue) -> {

			// if selected tab is material
			if (mainTabPane.getSelectionModel().getSelectedItem().equals(matTab)) {

				// if the text field is updated to be empty
				if (newValue == null || newValue.equals("")) {
					matTableView.getItems().clear();
					tempQuickSearchMatOrderList = FXCollections.observableArrayList(allMatOrderList);
				} else {
					// if user deleted char, copying original array
					if (newValue.length() < oldValue.length()) {
						matTableView.getItems().clear();
						tempQuickSearchMatOrderList = FXCollections.observableArrayList(allMatOrderList);
					}

					// removing orders that doesn't contain key word
					tempQuickSearchMatOrderList.removeIf(matOrder -> !matOrder.toString().contains(newValue));
				}
				matTableView.setItems(tempQuickSearchMatOrderList);
			}

			// if selected tab is order
			else {

				// if the text field is updated to be empty
				if (newValue == null || newValue.equals("")) {
					prodTableView.getItems().clear();
					tempQuickSearchProdOrderList = FXCollections.observableArrayList(allProdOrderList);
				} else {
					// if user deleted char, copying original array
					if (newValue.length() < oldValue.length()) {
						prodTableView.getItems().clear();
						tempQuickSearchProdOrderList = FXCollections.observableArrayList(allProdOrderList);
					}

					// removing orders that doesn't contain key word
					tempQuickSearchProdOrderList.removeIf(productOrder -> !productOrder.toString().contains(newValue));
				}
				prodTableView.setItems(tempQuickSearchProdOrderList);
			}
		});
	}

	/**
	 * Function to generate excel file with material
	 */
	private void generateMatExcel() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("../Material/MatGenerateExcel.fxml"));
			Parent newScene = loader.load();
			Stage stage = new Stage();

			MatGenerateExcel matGenerateExcel = loader.getController();
			matGenerateExcel.initData(stage);
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setTitle("生成表格");
			stage.setScene(new Scene(newScene));
			stage.show();
		} catch (Exception e) {
			AlertBox.display("错误", "窗口错误");
			e.printStackTrace();
			HandleError error = new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
					e.getMessage(), e.getStackTrace(), false);
			error.WriteToLog();
		}
	}

	/**
	 * Function to generate excel file with product
	 */
	private void generateProdExcel() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("../Product/ProdGenerateExcel.fxml"));
			Parent newScene = loader.load();
			Stage stage = new Stage();

			ProdGenerateExcel prodGenerateExcel = loader.getController();
			prodGenerateExcel.initData(stage);
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setTitle("生成表格");
			stage.setScene(new Scene(newScene));
			stage.show();
		} catch (Exception e) {
			AlertBox.display("错误", "窗口错误");
			e.printStackTrace();
			HandleError error = new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
					e.getMessage(), e.getStackTrace(), false);
			error.WriteToLog();
		}
	}

	/**
	 * Filling of the material table
	 * @param selectedMatOrders the orders specified
	 */
	public void fillMatTable(ObservableList<MatOrder> selectedMatOrders) {

		// array of columns
		Collection<TableColumn<MatOrder, ?>> orderColumnArrayList = new ArrayList<>();

		// setting up first action column
		TableColumn actionColumn = new TableColumn("动作");
		actionColumn.setSortable(false);
		orderColumnArrayList.add(actionColumn);

		// loop to set up all regular columns
		for (int i = 0; i < matHeaders.length; i++) {
			if (i == 8 || i == 9 || i == 10 || i == 11 || i == 12) {
				// Doubles
				TableColumn<MatOrder, Double> newColumn = new TableColumn<>(matHeaders[i]);
				newColumn.setCellValueFactory(new PropertyValueFactory<>(matProperty[i]));
				newColumn.setStyle( "-fx-alignment: CENTER;");
				orderColumnArrayList.add(newColumn);
			} else if (i == 0 || i == 4 || i == 5 || i == 6) {
				// Main.Date
				TableColumn<MatOrder, Date> newColumn = new TableColumn<>(matHeaders[i]);
				newColumn.setCellValueFactory(new PropertyValueFactory<>(matProperty[i]));
				newColumn.setStyle("-fx-alignment: CENTER;");
				newColumn.setMinWidth(110);
				orderColumnArrayList.add(newColumn);
			} else if (i == 13) {
				// signed by increase column width
				TableColumn<MatOrder, String> newColumn = new TableColumn<>(matHeaders[i]);
				newColumn.setCellValueFactory(new PropertyValueFactory<>(matProperty[i]));
				newColumn.setStyle( "-fx-alignment: CENTER;");
				newColumn.setMinWidth(60);
				orderColumnArrayList.add(newColumn);
			}
			else {
				// String
				TableColumn<MatOrder, String> newColumn = new TableColumn<>(matHeaders[i]);
				newColumn.setCellValueFactory(new PropertyValueFactory<>(matProperty[i]));
				newColumn.setStyle( "-fx-alignment: CENTER;");
				orderColumnArrayList.add(newColumn);
			}
		}

		// Setting a call back to handle the first column of action buttons
		Callback<TableColumn<MatOrder, String>, TableCell<MatOrder, String>> cellFactory = new Callback<>() {
					@Override
					public TableCell<MatOrder, String> call(TableColumn<MatOrder, String> matOrderStringTableColumn) {
						TableCell<MatOrder, String> cell = new TableCell<>() {
							// define new buttons
							Button edit = new Button("编辑");
							Button delete = new Button("删除");
							HBox actionButtons = new HBox(edit, delete);

							@Override
							public void updateItem(String item, boolean empty) {
								super.updateItem(item, empty);
								if (empty) {
									setGraphic(null);
								} else {
									edit.setOnAction(event -> modifyMatOrder(getTableView().getItems().get(getIndex())));
									delete.setOnAction(event -> deleteMatOrder(getTableView().getItems().get(getIndex())));

									edit.getStyleClass().add("actionButtons");
									delete.getStyleClass().add("actionButtons");
									actionButtons.setSpacing(5);
									actionButtons.setAlignment(Pos.CENTER);
									setGraphic(actionButtons);
								}
								setText(null);
							}
						};
						return cell;
					}
				};

		actionColumn.setCellFactory(cellFactory);

		// filling the table
		matTableView.setItems(selectedMatOrders);
		matTableView.getColumns().setAll(orderColumnArrayList);

		// if double clicked, enable edit
		matTableView.setRowFactory( tv -> {
			TableRow<MatOrder> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
					MatOrder order = row.getItem();
					modifyMatOrder(order);
				}
			});
			return row;
		});
	}

	/**
	 * Filling of the product table
	 * @param selectedProdOrders the orders specified
	 */
	public void fillProdTable(ObservableList<ProductOrder> selectedProdOrders) {
		// array of columns
		Collection<TableColumn<ProductOrder, ?>> productColumnArrayList = new ArrayList<>();

		// setting up first action column
		TableColumn actionColumn = new TableColumn("动作");
		actionColumn.setSortable(false);
		productColumnArrayList.add(actionColumn);

		// loop to set up all regular columns
		for (int i = 0; i < prodHeaders.length; i++) {
			if (i == 4 || i == 5 || i == 6 || i == 7 || i == 8 || i == 9) {
				// Doubles
				TableColumn<ProductOrder, Double> newColumn = new TableColumn<>(prodHeaders[i]);
				newColumn.setCellValueFactory(new PropertyValueFactory<>(prodProperty[i]));
				newColumn.setStyle( "-fx-alignment: CENTER;");
				newColumn.setMinWidth(100);
				productColumnArrayList.add(newColumn);
			} else if (i == 0) {
				// Main.Date
				TableColumn<ProductOrder, Date> newColumn = new TableColumn<>(prodHeaders[i]);
				newColumn.setCellValueFactory(new PropertyValueFactory<>(prodProperty[i]));
				newColumn.setStyle("-fx-alignment: CENTER;");
				newColumn.setMinWidth(110);
				productColumnArrayList.add(newColumn);
			} else {
				// String
				TableColumn<ProductOrder, String> newColumn = new TableColumn<>(prodHeaders[i]);
				newColumn.setCellValueFactory(new PropertyValueFactory<>(prodProperty[i]));
				newColumn.setStyle( "-fx-alignment: CENTER;");
				newColumn.setMinWidth(100);
				productColumnArrayList.add(newColumn);
			}
		}

		// Setting a call back to handle the first column of action buttons
		Callback<TableColumn<ProductOrder, String>, TableCell<ProductOrder, String>> cellFactory = new Callback<>() {
			@Override
			public TableCell<ProductOrder, String> call(TableColumn<ProductOrder, String> matOrderStringTableColumn) {
				TableCell<ProductOrder, String> cell = new TableCell<>() {
					// define new buttons
					Button edit = new Button("编辑");
					Button delete = new Button("删除");
					Button formula = new Button("配方");
					HBox actionButtons = new HBox(edit, delete, formula);

					@Override
					public void updateItem(String item, boolean empty) {
						super.updateItem(item, empty);
						if (empty) {
							setGraphic(null);
						} else {
							edit.setOnAction(event -> modifyProdOrder(getTableView().getItems().get(getIndex())));
							delete.setOnAction(event -> deleteProdOrder(getTableView().getItems().get(getIndex())));
							formula.setOnAction(event -> prodFormula(getTableView().getItems().get(getIndex())));
							edit.getStyleClass().add("actionButtons");
							delete.getStyleClass().add("actionButtons");
							formula.getStyleClass().add("actionButtons");
							actionButtons.setSpacing(5);
							actionButtons.setAlignment(Pos.CENTER);
							setGraphic(actionButtons);
						}
						setText(null);
					}
				};
				return cell;
			}
		};

		actionColumn.setCellFactory(cellFactory);

		// filling the table
		prodTableView.setItems(selectedProdOrders);
		prodTableView.getColumns().setAll(productColumnArrayList);

		// if double clicked, enable edit
		prodTableView.setRowFactory( tv -> {
			TableRow<ProductOrder> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
					ProductOrder order = row.getItem();
					modifyProdOrder(order);
				}
			});
			return row;
		});
	}

	/**
	 * Helper function to set up window to add a mat order
	 */
	private void addMatOrder() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("../Material/MatAddOrderModifySeller.fxml"));
			Parent newScene = loader.load();
			Stage stage = new Stage();

			MatAddOrderModifySeller matAddOrderModifySeller = loader.getController();
			matAddOrderModifySeller.initData(stage);

			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setTitle("添加订单");
			stage.setScene(new Scene(newScene));
			stage.showAndWait();
			matTableView.getItems().clear();
			allMatOrderList = DatabaseUtil.GetAllMatOrders();
			matTableView.getItems().setAll(allMatOrderList);
			matTableView.refresh();
		} catch (Exception e) {
			AlertBox.display("错误", "窗口错误");
			e.printStackTrace();
			HandleError error = new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
					e.getMessage(), e.getStackTrace(), false);
			error.WriteToLog();
		}
	}

	/**
	 * Helper function to set up window to add a prod order
	 */
	private void addProdOrder() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("../Product/ProdAddOrder.fxml"));
			Parent newScene = loader.load();
			Stage stage = new Stage();

			ProdAddOrder prodAddOrder = loader.getController();
			prodAddOrder.initData(stage);

			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setTitle("添加产品");
			stage.setScene(new Scene(newScene));
			stage.showAndWait();
			prodTableView.getItems().clear();
			allProdOrderList = DatabaseUtil.GetAllProdOrders();
			prodTableView.getItems().setAll(allProdOrderList);
			prodTableView.refresh();
		} catch (Exception e) {
			AlertBox.display("错误", "窗口错误");
			e.printStackTrace();
			HandleError error = new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
					e.getMessage(), e.getStackTrace(), false);
			error.WriteToLog();
		}
	}

	/**
	 * Helper function set up new window to modify order
	 * @param selectedOrder the order to be updated
	 */
	private void modifyMatOrder(MatOrder selectedOrder) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("../Material/MatEditOrder.fxml"));
			Parent newScene = loader.load();
			Stage stage = new Stage();

			MatEditOrder editOrderController = loader.getController();
			editOrderController.initData(selectedOrder, stage);

			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setTitle("编辑订单");
			stage.setScene(new Scene(newScene));
			stage.showAndWait();
			matTableView.getItems().clear();
			allMatOrderList = DatabaseUtil.GetAllMatOrders();
			matTableView.getItems().setAll(allMatOrderList);
			matTableView.refresh();
		} catch (Exception e) {
			AlertBox.display("错误", "窗口错误");
			e.printStackTrace();
			HandleError error = new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
					e.getMessage(), e.getStackTrace(), false);
			error.WriteToLog();
		}
	}

	/**
	 * Helper function set up new window to modify order
	 * @param selectedOrder the order to be updated
	 */
	private void modifyProdOrder(ProductOrder selectedOrder) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("../Product/ProdEditOrder.fxml"));
			Parent newScene = loader.load();
			Stage stage = new Stage();

			ProdEditOrder prodEditOrder = loader.getController();
			prodEditOrder.initData(selectedOrder, stage);

			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setTitle("编辑订单");
			stage.setScene(new Scene(newScene));
			stage.showAndWait();
			prodTableView.getItems().clear();
			allProdOrderList = DatabaseUtil.GetAllProdOrders();
			prodTableView.getItems().setAll(allProdOrderList);
			prodTableView.refresh();
		} catch (Exception e) {
			AlertBox.display("错误", "窗口错误");
			e.printStackTrace();
			HandleError error = new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
					e.getMessage(), e.getStackTrace(), false);
			error.WriteToLog();
		}
	}

	/**
	 * Helper function to delete order
	 * @param selectedOrder the order to be deleted
	 */
	private void deleteMatOrder(MatOrder selectedOrder) {
		if (ConfirmBox.display("确认", "确定删除？", "确定", "取消")) {
			try {
				DatabaseUtil.DeleteMatOrder(selectedOrder.getSerialNum());
				allMatOrderList = DatabaseUtil.GetAllMatOrders();
				matTableView.getItems().clear();
				matTableView.getItems().setAll(allMatOrderList);
			} catch (SQLException e) {
				AlertBox.display("错误", "无法删除");
				e.printStackTrace();
				HandleError error = new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
						e.getMessage(), e.getStackTrace(), false);
				error.WriteToLog();
			}
		}
	}

	/**
	 * Helper function to delete order
	 * @param selectedOrder the order to be deleted
	 */
	private void deleteProdOrder(ProductOrder selectedOrder) {
		if (ConfirmBox.display("确认", "确定删除？", "确定", "取消")) {
			try {
				DatabaseUtil.DeleteProdOrder(selectedOrder.getSerialNum());
				allProdOrderList = DatabaseUtil.GetAllProdOrders();
				prodTableView.getItems().clear();
				prodTableView.getItems().setAll(allProdOrderList);
			} catch (SQLException e) {
				AlertBox.display("错误", "无法删除");
				e.printStackTrace();
				HandleError error = new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
						e.getMessage(), e.getStackTrace(), false);
				error.WriteToLog();
			}
		}
	}

	/**
	 * Helper function to set up window for advance/precision searching of mat order
	 */
	private void searchMatOrder() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("../Material/MatSearchOrder.fxml"));
			Parent newScene = loader.load();
			Stage stage = new Stage();

			MatSearchOrder matSearchOrder = loader.getController();
			matSearchOrder.initData(stage, this);
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setTitle("搜索订单");
			stage.setScene(new Scene(newScene));
			stage.show();
		} catch (Exception e) {
			AlertBox.display("错误", "窗口错误");
			e.printStackTrace();
			HandleError error = new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
					e.getMessage(), e.getStackTrace(), false);
			error.WriteToLog();
		}
	}

	/**
	 * Helper function to set up window for advance/precision searching of prod order
	 */
	private void searchProdOrder() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("../Product/ProdSearchOrder.fxml"));
			Parent newScene = loader.load();
			Stage stage = new Stage();

			ProdSearchOrder prodSearchOrder = loader.getController();
			prodSearchOrder.initData(stage, this);
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setTitle("搜索产品");
			stage.setScene(new Scene(newScene));
			stage.show();
		} catch (Exception e) {
			AlertBox.display("错误", "窗口错误");
			e.printStackTrace();
			HandleError error = new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
					e.getMessage(), e.getStackTrace(), false);
			error.WriteToLog();
		}
	}

	/**
	 * Helper function to reset the table to all orders
	 */
	private void resetTable() {
		// TODO: reset order table
		try {
			searchBarTextField.setText("");

			matTableView.getItems().clear();
			allMatOrderList = DatabaseUtil.GetAllMatOrders();
			matTableView.getItems().setAll(allMatOrderList);
			matTableView.refresh();

			prodTableView.getItems().clear();
			allProdOrderList = DatabaseUtil.GetAllProdOrders();
			prodTableView.getItems().setAll(allProdOrderList);
			prodTableView.refresh();
		} catch (Exception e) {
			AlertBox.display("错误", "无法摘取信息");
			e.printStackTrace();
			HandleError error = new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
					e.getMessage(), e.getStackTrace(), false);
			error.WriteToLog();
		}
	}

	/**
	 * Public function for other controller to call, to set the table with new list
	 * @param newList the search result list
	 */
	public void setMatSearchList(ObservableList<MatOrder> newList) {
		ObservableList<MatOrder> searchList = FXCollections.observableArrayList(newList);
		matTableView.getItems().clear();
		matTableView.getItems().setAll(searchList);
	}

	/**
	 * Public function for other controller to call, to set the table with new list
	 * @param newList the search result list
	 */
	public void setProdSearchList(ObservableList<ProductOrder> newList) {
		ObservableList<ProductOrder> searchList = FXCollections.observableArrayList(newList);
		prodTableView.getItems().clear();
		prodTableView.getItems().setAll(searchList);
	}

	public void prodFormula(ProductOrder order) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("../Product/ProdFormula.fxml"));
			Parent newScene = loader.load();
			Stage stage = new Stage();

			ProdFormula prodFormula = loader.getController();
			prodFormula.initData(order, stage);
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setTitle("配方");
			stage.setScene(new Scene(newScene));
			stage.show();
		} catch (Exception e) {
			AlertBox.display("错误", "窗口错误");
			e.printStackTrace();
			HandleError error = new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
					e.getMessage(), e.getStackTrace(), false);
			error.WriteToLog();
		}
	}

}
