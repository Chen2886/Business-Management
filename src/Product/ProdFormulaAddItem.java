package Product;

import Main.ConfirmBox;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.ArrayList;

public class ProdFormulaAddItem {

    @FXML Button addFormula;
    @FXML Button cancelButton;
    @FXML Button completeButton;
    @FXML HBox infoHBox;
    Stage currentStage;

    ArrayList<TextField> inputArray;
    Formula currentFormula;

    private static String[] property = new String[] {"ItemName", "amount", "unitPrice", "totalPrice"};
    private static String[] header = new String[] {"原料名称", "数量", "单价", "金额"};

    public void initData(Stage stage) {
        this.currentStage = stage;
        currentFormula = new Formula();
        init();
    }

    private void init() {

        addFormula.setOnAction(event -> {});
        cancelButton.setOnAction(event -> {
            if (ConfirmBox.display("确认", "确定取消？此原料不会被保存", "确认", "取消"))
                currentStage.close();
        });

        inputArray = new ArrayList<>();
        for (String s : header) {
            Label newLabel = new Label(s);
            newLabel.setStyle("-fx-font-size: 20px;" +
                    "-fx-alignment: center-right;");
            infoHBox.getChildren().add(newLabel);

            TextField newTextField = new TextField();
            newTextField.setPromptText("输入" + s);
            infoHBox.getChildren().add(newTextField);
        }
    }

}
