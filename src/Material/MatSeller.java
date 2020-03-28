package Material;

// from my other packages
import Main.*;

public class MatSeller {
	private int sellerId;
	private String companyName;
	private String contactName;
	private String mobile;
	private String landLine;
	private String fax;
	private String accountNum;
	private String bankAddress;
	private String address;

	public MatSeller(int sellerId, String companyName) {
		this.sellerId = sellerId;
		this.companyName = companyName;
		this.contactName = "";
		this.mobile = "";
		this.landLine = "";
		this.fax = "";
		this.accountNum = "";
		this.bankAddress = "";
		this.address = "";
	}

	public int getSellerId() {
		return sellerId;
	}

	public void setSellerId(int sellerId) {
		this.sellerId = sellerId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getLandLine() {
		return landLine;
	}

	public void setLandLine(String landLine) {
		this.landLine = landLine;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getAccountNum() {
		return accountNum;
	}

	public void setAccountNum(String accountNum) {
		this.accountNum = accountNum;
	}

	public String getBankAddress() {
		return bankAddress;
	}

	public void setBankAddress(String bankAddress) {
		this.bankAddress = bankAddress;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String toString(boolean includeSellerId) {
		String returnVal = "";
		if (includeSellerId) returnVal = addFormat(String.valueOf(sellerId), returnVal);
		returnVal = addFormat(companyName, returnVal);
		returnVal = addFormat(contactName, returnVal);
		returnVal = addFormat(mobile, returnVal);
		returnVal = addFormat(landLine, returnVal);
		returnVal = addFormat(fax, returnVal);
		returnVal = addFormat(accountNum, returnVal);
		returnVal = addFormat(bankAddress, returnVal);
		returnVal = addFormat(address, returnVal);
		return returnVal;
	}

	@Override
	public String toString() {
		String returnVal = "";
		returnVal = addFormat(String.valueOf(sellerId), returnVal);
		returnVal = addFormat(companyName, returnVal);
		returnVal = addFormat(contactName, returnVal);
		returnVal = addFormat(mobile, returnVal);
		returnVal = addFormat(landLine, returnVal);
		returnVal = addFormat(fax, returnVal);
		returnVal = addFormat(accountNum, returnVal);
		returnVal = addFormat(bankAddress, returnVal);
		returnVal = addFormat(address, returnVal);
		return returnVal;
	}

	private String addFormat(String content, String input) {
		if (content == null || content.equals("")) {
			return input + ",";
		}
		else {
			return input + content + ",";
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof MatSeller)) {
			return false;
		}
		else {
			return ((MatSeller) obj).getSellerId() == this.sellerId && ((MatSeller) obj).getCompanyName().equals(this.companyName) &&
					((MatSeller) obj).contactName.equals(this.contactName);
		}
	}
}
