package Main;

// from my other packages

import Material.MatOrder;
import com.jfoenix.controls.JFXTextField;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;

public class MatOrderTextFieldBox extends TableCell {

	//Create variable
	static String answer;
	private static final String mediumFont = "-fx-font: 18 arial;";
	private static JFXTextField textField;

	public static void display(String title, String message, String yesButtonMessage, String noButtonMessage,
							   Method setter, MatOrder order) {
		Stage window = new Stage();

		textField = new JFXTextField();
		textField.setPadding(new Insets(20, 10, 10, 10));
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
		textField.setOnKeyPressed(keyEvent -> {
			if (keyEvent.getCode().equals(KeyCode.ENTER)) {
				try {
					setter.invoke(order, textField.getText());
					DatabaseUtil.UpdateMatOrder(order);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException | SQLException e) {
					e.printStackTrace();
				}
				window.close();
			}
		});
		yesButton.setOnKeyPressed(keyEvent -> {
			if (keyEvent.getCode().equals(KeyCode.ENTER)) {
				try {
					setter.invoke(order, textField.getText());
					DatabaseUtil.UpdateMatOrder(order);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException | SQLException e) {
					e.printStackTrace();
				}
				window.close();
			}
		});
		yesButton.setOnAction(event -> {
			try {
				setter.invoke(order, textField.getText());
				DatabaseUtil.UpdateMatOrder(order);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			window.close();
		});
		noButton.setOnAction(e -> {
			window.close();
		});

		VBox layout = new VBox(10);

		//Add buttons
		layout.getChildren().addAll(textField, buttonHBox);
		layout.setAlignment(Pos.CENTER);
		Scene scene = new Scene(layout);
		window.setScene(scene);
		window.show();
	}

}