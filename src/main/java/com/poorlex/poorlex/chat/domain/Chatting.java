package com.poorlex.poorlex.chat.domain;

import com.poorlex.poorlex.common.BaseCreatedAtEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Chatting extends BaseCreatedAtEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "battle_id")
    private Long battleId;

    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "content")
    private String content;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private MessageType type;

    public Chatting(final Long id, final Long battleId, final Long memberId, final String content, final MessageType type) {
        this.id = id;
        this.battleId = battleId;
        this.memberId = memberId;
        this.content = content;
        this.type = type;
    }

    public static Chatting withoutId(final Long battleId, final Long memberId, final MessageType type, final String content) {
        return new Chatting(null, battleId, memberId, content, type);
    }
}
