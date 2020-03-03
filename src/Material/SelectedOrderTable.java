package Material;

import Main.DatabaseUtil;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class SelectedOrderTable {
    private Order selectedOrder;
    private TableView<Order> orderTableView;
    private VBox returnTableView;
    private Button deleteButton;
    private Button updateButton;

    private static final String smallFont = "-fx-font: 16 arial;";
    private static final String mediumFontSizeWithBorder = "-fx-font: 24 arial;\n" +
            "-fx-font-weight: bold;\n" +
            "-fx-border-width: 0 0 1 0;\n" +
            "-fx-border-color: black;\n";
    private static final String[] tableHeaders = new String[]{"订单编号", "原料名称", "原料类别", "订单日期", "付款日期",
            "到达日期", "发票日期", "发票编号", "规格", "数量", "公斤", "单价", "总价", "签收人", "供应商订单编号", "供应商名称",
            "供应商联系人", "手机", "座机", "传真", "供应商账号", "供应商银行地址", "供应商地址", "备注"};
    private static final String[] propertyHeaders = new String[]{"sku", "name", "type", "orderDate", "paymentDate",
            "arrivalDate", "invoiceDate", "invoice", "unitAmount", "amount", "kgAmount", "unitPrice", "totalPrice",
            "signed", "skuSeller", "companyName", "contactName", "mobile", "landLine", "fax", "accountNum",
            "bankAddress", "address", "note"};

    /**
     * Create a new VBox with table of result
     * @param selectedOrders order in VBox
     * @return VBox with table
     */
    public VBox CreateTable(ObservableList<Order> selectedOrders) {
        Collection<TableColumn<Order, ?>> orderColumnArrayList = new ArrayList<>();

        for (int i = 0; i < tableHeaders.length; i++) {
            if (i == 0 || i == 8 || i == 9 || i == 17 || i == 18 || i == 19) {
                // integers
                TableColumn<Order, Integer> newColumn = new TableColumn<>(tableHeaders[i]);
                newColumn.setMinWidth(100);
                newColumn.setCellValueFactory(new PropertyValueFactory<>(propertyHeaders[i]));
                newColumn.setStyle( "-fx-alignment: CENTER;");
                orderColumnArrayList.add(newColumn);
            }
            else if (i == 10 || i == 11 || i == 12) {
                // doubles
                TableColumn<Order, Integer> newColumn = new TableColumn<>(tableHeaders[i]);
                newColumn.setMinWidth(100);
                newColumn.setCellValueFactory(new PropertyValueFactory<>(propertyHeaders[i]));
                newColumn.setStyle( "-fx-alignment: CENTER;");
                orderColumnArrayList.add(newColumn);
            }
            else if (i == 3 || i == 4 || i == 5 || i == 6) {
                // date
                TableColumn<Order, Integer> newColumn = new TableColumn<>(tableHeaders[i]);
                newColumn.setMinWidth(100);
                newColumn.setCellValueFactory(new PropertyValueFactory<>(propertyHeaders[i]));
                newColumn.setStyle( "-fx-alignment: CENTER;");
                orderColumnArrayList.add(newColumn);
            }
            else {
                // String
                TableColumn<Order, Integer> newColumn = new TableColumn<>(tableHeaders[i]);
                newColumn.setMinWidth(100);
                newColumn.setCellValueFactory(new PropertyValueFactory<>(propertyHeaders[i]));
                newColumn.setStyle( "-fx-alignment: CENTER;");
                orderColumnArrayList.add(newColumn);
            }
        }

        orderTableView = new TableView<>();
        orderTableView.setItems(selectedOrders);
        orderTableView.getColumns().setAll(orderColumnArrayList);
        orderTableView.setOnMouseClicked(e -> OnClick());

        returnTableView = new VBox();
        returnTableView.setPadding(new Insets(10, 10, 10, 10));
        returnTableView.setSpacing(10);

        deleteButton = new Button("删除订单");
        deleteButton.setStyle(smallFont);

        updateButton = new Button("更新订单");
        updateButton.setStyle(smallFont);
        updateButton.setOnAction(e -> UpdateOrder(orderTableView.getSelectionModel().getSelectedItem()));

        HBox buttonHBox = new HBox(updateButton, deleteButton);
        buttonHBox.setSpacing(10);
        buttonHBox.setAlignment(Pos.CENTER_RIGHT);
        buttonHBox.setPadding(new Insets(10, 10, 10, 10));

        returnTableView.setAlignment(Pos.CENTER_RIGHT);
        returnTableView.getChildren().addAll(orderTableView, buttonHBox);

        return returnTableView;
    }

    private void UpdateOrder(Order selectedOrderFromTable) {
        NewInfoOrder newInfoOrder = new NewInfoOrder();
        Order newOrder = newInfoOrder.display(selectedOrderFromTable);
        if (newOrder == null) return;
        try {
            if (!ConfirmBox.display("确认", "确定更新？", "是", "否")) {
                return;
            }
            DatabaseUtil.UpdateOrderInMain(newOrder);
            ObservableList<Order> orderObservableList = orderTableView.getItems();
            orderObservableList.set(orderObservableList.indexOf(selectedOrderFromTable), newOrder);
        }
        catch (SQLException e) {
            HandleError error = new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            error.WriteToLog();
        }
    }

    /**
     * Get order selected
     */
    private void OnClick() {
        // check the table's selected item and get selected item
        try {
            selectedOrder = orderTableView.getSelectionModel().getSelectedItem();
        }
        catch (Exception e) {
            HandleError error = new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), true);
            error.WriteToLog();
        }
    }

    /**
     * create a label
     * @param text label with text
     * @return label with text
     */
    private Label NewLabelHeaders(String text) {
        Label returnLabel = new Label(text);
        returnLabel.setStyle(mediumFontSizeWithBorder);
        returnLabel.setMaxWidth(Double.MAX_VALUE);
        returnLabel.setAlignment(Pos.CENTER);
        return returnLabel;
    }

    public TableView<Order> getOrderTableView() {
        return orderTableView;
    }

    public Button getDeleteButton() {
        return deleteButton;
    }

    public Order getSelectedOrder() {
        return selectedOrder;
    }
}
