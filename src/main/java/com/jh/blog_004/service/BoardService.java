package com.jh.blog_004.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jh.blog_004.model.Board;
import com.jh.blog_004.model.User;
import com.jh.blog_004.repository.BoardRepository;

@Service
public class BoardService {

	@Autowired
	private BoardRepository boardRepository;
	
	@Transactional
	public void boardWrite(Board board, User user) { //title, content
		board.setCount(0);
		board.setUser(user);
		boardRepository.save(board);
	}
	
	public List<Board> boardList() {
		return boardRepository.findAll();
	}
	
}
