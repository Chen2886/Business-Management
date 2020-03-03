package Product;

import Material.*;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.awt.*;

public class ProdMainScreen {
	private static final String standardBigHeader = "-fx-font: 28 arial;\n" +
			"-fx-border-style: solid;\n" +
			"-fx-border-width: 0 0 1.5 0;\n" +
			"-fx-border-color: black;\n";
	private final static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	private final static String[] leftMenu = new String[] {"添加订单", "搜索/更改订单", "添加产品", "搜索/更改产品",
			"添加原料", "搜索/更改原料", "生成/更新一览表", "高级搜索", "返回主菜单"};

	// top menu bar item
	private MenuItem exitMenuItem;

	// left side bar
	private ListView<String> menuLeftList;

	// current Stage and Scene
	private Stage mainStage;
	private Scene currentScene;
	private BorderPane currentBorderPane;

	// buttons on the left side
	private Button menuAddOrder;
	private Button menuUpdateOrder;
	private Button menuAddProduct;
	private Button menuUpdateProduct;
	private Button menuAddMaterial;
	private Button menuUpdateMaterial;
	private Button menuAdvanceSearch;
	private Button menuBackToMain;
	private Button menuGenerateExcel;


	/**
	 * Start the product main screen
	 * @param primaryStage stage passed in by main screen
	 */
	public void start(Stage primaryStage) {
		primaryStage.setTitle("产品订单管理");
		mainStage = primaryStage;
		initScene();
//		"添加订单", "搜索/更改订单", "添加产品", "搜索/更改产品",
//				"添加原料", "搜索/更改原料", "生成/更新一览表", "高级搜索", "返回主菜单"
		menuLeftList.getSelectionModel().getSelectedItems().addListener((ListChangeListener<String>) change -> {
			for (int i = 0; i < leftMenu.length; i++) {
				if (menuLeftList.getSelectionModel().getSelectedItem().equals(leftMenu[i])) {
					ChangeScene(i);
					break;
				}
			}
		});

		exitMenuItem.setOnAction(e -> System.exit(1));
	}

	/**
	 * set up the main product page
	 */
	private void initScene() {
		this.currentBorderPane = new BorderPane();
		initAllButtons();
		MenuBar menuBar = AddMenuBar();

		menuLeftList = new ListView<>();
		menuLeftList.getItems().setAll("添加订单", "搜索/更改订单", "添加产品", "搜索/更改产品",
				"添加原料", "搜索/更改原料", "生成/更新一览表", "高级搜索", "返回主菜单");
		menuLeftList.setMaxWidth(screenSize.width * 0.08);

		currentBorderPane.setLeft(menuLeftList);
		currentBorderPane.setTop(menuBar);

		mainStage.setMinWidth(screenSize.width);
		mainStage.setMinHeight((int) (screenSize.height * 0.9));

		currentScene = new Scene(currentBorderPane, screenSize.width,
				(int) (screenSize.height * 0.9));
		mainStage.setScene(currentScene);
		mainStage.centerOnScreen();
		mainStage.show();
	}

	/**
	 * Initialize all button on the left side, so it can be focused later
	 */
	private void initAllButtons() {
		menuAddOrder = new Button("添加订单");
		menuUpdateOrder = new Button("搜索/更改订单");
		menuAddProduct = new Button("添加产品");
		menuUpdateProduct = new Button("搜索/更改产品");
		menuAddMaterial = new Button("添加原料");
		menuUpdateMaterial = new Button("搜索/更改原料");
		menuAdvanceSearch = new Button("高级搜索");
		menuGenerateExcel = new Button("生成/更新一览表");
		menuBackToMain = new Button("返回主菜单");
	}

	/**
	 * returns a menu bar with items
	 * @return A menu bar for the main stage
	 */
	private MenuBar AddMenuBar() {
		MenuBar menuBar = new MenuBar();
		menuBar.prefWidthProperty().bind(mainStage.widthProperty());

		Menu ApplicationMenu = new Menu("程序");
		exitMenuItem = new MenuItem("退出");

		ApplicationMenu.getItems().addAll(exitMenuItem);

		menuBar.getMenus().addAll(ApplicationMenu);
		return menuBar;
	}

	/**
	 * Handles all button clicks
	 * @param options the choice user made
	 */
	private void ChangeScene(int options) {
		//		"添加订单", "搜索/更改订单", "添加产品", "搜索/更改产品",
		//		"添加原料", "搜索/更改原料", "生成/更新一览表", "高级搜索", "返回主菜单"
		switch (options) {
			case 0:
				AddOrderPane addOrderPane = new AddOrderPane();
				addOrderPane.initScene();
				VBox addOrderVBox = addOrderPane.getPane();
				addOrderVBox.getChildren().add(0, NewStandardSceneHeader("添加订单"));
				resetStage(addOrderVBox, 1, 0.9, menuAddOrder);
				break;
			case 1:
				break;
			case 2:
				break;
			case 3:
				break;
			case 4:
				break;
			case 5:
				break;
			case 6:
				break;
			case 7:
				break;
			case 8:
				Main.startScreen startScreen = new Main.startScreen();
				try {
					startScreen.start(mainStage);
				} catch (Exception e) {
					HandleError error = new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
							e.getMessage(), e.getStackTrace(), false);
					error.WriteToLog();
				}
				break;
		}
	}

	/**
	 * Create a new standard label that will be used across the main scene
	 * @param text text needed in the label
	 * @return a label with standard font and with the text provided
	 */
	private Label NewStandardSceneHeader(String text) {
		Label standardLabel = new Label(text);
		standardLabel.setStyle(standardBigHeader);
		standardLabel.setMaxWidth(Double.MAX_VALUE);
		standardLabel.setAlignment(Pos.CENTER);
		standardLabel.setPadding(new Insets(10, 10, 0, 10));

		return standardLabel;
	}

	/**if
	 * reset the stage with the new vbox provided
	 * @param vBox vbox in the middle of stage
	 * @param width width of the desired stage
	 * @param height height of the desired stage
	 * @param button buttons that needs focus on the left
	 */
	private void resetStage(VBox vBox, double width, double height, Button button) {
		currentBorderPane = CopyBorderPane(currentBorderPane);
		currentBorderPane.setCenter(vBox);
		mainStage.setMinWidth((int) (screenSize.width * width));
		mainStage.setMinHeight((int) (screenSize.height * height));
		currentScene = new Scene(currentBorderPane, (int) (screenSize.width * width),
				(int) (screenSize.height * height));
		mainStage.setScene(currentScene);
		mainStage.centerOnScreen();
		mainStage.sizeToScene();
		mainStage.show();
		button.requestFocus();
	}

	/**
	 * Make a copy of the borderpane passed in. Helper Function.
	 * @param source the borderpane that needs to be copied
	 * @return the copy of borderpane
	 */
	private BorderPane CopyBorderPane(BorderPane source) {
		BorderPane newBorderPane = new BorderPane();
		newBorderPane.setTop(source.getTop());
		newBorderPane.setCenter(source.getCenter());
		newBorderPane.setBottom(source.getBottom());
		newBorderPane.setLeft(source.getLeft());
		newBorderPane.setRight(source.getRight());
		this.currentBorderPane = newBorderPane;
		return newBorderPane;
	}
}
