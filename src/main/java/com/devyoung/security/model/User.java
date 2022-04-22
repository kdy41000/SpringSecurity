package com.devyoung.security.model;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Data;

@Entity  // 테이블 자동생성됨
@Data
public class User {

	@Id // primary key
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String username;
	private String password;
	private String email;
	private String role;
	
	private String provider;  // google
	private String providerId;  // google에서 받은 id
	@CreationTimestamp
	private Timestamp createDate;
	
}
