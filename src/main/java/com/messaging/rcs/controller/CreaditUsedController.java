package com.messaging.rcs.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.messaging.rcs.domain.CreditUsedEntity;
import com.messaging.rcs.domain.UserEntity;
import com.messaging.rcs.email.repository.CreaditUsedRepository;
import com.messaging.rcs.repository.UserRepository;
import com.messaging.rcs.util.DateUtils;

@RestController
@RequestMapping(value = { "/api/v1/rcsmessaging/credit" }, produces = APPLICATION_JSON_VALUE)
public class CreaditUsedController {
	@Autowired
	private CreaditUsedRepository creaditUsedRepository;

	@Autowired
	private UserRepository userRepository;

	@PostMapping(value = { "/createCredit" })
	public ResponseEntity<?> createCredit(@RequestBody CreditUsedEntity creditUsedEntity) {
		HashMap<String, Object> result = new HashMap<>();
		try {

			if (creditUsedEntity.getAcctCredit() == 0) {
				result.put("status", HttpStatus.NOT_FOUND);
				result.put("msg", "Credit Cannot Be Empty");
				return new ResponseEntity<>(result, HttpStatus.OK);

			}
			creditUsedEntity.setCreatedDate(DateUtils.getStringDateInTimeZone(new Date()));
			creditUsedEntity.setUpdateDate(DateUtils.getStringDateInTimeZone(new Date()));

			creditUsedEntity = creaditUsedRepository.save(creditUsedEntity);
			if (creditUsedEntity.getCrdId() != 0) {
				UserEntity userEntity = null;
				userEntity = userRepository.findByUserId(Long.valueOf(creditUsedEntity.getUserId()));
				if (creditUsedEntity.getMsgType().equalsIgnoreCase("RCS")) {
					userEntity.setCreditBalance(creditUsedEntity.getAcctCredit());
				} else if (creditUsedEntity.getMsgType().equalsIgnoreCase("SMS")) {
					userEntity.setSmsCreditBalance(creditUsedEntity.getAcctCredit());
				} else {
					userEntity.setWhatsAppCreditBalance(creditUsedEntity.getAcctCredit());
				}
				userRepository.save(userEntity);
				result.put("status", HttpStatus.OK);
				result.put("msg", "Success");
			} else {
				result.put("status", HttpStatus.NOT_FOUND);
				result.put("msg", "Not Found");
			}
		} catch (Exception e) {
			result.put("status", HttpStatus.INTERNAL_SERVER_ERROR);
			result.put("msg", "Internal Server Error ::" + e.getMessage());
		}
		return new ResponseEntity<>(result, HttpStatus.OK);

	}

	@GetMapping(value = { "/findAllCredit" })
	public ResponseEntity<?> findAllCredit(@RequestParam("userId") String userId) {
		HashMap<String, Object> result = new HashMap<>();
		try {

			List<CreditUsedEntity> creditUsedEntity = null;
			creditUsedEntity = creaditUsedRepository.findAllByUserId(userId);
			if (Objects.nonNull(creditUsedEntity)) {
				result.put("status", HttpStatus.OK);
				result.put("msg", "Success");
				result.put("data", creditUsedEntity);
			} else {
				result.put("status", HttpStatus.NOT_FOUND);
				result.put("msg", "Not Found");
			}
		} catch (Exception e) {
			result.put("status", HttpStatus.INTERNAL_SERVER_ERROR);
			result.put("msg", "Internal Server Error ::" + e.getMessage());
		}
		return new ResponseEntity<>(result, HttpStatus.OK);

	}

	@GetMapping(value = { "/findAllCreditForAdmin" })
	public ResponseEntity<?> findAllCreditForAdmin() {
		HashMap<String, Object> result = new HashMap<>();
		try {

			List<CreditUsedEntity> creditUsedEntity = null;
			creditUsedEntity = creaditUsedRepository.findAll();
			if (Objects.nonNull(creditUsedEntity)) {
				for (CreditUsedEntity crd : creditUsedEntity) {
					crd.setUserId(userRepository.findById(Long.valueOf(crd.getUserId())).get().getUserName());
				}
				result.put("status", HttpStatus.OK);
				result.put("msg", "Success");
				result.put("data", creditUsedEntity);
			} else {
				result.put("status", HttpStatus.NOT_FOUND);
				result.put("msg", "Not Found");
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("status", HttpStatus.INTERNAL_SERVER_ERROR);
			result.put("msg", "Internal Server Error ::" + e.getMessage());
		}
		return new ResponseEntity<>(result, HttpStatus.OK);

	}
}
