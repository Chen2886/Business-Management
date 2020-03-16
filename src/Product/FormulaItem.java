package Product;

import java.util.ArrayList;

public class FormulaItem {
    private String itemName;
    private double amount;
    private double unitPrice;
    private double totalPrice;
    private ArrayList<FormulaItem> formula;

    public FormulaItem() {
        this("");
    }

    public FormulaItem(String itemName) {
        this(itemName, 0);
    }

    public FormulaItem(String itemName, double amount) {
        this(itemName, amount, 0);
    }

    public FormulaItem(String itemName, double amount, double unitPrice) {
        this.itemName = itemName;
        this.amount = amount;
        this.unitPrice = unitPrice;
        this.totalPrice = amount * unitPrice;
    }

    public FormulaItem(ArrayList<FormulaItem> formula) {
        this.formula = formula;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
        setTotalPrice();
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
        setTotalPrice();
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice() {
        this.totalPrice = amount * unitPrice;
    }

    public ArrayList<FormulaItem> getFormula() {
        return formula;
    }

    public void setFormula(ArrayList<FormulaItem> formula) {
        this.formula = formula;
    }

    @Override
    public String toString() {
        return String.format("%s,%f,%f,%f", itemName, amount, unitPrice, totalPrice);
    }
}