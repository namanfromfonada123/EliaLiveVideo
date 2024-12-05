package com.messaging.rcs.Pojos.DynamicVideoPojo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class DynamicVideoRoot {
  boolean emailNotification;
  
  boolean fitTextBox;
  
  @JsonProperty("public")
  boolean public1;
  
  boolean render;
  
  List<Map<String, String>> templateData;
  
  List<String> tags;
  
  String requestId;
  
}
