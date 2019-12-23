package sep.project.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sep.project.DTOs.BankResponseDTO;
import sep.project.DTOs.CompletedDTO;
import sep.project.DTOs.PayRequestDTO;
import sep.project.DTOs.RegisterSellerDTO;
import sep.project.services.BankService;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 3600)
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class BankController {

	@Autowired
	BankService bankService;

	@PostMapping(value = "/initiatePayment")
	public ResponseEntity<BankResponseDTO> initiatePayment(@RequestBody PayRequestDTO request) {

		ResponseEntity<BankResponseDTO> ret = bankService.initiatePayment(request);
		if (ret != null) {
			return ret;
		} else {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

	}

	@PostMapping(value = "/registerSeller")
	public ResponseEntity<Boolean> registerSeller(@RequestBody RegisterSellerDTO registerSellerDTO) {

		Boolean ret = bankService.registerSeller(registerSellerDTO);
		if (ret) {
			return new ResponseEntity<>(HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

	}

	@PostMapping(value = "/finishPayment")
	public ResponseEntity finishPayment(@RequestBody CompletedDTO completedDTO) {

		return bankService.finishPayment(completedDTO);

	}

}
