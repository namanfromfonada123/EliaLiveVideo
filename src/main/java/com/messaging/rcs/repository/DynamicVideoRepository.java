package com.messaging.rcs.repository;

import com.messaging.rcs.model.DyVideo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DynamicVideoRepository extends JpaRepository<DyVideo, Long> {}
