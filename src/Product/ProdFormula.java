package Product;

import Main.AlertBox;
import Main.HandleError;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;

public class ProdFormula {

    private static String[] property = new String[] {"ItemName", "amount", "unitPrice", "totalPrice"};
    private static String[] header = new String[] {"原料名称", "数量", "单价", "金额"};

    @FXML Button addItemButton;
    @FXML TableView<FormulaItem> prodFormulaTable;
    @FXML Label prodFormulaTitleLabel;

    ArrayList<FormulaItem> formula;
    Stage currentStage;
    ProductOrder selectedOrder;

    /**
     * Called by main controller to give the selected order
     * @param selectedOrder the order that was selected, to fill the information
     * @param currentStage the stage, so it can be closed later
     */
    public void initData(ProductOrder selectedOrder, Stage currentStage) {
        this.selectedOrder = selectedOrder;
        this.currentStage = currentStage;
        formula = new ArrayList<>();
        init();
    }

    private void init() {
        if (selectedOrder.getFormulaFile().equals("")) {
            // no formula given found

            // TODO: Find formula

            // TODO: If formula found, populate table

            // TODO: If formula not found, create new formula, save to database
            addItemButton.setOnAction(event -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("ProdFormulaAddItem.fxml"));
                    Parent newScene = loader.load();
                    Stage stage = new Stage();

                    ProdFormulaAddItem prodFormulaAddItem = loader.getController();
                    prodFormulaAddItem.initData(stage);
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.setTitle("添加原料");
                    stage.setScene(new Scene(newScene));
                    stage.show();
                } catch (Exception e) {
                    AlertBox.display("错误", "窗口错误");
                    e.printStackTrace();
                    HandleError error = new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                            e.getMessage(), e.getStackTrace(), false);
                    error.WriteToLog();
                }
            });

        }
        else {
            // TODO: formula given, populate table
        }
    }

    /**
     * public function for other controller to call, to add to the list, and refresh table
     * @param item the item to be added to list
     */
    public void addItemToList(FormulaItem item) {
        formula.add(item);
        prodFormulaTable.getItems().clear();
        prodFormulaTable.getItems().setAll(formula);
    }

}
