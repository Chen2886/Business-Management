package Main;

// from my other packages
import Material.*;
import Product.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;

public class DatabaseUtil {
    
    // database location
    private static String dataBaseLocationFile = "jdbc:sqlite:BusinessCashFlow.db";

    /**
     * connection that is required to connect to the database, all operations are done with this connection
     * to prevent error when more than one connection is open
     */
    private static Connection connection;

    /**
     * Gets the largest serial num in existence in the database
     */
    public static int GetNewestSerialNum(String tableName) throws SQLException {
        try {
            ConnectToDB();
            Statement statement = connection.createStatement();
            
            // seller has a different serialNum name
            if (tableName.equals("seller")) {
                ResultSet resultSet = statement.executeQuery(String.format("SELECT max(sellerId) FROM [%s]", tableName));
                return resultSet.getInt("max(sellerId)");
            } else {
                ResultSet resultSet = statement.executeQuery(String.format("SELECT max(serialNum) FROM [%s]", tableName));
                return resultSet.getInt("max(serialNum)");
            }
        } catch (SQLException e) {
            new HandleError(DatabaseUtil.class.getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
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
            
            // if connection can not be established, then we need to create a database
            if (connection == null) {
                CloseConnectionToDB();
                CreateNewDB();
            }
        } catch (SQLException e) {
            new HandleError(DatabaseUtil.class.getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            throw new SQLException();
        } finally {
            CloseConnectionToDB();
        }
    }

    /**
     * Create database
     * @throws SQLException if any error occurs while operating on database
     */
    private static void CreateNewDB() throws SQLException {
        ConnectToDB();
        try {
            connection = DriverManager.getConnection(dataBaseLocationFile);
        } catch (SQLException e) {
            new HandleError(DatabaseUtil.class.getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            throw new SQLException();
        } finally {
            CloseConnectionToDB();
        }
    }

    /**
     * Connect to database if a connection does not exist
     * @throws SQLException If connection cannot be established to the database
     */
    private static void ConnectToDB() throws SQLException {
        if (connection != null)
            return;
        try {
            connection = DriverManager.getConnection(dataBaseLocationFile);
        } catch (SQLException e) {
            new HandleError(DatabaseUtil.class.getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            throw new SQLException();
        }
    }

    /**
     * Terminate any connection to database.
     */
    public static void CloseConnectionToDB() {
        if (connection == null)
            return;
        try {
            connection.close();
            connection = null;
        } catch (SQLException e) {
            new HandleError(DatabaseUtil.class.getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            connection = null;
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
            while (resultSet.next()) {
                if ("materialManagement".equals(resultSet.getString("TABLE_NAME"))) numOfTable++;
                else if ("seller".equals(resultSet.getString("TABLE_NAME"))) numOfTable++;
                else if ("productManagement".equals(resultSet.getString("TABLE_NAME"))) numOfTable++;
                else if ("formula".equals(resultSet.getString("TABLE_NAME"))) numOfTable++;
                else if ("newestFormula".equals(resultSet.getString("TABLE_NAME"))) numOfTable++;
                else if ("productUnitPrice".equals(resultSet.getString("TABLE_NAME"))) numOfTable++;
                else if ("materialUnitPrice".equals(resultSet.getString("TABLE_NAME"))) numOfTable++;
            }

            if (numOfTable == 7) CloseConnectionToDB();
            else {
                CreateNewTable("materialManagement");
                CreateNewTable("seller");
                CreateNewTable("productManagement");
                CreateNewTable("formula");
                CreateNewTable("newestFormula");
                CreateNewTable("productUnitPrice");
                CreateNewTable("materialUnitPrice");
            }
        } catch (SQLException e) {
            new HandleError(DatabaseUtil.class.getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
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

            switch (tableName) {
                case "materialManagement":
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
                    break;
                case "seller":
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
                    break;
                case "productManagement":
                    SQLCommand = "CREATE TABLE IF NOT EXISTS [productManagement] (\n" +
                            "serialNum	 	INTEGER	PRIMARY KEY	NOT NULL,\n" +
                            "sku        	TEXT	        ,\n" +
                            "name       	TEXT	        ,\n" +
                            "customer 		TEXT			,\n" +
                            "note   		TEXT			,\n" +
                            "orderDateYear	INTEGER			,\n" +
                            "orderDateMonth	INTEGER			,\n" +
                            "orderDateDay	INTEGER			,\n" +
                            "unitAmount		REAL	        ,\n" +
                            "amount 		REAL			,\n" +
                            "unitPrice  	REAL			,\n" +
                            "basePrice  	REAL			,\n" +
                            "remote         INTEGER			,\n" +
                            "formulaIndex   INTEGER			\n" +
                            ");";
                    break;
                case "formula":
                    SQLCommand = "CREATE TABLE IF NOT EXISTS [formula] (\n" +
                            "serialNum	 	INTEGER	PRIMARY KEY	NOT NULL,\n" +
                            "formula        BLOB			 \n" +
                            ");";
                    break;
                case "newestFormula":
                    SQLCommand = "CREATE TABLE IF NOT EXISTS [newestFormula] (\n" +
                            "name   	 	TEXT	PRIMARY KEY	NOT NULL,\n" +
                            "formulaIndex   INTEGER			 \n" +
                            ");";
                    break;
                case "productUnitPrice":
                    SQLCommand = "CREATE TABLE IF NOT EXISTS [productUnitPrice] (\n" +
                            "serialNum   	INTEGER	PRIMARY KEY	NOT NULL,\n" +
                            "name	        TEXT			,\n" +
                            "price          REAL			,\n" +
                            "orderDateYear	INTEGER			,\n" +
                            "orderDateMonth	INTEGER			,\n" +
                            "orderDateDay	INTEGER			,\n" +
                            "customer   	TEXT			,\n" +
                            "note       	TEXT			,\n" +
                            "sku        	TEXT			\n" +
                            ");";
                    break;
                case "materialUnitPrice":
                    SQLCommand = "CREATE TABLE IF NOT EXISTS [materialUnitPrice] (\n" +
                            "name   	 	TEXT	PRIMARY KEY	NOT NULL,\n" +
                            "price          REAL    NOT NULL,\n" +
                            "note           TEXT			 \n" +
                            ");";
                    break;
            }
            statement.execute(SQLCommand);
            CloseConnectionToDB();
        } catch (SQLException e) {
            new HandleError(DatabaseUtil.class.getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
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
            new HandleError(DatabaseUtil.class.getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            return false;
        } finally {
            CloseConnectionToDB();
        }
    }

    /**
     * Check is sku provided exists
     * @param serialNum sku to be checked
     * @return weather serialNum exists or not
     * @throws SQLException if any error occurs while operating on database
     */
    public static boolean CheckIfMatSerialExists(int serialNum) throws SQLException {
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
            new HandleError(DatabaseUtil.class.getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            throw new SQLException();
        } finally {
            CloseConnectionToDB();
        }
    }

    /**
     * Check is sku provided exists
     * @param sellerId sellerId of the seller
     * @return weather serialNum exists or not
     * @throws SQLException if any error occurs while operating on database
     */
    public static boolean CheckIfMatSellerExists(int sellerId) throws SQLException {
        try {
            ConnectToDB();

            String SQLCommand = "SELECT * FROM seller WHERE sellerId = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(SQLCommand);
            preparedStatement.setInt(1, sellerId);
            ResultSet resultSet = preparedStatement.executeQuery();
            CloseConnectionToDB();
            return resultSet.next();
        } catch (SQLException e) {
            new HandleError(DatabaseUtil.class.getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            throw new SQLException();
        } finally {
            CloseConnectionToDB();
        }
    }

    /**
     * Check is sku provided exists
     * @param serialNum sku to be checked
     * @return weather serialNum exists or not
     * @throws SQLException if any error occurs while operating on database
     */
    public static boolean CheckIfProdSerialExists(int serialNum) throws SQLException {
        try {
            ConnectToDB();

            String SQLCommand = "SELECT serialNum FROM productManagement WHERE serialNum = ?";
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
            new HandleError(DatabaseUtil.class.getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            throw new SQLException();
        } finally {
            CloseConnectionToDB();
        }
    }

    /**
     * Given a name, see if newest formula table contains the name
     * @param name the name that needs to be true
     * @return true if contains, false if does not contain
     */
    public static boolean CheckIfNameExistsInNewestFormula(String name) {
        String SQLCommand = "SELECT formulaIndex FROM newestFormula WHERE name = ?";
        try {
            ConnectToDB();
            PreparedStatement preparedStatement = connection.prepareStatement(SQLCommand);
            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();
            CloseConnectionToDB();
            return resultSet.next();
        } catch (SQLException e) {
            new HandleError(DatabaseUtil.class.getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            return false;
        } finally {
            CloseConnectionToDB();
        }
    }

    /**
     * Given a name, see if newest formula table contains the name
     * @param name the name that needs to be true
     * @return true if contains, false if does not contain
     */
    public static boolean CheckIfNameExistsInMatUnitPrice(String name) {
        String SQLCommand = "SELECT name FROM materialUnitPrice WHERE name = ?";
        try {
            ConnectToDB();
            PreparedStatement preparedStatement = connection.prepareStatement(SQLCommand);
            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();
            CloseConnectionToDB();
            return resultSet.next();
        } catch (SQLException e) {
            new HandleError(DatabaseUtil.class.getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            return false;
        } finally {
            CloseConnectionToDB();
        }
    }

    /**
     * Get All the mat order from database
     * @return a list of all mat order ordered by sku
     * @throws SQLException if any error occurs while operating on database
     */
    public static ObservableList<MatOrder> GetAllMatOrders() throws SQLException {
        String SQLCommand = "SELECT * FROM materialManagement ORDER BY sku DESC";
        ObservableList<MatOrder> orderObservableList = FXCollections.observableArrayList();
        try {
            ConnectToDB();

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(SQLCommand);

            while (resultSet.next()) {

                // new seller
                MatSeller seller = new MatSeller(resultSet.getInt("sellerId"),
                        resultSet.getString("companyName"));
                seller.setContactName(resultSet.getString("contactName"));
                seller.setMobile(resultSet.getString("mobile"));
                seller.setLandLine(resultSet.getString("landLine"));
                seller.setFax(resultSet.getString("fax"));
                seller.setAccountNum(resultSet.getString("accountNum"));
                seller.setBankAddress(resultSet.getString("bankAddress"));
                seller.setAddress(resultSet.getString("address"));

                // new order
                MatOrder newOrder = new MatOrder(resultSet.getInt("serialNum"),
                        resultSet.getString("sku"));
                newOrder.setName(resultSet.getString("name"));
                newOrder.setType(resultSet.getString("type"));
                newOrder.setInvoice(resultSet.getString("invoice"));
                newOrder.setSigned(resultSet.getString("signed"));
                newOrder.setSkuSeller(resultSet.getString("skuSeller"));
                newOrder.setNote(resultSet.getString("note"));
                newOrder.setOrderDate(new Date(resultSet.getInt("orderDateYear"),
                        resultSet.getInt("orderDateMonth"),
                        resultSet.getInt("orderDateDay")));
                newOrder.setPaymentDate(new Date(resultSet.getInt("paymentDateYear"),
                        resultSet.getInt("paymentDateMonth"),
                        resultSet.getInt("paymentDateDay")));
                newOrder.setArrivalDate(new Date(resultSet.getInt("arrivalDateYear"),
                        resultSet.getInt("arrivalDateMonth"),
                        resultSet.getInt("arrivalDateDay")));
                newOrder.setInvoiceDate(new Date(resultSet.getInt("invoiceDateYear"),
                        resultSet.getInt("invoiceDateMonth"),
                        resultSet.getInt("invoiceDateDay")));
                newOrder.setSeller(seller);
                newOrder.setUnitAmount(resultSet.getDouble("unitAmount"));
                newOrder.setAmount(resultSet.getDouble("amount"));
                newOrder.setKgAmount();
                newOrder.setUnitPrice(resultSet.getDouble("unitPrice"));
                newOrder.setTotalPrice();

                // adding order
                orderObservableList.add(newOrder);
            }
            CloseConnectionToDB();
            return orderObservableList;
        } catch (SQLException e) {
            new HandleError(DatabaseUtil.class.getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            throw new SQLException();
        } finally {
            CloseConnectionToDB();
        }
    }

    /**
     * Get All the product order from database
     * @return a list of all product order ordered by sku
     * @throws SQLException if any error occurs while operating on database
     */
    public static ObservableList<ProductOrder> GetAllProdOrders() throws SQLException {
        String SQLCommand = "SELECT * FROM productManagement ORDER BY sku DESC";
        ObservableList<ProductOrder> orderObservableList = FXCollections.observableArrayList();
        try {
            ConnectToDB();

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(SQLCommand);

            while (resultSet.next()) {

                // new order
                ProductOrder newOrder = new ProductOrder(resultSet.getInt("serialNum"));
                newOrder.setSku(resultSet.getString("sku"));
                newOrder.setName(resultSet.getString("name"));
                newOrder.setCustomer(resultSet.getString("customer"));
                newOrder.setNote(resultSet.getString("note"));
                newOrder.setOrderDate(new Date(resultSet.getInt("orderDateYear"),
                        resultSet.getInt("orderDateMonth"),
                        resultSet.getInt("orderDateDay")));
                newOrder.setUnitAmount(resultSet.getDouble("unitAmount"));
                newOrder.setAmount(resultSet.getDouble("amount"));
                newOrder.setKgAmount();
                newOrder.setUnitPrice(resultSet.getDouble("unitPrice"));
                newOrder.setBasePrice(resultSet.getDouble("basePrice"));
                newOrder.setTotalPrice();
                newOrder.setFormulaIndex(resultSet.getInt("formulaIndex"));
                newOrder.setRemote(resultSet.getInt("remote"));

                // adding order
                orderObservableList.add(newOrder);
            }
            CloseConnectionToDB();
            return orderObservableList;
        } catch (SQLException e) {
            new HandleError(DatabaseUtil.class.getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            throw new SQLException();
        } finally {
            CloseConnectionToDB();
        }
    }

    /**
     * Return a list of all mat sellers available
     * @return all mat sellers in the database
     * @throws SQLException if any error occurs while operating on database
     */
    public static ObservableList<MatSeller> GetAllMatSellers() throws SQLException {
        String SQLCommand = "SELECT * FROM seller";
        ObservableList<MatSeller> sellerObservableList = FXCollections.observableArrayList();
        try {
            ConnectToDB();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(SQLCommand);
            while (resultSet.next()) {
                MatSeller seller = new MatSeller(resultSet.getInt("sellerId"),
                        resultSet.getString("companyName"));
                seller.setContactName(resultSet.getString("contactName"));
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
            new HandleError(DatabaseUtil.class.getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            throw new SQLException();
        } finally {
            CloseConnectionToDB();
        }
    }

    /**
     * Get All the mat unit prices from database
     * @return a list of all mat unit prices
     * @throws SQLException if any error occurs while operating on database
     */
    public static ObservableList<MatUnitPrice> GetAllMatUnitPrice() throws SQLException {
        String SQLCommand = "SELECT * FROM materialUnitPrice REVER";
        ObservableList<MatUnitPrice> matUnitPriceTableObservableList = FXCollections.observableArrayList();
        try {
            ConnectToDB();

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(SQLCommand);

            while (resultSet.next()) {
                // new order
                MatUnitPrice newUnitPrice = new MatUnitPrice(resultSet.getString("name"),
                        resultSet.getInt("price"), resultSet.getString("note"));
                matUnitPriceTableObservableList.add(0, newUnitPrice);
            }
            CloseConnectionToDB();
            return matUnitPriceTableObservableList;
        } catch (SQLException e) {
            new HandleError(DatabaseUtil.class.getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            throw new SQLException();
        } finally {
            CloseConnectionToDB();
        }
    }

    /**
     * Get All the product unit prices from database
     * @return a list of all product unit prices
     * @throws SQLException if any error occurs while operating on database
     */
    public static ObservableList<ProdUnitPrice> GetAllProdUnitPrice() throws SQLException {
        String SQLCommand = "SELECT * FROM productUnitPrice ORDER BY serialNum DESC";
        ObservableList<ProdUnitPrice> prodUnitPriceObservableList = FXCollections.observableArrayList();
        try {
            ConnectToDB();

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(SQLCommand);

            while (resultSet.next()) {

                // new order
                ProdUnitPrice prodUnitPrice = new ProdUnitPrice();
                prodUnitPrice.setSerialNum(resultSet.getInt("serialNum"));
                prodUnitPrice.setName(resultSet.getString("name"));
                prodUnitPrice.setUnitPrice(resultSet.getDouble("price"));
                prodUnitPrice.setDate(new Date(resultSet.getInt("orderDateYear"),
                        resultSet.getInt("orderDateMonth"),
                        resultSet.getInt("orderDateDay")));
                prodUnitPrice.setCustomer(resultSet.getString("customer"));
                prodUnitPrice.setNote(resultSet.getString("note"));
                prodUnitPrice.setSku(resultSet.getString("sku"));

                // adding unitPrice
                prodUnitPriceObservableList.add(prodUnitPrice);
            }
            CloseConnectionToDB();
            return prodUnitPriceObservableList;
        } catch (SQLException e) {
            new HandleError(DatabaseUtil.class.getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            throw new SQLException();
        } finally {
            CloseConnectionToDB();
        }
    }

    /**
     * Given index, return formula object
     * @param index the index of the formula
     * @return the formula @ index
     * @throws SQLException if any error occurs while operating on database
     */
    public static Formula GetFormulaByIndex(int index) throws SQLException {
        String SQLCommand = "SELECT formula FROM formula WHERE serialNum = ?";
        try {
            ConnectToDB();

            PreparedStatement preparedStatement = connection.prepareStatement(SQLCommand);
            preparedStatement.setInt(1, index);
            ResultSet resultSet = preparedStatement.executeQuery();

            byte[] buf = resultSet.getBytes(1);
            Formula formula = null;
            if (buf != null) {
                ObjectInputStream objectIn = new ObjectInputStream(new ByteArrayInputStream(buf));
                formula = (Formula) objectIn.readObject();
            }
            CloseConnectionToDB();
            return formula;
        } catch (SQLException | IOException | ClassNotFoundException e) {
            new HandleError(DatabaseUtil.class.getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            throw new SQLException();
        } finally {
            CloseConnectionToDB();
        }
    }

    /**
     * get the index from the newest formula's name
     * @param name name of the formula that needs to be found
     * @return the index
     * @throws SQLException if any error occurs while operating on database
     */
    public static int GetNewestFormulaIndex(String name) throws SQLException {
        String SQLCommand = "SELECT formulaIndex FROM newestFormula WHERE name = ?";
        try {
            ConnectToDB();

            PreparedStatement preparedStatement = connection.prepareStatement(SQLCommand);
            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();
            int returnVal = resultSet.getInt(1);
            CloseConnectionToDB();
            return returnVal;
        } catch (SQLException e) {
            new HandleError(DatabaseUtil.class.getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            throw new SQLException();
        } finally {
            CloseConnectionToDB();
        }
    }

    /**
     * get the unit price from the name provided
     * @param name the name of the formula that needs to be found
     * @return the unit price, 0 it not found
     * @throws SQLException if any error occurs while operating on database
     */
    public static double GetMatUnitPrice(String name) throws SQLException {
        String SQLCommand = "SELECT price FROM materialUnitPrice WHERE name = ?";
        try {
            ConnectToDB();

            PreparedStatement preparedStatement = connection.prepareStatement(SQLCommand);
            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();
            double returnVal = resultSet.next() ? resultSet.getDouble(1) : 0;
            CloseConnectionToDB();
            return returnVal;
        } catch (SQLException e) {
            new HandleError(DatabaseUtil.class.getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            throw new SQLException();
        } finally {
            CloseConnectionToDB();
        }
    }

    /**
     * get the unit price from the name provided
     * @param customer of the product that needs to be found
     * @param name the name of the product that needs to be found
     * @return the unit price, 0 it not found
     * @throws SQLException if any error occurs while operating on database
     */
    public static double GetProdUnitPrice(String name, String customer) throws SQLException {
        String SQLCommand = "SELECT price FROM productUnitPrice WHERE name = ? AND customer = ?";
        try {
            ConnectToDB();

            PreparedStatement preparedStatement = connection.prepareStatement(SQLCommand);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, customer);
            ResultSet resultSet = preparedStatement.executeQuery();
            double returnVal = resultSet.next() ? resultSet.getDouble(1) : 0;
            CloseConnectionToDB();
            return returnVal;
        } catch (SQLException e) {
            new HandleError(DatabaseUtil.class.getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            throw new SQLException();
        } finally {
            CloseConnectionToDB();
        }
    }

    /**
     * Given serial num, delete from database
     * @param serialNum order identified to be deleted
     */
    public static void DeleteMatOrder(int serialNum) throws SQLException {
        try {
            ConnectToDB();

            if (!CheckIfMatSerialExists(serialNum)) {
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
            new HandleError(DatabaseUtil.class.getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            throw new SQLException();
        } finally {
            CloseConnectionToDB();
        }
    }

    /**
     * Given sellerid, delete from database
     * @param sellerId seller identified to be deleted
     */
    public static void DeleteMatSeller(int sellerId) throws SQLException {
        try {
            ConnectToDB();

            if (!CheckIfMatSellerExists(sellerId)) {
                CloseConnectionToDB();
                return;
            }

            ConnectToDB();
            String SQLCommand = "DELETE FROM seller WHERE sellerId = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(SQLCommand);
            preparedStatement.setInt(1, sellerId);
            preparedStatement.executeUpdate();

            SQLCommand = "UPDATE materialManagement SET companyName = \"\", contactName = \"\", mobile = \"\", " +
                "landLine = \"\", fax = \"\", accountNum = \"\", bankAddress = \"\", address = \"\" WHERE sellerId = ?";
            preparedStatement = connection.prepareStatement(SQLCommand);
            preparedStatement.setInt(1, sellerId);
            preparedStatement.executeUpdate();
            CloseConnectionToDB();
        } catch (SQLException e) {
            new HandleError(DatabaseUtil.class.getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            throw new SQLException();
        } finally {
            CloseConnectionToDB();
        }
    }

    /**
     * Given the name of the unit price, delete from database
     * @param name name of the unit prices that needs to be deleted
     */
    public static void DeleteMatUnitPrice(String name) throws SQLException {
        try {
            ConnectToDB();

            String SQLCommand = "DELETE FROM materialUnitPrice WHERE name = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(SQLCommand);
            preparedStatement.setString(1, name);
            preparedStatement.executeUpdate();
            CloseConnectionToDB();
        } catch (SQLException e) {
            new HandleError(DatabaseUtil.class.getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            throw new SQLException();
        } finally {
            CloseConnectionToDB();
        }
    }

    /**
     * Given the name and the customer, delete from database
     * @param name name of the unit prices that needs to be deleted
     * @param customer customer of the unit prices that needs to be deleted
     */
    public static void DeleteProdUnitPrice(String name, String customer) throws SQLException {
        try {
            ConnectToDB();

            String SQLCommand = "DELETE FROM productUnitPrice WHERE name = ? AND customer = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(SQLCommand);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, customer);
            preparedStatement.executeUpdate();
            CloseConnectionToDB();
        } catch (SQLException e) {
            new HandleError(DatabaseUtil.class.getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            throw new SQLException();
        } finally {
            CloseConnectionToDB();
        }
    }

    /**
     * Given serial num, delete from database
     * @param serialNum order identified to be deleted
     */
    public static void DeleteProdOrder(int serialNum) throws SQLException {
        try {
            ConnectToDB();

            if (!CheckIfProdSerialExists(serialNum)) {
                CloseConnectionToDB();
                return;
            }

            ConnectToDB();
            String SQLCommand = "DELETE FROM productManagement WHERE serialNum = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(SQLCommand);
            preparedStatement.setInt(1, serialNum);
            preparedStatement.executeUpdate();
            CloseConnectionToDB();
        } catch (SQLException e) {
            new HandleError(DatabaseUtil.class.getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
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
    public static void AddMatOrder(MatOrder order) throws SQLException {
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
            preparedStatement.setInt(4, order.getOrderDate().getY());
            preparedStatement.setInt(5, order.getOrderDate().getM());
            preparedStatement.setInt(6, order.getOrderDate().getD());
            preparedStatement.setDouble(7, order.getUnitAmount());
            preparedStatement.setDouble(8, order.getAmount());
            preparedStatement.setDouble(9, order.getKgAmount());
            preparedStatement.setDouble(10, order.getUnitPrice());
            preparedStatement.setDouble(11, order.getTotalPrice());
            if (order.getSeller() != null) {
                preparedStatement.setInt(12, order.getSeller().getSellerId());
                preparedStatement.setString(13, order.getSeller().getCompanyName());
                preparedStatement.setString(14, order.getSeller().getContactName());
                preparedStatement.setString(15, order.getSeller().getMobile());
                preparedStatement.setString(16, order.getSeller().getLandLine());
                preparedStatement.setString(17, order.getSeller().getFax());
                preparedStatement.setString(18, order.getSeller().getAccountNum());
                preparedStatement.setString(19, order.getSeller().getBankAddress());
                preparedStatement.setString(20, order.getSeller().getAddress());
            } else {
                preparedStatement.setInt(12, -1);
                preparedStatement.setString(13, "");
                preparedStatement.setString(14, "");
                preparedStatement.setString(15, "");
                preparedStatement.setString(16, "");
                preparedStatement.setString(17, "");
                preparedStatement.setString(18, "");
                preparedStatement.setString(19, "");
                preparedStatement.setString(20, "");
            }

            preparedStatement.setString(21, order.getPaymentDate() == null ? "" : String.valueOf(order.getPaymentDate().getY()));
            preparedStatement.setString(22, order.getPaymentDate() == null ? "" : String.valueOf(order.getPaymentDate().getM()));
            preparedStatement.setString(23, order.getPaymentDate() == null ? "" : String.valueOf(order.getPaymentDate().getD()));

            preparedStatement.setString(24, order.getArrivalDate() == null ? "" : String.valueOf(order.getArrivalDate().getY()));
            preparedStatement.setString(25, order.getArrivalDate() == null ? "" : String.valueOf(order.getArrivalDate().getM()));
            preparedStatement.setString(26, order.getArrivalDate() == null ? "" : String.valueOf(order.getArrivalDate().getD()));

            preparedStatement.setString(27, order.getInvoiceDate() == null ? "" : String.valueOf(order.getInvoiceDate().getY()));
            preparedStatement.setString(28, order.getInvoiceDate() == null ? "" : String.valueOf(order.getInvoiceDate().getM()));
            preparedStatement.setString(29, order.getInvoiceDate() == null ? "" : String.valueOf(order.getInvoiceDate().getD()));

            preparedStatement.setString(30, order.getInvoice() == null ? "" : order.getInvoice());
            preparedStatement.setString(31, order.getSigned() == null ? "" : order.getSigned());
            preparedStatement.setString(32, order.getSkuSeller() == null ? "" : order.getSkuSeller());
            preparedStatement.setString(33, order.getNote() == null ? "" : order.getNote());
            preparedStatement.setInt(34, order.getSerialNum());
            preparedStatement.executeUpdate();
            CloseConnectionToDB();
        } catch (SQLException e) {
            new HandleError(DatabaseUtil.class.getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            
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
    public static void AddProdOrder(ProductOrder order) throws SQLException {
        try {
            ConnectToDB();
            String SQLCommand = "INSERT INTO [productManagement] (sku, name, customer, " +
                    "orderDateYear, orderDateMonth, orderDateDay, unitAmount, amount, unitPrice, " +
                    "basePrice, formulaIndex, note, serialNum, remote) " +
                    "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(SQLCommand);
            preparedStatement.setString(1, order.getSku());
            preparedStatement.setString(2, order.getName());
            preparedStatement.setString(3, order.getCustomer());
            preparedStatement.setInt(4, order.getOrderDate().getY());
            preparedStatement.setInt(5, order.getOrderDate().getM());
            preparedStatement.setInt(6, order.getOrderDate().getD());
            preparedStatement.setDouble(7, order.getUnitAmount());
            preparedStatement.setDouble(8, order.getAmount());
            preparedStatement.setDouble(9, order.getUnitPrice());
            preparedStatement.setDouble(10, order.getBasePrice());
            preparedStatement.setInt(11, order.getFormulaIndex());
            preparedStatement.setString(12, order.getNote() == null ? "" : order.getNote());
            preparedStatement.setInt(13, order.getSerialNum());
            preparedStatement.setInt(14, order.getRemoteInt());
            preparedStatement.executeUpdate();
            CloseConnectionToDB();
        } catch (SQLException e) {
            new HandleError(DatabaseUtil.class.getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            
            throw new SQLException();
        } finally {
            CloseConnectionToDB();
        }
    }

    /**
     * Insert an unit price into the database
     * @param matUnitPrice specified mat unit price
     * @throws SQLException if any error occurs while operating on database
     */
    public static void AddMatUnitPrice(MatUnitPrice matUnitPrice) throws SQLException {
        try {
            ConnectToDB();
            String SQLCommand = "INSERT INTO [materialUnitPrice] (name, price, note) " +
                    "VALUES(?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(SQLCommand);
            preparedStatement.setString(1, matUnitPrice.getName());
            preparedStatement.setDouble(2, matUnitPrice.getUnitPrice());
            preparedStatement.setString(3, matUnitPrice.getNote());
            preparedStatement.executeUpdate();
            CloseConnectionToDB();
        } catch (SQLException e) {
            new HandleError(DatabaseUtil.class.getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            throw new SQLException();
        } finally {
            CloseConnectionToDB();
        }
    }

    /**
     * Insert an unit price into the database
     * @param prodUnitPrice specified prod unit price
     * @throws SQLException if any error occurs while operating on database
     */
    public static void AddProdUnitPrice(ProdUnitPrice prodUnitPrice) throws SQLException {
        try {
            ConnectToDB();
            String SQLCommand = "INSERT INTO [productUnitPrice] (serialNum, name, price, orderDateYear, " +
                    "orderDateMonth, orderDateDay, customer, note, sku) " +
                    "VALUES(?,?,?,?,?,?,?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(SQLCommand);
            preparedStatement.setInt(1, prodUnitPrice.getSerialNum());
            preparedStatement.setString(2, prodUnitPrice.getName());
            preparedStatement.setDouble(3, prodUnitPrice.getUnitPrice());
            preparedStatement.setInt(4, prodUnitPrice.getDate().getY());
            preparedStatement.setInt(5, prodUnitPrice.getDate().getM());
            preparedStatement.setInt(6, prodUnitPrice.getDate().getD());
            preparedStatement.setString(7, prodUnitPrice.getCustomer());
            preparedStatement.setString(8, prodUnitPrice.getNote());
            preparedStatement.setString(9, prodUnitPrice.getSku());
            preparedStatement.executeUpdate();
            CloseConnectionToDB();
        } catch (SQLException e) {
            new HandleError(DatabaseUtil.class.getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            throw new SQLException();
        } finally {
            CloseConnectionToDB();
        }
    }

    /**
     * Insert seller into the seller database
     * @param seller seller that needs to be added
     * @throws SQLException if any error occurs while operating on database
     */
    public static void AddMatSeller(MatSeller seller) throws SQLException {
        try {
            ConnectToDB();
            String SQLCommand = "INSERT INTO [seller] (sellerId, companyName, contactName, mobile, landLine, fax," +
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
            preparedStatement.executeUpdate();
            CloseConnectionToDB();
        } catch (SQLException e) {
            new HandleError(DatabaseUtil.class.getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            throw new SQLException();
        } finally {
            CloseConnectionToDB();
        }
    }

    /**
     * Insert formula to formula table
     * @return the index where Formula was inserted
     */
    public static int AddFormula (Formula formula) throws SQLException {
        String SQLCommand = "INSERT INTO formula (serialNum, formula) VALUES(?,?)";
        try {
            ConnectToDB();
            int serialNum = SerialNum.getSerialNum(DBOrder.FORMULA);
            PreparedStatement preparedStatement = connection.prepareStatement(SQLCommand);
            preparedStatement.setInt(1, serialNum);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(formula);
            objectOutputStream.close();

            preparedStatement.setObject(2, byteArrayOutputStream.toByteArray());
            preparedStatement.executeUpdate();
            CloseConnectionToDB();

            return serialNum;
        } catch (SQLException | IOException e) {
            new HandleError(DatabaseUtil.class.getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            throw new SQLException();
        } finally {
            CloseConnectionToDB();
        }
    }

    /**
     * Update mat order within database
     * @param matOrder the order that needs to be updated
     * @throws SQLException if any error occurs while operating on database
     */
    public static void UpdateMatOrder(MatOrder matOrder) throws SQLException {
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

            preparedStatement.setString(1, matOrder.getSku());
            preparedStatement.setString(2, matOrder.getName());
            preparedStatement.setString(3, matOrder.getType());
            preparedStatement.setInt(4, matOrder.getOrderDate().getY());
            preparedStatement.setInt(5, matOrder.getOrderDate().getM());
            preparedStatement.setInt(6, matOrder.getOrderDate().getD());
            preparedStatement.setDouble(7, matOrder.getUnitAmount());
            preparedStatement.setDouble(8, matOrder.getAmount());
            preparedStatement.setDouble(9, matOrder.getKgAmount());
            preparedStatement.setDouble(10, matOrder.getUnitPrice());
            preparedStatement.setDouble(11, matOrder.getTotalPrice());
            if (matOrder.getSeller() != null) {
                preparedStatement.setInt(12, matOrder.getSeller().getSellerId());
                preparedStatement.setString(13, matOrder.getSeller().getCompanyName());
                preparedStatement.setString(14, matOrder.getSeller().getContactName());
                preparedStatement.setString(15, matOrder.getSeller().getMobile());
                preparedStatement.setString(16, matOrder.getSeller().getLandLine());
                preparedStatement.setString(17, matOrder.getSeller().getFax());
                preparedStatement.setString(18, matOrder.getSeller().getAccountNum());
                preparedStatement.setString(19, matOrder.getSeller().getBankAddress());
                preparedStatement.setString(20, matOrder.getSeller().getAddress());
            } else {
                preparedStatement.setInt(12, 0);
                preparedStatement.setString(13, "");
                preparedStatement.setString(14, "");
                preparedStatement.setString(15, "");
                preparedStatement.setString(16, "");
                preparedStatement.setString(17, "");
                preparedStatement.setString(18, "");
                preparedStatement.setString(19, "");
                preparedStatement.setString(20, "");
            }

            preparedStatement.setString(21, matOrder.getPaymentDate() == null ? "" : String.valueOf(matOrder.getPaymentDate().getY()));
            preparedStatement.setString(22, matOrder.getPaymentDate() == null ? "" : String.valueOf(matOrder.getPaymentDate().getM()));
            preparedStatement.setString(23, matOrder.getPaymentDate() == null ? "" : String.valueOf(matOrder.getPaymentDate().getD()));

            preparedStatement.setString(24, matOrder.getArrivalDate() == null ? "" : String.valueOf(matOrder.getArrivalDate().getY()));
            preparedStatement.setString(25, matOrder.getArrivalDate() == null ? "" : String.valueOf(matOrder.getArrivalDate().getM()));
            preparedStatement.setString(26, matOrder.getArrivalDate() == null ? "" : String.valueOf(matOrder.getArrivalDate().getD()));

            preparedStatement.setString(27, matOrder.getInvoiceDate() == null ? "" : String.valueOf(matOrder.getInvoiceDate().getY()));
            preparedStatement.setString(28, matOrder.getInvoiceDate() == null ? "" : String.valueOf(matOrder.getInvoiceDate().getM()));
            preparedStatement.setString(29, matOrder.getInvoiceDate() == null ? "" : String.valueOf(matOrder.getInvoiceDate().getD()));

            preparedStatement.setString(30, matOrder.getInvoice());
            preparedStatement.setString(31, matOrder.getSigned());
            preparedStatement.setString(32, matOrder.getSkuSeller());
            preparedStatement.setString(33, matOrder.getNote());
            preparedStatement.setInt(34, matOrder.getSerialNum());

            preparedStatement.executeUpdate();
            CloseConnectionToDB();
        } catch (SQLException e) {
            new HandleError(DatabaseUtil.class.getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            throw new SQLException();
        } finally {
            CloseConnectionToDB();
        }
    }

    /**
     * Update an order to the database
     * @param order specified order
     * @throws SQLException if any error occurs while operating on database
     */
    public static void UpdateProdOrder(ProductOrder order) throws SQLException {
        try {
            ConnectToDB();
            String SQLCommand = "UPDATE productManagement SET sku = ?, name = ?, customer = ?, " +
                    "orderDateYear = ?, orderDateMonth = ?, orderDateDay = ?, unitAmount = ?, amount = ?, unitPrice = ?, " +
                    "basePrice = ?, formulaIndex = ?, note = ?, remote = ? WHERE serialNum = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(SQLCommand);
            preparedStatement.setString(1, order.getSku());
            preparedStatement.setString(2, order.getName());
            preparedStatement.setString(3, order.getCustomer());
            preparedStatement.setInt(4, order.getOrderDate().getY());
            preparedStatement.setInt(5, order.getOrderDate().getM());
            preparedStatement.setInt(6, order.getOrderDate().getD());
            preparedStatement.setDouble(7, order.getUnitAmount());
            preparedStatement.setDouble(8, order.getAmount());
            preparedStatement.setDouble(9, order.getUnitPrice());
            preparedStatement.setDouble(10, order.getBasePrice());
            preparedStatement.setInt(11, order.getFormulaIndex());
            preparedStatement.setString(12, order.getNote() == null ? "" : order.getNote());
            preparedStatement.setInt(13, order.getRemoteInt());
            preparedStatement.setInt(14, order.getSerialNum());
            preparedStatement.executeUpdate();
            CloseConnectionToDB();
        } catch (SQLException e) {
            new HandleError(DatabaseUtil.class.getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            throw new SQLException();
        } finally {
            CloseConnectionToDB();
        }
    }

    /**
     * update all order using this seller in main
     * @param newSeller new seller
     * @throws SQLException if any error occurs while operating on database
     */
    public static void UpdateMatSellerInMain(MatSeller newSeller) throws SQLException {
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
            new HandleError(DatabaseUtil.class.getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            throw new SQLException();
        }
    }

    /**
     * update seller info
     * @param newSeller the new seller that needs to be updated
     * @throws SQLException if any error occurs while operating on database
     */
    public static void UpdateMatSellerInSeller(MatSeller newSeller) throws SQLException {
        String SQLCommand = "UPDATE seller SET companyName = ?, contactName = ?, mobile = ?, landLine = ?, " +
                "fax = ?, accountNum = ?, bankAddress = ?, address = ? WHERE sellerId = ?";
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
            new HandleError(DatabaseUtil.class.getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            throw new SQLException();
        }
    }

    /**
     * Update the newest formula table
     * @param exists if formula exists already
     * @param name the name of the formula
     * @param index the index of the formula
     */
    public static void UpdateNewestFormula(boolean exists, String name, int index) throws SQLException {
        String SQLCommand;
        if (exists) SQLCommand = "UPDATE newestFormula SET formulaIndex = ? WHERE name = ?";
        else SQLCommand = "INSERT INTO newestFormula (formulaIndex, name) VALUES(?,?)";

        String SQLCommandUpdateAll = "UPDATE productManagement SET formulaIndex = ? WHERE formulaIndex = -1 AND name = ?";
        try {
            ConnectToDB();
            PreparedStatement preparedStatement = connection.prepareStatement(SQLCommand);
            preparedStatement.setInt(1, index);
            preparedStatement.setString(2, name);
            preparedStatement.executeUpdate();

            PreparedStatement preparedStatementUpdateAll = connection.prepareStatement(SQLCommandUpdateAll);
            preparedStatementUpdateAll.setInt(1, index);
            preparedStatementUpdateAll.setString(2, name);
            preparedStatementUpdateAll.executeUpdate();

            CloseConnectionToDB();
        } catch (SQLException e) {
            new HandleError(DatabaseUtil.class.getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            throw new SQLException();
        } finally {
            CloseConnectionToDB();
        }
    }

    /**
     * Update formula to formula table
     * @param formula the formula that needs to be updated
     * @param serialNum the serial num where it needs to be updated
     */
    public static void UpdateFormula (Formula formula, int serialNum) throws SQLException {
        String SQLCommand = "UPDATE formula SET formula = ? WHERE serialNum = ?";
        try {
            ConnectToDB();
            PreparedStatement preparedStatement = connection.prepareStatement(SQLCommand);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(formula);
            objectOutputStream.close();

            preparedStatement.setObject(1, byteArrayOutputStream.toByteArray());
            preparedStatement.setInt(2, serialNum);
            preparedStatement.executeUpdate();
            CloseConnectionToDB();
        } catch (SQLException | IOException e) {
            new HandleError(DatabaseUtil.class.getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            throw new SQLException();
        } finally {
            CloseConnectionToDB();
        }
    }

    /**
     * Update a product unit price
     * @param prodUnitPrice specified prod unit price
     * @throws SQLException if any error occurs while operating on database
     */
    public static void UpdateProdUnitPrice(ProdUnitPrice prodUnitPrice) throws SQLException {
        try {
            ConnectToDB();
            String SQLCommand = "UPDATE [productUnitPrice] SET name = ?, price = ?, orderDateYear = ?, " +
                    "orderDateMonth = ?, orderDateDay = ?, customer = ?, note = ?, sku = ? WHERE serialNum = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(SQLCommand);
            preparedStatement.setString(1, prodUnitPrice.getName());
            preparedStatement.setDouble(2, prodUnitPrice.getUnitPrice());
            preparedStatement.setInt(3, prodUnitPrice.getDate().getY());
            preparedStatement.setInt(4, prodUnitPrice.getDate().getM());
            preparedStatement.setInt(5, prodUnitPrice.getDate().getD());
            preparedStatement.setString(6, prodUnitPrice.getCustomer());
            preparedStatement.setString(7, prodUnitPrice.getNote());
            preparedStatement.setString(8, prodUnitPrice.getSku());
            preparedStatement.setInt(9, prodUnitPrice.getSerialNum());
            preparedStatement.executeUpdate();
            CloseConnectionToDB();
        } catch (SQLException e) {
            new HandleError(DatabaseUtil.class.getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            throw new SQLException();
        } finally {
            CloseConnectionToDB();
        }
    }

    /**
     * Update an unit price into the database
     * @param oldMatUnitPrice original unit price
     * @param newMatUnitPrice new unit price to replace the old one
     * @throws SQLException if any error occurs while operating on database
     */
    public static void UpdateMatUnitPrice(MatUnitPrice oldMatUnitPrice, MatUnitPrice newMatUnitPrice) throws SQLException {
        try {
            ConnectToDB();
            String SQLCommand = "UPDATE [materialUnitPrice] SET name = ?, price = ?, note = ? " +
                    "WHERE name = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(SQLCommand);
            preparedStatement.setString(1, newMatUnitPrice.getName());
            preparedStatement.setDouble(2, newMatUnitPrice.getUnitPrice());
            preparedStatement.setString(3, newMatUnitPrice.getNote());
            preparedStatement.setString(4, oldMatUnitPrice.getName());
            preparedStatement.executeUpdate();
            CloseConnectionToDB();
        } catch (SQLException e) {
            new HandleError(DatabaseUtil.class.getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            throw new SQLException();
        } finally {
            CloseConnectionToDB();
        }
    }

    /**
     * Update an unit price into the database
     * @param name original unit price name
     * @param unitPrice new unit price to replace the old one
     * @throws SQLException if any error occurs while operating on database
     */
    public static void UpdateMatUnitPrice(String name, double unitPrice) throws SQLException {
        try {
            ConnectToDB();
            String SQLCommand = "UPDATE [materialUnitPrice] SET price = ? WHERE name = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(SQLCommand);
            preparedStatement.setDouble(1, unitPrice);
            preparedStatement.setString(2, name);
            preparedStatement.executeUpdate();
            CloseConnectionToDB();
        } catch (SQLException e) {
            new HandleError(DatabaseUtil.class.getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            throw new SQLException();
        } finally {
            CloseConnectionToDB();
        }
    }

    /**
     * Update all mat unit price in material management
     * @param name the name of material
     * @param price new price
     * @throws SQLException if any error occurs while operating on database
     */
    public static void UpdateAllMatUnitPrice(String name, double price) throws SQLException {
        try {
            ConnectToDB();
            String SQLCommand = "UPDATE [materialManagement] SET unitPrice = ? WHERE name = ? AND unitPrice = 0.0";
            PreparedStatement preparedStatement = connection.prepareStatement(SQLCommand);
            preparedStatement.setDouble(1, price);
            preparedStatement.setString(2, name);
            preparedStatement.executeUpdate();
            CloseConnectionToDB();
        } catch (SQLException e) {
            new HandleError(DatabaseUtil.class.getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            throw new SQLException();
        } finally {
            CloseConnectionToDB();
        }
    }

    /**
     * Update all mat unit price in material management
     * @param name the name of material
     * @param price new price
     * @throws SQLException if any error occurs while operating on database
     */
    public static void UpdateAllProdUnitPrice(String name, String customer, double price) throws SQLException {
        try {
            ConnectToDB();
            String SQLCommand = "UPDATE [productManagement] SET unitPrice = ? " +
                    "WHERE name = ? AND customer = ? AND unitPrice = 0.0";
            PreparedStatement preparedStatement = connection.prepareStatement(SQLCommand);
            preparedStatement.setDouble(1, price);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, customer);
            preparedStatement.executeUpdate();
            CloseConnectionToDB();
        } catch (SQLException e) {
            new HandleError(DatabaseUtil.class.getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            throw new SQLException();
        } finally {
            CloseConnectionToDB();
        }
    }

    /**
     * Update all product order with new base price given the formula index.
     */
    public static void UpdateAllProdOrderNewBasePrice(int formulaIndex, double basePrice) throws SQLException {
        try {
            ConnectToDB();
            String SQLCommand = "UPDATE [productManagement] SET basePrice = ? WHERE formulaIndex = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(SQLCommand);
            preparedStatement.setDouble(1, basePrice);
            preparedStatement.setInt(2, formulaIndex);
            preparedStatement.executeUpdate();
            CloseConnectionToDB();
        } catch (SQLException e) {
            new HandleError(DatabaseUtil.class.getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            throw new SQLException();
        } finally {
            CloseConnectionToDB();
        }
    }

    /**
     * Execute a given command related to mat order
     * @return command result
     * @throws SQLException if any error occurs while operating on database
     */
    public static ObservableList<MatOrder> ExecuteMatOrderCommand(String SQLCommand) throws SQLException {
        ObservableList<MatOrder> orderObservableList = FXCollections.observableArrayList();
        try {
            ConnectToDB();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(SQLCommand);
            while (resultSet.next()) {

                // new seller
                MatSeller seller = new MatSeller(resultSet.getInt("sellerId"),
                        resultSet.getString("companyName"));
                seller.setContactName(resultSet.getString("contactName"));
                seller.setMobile(resultSet.getString("mobile"));
                seller.setLandLine(resultSet.getString("landLine"));
                seller.setFax(resultSet.getString("fax"));
                seller.setAccountNum(resultSet.getString("accountNum"));
                seller.setBankAddress(resultSet.getString("bankAddress"));
                seller.setAddress(resultSet.getString("address"));

                // new order
                MatOrder newOrder = new MatOrder(resultSet.getInt("serialNum"),
                        resultSet.getString("sku"));
                newOrder.setName(resultSet.getString("name"));
                newOrder.setType(resultSet.getString("type"));
                newOrder.setInvoice(resultSet.getString("invoice"));
                newOrder.setSigned(resultSet.getString("signed"));
                newOrder.setSkuSeller(resultSet.getString("skuSeller"));
                newOrder.setNote(resultSet.getString("note"));
                newOrder.setOrderDate(new Date(resultSet.getInt("orderDateYear"),
                        resultSet.getInt("orderDateMonth"),
                        resultSet.getInt("orderDateDay")));
                newOrder.setPaymentDate(new Date(resultSet.getInt("paymentDateYear"),
                        resultSet.getInt("paymentDateMonth"),
                        resultSet.getInt("paymentDateDay")));
                newOrder.setArrivalDate(new Date(resultSet.getInt("arrivalDateYear"),
                        resultSet.getInt("arrivalDateMonth"),
                        resultSet.getInt("arrivalDateDay")));
                newOrder.setInvoiceDate(new Date(resultSet.getInt("invoiceDateYear"),
                        resultSet.getInt("invoiceDateMonth"),
                        resultSet.getInt("invoiceDateDay")));
                newOrder.setSeller(seller);
                newOrder.setUnitAmount(resultSet.getDouble("unitAmount"));
                newOrder.setAmount(resultSet.getDouble("amount"));
                newOrder.setKgAmount();
                newOrder.setUnitPrice(resultSet.getDouble("unitPrice"));
                newOrder.setTotalPrice();

                // adding order
                orderObservableList.add(newOrder);
            }
            CloseConnectionToDB();
            return orderObservableList;
        } catch (SQLException e) {
            new HandleError(DatabaseUtil.class.getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            throw new SQLException();
        } finally {
            CloseConnectionToDB();
        }
    }

    /**
     * Execute a given command related to mat order
     * @return command result
     * @throws SQLException if any error occurs while operating on database
     */
    public static ObservableList<ProductOrder> ExecuteProdOrderCommand(String SQLCommand) throws SQLException {
        ObservableList<ProductOrder> orderObservableList = FXCollections.observableArrayList();
        try {
            ConnectToDB();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(SQLCommand);
            while (resultSet.next()) {

                // new order
                ProductOrder newOrder = new ProductOrder(resultSet.getInt("serialNum"));
                newOrder.setSku(resultSet.getString("sku"));
                newOrder.setName(resultSet.getString("name"));
                newOrder.setCustomer(resultSet.getString("customer"));
                newOrder.setNote(resultSet.getString("note"));
                newOrder.setOrderDate(new Date(resultSet.getInt("orderDateYear"),
                        resultSet.getInt("orderDateMonth"),
                        resultSet.getInt("orderDateDay")));
                newOrder.setUnitAmount(resultSet.getDouble("unitAmount"));
                newOrder.setAmount(resultSet.getDouble("amount"));
                newOrder.setKgAmount();
                newOrder.setUnitPrice(resultSet.getDouble("unitPrice"));
                newOrder.setBasePrice(resultSet.getDouble("basePrice"));
                newOrder.setTotalPrice();
                newOrder.setFormulaIndex(resultSet.getInt("formulaIndex"));

                // adding order
                orderObservableList.add(newOrder);
            }
            CloseConnectionToDB();
            return orderObservableList;
        } catch (SQLException e) {
            new HandleError(DatabaseUtil.class.getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            throw new SQLException();
        } finally {
            CloseConnectionToDB();
        }
    }

    /**
     * get all material orders with in the input date range
     * @param input 2d array of size 2, 1st is start date (yyyy-mm-dd), 2nd is end date (yyyy-mm-dd)
     * @return ArrayList of material order
     * @throws SQLException if any error occurs while operating on database
     */
    public static ArrayList<MatOrder> SelectMatOrderWithDateRange(int[][] input) throws SQLException {
        ArrayList<MatOrder> orderArrayList = new ArrayList<>();
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

                // new seller
                MatSeller seller = new MatSeller(resultSet.getInt("sellerId"),
                        resultSet.getString("companyName"));
                seller.setContactName(resultSet.getString("contactName"));
                seller.setMobile(resultSet.getString("mobile"));
                seller.setLandLine(resultSet.getString("landLine"));
                seller.setFax(resultSet.getString("fax"));
                seller.setAccountNum(resultSet.getString("accountNum"));
                seller.setBankAddress(resultSet.getString("bankAddress"));
                seller.setAddress(resultSet.getString("address"));

                // new order
                MatOrder newOrder = new MatOrder(resultSet.getInt("serialNum"),
                        resultSet.getString("sku"));
                newOrder.setName(resultSet.getString("name"));
                newOrder.setType(resultSet.getString("type"));
                newOrder.setInvoice(resultSet.getString("invoice"));
                newOrder.setSigned(resultSet.getString("signed"));
                newOrder.setSkuSeller(resultSet.getString("skuSeller"));
                newOrder.setNote(resultSet.getString("note"));
                newOrder.setOrderDate(new Date(resultSet.getInt("orderDateYear"),
                        resultSet.getInt("orderDateMonth"),
                        resultSet.getInt("orderDateDay")));
                newOrder.setPaymentDate(new Date(resultSet.getInt("paymentDateYear"),
                        resultSet.getInt("paymentDateMonth"),
                        resultSet.getInt("paymentDateDay")));
                newOrder.setArrivalDate(new Date(resultSet.getInt("arrivalDateYear"),
                        resultSet.getInt("arrivalDateMonth"),
                        resultSet.getInt("arrivalDateDay")));
                newOrder.setInvoiceDate(new Date(resultSet.getInt("invoiceDateYear"),
                        resultSet.getInt("invoiceDateMonth"),
                        resultSet.getInt("invoiceDateDay")));
                newOrder.setSeller(seller);
                newOrder.setUnitAmount(resultSet.getDouble("unitAmount"));
                newOrder.setAmount(resultSet.getDouble("amount"));
                newOrder.setKgAmount();
                newOrder.setUnitPrice(resultSet.getDouble("unitPrice"));
                newOrder.setTotalPrice();

                orderArrayList.add(newOrder);
            }
            CloseConnectionToDB();
            return orderArrayList;
        } catch (SQLException e) {
            new HandleError(DatabaseUtil.class.getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            throw new SQLException();
        }
    }

    /**
     * get all product orders with in the input date range
     * @param input 2d array of size 2, 1st is start date (yyyy-mm-dd), 2nd is end date (yyyy-mm-dd)
     * @return ArrayList of product order
     * @throws SQLException if any error occurs while operating on database
     */
    public static ArrayList<ProductOrder> SelectProductOrderWithDateRange(int[][] input) throws SQLException {
        String SQLCommand = "SELECT * FROM productManagement WHERE " +
                "(orderDateYear * 10000 + orderDateMonth * 100 + orderDateDay) >= ? " +
                "AND (orderDateYear * 10000 + orderDateMonth * 100 + orderDateDay) <= ?";
        ArrayList<ProductOrder> orderArrayList = new ArrayList<>();
        try {
            ConnectToDB();

            PreparedStatement statement = connection.prepareStatement(SQLCommand);
            statement.setInt(1, input[0][0] * 10000 + input[0][1] * 100 + input[0][2]);
            statement.setInt(2, input[1][0] * 10000 + input[1][1] * 100 + input[1][2]);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {

                // new order
                ProductOrder newOrder = new ProductOrder(resultSet.getInt("serialNum"));
                newOrder.setSku(resultSet.getString("sku"));
                newOrder.setName(resultSet.getString("name"));
                newOrder.setCustomer(resultSet.getString("customer"));
                newOrder.setNote(resultSet.getString("note"));
                newOrder.setOrderDate(new Date(resultSet.getInt("orderDateYear"),
                        resultSet.getInt("orderDateMonth"),
                        resultSet.getInt("orderDateDay")));
                newOrder.setUnitAmount(resultSet.getDouble("unitAmount"));
                newOrder.setAmount(resultSet.getDouble("amount"));
                newOrder.setKgAmount();
                newOrder.setUnitPrice(resultSet.getDouble("unitPrice"));
                newOrder.setBasePrice(resultSet.getDouble("basePrice"));
                newOrder.setTotalPrice();
                newOrder.setFormulaIndex(resultSet.getInt("formulaIndex"));

                // adding order
                orderArrayList.add(newOrder);
            }
            CloseConnectionToDB();
            return orderArrayList;
        } catch (SQLException e) {
            new HandleError(DatabaseUtil.class.getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            throw new SQLException();
        } finally {
            CloseConnectionToDB();
        }
    }
}
