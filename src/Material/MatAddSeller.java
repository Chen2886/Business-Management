package Material;

// from my other packages

import Main.*;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.TextFields;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class MatAddSeller {

	public VBox infoInputVBox;
	public ScrollPane scrollPane;
	public ImageView backButton;
	public TableView<MatSeller> matSellerTableView;
	@FXML
	Label matAddOrderTitleLabel;
	@FXML
	Button matAddOrderContinueButton;

	Stage currentStage;
	ObservableList<MatSeller> allMatSeller;
	ArrayList<JFXTextField> matSellerInputArray;
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
		matAddOrderContinueButton.setOnAction(actionEvent -> addSeller());
		matAddOrderContinueButton.setOnKeyReleased(actionEvent -> {
			if (actionEvent.getCode() == KeyCode.ENTER) addSeller();
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
		fillMatSellerTable(FinalConstants.updateAllMatSellers());
	}

	/**
	 * Filling of the material table
	 *
	 * @param selectedMatSeller the orders specified
	 */
	public void fillMatSellerTable(ObservableList<MatSeller> selectedMatSeller) {

		// array of columns
		Collection<TableColumn<MatSeller, ?>> matSellerColumnArrayList = new ArrayList<>();

		// loop to set up all regular columns
		for (int i = 0; i < FinalConstants.matSellerTableHeaders.length; i++) {
			// String
			TableColumn<MatSeller, String> newColumn = new TableColumn<>(FinalConstants.matSellerTableHeaders[i]);
			newColumn.setCellValueFactory(new PropertyValueFactory<>(FinalConstants.matSellerPropertyHeaders[i]));
			newColumn.setStyle("-fx-alignment: CENTER;");
			matSellerColumnArrayList.add(newColumn);
		}

		// if backspace or delete, delete the order
		matSellerTableView.setOnKeyPressed(keyEvent -> {
			if (keyEvent.getCode() == KeyCode.BACK_SPACE || keyEvent.getCode() == KeyCode.DELETE)
				deleteMatSeller(matSellerTableView.getSelectionModel().getSelectedItem());
		});

		// filling the table
		matSellerTableView.getColumns().setAll(matSellerColumnArrayList);
		matSellerTableView.getItems().clear();
		matSellerTableView.getItems().setAll(selectedMatSeller);
		matSellerTableView.refresh();
	}


	/**
	 * initialize all labels and text fields for add mat order grid
	 */
	private void initAddMatOrder() {

		FinalConstants.setButtonImagesAndCursor(backButton, FinalConstants.backWhite, FinalConstants.backBlack);

		infoInputVBox.setMaxWidth(Double.MAX_VALUE);

		// setting up all the text field
		matSellerInputArray = new ArrayList<>();

		Node buttonHBox = infoInputVBox.getChildren().get(1);
		infoInputVBox.getChildren().remove(buttonHBox);

		for (int i = 0; i < FinalConstants.matSellerTableHeaders.length; i++) {
			// regular text field
			JFXTextField newTextField = new JFXTextField();
			newTextField.setPromptText("输入" + FinalConstants.matSellerTableHeaders[i]);
			newTextField.setMaxWidth(Double.MAX_VALUE);
			newTextField.getStyleClass().add("white-jfx-text-field");
			matSellerInputArray.add(newTextField);
			infoInputVBox.getChildren().add(newTextField);
			newTextField.setPadding(new Insets(0, 25, 0, 25));
		}

		infoInputVBox.getChildren().add(buttonHBox);
	}

	/**
	 * Clear all input area for add order
	 */
	private void clearAddSellerFields() {
		for (int i = 0; i < matSellerInputArray.size(); i++)
			if (i != 0 && i != 1 && matSellerInputArray.get(i) != null)
				matSellerInputArray.get(i).clear();
	}

	/**
	 * Obtain all the new information, push new order to database, and clear fields
	 */
	private void addSeller() {

		if (matSellerInputArray.get(0).getText() != null) {
			// initialize a new order
			MatSeller newSeller = new MatSeller(SerialNum.getSerialNum(DBOrder.SELLER), matSellerInputArray.get(0).getText());
			for (int i = 1; i < matSellerInputArray.size(); i++) {
				try {
					Method setter = MatSeller.class.getDeclaredMethod("set" + FinalConstants.matSellerPropertyHeaders[i], String.class);
					setter.invoke(newSeller, matSellerInputArray.get(i).getText());
				} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
					e.printStackTrace();
				}
			}

			try {
				DatabaseUtil.AddMatSeller(newSeller);
				clearAddSellerFields();
				matSellerTableView.getItems().setAll(FinalConstants.updateAllMatSellers());
				matSellerTableView.refresh();
			} catch (SQLException e) {
				AlertBox.display("错误", "更新错误，联系管理员");
				new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
						e.getMessage(), e.getStackTrace(), false);
			}
		} else {
			AlertBox.display("错误", "没有输入供应商名称");
		}
	}

	/**
	 * Helper function to delete mat seller
	 *
	 * @param selectedSeller the order to be deleted
	 */
	private void deleteMatSeller(MatSeller selectedSeller) {
		if (ConfirmBox.display("确认", "确定删除？", "是", "否")) {
			try {
				DatabaseUtil.DeleteMatSeller(selectedSeller.getSellerId());
				matSellerTableView.getItems().setAll(FinalConstants.updateAllMatSellers());
			} catch (SQLException e) {
				AlertBox.display("错误", "无法删除原料供应商！");
				new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
						e.getMessage(), e.getStackTrace(), false);
			}
		}
	}

}
