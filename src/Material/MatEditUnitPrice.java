package Material;

import Main.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;

public class MatEditUnitPrice {

    public TextField matNameTextField;
    public TextField matPriceTextField;
    public TextField matNoteTextField;
    public Button cancelButton;
    public Button completeButton;

    MatUnitPrice matUnitPrice;
    Stage stage;

    public void initData(Stage stage, MatUnitPrice matUnitPrice) {
        this.stage = stage;
        this.matUnitPrice = matUnitPrice;
        init();
    }

    private void init() {
        // button actions
        cancelButton.setOnAction(event -> {
            if (ConfirmBox.display("确认", "确定关闭窗口？单价不会被保存", "是", "否"))
                stage.close();
        });

        completeButton.setOnAction(event -> updateMatPrice());

        matNameTextField.setText(matUnitPrice.getName());
        matPriceTextField.setText(String.valueOf(matUnitPrice.getUnitPrice()));
        matNoteTextField.setText(matUnitPrice.getNote());

    }

    private void updateMatPrice() {
        MatUnitPrice newMatUnitPrice;
        try {
            newMatUnitPrice = new MatUnitPrice(matNameTextField.getText(),
                    Double.parseDouble(matPriceTextField.getText()), matNoteTextField.getText());
        } catch (Exception e) {
            newMatUnitPrice = new MatUnitPrice(matNameTextField.getText(),
                    0.0, matNoteTextField.getText());
            AlertBox.display("错误", "单价格式输入错误, 数字默认0");
        }
        try {
            DatabaseUtil.UpdateMatUnitPrice(matUnitPrice, newMatUnitPrice);
            if (ConfirmBox.display("确认", "是否更新所有此原料没有单价的订单？", "是", "否"))
                MatUnitPriceTable.updateAllUnitPrice(newMatUnitPrice.getName(), newMatUnitPrice.getUnitPrice());
            stage.close();
        } catch (SQLException e) {
            AlertBox.display("错误", "无法更新，确保新的名称唯一");
            HandleError error = new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
        }

    }
}
