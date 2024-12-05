package com.messaging.rcs.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.messaging.rcs.domain.UserEntity;

/**
 * 
 * @author RahulRajput 2023-06-13
 *
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
	Optional<UserEntity> findByUserName(String username);

	@Query("select u.apiKey from UserEntity u where u.apiKey = ?1")
	String findbyApiKey(String apiKey);

	UserEntity findByUserId(Long userId);

	UserEntity findByUserNameAndEmail(String username, String email);

	@Query("select u from UserEntity u where u.userName = ?1 and u.userPassword =?2 and u.isDeleted = 0")
	UserEntity findByUserNameAndUserPassword(String userName, String userPassword);

	UserEntity getUserEntityByUserId(Long userId);

	@Query("select u from UserEntity u where u.parentUserId = ?1")
	List<UserEntity> getUserByUserParentId(Long parentId);

	@Query("select u from UserEntity u where u.parentUserId = 0")
	List<UserEntity> getAdminUser();

	@Transactional
	@Modifying
	@Query(value = "update users set sms_dlt_principle_id=:smsDltPrincipleId where user_id=:user_id", nativeQuery = true)
	Integer updateSmsDltPrincipleId(@Param("smsDltPrincipleId") String smsDltPrincipleId,
			@Param("user_id") String user_id);

	UserEntity getByUserName(String username);
}
