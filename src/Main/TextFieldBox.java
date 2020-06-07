package Main;

// from my other packages

import com.jfoenix.controls.JFXTextField;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class TextFieldBox {

	//Create variable
	static String answer;
	private static final String mediumFont = "-fx-font: 18 arial;";
	private static JFXTextField textField;

	public static String display(String title, String message, String yesButtonMessage, String noButtonMessage) {
		Stage window = new Stage();

		textField = new JFXTextField();
		textField.setPromptText(message);
		textField.setStyle(mediumFont);

		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle(title);
		window.setMinWidth(250);

		//Create two buttons
		Button yesButton = new Button(yesButtonMessage);
		Button noButton = new Button(noButtonMessage);

		HBox buttonHBox = new HBox(yesButton, noButton);
		buttonHBox.setSpacing(10);
		buttonHBox.setAlignment(Pos.CENTER_RIGHT);
		buttonHBox.setPadding(new Insets(10, 10, 10, 10));

		//Clicking will set answer and close window
		yesButton.setOnKeyPressed(keyEvent -> {
			if (keyEvent.getCode().equals(KeyCode.ENTER)) {
				answer = textField.getText();
				window.close();
			}
		});
		yesButton.setOnAction(e -> {
			answer = textField.getText();
			window.close();
		});
		noButton.setOnAction(e -> {
			answer = "CANCELED PRESSED";
			window.close();
		});

		VBox layout = new VBox(10);

		//Add buttons
		layout.getChildren().addAll(textField, buttonHBox);
		layout.setAlignment(Pos.CENTER);
		Scene scene = new Scene(layout);
		window.setScene(scene);
		window.showAndWait();

		//Make sure to return answer
		return answer;
	}

}