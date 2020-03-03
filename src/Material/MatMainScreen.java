package Material;

import Main.DatabaseUtil;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.awt.*;
import java.sql.SQLException;

public class MatMainScreen {
    private static final String standardBigHeader = "-fx-font: 28 arial;\n" +
            "-fx-border-style: solid;\n" +
            "-fx-border-width: 0 0 1.5 0;\n" +
            "-fx-border-color: black;\n";
    private final static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    // Buttons on the left side used for focus
    private Button menuAddOrder;
    private Button menuUpdateOrder;
    private Button menuGenerateExcel;
    private Button menuUpdateSeller;
    private Button menuAddSeller;
    private Button menuAdvanceSearch;
    private Button menuBackToMain;

    // top menu bar item
    private MenuItem exitMenuItem;

    // left side bar
    private ListView<String> menuLeftList;

    // current Stage and Scene
    private Stage mainStage;
    private Scene currentScene;
    private BorderPane currentBorderPane;

    private Order selectedOrderFromTable;
    private Seller selectedSellerFromTable;

    /**
     * Stage launch from Application
     * @param primaryStage stage passed in by the class
     */
    public void start(Stage primaryStage) {
        primaryStage.setTitle("原料订单管理");
        mainStage = primaryStage;
        initScene();

        menuLeftList.getSelectionModel().getSelectedItems().addListener((ListChangeListener<String>) change -> {
            if (menuLeftList.getSelectionModel().getSelectedItem().equals("添加订单")) {
                ChangeScene("menuAddOrder");
            }
            else if (menuLeftList.getSelectionModel().getSelectedItem().equals("搜索/更改订单")) {
                ChangeScene("menuUpdateOrder");
            }
            else if (menuLeftList.getSelectionModel().getSelectedItem().equals("添加供应商")) {
                ChangeScene("menuAddSeller");
            }
            else if (menuLeftList.getSelectionModel().getSelectedItem().equals("搜索/更改供应商")) {
                ChangeScene("menuUpdateSeller");
            }
            else if (menuLeftList.getSelectionModel().getSelectedItem().equals("生成/更新一览表")) {
                ChangeScene("menuGenerateExcel");
            }
            else if (menuLeftList.getSelectionModel().getSelectedItem().equals("高级搜索")) {
                ChangeScene("menuAdvanceSearch");
            }
            else if (menuLeftList.getSelectionModel().getSelectedItem().equals("返回主菜单")) {
                ChangeScene("menuBackToMain");
            }
        });

        exitMenuItem.setOnAction(e -> System.exit(1));
    }

    /**
     * Set up the main stage as soon as program starts
     */
    private void initScene() {
        this.currentBorderPane = new BorderPane();

        leftSideVBoxButtonInitialize();
        MenuBar menuBar = AddMenuBar();

        menuLeftList = new ListView<>();
        menuLeftList.getItems().setAll("添加订单", "搜索/更改订单", "添加供应商", "搜索/更改供应商",
                "生成/更新一览表", "高级搜索", "返回主菜单");
        menuLeftList.setMaxWidth(screenSize.width * 0.08);

        currentBorderPane.setLeft(menuLeftList);
        currentBorderPane.setTop(menuBar);

        mainStage.setMinWidth(screenSize.width);
        mainStage.setMinHeight((int) (screenSize.height * 0.9));

        currentScene = new Scene(currentBorderPane, screenSize.width,
                (int) (screenSize.height * 0.9));
        mainStage.setScene(currentScene);
        mainStage.centerOnScreen();
        mainStage.show();
    }

    /**
     * Initialize all button on the left side, so it can be focused later
     * @return the VBox that contains all actions button
     */
    private VBox leftSideVBoxButtonInitialize() {
        // defining a new hBox, setting padding and spacing
        VBox vBox = new VBox();
        vBox.setPadding(new Insets(15, 10, 10, 10));
        vBox.setSpacing(10);

        // initializing all buttons
        menuAddOrder = new Button("添加订单");
        menuUpdateOrder = new Button("搜索/更改订单");
        menuGenerateExcel = new Button("生成/更新一览表");
        menuAddSeller = new Button("添加供应商");
        menuUpdateSeller = new Button("搜索/更改供应商");
        menuAdvanceSearch = new Button("高级搜索");
        menuBackToMain = new Button("返回主菜单");

        // adding button to hbox
        vBox.getChildren().addAll(menuAddOrder, menuUpdateOrder, menuAddSeller, menuUpdateSeller,
                menuGenerateExcel, menuAdvanceSearch);

        return vBox;
    }

    /**
     * returns a menu bar with items
     * @return A menu bar for the main stage
     */
    private MenuBar AddMenuBar() {
        MenuBar menuBar = new MenuBar();
        menuBar.prefWidthProperty().bind(mainStage.widthProperty());

        Menu ApplicationMenu = new Menu("程序");
        exitMenuItem = new MenuItem("退出");

        ApplicationMenu.getItems().addAll(exitMenuItem);

        menuBar.getMenus().addAll(ApplicationMenu);
        return menuBar;
    }

    /**
     * Handles all button clicks
     * @param options the choice user made
     */
    private void ChangeScene(String options) {
        if (options.equals("menuAddOrder")) {
            AddOrderPane addOrderPane = new AddOrderPane();
            addOrderPane.initScene();
            VBox addOrderVBox = addOrderPane.getPane();
            addOrderVBox.getChildren().add(0, NewStandardSceneHeader("添加订单"));

            resetStage(addOrderVBox, 1, 0.9, menuAddOrder);
        }
        else if (options.equals("menuUpdateOrder")) {
            SearchOrUpdateOrderPane searchOrUpdateOrderPane = new SearchOrUpdateOrderPane();
            searchOrUpdateOrderPane.initScene();
            VBox updateOrderVBox = searchOrUpdateOrderPane.getPane();
            updateOrderVBox.getChildren().add(0, NewStandardSceneHeader("搜索/更改订单"));

            resetStage(updateOrderVBox, 1, 0.9, menuUpdateOrder);

            Button searchButton = searchOrUpdateOrderPane.getSearchButton();
            searchButton.setOnAction(e -> searchOrderResult(searchOrUpdateOrderPane));
        }
        else if (options.equals("menuGenerateExcel")) {
            GenerateExcelPane generateExcelPane = new GenerateExcelPane();
            generateExcelPane.initScene();
            VBox excelVBox = generateExcelPane.getPane();
            excelVBox.getChildren().add(0, NewStandardSceneHeader("生成/更新一览表 (更新之前必须关闭Excel表格！）"));

            resetStage(excelVBox, 1, 0.9, menuGenerateExcel);
        }
        else if (options.equals("menuAddSeller")) {
            AddSellerPane addSellerPane = new AddSellerPane();
            addSellerPane.initScene();
            VBox addSellerVBox = addSellerPane.getPane();
            addSellerVBox.getChildren().add(0, NewStandardSceneHeader("添加供应商"));

            resetStage(addSellerVBox, 1, 0.9, menuAddSeller);
        }
        else if (options.equals("menuUpdateSeller")) {
            SearchOrUpdateSellerPane searchOrUpdateSellerPane = new SearchOrUpdateSellerPane();
            searchOrUpdateSellerPane.initScene();
            VBox mainVBox = searchOrUpdateSellerPane.getPane();
            mainVBox.getChildren().add(0, NewStandardSceneHeader("搜索/更改供应商"));

            resetStage(mainVBox, 1, 0.9, menuUpdateSeller);

            Button okButton = searchOrUpdateSellerPane.getAddButton();
            okButton.setOnAction(e -> searchSellerResult(searchOrUpdateSellerPane));
        }
        else if (options.equals("menuAdvanceSearch")) {
            AdvanceSearchPane advanceSearchPane = new AdvanceSearchPane();
            VBox advanceSearchVBox = advanceSearchPane.getPane();
            advanceSearchVBox.getChildren().add(0, NewStandardSceneHeader("高级搜索"));

            resetStage(advanceSearchVBox, 1, 0.9, menuAdvanceSearch);

            Button search = advanceSearchPane.getSearch();
            search.setOnAction(e -> advanceSearchResult(advanceSearchPane));
        }
        else if (options.equals("menuBackToMain")) {
            Main.startScreen startScreen = new Main.startScreen();
            try {
                startScreen.start(mainStage);
            } catch (Exception e) {
                HandleError error = new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                        e.getMessage(), e.getStackTrace(), false);
                error.WriteToLog();
            }
        }
    }

    /**if
     * reset the stage with the new vbox provided
     * @param vBox vbox in the middle of stage
     * @param width width of the desired stage
     * @param height height of the desired stage
     * @param button buttons that needs focus on the left
     */
    private void resetStage(VBox vBox, double width, double height, Button button) {
        currentBorderPane = CopyBorderPane(currentBorderPane);
        currentBorderPane.setCenter(vBox);
        mainStage.setMinWidth((int) (screenSize.width * width));
        mainStage.setMinHeight((int) (screenSize.height * height));
        currentScene = new Scene(currentBorderPane, (int) (screenSize.width * width),
                (int) (screenSize.height * height));
        mainStage.setScene(currentScene);
        mainStage.centerOnScreen();
        mainStage.sizeToScene();
        mainStage.show();
        button.requestFocus();
    }

    /**
     * Make a copy of the borderpane passed in. Helper Function.
     * @param source the borderpane that needs to be copied
     * @return the copy of borderpane
     */
    private BorderPane CopyBorderPane(BorderPane source) {
        BorderPane newBorderPane = new BorderPane();
        newBorderPane.setTop(source.getTop());
        newBorderPane.setCenter(source.getCenter());
        newBorderPane.setBottom(source.getBottom());
        newBorderPane.setLeft(source.getLeft());
        newBorderPane.setRight(source.getRight());
        this.currentBorderPane = newBorderPane;
        return newBorderPane;
    }

    /**
     * Create a new standard label that will be used across the main scene
     * @param text text needed in the label
     * @return a label with standard font and with the text provided
     */
    private Label NewStandardSceneHeader(String text) {
        Label standardLabel = new Label(text);
        standardLabel.setStyle(standardBigHeader);
        standardLabel.setMaxWidth(Double.MAX_VALUE);
        standardLabel.setAlignment(Pos.CENTER);
        standardLabel.setPadding(new Insets(10, 10, 0, 10));

        return standardLabel;
    }

    /**
     * function that will delete order from database
     * @param selectedOrder the order that the DBU will delete
     * @return null: failed, order: success
     */
    private void DeleteOrder(Order selectedOrder, TableView<Order> orderTableView) {
        if (selectedOrder == null) {
            AlertBox.display("错误", "没有选择订单");
            return;
        }
        boolean confirmation = ConfirmBox.display("确认删除", String.format("确定删除%s的%s订单吗？",
                selectedOrder.getOrderDate().toString(), selectedOrder.getName()), "确定", "取消");
        if (confirmation) {
            try {
                DatabaseUtil.deleteFromMain(selectedOrder.getSerialNum());
                AlertBox.display("成功", "删除成功");
                ObservableList<Order> orderObservableList = orderTableView.getItems();
                orderObservableList.remove(selectedOrder);
                orderTableView.requestFocus();
            } catch (SQLException e) {
                AlertBox.display("错误", String.format("删除失败\n%s", e.getMessage()));
                HandleError error = new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                        e.getMessage(), e.getStackTrace(), false);
                error.WriteToLog();
            }
        }
    }

    /**
     * reset the stage with new vbox, and focus on tableview instead of button
     * @param vBox vbox in the middle of the stage
     * @param width width of the desired stage
     * @param height height of the desired stage
     * @param tableView tableview that needs focus
     */
    private void resetStage(VBox vBox, double width, double height, TableView tableView) {
        currentBorderPane = CopyBorderPane(currentBorderPane);
        vBox.setMaxHeight(Double.MAX_VALUE);
        currentBorderPane.setCenter(vBox);
        mainStage.setMinWidth((int) (screenSize.width * width));
        mainStage.setMinHeight((int) (screenSize.height * height));
        currentScene = new Scene(currentBorderPane, (int) (screenSize.width * width),
                (int) (screenSize.height * height));
        tableView.setMaxHeight(Double.MAX_VALUE);
        tableView.setMaxWidth(Double.MAX_VALUE);
        mainStage.setScene(currentScene);
        mainStage.centerOnScreen();
        mainStage.sizeToScene();
        mainStage.show();
        tableView.requestFocus();
    }

    /**
     * Handles when user clicks the search button in SearchOrUpdateOrderPane
     * @param searchOrUpdateOrderPane the SearchOrUpdateOrderPane where the info resides
     */
    private void searchOrderResult(SearchOrUpdateOrderPane searchOrUpdateOrderPane) {
        try {
            ObservableList<Order> searchOrders = null;
            String command = searchOrUpdateOrderPane.GetFieldVal();

            if (command == null) {
                return;
            }
            if (command.equals("")) {
                searchOrders = DatabaseUtil.GetAllOrder();
                searchOrUpdateOrderPane.clearFields();
            }
            else {
                searchOrders = DatabaseUtil.GetOrderWithSpecifiedCriteria(command);
                searchOrUpdateOrderPane.clearFields();
            }

            SelectedOrderTable selectedOrderTable = new SelectedOrderTable();
            VBox table = selectedOrderTable.CreateTable(searchOrders);

            VBox searchOrderVBox = searchOrUpdateOrderPane.getPane();
            try {
                searchOrderVBox.getChildren().remove(2);
            } catch (Exception e) {
                HandleError error = new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                        e.getMessage(), e.getStackTrace(), true);
                error.WriteToLog();
            }
            searchOrderVBox.getChildren().add(table);

            resetStage(searchOrderVBox, 1, 0.9, selectedOrderTable.getOrderTableView());

            Button deleteButton = selectedOrderTable.getDeleteButton();
            deleteButton.setOnAction(e -> {
                selectedOrderFromTable = selectedOrderTable.getSelectedOrder();
                DeleteOrder(selectedOrderFromTable, selectedOrderTable.getOrderTableView());
            });

        } catch (SQLException e) {
            HandleError error = new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            error.WriteToLog();
        }
    }

    /**
     * Handles when user clicks the search button in SearchOrUpdateSellerPane
     * @param searchOrUpdateSellerPane the SearchOrUpdateOrderPane where the info resides
     */
    private void searchSellerResult(SearchOrUpdateSellerPane searchOrUpdateSellerPane) {
        try {
            ObservableList<Seller> searchSellers = null;
            String command = searchOrUpdateSellerPane.GetFieldVal();

            if (command == null) {
                return;
            }
            if (command.equals("")) {
                searchSellers = DatabaseUtil.GetAllSellerForTable();
                searchOrUpdateSellerPane.clearFields();
            }
            else {
//                System.out.println(command);
                searchSellers = DatabaseUtil.GetSellerWithSpecifiedCriteria(command);
                searchOrUpdateSellerPane.clearFields();
            }

            SelectedSellerTable selectedSellerTable = new SelectedSellerTable();
            VBox table = selectedSellerTable.CreateTable(searchSellers);

            VBox searchSellerVBox = searchOrUpdateSellerPane.getPane();
            try {
                searchSellerVBox.getChildren().remove(2);
            } catch (Exception e) {
                HandleError error = new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                        e.getMessage(), e.getStackTrace(), true);
                error.WriteToLog();
            }
            searchSellerVBox.getChildren().add(table);

            resetStage(searchSellerVBox, 1, 0.9, selectedSellerTable.getSellerTableView());

            Button deleteButton = selectedSellerTable.getDeleteButton();
            deleteButton.setOnAction(e -> {
                selectedSellerFromTable = selectedSellerTable.getSelectedSeller();
                DeleteSeller(selectedSellerFromTable, selectedSellerTable.getSellerTableView());
            });

        } catch (SQLException e) {
            HandleError error = new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            error.WriteToLog();
        }
    }

    /**
     * function that will delete a seller from database
     * @param selectedSeller the seller that the DBU will delete
     * @return null: failed, order: success
     */
    private void DeleteSeller(Seller selectedSeller, TableView<Seller> sellerTableView) {
        if (selectedSeller == null) {
            AlertBox.display("错误", "没有选择订单");
            return;
        }
        boolean confirmation = ConfirmBox.display("确认删除", String.format("确定删除%s？",
                selectedSeller.getCompanyName()), "确定", "取消");
        if (confirmation) {
            try {
                DatabaseUtil.deleteFromSeller(selectedSeller.getSellerId());
                AlertBox.display("成功", "删除成功");
                ObservableList<Seller> sellerObservableList = sellerTableView.getItems();
                sellerObservableList.remove(selectedSeller);
                sellerTableView.requestFocus();
            } catch (SQLException e) {
                AlertBox.display("错误", String.format("删除失败\n%s", e.getMessage()));
                HandleError error = new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                        e.getMessage(), e.getStackTrace(), false);
                error.WriteToLog();
            }
        }
    }

    /**
     * add search result table to the screen, used when button is pressed
     * @param advanceSearchPane input pane
     */
    private void advanceSearchResult(AdvanceSearchPane advanceSearchPane) {
        try {
            ObservableList<Order> searchOrders = null;
            if (advanceSearchPane.getEnterCommand().getText().equals("")) {
                searchOrders = DatabaseUtil.GetAllOrder();
            }
            else {
                searchOrders = DatabaseUtil.GetOrderWithSpecificCommand(advanceSearchPane.getEnterCommand().getText());
            }

            SelectedOrderTable selectedOrderTable = new SelectedOrderTable();
            VBox table = selectedOrderTable.CreateTable(searchOrders);

            VBox searchOrderVBox = advanceSearchPane.getPane();
            try {
                searchOrderVBox.getChildren().remove(4);
            } catch (Exception e) {
                HandleError error = new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                        e.getMessage(), e.getStackTrace(), true);
                error.WriteToLog();
            }
            searchOrderVBox.getChildren().add(table);

            resetStage(searchOrderVBox, 1, 0.9, selectedOrderTable.getOrderTableView());

            Button deleteButton = selectedOrderTable.getDeleteButton();
            deleteButton.setOnAction(e -> {
                selectedOrderFromTable = selectedOrderTable.getSelectedOrder();
                DeleteOrder(selectedOrderFromTable, selectedOrderTable.getOrderTableView());
            });

        } catch (SQLException e) {
            HandleError error = new HandleError("DataBaseUtility", Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            error.WriteToLog();
            AlertBox alertBox = new AlertBox();
            alertBox.display("错误", "失败");
        }
    }
}
