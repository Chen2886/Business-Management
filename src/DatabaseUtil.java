import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class DatabaseUtil {
    private static String dataBaseLocationFile = "jdbc:sqlite:BusinessCashFlow.db";
    private static Connection connection;

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
     * Create database if not exist
     * @throws SQLException if any error occurs while operating on database
     */
    private static void CreateNewDB() throws SQLException {
        ConnectToDB();
        try {
            connection = DriverManager.getConnection(dataBaseLocationFile);
            if (connection != null) {
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
            e.printStackTrace();
            HandleError error = new HandleError("DatabaseUtil", Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            error.WriteToLog();
            throw new SQLException();
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
                } else if ("productManagement".equals(resultSet.getString("TABLE_NAME"))) {
                    numOfTable++;
                }
            }
            if (numOfTable == 3) {
                CloseConnectionToDB();
            } else {
                CreateNewTable("materialManagement");
                CreateNewTable("seller");
                CreateNewTable("productManagement");
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
            } else if (tableName.equals("productManagement")) {
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
                        "formulaFile    TEXT			\n" +
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

    /**
     * Get All the mat order from database
     * @return a list of all mat order
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
     * Get All the mat order from database
     * @return a list of all mat order
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
                newOrder.setTotalPrice();
                newOrder.setFormulaFile(resultSet.getString("formulaFile"));

                // adding order
                orderObservableList.add(newOrder);
            }
            CloseConnectionToDB();
            return orderObservableList;
        } catch (SQLException e) {
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
     * Return a list of all sellers available
     * @return all sellers in the database
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
            HandleError error = new HandleError("DataBaseUtility", Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            error.WriteToLog();
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
     * Add seller into the seller database
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
            HandleError error = new HandleError("DataBaseUtility", Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            error.WriteToLog();
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
            //System.out.println("UpdateSellerInSeller failed");
            HandleError error = new HandleError("DataBaseUtility", Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            error.WriteToLog();
            throw new SQLException();
        }
    }

    /**
     * update seller info
     * @param newSeller the new seller that needs to be updated
     * @throws SQLException if any error occurs while operating on database
     */
    public static void UpdateMatSellerInSeller(MatSeller newSeller) throws SQLException {
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
            HandleError error = new HandleError("DataBaseUtility", Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            error.WriteToLog();
            throw new SQLException();
        } finally {
            CloseConnectionToDB();
        }
    }

}
