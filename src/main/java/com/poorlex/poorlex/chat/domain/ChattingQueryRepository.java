package com.poorlex.poorlex.chat.domain;

import com.poorlex.poorlex.chat.service.dto.response.ChatHistoryResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.poorlex.poorlex.chat.domain.QChatting.chatting;

@Repository
@RequiredArgsConstructor
public class ChattingQueryRepository {

    private final JPAQueryFactory queryFactory;

    public List<ChatHistoryResponse> findByBattleId(Long battleId, Pageable pageable) {
        return queryFactory.select(Projections.constructor(ChatHistoryResponse.class,
                            chatting.memberId,
                            chatting.content,
                            chatting.type
                        )).from(chatting)
                .where(chatting.battleId.eq(battleId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(chatting.createdAt.desc())
                .fetch();
    }
}
