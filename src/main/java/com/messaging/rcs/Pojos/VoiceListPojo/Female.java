package com.messaging.rcs.Pojos.VoiceListPojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.messaging.rcs.Pojos.VoiceListPojo.PlayedTag;

import lombok.Data;

import java.util.ArrayList;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Female {
  public String voiceProvider;
  
  public String character;
  
  public String name;
  
  public String locale;
  
  public String voice;
  
  public String icon;
  
  public ArrayList<String> styleList;
  
  public String defaultStyle;
  
  public boolean premium;
  
  public ArrayList<String> tags;
  
  public ArrayList<PlayedTag> playedTags;
  
  public String url;
  
  public int order;
  
  public ArrayList<String> rolePlayList;
  
}
