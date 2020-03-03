package Main;

import Material.Date;
import Material.HandleError;
import Material.Order;
import Material.Seller;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;

public class DatabaseUtil {
    private static String dataBaseLocationFile = "jdbc:sqlite:BusinessCashFlow.db";
    private static Connection connection;

    /**
     * Update connection to the database
     * @throws SQLException If connection cannot be established to the database
     */
    private static void ConnectToDB() throws SQLException {
        if (connection!=null) {
            return;
        }
        try {
            connection = DriverManager.getConnection(dataBaseLocationFile);
        } catch (SQLException e) {
            HandleError error = new HandleError("DatabaseUtil", Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            error.WriteToLog();
            throw new SQLException();
        }
    }

    /**
     * Make sure both table exists. If not exists, create both table
     * @throws SQLException if any error occurs while operating on database
     */
    public static void CheckIfTableExists() throws SQLException {
        try {
            ConnectToDB();

            DatabaseMetaData databaseMetaData = connection.getMetaData();
            ResultSet resultSet = databaseMetaData.getTables(null, null, "materialManagement", null);
            int numOfTable = 0;
            // TODO: add more tables
            while (resultSet.next()) {
                if ("materialManagement".equals(resultSet.getString("TABLE_NAME"))) {
                    numOfTable++;
                } else if ("seller".equals(resultSet.getString("TABLE_NAME"))) {
                    numOfTable++;
                } else if ("orderManagement".equals(resultSet.getString("TABLE_NAME"))) {
                    numOfTable++;
                }
            }
            if (numOfTable == 3) {
                CloseConnectionToDB();
            } else {
                CreateNewTable("materialManagement");
                CreateNewTable("seller");
                CreateNewTable("orderManagement");
            }
        } catch (SQLException e) {
            HandleError error = new HandleError("DatabaseUtil", Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            error.WriteToLog();
            throw new SQLException();
        } finally {
            CloseConnectionToDB();
        }
    }

    /**
     * create a new table in the database
     * @param tableName indication of which table to create
     * @throws SQLException if any error occurs while operating on database
     */
    public static void CreateNewTable(String tableName) throws SQLException {
        try {
            ConnectToDB();
            Statement statement = connection.createStatement();
            String SQLCommand = "";

            if (tableName.equals("materialManagement")) {
                // 34 Col
                SQLCommand = "CREATE TABLE IF NOT EXISTS [materialManagement] (" +
                        "serialNum 		INTEGER	PRIMARY KEY	NOT NULL,\n" +
                        "sku     		TEXT	NOT NULL,\n" +
                        "name    		TEXT	NOT NULL,\n" +
                        "type    		TEXT	NOT NULL,\n" +
                        "orderDateYear	INTEGER	NOT NULL,\n" +
                        "orderDateMonth	INTEGER	NOT NULL,\n" +
                        "orderDateDay	INTEGER	NOT NULL,\n" +
                        "paymentDateYear	INTEGER	NOT NULL,\n" +
                        "paymentDateMonth	INTEGER	NOT NULL,\n" +
                        "paymentDateDay 	INTEGER	NOT NULL,\n" +
                        "arrivalDateYear	INTEGER	NOT NULL,\n" +
                        "arrivalDateMonth	INTEGER	NOT NULL,\n" +
                        "arrivalDateDay 	INTEGER	NOT NULL,\n" +
                        "invoiceDateYear	INTEGER	NOT NULL,\n" +
                        "invoiceDateMonth	INTEGER	NOT NULL,\n" +
                        "invoiceDateDay 	INTEGER	NOT NULL,\n" +
                        "invoice		TEXT			,\n" +
                        "unitAmount		REAL	NOT NULL,\n" +
                        "amount			REAL	NOT NULL,\n" +
                        "kgAmount		REAL			,\n" +
                        "unitPrice		REAL	        ,\n" +
                        "totalPrice		REAL			,\n" +
                        "signed 		TEXT			,\n" +
                        "skuSeller  	TEXT			,\n" +
                        "note			TEXT			,\n" +
                        "sellerId		INTEGER	NOT NULL,\n" +
                        "companyName	TEXT	NOT NULL,\n" +
                        "contactName	TEXT	NOT NULL,\n" +
                        "mobile 		TEXT			,\n" +
                        "landLine		TEXT			,\n" +
                        "fax			TEXT	        ,\n" +
                        "accountNum		TEXT			,\n" +
                        "bankAddress	TEXT			,\n" +
                        "address		TEXT			\n" +
                        ");";
            } else if (tableName.equals("seller")) {
                SQLCommand = "CREATE TABLE IF NOT EXISTS [seller] (\n" +
                        "sellerId	 	INTEGER	PRIMARY KEY	NOT NULL,\n" +
                        "companyName	TEXT	NOT NULL,\n" +
                        "contactName	TEXT	NOT NULL,\n" +
                        "mobile 		TEXT			,\n" +
                        "landLine		TEXT			,\n" +
                        "fax			TEXT	        ,\n" +
                        "accountNum		TEXT			,\n" +
                        "bankAddress	TEXT			,\n" +
                        "address		TEXT			\n" +
                        ");";
            } else if (tableName.equals("orderManagement")) {
                SQLCommand = "CREATE TABLE IF NOT EXISTS [orderManagement] (\n" +
                        "serialNum	 	INTEGER	PRIMARY KEY	NOT NULL,\n" +
                        "sku        	TEXT	NOT NULL,\n" +
                        "prod       	TEXT	NOT NULL,\n" +
                        "customer 		TEXT			,\n" +
                        "note   		TEXT			,\n" +
                        "orderDate		TEXT			,\n" +
                        "unitAmount		REAL	        ,\n" +
                        "amount 		REAL			,\n" +
                        "unitPrice  	REAL			\n" +
                        ");";
            }
            statement.execute(SQLCommand);
            CloseConnectionToDB();
        } catch (SQLException e) {
            HandleError error = new HandleError("DatabaseUtil", Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            error.WriteToLog();
            throw new SQLException();
        } finally {
            CloseConnectionToDB();
        }
    }

    /**
     * Terminate any connection to database.
     */
    public static void CloseConnectionToDB() {
        if (connection == null) {
            return;
        }
        try {
            connection.close();
            connection = null;
        } catch (SQLException e) {
            HandleError error = new HandleError("DatabaseUtil", Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            error.WriteToLog();
            connection = null;
        }
    }

    /**
     * Create database if not exist
     * @throws SQLException if any error occurs while operating on database
     */
    private static void CreateNewDB() throws SQLException {
        ConnectToDB();
        try {
            connection = DriverManager.getConnection(dataBaseLocationFile);
            if (connection!=null) {
                DatabaseMetaData meta = connection.getMetaData();
            }
        } catch (SQLException e) {
            HandleError error = new HandleError("DatabaseUtil", Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            error.WriteToLog();
            throw new SQLException();
        } finally {
            CloseConnectionToDB();
        }
    }

    /**
     * If DB does not exists, create DB
     * @throws SQLException if any error occurs while operating on database
     */
    public static void CheckIfDBExists() throws SQLException {
        try {
            ConnectToDB();
            if (connection == null) {
                CloseConnectionToDB();
                CreateNewDB();
            }
        } catch (SQLException e) {
            HandleError error = new HandleError("DatabaseUtil", Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            error.WriteToLog();
            throw new SQLException();
        } finally {
            CloseConnectionToDB();
        }
    }

    /**
     * Check if DB, table exists. If no, create and return
     * @return if successful return true, if any error occurs, return false
     */
    public static boolean ConnectionInitAndCreate() {
        try {
            CheckIfDBExists();
            CheckIfTableExists();
            CloseConnectionToDB();
            return true;
        } catch (SQLException e) {
            HandleError error = new HandleError("DatabaseUtil", Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            error.WriteToLog();
            return false;
        } finally {
            CloseConnectionToDB();
        }
    }

    /**
     * get all the seller from the seller table
     * @return an array list of seller
     * @throws SQLException if any error occurs while operating on database
     */
    public static ArrayList<Seller> GetAllSeller() throws SQLException {
        String SQLCommand = "SELECT * FROM seller";
        try {
            ConnectToDB();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(SQLCommand);
            ArrayList<Seller> sellerArrayList = new ArrayList<>();

            while (resultSet.next()) {
                int sellerId = resultSet.getInt("sellerId");
                String companyName = resultSet.getString("companyName");
                String contactName = resultSet.getString("contactName");
                Seller seller = new Seller(sellerId, companyName, contactName);
                seller.setMobile(resultSet.getString("mobile"));
                seller.setLandLine(resultSet.getString("landLine"));
                seller.setFax(resultSet.getString("fax"));
                seller.setAccountNum(resultSet.getString("accountNum"));
                seller.setBankAddress(resultSet.getString("bankAddress"));
                seller.setAddress(resultSet.getString("address"));
                sellerArrayList.add(seller);
            }
            CloseConnectionToDB();
            return sellerArrayList;
        } catch (SQLException e) {
            //System.out.println("getAllSeller failed");
            HandleError error = new HandleError("DatabaseUtil", Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            error.WriteToLog();
            throw new SQLException();
        } finally {
            CloseConnectionToDB();
        }
    }

    /**
     * Insert an order into the database
     * @param order specified order
     * @throws SQLException if any error occurs while operating on database
     */
    public static void InsertToMatManagement(Order order) throws SQLException {
        try {
            ConnectToDB();
            String SQLCommand = "INSERT INTO [materialManagement] (sku, name, type, orderDateYear, " +
                    "orderDateMonth, orderDateDay, unitAmount, amount, kgAmount, unitPrice, totalPrice, sellerId, " +
                    "companyName, contactName, mobile, landLine, fax, accountNum, bankAddress, address, paymentDateYear," +
                    "paymentDateMonth, paymentDateDay, arrivalDateYear, arrivalDateMonth, arrivalDateDay, " +
                    "invoiceDateYear, invoiceDateMonth, invoiceDateDay, invoice, signed, skuSeller, note, serialNum) " +
                    "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(SQLCommand);
            preparedStatement.setString(1, order.getSku());
            preparedStatement.setString(2, order.getName());
            preparedStatement.setString(3, order.getType());
            preparedStatement.setInt(4, order.getOrderDate().getYear());
            preparedStatement.setInt(5, order.getOrderDate().getMonth());
            preparedStatement.setInt(6, order.getOrderDate().getDay());
            preparedStatement.setDouble(7, order.getUnitAmount());
            preparedStatement.setDouble(8, order.getAmount());
            preparedStatement.setDouble(9, order.getKgAmount());
            preparedStatement.setDouble(10, order.getUnitPrice());
            preparedStatement.setDouble(11, order.getTotalPrice());
            preparedStatement.setInt(12, order.getSeller().getSellerId());
            preparedStatement.setString(13, order.getSeller().getCompanyName());
            preparedStatement.setString(14, order.getSeller().getContactName());
            preparedStatement.setString(15, order.getSeller().getMobile());
            preparedStatement.setString(16, order.getSeller().getLandLine());
            preparedStatement.setString(17, order.getSeller().getFax());
            preparedStatement.setString(18, order.getSeller().getAccountNum());
            preparedStatement.setString(19, order.getSeller().getBankAddress());
            preparedStatement.setString(20, order.getSeller().getAddress());

            preparedStatement.setString(21, order.getPaymentDate() == null ? "" : String.valueOf(order.getPaymentDate().getYear()));
            preparedStatement.setString(22, order.getPaymentDate() == null ? "" : String.valueOf(order.getPaymentDate().getMonth()));
            preparedStatement.setString(23, order.getPaymentDate() == null ? "" : String.valueOf(order.getPaymentDate().getDay()));

            preparedStatement.setString(24, order.getArrivalDate() == null ? "" : String.valueOf(order.getArrivalDate().getYear()));
            preparedStatement.setString(25, order.getArrivalDate() == null ? "" : String.valueOf(order.getArrivalDate().getMonth()));
            preparedStatement.setString(26, order.getArrivalDate() == null ? "" : String.valueOf(order.getArrivalDate().getDay()));

            preparedStatement.setString(27, order.getInvoiceDate() == null ? "" : String.valueOf(order.getInvoiceDate().getYear()));
            preparedStatement.setString(28, order.getInvoiceDate() == null ? "" : String.valueOf(order.getInvoiceDate().getMonth()));
            preparedStatement.setString(29, order.getInvoiceDate() == null ? "" : String.valueOf(order.getInvoiceDate().getDay()));

            preparedStatement.setString(30, order.getInvoice() == null ? "" : order.getInvoice());
            preparedStatement.setString(31, order.getSigned() == null ? "" : order.getSigned());
            preparedStatement.setString(32, order.getSkuSeller() == null ? "" : order.getSkuSeller());
            preparedStatement.setString(33, order.getNote() == null ? "" : order.getNote());
            preparedStatement.setInt(34, order.getSerialNum());
            preparedStatement.executeUpdate();
            CloseConnectionToDB();
        } catch (SQLException e) {
            e.printStackTrace();
            HandleError error = new HandleError("DatabaseUtil", Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            error.WriteToLog();
            throw new SQLException();
        } finally {
            CloseConnectionToDB();
        }
    }

    /**
     * get all orders from the database
     * @throws SQLException if any error occurs while operating on database
     */
    public static ObservableList<Order> GetAllOrder() throws SQLException {
        String SQLCommand = "SELECT * FROM materialManagement";
        ObservableList<Order> orderObservableList = FXCollections.observableArrayList();
        try {
            ConnectToDB();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(SQLCommand);
            while (resultSet.next()) {
                String sku = resultSet.getString("sku");
                String name = resultSet.getString("name");
                String type = resultSet.getString("type");
                Material.Date orderDate = new Material.Date(resultSet.getInt("orderDateYear"),
                        resultSet.getInt("orderDateMonth"),
                        resultSet.getInt("orderDateDay"));
                Material.Date paymentDate = new Material.Date(resultSet.getInt("paymentDateYear"),
                        resultSet.getInt("paymentDateMonth"),
                        resultSet.getInt("paymentDateDay"));
                Material.Date arrivalDate = new Material.Date(resultSet.getInt("arrivalDateYear"),
                        resultSet.getInt("arrivalDateMonth"),
                        resultSet.getInt("arrivalDateDay"));
                Material.Date invoiceDate = new Material.Date(resultSet.getInt("invoiceDateYear"),
                        resultSet.getInt("invoiceDateMonth"),
                        resultSet.getInt("invoiceDateDay"));
                String invoice = resultSet.getString("invoice");
                double unitAmount = resultSet.getDouble("unitAmount");
                double amount = resultSet.getDouble("amount");
                double kgAmount = resultSet.getDouble("kgAmount");
                double unitPrice = resultSet.getDouble("unitPrice");
                double totalPrice = resultSet.getDouble("totalPrice");
                String note = resultSet.getString("note");
                String signed = resultSet.getString("signed");
                String skuSeller = resultSet.getString("skuSeller");
                int serialNum = resultSet.getInt("serialNum");

                Seller seller = new Seller(resultSet.getInt("sellerId"),
                        resultSet.getString("companyName"),
                        resultSet.getString("contactName"));
                seller.setMobile(resultSet.getString("mobile"));
                seller.setLandLine(resultSet.getString("landLine"));
                seller.setFax(resultSet.getString("fax"));
                seller.setAccountNum(resultSet.getString("accountNum"));
                seller.setBankAddress(resultSet.getString("bankAddress"));
                seller.setAddress(resultSet.getString("address"));

                // new order
                Order newOrder = new Order(serialNum, sku, name, type, orderDate, seller, unitAmount, amount);
                try {
                    newOrder.setUnitPrice(unitPrice);
                } catch (Exception ignored) {}
                newOrder.setPaymentDate(paymentDate);
                newOrder.setArrivalDate(arrivalDate);
                newOrder.setInvoiceDate(invoiceDate);
                newOrder.setInvoice(invoice);
                newOrder.setNote(note);
                newOrder.setSigned(signed);
                newOrder.setSkuSeller(skuSeller);

                orderObservableList.add(newOrder);
            }
            CloseConnectionToDB();
            return orderObservableList;
        } catch (SQLException e) {
            //System.out.println("GetAllOrder failed");
            e.printStackTrace();
            HandleError error = new HandleError("DataBaseUtility", Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            error.WriteToLog();
            throw new SQLException();
        } finally {
            CloseConnectionToDB();
        }
    }

    /**
     * return orders with specific criteria
     * @param command specific criteria
     * @return List of orders
     * @throws SQLException if any error occurs while operating on database
     */
    public static ObservableList<Order> GetOrderWithSpecifiedCriteria(String command) throws SQLException {
        String SQLCommand = "SELECT * FROM materialManagement " + command;
        ObservableList<Order> orderObservableList = FXCollections.observableArrayList();
        try {
            ConnectToDB();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(SQLCommand);
            while (resultSet.next()) {
                String sku = resultSet.getString("sku");
                String name = resultSet.getString("name");
                String type = resultSet.getString("type");
                Material.Date orderDate = new Material.Date(resultSet.getInt("orderDateYear"),
                        resultSet.getInt("orderDateMonth"),
                        resultSet.getInt("orderDateDay"));
                Material.Date paymentDate = new Material.Date(resultSet.getInt("paymentDateYear"),
                        resultSet.getInt("paymentDateMonth"),
                        resultSet.getInt("paymentDateDay"));
                Material.Date arrivalDate = new Material.Date(resultSet.getInt("arrivalDateYear"),
                        resultSet.getInt("arrivalDateMonth"),
                        resultSet.getInt("arrivalDateDay"));
                Material.Date invoiceDate = new Material.Date(resultSet.getInt("invoiceDateYear"),
                        resultSet.getInt("invoiceDateMonth"),
                        resultSet.getInt("invoiceDateDay"));
                String invoice = resultSet.getString("invoice");
                double unitAmount = resultSet.getDouble("unitAmount");
                double amount = resultSet.getDouble("amount");
                double kgAmount = resultSet.getDouble("kgAmount");
                double unitPrice = resultSet.getDouble("unitPrice");
                double totalPrice = resultSet.getDouble("totalPrice");
                String note = resultSet.getString("note");
                String signed = resultSet.getString("signed");
                String skuSeller = resultSet.getString("skuSeller");
                int serialNum = resultSet.getInt("serialNum");

                Seller seller = new Seller(resultSet.getInt("sellerId"),
                        resultSet.getString("companyName"),
                        resultSet.getString("contactName"));
                seller.setMobile(resultSet.getString("mobile"));
                seller.setLandLine(resultSet.getString("landLine"));
                seller.setFax(resultSet.getString("fax"));
                seller.setAccountNum(resultSet.getString("accountNum"));
                seller.setBankAddress(resultSet.getString("bankAddress"));
                seller.setAddress(resultSet.getString("address"));

                // new order
                Order newOrder = new Order(serialNum, sku, name, type, orderDate, seller, unitAmount, amount);
                try {
                    newOrder.setUnitPrice(unitPrice);
                } catch (Exception ignored) {}
                newOrder.setPaymentDate(paymentDate);
                newOrder.setArrivalDate(arrivalDate);
                newOrder.setInvoiceDate(invoiceDate);
                newOrder.setInvoice(invoice);
                newOrder.setNote(note);
                newOrder.setSigned(signed);
                newOrder.setSkuSeller(skuSeller);

                orderObservableList.add(newOrder);
            }
            CloseConnectionToDB();
            return orderObservableList;
        } catch (SQLException e) {
            HandleError error = new HandleError("DataBaseUtility", Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            error.WriteToLog();
            throw new SQLException();
        } finally {
            CloseConnectionToDB();
        }
    }

    public static void deleteFromMain(int serialNum) throws SQLException {
        try {
            ConnectToDB();

            if (!checkIfSkuExists(serialNum)) {
                CloseConnectionToDB();
                return;
            }
            ConnectToDB();
            String SQLCommand = "DELETE FROM materialManagement WHERE serialNum = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(SQLCommand);
            preparedStatement.setInt(1, serialNum);
            preparedStatement.executeUpdate();
            CloseConnectionToDB();
        } catch (SQLException e) {
            HandleError error = new HandleError("DataBaseUtility", Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            error.WriteToLog();
            e.printStackTrace();
            throw new SQLException();
        } finally {
            CloseConnectionToDB();
        }
    }

    public static boolean checkIfSkuExists(int serialNum) throws SQLException {
        try {
            ConnectToDB();

            String SQLCommand = "SELECT serialNum FROM materialManagement WHERE serialNum = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(SQLCommand);
            preparedStatement.setInt(1, serialNum);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                CloseConnectionToDB();
                return true;
            } else {
                CloseConnectionToDB();
                return false;
            }

        } catch (SQLException e) {
            //System.out.println("checkIfSkuExists failed");
            HandleError error = new HandleError("DataBaseUtility", Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            error.WriteToLog();
            e.printStackTrace();
            throw new SQLException();
        } finally {
            CloseConnectionToDB();
        }
    }

    public static void InsertToSeller(String tableName, Seller seller) throws SQLException {
        try {
            ConnectToDB();
            String SQLCommand = "INSERT INTO [" + tableName + "] (sellerId, companyName, contactName, mobile, landLine, fax," +
                    "accountNum, bankAddress, address) VALUES(?,?,?,?,?,?,?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(SQLCommand);
            preparedStatement.setInt(1, seller.getSellerId());
            preparedStatement.setString(2, seller.getCompanyName());
            preparedStatement.setString(3, seller.getContactName());
            preparedStatement.setString(4, seller.getMobile());
            preparedStatement.setString(5, seller.getLandLine());
            preparedStatement.setString(6, seller.getFax());
            preparedStatement.setString(7, seller.getAccountNum());
            preparedStatement.setString(8, seller.getBankAddress());
            preparedStatement.setString(9, seller.getAddress());
            //System.out.println("here");
            preparedStatement.executeUpdate();
            CloseConnectionToDB();
        } catch (SQLException e) {
            HandleError error = new HandleError("DataBaseUtility", Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            error.WriteToLog();
            throw new SQLException();
        } finally {
            CloseConnectionToDB();
        }
    }

    public static ObservableList<Seller> GetAllSellerForTable() throws SQLException {
        String SQLCommand = "SELECT * FROM seller";
        ObservableList<Seller> sellerObservableList = FXCollections.observableArrayList();
        try {
            ConnectToDB();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(SQLCommand);
            while (resultSet.next()) {
                Seller seller = new Seller(resultSet.getInt("sellerId"),
                        resultSet.getString("companyName"),
                        resultSet.getString("contactName"));
                seller.setMobile(resultSet.getString("mobile"));
                seller.setLandLine(resultSet.getString("landLine"));
                seller.setFax(resultSet.getString("fax"));
                seller.setAccountNum(resultSet.getString("accountNum"));
                seller.setBankAddress(resultSet.getString("bankAddress"));
                seller.setAddress(resultSet.getString("address"));
                sellerObservableList.add(seller);
            }
            CloseConnectionToDB();
            return sellerObservableList;
        } catch (SQLException e) {
            //System.out.println("getAllSeller failed");
            HandleError error = new HandleError("DataBaseUtility", Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            error.WriteToLog();
            throw new SQLException();
        } finally {
            CloseConnectionToDB();
        }
    }

    public static ObservableList<Seller> GetSellerWithSpecifiedCriteria(String command) throws SQLException {
        String SQLCommand = "SELECT * FROM seller " + command;
        ObservableList<Seller> orderObservableList = FXCollections.observableArrayList();
        try {
            ConnectToDB();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(SQLCommand);
            while (resultSet.next()) {
                Seller seller = new Seller(resultSet.getInt("sellerId"),
                        resultSet.getString("companyName"),
                        resultSet.getString("contactName"));
                seller.setMobile(resultSet.getString("mobile"));
                seller.setLandLine(resultSet.getString("landLine"));
                seller.setFax(resultSet.getString("fax"));
                seller.setAccountNum(resultSet.getString("accountNum"));
                seller.setBankAddress(resultSet.getString("bankAddress"));
                seller.setAddress(resultSet.getString("address"));

                orderObservableList.add(seller);
            }
            CloseConnectionToDB();
            return orderObservableList;
        } catch (SQLException e) {
            //System.out.println("GetSellerWithSpecifiedCriteria failed");
            HandleError error = new HandleError("DataBaseUtility", Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            error.WriteToLog();
            throw new SQLException();
        } finally {
            CloseConnectionToDB();
        }
    }

    public static void deleteFromSeller(int sellerId) throws SQLException {
        try {
            ConnectToDB();
            String SQLCommand = "DELETE FROM seller WHERE sellerId = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(SQLCommand);
            preparedStatement.setInt(1, sellerId);
            preparedStatement.executeUpdate();
            CloseConnectionToDB();
        } catch (SQLException e) {
            //System.out.println("deleteFromTable failed");
            HandleError error = new HandleError("DataBaseUtility", Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            error.WriteToLog();
            e.printStackTrace();
            throw new SQLException();
        } finally {
            CloseConnectionToDB();
        }
    }

    /**
     * update all related order in main
     * @param newSeller new seller
     * @throws SQLException if any error occurs while operating on database
     */
    public static void UpdateSellerInMain(Seller newSeller) throws SQLException {
        String SQLCommand = "UPDATE materialManagement SET companyName = ?, contactName = ?, mobile = ?, landLine = ?," +
                " fax = ?, accountNum = ?, bankAddress = ?, address = ? WHERE sellerId = ?";
        try {
            ConnectToDB();
            PreparedStatement preparedStatement = connection.prepareStatement(SQLCommand);
            preparedStatement.setString(1, newSeller.getCompanyName());
            preparedStatement.setString(2, newSeller.getContactName());
            preparedStatement.setString(3, newSeller.getMobile());
            preparedStatement.setString(4, newSeller.getLandLine());
            preparedStatement.setString(5, newSeller.getFax());
            preparedStatement.setString(6, newSeller.getAccountNum());
            preparedStatement.setString(7, newSeller.getBankAddress());
            preparedStatement.setString(8, newSeller.getAddress());
            preparedStatement.setInt(9, newSeller.getSellerId());
            preparedStatement.executeUpdate();
            CloseConnectionToDB();
        } catch (SQLException e) {
            //System.out.println("UpdateSellerInSeller failed");
            HandleError error = new HandleError("DataBaseUtility", Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            error.WriteToLog();
            throw new SQLException();
        }
    }

    /**
     * update seller info
     * @param newSeller
     * @throws SQLException if any error occurs while operating on database
     */
    public static void UpdateSellerInSeller(Seller newSeller) throws SQLException {
        String SQLCommand = "UPDATE seller SET companyName = ?, contactName = ?, mobile = ?, landLine = ?," +
                " fax = ?, accountNum = ?, bankAddress = ?, address = ? WHERE sellerId = ?";
        try {
            ConnectToDB();
            PreparedStatement preparedStatement = connection.prepareStatement(SQLCommand);
            preparedStatement.setString(1, newSeller.getCompanyName());
            preparedStatement.setString(2, newSeller.getContactName());
            preparedStatement.setString(3, newSeller.getMobile());
            preparedStatement.setString(4, newSeller.getLandLine());
            preparedStatement.setString(5, newSeller.getFax());
            preparedStatement.setString(6, newSeller.getAccountNum());
            preparedStatement.setString(7, newSeller.getBankAddress());
            preparedStatement.setString(8, newSeller.getAddress());
            preparedStatement.setInt(9, newSeller.getSellerId());
            preparedStatement.executeUpdate();
            CloseConnectionToDB();
        } catch (SQLException e) {
            //System.out.println("UpdateSellerInSeller failed");
            HandleError error = new HandleError("DataBaseUtility", Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            error.WriteToLog();
            throw new SQLException();
        }
    }

    public static void UpdateOrderInMain(Order order) throws SQLException {
        try {
            ConnectToDB();
            String SQLCommand = "UPDATE materialManagement SET sku = ?, name = ?, type = ?, orderDateYear = ?, " +
                    "orderDateMonth = ?, orderDateDay = ?, unitAmount = ?, amount = ?, kgAmount = ?, unitPrice = ?, " +
                    "totalPrice = ?, sellerId = ?, companyName = ?, contactName = ?, mobile = ?, landLine = ?, " +
                    "fax = ?, accountNum = ?, bankAddress = ?, address = ?, paymentDateYear = ?, paymentDateMonth = ?, " +
                    "paymentDateDay = ?, arrivalDateYear = ?, arrivalDateMonth = ?, arrivalDateDay = ?, " +
                    "invoiceDateYear = ?, invoiceDateMonth = ?, invoiceDateDay = ?, invoice = ?, signed = ?, " +
                    "skuSeller = ?, note = ? WHERE serialNum = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(SQLCommand);

            preparedStatement.setString(1, order.getSku());
            preparedStatement.setString(2, order.getName());
            preparedStatement.setString(3, order.getType());
            preparedStatement.setInt(4, order.getOrderDate().getYear());
            preparedStatement.setInt(5, order.getOrderDate().getMonth());
            preparedStatement.setInt(6, order.getOrderDate().getDay());
            preparedStatement.setDouble(7, order.getUnitAmount());
            preparedStatement.setDouble(8, order.getAmount());
            preparedStatement.setDouble(9, order.getKgAmount());
            preparedStatement.setDouble(10, order.getUnitPrice());
            preparedStatement.setDouble(11, order.getTotalPrice());
            preparedStatement.setInt(12, order.getSeller().getSellerId());
            preparedStatement.setString(13, order.getSeller().getCompanyName());
            preparedStatement.setString(14, order.getSeller().getContactName());
            preparedStatement.setString(15, order.getSeller().getMobile());
            preparedStatement.setString(16, order.getSeller().getLandLine());
            preparedStatement.setString(17, order.getSeller().getFax());
            preparedStatement.setString(18, order.getSeller().getAccountNum());
            preparedStatement.setString(19, order.getSeller().getBankAddress());
            preparedStatement.setString(20, order.getSeller().getAddress());

            preparedStatement.setString(21, order.getPaymentDate() == null ? "" : String.valueOf(order.getPaymentDate().getYear()));
            preparedStatement.setString(22, order.getPaymentDate() == null ? "" : String.valueOf(order.getPaymentDate().getMonth()));
            preparedStatement.setString(23, order.getPaymentDate() == null ? "" : String.valueOf(order.getPaymentDate().getDay()));

            preparedStatement.setString(24, order.getArrivalDate() == null ? "" : String.valueOf(order.getArrivalDate().getYear()));
            preparedStatement.setString(25, order.getArrivalDate() == null ? "" : String.valueOf(order.getArrivalDate().getMonth()));
            preparedStatement.setString(26, order.getArrivalDate() == null ? "" : String.valueOf(order.getArrivalDate().getDay()));

            preparedStatement.setString(27, order.getInvoiceDate() == null ? "" : String.valueOf(order.getInvoiceDate().getYear()));
            preparedStatement.setString(28, order.getInvoiceDate() == null ? "" : String.valueOf(order.getInvoiceDate().getMonth()));
            preparedStatement.setString(29, order.getInvoiceDate() == null ? "" : String.valueOf(order.getInvoiceDate().getDay()));

            preparedStatement.setString(30, order.getInvoice() == null ? "" : order.getInvoice());
            preparedStatement.setString(31, order.getSigned() == null ? "" : order.getSigned());
            preparedStatement.setString(32, order.getSkuSeller() == null ? "" : order.getSkuSeller());
            preparedStatement.setString(33, order.getNote() == null ? "" : order.getNote());
            preparedStatement.setInt(34, order.getSerialNum());

            preparedStatement.executeUpdate();
            CloseConnectionToDB();
        } catch (SQLException e) {
            HandleError error = new HandleError("DataBaseUtility", Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            error.WriteToLog();
            e.printStackTrace();
            throw new SQLException();
        } finally {
            CloseConnectionToDB();
        }
    }

    public static ObservableList<Order> GetOrderWithSpecificCommand(String SQLCommand) throws SQLException {
        ObservableList<Order> orderObservableList = FXCollections.observableArrayList();
        try {
            ConnectToDB();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(SQLCommand);
            while (resultSet.next()) {
                String sku = resultSet.getString("sku");
                String name = resultSet.getString("name");
                String type = resultSet.getString("type");
                Material.Date orderDate = new Material.Date(resultSet.getInt("orderDateYear"),
                        resultSet.getInt("orderDateMonth"),
                        resultSet.getInt("orderDateDay"));
                Material.Date paymentDate = new Material.Date(resultSet.getInt("paymentDateYear"),
                        resultSet.getInt("paymentDateMonth"),
                        resultSet.getInt("paymentDateDay"));
                Material.Date arrivalDate = new Material.Date(resultSet.getInt("arrivalDateYear"),
                        resultSet.getInt("arrivalDateMonth"),
                        resultSet.getInt("arrivalDateDay"));
                Material.Date invoiceDate = new Material.Date(resultSet.getInt("invoiceDateYear"),
                        resultSet.getInt("invoiceDateMonth"),
                        resultSet.getInt("invoiceDateDay"));
                String invoice = resultSet.getString("invoice");
                double unitAmount = resultSet.getDouble("unitAmount");
                double amount = resultSet.getDouble("amount");
                double kgAmount = resultSet.getDouble("kgAmount");
                double unitPrice = resultSet.getDouble("unitPrice");
                double totalPrice = resultSet.getDouble("totalPrice");
                String note = resultSet.getString("note");
                String signed = resultSet.getString("signed");
                String skuSeller = resultSet.getString("skuSeller");
                int serialNum = resultSet.getInt("serialNum");

                Seller seller = new Seller(resultSet.getInt("sellerId"),
                        resultSet.getString("companyName"),
                        resultSet.getString("contactName"));
                seller.setMobile(resultSet.getString("mobile"));
                seller.setLandLine(resultSet.getString("landLine"));
                seller.setFax(resultSet.getString("fax"));
                seller.setAccountNum(resultSet.getString("accountNum"));
                seller.setBankAddress(resultSet.getString("bankAddress"));
                seller.setAddress(resultSet.getString("address"));

                // new order
                Order newOrder = new Order(serialNum, sku, name, type, orderDate, seller, unitAmount, amount);
                try {
                    newOrder.setUnitPrice(unitPrice);
                } catch (Exception ignored) {}
                newOrder.setPaymentDate(paymentDate);
                newOrder.setArrivalDate(arrivalDate);
                newOrder.setInvoiceDate(invoiceDate);
                newOrder.setInvoice(invoice);
                newOrder.setNote(note);
                newOrder.setSigned(signed);
                newOrder.setSkuSeller(skuSeller);

                orderObservableList.add(newOrder);
            }
            CloseConnectionToDB();
            return orderObservableList;
        } catch (SQLException e) {
            HandleError error = new HandleError("DataBaseUtility", Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            error.WriteToLog();
            throw new SQLException();
        } finally {
            CloseConnectionToDB();
        }
    }

    public static ArrayList<Order> SelectDataWithinRange(int[][] input) throws SQLException {
        ArrayList<Order> orderArrayList = new ArrayList<>();
        String SQLCommand = "SELECT * FROM materialManagement WHERE " +
                "(orderDateYear * 10000 + orderDateMonth * 100 + orderDateDay) >= ? " +
                "AND (orderDateYear * 10000 + orderDateMonth * 100 + orderDateDay) <= ?";
        try {
            ConnectToDB();
            PreparedStatement statement = connection.prepareStatement(SQLCommand);
            statement.setInt(1, input[0][0] * 10000 + input[0][1] * 100 + input[0][2]);
            statement.setInt(2, input[1][0] * 10000 + input[1][1] * 100 + input[1][2]);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String sku = resultSet.getString("sku");
                String name = resultSet.getString("name");
                String type = resultSet.getString("type");
                Material.Date orderDate = new Material.Date(resultSet.getInt("orderDateYear"),
                        resultSet.getInt("orderDateMonth"),
                        resultSet.getInt("orderDateDay"));
                Material.Date paymentDate = new Material.Date(resultSet.getInt("paymentDateYear"),
                        resultSet.getInt("paymentDateMonth"),
                        resultSet.getInt("paymentDateDay"));
                Material.Date arrivalDate = new Material.Date(resultSet.getInt("arrivalDateYear"),
                        resultSet.getInt("arrivalDateMonth"),
                        resultSet.getInt("arrivalDateDay"));
                Material.Date invoiceDate = new Date(resultSet.getInt("invoiceDateYear"),
                        resultSet.getInt("invoiceDateMonth"),
                        resultSet.getInt("invoiceDateDay"));
                String invoice = resultSet.getString("invoice");
                double unitAmount = resultSet.getDouble("unitAmount");
                double amount = resultSet.getDouble("amount");
                double kgAmount = resultSet.getDouble("kgAmount");
                double unitPrice = resultSet.getDouble("unitPrice");
                double totalPrice = resultSet.getDouble("totalPrice");
                String note = resultSet.getString("note");
                String signed = resultSet.getString("signed");
                String skuSeller = resultSet.getString("skuSeller");
                int serialNum = resultSet.getInt("serialNum");

                Seller seller = new Seller(resultSet.getInt("sellerId"),
                        resultSet.getString("companyName"),
                        resultSet.getString("contactName"));
                seller.setMobile(resultSet.getString("mobile"));
                seller.setLandLine(resultSet.getString("landLine"));
                seller.setFax(resultSet.getString("fax"));
                seller.setAccountNum(resultSet.getString("accountNum"));
                seller.setBankAddress(resultSet.getString("bankAddress"));
                seller.setAddress(resultSet.getString("address"));

                // new order
                Order newOrder = new Order(serialNum, sku, name, type, orderDate, seller, unitAmount, amount);
                try {
                    newOrder.setUnitPrice(unitPrice);
                } catch (Exception ignored) {}
                newOrder.setPaymentDate(paymentDate);
                newOrder.setArrivalDate(arrivalDate);
                newOrder.setInvoiceDate(invoiceDate);
                newOrder.setInvoice(invoice);
                newOrder.setNote(note);
                newOrder.setSigned(signed);
                newOrder.setSkuSeller(skuSeller);

                orderArrayList.add(newOrder);
            }
            CloseConnectionToDB();
            return orderArrayList;
        } catch (SQLException e) {
            HandleError error = new HandleError("DataBaseUtility", Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            error.WriteToLog();
            throw new SQLException();
        }
    }
}
