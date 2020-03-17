package Product;

import java.util.ArrayList;

public class Formula {
    private String folderPath;
    private String formulaName;

    private double amount;
    private double unitPrice;
    private double totalPrice;

    private ArrayList<Formula> formulaList;
    private ArrayList<FormulaItem> simpleItemList;

    // constructor chain
    public Formula() {
        this("");
    }

    public Formula(String folderPath) {
        this(folderPath, "");
    }

    public Formula(String folderPath, String formulaName) {
        this(folderPath, formulaName, 0);
    }

    public Formula(String folderPath, String formulaName, double amount) {
        this(folderPath, formulaName, amount, 0);
    }

    public Formula(String folderPath, String formulaName, double amount, double unitPrice) {
        this.folderPath = folderPath;
        this.formulaName = formulaName;
        this.amount = amount;
        this.unitPrice = unitPrice;
        setTotalPrice();
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

    public String getFolderPath() {
        return folderPath;
    }

    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }

    public String getFormulaName() {
        return formulaName;
    }

    public void setFormulaName(String formulaName) {
        this.formulaName = formulaName;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
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
        this.totalPrice = amount * unitPrice;
    }

    @Override
    public String toString() {
        return String.format("%s,%.2f,%.2f,%.2f,%s\n", formulaName, amount, unitPrice, totalPrice, folderPath);
    }
}