package Main;

import Material.MatOrder;
import Material.MatSeller;
import Material.MatUnitPrice;
import Product.ProdUnitPrice;
import Product.ProductOrder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashSet;

public class FinalConstants {

    // material names from material unit prices
    public static String[] autoCompleteMatName;

    // product names from product unit prices
    public static String[] autoCompleteProdName;

    // product customer names from productManagement
    public static String[] autoCompleteProdCustomerName;

    // all material sellers
    public static ObservableList<MatSeller> allMatSellers;

    // all material orders
    public static ObservableList<MatOrder> allMatOrders;

    // all product orders
    public static ObservableList<ProductOrder> allProdOrders;

    // material table headers
    public static final String[] matTableHeaders = new String[]{"订单日期", "订单号", "原料名称", "类别", "付款日期",
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

    // product table headers
    public static final String[] prodTableHeaders = new String[]{"订单日期", "送货单号", "客户", "产品名称",
            "规格", "数量", "公斤", "单价", "金额", "成本价", "备注"};

    // product property listed
    public static final String[] prodPropertyHeaders = new String[]{"orderDate", "sku", "customer", "name",
            "unitAmount", "amount", "kgAmount", "unitPrice", "totalPrice", "basePrice", "note"};

    // all material types
    public static final String[] matOfType = new String[]{"RD", "S", "P", "A", "R", "PA"};

    // save user's last selected tab
    public static int selectedTab;

    public static URL resetWhite;
    public static URL resetBlack;
    public static URL searchWhite;
    public static URL searchBlack;
    public static URL addWhite;
    public static URL addBlack;
    public static URL excelWhite;
    public static URL excelBlack;
    public static URL quitWhite;
    public static URL quitBlack;
    public static URL backWhite;
    public static URL backBlack;

    public static int getSelectedTab() {
        return selectedTab;
    }

    public static void setSelectedTab(int selectedTab) {
        FinalConstants.selectedTab = selectedTab;
    }

    public static void init() {
        updateAutoCompleteMatName();
        updateAutoCompleteProdName();
        updateAllMatSellers();
        updateAllMatOrders();
        updateAllProdOrders();
        initPictures();
        selectedTab = 0;
    }

    public static void initPictures() {
        resetWhite = FinalConstants.class.getResource("/images/resetWhite.png");
        resetBlack = FinalConstants.class.getResource("/images/resetBlack.png");
        searchWhite = FinalConstants.class.getResource("/images/searchWhite.png");
        searchBlack = FinalConstants.class.getResource("/images/searchBlack.png");
        addWhite = FinalConstants.class.getResource("/images/addWhite.png");
        addBlack = FinalConstants.class.getResource("/images/addBlack.png");
        excelWhite = FinalConstants.class.getResource("/images/exportWhite.png");
        excelBlack = FinalConstants.class.getResource("/images/exportBlack.png");
        quitWhite = FinalConstants.class.getResource("/images/powerWhite.png");
        quitBlack = FinalConstants.class.getResource("/images/powerBlack.png");
        backWhite = FinalConstants.class.getResource("/images/backWhite.png");
        backBlack = FinalConstants.class.getResource("/images/backBlack.png");
    }

    public static ObservableList<MatOrder> updateAllMatOrders() {
        try {
            allMatOrders = DatabaseUtil.GetAllMatOrders();
        } catch (SQLException e) {
            allMatOrders = FXCollections.observableArrayList();
            e.printStackTrace();
            new HandleError(FinalConstants.class.getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
        }
        Comparator<MatOrder> comparator = Comparator.comparing(o ->
                LocalDate.of(o.getOrderDate().getY(), o.getOrderDate().getM(), o.getOrderDate().getD()));
        comparator = comparator.reversed();
        allMatOrders.sort(comparator);
        return allMatOrders;
    }

    public static ObservableList<ProductOrder> updateAllProdOrders() {
        try {
            allProdOrders = DatabaseUtil.GetAllProdOrders();
        } catch (SQLException e) {
            allProdOrders = FXCollections.observableArrayList();
            e.printStackTrace();
            new HandleError(FinalConstants.class.getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
        }
        Comparator<ProductOrder> comparator = Comparator.comparing(o ->
                LocalDate.of(o.getOrderDate().getY(), o.getOrderDate().getM(), o.getOrderDate().getD()));
        comparator = comparator.reversed();
        allProdOrders.sort(comparator);
        return allProdOrders;
    }


    public static ObservableList<MatSeller> updateAllMatSellers() {
        try {
            allMatSellers = DatabaseUtil.GetAllMatSellers();
        } catch (SQLException e) {
            allMatSellers = FXCollections.observableArrayList();
            e.printStackTrace();
            new HandleError(FinalConstants.class.getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
        }
        return allMatSellers;
    }

    public static String[] updateAutoCompleteProdCustomerName() {
        try {
            ObservableList<ProdUnitPrice> allProdUnitPrice = DatabaseUtil.GetAllProdUnitPrice();

            // put everything into hashset to avoid duplicates
            HashSet<String> avoidDuplicateSet = new HashSet<>();
            for (ProdUnitPrice unitPrice : allProdUnitPrice)
                avoidDuplicateSet.add(unitPrice.getCustomer());

            // populating the actual array
            autoCompleteProdCustomerName = new String[avoidDuplicateSet.size()];
            int i = 0;
            for (String s : avoidDuplicateSet) autoCompleteProdCustomerName[i++] = s;
        } catch (SQLException e) {
            autoCompleteProdCustomerName = new String[0];
            e.printStackTrace();
            new HandleError(FinalConstants.class.getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
        }
        return autoCompleteProdCustomerName;
    }

    public static String[] updateAutoCompleteProdName() {
        try {
            ObservableList<ProdUnitPrice> allProdUnitPrices = DatabaseUtil.GetAllProdUnitPrice();

            // put everything into hashset to avoid duplicates
            HashSet<String> avoidDuplicateSet = new HashSet<>();
            for (ProdUnitPrice unitPrice : allProdUnitPrices)
                avoidDuplicateSet.add(unitPrice.getName());

            // populating the actual array
            autoCompleteProdName = new String[avoidDuplicateSet.size()];
            int i = 0;
            for (String s : avoidDuplicateSet) autoCompleteProdName[i++] = s;
        } catch (SQLException e) {
            autoCompleteProdName = new String[0];
            e.printStackTrace();
            new HandleError(FinalConstants.class.getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
        }
        return autoCompleteProdName;
    }

    public static String[] updateAutoCompleteMatName() {
        try {
            ObservableList<MatUnitPrice> allMatUnitPrices = DatabaseUtil.GetAllMatUnitPrice();
            autoCompleteMatName = new String[allMatUnitPrices.size()];
            for (int i = 0; i < allMatUnitPrices.size(); i++) {
                autoCompleteMatName[i] = allMatUnitPrices.get(i).getName();
            }
        } catch (SQLException e) {
            autoCompleteMatName = new String[0];
            e.printStackTrace();
            new HandleError(FinalConstants.class.getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
        }
        return autoCompleteMatName;
    }

    /**
     * Set image, when hovered, turn to black, otherwise turn to white
     * @param imageView the button that needs to be changed
     * @param White the white image
     * @param Black the black image
     */
    public static void setButtonImagesAndCursor(ImageView imageView, URL White, URL Black) {
        imageView.setImage(new Image(White.toString()));
        imageView.setOnMouseEntered(event -> {
            imageView.setImage(new Image(Black.toString()));
            Main.mainStage.getScene().setCursor(javafx.scene.Cursor.HAND);
        });
        imageView.setOnMouseExited(event -> {
            imageView.setImage(new Image(White.toString()));
            Main.mainStage.getScene().setCursor(Cursor.DEFAULT);
        });
    }
}
