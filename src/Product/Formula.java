package Product;

import java.util.ArrayList;

public class Formula {

    private String fileName;
    private ArrayList<FormulaItem> formula;

    public Formula() {
        fileName = "";
        formula = new ArrayList<>();
    }

    public Formula(String fileName) {
        this.fileName = fileName;
    }

    public Formula(String fileName, ArrayList<FormulaItem> formula) {
        this.fileName = fileName;
        this.formula = formula;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public ArrayList<FormulaItem> getFormula() {
        return formula;
    }

    public void setFormula(ArrayList<FormulaItem> formula) {
        this.formula = formula;
    }
}
