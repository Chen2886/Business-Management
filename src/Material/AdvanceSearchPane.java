package Material;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class AdvanceSearchPane {
	VBox mainVBox;
	TextField enterCommand;
	Button search;
	private static final String mediumFont = "-fx-font: 18 arial;";

	public AdvanceSearchPane() {
		mainVBox = new VBox();
		mainVBox.setSpacing(10);
//		mainVBox.setPadding(new Insets(10, 10, 10, 10));

		Label header = new Label("输入指令");
		header.setStyle(mediumFont);
		header.setMaxWidth(Double.MAX_VALUE);
		header.setAlignment(Pos.CENTER);
		header.setPadding(new Insets(10, 10, 10, 10));
		enterCommand = new TextField();
		enterCommand.setPromptText("指令");
		enterCommand.setPadding(new Insets(10, 10, 10, 10));
		enterCommand.setAlignment(Pos.TOP_LEFT);

		search = new Button("搜索");
		search.setAlignment(Pos.BASELINE_RIGHT);
		search.setPadding(new Insets(10, 10, 10, 10));

		mainVBox.getChildren().addAll(header, enterCommand, search);
	}

	public VBox getPane() {
		return mainVBox;
	}

	public Button getSearch() {
		return search;
	}

	public TextField getEnterCommand() {
		return enterCommand;
	}
}
