package com.messaging.rcs.Pojos.VoiceListPojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayedTag {
  public String id;
  
  public String name;
  
  public String url;
  
}
