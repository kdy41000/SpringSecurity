package com.devyoung.security.model;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity  // JPA에서 사용할 엔티티 이름을 지정,  보통 기본값인 클래스 이름을 사용
@Data
@NoArgsConstructor
public class User {

	@Id // primary key(기본 키를 애플리케이션에 직접 할당)
	@GeneratedValue(strategy = GenerationType.IDENTITY)   // 기본 키 생성을 데이터베이스에 위임 (= AUTO_INCREMENT)
	private int id;
	private String username;
	private String password;
	private String email;
	private String role;
	
	private String provider;  // google
	private String providerId;  // google에서 받은 id
	@CreationTimestamp
	private Timestamp createDate;
	
	@Builder
	public User(String username, String password, String email, String role, String provider, String providerId,
			Timestamp createDate) {
		this.username = username;
		this.password = password;
		this.email = email;
		this.role = role;
		this.provider = provider;
		this.providerId = providerId;
		this.createDate = createDate;
	}
	
	
}
