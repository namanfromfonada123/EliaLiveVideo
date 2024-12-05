package com.messaging.rcs.Pojos.DynamicVideoPojo;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class TemplateDataPojo {
  String name;
  
  String product;
  
  @JsonProperty("457597335176")
  String imageurl;
}
