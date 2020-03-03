package Material;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.util.ArrayList;

public class SearchOrUpdateSellerPane {

	private static final String headerStyle = "-fx-border-style: solid;\n" +
			"-fx-border-width: 0 0 1 0;\n" +
			"-fx-border-color: black;\n" +
			"-fx-font: 24 arial;";
	private static final String smallFont = "-fx-font: 16 arial;";

	// main scene component
	private VBox mainVBox;
	private HBox infoEnterHBox;
	private VBox leftVBox;
	private VBox rightVBox;
	private HBox buttonHBox;
	private GridPane sellerGridPane;

	private Region leftRegion;
	private Region rightRegion;

	private Button addButton;

	// left grid
	private TextField companyNameInput;
	private TextField contactNameInput;
	private TextField mobileInput;

	// right grid
	private TextField landLineInput;
	private TextField faxInput;
	private TextField accountNumInput;
	private TextField bankAddressInput;
	private TextField addressInput;

	// order
	private ArrayList<Seller> sellerArrayList;

	public SearchOrUpdateSellerPane() {
		leftRegion = new Region();
		HBox.setHgrow(leftRegion, Priority.ALWAYS);
		rightRegion = new Region();
		HBox.setHgrow(rightRegion, Priority.ALWAYS);

		leftVBox = new VBox();
		rightVBox = new VBox();
		sellerGridPane = new GridPane();
		buttonHBox = new HBox();
	}

	public void initScene() {
		leftVBox.setSpacing(10);
		leftVBox.setPadding(new Insets(20, 20, 20, 20));
		leftVBox.setAlignment(Pos.TOP_LEFT);
		rightVBox.setSpacing(10);
		rightVBox.setPadding(new Insets(20, 20, 20, 20));
		rightVBox.setAlignment(Pos.TOP_RIGHT);

		addButton = NewButton("搜索供应商");

		Button clearButton = NewButton("清空");
		GridPane.setHalignment(clearButton, HPos.LEFT);
		clearButton.setOnAction(e -> clearFields());

		buttonHBox.setPadding(new Insets(10, 10, 10, 10));
		buttonHBox.setSpacing(10);
		buttonHBox.getChildren().addAll(clearButton, addButton);
		buttonHBox.setAlignment(Pos.BOTTOM_RIGHT);

		initGrid();

		Label infoHeaders = new Label("选填任意内容");
		infoHeaders.setMaxWidth(Double.MAX_VALUE);
		infoHeaders.setAlignment(Pos.CENTER);
		infoHeaders.setPadding(new Insets(20, 10, 0, 10));
		infoHeaders.setStyle(headerStyle);

		VBox sellerGridPaneAndButtonVBox = new VBox(infoHeaders, sellerGridPane, buttonHBox);

		infoEnterHBox = new HBox(leftRegion, sellerGridPaneAndButtonVBox, rightRegion);
		infoEnterHBox.setSpacing(10);

		mainVBox = new VBox(infoEnterHBox);
		mainVBox.setSpacing(10);
	}

	private void initGrid() {

		sellerGridPane.setPadding(new Insets(10, 10, 10, 10));
		sellerGridPane.setVgap(8);
		sellerGridPane.setHgap(10);

		Label companyNameLabel = NewLabel("供应商名称:", 0, 0);
		companyNameInput = NewTextField("输入供应商名称", 1, 0);

		Label contactNameLabel = NewLabel("联系人名字:", 0, 1);
		contactNameInput = NewTextField("输入联系人名字", 1, 1);

		Label phoneLabel = NewLabel("手机:", 0, 2);
		mobileInput = NewTextField("输入手机", 1, 2);

		Label landLineLabel = NewLabel("座机:", 2, 0);
		landLineInput = NewTextField("输入座机", 3, 0);

		Label faxLabel = NewLabel("传真:", 2, 1);
		faxInput = NewTextField("输入传真", 3, 1);

		Label accountNumLabel = NewLabel("供应商账号:", 2, 2);
		accountNumInput = NewTextField("输入供应商账号", 3, 2);

		Label bankAddressLabel = NewLabel("开户行:", 2, 3);
		bankAddressInput = NewTextField("输入开户行", 3, 3);

		Label addressLabel = NewLabel("供应商地址:", 2, 4);
		addressInput = NewTextField("输入供应商地址", 3, 4);

		sellerGridPane.getChildren().setAll(companyNameLabel, companyNameInput, contactNameLabel, contactNameInput,
				phoneLabel, mobileInput, landLineLabel, landLineInput, faxLabel, faxInput, accountNumLabel,
				accountNumInput, bankAddressLabel, bankAddressInput, addressLabel, addressInput);
	}

	public void clearFields() {
		for (Node element: sellerGridPane.getChildren()) {
			if (element instanceof TextField) {
				((TextField) element).clear();
			}
			else if (element instanceof ComboBox) {
				((ComboBox) element).getSelectionModel().clearSelection();
			}
			else if (element instanceof DatePicker) {
				((DatePicker) element).setValue(LocalDate.now());
			}
		}
	}

	public VBox getPane() {
		return mainVBox;
	}

	public String GetFieldVal() {
		String returnVal = "WHERE ";
		ArrayList<String> SQLCommand = new ArrayList<>();

		// companyName
		String companyName = companyNameInput.getText();
		if (!companyName.equals("")) {
			SQLCommand.add(String.format("%s = '%s'", "companyName", companyName));
		}

		// contactName
		String contactName = contactNameInput.getText();
		if (!contactName.equals("")) {
			SQLCommand.add(String.format("%s = '%s'", "contactName", contactName));
		}

		// mobile
		String mobile = mobileInput.getText();
		if (!mobile.equals("")) {
			SQLCommand.add(String.format("%s = '%s'", "mobile", mobile));
		}

		// landLine
		String landLine = landLineInput.getText();
		if (!landLine.equals("")) {
			SQLCommand.add(String.format("%s = '%s'", "landLine", landLine));
		}

		// fax
		String fax = faxInput.getText();
		if (!fax.equals("")) {
			SQLCommand.add(String.format("%s = '%s'", "landLine", landLine));
		}

		// accountNum
		String accountNum = accountNumInput.getText();
		if (!accountNum.equals("")) {
			SQLCommand.add(String.format("%s = '%s'", "accountNum", accountNum));
		}

		// bankAddress
		String bankAddress = bankAddressInput.getText();
		if (!bankAddress.equals("")) {
			SQLCommand.add(String.format("%s = '%s'", "bankAddress", bankAddress));
		}

		// address
		String address = addressInput.getText();
		if (!address.equals("")) {
			SQLCommand.add(String.format("%s = '%s'", "address", address));
		}

		if (SQLCommand.size() == 0) {
			return "";
		}
		else {
			for (int i = 0; i < SQLCommand.size(); i++) {
				if (i == SQLCommand.size() - 1) {
					returnVal += SQLCommand.get(i);
				}
				else {
					returnVal += SQLCommand.get(i) + " AND ";
				}
			}
		}

		return returnVal;
	}

	private Label NewLabel(String text, int col, int row) {
		Label returnLabel = new Label(text);
		returnLabel.setStyle(smallFont);
		GridPane.setConstraints(returnLabel, col, row);
		GridPane.setHalignment(returnLabel, HPos.RIGHT);
		return returnLabel;
	}

	private TextField NewTextField(String text, int col, int row) {
		TextField returnTextField = new TextField();
		returnTextField.setPromptText(text);
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

	public Button getAddButton() {
		return addButton;
	}
}