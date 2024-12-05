package com.messaging.rcs.Pojos.VoiceListPojo;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.messaging.rcs.Pojos.VoiceListPojo.VoiseList;

import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RootPojoVoice {
  List<VoiseList> voiceLists;
  
}
