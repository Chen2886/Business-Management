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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main extends Application {

	public static Stage mainStage;
	public static String fxmlPath = "fxml/";
	public static String styleSheetPath = System.getProperty("user.dir") + "/fxml/stylesheet.css";

	// * main function to get everything started
	public static void main(String[] args) {

		// clear error
		HandleError.clear();

		// Initialize Database
		if (!DatabaseUtil.ConnectionInitAndCreate()) {
			AlertBox.display("错误", "数据库不存在");
			System.exit(0);
		}

		Path source = Paths.get("BusinessCashFlow.db");
		Path target = Paths.get(System.getProperty("user.home") + "/BusinessCashFlow.db");
		try {
			if (Files.exists(target)) Files.delete(target);
			Files.copy(source, target);
		} catch (IOException e) {
			e.printStackTrace();
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
		mainStage = stage;

		stage.setOnCloseRequest(event -> {
			Path source = Paths.get("BusinessCashFlow.db");
			Path target = Paths.get(System.getProperty("user.home") + "/BusinessCashFlow.db");
			try {
				if (Files.exists(target)) Files.delete(target);
				Files.copy(source, target);
			} catch (IOException e) {
				e.printStackTrace();
			}
			stage.close();
		});

		stage.show();
	}

	public static Scene loadFXML(String fxmlName) {
		try {
			FXMLLoader loader = new FXMLLoader();
			FileInputStream fileInputStream = new FileInputStream(new File(Main.fxmlPath + fxmlName));
			Parent parent = loader.load(fileInputStream);
			Scene scene = new Scene(parent);
			scene.getStylesheets().add("file:///" + styleSheetPath);
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
