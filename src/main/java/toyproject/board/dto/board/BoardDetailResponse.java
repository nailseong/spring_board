package toyproject.board.dto.board;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import toyproject.board.dto.BasicResponseDto;

@Getter
@Setter
@SuperBuilder
public class BoardDetailResponse extends BasicResponseDto {

    private BoardDetail boardDetail;

}
