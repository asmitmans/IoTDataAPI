package com.futuro.iotdataapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.futuro.iotdataapi.entity.User;

import jakarta.transaction.Transactional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

	Optional<User> findByUsername(String username);

	boolean existsByUsername(String username);

	@Transactional
	@Modifying
	@Query("UPDATE User us SET us.password =:password WHERE us.username =:username")
	void changePassword(@Param("password") String password, @Param("username") String username);
	
	long countByEnabledTrue();
}
