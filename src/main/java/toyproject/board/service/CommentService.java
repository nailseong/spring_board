package toyproject.board.service;

import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import toyproject.board.domain.board.Board;
import toyproject.board.domain.board.BoardRepository;
import toyproject.board.domain.comment.Comment;
import toyproject.board.domain.comment.CommentRepository;
import toyproject.board.dto.comment.CreateCommentLoginDto;
import toyproject.board.dto.comment.CreateCommentNotLoginDto;

import javax.validation.Valid;

@RequiredArgsConstructor
@Validated
@Transactional(readOnly = true)
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;

    /*
    @Transactional
    @Validated({Login.class, Default.class})
    public Long createCommentLogin(@Valid CreateCommentRequestDto dto) {

        Board board = boardRepository.findById(dto.getBoardId())
                .orElseThrow(() -> new IllegalArgumentException("게시물을 찾을 수 없습니다."));

        dto.setNickname(dto.getMember().getUsername());

        Comment comment = dto.toEntity(board);
        commentRepository.save(comment);

        return comment.getId();
    }

    @Transactional
    @Validated({NotLogin.class, Default.class})
    public Long createCommentNotLogin(@Valid CreateCommentRequestDto dto) {

        Board board = boardRepository.findById(dto.getBoardId())
                .orElseThrow(() -> new IllegalArgumentException("게시물을 찾을 수 없습니다."));

        dto.setPassword(BCrypt.hashpw(dto.getPassword(), BCrypt.gensalt(10)));

        Comment comment = dto.toEntity(board);
        commentRepository.save(comment);

        return comment.getId();
    }
     */

    @Transactional
    @Validated
    public Long createComment(@Valid CreateCommentLoginDto dto) {

        Board board = boardRepository.findById(dto.getBoardId())
                .orElseThrow(() -> new IllegalArgumentException("게시물을 찾을 수 없습니다."));

        Comment comment = dto.toEntity(board);
        commentRepository.save(comment);

        return comment.getId();
    }

    @Transactional
    @Validated
    public Long createComment(@Valid CreateCommentNotLoginDto dto) {

        Board board = boardRepository.findById(dto.getBoardId())
                .orElseThrow(() -> new IllegalArgumentException("게시물을 찾을 수 없습니다."));

        dto.setPassword(BCrypt.hashpw(dto.getPassword(), BCrypt.gensalt(10)));

        Comment comment = dto.toEntity(board);
        commentRepository.save(comment);

        return comment.getId();
    }

}
