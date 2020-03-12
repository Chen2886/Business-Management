package Product;

// from my other packages
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ProdAddOrderModifySellerController {

    // prod table headers
    // TODO: Add Formula
    private static final String[] prodHeaders = new String[] {"订单日期", "送货单号", "客户", "产品名称",
            "规格", "数量", "单价", "备注"};

    // all prod property listed
    private static final String[] prodProperty = new String[]{"orderDate", "sku", "customer", "name",
            "unitAmount", "amount", "unitPrice", "note"};

    @FXML Button prodAddOrderCancelButton;
    @FXML Button prodAddOrderCompleteButton;
    @FXML Button prodAddOrderContinueButton;
    @FXML GridPane prodAddOrderGrid;
    @FXML Label prodAddOrderTitle;

    Stage currentStage;
    private ArrayList<Node> prodOrderInputArray;

    /**
     * Called by main controller, pass in the stage for later closing, and init the screen
     * @param currentStage the current stage so it can be closed
     */
    public void initData(Stage currentStage) {
        this.currentStage = currentStage;
        init();
    }

    /**
     * initialize all labels and text fields for add prod order grid
     */
    private void init() {

        prodAddOrderCancelButton.setOnAction(actionEvent -> {

        });

        prodAddOrderCompleteButton.setOnAction(actionEvent -> {

        });

        prodAddOrderContinueButton.setOnAction(actionEvent -> {

        });

        prodAddOrderTitle.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        int row = 1;
        int col = 0;

        // setting up all the labels
        ArrayList<Label> prodOrderLabelArray = new ArrayList<>();
        for(int i = 0; i < prodHeaders.length; i++) {
            Label newLabel = new Label(prodHeaders[i]);
            newLabel.setStyle("-fx-font-size: 20px;" +
                    "-fx-alignment: center-right;");
            // newLabel.setMaxWidth(Double.MAX_VALUE);
            GridPane.setConstraints(newLabel, col, row++);
            prodOrderLabelArray.add(newLabel);
            if ((i + 5) % 4 == 0) {
                row = 1;
                col += 2;
            }
        }

        row = 1;
        col = 1;

        // setting up all the text field
        prodOrderInputArray = new ArrayList<>();
        for(int i = 0; i < prodProperty.length; i++) {

            // dates, date picker
            if (i == 0) {
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                DatePicker datePicker = new DatePicker();

                datePicker.setConverter(new StringConverter<>() {
                    @Override
                    public String toString(LocalDate localDate) {
                        if (localDate == null) {
                            return "0/0/0";
                        }
                        return dateTimeFormatter.format(localDate);
                    }

                    @Override
                    public LocalDate fromString(String string) {
                        if (string == null || string.isEmpty()) {
                            return null;
                        }
                        return LocalDate.from(dateTimeFormatter.parse(string));
                    }
                });


                GridPane.setConstraints(datePicker, col, row++);
                prodOrderInputArray.add(datePicker);
            }

            // regular text field
            else {
                TextField newTextField = new TextField();
                newTextField.setPromptText("输入" + prodHeaders[i]);
                GridPane.setConstraints(newTextField, col, row++);
                prodOrderInputArray.add(newTextField);

            }

            if ((i + 5) % 4 == 0) {
                row = 1;
                col += 2;
            }
        }

        prodAddOrderGrid.setVgap(10);
        prodAddOrderGrid.setHgap(10);
        prodAddOrderGrid.getChildren().addAll(prodOrderLabelArray);
        prodAddOrderGrid.getChildren().addAll(prodOrderInputArray);

    }




}
