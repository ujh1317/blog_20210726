package com.jh.blog_004.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jh.blog_004.model.KakaoProfile;
import com.jh.blog_004.model.OAuthToken;
import com.jh.blog_004.model.User;
import com.jh.blog_004.service.UserService;

@Controller
public class UserController {

	@Value("${jh.key}")
	private String jhKey;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private UserService userService;
	
	@GetMapping("/auth/joinForm")
	public String joinForm() {
		return "user/joinForm";
	}
	
	@GetMapping("/auth/loginForm")
	public String loginForm() {
		return "user/loginForm";
	}
	
	@GetMapping("/auth/kakao/callback")
	public String kakaoCallback(String code) {
		
		RestTemplate rt = new RestTemplate();
		
		//HttpHeader 오브젝트 생성
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
		
		//HttpBody 오브젝트 생성
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "authorization_code");
		params.add("client_id", "054badfe27f4e9c522495ec6d3e45434");
		params.add("redirect_uri", "http://localhost:8080/auth/kakao/callback");
		params.add("code", code);
		
		//HttpHeader와 HttpBody를 하나의 오브젝트에 담기
		HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);
		
		//Http 요청하기(POST방식으로) - response 변수의 응답 받음
		ResponseEntity<String> response = rt.exchange(
				"https://kauth.kakao.com/oauth/token",
				HttpMethod.POST,
				kakaoTokenRequest,
				String.class
		);
		
		ObjectMapper objectMapper = new ObjectMapper();
		OAuthToken oauthToken = null;
		try {
			oauthToken = objectMapper.readValue(response.getBody(), OAuthToken.class);
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		System.out.println("카카오 엑세스 토큰 : "+ oauthToken.getAccess_token());
		
		
		RestTemplate rt2 = new RestTemplate();
		
		HttpHeaders headers2 = new HttpHeaders();
		headers2.add("Authorization", "Bearer "+oauthToken.getAccess_token());
		headers2.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

		
		HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest = new HttpEntity<>(headers2);
		
		ResponseEntity<String> response2 = rt2.exchange(
				"https://kapi.kakao.com/v2/user/me",
				HttpMethod.POST,
				kakaoProfileRequest,
				String.class
		);
		System.out.println(response2.getBody());
		
		ObjectMapper objectMapper2 = new ObjectMapper();
		KakaoProfile kakaoProfile = null;
		try {
			kakaoProfile = objectMapper2.readValue(response2.getBody(), KakaoProfile.class);
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		System.out.println("카카오 아이디(번호) : "+kakaoProfile.getId());
		System.out.println("카카오 이메일 : "+kakaoProfile.getKakao_account().getEmail());
		
		System.out.println("블로그서버 유저네임 : "+kakaoProfile.getKakao_account().getEmail()+"_"+kakaoProfile.getId());
		System.out.println("블로그서버 이메일 : "+kakaoProfile.getKakao_account().getEmail());
		System.out.println("블로그서버 패스워드 : "+jhKey);
		
		User kakaoUser = User.builder()
				.username(kakaoProfile.getKakao_account().getEmail()+"_"+kakaoProfile.getId())
				.password(jhKey)
				.email(kakaoProfile.getKakao_account().getEmail())
				.oauth("kakao")
				.build();
		
		User originUser = userService.findByUser(kakaoUser.getUsername());
		if(originUser.getUsername() == null) {
			System.out.println("가입된 회원이 아닙니다.");
			userService.join(kakaoUser);
		}
		
		//로그인 처리
		Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(kakaoUser.getUsername(), jhKey));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		return "redirect:/";
	}
	
	@GetMapping("/user/updateForm")
	public String updateForm() {
		return "user/updateForm";
	}
	
}
