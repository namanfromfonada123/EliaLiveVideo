package com.messaging.rcs.Pojos;

import lombok.Data;

@Data
public class ResponsePojo {
  int status;
  
  Object data;
  
  String message;
  
}
