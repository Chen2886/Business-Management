import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class Main extends Application {

	private Stage stage;

	// * main function to get everything started
	public static void main(String[] args) {

		// clear error
		HandleError.clear();

		// Initialize Database
		if (!DatabaseUtil.ConnectionInitAndCreate()) {
			System.out.println("DATABASE ConnectionInitAndCreate FAILURE");
			System.exit(1);
		}

		MatSerialNum.initMatSerialNum();
		MatSellerId.initMatSellerId();
		ProdSerialNum.initProdSerialNum();

		launch(args);
	}

	// first function to be called to launch the first screen
	@Override
	public void start(Stage stage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("MainScreen.fxml"));
		Scene scene = new Scene(root);
		scene.getStylesheets().add("stylesheet.css");
		stage.setScene(scene);
		this.stage = stage;
		stage.show();
	}

}
