package com.messaging.rcs.model;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table
@Data
public class TemplateVideo {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  
  private String userId;
  
  @Column(length = 2000)
  private String reqString;
  
  private String resString;
  
  private String resVideoTemplateID;
  
  private String templateVideoName;
  
  @Column(length = 20)
  private String createdOn;
  
  @Column(length = 20)
  private String updatedOn;
  
  
  @PrePersist
  protected void onCreate() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    this.createdOn = LocalDateTime.now().format(formatter);
  }
  
  @PreUpdate
  protected void onUpdate() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    this.updatedOn = LocalDateTime.now().format(formatter);
  }
  
}
