package sep.project.DTOs;

import java.util.Date;

import sep.project.enums.Status;

public class CompletedDTO {
	
	private Status transactionStatus;

	private Long merchantOrderID;

	private Long acquirerOrderID;

	private Date acquirerTimestamp;
	
	private Long paymentID;

	private String redirectURL;
	
	public CompletedDTO() {
		
	}

	public Status getTransactionStatus() {
		return transactionStatus;
	}

	public void setTransactionStatus(Status transactionStatus) {
		this.transactionStatus = transactionStatus;
	}

	public Long getMerchantOrderID() {
		return merchantOrderID;
	}

	public void setMerchantOrderID(Long merchantOrderID) {
		this.merchantOrderID = merchantOrderID;
	}

	public Long getAcquirerOrderID() {
		return acquirerOrderID;
	}

	public void setAcquirerOrderID(Long acquirerOrderID) {
		this.acquirerOrderID = acquirerOrderID;
	}

	public Date getAcquirerTimestamp() {
		return acquirerTimestamp;
	}

	public void setAcquirerTimestamp(Date acquirerTimestamp) {
		this.acquirerTimestamp = acquirerTimestamp;
	}

	public Long getPaymentID() {
		return paymentID;
	}

	public void setPaymentID(Long paymentID) {
		this.paymentID = paymentID;
	}

	public String getRedirectURL() {
		return redirectURL;
	}

	public void setRedirectURL(String redirectURL) {
		this.redirectURL = redirectURL;
	}
	
}
