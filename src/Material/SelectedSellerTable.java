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

public class SelectedSellerTable {

	private Seller selectedSeller;
	private TableView<Seller> sellerTableView;
	private VBox returnTableView;
	private Button deleteButton;
	private Button updateButton;

	private static final String smallFont = "-fx-font: 16 arial;";
	private static final String mediumFontSizeWithBorder = "-fx-font: 24 arial;\n" +
			"-fx-font-weight: bold;" +
			"-fx-border-width: 0 0 1 0;\n" +
			"-fx-border-color: black;\n";
	private static final String[] tableHeaders = new String[]{"供应商名称", "供应商联系人", "手机", "座机", "传真",
			"供应商账号", "供应商银行地址", "供应商地址"};
	private static final String[] propertyHeaders = new String[]{"companyName", "contactName", "mobile", "landLine", "fax", "accountNum",
			"bankAddress", "address"};

	public VBox CreateTable(ObservableList<Seller> selectedSeller) {
		Collection<TableColumn<Seller, ?>> sellerColumnArrayList = new ArrayList<>();

		for (int i = 0; i < tableHeaders.length; i++) {
			if (i==2 || i==3 || i==4) {
				// integers
				TableColumn<Seller, Integer> newColumn = new TableColumn<>(tableHeaders[i]);
				newColumn.setMinWidth(100);
				newColumn.setCellValueFactory(new PropertyValueFactory<>(propertyHeaders[i]));
				newColumn.setStyle("-fx-alignment: CENTER;");
				sellerColumnArrayList.add(newColumn);
			} else {
				// String
				TableColumn<Seller, Integer> newColumn = new TableColumn<>(tableHeaders[i]);
				newColumn.setMinWidth(100);
				newColumn.setCellValueFactory(new PropertyValueFactory<>(propertyHeaders[i]));
				newColumn.setStyle("-fx-alignment: CENTER;");
				sellerColumnArrayList.add(newColumn);
			}
		}

		sellerTableView = new TableView<>();
		sellerTableView.setItems(selectedSeller);
		sellerTableView.getColumns().setAll(sellerColumnArrayList);
		sellerTableView.setOnMouseClicked(e -> OnClick());

		returnTableView = new VBox();
		returnTableView.setPadding(new Insets(10, 10, 10, 10));
		returnTableView.setSpacing(10);

		deleteButton = new Button("删除供应商");
		deleteButton.setStyle(smallFont);

		updateButton = new Button("更新供应商");
		updateButton.setStyle(smallFont);
		updateButton.setOnAction(e -> updateSeller(sellerTableView.getSelectionModel().getSelectedItem()));

		HBox buttonHBox = new HBox(updateButton, deleteButton);
		buttonHBox.setSpacing(10);
		buttonHBox.setAlignment(Pos.CENTER_RIGHT);
		buttonHBox.setPadding(new Insets(10, 10, 10, 10));

		returnTableView.setAlignment(Pos.CENTER_RIGHT);
		returnTableView.getChildren().addAll(sellerTableView, buttonHBox);

		return returnTableView;
	}

	private void OnClick() {
		// check the table's selected item and get selected item
		try {
			selectedSeller = sellerTableView.getSelectionModel().getSelectedItem();
		} catch (Exception e) {
			HandleError error = new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
					e.getMessage(), e.getStackTrace(), true);
			error.WriteToLog();
		}
	}

	private Label NewLabelHeaders(String text) {
		Label returnLabel = new Label(text);
		returnLabel.setStyle(mediumFontSizeWithBorder);
		returnLabel.setMaxWidth(Double.MAX_VALUE);
		returnLabel.setAlignment(Pos.CENTER);
		return returnLabel;
	}

	public Seller getSelectedSeller() {
		return selectedSeller;
	}

	public Button getDeleteButton() {
		return deleteButton;
	}

	public Button getUpdateButton() {
		return updateButton;
	}

	public TableView<Seller> getSellerTableView() {
		return sellerTableView;
	}

	private void updateSeller(Seller selectedSellerFromTable) {
		NewInfoSeller newInfoSeller = new NewInfoSeller();
		Seller newSeller = newInfoSeller.display(selectedSellerFromTable);
		try {
			if (!ConfirmBox.display("确认", "确定更新？所有此供应商的订单也将被更新。", "是", "否")) {
				return;
			}
			DatabaseUtil.UpdateSellerInSeller(newSeller);
			DatabaseUtil.UpdateSellerInMain(newSeller);
			ObservableList<Seller> orderObservableList = sellerTableView.getItems();
			orderObservableList.set(orderObservableList.indexOf(selectedSellerFromTable), newSeller);
		}
		catch (SQLException e) {
			HandleError error = new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
					e.getMessage(), e.getStackTrace(), false);
			error.WriteToLog();
		}
	}
}
