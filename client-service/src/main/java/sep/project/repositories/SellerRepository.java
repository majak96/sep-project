package sep.project.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import sep.project.model.Seller;

public interface SellerRepository extends JpaRepository<Seller, Long>, JpaSpecificationExecutor<Seller> {

	Seller findByEmailAndDeleted(String email, Boolean deleted);
	
	Seller findByIdAndDeleted(Long id, Boolean deleted);

}
