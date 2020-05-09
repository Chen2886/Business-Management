package Product;

import Main.AlertBox;
import Main.DatabaseUtil;
import Main.HandleError;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class ProdGenerateExcel {

    // prod table headers
    // TODO: Add Formula
    private static final String[] prodHeaders = new String[] {"订单日期", "送货单号", "客户", "产品名称",
            "规格", "数量", "公斤", "单价", "金额", "成本价", "备注"};

    // all prod property listed
    private static final String[] prodProperty = new String[]{"orderDate", "sku", "customer", "name",
            "unitAmount", "amount", "kgAmount", "unitPrice", "totalPrice", "basePrice", "note"};

    Stage stage;

    @FXML Button generateButton;
    @FXML Button cancelButton;
    @FXML DatePicker startDate;
    @FXML DatePicker endDate;

    /**
     * Init data called by main controller
     * @param stage the current stage so it can be closed
     */
    public void initData(Stage stage) {
        this.stage = stage;
        init();
    }

    /**
     * Initialize button actions
     */
    public void init() {
        cancelButton.setOnAction(event -> stage.close());
        generateButton.setOnAction(event -> {
            generateExcel(GetFieldVal());
            stage.close();
        });
    }

    private void generateExcel(int[][] input) {
        if (input == null) return;

        String fileName = "产品一览表 " + String.format("%d-%d-%d - %d-%d-%d", input[0][0], input[0][1], input[0][2], input[1][0],
                input[1][1], input[1][2]) + ".xlsx";
        String sheetName = String.format("%d-%d-%d - %d-%d-%d", input[0][0], input[0][1], input[0][2], input[1][0],
                input[1][1], input[1][2]);

        try {
            ArrayList<ProductOrder> selectedData = DatabaseUtil.SelectProductOrderWithDateRange(input);

            XSSFWorkbook workbook;
            XSSFSheet sheet;
            String desktopPath = System.getProperty("user.home") + "/Desktop/";
            File excelFile = new File(desktopPath + fileName);

            if (excelFile.exists()) {
                if (!excelFile.delete()) AlertBox.display("错误", "无法删除文件");
                if (!excelFile.createNewFile()) AlertBox.display("错误", "无法删除文件");
            }

            workbook = new XSSFWorkbook();
            sheet = workbook.createSheet(sheetName);

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


            for (String string: prodHeaders) {
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
                    if (j == 4 || j == 5 || j==6 || j==7 || j==8 || j == 9) {
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

            for (int i = 0; i < prodHeaders.length; i++) {
                sheet.autoSizeColumn(i);
            }
            workbook.write(fileOutputStream);
            fileOutputStream.close();
        } catch (SQLException e) {
            AlertBox.display("错误", "数据库读取失败！");
            new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            }
        catch (IOException e) {
            AlertBox.display("错误", "写入文件失败！");
            new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            } catch (Exception e) {
            AlertBox.display("错误", "删除表格后重试");
            new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
        }
    }

    public int[][] GetFieldVal() {
        int[][] input = new int[2][3];

        if (startDate.getValue().getYear() > endDate.getValue().getYear()) {
            AlertBox.display("错误", "开始日期小于结束日期");
            return null;
        } else if (startDate.getValue().getYear() == endDate.getValue().getYear()) {
            if (startDate.getValue().getMonthValue() > endDate.getValue().getMonthValue()) {
                AlertBox.display("错误", "开始日期小于结束日期");
                return null;
            } else if (startDate.getValue().getMonthValue() == endDate.getValue().getMonthValue()) {
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

}
