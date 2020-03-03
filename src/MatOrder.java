import java.text.DecimalFormat;

public class MatOrder {
	private static DecimalFormat twoDecimalFormat = new DecimalFormat("0.00");
	private static DecimalFormat integerFormat = new DecimalFormat("0");

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

	private MatSeller seller;

	// kgAmount = unitAmount * amount;
	// totalPrice = kgAmount * unitPrice;
	private double unitAmount; //规格
	private double amount;  //数量
	private double kgAmount; //公斤
	private double unitPrice;  //单价
	private double totalPrice;  //总价

	public MatOrder(int serialNum, String sku) {
		this.serialNum = serialNum;
		this.sku = sku;
		this.name = "";
		this.type = "";
		this.invoice = "";
		this.signed = "";
		this.skuSeller = "";
		this.note = "";
		this.orderDate = new Date(0, 0, 0);
		this.paymentDate = new Date(0, 0, 0);
		this.arrivalDate = new Date(0, 0, 0);
		this.invoiceDate = new Date(0, 0, 0);
		this.unitAmount = 0;
		this.amount = 0;
		this.kgAmount = 0;
		this.unitAmount = 0;
		this.totalPrice = 0;
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
		return invoice;
	}

	public void setInvoice(String invoice) {
		this.invoice = invoice;
	}

	public String getSigned() {
		return signed;
	}

	public void setSigned(String signed) {
		this.signed = signed;
	}

	public String getSkuSeller() {
		return skuSeller;
	}

	public void setSkuSeller(String skuSeller) {
		this.skuSeller = skuSeller;
	}

	public String getNote() {
		return note;
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

	public MatSeller getSeller() {
		return seller;
	}

	public void setSeller(MatSeller seller) {
		this.seller = seller;
	}

	public double getUnitAmount() {
		return Double.parseDouble(integerFormat.format(unitAmount));
	}

	public void setUnitAmount(double unitAmount) {
		this.unitAmount = unitAmount;
	}

	public double getAmount() {
		return Double.parseDouble(integerFormat.format(amount));
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public double getKgAmount() {
		return Double.parseDouble(integerFormat.format(kgAmount));
	}

	public void setKgAmount() {
		this.kgAmount = unitAmount * amount;;
	}

	public double getUnitPrice() {
		return Double.parseDouble(twoDecimalFormat.format(unitPrice));
	}

	public void setUnitPrice(double unitPrice) {
		this.unitPrice = unitPrice;
	}

	public double getTotalPrice() {
		return Double.parseDouble(twoDecimalFormat.format(totalPrice));
	}

	public void setTotalPrice() {
		this.totalPrice = kgAmount * unitPrice;;
	}

	public String getCompany() {
		return seller == null ? "" : seller.getCompanyName();
	}

	public String getContact() {
		return seller == null ? "" : seller.getContactName();
	}

	public String getMobile() {
		return seller == null ? "" : seller.getMobile();
	}

	public String getLand() {
		return seller == null ? "" : seller.getLandLine();
	}

	public String getFax() {
		return seller == null ? "" : seller.getFax();
	}

	public String getAccount() {
		return seller == null ? "" : seller.getAccountNum();
	}

	public String getBank() {
		return seller == null ? "" : seller.getBankAddress();
	}

	public String getAddress() {
		return seller == null ? "" : seller.getAddress();
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
		returnVal = addFormat(seller == null ? "" : seller.toString(false), returnVal);
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
