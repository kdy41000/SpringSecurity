package com.devyoung.security.config.auth;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.devyoung.security.model.User;

import lombok.Data;

// 시큐리티가 /login 주소 요청이 오면 낚아채서 로그인을 진행시킨다.
// 로그인이 진행이 완료가 되면 session을 만들어준다. (Security ContextHolder <=여기에 저장)
// 오브젝트 => Authentication 타입의 객체
// Authentication 안에 User정보가 있어야 됨.
// User오브젝트타입 => UserDetails타입 객체

// Security Session => Authentication타입 => UserDetails타입(PrincipalDetails)

@Data
public class PrincipalDetails implements UserDetails, OAuth2User {  //UserDetails, OAuth2User를 PrincipalDetails로 묶어서 PrincipalDetails를 인자로 받으면 2개 다 사용하능하도록 설정

	private User user;  //콤포지션
	private Map<String, Object> attributes;
	
	// 일반 로그인
	public PrincipalDetails(User user) {
		this.user = user;
	}
	
	// OAuth 로그인
	public PrincipalDetails(User user, Map<String, Object>attributes) {
		this.user = user;
		this.attributes = attributes;
	}
	
	//UserDetails Override
	// 해당 User의 권한을 리턴하는 곳!!
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> collect = new ArrayList<>();
		collect.add(new GrantedAuthority() {
			
			@Override
			public String getAuthority() {
				return user.getRole();
			}
		});
		return collect;
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getUsername();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		
		// 사이트에서 1년동안 회원이 로그인을 안해서 휴먼계정으로 하기로 하면
		// 로그인을 시도한 사용자의 마지막 로그인 시간을 들고와서 "현재시간 - 마지막 로그인시간" => 1년을 초과하면 return false;
		return true;
	}

	//OAuth2 Override
	@Override
	public Map<String, Object> getAttributes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

}
