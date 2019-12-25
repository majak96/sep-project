 package sep.project.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sep.project.model.Merchant;
import sep.project.repositories.MerchantRepository;

/**
* Generated by Spring Data Generator on 20/12/2019
*/
@Service
public class MerchantService {

	private MerchantRepository merchantRepository;

	@Autowired
	public MerchantService(MerchantRepository merchantRepository) {
		this.merchantRepository = merchantRepository;
	}
	
	/**
	 * Metoda za pronalazak prodavca na osnovu email adrese
	 * @param email email adresa prodavca
	 * @return prodavac sa datom adresom
	 */
	public Merchant getMerchant(String email) {
		Optional<Merchant> merchant = this.merchantRepository.findByEmail(email);
		if (merchant.isPresent()) {
			return merchant.get();
		}
		
		return null;
	}


}