package toyproject.board.service;

import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import toyproject.board.domain.board.Board;
import toyproject.board.domain.board.BoardRepository;
import toyproject.board.domain.board.query.BoardQueryRepository;
import toyproject.board.domain.comment.CommentRepository;
import toyproject.board.domain.member.Member;
import toyproject.board.dto.board.command.*;
import toyproject.board.dto.board.query.BoardAndCommentCount;
import toyproject.board.dto.board.query.BoardQueryDto;
import toyproject.board.dto.board.query.BoardSearchCondition;
import toyproject.board.dto.CheckPasswordDto;

import javax.validation.Valid;

import static org.springframework.util.StringUtils.hasText;

@RequiredArgsConstructor
@Validated
@Transactional(readOnly = true)
@Service
public class BoardService {

    private final BoardRepository boardRepository;
    private final BoardQueryRepository boardQueryRepository;
    private final CommentRepository commentRepository;

    // 게시물 생성 (로그인)
    @Transactional
    @Validated
    public Long createBoard(@Valid CreateBoardLoginDto dto) {

        Member member = dto.getMember();
        dto.setNickname(member.getUsername());

        Board board = dto.toEntity();
        boardRepository.save(board);

        return board.getId();
    }

    // 게시물 생성 (비로그인)
    @Transactional
    @Validated
    public Long createBoard(@Valid CreateBoardNotLoginDto dto) {

        dto.setPassword(
                BCrypt.hashpw(dto.getPassword(), BCrypt.gensalt(10))
        );

        Board board = dto.toEntity();
        boardRepository.save(board);

        return board.getId();
    }

    // 게시물 삭제 (로그인)
    @Transactional
    @Validated
    public void deleteBoard(@Valid DeleteBoardLoginDto dto) {

        Board board = getBoardWithPassword(dto.getId());
        checkMemberId(board.getMember().getId(), dto.getMember().getId(), true);

        commentRepository.deleteByBoardId(board.getId());
        boardRepository.delete(board);

    }

    // 게시물 삭제 (비로그인)
    @Transactional
    @Validated
    public void deleteBoard(@Valid DeleteBoardNotLoginDto dto) {

        Board board = getBoardWithPassword(dto.getId());
        checkPassword(dto.getPassword(), board.getPassword());

        commentRepository.deleteByBoardId(board.getId());
        boardRepository.delete(board);

    }

    // 게시물 수정 (로그인)
    @Transactional
    @Validated
    public void updateBoard(@Valid UpdateBoardLoginDto dto) {

        if (hasText(dto.getTitle()) || hasText(dto.getContent())) {
            Board board = getBoardWithPassword(dto.getId());
            checkMemberId(board.getMember().getId(), dto.getMember().getId(), false);
            updateTitleAndContent(dto.getTitle(), dto.getContent(), board);
        }

    }

    // 게시물 수정 (비로그인)
    @Transactional
    @Validated
    public void updateBoard(@Valid UpdateBoardNotLoginDto dto) {

        if (hasText(dto.getTitle()) || hasText(dto.getContent())) {
            Board board = getBoardWithPassword(dto.getId());
            checkPassword(dto.getPassword(), board.getPassword());
            updateTitleAndContent(dto.getTitle(), dto.getContent(), board);
        }

    }

    // 게시물 생성자와 요청한 멤버가 동일한지 판단
    private void checkMemberId(Long memberId, Long requestMemberId, boolean isDelete) {
        if (!memberId.equals(requestMemberId)) {
            String condition = isDelete ? "삭제" : "수정";
            String message = "게시물을 " + condition + "할 수 없습니다.";
            throw new IllegalArgumentException(message);
        }
    }

    // 해시된 비밀번호와 순수한 비밀번호와 동일한지 판단
    private void checkPassword(String plainPassword, String hashed) {
        boolean isMatch = BCrypt.checkpw(plainPassword, hashed);
        if (!isMatch) {
            throw new IllegalArgumentException("비밀번호를 다시 확인해 주세요.");
        }
    }

    /**
     * PK로 게시물 조회
     * - SELECT * FROM board WHERE baord.board_id = :boardId
     */
    public Board getBoardWithPassword(Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new NullPointerException("게시물을 찾을 수 없습니다."));
    }

    /**
     * PK로 비밀번호 컬럼만 조회
     * - 비밀번호가 Null이 아니면 로그인한 멤버가 생성한 게시물
     * - SELECT board.password FROM board WHERE board.board_id = :boardId
     */
    public boolean isLoggedIn(Long boardId) {
        CheckPasswordDto result = boardQueryRepository.findPassword(boardId);
        if (result == null) {
            throw new NullPointerException("게시물을 찾을 수 없습니다.");
        }
        return result.getPassword() == null;
    }

    // 제목, 내용이 존재하면 게시물을 수정
    private void updateTitleAndContent(String title, String content, Board board) {

        if (hasText(title)) {
            board.updateTitle(title);
        }

        if (hasText(content)) {
            board.updateContent(content);
        }

    }

    // 게시물 상세 조회
    // 조회 수 + 1
    @Transactional
    public BoardQueryDto getBoard(Long boardId, Member member) {

        // TODO : 조회수 업데이트 로직 메서드로 분리

        Board board = getBoardWithPassword(boardId);

        if (member == null) {
            board.updateViews();
        }

        if (member != null) {
            if (board.getMember().getId() != member.getId()) {
                board.updateViews();
            }
            // TODO : 작성자 IP와 요청 IP 비교
        }

        return board.toQueryDto();
    }

    // 게시물 리스트 쿼리 (검색 X)
    public Page<BoardAndCommentCount> getBoardList(Pageable pageable) {

        Page<BoardAndCommentCount> result = boardQueryRepository.getBoardList(pageable);

        if (result.getTotalElements() != 0 && pageable.getPageNumber() >= result.getTotalPages()) {
            Pageable newPageable = PageRequest.of(result.getTotalPages() - 1, pageable.getPageSize());
            return boardQueryRepository.getBoardList(newPageable);
        }

        return result;
    }

    // 게시물 검색 쿼리
    // 닉네임, 제목, 내용, 생성 날짜 정렬
    public Page<BoardAndCommentCount> searchBoard(BoardSearchCondition condition, Pageable pageable) {

        Page<BoardAndCommentCount> result = boardQueryRepository.searchBoard(condition, pageable);

        if (result.getTotalElements() != 0 && pageable.getPageNumber() >= result.getTotalPages()) {
            Pageable newPageable = PageRequest.of(result.getTotalPages() - 1, pageable.getPageSize());
            return boardQueryRepository.searchBoard(condition, newPageable);
        }

        return result;
    }

}
