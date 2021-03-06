package toyproject.board.domain.board.query;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import toyproject.board.dto.CheckPasswordDto;
import toyproject.board.dto.QCheckPasswordDto;
import toyproject.board.dto.board.query.*;

import java.util.List;

import static toyproject.board.domain.board.QBoard.board;
import static toyproject.board.domain.comment.QComment.comment;

@RequiredArgsConstructor
public class BoardQueryRepositoryImpl implements BoardQueryRepositoryCustom {

    private final JPAQueryFactory queryFactory;

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

    @Override
    public Page<BoardAndCommentCount> getBoardList(Pageable pageable) {
        QueryResults<BoardAndCommentCount> results = queryFactory
                .select(new QBoardAndCommentCount(
                        board.id,
                        board.views,
                        board.title,
                        board.nickname,
                        board.member.id,
                        comment.id.count(),
                        board.createdDate,
                        board.lastModifiedDate
                ))
                .from(board)
                    .leftJoin(comment)
                    .on(comment.board.id.eq(board.id))
                .groupBy(board.id)
                .orderBy(board.createdDate.desc())
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetchResults();

        List<BoardAndCommentCount> content = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<BoardAndCommentCount> searchBoard(BoardSearchCondition condition, Pageable pageable) {
        QueryResults<BoardAndCommentCount> results = queryFactory
                .select(new QBoardAndCommentCount(
                        board.id,
                        board.views,
                        board.content,
                        board.nickname,
                        board.member.id,
                        comment.id.count(),
                        board.createdDate,
                        board.lastModifiedDate
                ))
                .from(board)
                .leftJoin(comment)
                .on(comment.board.id.eq(board.id))
                .where(nicknameLike(condition.getNickname()),
                        titleLike(condition.getTitle()),
                        contentLike(condition.getContent()),
                        memberIdEq(condition.getMemberId()))
                .groupBy(board.id)
                .orderBy(orderByCreatedDate(condition.getIsAsc()))
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetchResults();

        List<BoardAndCommentCount> content = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public List<Long> findAllBoardIdByMemberId(Long memberId) {
        return queryFactory
                .select(board.id)
                .from(board)
                .where(board.member.id.eq(memberId))
                .fetch();
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

    private OrderSpecifier<?> orderByCreatedDate(Boolean isAsc) {
        return isAsc == null || !isAsc
                ? board.createdDate.desc()
                : board.createdDate.asc();
    }

    private Predicate memberIdEq(Long memberId) {
        return memberId != null
                ? board.member.id.eq(memberId)
                : null;
    }

}
