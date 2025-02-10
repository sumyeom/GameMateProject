package com.example.gamemate.domain.board.repository;

import com.example.gamemate.domain.board.entity.Board;
import com.example.gamemate.domain.board.entity.QBoard;
import com.example.gamemate.domain.board.enums.BoardCategory;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

@RequiredArgsConstructor
public class BoardQuerydslRepositoryImpl implements BoardQuerydslRepository {
    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 게시글 조회
     * @param category
     * @param title
     * @param content
     * @return
     */
    @Override
    public Page<Board> searchBoardQuerydsl(BoardCategory category, String title, String content, Pageable pageable) {

        QBoard board = QBoard.board;

        BooleanBuilder builder = new BooleanBuilder();

        if(category != null) {
            builder.and(board.category.eq(category));
        }

        if(title != null) {
            builder.and(board.title.like("%"+title+"%"));
        }

        if(content != null) {
            builder.and(board.content.like("%"+content+"%"));
        }

        JPAQuery<Board> query = jpaQueryFactory.selectFrom(board)
                .where(builder)
                .orderBy(new OrderSpecifier<>(Order.DESC, board.createdAt))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        List<Board> contentList = query.fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory.select(board.count())
                .from(board)
                .where(builder);

        return PageableExecutionUtils.getPage(contentList, pageable, countQuery::fetchOne);
    }
}
