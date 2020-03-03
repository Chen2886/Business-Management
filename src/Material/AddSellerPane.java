package Material;

import Main.DatabaseUtil;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.sql.SQLException;
import java.time.LocalDate;

public class AddSellerPane {
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
	private GridPane leftGridPane;
	private GridPane rightGridPane;

	private Region leftRegion;
	private Region rightRegion;

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

	public AddSellerPane() {

		leftRegion = new Region();
		HBox.setHgrow(leftRegion, Priority.ALWAYS);
		rightRegion = new Region();
		HBox.setHgrow(rightRegion, Priority.ALWAYS);

		leftVBox = new VBox();
		rightVBox = new VBox();
		leftGridPane = new GridPane();
		rightGridPane = new GridPane();
		buttonHBox = new HBox();
	}

	public void initScene() {
		leftVBox.setSpacing(10);
		leftVBox.setPadding(new Insets(20, 20, 20, 20));
		leftVBox.setAlignment(Pos.TOP_LEFT);
		rightVBox.setSpacing(10);
		rightVBox.setPadding(new Insets(20, 20, 20, 20));
		rightVBox.setAlignment(Pos.TOP_RIGHT);

		Button okButton = NewButton("添加供应商");
		okButton.setOnAction(e -> addSeller());

		Button clearButton = NewButton("清空");
		GridPane.setHalignment(clearButton, HPos.LEFT);
		clearButton.setOnAction(e -> clearFields());

		buttonHBox.setPadding(new Insets(10, 10, 10, 10));
		buttonHBox.setSpacing(10);
		buttonHBox.getChildren().addAll(clearButton, okButton);

		initLeftGrid();
		initRightGrid();

		infoEnterHBox = new HBox(leftRegion, leftVBox, rightVBox, rightRegion);
		infoEnterHBox.setSpacing(10);

		mainVBox = new VBox(infoEnterHBox);
		mainVBox.setSpacing(10);
//		mainVBox.setBorder(new Border(new BorderStroke(Color.BLACK,
//				BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1.5),
//				new Insets(10, 10, 10, 10))));
	}

	private void addSeller() {
		Seller seller = GetFieldVal();
		if (seller!=null) {
			try {
				DatabaseUtil.InsertToSeller("seller", seller);
				AlertBox.display("成功", "添加成功");
				clearFields();
			} catch (SQLException e) {
				AlertBox.display("错误", "添加失败");
				HandleError error = new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
						e.getMessage(), e.getStackTrace(), false);
				error.WriteToLog();
			}
		}
	}

	private Seller GetFieldVal() {

		// companyName
		String companyName = companyNameInput.getText();
		if (companyName.equals("")) {
			AlertBox.display("错误", "必须输入供应商名称");
			return null;
		}

		// contactName
		String contactName = contactNameInput.getText();
		if (contactName.equals("")) {
			AlertBox.display("错误", "必须输入联系人名称");
			return null;
		}

		// mobile
		String mobile = mobileInput.getText();

		// landLine
		String landLine = landLineInput.getText();

		// fax
		String fax = faxInput.getText();

		// accountNum
		String accountNum = accountNumInput.getText();

		// bankAddress
		String bankAddress = bankAddressInput.getText();

		// address
		String address = addressInput.getText();

		int sellerId = SellerId.GetSellerId();
		Seller seller = new Seller(sellerId, companyName, contactName);

		seller.setMobile(mobile);
		seller.setLandLine(landLine);
		seller.setFax(fax);
		seller.setAccountNum(accountNum.equals("") ? "" : accountNum);
		seller.setBankAddress(bankAddress.equals("") ? "" : bankAddress);
		seller.setAddress(address.equals("") ? "" : address);

		System.out.println(seller.toString());

		return seller;
	}

	private void initRightGrid() {

		rightGridPane.setPadding(new Insets(10, 10, 10, 10));
		rightGridPane.setVgap(8);
		rightGridPane.setHgap(10);

		Label rightHeader = new Label("选填内容");
		rightHeader.setMaxWidth(Double.MAX_VALUE);
		rightHeader.setAlignment(Pos.CENTER);
		rightHeader.setStyle(headerStyle);

		Label landLineLabel = NewLabel("座机:", 0, 0);
		landLineInput = NewTextField("输入座机", 1, 0);

		Label faxLabel = NewLabel("传真:", 0, 1);
		faxInput = NewTextField("输入传真", 1, 1);

		Label accountNumLabel = NewLabel("供应商账号:", 0, 2);
		accountNumInput = NewTextField("输入供应商账号", 1, 2);

		Label bankAddressLabel = NewLabel("开户行:", 0, 3);
		bankAddressInput = NewTextField("输入开户行", 1, 3);

		Label addressLabel = NewLabel("供应商地址:", 0, 4);
		addressInput = NewTextField("输入供应商地址", 1, 4);

		rightGridPane.getChildren().addAll(landLineLabel, landLineInput, faxLabel, faxInput, accountNumLabel, accountNumInput,
				bankAddressLabel, bankAddressInput, addressLabel, addressInput);
		rightVBox.getChildren().setAll(rightHeader, rightGridPane, buttonHBox);
		buttonHBox.setAlignment(Pos.BASELINE_RIGHT);
	}

	private void initLeftGrid() {

		leftGridPane.setPadding(new Insets(10, 10, 10, 10));
		leftGridPane.setVgap(8);
		leftGridPane.setHgap(10);

		Label leftHeader = new Label("必填内容");
		leftHeader.setMaxWidth(Double.MAX_VALUE);
		leftHeader.setAlignment(Pos.CENTER);
		leftHeader.setStyle(headerStyle);

		Label companyNameLabel = NewLabel("供应商名称:", 0, 0);
		companyNameInput = NewTextField("输入供应商名称", 1, 0);

		Label contactNameLabel = NewLabel("联系人名字:", 0, 1);
		contactNameInput = NewTextField("输入联系人名字", 1, 1);

		Label phoneLabel = NewLabel("手机:", 0, 2);
		mobileInput = NewTextField("输入手机", 1, 2);


		leftGridPane.getChildren().setAll(companyNameLabel, companyNameInput, contactNameLabel, contactNameInput,
				phoneLabel, mobileInput);

		leftVBox.getChildren().setAll(leftHeader, leftGridPane);
	}

	private void clearFields() {
		for (Node element: leftGridPane.getChildren()) {
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
		for (Node element: rightGridPane.getChildren()) {
			if (element instanceof TextField) {
				((TextField) element).clear();
			}
			else if (element instanceof ComboBox) {
				((ComboBox) element).getSelectionModel().clearSelection();
			}
			else if (element instanceof DatePicker) {
				((DatePicker) element).setValue(null);
			}
		}
	}


	public VBox getPane() {
		return mainVBox;
	}

	private Label NewLabel(String text, int col, int row) {
		Label returnLabel = new Label(text);
		returnLabel.setStyle(smallFont);
		leftGridPane.setConstraints(returnLabel, col, row);
		leftGridPane.setHalignment(returnLabel, HPos.RIGHT);
		return returnLabel;
	}

	private TextField NewTextField(String text, int col, int row) {
		TextField returnTextField = new TextField();
		returnTextField.setPromptText(text);
		returnTextField.setStyle(smallFont);
		leftGridPane.setConstraints(returnTextField, col, row);
		return returnTextField;
	}

	private Button NewButton(String text) {
		Button returnButton = new Button(text);
		returnButton.setStyle(smallFont);
		returnButton.setMaxWidth(Double.MAX_VALUE);
		return returnButton;
	}
}