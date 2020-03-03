package Product;

import java.text.DecimalFormat;

public class Order {
	private static DecimalFormat decimalFormat = new DecimalFormat("0.00");

	private int serialNum;

	private String sku;
	private String prod;
	private String customer;
	private String note;
	private Date orderDate;
	// kgAmount = unitAmount * amount;
	// totalPrice = kgAmount * unitPrice;
	private double unitAmount; //规格
	private double amount;  //数量
	private double unitPrice;  //单价
	private Formula formula;

	public Order(int serialNum, String sku, String prod, String customer, double unitAmount, double amount, Date orderDate) {
		this.serialNum = serialNum;
		this.sku = sku;
		this.prod = prod;
		this.customer = customer;
		this.unitAmount = unitAmount;
		this.amount = amount;
		this.orderDate = orderDate;
		this.unitPrice = 0;
	}

	@Override
	public String toString() {
		String returnVal = "";
		returnVal = addFormat(sku, returnVal);
		returnVal = addFormat(prod, returnVal);
		returnVal = addFormat(customer, returnVal);
		returnVal = addFormat(note, returnVal);
		returnVal = addFormat(unitAmount, returnVal);
		returnVal = addFormat(amount, returnVal);
		returnVal = addFormat(unitPrice, returnVal);
		returnVal = addFormat(orderDate.toString(), returnVal);

		return returnVal;
	}

	public double getKgAmount() {
		return Double.parseDouble(decimalFormat.format(unitAmount * amount));
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

	public String getProd() {
		return prod;
	}

	public void setProd(String prod) {
		this.prod = prod;
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

	public double getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(double unitPrice) {
		this.unitPrice = unitPrice;
	}

	public double getTotalPrice() {
		return Double.parseDouble(decimalFormat.format(unitAmount * amount * unitPrice));
	}

	public Formula getFormula() {
		return formula;
	}

	public void setFormula(Formula formula) {
		this.formula = formula;
	}

	public Date getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
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

	/**
	 * helper function to toString() override
	 * if content null or empty, then , will be added
	 * @param content new content to be added
	 * @param input original string
	 * @return input with content
	 */
	private String addFormat(double content, String input) {
		return input + content + ",";
	}
}
