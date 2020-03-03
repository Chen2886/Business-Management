package Main;

import Material.*;
import Product.ProdMainScreen;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.awt.*;

public class startScreen extends Application {
	private static final String standardBigHeader = "-fx-font: 28 arial;\n" +
			"-fx-border-style: solid;\n" +
			"-fx-border-width: 0 0 1.5 0;\n" +
			"-fx-border-color: black;\n";
	private static final String standardFont = "-fx-font: 15 arial;";
	private static final String bigFont = "-fx-font: 36 arial;";
	private final static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

	private Stage mainStage;
	private Scene currentScene;
	private BorderPane currentBorderPane;
	private Button matButton;
	private Button prodButton;
	private Button exitButton;


	/**
	 * Main function to launch the application
	 * @param args DO NOT PROVIDE
	 */
	public static void main(String[] args) {
		HandleError.clear();

		if (!DatabaseUtil.ConnectionInitAndCreate()) {
			System.out.println("DATABASE ConnectionInitAndCreate FAILURE");
			System.exit(1);
		}

		// TODO: Add init for Prod
		SerialNum.initSku();
		SellerId.initSellerId();
		Application.launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		stage.setTitle("订单管理");
		mainStage = stage;

		currentBorderPane = new BorderPane();

		HBox buttonHBox = new HBox();
		buttonHBox.setMaxWidth(Double.MAX_VALUE);
		buttonHBox.setSpacing(10);
		buttonHBox.setPadding(new Insets(10, 10, 10, 10));
		buttonHBox.setAlignment(Pos.CENTER);

		matButton = NewButton("进入订单管理");
		prodButton = NewButton("进入产品管理");
		exitButton = NewButton("退出程序");

		matButton.setMaxWidth(Double.MAX_VALUE);
		prodButton.setMaxWidth(Double.MAX_VALUE);
		exitButton.setMaxWidth(Double.MAX_VALUE);
		buttonHBox.getChildren().setAll(matButton, prodButton, exitButton);

		currentBorderPane.setCenter(buttonHBox);

		this.currentScene = new Scene(currentBorderPane);
		mainStage.setMinWidth(screenSize.width);
		mainStage.setMinHeight((int) (screenSize.height * 0.9));
		mainStage.setScene(currentScene);
		mainStage.show();

		matButton.setOnAction(e -> {
			MatMainScreen matMainScreen = new MatMainScreen();
			matMainScreen.start(mainStage);
		});
		prodButton.setOnAction(e -> {
			ProdMainScreen prodMainScreen = new ProdMainScreen();
			prodMainScreen.start(mainStage);
		});
		exitButton.setOnAction(e -> {
			System.exit(1);
		});

	}

	/**
	 * Create a button with specified text
	 * @param text text on the button
	 * @return javaFx Button
	 */
	private Button NewButton(String text) {
		Button returnButton = new Button(text);
		returnButton.setStyle(bigFont);
		returnButton.setMaxWidth(Double.MAX_VALUE);
		return returnButton;
	}
}
