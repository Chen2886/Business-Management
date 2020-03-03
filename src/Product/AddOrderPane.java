package Product;

import Material.AlertBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.util.StringConverter;
import org.apache.xmlbeans.impl.soap.Text;

import java.awt.*;
import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;

public class AddOrderPane {
	private static final String headerStyle = "-fx-border-style: solid;\n" +
			"-fx-border-width: 0 0 1 0;\n" +
			"-fx-border-color: black;\n" +
			"-fx-font: 24 arial;";
	private static final String standardFont = "-fx-font: 15 arial;";
	private final static String[] leftMenu = new String[] {"添加订单", "搜索/更改订单",
			"添加产品", "搜索/更改产品", "添加原料", "搜索/更改原料", "生成/更新一览表",
			"高级搜索", "返回主菜单"};
	private final static String[] propertyHeaders = new String[] {"sku", "orderDate",
			"prod", "customer", "unitAmount", "amount", "unitPrice", "note"};
	private final static String[] tableHeaders = new String[] {"订单编号", "订单日期",
			"产品名称", "客户名称", "规格", "数量", "单价", "备注"};
	private final static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

	// array list of TextFields/Datepicker
	private ArrayList<Node> inputTextFieldArr;

	private Button addFormula;
	private Button searchUnitPrice;
	private Button addOrder;
	private Button addAll;
	private Button clear;
	private Formula tempFormula;

	private VBox addOrderAll;

	private TableView<Order> addOrderTable;
	private ObservableList<Product.Order> currentList;

	/**
	 * set up scene for add order
	 */
	public void initScene() {
		inputTextFieldArr = new ArrayList<>();
		currentList = FXCollections.observableArrayList();
		Collection<TableColumn<Order, ?>> orderColumnArrList = new ArrayList<>();

		for (int i = 0; i < tableHeaders.length; i++) {
			TableColumn<Order, String> newColumn = new TableColumn<>(tableHeaders[i]);
			newColumn.setMinWidth(100);
			newColumn.setCellValueFactory(new PropertyValueFactory<>(propertyHeaders[i]));
			newColumn.setMinWidth(screenSize.width * 0.9 / tableHeaders.length);
			newColumn.setStyle("-fx-alignment: CENTER;");
			orderColumnArrList.add(newColumn);
		}

		// setting up table
		addOrderTable = new TableView<>();
		addOrderTable.getColumns().setAll(orderColumnArrList);
		addOrderAll = new VBox();
		addOrderTable.setMaxWidth(Double.MAX_VALUE);
		addOrderTable.setMinHeight(screenSize.height * 0.4);
		addOrderAll.setPadding(new Insets(10, 10, 10, 10));
		addOrderAll.setSpacing(10);

		// setting up all textfields
		for (int i = 0; i < tableHeaders.length; i++) {
			if (i == 6) {
				TextField unitPriceInput = new TextField();
				unitPriceInput.setPromptText("点击查找按钮");
				unitPriceInput.setDisable(true);
				unitPriceInput.setStyle(standardFont);
				inputTextFieldArr.add(unitPriceInput);
			}
			else if (i == 1) {
				DatePicker tempDatePicker = NewDatePicker();
				inputTextFieldArr.add(tempDatePicker);
			}
			else {
				TextField tempTextField = NewTextField("输入" + tableHeaders[i]);
				inputTextFieldArr.add(tempTextField);
			}
		}

		HBox inputHBox = new HBox();
		inputHBox.setSpacing(10);
		inputHBox.setMaxWidth(Double.MAX_VALUE);
		inputHBox.getChildren().setAll(inputTextFieldArr);
		inputHBox.setPadding(new Insets(0, 10, 0, 10));

		for (int i = 0; i < tableHeaders.length; i++) {
			if (i == 1) {
				((DatePicker) inputTextFieldArr.get(i)).setMaxWidth((addOrderTable.getColumns().get(i).getWidth() - 10));
				System.out.println(((DatePicker) inputTextFieldArr.get(i)).getWidth());
			}
			else {
				((TextField) inputTextFieldArr.get(i)).setMaxWidth(addOrderTable.getColumns().get(i).getWidth() - 10);
				System.out.println(((TextField) inputTextFieldArr.get(i)).getWidth());
			}
		}

		// setting up button HBox
		HBox buttonHBox = new HBox();
		buttonHBox.setSpacing(10);
		buttonHBox.setMaxWidth(Double.MAX_VALUE);
		buttonHBox.setAlignment(Pos.BASELINE_RIGHT);
		clear = NewButton("清空");
		addOrder = NewButton("添加订单");
		addFormula = NewButton("添加配方");
		searchUnitPrice = NewButton("搜索单价");
		addAll = NewButton("添加所有订单");
		buttonHBox.getChildren().setAll(clear, searchUnitPrice, addFormula, addOrder, addAll);

		addOrder.setOnAction(e -> createOrder());
		clear.setOnAction(e -> clearFields());
		searchUnitPrice.setOnAction(e -> getUnitPriceFromDatabase());

		addOrderAll.getChildren().setAll(addOrderTable, inputHBox, buttonHBox);
	}

	/**
	 * Add order to the Database
	 */
	private void createOrder() {
		for (int i = 0; i < inputTextFieldArr.size(); i++) {
			if (i == 4 || i == 5) {
				try {
					Double.parseDouble(((TextField) inputTextFieldArr.get(i)).getText());
				} catch (Exception e) {
					AlertBox.display("错误", "数字输入错误");
					return;
				}
			}
		}
		Order newOrder = new Order(SerialNum.getSerialNum(), ((TextField) inputTextFieldArr.get(0)).getText(),
				((TextField) inputTextFieldArr.get(2)).getText(), ((TextField) inputTextFieldArr.get(3)).getText(),
				Double.parseDouble(((TextField) inputTextFieldArr.get(4)).getText()),
				Double.parseDouble(((TextField) inputTextFieldArr.get(5)).getText()),
				Date.stringToDate(((DatePicker) inputTextFieldArr.get(1)).getValue().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))));
		newOrder.setNote(((TextField) inputTextFieldArr.get(7)).getText());

		currentList.add(newOrder);
		addOrderTable.setItems(currentList);
		clearFields();
	}

	/**
	 * Clear all the fields on screen.
	 */
	private void clearFields() {
		for (int i = 0; i < inputTextFieldArr.size(); i++) {
			if (inputTextFieldArr.get(i) instanceof TextField) {
				((TextField) inputTextFieldArr.get(i)).clear();
			}
		}
	}

	private void getUnitPriceFromDatabase() {
		// TODO: find data value from database
		((TextField) inputTextFieldArr.get(6)).setText("0.00");
	}

	public VBox getPane() {
		return addOrderAll;
	}

	/**
	 * Create a button with specified text
	 * @param text text on the button
	 * @return javaFx Button
	 */
	private Button NewButton(String text) {
		Button returnButton = new Button(text);
		returnButton.setStyle(standardFont);
		returnButton.setMaxWidth(Double.MAX_VALUE);
		return returnButton;
	}

	/**
	 * create a label in the gridpane
	 * @param text text on the label
	 * @param col col in gridpane
	 * @param row row in gridpane
	 * @return the label
	 */
	private Label NewLabel(String text, int col, int row) {
		Label returnLabel = new Label(text);
		returnLabel.setStyle(standardFont);
		GridPane.setConstraints(returnLabel, col, row);
		GridPane.setHalignment(returnLabel, HPos.RIGHT);
		return returnLabel;
	}

	/**
	 * create a textfield in the gridpane
	 * @param text text in the textfield
	 * @return the textfield
	 */
	private TextField NewTextField(String text) {
		TextField returnTextField = new TextField();
		returnTextField.setPromptText(text);
		returnTextField.setStyle(standardFont);
//		returnTextField.setOnKeyPressed(keyEvent -> {
//			if (keyEvent.getCode().equals(KeyCode.ENTER)) {
//				createOrder();
//			}
//		});
		return returnTextField;
	}

	/**
	 * create date picker
	 * @return the date picker
	 */
	private DatePicker NewDatePicker() {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

		DatePicker returnDatePicker = new DatePicker();
		returnDatePicker.setStyle(standardFont);
		returnDatePicker.setValue(LocalDate.now());

//		returnDatePicker.setOnKeyPressed(keyEvent -> {
//			if (keyEvent.getCode().equals(KeyCode.ENTER)) {
//				createOrder();
//			}
//		});

		returnDatePicker.setConverter(new StringConverter<LocalDate>() {
			@Override
			public String toString(LocalDate localDate) {
				if (localDate==null) {
					return "没有选择";
				}
				return dateTimeFormatter.format(localDate);
			}

			@Override
			public LocalDate fromString(String string) {
				if (string==null || string.isEmpty()) {
					return null;
				}
				return LocalDate.from(dateTimeFormatter.parse(string));
			}
		});
		return returnDatePicker;
	}
}
