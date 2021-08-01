package toyproject.board.domain.board;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import toyproject.board.dto.board.BoardNoPw;

public interface BoardQueryRepositoryCustom {

    BoardNoPw findNoPasswordById(Long boardId);

    Page<BoardNoPw> findAllNoPassword(Long memberId, Pageable pageable);

}
