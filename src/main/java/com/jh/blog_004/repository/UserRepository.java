package com.jh.blog_004.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jh.blog_004.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {

//	User findByUsernameAndPassword(String username, String password);
}
