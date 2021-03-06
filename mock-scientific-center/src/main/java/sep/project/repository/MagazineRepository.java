package sep.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sep.project.model.Magazine;

@Repository
public interface MagazineRepository extends JpaRepository<Magazine, Long> {

	Magazine findByName(String name);
	
	Magazine findByEmail(String email);

}
