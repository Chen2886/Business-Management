package Product;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.ArrayList;

public class ProdFormulaAddItem {

    @FXML HBox infoHBox;
    Stage stage;

    ArrayList<TextField> inputArray;

    private static String[] property = new String[] {"ItemName", "amount", "unitPrice", "totalPrice"};
    private static String[] header = new String[] {"原料名称", "数量", "单价", "金额"};

    public void initData(Stage stage) {
        this.stage = stage;
        init();
    }

    private void init() {
        inputArray = new ArrayList<>();
        for (int i = 0; i < header.length; i++) {
            Label newLabel = new Label(header[i]);
            newLabel.setStyle("-fx-font-size: 20px;" +
                    "-fx-alignment: center-right;");
            infoHBox.getChildren().add(newLabel);

            TextField newTextField = new TextField();
            newTextField.setPromptText("输入" + header[i]);
            infoHBox.getChildren().add(newTextField);
        }
    }

}
