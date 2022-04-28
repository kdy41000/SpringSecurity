package com.devyoung.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.devyoung.security.config.oauth.PrincipalOauth2UserService;

@Configuration
@EnableWebSecurity  // 스프링 시큐리티 필터체인에 등록된다.
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)  // @Secured 어노테이션 활성화, @PreAuthorize,@PostAuthorize 어노테이션 활성화
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private PrincipalOauth2UserService principalOauth2UserService;
	
	// 해당 메서드의 리턴되는 오브젝트를 IoC로 등록해준다.
	@Bean
	public BCryptPasswordEncoder encodePwd() {
		return new BCryptPasswordEncoder();
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// "user"로 접근시 인증된 사용자만 접근가능
		// "manager"로 접근시 인증된 사용자 + 권한이 "ROLE_ADMIN","ROLE_MANAGER"만 접근가능
		// "admin"로 접근시 인증된 사용자 + 권한이 "ROLE_ADMIN"만 접근가능
		http.csrf().disable();
		http.authorizeRequests()
			.antMatchers("/user/**").authenticated()
			.antMatchers("/manager/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
			.antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")
			.anyRequest().permitAll()
			.and()
			.formLogin()
			.loginPage("/loginForm")
		 // .usernameParameter("userId")  UI에서 넘어오는 key값을 username으로 변경해주는 설정 => PrincipalDetailsService의 loadUserByUsername메서드에서는 무조건 username으로 받기 때문!
			.loginProcessingUrl("/login") // /login 주소가 호출이 되면 시큐리티가 낚아채서 대신 로그인을 진행해준다.
			.defaultSuccessUrl("/")       // 성공하면 / 로 이동
			.and()
			.oauth2Login()      //oauth2 login
			.loginPage("/loginForm")
			// 구글 로그인이 완료된 뒤의 후처리가 필요함. 1.코드받기(인증됨) 2.엑세스토큰(권한) 3.사용자프로필 정보를 가져오고 4.그 정보를 토대로 회원가입을 자동으로 진행시키기도 함
			// oauth2 라이브러리를 사용하면 코드X, (엑세스토큰+사용자정보 O) 좀더 편리하게 사용가능
			.userInfoEndpoint()
			.userService(principalOauth2UserService);
	}
	
}
