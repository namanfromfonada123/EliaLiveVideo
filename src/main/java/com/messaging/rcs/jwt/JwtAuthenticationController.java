package com.messaging.rcs.jwt;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.messaging.rcs.domain.UserEntity;
import com.messaging.rcs.repository.UserRepository;
import com.messaging.rcs.util.PasswordUtil;
import com.messaging.rcs.util.SendEmail;

@RestController
@RequestMapping("/api/v1/rcsmessaging/auth")
@CrossOrigin("*")
public class JwtAuthenticationController {
	private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationController.class);

	private static final String STATUS = "Status";
	private static final String MESSAGE = "message";
	public static final String USER = "USER";
	private PasswordUtil pwdUtil = new PasswordUtil();

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private SendEmail sendEmail;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private com.messaging.rcs.repository.UserRoleRepo userRoleRepo;
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private CustomUserDetailService userDetailService;

	@PostMapping("/generateToken")
	public ApiResponse<AuthToken> generate_token(@RequestBody JwtRequest loginUser) throws Exception {
		LOGGER.info("generate-token:=" + loginUser.getUsername());

		final CustomUserDetails userDetails = (CustomUserDetails) userDetailService
				.loadUserByUsername(loginUser.getUsername());
		if (!userDetails.isAccountNonExpired()) {
			throw new RuntimeException("User Account Expired due to Inactivity");
		}

		try {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginUser.getUsername(), loginUser.getPassword()));
		} catch (BadCredentialsException e) {
			throw new RuntimeException("Incorrect username or password", e);
		}

		LOGGER.info("authenticate=");
		Optional<UserEntity> user = userRepository.findByUserName(loginUser.getUsername());
		LOGGER.info("generate-token:user=" + user.get().getUserName());

		final String token = jwtTokenUtil.generateToken(user.get());

		LOGGER.info("generate-token::Token=" + token);

		ApiResponse<AuthToken> response = new ApiResponse<>(200, userRoleRepo.findById(user.get().getRole_id()).get().getName(), user.get().getUserType(),
				user.get().getPhone(), user.get().getUserId(), user.get().getDailyUsageLimit(), user.get().getBotId(),
				new AuthToken(token, user.get().getUserName()));
		LOGGER.info("generate-token::=> " + response);
		return response;
	}

	@GetMapping(value = "/forgot-password", produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<HashMap<String, Object>> forgotPassword(@RequestParam("userName") String userName,
			@RequestParam("mail") String mail) {
		UserEntity user = null;
		HashMap<String, Object> result = new HashMap<>();

		user = userRepository.findByUserNameAndEmail(userName, mail);
		if (Objects.isNull(user)) {
			result.put(STATUS, HttpStatus.NOT_FOUND.toString());
			result.put(MESSAGE, "User Not Found");
			return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
		} else {
			String pass = pwdUtil.generatePassword(8);
			System.out.println("System Generated Forgot Password Password::=>" + pass);
			user.setUserPassword(bCryptPasswordEncoder.encode(pass));
			sendEmail.sendEmail(user, pass);
			userRepository.save(user);
			result.put(STATUS, HttpStatus.OK.toString());
			result.put(MESSAGE, "Password Sent To Given Mail.");
			return new ResponseEntity<>(result, HttpStatus.OK);
		}
	}
}
