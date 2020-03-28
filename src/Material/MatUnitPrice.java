package Material;

public class MatUnitPrice {
    private String name;
    private double unitPrice;
    private String note;

    public MatUnitPrice() {
        this("");
    }

    public MatUnitPrice(String name) {
        this(name, 0.0);
    }

    public MatUnitPrice(String name, double unitPrice) {
        this(name, unitPrice, "");
    }

    public MatUnitPrice(String name, double unitPrice, String note) {
        this.name = name;
        this.unitPrice = unitPrice;
        this.note = note;
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
        return String.format("%s,%.2f, %s", name, unitPrice, note);
    }
}
