package com.devyoung.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devyoung.security.model.User;

// CRUD 함수를 JpaRepository가 들고 있음.
// @Repository라는 어노테이션이 없어도 IoC가능. 이유는 JpaRepository를 상속받았기 떄문에
public interface UserRepository extends JpaRepository<User, Integer>{

	// findBy규칙 -> Username문법
	// select * from user where username = 1?
	public User findByUsername(String username);  // jpa Query methods
	
}
