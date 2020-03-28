package Product;

import java.io.Serializable;
import java.util.ArrayList;

public class Formula implements Serializable {
    private String name;

    private double amount;
    private double unitPrice;
    private double totalPrice;

    private ArrayList<Formula> formulaList;
    private ArrayList<FormulaItem> simpleItemList;

    // constructor chain
    public Formula() {
        this("");
    }

    public Formula(String name) {
        this(name, 0);
    }

    public Formula(String name, double amount) {
        this(name, amount, 0);
    }

    public Formula(String name, double amount, double unitPrice) {
        this.name = name;
        this.amount = amount;
        this.unitPrice = unitPrice;
        setTotalPrice();
        formulaList = new ArrayList<>();
        simpleItemList = new ArrayList<>();
    }

    public void addItem(FormulaItem item) {
        simpleItemList.add(item);
    }

    public void removeItem(FormulaItem item) {
        simpleItemList.remove(item);
    }

    public void addFormula(Formula formula) {
        formulaList.add(formula);
    }

    public void removeFormula(Formula formula) {
        formulaList.remove(formula);
    }

    public ArrayList<Formula> getFormulaList() {
        return formulaList;
    }

    public ArrayList<FormulaItem> getSimpleItemList() {
        return simpleItemList;
    }

    public void setFormulaList(ArrayList<Formula> formulaList) {
        this.formulaList = formulaList;
    }

    public void setSimpleItemList(ArrayList<FormulaItem> simpleItemList) {
        this.simpleItemList = simpleItemList;
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

    @Override
    public String toString() {
        return String.format("%s,%.2f,%.2f,%.2f\n", name, amount, unitPrice, totalPrice);
    }
}