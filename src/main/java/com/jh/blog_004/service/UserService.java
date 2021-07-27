package com.jh.blog_004.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jh.blog_004.model.RoleType;
import com.jh.blog_004.model.User;
import com.jh.blog_004.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder encoder;
	
	@Transactional
	public void join(User user) {
		String rawPassword = user.getPassword(); //원문
		String encPassword = encoder.encode(rawPassword); //해쉬
		user.setPassword(encPassword);
		user.setRole(RoleType.USER);
		userRepository.save(user);
	}
	
	@Transactional
	public void userUpdate(User user) {
		User persistance = userRepository.findById(user.getId()).orElseThrow(()->{
			return new IllegalArgumentException("회원찾기 실패");
		});
		
		//Validate 체크 (oauth 필드에 값이 없으면 수정가능)
		if(persistance.getOauth() == null || persistance.getOauth().equals("")) {
			String rawPassword = user.getPassword();
			String encPassword = encoder.encode(rawPassword);
			persistance.setPassword(encPassword);
			persistance.setEmail(user.getEmail());
		}
	}
	
	@Transactional(readOnly = true)
	public User findByUser(String username) {
		User user = userRepository.findByUsername(username).orElseGet(()->{
			return new User();
		});
		return user;
	}
	
	
//	@Transactional(readOnly = true)
//	public User login(User user) {
//		return userRepository.findByUsernameAndPassword(user.getUsername(), user.getPassword());
//	}
	
}
