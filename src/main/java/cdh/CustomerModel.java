package cdh;

public class CustomerModel {
	/*
	mysql> DESC CDH_DB.CDH_CUSTOMERS;
	+-------------------+--------------+------+-----+---------+----------------+
	| Field             | Type         | Null | Key | Default | Extra          |
	+-------------------+--------------+------+-----+---------+----------------+
	| cust_id           | int          | NO   | PRI | NULL    | auto_increment |
	| ORGNO             | varchar(255) | YES  |     | NULL    |                |s
	| ORGNAME           | varchar(255) | YES  |     | NULL    |                |
	| ACCNUM            | varchar(255) | YES  |     | NULL    |                |
	| ACCNAME           | varchar(255) | YES  |     | NULL    |                |
	| SPEDITOR          | varchar(25)  | YES  |     | NULL    |                |
	| STATUS            | tinyint      | YES  |     | NULL    |                |
	| BRANCH            | varchar(10)  | YES  |     | NULL    |                |
	| VATNO             | varchar(25)  | YES  |     | NULL    |                |
	| VATCOUNTRYCODE    | varchar(3)   | YES  |     | NULL    |                |
	| INTERNAL_CUSTOMER | varchar(1)   | YES  |     | NULL    |                |
	+-------------------+--------------+------+-----+---------+----------------+
	 */

		// Column available in JSON Response
		public String rownum;
	    //Jgzz_fiscal_code in OeBS
	    public String organizationNumber;
	    //Party_name in OeBS
	    //private String organizationName;
	    //private String accountName;
	    //Party/Organization name and Account name should be merged to Customer Name
	    private String customerName;
	    //AccountNumber in OeBS
	    private String customerNumber;
	    /*CDH have party and account level, but only account level is necessary here.*/
	    private String accountStatus;
	    //Attribute18 (on Account level) in OeBS
	    private String branchOrFilial;
	    
	    private String branchOrFilialId;
	    //TAX_REFERENCE in OeBS
	    //private String vatNumber;
	    //private String speditor;
	    //private String vatCountryCode;
	    //Internal_Flag / Registration Type in OeBS. Might need information about internal customers because of VAT calculations
	    //public String isInternalCustomer;

	    public String getCustomerName() {
	        return customerName;
	    }

	    public void setCustomerName(String customerName) {
	        this.customerName = customerName;
	    }

	    public String getCustomerNumber() {
	        return customerNumber;
	    }

	    public void setCustomerNumber(String customerNumber) {
	        this.customerNumber = customerNumber;
	    }

	    public String getOrganizationNumber() {
	        return organizationNumber;
	    }

	    public void setOrganizationNumber(String organizationNumber) {
	        this.organizationNumber = organizationNumber;
	    }

	    public String getAccountStatus() {
	        return accountStatus;
	    }

	    public void setAccountStatus(String accountStatus) {
	        this.accountStatus = accountStatus;
	    }

	    public String getBranchOrFilial() {
	        return branchOrFilial;
	    }

	    public void setBranchOrFilial(String branchOrFilial) {
	        this.branchOrFilial = branchOrFilial;
	    }


		public String getRownum() {
			return rownum;
		}

		public void setRownum(String rownum) {
			this.rownum = rownum;
		}

		public String getBranchOrFilialId() {
			return branchOrFilialId;
		}

		public void setBranchOrFilialId(String branchOrFilialId) {
			this.branchOrFilialId = branchOrFilialId;
		}

	    
}
