package com.Audible.UserService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Audible.UserService.entity.user;

@Repository
public interface userRepository extends JpaRepository<user, Integer>{
	user findByUsername(String username);
}
