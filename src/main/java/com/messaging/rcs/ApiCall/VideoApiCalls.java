package com.messaging.rcs.ApiCall;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.messaging.rcs.Pojos.DynamicVideoPojo.DynamicVideoRoot;
import com.messaging.rcs.Pojos.RetriveVpojo.RetriveVideoPojo;
import com.messaging.rcs.Pojos.VoiceListPojo.VoiseList;
import com.messaging.rcs.utility.ApplicationProperties;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Component
public class VideoApiCalls {
	@Autowired
	RestTemplate restTemplate;

	@Autowired
	ApplicationProperties properties;

	Logger logger = LoggerFactory.getLogger(com.messaging.rcs.ApiCall.VideoApiCalls.class);

	public String getVideoList(String page, String limit) {
		this.logger.info("********************** To Get Video List **********************");
		String urlString = ((String) this.properties.getUrl().get("GetVideoList")).replace("[page]", page)
				.replace("[limit]", limit);
		try {
			HttpHeaders headers = new HttpHeaders();

			for (Map.Entry<String, String> entry : this.properties.getHeaders().entrySet())
				headers.set(entry.getKey(), entry.getValue());
			this.logger.info("Request header: " + headers.toSingleValueMap());
			this.logger.info("Request Url: " + urlString);
			HttpEntity<Object> httpEntity = new HttpEntity((MultiValueMap) headers);
			ResponseEntity<String> responseString = this.restTemplate.exchange(urlString, HttpMethod.GET, httpEntity,
					String.class, new Object[0]);
			this.logger.info("Response status: " + responseString.getStatusCode());
			System.out.println(
					"**************************************************************************************************************************************");
			return (String) responseString.getBody();
		} catch (HttpClientErrorException | org.springframework.web.client.HttpServerErrorException e) {
			String errorDescription = "Getting Exception During Api Call  :  !! " + urlString + " with StatusCode : "
					+ e.getStatusCode() + " And Error Message: " + e.getMessage().replace("\"", "");
			this.logger.error(errorDescription);
			SlackAlert(e.getMessage().replace('"', ' ').replace("/", "//"), errorDescription);
			System.out.println(
					"**************************************************************************************************************************************");
			return null;
		} catch (Exception e) {
			String errorDescription = "Getting Exception During Api Call  :  !! " + urlString + " "
					+ e.getMessage().replace("\"", "");
			this.logger.error(errorDescription);
			SlackAlert(e.getMessage().replace("\"", "").replace("/", "//"), errorDescription);
			System.out.println(
					"**************************************************************************************************************************************");
			return null;
		}
	}

	public String RenderVideoList(String videoId, String userId) {
		this.logger
				.info("********************** To Render Video List  for userId " + userId + " **********************");
		String urlString = ((String) this.properties.getUrl().get("RenderVideoList")).replace("[videoId]", videoId);
		try {
			HttpHeaders headers = new HttpHeaders();
			for (Map.Entry<String, String> entry : this.properties.getHeaders().entrySet())
				headers.set(entry.getKey(), entry.getValue());
			this.logger.info("Request header: " + headers.toSingleValueMap());
			this.logger.info("Request Url: " + urlString);
			HttpEntity<Object> httpEntity = new HttpEntity((MultiValueMap) headers);
			ResponseEntity<String> responseString = this.restTemplate.exchange(urlString, HttpMethod.POST, httpEntity,
					String.class, new Object[0]);
			this.logger.info("Response status: " + responseString.getStatusCode());
			System.out.println(
					"**************************************************************************************************************************************");
			return (String) responseString.getBody();
		} catch (HttpClientErrorException | org.springframework.web.client.HttpServerErrorException e) {
			String errorDescription = "Getting Exception During Api Call  :  !! " + urlString + " with StatusCode : "
					+ e.getStatusCode() + " And Error Message: " + e.getMessage().replace("\"", "");
			this.logger.error(errorDescription);
			SlackAlert(e.getMessage().replace('"', ' ').replace("/", "//"), errorDescription);
			System.out.println(
					"**************************************************************************************************************************************");
			return null;
		} catch (Exception e) {
			String errorDescription = "Getting Exception During Api Call  :  !! " + urlString + " "
					+ e.getMessage().replace("\"", "");
			this.logger.error(errorDescription);
			SlackAlert(e.getMessage().replace("\"", "").replace("/", "//"), errorDescription);
			System.out.println(
					"**************************************************************************************************************************************");
			return null;
		}
	}

	public String CreateVideo(String createVideoPojo) {
		this.logger.info("********************** To Create Video **********************");
		String urlString = (String) this.properties.getUrl().get("CreateVideoList");
		try {
			HttpHeaders headers = new HttpHeaders();
			for (Map.Entry<String, String> entry : this.properties.getHeaders().entrySet())
				headers.set(entry.getKey(), entry.getValue());
			this.logger.info("Request header: " + headers.toSingleValueMap());
			this.logger.info("Request Url: " + urlString);
			ObjectMapper objectMapper = new ObjectMapper();
			HttpEntity<Object> httpEntity = new HttpEntity(createVideoPojo, (MultiValueMap) headers);
			ResponseEntity<String> responseString = this.restTemplate.exchange(urlString, HttpMethod.POST, httpEntity,
					String.class, new Object[0]);
			this.logger.info("Response status: " + responseString.getStatusCode());
			System.out.println(
					"**************************************************************************************************************************************");
			return (String) responseString.getBody();
		} catch (HttpClientErrorException | org.springframework.web.client.HttpServerErrorException e) {
			String errorDescription = "Getting Exception During Api Call  :  !! " + urlString + " with StatusCode : "
					+ e.getStatusCode() + " And Error Message: " + e.getMessage().replace("\"", "");
			this.logger.error(errorDescription);
			SlackAlert(e.getMessage().replace('"', ' ').replace("/", "//"), errorDescription);
			System.out.println(
					"**************************************************************************************************************************************");
			return errorDescription;
		} catch (Exception e) {
			String errorDescription = "Getting Exception During Api Call  :  !! " + urlString + " "
					+ e.getMessage().replace("\"", "");
			this.logger.error(errorDescription);
			SlackAlert(e.getMessage().replace("\"", "").replace("/", "//"), errorDescription);
			System.out.println(
					"**************************************************************************************************************************************");
			return errorDescription;
		}
	}

	public ResponseEntity<?> dynamicVideoCreation(String videoId, DynamicVideoRoot dynamicVideoRoot)
			throws JsonProcessingException {
		this.logger.info("********************** To Create Dynamic Video  **********************");
		String urlString = ((String) this.properties.getUrl().get("DynamicVideo")).replace("[videoId]", videoId);
		try {
			HttpHeaders headers = new HttpHeaders();
			for (Map.Entry<String, String> entry : this.properties.getHeaders().entrySet())
				headers.set(entry.getKey(), entry.getValue());
			this.logger.info("Request header: " + headers.toSingleValueMap());
			this.logger.info("Request Url: " + urlString);
			ObjectMapper objectMapper = new ObjectMapper();
			String videoRequestJsonString = objectMapper.writeValueAsString(dynamicVideoRoot);
			this.logger.info("videoRequestJsonString: " + videoRequestJsonString);
			HttpEntity<Object> httpEntity = new HttpEntity(videoRequestJsonString, (MultiValueMap) headers);
			ResponseEntity<String> responseString = this.restTemplate.exchange(urlString, HttpMethod.POST, httpEntity,
					String.class, new Object[0]);
			this.logger.info("Response status: " + responseString.getStatusCode());
			System.out.println(
					"**************************************************************************************************************************************");
			return responseString;
		} catch (HttpClientErrorException | org.springframework.web.client.HttpServerErrorException e) {
			String errorDescription = "Getting Exception During Api Call  :  !! " + urlString + " with StatusCode : "
					+ e.getStatusCode() + " And Error Message: " + e.getMessage().replace("\"", "");
			this.logger.error(errorDescription);
			SlackAlert(e.getMessage().replace('"', ' ').replace("/", "//"), errorDescription);
			System.out.println(
					"**************************************************************************************************************************************");
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			String errorDescription = "Getting Exception During Api Call  :  !! " + urlString + " "
					+ e.getMessage().replace("\"", "");
			this.logger.error(errorDescription);
			SlackAlert(e.getMessage().replace("\"", "").replace("/", "//"), errorDescription);
			System.out.println(
					"**************************************************************************************************************************************");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}

	public ResponseEntity<?> retriveVideo(String videoId) {
		this.logger.info("********************** To Retrive Video List  **********************");
		String urlString = ((String) this.properties.getUrl().get("RetriveVideoList")).replace("[videoId]", videoId);
		try {
			HttpHeaders headers = new HttpHeaders();
			for (Map.Entry<String, String> entry : this.properties.getHeaders().entrySet())
				headers.set(entry.getKey(), entry.getValue());
			this.logger.info("Request header: " + headers.toSingleValueMap());
			this.logger.info("Request Url: " + urlString);
			HttpEntity<Object> httpEntity = new HttpEntity((MultiValueMap) headers);
			ResponseEntity<RetriveVideoPojo> responseString = this.restTemplate.exchange(urlString, HttpMethod.GET,
					httpEntity, RetriveVideoPojo.class, new Object[0]);
			this.logger.info("Response status: " + responseString.getStatusCode());
			System.out.println(
					"**************************************************************************************************************************************");
			return responseString;
		} catch (HttpClientErrorException | org.springframework.web.client.HttpServerErrorException e) {
			String errorDescription = "Getting Exception During Api Call  :  !! " + urlString + " with StatusCode : "
					+ e.getStatusCode() + " And Error Message: " + e.getMessage().replace("\"", "");
			this.logger.error(errorDescription);
			SlackAlert(e.getMessage().replace('"', ' ').replace("/", "//"), errorDescription);
			System.out.println(
					"**************************************************************************************************************************************");
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			String errorDescription = "Getting Exception During Api Call  :  !! " + urlString + " "
					+ e.getMessage().replace("\"", "");
			this.logger.error(errorDescription);
			SlackAlert(e.getMessage().replace("\"", "").replace("/", "//"), errorDescription);
			System.out.println(
					"**************************************************************************************************************************************");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}

	public ResponseEntity<String> deleteVideoList(String videoId) {
		this.logger.info("********************** To Delete Video, VideoID= " + videoId + "  **********************");
		String urlString = ((String) this.properties.getUrl().get("DeleteVideo")).replace("[videoId]", videoId);
		try {
			HttpHeaders headers = new HttpHeaders();
			for (Map.Entry<String, String> entry : this.properties.getHeaders().entrySet())
				headers.set(entry.getKey(), entry.getValue());
			this.logger.info("Request header: " + headers.toSingleValueMap());
			this.logger.info("Request Url: " + urlString);
			HttpEntity<Object> httpEntity = new HttpEntity((MultiValueMap) headers);
			ResponseEntity<String> responseString = this.restTemplate.exchange(urlString, HttpMethod.DELETE, httpEntity,
					String.class, new Object[0]);
			this.logger.info("Response status: " + responseString.getStatusCode());
			this.logger.info("Response body: " + (String) responseString.getBody());
			System.out.println(
					"**************************************************************************************************************************************");
			return responseString;
		} catch (HttpClientErrorException | org.springframework.web.client.HttpServerErrorException e) {
			String errorDescription = "Getting Exception During Api Call  :  !! " + urlString + " with StatusCode : "
					+ e.getStatusCode() + " And Error Message: " + e.getMessage().replace("\"", "");
			this.logger.error(errorDescription);
			SlackAlert(e.getMessage().replace('"', ' ').replace("/", "//"), errorDescription);
			System.out.println(
					"**************************************************************************************************************************************");
			return null;
		} catch (Exception e) {
			String errorDescription = "Getting Exception During Api Call  :  !! " + urlString + " "
					+ e.getMessage().replace("\"", "");
			this.logger.error(errorDescription);
			SlackAlert(e.getMessage().replace("\"", "").replace("/", "//"), errorDescription);
			System.out.println(
					"**************************************************************************************************************************************");
			return null;
		}
	}

	public List<VoiseList> getVoiceList(String userId) throws Exception {
		this.logger.info("********************** To Get Voice List  for userId " + userId + " **********************");
		String urlString = (String) this.properties.getUrl().get("GetVoiceList");
		try {
			Object object = new Object();
			HttpHeaders headers = new HttpHeaders();
			for (Map.Entry<String, String> entry : this.properties.getHeaders().entrySet())
				headers.set(entry.getKey(), entry.getValue());
			this.logger.info("Request header: " + headers.toSingleValueMap());
			this.logger.info("Request Url: " + urlString);
			HttpEntity<Object> httpEntity = new HttpEntity((MultiValueMap) headers);
			ResponseEntity<List<VoiseList>> responseString = this.restTemplate.exchange(urlString, HttpMethod.GET,
					httpEntity, new ParameterizedTypeReference<List<VoiseList>>() {});
			this.logger.info("Response status: " + responseString.getStatusCode());
			System.out.println(
					"**************************************************************************************************************************************");
			return (List<VoiseList>) responseString.getBody();
		} catch (HttpClientErrorException | org.springframework.web.client.HttpServerErrorException e) {
			String errorDescription = "Getting Exception During Api Call  :  !! " + urlString + " with StatusCode : "
					+ e.getStatusCode() + " And Error Message: " + e.getMessage().replace("\"", "");
			this.logger.error(errorDescription);
			SlackAlert(e.getMessage().replace('"', ' ').replace("/", "//"), errorDescription);
			System.out.println(
					"**************************************************************************************************************************************");
			return null;
		} catch (Exception e) {
			String errorDescription = "Getting Exception During Api Call  :  !! " + urlString + " "
					+ e.getMessage().replace("\"", "");
			this.logger.error(errorDescription);
			SlackAlert(e.getMessage().replace("\"", "").replace("/", "//"), errorDescription);
			System.out.println(
					"**************************************************************************************************************************************");
			return null;
		}
	}

	public String getAvatarList(String userId) throws Exception {
		this.logger.info("********************** To Get Avatar List  for userId" + userId + " **********************");
		String urlString = (String) this.properties.getUrl().get("GetAvatarList");
		try {
			HttpHeaders headers = new HttpHeaders();
			for (Map.Entry<String, String> entry : this.properties.getHeaders().entrySet())
				headers.set(entry.getKey(), entry.getValue());
			this.logger.info("Request header: " + headers.toSingleValueMap());
			this.logger.info("Request Url: " + urlString);
			HttpEntity<Object> httpEntity = new HttpEntity((MultiValueMap) headers);
			ResponseEntity<String> responseString = this.restTemplate.exchange(urlString, HttpMethod.GET, httpEntity,
					String.class, new Object[0]);
			this.logger.info("Response status: " + responseString.getStatusCode());
			System.out.println(
					"**************************************************************************************************************************************");
			return (String) responseString.getBody();
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			String errorDescription = "Getting Exception During Api Call  :  !! " + urlString + " with StatusCode : "
					+ e.getStatusCode() + " And Error Message: " + e.getMessage().replace("\"", "");
			this.logger.error(errorDescription);
			SlackAlert(e.getMessage().replace('"', ' ').replace("/", "//"), errorDescription);
			System.out.println(
					"**************************************************************************************************************************************");
			return null;
		} catch (Exception e) {
			String errorDescription = "Getting Exception During Api Call  :  !! " + urlString + " "
					+ e.getMessage().replace("\"", "");
			this.logger.error(errorDescription);
			SlackAlert(e.getMessage().replace("\"", "").replace("/", "//"), errorDescription);
			System.out.println(
					"**************************************************************************************************************************************");
			return null;
		}
	}

	public String changeRatios(String imageHttpUrl, String targetWidth, String targetHeight) {
		String url = "http://fuat.flash49.com/rcsmsg/template/resizeImage?imageHttpUrl=" + imageHttpUrl
				+ "&targetWidth=" + targetWidth + "&targetHeight=" + targetHeight;
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization",
				"Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJGb25hZGEiLCJzY29wZXMiOlt7ImF1dGhvcml0eSI6IlJPTEVfQURNSU4ifV0sImlzcyI6Imh0dHA6Ly90aW1lc2ludGVybmV0LmluIiwiaWF0IjoxNzE4ODgwMTUyLCJleHAiOjE3MTg4OTgxNTJ9.AJIukucnN5H655FHxQlA_nZu81prlpPbzlFqV-btOzI");
		headers.set("content-type", "application/json");
		this.logger.info("Request Header : " + headers.toSingleValueMap());
		this.logger.info("Request url: " + url);
		try {
			HttpEntity<Object> httpEntity = new HttpEntity((MultiValueMap) headers);
			ResponseEntity<String> responseEntity = this.restTemplate.exchange(url, HttpMethod.GET, httpEntity,
					String.class, new Object[0]);
			this.logger.info("Response status: " + responseEntity.getStatusCodeValue());
			System.out.println(
					"**************************************************************************************************************************************");
			return (String) responseEntity.getBody();
		} catch (HttpClientErrorException | org.springframework.web.client.HttpServerErrorException e) {
			e.printStackTrace();
			this.logger.info(e.getMessage().replace("\"", "").replace("/", "//"));
			String errorDescription = "Getting Exception During Api Call  :  !! " + url + " with StatusCode : "
					+ e.getStatusCode() + " And Error Message: " + e.getMessage().replace("\"", "");
			this.logger.error(errorDescription);
			SlackAlert(e.getMessage().replace('"', ' ').replace("/", "//"), errorDescription);
			System.out.println(
					"**************************************************************************************************************************************");
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			this.logger.info(e.getMessage().replace("\"", "").replace("/", "//"));
			this.logger.error("Getting Exception During First Api Call :  !! " + e.getMessage().replace("\"", ""));
			SlackAlert(e.getMessage().replace("\"", "").replace("/", "//"), "Getting Exception During First Api Call");
			System.out.println(
					"**************************************************************************************************************************************");
			return null;
		}
	}

	public String SlackAlert(String error, String description) {
		this.logger.info("********************** Generate Slack Alert  **********************");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String errorTime = sdf.format(Integer.valueOf((new Date()).getDate()));
		String urlString = ((String) this.properties.getUrl().get("SlackAlertApi")).replace("[error]", error)
				.replace("[time]", errorTime).replace("[description]", description);
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.set("content-type", "application/json");
			this.logger.info("Request header: " + headers.toSingleValueMap());
			this.logger.info("Request Url: " + urlString);
			HttpEntity<Object> httpEntity = new HttpEntity((MultiValueMap) headers);
			ResponseEntity<String> responseString = this.restTemplate.exchange(urlString, HttpMethod.GET, httpEntity,
					String.class, new Object[0]);
			this.logger.info("Response Header: " + responseString.getHeaders());
			this.logger.info("Response status: " + responseString.getStatusCode());
			System.out.println(
					"**************************************************************************************************************************************");
			return (String) responseString.getBody();
		} catch (HttpClientErrorException | org.springframework.web.client.HttpServerErrorException e) {
			String errorDescription = "Getting Exception During Api Call  :  !! " + urlString + " with StatusCode : "
					+ e.getStatusCode() + " And Error Message: " + e.getMessage().replace("\"", "");
			this.logger.error(errorDescription);
			this.logger.error("Failed to Generate Alert for : Error " + error + " Description :" + description);
			System.out.println(
					"**************************************************************************************************************************************");
			return null;
		} catch (Exception e) {
			String errorDescription = "Getting Exception During Api Call  :  !! " + urlString + " "
					+ e.getMessage().replace("\"", "");
			this.logger.error(errorDescription);
			this.logger.error("Failed to Generate Alert for : Error " + error + " Description :" + description);
			System.out.println(
					"**************************************************************************************************************************************");
			return null;
		}
	}
}
