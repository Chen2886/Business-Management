package Product;

import Main.DatabaseUtil;

import java.io.Serializable;

public class FormulaItem implements Serializable {

    private Formula parent;
    private String name;
    private double amount;
    private double unitPrice;
    private double totalPrice;

    public Formula getParent() {
        return parent;
    }

    public void setParent(Formula parent) {
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAmount() {
        return Math.round(amount * 100.0) / 100.0;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getUnitPrice() {
        if (unitPrice == 0.0) {
            try {
                setUnitPrice(DatabaseUtil.GetMatUnitPrice(name));
                setTotalPrice();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return Math.round(unitPrice * 100.0) / 100.0;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public double getTotalPrice() {
        return Math.round(totalPrice * 100.0) / 100.0;
    }

    public void setTotalPrice() {
        this.totalPrice = amount * unitPrice;
    }

    /**
     * Given a formula item, convert to a formula object
     * @param formulaItem the item that needs to be converted
     * @return a formula object that has all the information
     */
    public static Formula convertToFormula(FormulaItem formulaItem) {
        Formula returnVal = new Formula();
        returnVal.setName(formulaItem.getName());
        returnVal.setAmount(formulaItem.getAmount());
        returnVal.setUnitPrice(formulaItem.getUnitPrice());
        returnVal.setTotalPrice();
        return returnVal;
    }

}
