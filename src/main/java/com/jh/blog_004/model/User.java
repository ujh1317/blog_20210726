package com.jh.blog_004.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class User {
	
	@Id //Primary key
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 넘버링
	private int id;
	
	@Column(nullable = false, length = 50, unique = true)
	private String username;
	
	@Column(nullable = false, length = 100)
	private String password;
	
	@Column(nullable = false, length = 50)
	private String email;
	
	//@ColumnDefault("'user'")
	@Enumerated(EnumType.STRING)
	private RoleType role; //ADMIN, USER
	
	private String oauth; //kakao
	
	@CreationTimestamp
	private Timestamp createDate;
}
