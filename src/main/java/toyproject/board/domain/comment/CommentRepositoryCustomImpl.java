package toyproject.board.domain.comment;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import toyproject.board.dto.comment.CommentNoPw;
import toyproject.board.dto.comment.QCommentNoPw;

import java.util.List;

import static toyproject.board.domain.comment.QComment.comment;

@RequiredArgsConstructor
public class CommentRepositoryCustomImpl implements CommentRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<CommentNoPw> getCommentsByBoardId(Long boardId) {
        return queryFactory
                .select(new QCommentNoPw(
                        comment.id,
                        comment.content,
                        comment.nickname,
                        comment.createdDate,
                        comment.lastModifiedDate
                ))
                .from(comment)
                .where(comment.board.id.eq(boardId))
                .fetch();
    }

}
