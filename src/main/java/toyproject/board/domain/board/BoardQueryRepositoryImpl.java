package toyproject.board.domain.board;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import toyproject.board.dto.board.*;

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

    @Override
    public Page<BoardNoPw> searchBoard(BoardSearchCondition condition, Pageable pageable) {
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
                .where(nicknameLike(condition.getNickname()),
                        titleLike(condition.getTitle()),
                        contentLike(condition.getContent()))
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .orderBy(createdDateAsc(condition.getIsAsc()))
                .fetchResults();

        List<BoardNoPw> content = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public CheckPasswordDto findPassword(Long boardId) {
        return queryFactory
                .select(new QCheckPasswordDto(
                        board.id,
                        board.password
                ))
                .from(board)
                .where(board.id.eq(boardId))
                .fetchOne();
    }

    private Predicate nicknameLike(String nickname) {
        return nickname != null
                ? board.nickname.like("%" + nickname + "%")
                : null;
    }

    private Predicate titleLike(String title) {
        return title != null
                ? board.title.like("%" + title + "%")
                : null;
    }

    private Predicate contentLike(String content) {
        return content != null
                ? board.content.like("%" + content + "%")
                : null;
    }

    private OrderSpecifier<?> createdDateAsc(Boolean isAsc) {
        return isAsc != null
                ? isAsc
                    ? board.createdDate.asc()
                    : board.createdDate.desc()
                : board.createdDate.asc();
    }

    private Predicate memberIdEq(Long memberId) {
        return memberId != null
                ? board.member.id.eq(memberId)
                : null;
    }

}
