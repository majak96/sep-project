package sep.project.dto;

import java.util.Date;

public class CallBackDTO {

	private Long id;

	private String orderId;

	private String status;

	private String price_amount;

	private String priceCurrency;

	private String receive_currecy;

	private String receive_amount;
	
	private String pay_amount;
	
	private String pay_currency;
	
	private String underpaid_amount;
	
	private String overpaid_amount;

	private Boolean is_refundable;

	private Date created_at;

	private String token;
	
	public CallBackDTO() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPrice_amount() {
		return price_amount;
	}

	public void setPrice_amount(String price_amount) {
		this.price_amount = price_amount;
	}

	public String getPriceCurrency() {
		return priceCurrency;
	}

	public void setPriceCurrency(String priceCurrency) {
		this.priceCurrency = priceCurrency;
	}

	public String getReceive_currecy() {
		return receive_currecy;
	}

	public void setReceive_currecy(String receive_currecy) {
		this.receive_currecy = receive_currecy;
	}

	public String getReceive_amount() {
		return receive_amount;
	}

	public void setReceive_amount(String receive_amount) {
		this.receive_amount = receive_amount;
	}

	public String getPay_amount() {
		return pay_amount;
	}

	public void setPay_amount(String pay_amount) {
		this.pay_amount = pay_amount;
	}

	public String getPay_currency() {
		return pay_currency;
	}

	public void setPay_currency(String pay_currency) {
		this.pay_currency = pay_currency;
	}

	public String getUnderpaid_amount() {
		return underpaid_amount;
	}

	public void setUnderpaid_amount(String underpaid_amount) {
		this.underpaid_amount = underpaid_amount;
	}

	public String getOverpaid_amount() {
		return overpaid_amount;
	}

	public void setOverpaid_amount(String overpaid_amount) {
		this.overpaid_amount = overpaid_amount;
	}

	public Boolean getIs_refundable() {
		return is_refundable;
	}

	public void setIs_refundable(Boolean is_refundable) {
		this.is_refundable = is_refundable;
	}

	public Date getCreated_at() {
		return created_at;
	}

	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
