package application;
/**
 * Class used to store attributes for filling in the Copies explorer table
 * in the staff tab.
 * @author Charles Day.
 *
 */

public class ExplorerRow {
	private String resourceTitle;
	private String keeper;
	
	private int copyID;
	private int resourceID;
	private int loanDuration;
	private int orderNumber;
	
	private String borrowDate;
	private String lastRenewal;
	private String dueDate;
	
	private String history;
	
	//Filter: All
	/**
	 * Class constructor with all variables in it.
	 * @param resourceTitle title of resource.
	 * @param keeper keeper of resource.
	 * @param copyID Id of copy.
	 * @param resourceID id of resource.
	 * @param loanDuration loanduration of the resource.
	 * @param borrowDate borrowdate of resource.
	 * @param lastRenewal lastrenewal date of the resource.
	 * @param dueDate when the resource is due.
	 */
	public ExplorerRow(String resourceTitle, String keeper, 
			int copyID, int resourceID, int loanDuration, String borrowDate,
			String lastRenewal, String dueDate) {
		this.resourceTitle = resourceTitle;
		this.keeper = keeper;
		this.copyID = copyID;
		this.resourceID = resourceID;
		this.loanDuration = loanDuration;
		this.borrowDate = borrowDate;
		this.lastRenewal = lastRenewal;
		this.dueDate = dueDate;
	}
	
	//Filter: Overdue
	/**
	 * Class constructor with Variables for when a resource is overdue.
	 * @param resourceTitle title of the resource.
	 * @param keeper keeper of the resource.
	 * @param copyID id of the copy.
	 * @param resourceID id of the resource.
	 * @param borrowDate borrowdate of the resource.
	 * @param dueDate duedate of the resource.
	 */
	public ExplorerRow(String resourceTitle, String keeper, int copyID,
			int resourceID, String borrowDate, String dueDate) {
		this.resourceTitle = resourceTitle;
		this.keeper = keeper;
		this.copyID = copyID;
		this.resourceID = resourceID;
		this.borrowDate = borrowDate;
		this.dueDate = dueDate;
	}
	
	//Filter: Requested
	/**
	 * Class constructor with variables for requesting resources.
	 * @param keeper keeper of resource.	
	 * @param resourceID id of the resource.
	 */
	public ExplorerRow(String keeper, int resourceID) {
		this.keeper = keeper;
		this.resourceID = resourceID;
	}
	
	//Filter: History
	/**
	 * Class constructor with variables for getting a keepers history.
	 * @param copyID id of the copy.
	 * @param keeper keeper of the copy.
	 * @param history history of the copy.
	 */
		public ExplorerRow(int copyID, String keeper, String history) {
			this.copyID = copyID;
			this.keeper = keeper;
			this.history = history;
	}

		/**
		 * Getter method for the resourcetitle.
		 * @return resourcetitle title of the resource.
		 */
	public String getResourceTitle() {
		return resourceTitle;
	}
	

	/**
	 * Setter for the resourcetitle.
	 * @param resourceTitle title of the resource.
	 */
	public void setResourceTitle(String resourceTitle) {
		this.resourceTitle = resourceTitle;
	}

	/**
	 * Getter method for the keeper.
	 * @return keeper of the copy.
	 */
	public String getKeeper() {
		return keeper;
	}

	/**
	 * Setter method for the keeper.
	 * @param keeper of the copy.
	 */
	public void setKeeper(String keeper) {
		this.keeper = keeper;
	}
	
	/**
	 * Getter method for copy history.
	 * @return history of the copy.
	 */
	public String getHistory() {
		return history;
	}

	/**
	 * Setter method for copy history.
	 * @param history copy history.
	 */
	public void setHistory(String history) {
		this.history = history;
	}

	/**
	 * Getter method of copyid.
	 * @return copyid id of the copy.
	 */
	public int getCopyID() {
		return copyID;
	}

	/**
	 * Setter method of copyid.
	 * @param copyID id of the copy.
	 */
	public void setCopyID(int copyID) {
		this.copyID = copyID;
	}

	/**
	 * Getter method for resourceid.
	 * @return resourceid id of the resource.
	 */
	public int getResourceID() {
		return resourceID;
	}

	/**
	 * Setter method for resource resourceid.
	 * @param resourceID id of the resource.
	 */
	public void setResourceID(int resourceID) {
		this.resourceID = resourceID;
	}

	/**
	 * Getter method for loan duration.
	 * @return loanduration duration of the loan.
	 */
	public int getLoanDuration() {
		return loanDuration;
	}

	/**
	 * Setter method for loan duration.
	 * @param loanDuration duration of the loan.	
	 */
	public void setLoanDuration(int loanDuration) {
		this.loanDuration = loanDuration;
	}

	/**
	 * Getter method for ordernumber.
	 * @return the ordernumber of the resource.	
	 */
	public int getOrderNumber() {
		return orderNumber;
	}

	/**
	 * Setter method for ordernumber.
	 * @param orderNumber ordernumber of the resource.
	 */
	public void setOrderNumber(int orderNumber) {
		this.orderNumber = orderNumber;
	}

	/**
	 * Getter method for borrowdate.
	 * @return borrowdate of the copy.
	 */
	public String getBorrowDate() {
		return borrowDate;
	}

	/**
	 * Setter method for borrowdate.
	 * @param borrowDate borrowdate of the copy.
	 */
	public void setBorrowDate(String borrowDate) {
		this.borrowDate = borrowDate;
	}

	/**
	 * Getter method of last renewal.
	 * @return lastrenewal lastrenewal date of the copy.
	 */
	public String getLastRenewal() {
		return lastRenewal;
	}

	/**
	 * Setter method for lastrenewal.
	 * @param lastRenewal lastrenewal date of the copy.
	 */
	public void setLastRenewal(String lastRenewal) {
		this.lastRenewal = lastRenewal;
	}

	/**
	 * Getter method for the due date.
	 * @return duedate of a copy.
	 */
	public String getDueDate() {
		return dueDate;
	}

	/**
	 * Setter method for the duedate.
	 * @param dueDate of the copy.
	 */
	public void setDueDate(String dueDate) {
		this.dueDate = dueDate;
	}
	
}
