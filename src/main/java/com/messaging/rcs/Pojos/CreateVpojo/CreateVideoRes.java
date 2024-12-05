package com.messaging.rcs.Pojos.CreateVpojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateVideoRes {
  @JsonProperty("_id")
  String id;
  
  String name;
  
}
