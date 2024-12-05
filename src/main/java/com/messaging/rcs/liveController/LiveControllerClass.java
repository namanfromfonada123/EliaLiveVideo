package com.messaging.rcs.liveController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.messaging.rcs.ApiCall.VideoApiCalls;
import com.messaging.rcs.Pojos.ResponsePojo;
import com.messaging.rcs.Pojos.RetriveVpojo.RetriveVideoPojo;
import com.messaging.rcs.Pojos.VoiceListPojo.VoiseList;
import com.messaging.rcs.jwt.JwtTokenUtil;
import com.messaging.rcs.service.LiveVideoService;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({ "/live/Video" })
public class LiveControllerClass {
	Logger logger = LoggerFactory.getLogger(com.messaging.rcs.liveController.LiveControllerClass.class);

	@Autowired
	VideoApiCalls vApiCalls;

	@Autowired
	LiveVideoService lVideoService;

	@Autowired
	JwtTokenUtil jwtTokenUtil;

	@GetMapping({ "/getVideoList" })
	public ResponseEntity<?> getVideoList(HttpServletRequest request, @RequestParam String page,
			@RequestParam String limit) {
		ResponsePojo responsePojo = new ResponsePojo();
		try {
			responsePojo.setData(this.lVideoService.getVideoListService(limit, page,
					this.jwtTokenUtil.getUsernameFromToken(getToken(request.getHeader("Authorization")))));
			responsePojo.setMessage("Getting Video List Successfully!!");
			responsePojo.setStatus(HttpStatus.OK.value());
			return ResponseEntity.ok(responsePojo);
		} catch (Exception e) {
			e.printStackTrace();
			responsePojo.setData(null);
			responsePojo.setMessage("Something went wrong!!");
			responsePojo.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			return ResponseEntity.internalServerError().body(responsePojo);
		}
	}

	@PostMapping({ "/createVideo" })
	public ResponseEntity<?> createVideo(HttpServletRequest request, @RequestBody String CreateVideoPojo) {
		ResponsePojo responsePojo = new ResponsePojo();
		try {
			responsePojo.setData(this.lVideoService.CreateVideo(CreateVideoPojo,
					this.jwtTokenUtil.getUsernameFromToken(getToken(request.getHeader("Authorization")))));
			responsePojo.setMessage("Video Created Successfully!!");
			responsePojo.setStatus(HttpStatus.OK.value());
			return ResponseEntity.ok(responsePojo);
		} catch (Exception e) {
			e.printStackTrace();
			responsePojo.setData(null);
			responsePojo.setMessage("Something went wrong!!");
			responsePojo.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			return ResponseEntity.internalServerError().body(responsePojo);
		}
	}

	@GetMapping({ "/retriveVideo" })
	public ResponseEntity<?> retriveVideo(@RequestParam String videoId, @RequestParam String username) {
		ResponsePojo responsePojo = new ResponsePojo();
		try {
			ResponseEntity<?> responseEntity = this.lVideoService.retriveVideoPojoService(videoId, username);
			if (responseEntity.getStatusCodeValue() == 200) {
				RetriveVideoPojo retriveVideoPojo = (RetriveVideoPojo) responseEntity.getBody();
				if (retriveVideoPojo.getUrl() != null && retriveVideoPojo.getThumbnail() != null) {
					String str = (new ObjectMapper()).writeValueAsString(retriveVideoPojo);
					responsePojo.setData(str);
					responsePojo.setMessage("Retrive Video Successfully!!");
					responsePojo.setStatus(HttpStatus.OK.value());
					return ResponseEntity.ok(responsePojo);
				}
				String retriveVideoData = (new ObjectMapper()).writeValueAsString(retriveVideoPojo);
				responsePojo.setData(retriveVideoData);
				responsePojo.setMessage("Unable to retrive video url !!");
				responsePojo.setStatus(HttpStatus.BAD_REQUEST.value());
				return ResponseEntity.ok(responsePojo);
			}
			responsePojo.setData(null);
			responsePojo.setMessage(responseEntity.getBody().toString());
			responsePojo.setStatus(responseEntity.getStatusCodeValue());
			return ResponseEntity.status(responseEntity.getStatusCode()).body(responsePojo);
		} catch (Exception e) {
			this.logger.error(e.getLocalizedMessage());
			e.printStackTrace();
			responsePojo.setData(null);
			responsePojo.setMessage("Something went wrong!!");
			responsePojo.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			return ResponseEntity.internalServerError().body(responsePojo);
		}
	}

	@GetMapping({ "/renderVideo" })
	public ResponseEntity<?> renderVideo(@RequestParam String videoId, HttpServletRequest request) {
		ResponsePojo responsePojo = new ResponsePojo();
		try {
			responsePojo.setData(this.vApiCalls.RenderVideoList(videoId,
					this.jwtTokenUtil.getUsernameFromToken(getToken(request.getHeader("Authorization")))));
			responsePojo.setMessage("Render Video Successfully!!");
			responsePojo.setStatus(HttpStatus.OK.value());
			return ResponseEntity.ok(responsePojo);
		} catch (Exception e) {
			e.printStackTrace();
			responsePojo.setData(null);
			responsePojo.setMessage("Something went wrong!!");
			responsePojo.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			return ResponseEntity.internalServerError().body(responsePojo);
		}
	}

	@GetMapping({ "/GetVoiceList" })
	public List<VoiseList> GetVoiceList(HttpServletRequest request) {
		try {
			return this.lVideoService.getVoiceListService(
					this.jwtTokenUtil.getUsernameFromToken(getToken(request.getHeader("Authorization"))));
		} catch (Exception e) {
			this.logger.error(e.getLocalizedMessage());
			e.printStackTrace();
			return null;
		}
	}

	@GetMapping({ "/GetAvatarList" })
	public String GetAvatarList(HttpServletRequest request) {
		try {
			return this.lVideoService.getAvatarList(
					this.jwtTokenUtil.getUsernameFromToken(getToken(request.getHeader("Authorization"))));
		} catch (Exception e) {
			this.logger.error(e.getLocalizedMessage());
			e.printStackTrace();
			return null;
		}
	}

	@PostMapping({ "/DynamicVideo" })
	public ResponseEntity<?> CreateDynamicVideo(@RequestBody Map<String, String> templateData,
			@RequestParam String videoId, @RequestParam String username) {
		ResponsePojo responsePojo = new ResponsePojo();
		try {
			ResponseEntity<?> responseEntity = this.lVideoService.dynamicVideo(videoId, templateData, username);
			System.out.println("responseEntity  : " + responseEntity.getStatusCode());
			if (responseEntity.getStatusCodeValue() == 200) {
				responsePojo.setData(responseEntity.getBody().toString());
				responsePojo.setMessage("Getting SuccessFull Response !!");
				responsePojo.setStatus(responseEntity.getStatusCodeValue());
				return ResponseEntity.status(responseEntity.getStatusCode()).body(responsePojo);
			}
			responsePojo.setData(null);
			responsePojo.setMessage(responseEntity.getBody().toString());
			responsePojo.setStatus(responseEntity.getStatusCodeValue());
			return ResponseEntity.status(responseEntity.getStatusCode()).body(responsePojo);
		} catch (Exception e) {
			responsePojo.setData(null);
			responsePojo.setMessage("Something went wrong!!");
			responsePojo.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			return ResponseEntity.internalServerError().body(responsePojo);
		}
	}

	@PostMapping({ "/Webhook" })
	public String webhook(@RequestBody String webhookbody) {
		this.logger.info("webhook : " + webhookbody);
		return webhookbody;
	}

	public String getToken(String authorisation) {
		String tokenString = "";
		if (!authorisation.isEmpty())
			tokenString = authorisation.substring(7);
		return tokenString;
	}

	@GetMapping({ "/DeleteVideo" })
	public ResponseEntity<?> DeleteVideo(HttpServletRequest request, @RequestParam String videoId) {
		ResponsePojo responsePojo = new ResponsePojo();
		try {
			ResponseEntity<String> deleteResponse = this.lVideoService.deleteVideoService(videoId,
					this.jwtTokenUtil.getUsernameFromToken(getToken(request.getHeader("Authorization"))));
			if (deleteResponse.getStatusCodeValue() == 200) {
				responsePojo.setData(deleteResponse.getBody());
				responsePojo.setMessage("Video deleted Successfully !! ");
				responsePojo.setStatus(deleteResponse.getStatusCode().value());
				return ResponseEntity.ok(responsePojo);
			}
			responsePojo.setData(null);
			responsePojo.setMessage((String) deleteResponse.getBody());
			responsePojo.setStatus(deleteResponse.getStatusCode().value());
			return ResponseEntity.badRequest().body(responsePojo);
		} catch (Exception e) {
			responsePojo.setData(null);
			responsePojo.setMessage("Something went wrong!!");
			responsePojo.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			return ResponseEntity.internalServerError().body(responsePojo);
		}
	}
}
