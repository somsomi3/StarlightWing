package com.minjoo.StarlightWing.service;

import com.minjoo.StarlightWing.dto.BoardDto;
import com.minjoo.StarlightWing.dto.UserDto;
import com.minjoo.StarlightWing.model.Board;
import com.minjoo.StarlightWing.persist.BoardRepository;
import org.h2.engine.User;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

//db를 활용한 대부분을 처리를 여기서 하게됨.
@Service
public class BoardService {

    //    @Autowired
    private final BoardRepository boardRepository;

    public BoardService(BoardRepository boardRepository){
        this.boardRepository = boardRepository;
    }

    //게시글을 저장하는 writeBoard
    @Transactional
    public void writeBoard(Board board, UserDto userId){
        //실제 저장시에 member에 해당하는 아이디만 저장하게 된다.
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        // 작성자 설정
        board.setAuthor(userId);
        boardRepository.save(board); // 저장
    }


    //게시글리스트를 출력하는 getBoardList 메서드, 전체목록을 pageable객체로 반환하게함.
    //이때 findAll 메서드를 사용하는데, 이는 selectAll과같은 역할을 한다.
    public Page<BoardDto> getBoardList(Pageable pageable) {
        return boardRepository.findAll(pageable).map(board ->
            new BoardDto(
                board.getId(),
                board.getTitle(),
                board.getContent(),
                board.getAuthor(),
                board.getCreatedAt(),
                board.getUpdatedAt()
            )
        );
    }

    // 특정 게시글을 출력하는 getBoard(읽기만가능하게= readOnly)
    @Transactional(readOnly = true)
    public Board getBoard(Long id) {
        // 특정 ID로 게시글 조회
        return boardRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Failed to load post: cannot find post id"));
    }

    //게시글을 삭제하는 deleteBoard 메서드
    @Transactional
    public void deleteBoard(long id){
        //id만 넘겨서, 해당글에 해당하는 id 로그를 지워준다
        boardRepository.deleteById(id);
    }

    //게시글을 수정할 수 있는 updateBoard 메서드
    @Transactional
    public void updateBoard(long id, Board requestBoard){
        Board board = boardRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Failed to load post : cannot find post id")); //영속화
        board.setTitle(requestBoard.getTitle());
        board.setContent(requestBoard.getContent());
        // 카테고리 업데이트
        //board.setCategory(requestBoard.getCategory());

    }
}

//엔티티들 = db에서 조회된 어떤 값들.
//영속성 컨텍스트의 기능중하나는, 변경을 감지한다. 그리고 자동으로 데이터 베이스에 감지하는 더티체킹이 있다.(spring jpa에 있는 기능)
