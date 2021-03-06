package sep.project.services;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import sep.project.dto.RegistrationDTO;
import sep.project.model.PaymentMethod;
import sep.project.model.Seller;
import sep.project.repositories.SellerRepository;

@Service
public class SellerService {
	
	@Autowired
	private SellerRepository sellerRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
		
	
	public Seller createSeller(RegistrationDTO registrationDTO, String password) {
		Seller seller = new Seller();
		seller.setName(registrationDTO.getName());
		seller.setEmail(registrationDTO.getEmail());
		seller.setPassword(passwordEncoder.encode(password));
		seller.setPaymentMethods(new HashSet<>());
		seller.setConfirmationLink(registrationDTO.getConfirmationLink());
		seller.setReturnLink(registrationDTO.getReturnLink());
		return seller;
	}
	
	public Seller findByEmail(String email) {
		
		return sellerRepository.findByEmailAndDeleted(email, false);
	}
	
	public Seller getSeller(Long id) {
		
		return sellerRepository.findByIdAndDeleted(id, false);
	}
	
	public Seller save(Seller seller) {
		
		return sellerRepository.save(seller);
	}
	
	public Set<PaymentMethod> getPayments(String email) {
		
		Seller seller = findByEmail(email);
		if(seller == null) {
			
			return null;
		}
		
		return seller.getPaymentMethods();
	}

}
