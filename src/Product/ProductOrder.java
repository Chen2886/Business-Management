package Product;

// from my other packages
import Main.*;

public class ProductOrder {

    private int serialNum;

    private String sku;
    private String name;
    private String customer;
    private String note;
    private Date orderDate;

    // kgAmount = unitAmount * amount;
    // totalPrice = kgAmount * unitPrice;
    private double unitAmount; //规格
    private double amount;  //数量
    private double kgAmount; //公斤
    private double unitPrice;  //单价
    private double totalPrice;  //总价
    private double basePrice; //成本价

    private String formulaFile;

    public ProductOrder(int serialNum) {
        this.serialNum = serialNum;
        this.sku = "";
        this.name = "";
        this.customer = "";
        this.note = "";
        this.orderDate = new Date(0, 0, 0);
        this.unitAmount = 0.0;
        this.amount = 0.0;
        this.unitPrice = 0.0;
        this.basePrice = 0.0;
        this.formulaFile = "";
        setKgAmount();
        setTotalPrice();
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public int getSerialNum() {
        return serialNum;
    }

    public void setSerialNum(int serialNum) {
        this.serialNum = serialNum;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public double getUnitAmount() {
        return unitAmount;
    }

    public void setUnitAmount(double unitAmount) {
        this.unitAmount = unitAmount;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getKgAmount() {
        return kgAmount;
    }

    public void setKgAmount() {
        this.kgAmount = unitAmount * amount;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice() {
        this.totalPrice = kgAmount * unitPrice;
    }

    public String getFormulaFile() {
        return formulaFile;
    }

    public void setFormulaFile(String formulaFile) {
        this.formulaFile = formulaFile;
    }

    /**
     * an order's string format
     * @return order's string format
     */
    @Override
    public String toString() {
        String returnVal = "";
        returnVal = addFormat(sku, returnVal);
        returnVal = addFormat(name, returnVal);
        returnVal = addFormat(customer, returnVal);
        returnVal = addFormat(note, returnVal);
        returnVal = addFormat(orderDate.toString(), returnVal);
        returnVal = addFormat(String.valueOf(unitAmount), returnVal);
        returnVal = addFormat(String.valueOf(amount), returnVal);
        returnVal = addFormat(String.valueOf(getKgAmount()), returnVal);
        returnVal = addFormat(String.valueOf(unitPrice), returnVal);
        returnVal = addFormat(String.valueOf(getTotalPrice()), returnVal);
        returnVal = addFormat(formulaFile, returnVal);

        return returnVal;
    }

    /**
     * helper function to toString() override
     * if content null or empty, then , will be added
     * @param content new content to be added
     * @param input original string
     * @return input with content
     */
    private String addFormat(String content, String input) {
        if (content == null || content.equals("")) {
            return input + ",";
        }
        else {
            return input + content + ",";
        }
    }

}
