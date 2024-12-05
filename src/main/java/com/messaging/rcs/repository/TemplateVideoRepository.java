package com.messaging.rcs.repository;


import com.messaging.rcs.model.TemplateVideo;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TemplateVideoRepository extends JpaRepository<TemplateVideo, Long> {
  @Modifying
  @Transactional
  int deleteByResVideoTemplateID(String paramString);
  
  Optional<TemplateVideo> findByResVideoTemplateID(String paramString);
}
