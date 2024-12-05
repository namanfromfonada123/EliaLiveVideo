package com.messaging.rcs.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.messaging.rcs.ApiCall.VideoApiCalls;
import com.messaging.rcs.Pojos.CreateVpojo.CreateVideoPojo;
import com.messaging.rcs.Pojos.CreateVpojo.CreateVideoRes;
import com.messaging.rcs.Pojos.CreateVpojo.slide;
import com.messaging.rcs.Pojos.DynamicVideoPojo.DynamicVideoRoot;
import com.messaging.rcs.Pojos.VoiceListPojo.VoiseList;
import com.messaging.rcs.domain.UserEntity;
import com.messaging.rcs.model.DyVideo;
import com.messaging.rcs.model.TemplateVideo;
import com.messaging.rcs.repository.DynamicVideoRepository;
import com.messaging.rcs.repository.TemplateVideoRepository;
import com.messaging.rcs.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class LiveVideoService {
  Logger logger = LoggerFactory.getLogger(com.messaging.rcs.service.LiveVideoService.class);
  
  @Autowired
  VideoApiCalls videoApiCalls;
  
  @Autowired
  DynamicVideoRepository dvRepository;
  
  @Autowired
  TemplateVideoRepository templateVideoRepository;
  
  @Autowired
  UserRepository userRepository;
  
  public String CreateVideo(String reqCreatePojo, String username) throws Exception {
    Optional<UserEntity> userentity = this.userRepository.findByUserName(username);
    if (userentity.isPresent()) {
      ObjectMapper objectMapper = new ObjectMapper();
      CreateVideoPojo createVideoPojo = (CreateVideoPojo)objectMapper.readValue(reqCreatePojo, CreateVideoPojo.class);
      String userId = String.valueOf(((UserEntity)userentity.get()).getUserId());
      this.logger.info("** Creating Video with video name: " + createVideoPojo.getName() + " for UserId :" + userId + " **");
      String speech = ((slide)createVideoPojo.getSlides().get(0)).getSpeech().replace("[", "{{").replace("]", "}}");
      this.logger.info("Speech : " + speech);
      String speechVariable = extractVariablefromSpeechString(speech);
      this.logger.info("** Calling create video Api **");
      String createVideoResponse = this.videoApiCalls.CreateVideo(reqCreatePojo);
      CreateVideoRes res = (CreateVideoRes)objectMapper.readValue(createVideoResponse, CreateVideoRes.class);
      String resString = objectMapper.writeValueAsString(res);
      String reqString = objectMapper.writeValueAsString(reqCreatePojo);
      Map<String, Object> CreateVideoMap = new HashMap<>();
      CreateVideoMap.put("Create_Video_Response", resString);
      CreateVideoMap.put("speechVariable", speechVariable);
      CreateVideoMap.put("Userid", userId);
      TemplateVideo tv = new TemplateVideo();
      tv.setUserId(userId);
      tv.setReqString(reqString);
      tv.setResString(resString);
      tv.setTemplateVideoName(createVideoPojo.getName());
      tv.setResVideoTemplateID(res.getId());
      this.logger.info("** Saving video response and request to database **");
      this.templateVideoRepository.save(tv);
      this.logger.info("** Creating Video with video name: " + createVideoPojo.getName() + " for User :" + userId + " Successfully!! ** ");
      return objectMapper.writeValueAsString(CreateVideoMap);
    } 
    return null;
  }
  
  public String getVideoListService(String limit, String page, String username) {
    Optional<UserEntity> userentity = this.userRepository.findByUserName(username);
    if (userentity.isPresent()) {
      String userId = String.valueOf(((UserEntity)userentity.get()).getUserId());
      this.logger.info(" ** Calling getvideolist Api  having userid : " + userId + " ** ");
      return this.videoApiCalls.getVideoList(page, limit);
    } 
    this.logger.error(" No userId found With This username :" + username);
    return " No userId found With This username";
  }
  
  public ResponseEntity<?> retriveVideoPojoService(String videoId, String username) throws Exception {
    Optional<UserEntity> userentity = this.userRepository.findByUserName(username);
    if (userentity.isPresent()) {
      String userId = String.valueOf(((UserEntity)userentity.get()).getUserId());
      this.logger.info(" ** Calling Retrive video Api  having userid : " + userId + "** ");
      ResponseEntity<?> retriveVideoresResponseEntity = this.videoApiCalls.retriveVideo(videoId);
      return retriveVideoresResponseEntity;
    } 
    return ResponseEntity.badRequest().body("No User With This Token Present");
  }
  
  public ResponseEntity<String> deleteVideoService(String videoId, String username) throws Exception {
    Optional<UserEntity> userentity = this.userRepository.findByUserName(username);
    Optional<TemplateVideo> templateVideo = this.templateVideoRepository.findByResVideoTemplateID(videoId);
    if (userentity.isPresent()) {
      if (templateVideo.isPresent()) {
        if (((TemplateVideo)templateVideo.get()).getUserId().equals(String.valueOf(((UserEntity)userentity.get()).getUserId()))) {
          String userId = String.valueOf(((UserEntity)userentity.get()).getUserId());
          this.logger.info(" ** Calling Delete video  Api  for userId  " + userId + "** ");
          this.templateVideoRepository.deleteByResVideoTemplateID(videoId);
          this.logger.info("** after deleting data from db");
          return this.videoApiCalls.deleteVideoList(videoId);
        } 
        this.logger.info("This User Does't Access this videoId : " + videoId + " UserId :  " + ((UserEntity)userentity.get()).getUserId() + "");
        return ResponseEntity.badRequest().body("This User Does't Access this videoId : " + videoId);
      } 
      this.logger.info(" Video Id Not present !! ");
      return ResponseEntity.badRequest().body(" Video Id Not present !! ");
    } 
    this.logger.error("User with this token Not Found :  " + username);
    return ResponseEntity.badRequest().body("User with this token Not Found :  " + username);
  }
  
  public String renderVideoService(String videoId, String username) {
    Optional<UserEntity> userentity = this.userRepository.findByUserName(username);
    if (userentity.isPresent()) {
      String userId = String.valueOf(((UserEntity)userentity.get()).getUserId());
      this.logger.info(" ** Calling getVoiceList  Api  for userId  " + userId + "** ");
      return this.videoApiCalls.RenderVideoList(videoId, userId);
    } 
    this.logger.error("User with this token Not Found :  " + username);
    return null;
  }
  
  public List<VoiseList> getVoiceListService(String username) throws Exception {
    Optional<UserEntity> userentity = this.userRepository.findByUserName(username);
    if (userentity.isPresent()) {
      String userId = String.valueOf(((UserEntity)userentity.get()).getUserId());
      this.logger.info(" ** Calling getVoiceList  Api  for userId  " + userId + "** ");
      return this.videoApiCalls.getVoiceList(userId);
    } 
    this.logger.error("User with this token Not Found :  " + username);
    return null;
  }
  
  public ResponseEntity<?> dynamicVideo(String videoId, Map<String, String> templateData, String username) throws Exception {
    Optional<UserEntity> userentity = this.userRepository.findByUserName(username);
    List<Map<String, String>> templatedataList = new ArrayList<>();
    if (userentity.isPresent()) {
      String userId = String.valueOf(((UserEntity)userentity.get()).getUserId());
      this.logger.info("** Started Creating dynamic video by master video :" + videoId + " for userId :" + userId + "**");
      DynamicVideoRoot dVR = new DynamicVideoRoot();
      dVR.setEmailNotification(false);
      dVR.setFitTextBox(true);
      dVR.setPublic1(true);
      dVR.setRender(true);
      String requestid = videoId + LocalDateTime.now().toString();
      dVR.setRequestId(requestid);
      List<String> tagStrings = new ArrayList<>();
      tagStrings.add(requestid);
      dVR.setTags(tagStrings);
      templatedataList.add(templateData);
      dVR.setTemplateData(templatedataList);
      ResponseEntity<?> responseString = this.videoApiCalls.dynamicVideoCreation(videoId, dVR);
      System.out.println("Status code : " + responseString.getStatusCodeValue());
      if (responseString.getStatusCodeValue() == 200) {
        savetoDynamicVideodb(userId, (new ObjectMapper()).writeValueAsString(dVR), responseString.getBody().toString(), videoId);
      } else {
        savetoDynamicVideodb(userId, (new ObjectMapper()).writeValueAsString(dVR), responseString.getBody().toString(), videoId);
      } 
      return responseString;
    } 
    this.logger.error("No user with this Username found ");
    return ResponseEntity.badRequest().body("User Not Found !!");
  }
  
  public void savetoDynamicVideodb(String userId, String req, String res, String VideoId) {
    DyVideo dVideo = new DyVideo();
    dVideo.setUserId(userId);
    dVideo.setReqString(req);
    dVideo.setTemplateVideoId(VideoId);
    dVideo.setResString(res);
    this.dvRepository.save(dVideo);
  }
  
  public String getAvatarList(String username) throws Exception {
    Optional<UserEntity> userentity = this.userRepository.findByUserName(username);
    if (userentity.isPresent()) {
      String userId = String.valueOf(((UserEntity)userentity.get()).getUserId());
      this.logger.info(" ** Calling getAvatarList Api  for userId  " + userId + "** ");
      return this.videoApiCalls.getAvatarList(userId);
    } 
    this.logger.error("User with this token Not Found :  " + username);
    return null;
  }
  
  public String extractVariablefromSpeechString(String Speech) {
    Pattern pattern = Pattern.compile("\\{\\{(.*?)\\}\\}");
    Matcher matcher = pattern.matcher(Speech);
    String SpeechVariable = "";
    while (matcher.find())
      SpeechVariable = SpeechVariable + " video_" + matcher.group(1) + ","; 
    System.out.println("Extracted variables: " + SpeechVariable);
    return SpeechVariable;
  }
}

