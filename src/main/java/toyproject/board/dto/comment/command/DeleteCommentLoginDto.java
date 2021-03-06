package toyproject.board.dto.comment.command;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import toyproject.board.domain.member.Member;

import javax.validation.constraints.NotNull;

// 로그인 댓글 삭제 DTO
@Getter
@Setter
public class DeleteCommentLoginDto {

    @NotNull
    private Long id;

    @NotNull
    private Member member;

    @Builder
    public DeleteCommentLoginDto(Long id, Member member) {
        this.id = id;
        this.member = member;
    }

}
