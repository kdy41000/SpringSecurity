package com.devyoung.security.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.devyoung.security.config.auth.PrincipalDetails;
import com.devyoung.security.model.User;
import com.devyoung.security.repository.UserRepository;

@Controller
public class IndexController {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@GetMapping("/test/login")
	public @ResponseBody String testLogin(Authentication authentication, @AuthenticationPrincipal PrincipalDetails userDetails) {  //DI(의존성 주입)
		System.out.println("/test/login ====================");
		PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
		System.out.println("authentication: " + principalDetails.getUser());
		
		System.out.println("userDetails : " + userDetails.getUser());
		return "셔션 정보 확인";
	}
	
	@GetMapping("/test/oauth/login")
	public @ResponseBody String testOAuthLogin(Authentication authentication, @AuthenticationPrincipal OAuth2User oauth) {  //DI(의존성 주입)
		System.out.println("/test/login ====================");
		OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
		System.out.println("authentication: " + oauth2User.getAttributes());
		System.out.println("oauth2User: " + oauth.getAuthorities());
		return "셔션 정보 확인"; 
	} 
	
	@GetMapping({"","/"})
	public String index() {
		// 머스테치 기본폴더 src/main/resources/
		// 뷰리졸버 설정: templates (prefix), .mustache (suffix) 생략가능!
		return "index";  // src/main/resources/templates/index.mustache
	}
	
	@GetMapping("/user")
	public @ResponseBody String user(@AuthenticationPrincipal PrincipalDetails principalDetails) {
		System.out.println("principalDetails: " + principalDetails.getUser());
		return "user";
	}
	
	@GetMapping("/admin")
	public @ResponseBody String admin() {
		return "admin";
	}
	
	@GetMapping("/manager")
	public @ResponseBody String manager() {
		return "manager";
	}
	
	// 스프링 시큐리티가 해당주소를 낚아챔(SecurityConfig파일 생성 후 설정 시 시큐리티가 낚아채지 않고 해당api로 호출됨)
	@GetMapping("/loginForm")
	public String loginForm() {
		return "loginForm";
	}
	
	@GetMapping("/joinForm")
	public String joinForm() {
		return "joinForm";
	}
	
	@PostMapping("/join")
	public String join(User user) {
		user.setRole("ROLE_USER");
		String rawPassword = user.getPassword();
		String encPassword = bCryptPasswordEncoder.encode(rawPassword);  //패스워드 암호화
		user.setPassword(encPassword);
		userRepository.save(user); //회원가입 잘됨. 비밀번호: 1234 => 시큐리티로 로그인을 할 수 없음. 이유는 패스워드가 암호화가 안되어있기 때문
		return "redirect:/loginForm";  // loginForm api가 호출됨
	}
	
	@Secured("ROLE_ADMIN")  //해당 api호출시 권한이 admin만 호출가능(단일 설정가능)
	@GetMapping("/info")
	public @ResponseBody String info() {
		return "개인정보";
	}
	
	// @PostAuthorize : 해당 api가 호출되고 처리 한 후 권한에 따라 제어
	@PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_USER')")  //해당 api호출시 권한이 manager, user만 호출가능(단일, 멀티 설정가능)
	@GetMapping("/data")
	public @ResponseBody String data() {
		return "데이터정보";
	}
	
}
