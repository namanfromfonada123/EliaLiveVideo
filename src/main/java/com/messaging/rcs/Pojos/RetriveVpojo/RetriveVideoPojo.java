package com.messaging.rcs.Pojos.RetriveVpojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RetriveVideoPojo {
  String thumbnail;
  
  String url;

}
