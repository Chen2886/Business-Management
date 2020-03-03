package Material;

import Main.DatabaseUtil;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.StringConverter;
import org.apache.commons.compress.archivers.dump.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class GenerateExcelPane {

	private static final String[] matOfType = new String[]{"RD", "S", "P", "A", "R", "Pa"};
	private static final String headerStyle = "-fx-border-style: solid;\n" +
			"-fx-border-width: 0 0 1 0;\n" +
			"-fx-border-color: black;\n" +
			"-fx-font: 24 arial;";
	private static final String standardFont = "-fx-font: 16 arial;";

	/**
	 * table headers for SearchOrder and CSV
	 */
	private static final String[] tableHeaders = new String[]{"订单编号", "原料名称", "原料类别", "订单日期", "付款日期",
			"到达日期", "发票日期", "发票编号", "规格", "数量", "公斤", "单价", "总价", "签收人", "供应商订单编号", "供应商名称",
			"供应商联系人", "手机", "座机", "传真", "供应商账号", "供应商银行地址", "供应商地址", "备注"};

	// main scene component
	private VBox generateExcelMainVBox;
	private HBox infoEnterHBox;
	private HBox buttonHBox;
	private GridPane orderGridPane;

	private Region leftRegion;
	private Region rightRegion;

	private DatePicker startDate;
	private DatePicker endDate;

	// order
	private ArrayList<Seller> sellerArrayList;

	public GenerateExcelPane() {
		leftRegion = new Region();
		HBox.setHgrow(leftRegion, Priority.ALWAYS);
		rightRegion = new Region();
		HBox.setHgrow(rightRegion, Priority.ALWAYS);

		orderGridPane = new GridPane();
		buttonHBox = new HBox();
	}

	public void initScene() {
		Button generateButton = NewButton("生成一览表");
		Button clearButton = NewButton("清空");

		GridPane.setHalignment(clearButton, HPos.LEFT);
		clearButton.setOnAction(e -> clearFields());
		generateButton.setOnAction(e -> MenuGenerateCSV(GetFieldVal()));

		buttonHBox.setPadding(new Insets(10, 10, 10, 10));
		buttonHBox.setSpacing(10);
		buttonHBox.getChildren().addAll(clearButton, generateButton);
		buttonHBox.setAlignment(Pos.BOTTOM_RIGHT);

		initGrid();

		Label infoHeaders = new Label("填写以下内容");
		infoHeaders.setMaxWidth(Double.MAX_VALUE);
		infoHeaders.setAlignment(Pos.CENTER);
		infoHeaders.setPadding(new Insets(20, 10, 0, 10));
		infoHeaders.setStyle(headerStyle);

		VBox orderGridPaneAndButtonVBox = new VBox(infoHeaders, orderGridPane, buttonHBox);

		infoEnterHBox = new HBox(leftRegion, orderGridPaneAndButtonVBox, rightRegion);
		infoEnterHBox.setSpacing(10);

		generateExcelMainVBox = new VBox(infoEnterHBox);
		generateExcelMainVBox.setSpacing(10);

	}

	private void initGrid() {

		Label startLabel = NewLabel("输入开始日期", 0, 0);
		startDate = NewDatePicker(1, 0);

		Label endLabel = NewLabel("输入结束日期", 2, 0);
		endDate = NewDatePicker(3, 0);

		orderGridPane.setPadding(new Insets(10, 10, 10, 10));
		orderGridPane.setVgap(8);
		orderGridPane.setHgap(10);

		orderGridPane.getChildren().setAll(startLabel, startDate, endLabel, endDate, buttonHBox);
	}

	public void clearFields() {
		for (Node element: orderGridPane.getChildren()) {
			if (element instanceof TextField) {
				((TextField) element).clear();
			}
			else if (element instanceof ComboBox) {
				((ComboBox) element).getSelectionModel().clearSelection();
			}
			else if (element instanceof DatePicker) {
				((DatePicker) element).setValue(null);
			}
		}
	}

	public VBox getPane() {
		return generateExcelMainVBox;
	}

	public int[][] GetFieldVal() {
		int[][] input = new int[2][3];

		if (startDate.getValue().getYear() > endDate.getValue().getYear()) {
			AlertBox.display("错误", "开始日期小于结束日期");
			return null;
		}
		else if (startDate.getValue().getYear() == endDate.getValue().getYear()) {
			if (startDate.getValue().getMonthValue() > endDate.getValue().getMonthValue()) {
				AlertBox.display("错误", "开始日期小于结束日期");
				return null;
			}
			else if (startDate.getValue().getMonthValue() == endDate.getValue().getMonthValue()) {
				// month okay
				if (startDate.getValue().getDayOfMonth() > endDate.getValue().getDayOfMonth()) {
					AlertBox.display("错误", "开始日期小于结束日期");
					return null;
				}
			}
		}

		input[0][0] = startDate.getValue().getYear();
		input[0][1] = startDate.getValue().getMonthValue();
		input[0][2] = startDate.getValue().getDayOfMonth();

		input[1][0] = endDate.getValue().getYear();
		input[1][1] = endDate.getValue().getMonthValue();
		input[1][2] = endDate.getValue().getDayOfMonth();

		return input;
	}

	private ComboBox NewCombo(String[] text, int col, int row) {
		ComboBox returnComboBox = new ComboBox();
		returnComboBox.getItems().addAll(text);
		returnComboBox.setStyle(standardFont);
		returnComboBox.setMaxWidth(Double.MAX_VALUE);
		GridPane.setConstraints(returnComboBox, col, row);
		return returnComboBox;
	}

	private Label NewLabel(String text, int col, int row) {
		Label returnLabel = new Label(text);
		returnLabel.setStyle(standardFont);
		GridPane.setConstraints(returnLabel, col, row);
		GridPane.setHalignment(returnLabel, HPos.RIGHT);
		return returnLabel;
	}

	private Button NewButton(String text) {
		Button returnButton = new Button(text);
		returnButton.setStyle(standardFont);
		returnButton.setMaxWidth(Double.MAX_VALUE);
		return returnButton;
	}

	private static void MenuGenerateCSV(int[][] input) {
		if (input == null) return;
		String headers = "订单编号,原料名称,原料类别,订单日期,付款日期,到达日期,发票日期,发表编号,规格,数量,公斤,单价,总价,签收人," +
				"供应商订单编号,备注,供应商编号,供应商名称,供应商联系人名称,供应商手机,供应商座机,供应商传真,供应商银行账户," +
				"供应商银行地址,供应商地址";
		String fileName = "原料一览表.xlsx";
		String sheetName = String.format("%d-%d-%d - %d-%d-%d", input[0][0], input[0][1], input[0][2], input[1][0],
				input[1][1], input[1][2]);

		AlertBox.display("确认", "确认Excel表格已关闭");

		try {
			ArrayList<Order> selectedData = DatabaseUtil.SelectDataWithinRange(input);

			XSSFWorkbook workbook = null;
			XSSFSheet sheet = null;
			File excelFile = new File(fileName);


			if (excelFile.exists()) {
				InputStream inputStream = new FileInputStream(excelFile);
				try {
					workbook = new XSSFWorkbook(inputStream);
					try {
						sheet = workbook.createSheet(sheetName);
					} catch (IllegalArgumentException e) {
						sheet = workbook.getSheet(sheetName);
						HandleError error = new HandleError(GenerateExcelPane.class.getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
								e.getMessage(), e.getStackTrace(), false);
						error.WriteToLog();
					}
				} catch (InvalidFormatException e) {
					AlertBox.display("错误", "生成失败");
					HandleError error = new HandleError(GenerateExcelPane.class.getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
							e.getMessage(), e.getStackTrace(), false);
					error.WriteToLog();
					return;
				} finally {
					inputStream.close();
				}
			}
			else {
				workbook = new XSSFWorkbook();
				sheet = workbook.createSheet(sheetName);
			}

			FileOutputStream fileOutputStream = new FileOutputStream(excelFile);

			int rowNum = 1;
			int cellNum = 0;

			Row headerRow = sheet.createRow(0);

			CellStyle cellStyle = workbook.createCellStyle();
			org.apache.poi.ss.usermodel.Font boldFont = workbook.createFont();
			boldFont.setFontName("Calibri");
			boldFont.setBold(true);
			boldFont.setFontHeightInPoints((short) 24);

			Font font = workbook.createFont();
			font.setFontHeightInPoints((short) 16);
			font.setFontName("Calibri");
			cellStyle.setFont(font);

			cellStyle.setAlignment(HorizontalAlignment.CENTER);


			for (String string: tableHeaders) {
				org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(cellNum++);
				cellStyle.setAlignment(HorizontalAlignment.CENTER);
				cellStyle.setFont(boldFont);
				cell.setCellStyle(cellStyle);
				cell.setCellValue(string);
			}

			cellStyle.setFont(font);

			for (int i = 0; i < selectedData.size(); i++) {
				Row row = sheet.createRow(rowNum++);
				cellNum = 0;
				String[] orderObjArr = selectedData.get(i).toString().split(",");
				for (int j = 0; j < orderObjArr.length; j++) {
					Cell cell = row.createCell(cellNum++);
					if (j == 8 || j == 9 || j==10 || j==11 || j==12) {
						double value = Double.parseDouble(orderObjArr[j]);
						cellStyle.setAlignment(HorizontalAlignment.RIGHT);
						cell.setCellStyle(cellStyle);
						cell.setCellValue(value);
					} else {
						cellStyle.setAlignment(HorizontalAlignment.CENTER);
						cell.setCellStyle(cellStyle);
						cell.setCellValue(orderObjArr[j]);
					}
				}
			}

			for (int i = 0; i < tableHeaders.length; i++) {
				sheet.autoSizeColumn(i);
			}
			workbook.write(fileOutputStream);
			fileOutputStream.close();
		} catch (SQLException e) {
			AlertBox.display("失败", "没有数据");
			HandleError error = new HandleError(GenerateExcelPane.class.getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
					e.getMessage(), e.getStackTrace(), false);
			error.WriteToLog();
		}
		catch (IOException e) {
			HandleError error = new HandleError(GenerateExcelPane.class.getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
					e.getMessage(), e.getStackTrace(), false);
			error.WriteToLog();
		} catch (Exception e) {
			AlertBox.display("失败", "删除表格后重试");
			HandleError error = new HandleError(GenerateExcelPane.class.getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
					e.getMessage(), e.getStackTrace(), false);
			error.WriteToLog();

		}
	}

	/**
	 * create date picker
	 * @param col col in gridpane
	 * @param row row in gridpane
	 * @return the date picker
	 */
	private DatePicker NewDatePicker(int col, int row) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

		DatePicker returnDatePicker = new DatePicker(LocalDate.now());
		returnDatePicker.setStyle(standardFont);

		returnDatePicker.setConverter(new StringConverter<LocalDate>() {
			@Override
			public String toString(LocalDate localDate) {
				if (localDate==null) {
					return "";
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
		GridPane.setConstraints(returnDatePicker, col, row);
		return returnDatePicker;
	}
}