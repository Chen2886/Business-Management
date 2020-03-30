package Main;

// from my other packages

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main extends Application {

	private Stage stage;
	public static String fxmlPath = "/Users/chen2886/Desktop/Business-Management/fxml/";

	// * main function to get everything started
	public static void main(String[] args) {

		// clear error
		HandleError.clear();

		// Initialize Database
		if (!DatabaseUtil.ConnectionInitAndCreate()) {
			AlertBox.display("错误", "数据库不存在");
			System.exit(1);
		}

		SerialNum.initSerialNum();

		launch(args);
	}

	// first function to be called to launch the first screen
	@Override
	public void start(Stage stage) throws Exception {
		Scene scene = Main.loadFXML("MainScreen.fxml");

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		stage.setWidth(screenSize.width);
		stage.setHeight(screenSize.height * 0.9);

		stage.setScene(scene);
		this.stage = stage;
		stage.show();
	}

	public static Scene loadFXML(String fxmlName) {
		try {
			FXMLLoader loader = new FXMLLoader();
			FileInputStream fileInputStream = new FileInputStream(new File(Main.fxmlPath + fxmlName));
			Parent parent = loader.load(fileInputStream);
			Scene scene = new Scene(parent);
			scene.getStylesheets().add("file:///" + Main.fxmlPath + "stylesheet.css");
			return scene;
		} catch (Exception e) {
			AlertBox.display("错误", "错误，联系管理员");
			e.printStackTrace();
			HandleError error = new HandleError(Main.class.getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
					e.getMessage(), e.getStackTrace(), false);
			error.WriteToLog();
			return new Scene(new VBox());
		}
	}

}
