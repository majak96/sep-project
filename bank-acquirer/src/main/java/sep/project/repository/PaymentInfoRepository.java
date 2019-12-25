package sep.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import sep.project.model.PaymentInfo;
import sep.project.model.Transaction;

public interface PaymentInfoRepository extends JpaRepository<PaymentInfo, Long> {
	
	PaymentInfo findByPaymentURL(String url);

	PaymentInfo findByTransaction(Transaction t);

}