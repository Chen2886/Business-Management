package Product;

import Main.*;

public class ProdUnitPrice {
    private int serialNum;
    private Date date;
    private String sku;
    private String customer;
    private String name;
    private double unitPrice;
    private String note;

    public ProdUnitPrice() {
        this("");
    }

    public ProdUnitPrice(String name) {
        this(name, 0.0);
    }

    public ProdUnitPrice(String name, double unitPrice) {
        this.serialNum = 0;
        this.name = name;
        this.unitPrice = unitPrice;
        this.date = new Date(0, 0,0);
        this.sku = "";
        this.customer = "";
        this.note = "";
    }

    public int getSerialNum() {
        return serialNum;
    }

    public void setSerialNum(int serialNum) {
        this.serialNum = serialNum;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String toString() {
        return String.format("%s,%s,%s,%s,%.2f,%s", date.toString(), sku, customer, name, unitPrice, note);
    }
}
