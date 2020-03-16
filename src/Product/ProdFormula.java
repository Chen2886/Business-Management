package Product;

import javafx.stage.Stage;

public class ProdFormula {

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
        init();
    }

    private void init() {

    }

}
