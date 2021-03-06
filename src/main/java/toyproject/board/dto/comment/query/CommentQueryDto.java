package toyproject.board.dto.comment.query;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

// 댓글 쿼리 DTO
@Getter
@Setter
public class CommentQueryDto {

    private Long commentId;
    private String content;
    private String nickname;
    private Long memberId;
    private Long boardId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastModifiedDate;

    @Builder
    @QueryProjection
    public CommentQueryDto(Long commentId, String content, String nickname, Long memberId, Long boardId, LocalDateTime createdDate, LocalDateTime lastModifiedDate) {
        this.commentId = commentId;
        this.content = content;
        this.nickname = nickname;
        this.memberId = memberId;
        this.boardId = boardId;
        this.createdDate = createdDate;
        this.lastModifiedDate = lastModifiedDate;
    }
}
