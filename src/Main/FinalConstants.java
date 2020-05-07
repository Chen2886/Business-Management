package Main;

import Material.MatSeller;
import Material.MatUnitPrice;
import Product.ProdUnitPrice;
import Product.ProductOrder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class FinalConstants {

    // material names from material unit prices
    public static String[] autoCompleteMatName;

    // product names from product unit prices
    public static String[] autoCompleteProdName;

    // product customer names from productManagement
    public static String[] autoCompleteProdCustomerName;

    // all material sellers
    public static ObservableList<MatSeller> allMatSellers;

    // material table headers
    public static final String[] matTableHeaders = new String[] {"订单日期", "订单号", "原料名称", "类别", "付款日期",
            "到达日期", "发票日期", "发票编号", "规格", "数量", "单价", "签收人", "供应商订单编号", "备注", "供应商"};

    // material property headers
    public static final String[] matPropertyHeaders = new String[]{"orderDate", "sku", "name", "type", "paymentDate",
            "arrivalDate", "invoiceDate", "invoice", "unitAmount", "amount", "unitPrice", "signed", "skuSeller",
            "note", "seller"};

    // material seller table headers
    public static final String[] matSellerTableHeaders = new String[]{"供应商",
            "联系人", "手机", "座机", "传真", "供应商账号", "供应商银行地址", "供应商地址"};

    // material seller property headers
    public static final String[] matSellerPropertyHeaders = new String[]{"CompanyName", "ContactName", "Mobile",
            "LandLine", "Fax", "AccountNum", "BankAddress", "Address"};

    // all material types
    public static final String[] matOfType = new String[]{"RD", "S", "P", "A", "R", "PA"};

    public static void init() {
        updateAllMatSellers();
        updateAutoCompleteMatName();
        updateAutoCompleteProdName();
    }


    public static void updateAllMatSellers() {
        try {
            allMatSellers = DatabaseUtil.GetAllMatSellers();
        } catch (SQLException e) {
            allMatSellers = FXCollections.observableArrayList();
            e.printStackTrace();
            HandleError error = new HandleError(FinalConstants.class.getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            error.WriteToLog();
        }
    }

    public static void updateAutoCompleteProdCustomerName() {
        try {
            ObservableList<ProductOrder> allProductOrder = DatabaseUtil.GetAllProdOrders();

            // put everything into hashset to avoid duplicates
            HashSet<String> avoidDuplicateSet = new HashSet<>();
            for(ProductOrder order : allProductOrder)
                avoidDuplicateSet.add(order.getCustomer());

            // populating the actual array
            autoCompleteProdCustomerName = new String[avoidDuplicateSet.size()];
            int i = 0;
            for (String s : avoidDuplicateSet) autoCompleteProdCustomerName[i++] = s;
        } catch (SQLException e) {
            autoCompleteMatName = new String[0];
            e.printStackTrace();
            HandleError error = new HandleError(FinalConstants.class.getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            error.WriteToLog();
        }
    }

    public static void updateAutoCompleteProdName() {
        try {
            ObservableList<ProdUnitPrice> allProdUnitPrices = DatabaseUtil.GetAllProdUnitPrice();

            // put everything into hashset to avoid duplicates
            HashSet<String> avoidDuplicateSet = new HashSet<>();
            for(ProdUnitPrice unitPrice : allProdUnitPrices)
                avoidDuplicateSet.add(unitPrice.getName());

            // populating the actual array
            autoCompleteProdName = new String[avoidDuplicateSet.size()];
            int i = 0;
            for (String s : avoidDuplicateSet) autoCompleteProdName[i++] = s;
        } catch (SQLException e) {
            autoCompleteMatName = new String[0];
            e.printStackTrace();
            HandleError error = new HandleError(FinalConstants.class.getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            error.WriteToLog();
        }
    }

    public static void updateAutoCompleteMatName() {
        try {
            ObservableList<MatUnitPrice> allMatUnitPrices = DatabaseUtil.GetAllMatUnitPrice();
            autoCompleteMatName = new String[allMatUnitPrices.size()];
            for (int i = 0; i < allMatUnitPrices.size(); i++) {
                autoCompleteMatName[i] = allMatUnitPrices.get(i).getName();
            }
        } catch (SQLException e) {
            autoCompleteMatName = new String[0];
            e.printStackTrace();
            HandleError error = new HandleError(FinalConstants.class.getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            error.WriteToLog();
        }
    }
}
