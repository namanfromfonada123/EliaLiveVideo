package com.messaging.rcs.Pojos.CreateVpojo;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateVideoPojo {
  String name;
  
  List<slide> slides;
  
  List<String> tags;
 
  
 
}
