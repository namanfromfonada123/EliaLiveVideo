package com.messaging.rcs.Pojos.DynamicVideoPojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DynamicVideoVideoID {
  String videoId;
  
  String requestId;
  
  boolean accepted;
  

}
