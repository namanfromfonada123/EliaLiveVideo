package com.messaging.rcs.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.messaging.rcs.domain.TemplateFileDB;
import com.messaging.rcs.email.model.ResponseFile;
import com.messaging.rcs.email.model.TemplateModel;
import com.messaging.rcs.email.repository.TemplateFileDbRepository;
import com.messaging.rcs.model.Template;
import com.messaging.rcs.repository.RcsMsgTypeRepository;
import com.messaging.rcs.repository.TemplateRepository;
import com.messaging.rcs.repository.UserRepository;
import com.messaging.rcs.schedular.BotTokenAPIService;
import com.messaging.rcs.schedular.TokenPojo;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@RestController
@RequestMapping(value = { "/api/v1/rcsmessaging/template" }, produces = APPLICATION_JSON_VALUE)
public class TemplateController {

	@Value("${template.basic.token}")
	public String templateBasicToken;

	private static final Logger LOGGER = Logger.getLogger(TemplateController.class.getName());
	private static final String STATUS = "status";
	private BeanUtilsBean beanUtils = new BeanUtilsBean();

	private static final String MESSAGE = "message";
	@Value("${template.path}")
	private String templatePath;

	@Value("${approved.template.status.api}")
	private String approvedTempalteStatusApi;

	@Autowired
	private TemplateRepository templateRepository;
	@Autowired
	private TemplateFileDbRepository templateFileDbRepository;
	@Autowired
	private BotTokenAPIService botTokenAPIService;

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RcsMsgTypeRepository rcsMsgTypeRepository;

	@PostMapping({ "/addTemplate" })
	public ResponseEntity<HashMap<String, Object>> createTemplate(
			@RequestParam(value = "addTemplate") String addTemplate,
			@RequestParam(value = "files", required = false) MultipartFile[] files) throws Exception {
		
		LOGGER.info("Inside Template Controller::" + addTemplate.toString());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String date = sdf.format(new Date());
		
		String fileResponse = "";
		List<String> fileNames = new ArrayList<>();
		
		Template savedtemplate = null;
		Template template = new Template();

		TemplateModel templateModel = new ObjectMapper().readValue(addTemplate, TemplateModel.class);
		HashMap<String, Object> result = new HashMap<>();

		try {
			if (Integer.valueOf(templateModel.getRcsMsgTypeId()) == 2) {
				LOGGER.info("****** Controller Checking SMS Param ******");

				if (Objects.isNull(templateModel.getSms_content())
						|| Objects.isNull(templateModel.getSms_dlt_content_id())
						|| Objects.isNull(templateModel.getSms_dlt_principle_id())
						|| Objects.isNull(templateModel.getSms_senderId())) {
					result.put(STATUS, HttpStatus.NOT_FOUND);
					result.put(MESSAGE,
							"SMS ContentId Or DltPrincipleId Or DltContentId Or SenderId Or Content Cannot Be Empty");
					return new ResponseEntity<>(result, HttpStatus.CREATED);

				}
			}
			if (Objects.nonNull(templateRepository.findFirstByTemplateCodeOrderById(templateModel.getTemplateCode()))) {
				result.put(STATUS, HttpStatus.CREATED);
				result.put(MESSAGE, "Template Code Already Exist.");
				return new ResponseEntity<>(result, HttpStatus.CREATED);

			}
			
			
			if (templateModel != null) {
				template.setStatus(0);
				beanUtils.copyProperties(template, templateModel);

			}

			// For SMS Template
			if (Integer.valueOf(templateModel.getRcsMsgTypeId()) == 2) {
				template.setStatus(1);
				template.setApproveResponse("approved");
				template = templateRepository.save(template);

				result.put(MESSAGE, "Template Added Successfully ");
				result.put(STATUS, HttpStatus.OK);
				result.put("Template", template);
				return new ResponseEntity<>(result, HttpStatus.CREATED);
			}
			// });

			if (Integer.valueOf(templateModel.getRcsMsgTypeId()) == 1) {
				if (Objects.nonNull(files)) {
					for (MultipartFile file : files) {
						fileResponse = "And Files Saved this location ::" + templatePath;
						LOGGER.info("****** SAVING RCS FILE GIVEN PATH ::" + fileResponse + "/"
								+ file.getOriginalFilename());

						byte[] bytes = new byte[0];
						bytes = file.getBytes();
						Files.write(Paths.get(templatePath + file.getOriginalFilename()), bytes);

					}
				}


				ResponseEntity<HashMap<String, Object>> operatorOutput = sendTemplateToOperator(template, files);
				if (operatorOutput.getStatusCodeValue() == 201) {
					LOGGER.info("****** GOING ON SAVING TEMPALTE AFTER GETTING OPERATOR RESPONSE ::"
							+ operatorOutput.getBody());
					template = templateRepository.save(template);
					if (Objects.nonNull(files)) {
						for (MultipartFile file : files) {
							LOGGER.info("****** GOING ON SAVING RCS FILE IN DB ::" + fileResponse + "/"
									+ file.getOriginalFilename());
							// byte[] bytes = new byte[0];
							try {
								// bytes = file.getBytes();
								// Files.write(Paths.get(templatePath + file.getOriginalFilename()), bytes);
								TemplateFileDB fileDb = new TemplateFileDB();
								fileDb.setData(file.getBytes());
								fileDb.setName(file.getOriginalFilename());
								fileDb.setType(file.getContentType());
								fileDb.setUserId(templateModel.getTemplateUserId());
								fileDb.setCreatedDate(date);
								fileDb.setTemplateId(template.getId());
								fileNames.add(file.getOriginalFilename());
								TemplateFileDB savedTemplateFile = templateFileDbRepository.save(fileDb);

								if (savedTemplateFile != null) {
									LOGGER.info("******AFTER SAVED TEMPLATE ID IN TEMPLATE FILE DB TABLE With Template Id::"
											+ savedTemplateFile.getTemplateId());

									/*
									 * Integer updateTmpId = 0; updateTmpId =
				SSS					 * templateFileDbRepository.updateFileStorageTempalteId(template.getId(),
									 * savedTemplateFile.getId()); if (updateTmpId != 0) {
									 * LOGGER.info("****** UPDATED TEMPLATE ID IN TEMPLATE FILE DB TABLE::" +
									 * savedTemplateFile.getId());
									 * 
									 * } else { LOGGER.info(
									 * "******NOT UPDATED TEMPLATE ID IN TEMPLATE FILE DB TABLE GIVEN ID *****"); }
									 * } else {
									 * 
									 * }
									 */
								}
							} catch (IOException e) {
								e.printStackTrace();
								result.put(MESSAGE, e.getMessage());
								result.put(STATUS, HttpStatus.INTERNAL_SERVER_ERROR);
								result.put("Template", template);
								return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
							}
						}
					}
					return operatorOutput;
				} else {
					return operatorOutput;
				}
			}

			return null;
		} catch (Exception e) {
			e.printStackTrace();
			result.put(MESSAGE, e.getMessage());
			result.put(STATUS, HttpStatus.INTERNAL_SERVER_ERROR.toString());
			result.put("Template", template);
			return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping({ "/findAllTemplate" })
	public ResponseEntity<HashMap<String, Object>> findAllTemplate(@RequestParam("rcsMsgTypeId") String rcsMsgTypeId,
			@RequestParam("from") String from, @RequestParam("to") String to,
			@RequestParam("templateUserId") Long templateUserId,
			@RequestParam(value = "start", required = false) Integer start,
			@RequestParam(value = "limit", required = false) Integer limit,
			@RequestParam(value = "templateCode", required = false) String templateCode,
			@RequestParam(value = "status", required = false) String status,
			@RequestParam(value = "downloadStatus") String downloadStatus) throws Exception {
		LOGGER.info("Inside Template Controller");
		HashMap<String, Object> result = new HashMap<>();
		long templateCount = 0;
		Pageable pageable = null;
		List<Template> templateList = null;
		try {

			LOGGER.info("Template Total Size::=>" + templateCount);
			if (limit == 0 && !downloadStatus.equalsIgnoreCase("D")) {
				templateList = templateRepository.findAllByTemplateUserIdWithoutDateRange(templateUserId, rcsMsgTypeId);

			} else {
				start = start - 1;
				if (start >= 0) {
					int pageSize = limit;
					int pageNum = start != 0 ? start / pageSize : 0;

					pageable = PageRequest.of(pageNum, pageSize);
				}
				if (Objects.nonNull(templateCode) && Objects.nonNull(status)) {
					LOGGER.info("TemplaCode And Status Is Not Null.");
					templateCount = templateRepository.countByTemplateUserIdAndStatusAndTemplateCode(templateUserId,
							templateCode, status, rcsMsgTypeId);
					templateList = templateRepository.findAllByUserIdAndTemplateCodeAndStatus(pageable, templateUserId,
							templateCode, status, rcsMsgTypeId);

				}
				if (Objects.isNull(templateCode) && Objects.nonNull(status) && limit != 0) {
					LOGGER.info("Status  Is  NOT Null.");
					templateCount = templateRepository.countByTemplateUserIdAndStatus(templateUserId, status,
							rcsMsgTypeId);
					templateList = templateRepository.findAllByUserIdAndStatus(pageable, templateUserId, status,
							rcsMsgTypeId);
				}
				if (Objects.isNull(templateCode) && Objects.nonNull(status) && downloadStatus.equalsIgnoreCase("D")) {
					LOGGER.info("Status  Is  NOT Null.");
					templateCount = templateRepository.countByTemplateUserIdAndStatus(templateUserId, status,
							rcsMsgTypeId);
					templateList = templateRepository.findAllByUserIdAndStatusWithoutPageAble(templateUserId, status,
							rcsMsgTypeId);
				}
				if (Objects.nonNull(templateCode) && Objects.isNull(status)) {
					LOGGER.info("TemplateCode  Is NOT Null.");
					templateCount = templateRepository.countByTemplateUserIdAndTemplateCode(templateUserId,
							templateCode, rcsMsgTypeId);
					templateList = templateRepository.findAllByUserIdAndTemplateCode(pageable, templateUserId,
							templateCode, rcsMsgTypeId);
				}
				if (Objects.isNull(templateCode) && Objects.isNull(status)) {
					LOGGER.info("TemplaCode And Status Is Null.");
					templateCount = templateRepository.countByTemplateUserIdWithDateRange(from + " 00:00:00",
							to + " 23:59:59", templateUserId, rcsMsgTypeId);
					templateList = templateRepository.findAllByUserId(from + " 00:00:00", to + " 23:59:59", pageable,
							templateUserId, rcsMsgTypeId);
				}
			}
			if (templateList.size() > 0) {
				result.put(STATUS, HttpStatus.OK);
				result.put("template", templateList);
				result.put("totalCount", templateCount);
				result.put(MESSAGE, "Record Founded.");
				return new ResponseEntity<>(result, HttpStatus.OK);
			} else {
				result.put(STATUS, HttpStatus.NOT_FOUND);
				result.put(MESSAGE, "Record Not Founded.");

				return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			result.put(STATUS, HttpStatus.INTERNAL_SERVER_ERROR.toString());
			result.put(MESSAGE, "GOT Exception");
			e.printStackTrace();
			return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping({ "/getAllTemplateName" })
	public ResponseEntity<HashMap<String, Object>> getAllTemplateName(@RequestParam("rcsMsgTypeId") String rcsMsgTypeId,
			@RequestParam("templateUserId") Long templateUserId) throws Exception {
		LOGGER.info("Inside Template Controller");
		HashMap<String, Object> result = new HashMap<>();
		List<Template> templateList = null;
		List<HashMap<String, Object>> templateNameList = new ArrayList<>();

		if (rcsMsgTypeId.equalsIgnoreCase("0")) {
			LOGGER.info("***** Getting All Template ****** ");

			templateList = templateRepository.findListByTemplateByUserId(templateUserId);
		} else {
			templateList = templateRepository.findAllByTemplateUserIdAndStatusAndRcsMsgTypeId(templateUserId, 1,
					rcsMsgTypeId);
		}
		if (templateList.size() > 0) {
			for (Template tmp : templateList) {
				HashMap<String, Object> templateName = new HashMap<>();

				templateName.put("id", tmp.getId());
				templateName.put("templateCode", tmp.getTemplateCode());
				templateNameList.add(templateName);
			}

			result.put(STATUS, HttpStatus.OK);
			result.put("template", templateNameList);
			result.put(MESSAGE, "Record Founded.");
			return new ResponseEntity<>(result, HttpStatus.OK);
		} else {
			result.put(STATUS, HttpStatus.NOT_FOUND);
			result.put(MESSAGE, "Record Not Founded.");
			return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
		}

	}

	@GetMapping({ "/getAllTemplateNameAndIdWithDateFilter" })
	public ResponseEntity<HashMap<String, Object>> getAllTemplateNameAndIdWithDateFilter(
			@RequestParam("rcsMsgTypeId") String rcsMsgTypeId, @RequestParam("from") String from,
			@RequestParam("to") String to, @RequestParam("templateUserId") Long templateUserId) throws Exception {
		LOGGER.info("Inside Template Controller getAllTemplateNameAndIdWithDateFilter");
		HashMap<String, Object> result = new HashMap<>();

		List<Template> templateList = null;
		if (rcsMsgTypeId.equalsIgnoreCase("0")) {
			LOGGER.info("***** Getting All Template ****** ");

			templateList = templateRepository.findListByTemplateByUserId(templateUserId);
		} else {
			LOGGER.info("***** Getting All Template Based On RcsMsgTypeId ****** ");

			templateList = templateRepository.findAllByTemplateUserId(from + " 00:00:00", to + " 23:59:59",
					templateUserId, rcsMsgTypeId);
		}
		if (templateList.size() > 0) {
			List<HashMap<String, Object>> templateNameList = new ArrayList<>();
			for (Template tmp : templateList) {
				HashMap<String, Object> templateName = new HashMap<>();

				templateName.put("id", tmp.getId());
				templateName.put("templateCode", tmp.getTemplateCode());
				templateNameList.add(templateName);
			}

			result.put(STATUS, HttpStatus.OK);
			result.put("template", templateNameList);
			result.put(MESSAGE, "Record Founded.");
			return new ResponseEntity<>(result, HttpStatus.OK);
		} else {
			result.put(STATUS, HttpStatus.NOT_FOUND);
			result.put(MESSAGE, "Record Not Founded.");
			return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
		}
	}

	@GetMapping({ "/files" })
	public ResponseEntity<List<ResponseFile>> getListFiles() {
		List<ResponseFile> files = templateFileDbRepository.findAll().stream().map(dbFile -> {
			String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/files/")
					.path(String.valueOf(dbFile.getId())).toUriString();

			return new ResponseFile(dbFile.getName(), fileDownloadUri, dbFile.getType(), dbFile.getData().length);
		}).collect(Collectors.toList());

		return ResponseEntity.status(HttpStatus.OK).body(files);
	}

	@GetMapping({ "getFilesbyId" })
	public ResponseEntity<byte[]> getFile(@RequestParam("id") Long id) {
		TemplateFileDB fileDB = templateFileDbRepository.findById(id).get();

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileDB.getName() + "\"")
				.body(fileDB.getData());
	}

	/**
	 * 
	 */
	// @Scheduled(cron = "0 */1 * * * *")
	public ResponseEntity<HashMap<String, Object>> sendTemplateToOperator(Template template, MultipartFile[] files) {
		LOGGER.info("*****Ready to Send Template Json  To Operator ******");

		HashMap<String, Object> result = new HashMap<>();
		ResponseEntity<String> templateResponse = null;
		TokenPojo pojo = null;
		// Template template = null;
		try {
			// This Line I have To Commited
			// template = templateRepository.findById(1L).get();
			LOGGER.info("Basic Token :: " + templateBasicToken);
			pojo = new Gson().fromJson(botTokenAPIService.getTokenFromClientAPI(templateBasicToken), TokenPojo.class);

			if (pojo != null) {
				LOGGER.info("TOKEN :: " + pojo.getAccess_token());

				templateResponse = botTokenAPIService.sendTemplateToOperator(
						userRepository.findByUserId(template.getTemplateUserId()).getBotId(), pojo.getAccess_token(),
						template, files);
				if (templateResponse != null && templateResponse.getStatusCodeValue() == 202) {
					LOGGER.info("OPERATOR PUSHED TEMPLATE RESPONSE  :: " + templateResponse.getBody());

					result.put(STATUS, HttpStatus.OK);
					result.put("template", templateResponse.getBody());
					result.put(MESSAGE, "Template Pushed To Operator Successfully.");
					LOGGER.info("***** Template Json Pushed To Operator ******" + result.toString());
					Integer updatePushedTemplateResponse = 0;
					updatePushedTemplateResponse = templateRepository
							.updateTemplatePushedResponseByTemplateId(template.getId());
					if (updatePushedTemplateResponse == 1) {
						LOGGER.info("***** Pushed Template Response Updated. ******");

					} else {
						LOGGER.info("***** Pushed Template Response Not Updated. ******");

					}
					return new ResponseEntity<>(result, HttpStatus.CREATED);

				} else {
					result.put(STATUS, HttpStatus.NOT_FOUND);
					result.put("template", templateResponse.getBody());
					result.put(MESSAGE, "Template Json Not Pushed To Operator.");
					LOGGER.info("***** Template Json Not Pushed To Operator ******" + result.toString());
					return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);

				}
			} else {
				result.put(STATUS, 400);
				result.put(MESSAGE, "Token Not Created.");
				LOGGER.info("***** Token Not Created ******");
				return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);

			}
		} catch (Exception e) {
			LOGGER.info("***** Operator Given Error ******" + result.toString());

			e.printStackTrace();
			result.put(STATUS, 500);
			result.put(MESSAGE, e.getMessage());
			e.printStackTrace();
			return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);

		}
		// LOGGER.info("*****Pushed Done Template Json To Operator ******");
//		return null;

	}

	@SuppressWarnings("unused")
	// @Scheduled(cron = "0 */30 * * * *")
	public void checkTempalteStatusFromOperator() {
		LOGGER.info("*****Template Approval Schedular Task Started ******");

		HashMap<String, Object> result = new HashMap<>();

		TokenPojo pojo = null;
		List<Template> templateList = null;
		try {
			templateList = templateRepository.findPendingTemplateForApprovelFromOperator();
			LOGGER.info("*****Got Size For Approve Template ******" + templateList.size());

			if (templateList.size() > 0) {
				for (Template template : templateList) {
					pojo = new Gson().fromJson(botTokenAPIService.getTokenFromClientAPI(templateBasicToken),
							TokenPojo.class);
					LOGGER.info("***** Token Got It ::******" + pojo.getAccess_token());

					if (pojo != null) {
						ResponseEntity<String> templateResponse = botTokenAPIService.findTemplateStatusFromOperator(
								pojo.getAccess_token(),
								Base64.getEncoder().encodeToString(template.getTemplateCode().getBytes()),
								template.getBotId());
						// Base64.getEncoder().encodeToString(template.getTemplateCode().getBytes()),

						if (templateResponse != null && templateResponse.getBody().contains("approved")) {
							LOGGER.info(
									"***** GOING ON UPATING APPROVED OPERATOR STATUS ::" + templateResponse.getBody());

							JSONObject responseObj = new JSONObject(templateResponse.getBody());

							if (responseObj.has("templateModel")) {
								JSONObject templateModel = responseObj.getJSONObject("templateModel");

								String code = templateModel.optString("status");
								LOGGER.info("**** RESPONSE GOT IT FROM findTemplateStatusFromOperator() ::"
										+ templateResponse);

								result.put(STATUS, HttpStatus.OK);
								result.put("template", templateResponse);
								result.put(MESSAGE, "Template Pused To Operator For Approvel");
								LOGGER.info("***** Template Pused To Operator For Approvel  ******");

								Integer updateStatus = templateRepository
										.updateStatusByTemplateId(templateResponse.getBody(), code, template.getId());
								if (updateStatus == 1) {
									LOGGER.info("***** Approve Template Status Updated. ******");

								} else {
									LOGGER.info("***** Approve Template Status Not Updated. ******");

								}
							}
						} else {
							LOGGER.info("***** GOING ON UPATING OPERATOR STATUS ::" + templateResponse.getBody());
							JSONObject responseObj = new JSONObject(templateResponse.getBody());

							if (responseObj.has("templateModel")) {
								JSONObject templateModel = responseObj.getJSONObject("templateModel");

								String code = templateModel.optString("status");
								Integer updateStatus = templateRepository
										.rejectStatusByTemplateId(templateResponse.getBody(), code, template.getId());
								if (updateStatus == 1) {
									LOGGER.info("***** REJECT STATUS UPATED ******");

								} else {
									LOGGER.info("***** REJECT STATUS NOT UPATED ******");

								}
							}
							result.put(STATUS, HttpStatus.NOT_FOUND);
							result.put("template", template);
							result.put(MESSAGE, "Approve Template Status Not Updated");
							LOGGER.info("***** Approve Template Status Not Updated ******");
						}
					} else {
						result.put(STATUS, HttpStatus.NOT_FOUND);
						result.put(MESSAGE, "Token Not Created.");
						LOGGER.info("***** Token Not Created ******");
					}
				}
			}
		} catch (Exception e) {

			result.put(STATUS, HttpStatus.INTERNAL_SERVER_ERROR);
			result.put(MESSAGE, e.getMessage());
			LOGGER.info("***** Operator Given Error " + result.toString());

			e.printStackTrace();
		}
		LOGGER.info("*****Template Approval Schedular Task Done ******");

	}

	public Response sss(String botId, String viAuthorization, Template templateJson, MultipartFile[] files)
			throws IOException {
		OkHttpClient client = new OkHttpClient().newBuilder().build();
		MediaType mediaType = MediaType.parse("text/plain");
		@SuppressWarnings("deprecation")
		RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
				.addFormDataPart("rich_template_data", templateJson.getTemplateJson())
				.addFormDataPart("multimedia_files", "/C:/Users/rahul/Downloads/banner.jpg")
				.addFormDataPart("multimedia_files", "/C:/Users/rahul/Downloads/banners.jpg", RequestBody.create(
						MediaType.parse("application/octet-stream"), new File("/C:/Users/rahul/Downloads/banners.jpg")))
				.build();
		Request request = new Request.Builder()
				.url("https://virbm.in/directory/secure/api/v1/bots/f7u6t8vnIvM43ely/templates").method("POST", body)
				.addHeader("Authorization", "Bearer " + viAuthorization).build();
		Response response = client.newCall(request).execute();
		response.code();
		return response;
	}
}
