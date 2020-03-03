package Material;

public class Seller {
    private int sellerId;
    private String companyName;
    private String contactName;
    private String mobile;
    private String landLine;
    private String fax;
    private String accountNum;
    private String bankAddress;
    private String address;

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public int getSellerId() {
        return sellerId;
    }

    public void setSellerId(int sellerId) {
        this.sellerId = sellerId;
    }

    public Seller(int sellerId, String companyName, String contactName) {
        this.sellerId = sellerId;
        this.companyName = companyName;
        this.contactName = contactName;
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

    public static Seller stringToSeller (String seller) {
        String[] orderVal = seller.split(",");
        int sellerId = Integer.parseInt(orderVal[0]);
        String companyName = orderVal[1];
        String contactName = orderVal[2];
        String mobile = orderVal[3];
        String landLine = orderVal[4];
        String fax = orderVal[5];
        String accountNum = orderVal[6];
        String bankAddress = orderVal[7];
        String address = orderVal[8];

        Seller newSeller = new Seller(sellerId, companyName, contactName);
        newSeller.setMobile(mobile);
        newSeller.setLandLine(landLine);
        newSeller.setFax(fax);
        newSeller.setAccountNum(accountNum);
        newSeller.setBankAddress(bankAddress);
        newSeller.setAddress(address);

        return newSeller;
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

    public String toStringWithoutSellerId() {
        String returnVal = "";
        returnVal = addFormat(companyName, returnVal);
        returnVal = addFormat(contactName, returnVal);
        returnVal = addFormat(mobile + " ", returnVal);
        returnVal = addFormat(landLine + " ", returnVal);
        returnVal = addFormat(fax + " ", returnVal);
        returnVal = addFormat(accountNum + " ", returnVal);
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
        if (!(obj instanceof Seller)) {
            return false;
        }
        else {
            return ((Seller) obj).getSellerId() == this.sellerId && ((Seller) obj).getCompanyName().equals(this.companyName);
        }
    }
}
