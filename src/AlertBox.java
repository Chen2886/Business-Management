import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AlertBox {

	private static final String smallFont = "-fx-font: 16 arial;";
	private static final String mediumFont = "-fx-font: 18 arial;";

	public static void display(String title, String message) {
		Stage window = new Stage();

		//Block events to other windows
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle(title);
		window.setMinWidth(250);

		Label label = new Label();
		label.setText(message);
		label.setPadding(new Insets(10, 10, 10, 10));
		label.setStyle(mediumFont);

		Button closeButton = new Button("确定");
		closeButton.setStyle(smallFont);
		closeButton.setPadding(new Insets(10, 10, 10, 10));
		closeButton.setAlignment(Pos.BOTTOM_RIGHT);
		closeButton.setOnAction(e -> window.close());

		VBox layout = new VBox();
		layout.setPadding(new Insets(10, 10, 10, 10));
		layout.getChildren().addAll(label, closeButton);
		layout.setAlignment(Pos.CENTER);

		//Display window and wait for it to be closed before returning
		Scene scene = new Scene(layout);
		window.setScene(scene);
		window.showAndWait();
	}

}
