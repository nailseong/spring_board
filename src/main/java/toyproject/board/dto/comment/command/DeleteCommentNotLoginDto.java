package toyproject.board.dto.comment.command;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

// 비로그인 댓글 삭제 DTO
@Getter
@Setter
public class DeleteCommentNotLoginDto {

    @NotNull
    private Long id;

    @NotBlank
    private String password;

    @Builder
    public DeleteCommentNotLoginDto(Long id, String password) {
        this.id = id;
        this.password = password;
    }

}
