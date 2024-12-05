package com.messaging.rcs.utility;


import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@ConfigurationProperties("")
@Component
public class ApplicationProperties {
  public void setHeaders(Map<String, String> headers) {
    this.headers = headers;
  }
  
  public void setUrl(Map<String, String> url) {
    this.url = url;
  }

  
  Map<String, String> headers = new HashMap<>();
  
  public Map<String, String> getHeaders() {
    return this.headers;
  }
  
  Map<String, String> url = new HashMap<>();
  
  public Map<String, String> getUrl() {
    return this.url;
  }
}
