package com.devyoung.security.config.oauth;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.devyoung.security.config.auth.PrincipalDetails;
import com.devyoung.security.config.oauth.provider.GoogleUserInfo;
import com.devyoung.security.config.oauth.provider.NaverUserInfo;
import com.devyoung.security.config.oauth.provider.OAuth2UserInfo;
import com.devyoung.security.model.User;
import com.devyoung.security.repository.UserRepository;

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private UserRepository userRepository;
	
	// 구글로 부터 받은 userRequest 데이터에 대한 후처리되는 함수
	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		System.out.println("getClientRegistration => " + userRequest.getClientRegistration()); //registrationId로 어떤 OAuth로 로그인 했는지 알수 있음
		System.out.println("getAccessToken => " + userRequest.getAccessToken().getTokenValue());
		// 구글로그인 버튼 클릭 => 구글 로그인창 => 로그인을 완료 => code를 리턴(OAuth-Client라이브러리) => AccessToken요청
		// userRequest 정보 => loadUser함수 => 구글로부터 회원프로필을 받아준다.
		
		OAuth2User oauth2User = super.loadUser(userRequest);
	
		System.out.println("getAttributes => " + oauth2User.getAttributes());
		
		OAuth2UserInfo oAuth2UserInfo = null;
		if(userRequest.getClientRegistration().getRegistrationId().equals("google")) {
			System.out.println("구글 로그인 요청");
			oAuth2UserInfo = new GoogleUserInfo(oauth2User.getAttributes());
		} else if(userRequest.getClientRegistration().getRegistrationId().equals("naver")) {
			System.out.println("네이버 로그인 요청");
			oAuth2UserInfo = new NaverUserInfo((Map<String, Object>)oauth2User.getAttributes().get("response"));
		} else {
			System.out.println("우리는 구글과 네이버만 지원해요!");
		}
				
		String provider = oAuth2UserInfo.getProvider();  // google
		String providerId = oAuth2UserInfo.getProviderId();
		String username = oAuth2UserInfo.getName();  // ex) google_15203030255959595959
		String password = bCryptPasswordEncoder.encode("겟인데어");  // 의미없음(정보 저장을 위해 패스워드 임의로 암호화)
		String email = oAuth2UserInfo.getEmail();
		String role = "ROLE_USER";
		
		User userEntity = userRepository.findByUsername(username);
		
		if(userEntity == null) {   // 없으면 등록
			System.out.println("OAuth로그인이 처음 입니다.");
			userEntity = User.builder()
					.username(username)
					.password(password)
					.email(email)
					.role(role)
					.provider(provider)
					.providerId(providerId)
					.build();
			userRepository.save(userEntity);
		} else {
			System.out.println("이미 구글 로그인 한적이 있습니다. 당신은 자동회원가입이 되어 있습니다.");
		}
		
		// 회원가입을 강제로 진행해볼 예정
		return new PrincipalDetails(userEntity, oauth2User.getAttributes());
	}
	
}
