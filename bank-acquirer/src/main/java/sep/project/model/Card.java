package sep.project.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Card {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(nullable = false)
	//@Convert(converter = CryptoConverter.class)
	private String pan; //permanent account number

	@Column(nullable = false)
	//@Convert(converter = CryptoConverter.class)
	private String cvv; //Card Verification Value

	@Column(nullable = false) // Date is in format mm/yy
	private String expDate;

	@Column(nullable = false)
	//@Convert(converter = CryptoConverter.class)
	private String accountNumber;

	@Column(nullable = false)
	private Float availableFunds;

	public Card() {
		
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getPan() {
		return pan;
	}

	public void setPan(String pan) {
		this.pan = pan;
	}

	public String getCvv() {
		return cvv;
	}

	public void setCvv(String cvv) {
		this.cvv = cvv;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public Float getAvailableFunds() {
		return availableFunds;
	}

	public void setAvailableFunds(Float availableFunds) {
		this.availableFunds = availableFunds;
	}

	public String getExpDate() {
		return expDate;
	}

	public void setExpDate(String expDate) {
		this.expDate = expDate;
	}
	
}
