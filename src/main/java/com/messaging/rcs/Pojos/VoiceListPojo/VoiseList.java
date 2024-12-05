package com.messaging.rcs.Pojos.VoiceListPojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.messaging.rcs.Pojos.VoiceListPojo.Female;
import com.messaging.rcs.Pojos.VoiceListPojo.Male;

import lombok.Data;

import java.util.ArrayList;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class VoiseList {
  public String name;
  
  public ArrayList<Male> male;
  
  public ArrayList<Female> female;
  
}
