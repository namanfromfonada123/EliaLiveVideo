package com.messaging.rcs.Pojos.CreateVpojo;


import com.messaging.rcs.Pojos.CreateVpojo.avatar;
import com.messaging.rcs.Pojos.CreateVpojo.canvas;

import lombok.Data;

@Data
public class slide {
  int id;
  
  canvas canvas;
  
  avatar avatar;
  
  String animation;
  
  String language;
  
  String speech;
  
  String voice;
  
  String voiceType;
  
  String voiceProvider;
  
}

