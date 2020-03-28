package Main;

// from my other packages
import Material.*;
import Product.*;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.security.Key;

public class ConfirmBox {

	//Create variable
	static boolean answer;
	private static final String mediumFont = "-fx-font: 18 arial;";

	public static boolean display(ConfirmMessage confirmMessage) {
		if (confirmMessage == ConfirmMessage.CLOSEWINDOW)
			return display("确认", "确定关闭窗口？进度即将还原。", "是","否");
		else if (confirmMessage == ConfirmMessage.CANCEL)
			return display("确认", "确定取消？订单即将还原（不会被保存）。", "是","否");
		else
			return false;
	}

	public static boolean display(String title, String message, String yesButtonMessage, String noButtonMessage) {
		Stage window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle(title);
		window.setMinWidth(250);

		Label label = new Label();
		label.setText(message);
		label.setPadding(new Insets(10, 10, 10, 10));
		label.setStyle(mediumFont);

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
				answer = true;
				window.close();
			}
		});
		yesButton.setOnAction(e -> {
			answer = true;
			window.close();
		});
		noButton.setOnAction(e -> {
			answer = false;
			window.close();
		});

		VBox layout = new VBox(10);

		//Add buttons
		layout.getChildren().addAll(label, buttonHBox);
		layout.setAlignment(Pos.CENTER);
		Scene scene = new Scene(layout);
		window.setScene(scene);
		window.showAndWait();

		//Make sure to return answer
		return answer;
	}

}