package Product;

import Main.DatabaseUtil;

import java.io.Serializable;
import java.util.ArrayList;

public class Formula implements Serializable {

    private Formula parent;
    private String name;
    private double amount;
    private double unitPrice;
    private double totalPrice;

    private ArrayList<Formula> formulaList;

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
        this.parent = null;
        this.name = name;
        this.amount = amount;
        this.unitPrice = unitPrice;
        setTotalPrice();
        formulaList = new ArrayList<>();
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

    public void setFormulaList(ArrayList<Formula> formulaList) {
        this.formulaList = formulaList;
    }

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