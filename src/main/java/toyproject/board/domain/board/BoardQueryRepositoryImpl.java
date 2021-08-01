package toyproject.board.domain.board;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import toyproject.board.dto.board.BoardNoPw;
import toyproject.board.dto.board.QBoardNoPw;

import java.util.List;

import static toyproject.board.domain.board.QBoard.board;

@RequiredArgsConstructor
public class BoardQueryRepositoryImpl implements BoardQueryRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public BoardNoPw findNoPasswordById(Long boardId) {
        return queryFactory
                .select(new QBoardNoPw(
                        board.id,
                        board.title,
                        board.content,
                        board.nickname,
                        board.member.id,
                        board.createdDate,
                        board.lastModifiedDate
                ))
                .from(board)
                .where(board.id.eq(boardId))
                .fetchOne();
    }

    @Override
    public Page<BoardNoPw> findAllNoPassword(Long memberId, Pageable pageable) {
        QueryResults<BoardNoPw> results = queryFactory
                .select(new QBoardNoPw(
                        board.id,
                        board.title,
                        board.content,
                        board.nickname,
                        board.member.id,
                        board.createdDate,
                        board.lastModifiedDate
                ))
                .from(board)
                .where(memberIdEq(memberId))
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .orderBy(board.id.asc())
                .fetchResults();

        List<BoardNoPw> content = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
    }

    private Predicate memberIdEq(Long memberId) {
        return memberId != null
                ? board.member.id.eq(memberId)
                : null;
    }

}
