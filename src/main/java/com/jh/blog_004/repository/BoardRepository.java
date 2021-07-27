package com.jh.blog_004.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jh.blog_004.model.Board;

public interface BoardRepository extends JpaRepository<Board, Integer> {

	
}
