import javafx.collections.ObservableList;
import javafx.fxml.*;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.util.Callback;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ResourceBundle;

public class MainScreenController implements Initializable  {

	// table headers
	private static final String[] tableHeaders = new String[] {"订单号", "原料名称", "类别", "订单日期", "付款日期",
			"到达日期", "发票日期", "发票编号", "规格", "数量", "公斤", "单价", "总价", "签收人", "供应商订单编号", "供应商",
			"联系人", "手机", "座机", "传真", "供应商账号", "供应商银行地址", "供应商地址", "备注"};

	// all property listed
	private static final String[] propertyHeaders = new String[]{"sku", "name", "type", "orderDate", "paymentDate",
			"arrivalDate", "invoiceDate", "invoice", "unitAmount", "amount", "kgAmount", "unitPrice", "totalPrice",
			"signed", "skuSeller", "company", "contact", "mobile", "land", "fax", "account",
			"bank", "address", "note"};

	private ObservableList<MatOrder> allMatOrders;

	@FXML TableView<ProductOrder> orderTableView;
	@FXML TableView<MatOrder> matTableView;
	@FXML Button searchButton;
	@FXML Button addButton;
	@FXML Button quitButton;

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		try {
			allMatOrders = DatabaseUtil.GetAllMatOrders();
			fillMatTable(allMatOrders);
		} catch (SQLException e) {
			e.printStackTrace();
			HandleError error = new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
					e.getMessage(), e.getStackTrace(), false);
			error.WriteToLog();
			AlertBox.display("错误", "无法读取数据！");
			return;
		}

		searchButton.setOnAction(event -> {
			System.out.println("search pressed");
		});

		addButton.setOnAction(event -> {
			addOrder();
		});
		quitButton.setOnAction(actionEvent -> {
			System.exit(1);
		});

	}

	/**
	 * Filling of the material table
	 * @param selectedMatOrders the orders specified
	 */
	private void fillMatTable(ObservableList<MatOrder> selectedMatOrders) {
		Collection<TableColumn<MatOrder, ?>> orderColumnArrayList = new ArrayList<>();

		TableColumn actionColumn = new TableColumn("动作");
		actionColumn.setSortable(false);
		orderColumnArrayList.add(actionColumn);

		for (int i = 0; i < tableHeaders.length; i++) {
			if (i == 8 || i == 9 || i == 10 || i == 11 || i == 12) {
				// Doubles
				TableColumn<MatOrder, Double> newColumn = new TableColumn<>(tableHeaders[i]);
				newColumn.setCellValueFactory(new PropertyValueFactory<>(propertyHeaders[i]));
				newColumn.setStyle( "-fx-alignment: CENTER;");
				orderColumnArrayList.add(newColumn);
			} else if (i == 3 || i == 4 || i == 5 || i == 6) {
				// Date
				TableColumn<MatOrder, Date> newColumn = new TableColumn<>(tableHeaders[i]);
				newColumn.setCellValueFactory(new PropertyValueFactory<>(propertyHeaders[i]));
				newColumn.setStyle("-fx-alignment: CENTER;");
				newColumn.setMinWidth(100);
				orderColumnArrayList.add(newColumn);
			} else if (i == 13) {
				// signed by increase column width
				TableColumn<MatOrder, String> newColumn = new TableColumn<>(tableHeaders[i]);
				newColumn.setCellValueFactory(new PropertyValueFactory<>(propertyHeaders[i]));
				newColumn.setStyle( "-fx-alignment: CENTER;");
				newColumn.setMinWidth(60);
				orderColumnArrayList.add(newColumn);
			}
			else {
				// String
				TableColumn<MatOrder, String> newColumn = new TableColumn<>(tableHeaders[i]);
				newColumn.setCellValueFactory(new PropertyValueFactory<>(propertyHeaders[i]));
				newColumn.setStyle( "-fx-alignment: CENTER;");
				orderColumnArrayList.add(newColumn);
			}
		}

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

		matTableView.setItems(selectedMatOrders);
		matTableView.getColumns().setAll(orderColumnArrayList);
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

	private void deleteMatOrder(MatOrder selectedOrder) {
		if (ConfirmBox.display("确认", "确定删除？", "确定", "删除")) {
			try {
				DatabaseUtil.DeleteMatOrder(selectedOrder.getSerialNum());
				allMatOrders.remove(selectedOrder);
			} catch (SQLException e) {
				AlertBox.display("错误", "无法删除");
				e.printStackTrace();
				HandleError error = new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
						e.getMessage(), e.getStackTrace(), false);
				error.WriteToLog();
			}
		}
	}

	private void modifyMatOrder(MatOrder selectedOrder) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("MatEditOrder.fxml"));
			Parent newScene = loader.load();
			Stage stage = new Stage();

			MatEditOrderController editOrderController = loader.getController();
			editOrderController.initData(selectedOrder, stage);

			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setTitle("编辑订单");
			stage.setScene(new Scene(newScene));
			stage.showAndWait();
			matTableView.getItems().removeAll();
			matTableView.getItems().setAll(DatabaseUtil.GetAllMatOrders());
			matTableView.refresh();
		} catch (Exception e) {
			AlertBox.display("错误", "窗口错误");
			e.printStackTrace();
			HandleError error = new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
					e.getMessage(), e.getStackTrace(), false);
			error.WriteToLog();
		}
	}

	private void addOrder() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("MatAddOrder.fxml"));
			Parent newScene = loader.load();
			Stage stage = new Stage();

			MatAddOrderController matAddOrderController = loader.getController();
			matAddOrderController.initData(stage);

			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setTitle("添加订单");
			stage.setScene(new Scene(newScene));
			stage.showAndWait();
			matTableView.getItems().removeAll();
			matTableView.getItems().setAll(DatabaseUtil.GetAllMatOrders());
			matTableView.refresh();
		} catch (Exception e) {
			AlertBox.display("错误", "窗口错误");
			e.printStackTrace();
			HandleError error = new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
					e.getMessage(), e.getStackTrace(), false);
			error.WriteToLog();
		}
	}

}
