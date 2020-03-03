package Material;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class NewInfoSeller {

	//Create variable
	Seller seller;
	int sellerId;

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

	private static final String mediumFont = "-fx-font: 18 arial;";
	private static final String headerStyle = "-fx-border-style: solid;\n" +
			"-fx-border-width: 0 0 1 0;\n" +
			"-fx-border-color: black;\n" +
			"-fx-font: 24 arial;";
	private static final String smallFont = "-fx-font: 16 arial;";

	public NewInfoSeller() {
		leftRegion = new Region();
		HBox.setHgrow(leftRegion, Priority.ALWAYS);
		rightRegion = new Region();
		HBox.setHgrow(rightRegion, Priority.ALWAYS);
		mainVBox = new VBox();
		infoEnterHBox = new HBox();
		leftGridPane = new GridPane();
		rightGridPane = new GridPane();
		leftVBox = new VBox();
		rightVBox = new VBox();
	}

	public Seller display(Seller selectedSeller) {
		sellerId = selectedSeller.getSellerId();
		Stage window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle("填写新的信息");

		//Create two buttons
		Button yesButton = NewButton("确定");
		Button noButton = NewButton("取消");

		buttonHBox = new HBox(yesButton, noButton);
		buttonHBox.setSpacing(10);
		buttonHBox.setAlignment(Pos.CENTER_RIGHT);
		buttonHBox.setPadding(new Insets(10, 10, 10, 10));

		//Clicking will set answer and close window
		yesButton.setOnAction(e -> {
			CreateSeller();
			window.close();
		});
		noButton.setOnAction(e -> {
			seller = null;
			window.close();
		});

		initLeftGrid(selectedSeller);
		initRightGrid(selectedSeller);

		leftVBox.setSpacing(10);
		leftVBox.setPadding(new Insets(20, 20, 20, 20));
		leftVBox.setAlignment(Pos.TOP_LEFT);
		rightVBox.setSpacing(10);
		rightVBox.setPadding(new Insets(20, 20, 20, 20));
		rightVBox.setAlignment(Pos.TOP_RIGHT);

		infoEnterHBox = new HBox(leftRegion, leftVBox, rightVBox, rightRegion);
		infoEnterHBox.setSpacing(10);

		mainVBox = new VBox(infoEnterHBox);
		mainVBox.setSpacing(10);

		Scene scene = new Scene(mainVBox);
		window.setScene(scene);
		window.showAndWait();

		//Make sure to return answer
		return seller;
	}

	private void initRightGrid(Seller seller) {

		rightGridPane.setPadding(new Insets(10, 10, 10, 10));
		rightGridPane.setVgap(8);
		rightGridPane.setHgap(10);

		Label rightHeader = new Label("选填内容");
		rightHeader.setMaxWidth(Double.MAX_VALUE);
		rightHeader.setAlignment(Pos.CENTER);
		rightHeader.setStyle(headerStyle);

		Label landLineLabel = NewLabel("座机:", 0, 0);
		landLineInput = NewTextField(String.valueOf(seller.getLandLine()), 1, 0);

		Label faxLabel = NewLabel("传真:", 0, 1);
		faxInput = NewTextField(String.valueOf(seller.getFax()), 1, 1);

		Label accountNumLabel = NewLabel("供应商账号:", 0, 2);
		accountNumInput = NewTextField(seller.getAccountNum(), 1, 2);

		Label bankAddressLabel = NewLabel("开户行:", 0, 3);
		bankAddressInput = NewTextField(seller.getBankAddress(), 1, 3);

		Label addressLabel = NewLabel("供应商地址:", 0, 4);
		addressInput = NewTextField(seller.getAddress(), 1, 4);

		rightGridPane.getChildren().addAll(landLineLabel, landLineInput, faxLabel, faxInput, accountNumLabel, accountNumInput,
				bankAddressLabel, bankAddressInput, addressLabel, addressInput);
		rightVBox.getChildren().setAll(rightHeader, rightGridPane, buttonHBox);
		buttonHBox.setAlignment(Pos.BASELINE_RIGHT);
	}

	private void initLeftGrid(Seller seller) {

		leftGridPane.setPadding(new Insets(10, 10, 10, 10));
		leftGridPane.setVgap(8);
		leftGridPane.setHgap(10);

		Label leftHeader = new Label("必填内容");
		leftHeader.setMaxWidth(Double.MAX_VALUE);
		leftHeader.setAlignment(Pos.CENTER);
		leftHeader.setStyle(headerStyle);

		Label companyNameLabel = NewLabel("供应商名称:", 0, 0);
		companyNameInput = NewTextField(seller.getCompanyName(), 1, 0);

		Label contactNameLabel = NewLabel("联系人名字:", 0, 1);
		contactNameInput = NewTextField(seller.getContactName(), 1, 1);

		Label phoneLabel = NewLabel("手机:", 0, 2);
		mobileInput = NewTextField(String.valueOf(seller.getMobile()), 1, 2);


		leftGridPane.getChildren().setAll(companyNameLabel, companyNameInput, contactNameLabel, contactNameInput,
				phoneLabel, mobileInput);

		leftVBox.getChildren().setAll(leftHeader, leftGridPane);
	}

	private void CreateSeller() {

		// companyName
		String companyName = companyNameInput.getText();
		if (companyName.equals("")) {
			AlertBox.display("错误", "必须输入供应商名称");
			seller = null;
			return;
		}

		// contactName
		String contactName = contactNameInput.getText();
		if (contactName.equals("")) {
			AlertBox.display("错误", "必须输入联系人名称");
			seller = null;
			return;
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

		;
		Seller seller = new Seller(sellerId, companyName, contactName);

		seller.setMobile(mobile);
		seller.setLandLine(landLine);
		seller.setFax(fax);
		seller.setAccountNum(accountNum);
		seller.setBankAddress(bankAddress);
		seller.setAddress(address);

		this.seller = seller;
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
		returnTextField.setText(text);
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

	public Seller getSeller() {
		return seller;
	}
}