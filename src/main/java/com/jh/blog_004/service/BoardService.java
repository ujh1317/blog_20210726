package com.jh.blog_004.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jh.blog_004.config.auth.PrincipalDetail;
import com.jh.blog_004.dto.ReplySaveRequestDto;
import com.jh.blog_004.model.Board;
import com.jh.blog_004.model.User;
import com.jh.blog_004.repository.BoardRepository;
import com.jh.blog_004.repository.ReplyRepository;

@Service
public class BoardService {

	@Autowired
	private BoardRepository boardRepository;

	@Autowired
	private ReplyRepository replyRepository;

	@Transactional
	public void boardWrite(Board board, User user) { // title, content
		board.setCount(0);
		board.setUser(user);
		boardRepository.save(board);
	}

	@Transactional(readOnly = true)
	public Page<Board> boardList(Pageable pageable) {
		return boardRepository.findAll(pageable);
	}

	@Transactional(readOnly = true)
	public Board boardDetail(int id) {
		return boardRepository.findById(id)
				.orElseThrow(() -> {
			return new IllegalArgumentException("글 상세보기 실패 : 아이디를 찾을 수 없습니다.");
		});
	}

	@Transactional
	public void boardUpdate(int id, Board requestBoard) {
		Board board = boardRepository.findById(id)
				.orElseThrow(() -> {
			return new IllegalArgumentException("글 찾기 실패 : 아이디를 찾을 수 없습니다.");
		});
		board.setTitle(requestBoard.getTitle());
		board.setContent(requestBoard.getContent());
	}

	@Transactional
	public void boardDelete(int id, PrincipalDetail principal) {
		Board board = boardRepository.findById(id)
				.orElseThrow(() -> {
			return new IllegalArgumentException("글 찾기 실패 : 해당 게시글을 찾을 수 없습니다.");
		});
		if (board.getUser().getId() != principal.getUser().getId()) {
			throw new IllegalArgumentException("글 삭제 실패 : 해당 게시글을 삭제할 권한이 없습니다.");
		}
		boardRepository.deleteById(id);
	}

	@Transactional
	public void replyWrite(ReplySaveRequestDto replySaveRequestDto) {
		replyRepository.mSave(replySaveRequestDto.getUserId(), replySaveRequestDto.getBoardId(),
				replySaveRequestDto.getContent());
	}

	@Transactional
	public void replyDelete(int replyId) {
		replyRepository.deleteById(replyId);
	}

}
