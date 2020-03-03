package Material;

import java.text.DecimalFormat;

public class Order {
    private static DecimalFormat decimalFormat = new DecimalFormat("0.00");

    private int serialNum;

    private String sku;
    private String name;
    private String type;
    private String invoice;
    private String signed;
    private String skuSeller;
    private String note;

    private Date orderDate;
    private Date paymentDate;
    private Date arrivalDate;
    private Date invoiceDate;

    private Seller seller;

    // kgAmount = unitAmount * amount;
    // totalPrice = kgAmount * unitPrice;
    private double unitAmount; //规格
    private double amount;  //数量
    private double kgAmount; //公斤
    private double unitPrice;  //单价
    private double totalPrice;  //总价

    public Order(int serialNum, String sku, String name, String type, Date orderDate, Seller seller, double unitAmount,
                 double amount) {
        this.serialNum = serialNum;
        this.sku = sku;
        this.name = name;
        this.type = type;
        this.orderDate = orderDate;
        this.seller = seller;
        this.unitAmount = unitAmount;
        this.amount = amount;
        try {
            calcKgAmount();
        } catch (IllegalAccessException e) {
            HandleError error = new HandleError(getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage(), e.getStackTrace(), false);
            error.WriteToLog();
        }
    }

    private void calcKgAmount() throws IllegalAccessException {
        try {
            kgAmount = unitAmount * amount;
        } catch (Exception e) {
            throw new IllegalAccessException();
        }
    }

    private void calcTotalPrice() throws IllegalAccessException {
        try {
            totalPrice = kgAmount * unitPrice;
        } catch (Exception e) {
            throw new IllegalAccessException();
        }
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getInvoice() {
        return invoice == null ? "" : invoice;
    }

    public void setInvoice(String invoice) {
        this.invoice = invoice;
    }

    public String getSigned() {
        return signed == null ? "" : signed;
    }

    public void setSigned(String signed) {
        this.signed = signed;
    }

    public String getSkuSeller() {
        return skuSeller == null ? "" : skuSeller;
    }

    public void setSkuSeller(String skuSeller) {
        this.skuSeller = skuSeller;
    }

    public String getNote() {
        return note == null ? "" : note;
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

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public Date getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(Date arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public Date getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public Seller getSeller() {
        return seller;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    public double getUnitAmount() {
        return unitAmount;
    }

    public void setUnitAmount(double unitAmount) throws IllegalAccessException {
        this.unitAmount = unitAmount;
        calcKgAmount();
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount)  throws IllegalAccessException {
        this.amount = amount;
        calcKgAmount();
    }

    public double getKgAmount() {
        return kgAmount;
    }

    public void setKgAmount()  throws IllegalAccessException {
        calcKgAmount();
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) throws IllegalAccessException {
        this.unitPrice = unitPrice;
        calcTotalPrice();
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice()  throws IllegalAccessException {
        calcTotalPrice();
    }

    public String getCompanyName() {
        return seller.getCompanyName();
    }

    public String getContactName() {
        return seller.getContactName();
    }

    public String getMobile() {
        return seller.getMobile();
    }

    public String getLandLine() {
        return seller.getLandLine();
    }

    public String getFax() {
        return seller.getFax();
    }

    public String getAccountNum() {
        return seller.getAccountNum();
    }

    public String getBankAddress() {
        return seller.getBankAddress();
    }

    public String getAddress() {
        return seller.getAddress();
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
        returnVal = addFormat(type, returnVal);
        returnVal = addFormat(orderDate == null ? "" : orderDate.toString(), returnVal);
        returnVal = addFormat(paymentDate == null ? "" : paymentDate.toString(), returnVal);
        returnVal = addFormat(arrivalDate == null ? "" : arrivalDate.toString(), returnVal);
        returnVal = addFormat(invoiceDate == null ? "" : invoiceDate.toString(), returnVal);
        returnVal = addFormat(invoice == null ? "" : invoice, returnVal);
        returnVal = addFormat(String.valueOf(unitAmount), returnVal);
        returnVal = addFormat(String.valueOf(amount), returnVal);
        returnVal = addFormat(String.valueOf(getKgAmount()), returnVal);
        returnVal = addFormat(String.valueOf(unitPrice), returnVal);
        returnVal = addFormat(String.valueOf(getTotalPrice()), returnVal);
        returnVal = addFormat((signed == null || signed.equals("")) ? "" : signed, returnVal);
        returnVal = addFormat((skuSeller == null || skuSeller.equals("")) ? "" : skuSeller, returnVal);
        returnVal = addFormat(seller == null ? "" : seller.toStringWithoutSellerId(), returnVal);
        returnVal = addFormat((note == null || note.equals("")) ? "" : note, returnVal);
        System.out.println(returnVal);
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
