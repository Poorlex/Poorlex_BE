package com.poorlex.poorlex.bridge;

import com.poorlex.poorlex.config.event.Events;
import com.poorlex.poorlex.consumption.expenditure.service.event.ExpenditureMemberDeletedEvent;
import com.poorlex.poorlex.user.member.service.event.MemberDeletedEvent;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

public class EventConvertor {

    @TransactionalEventListener(classes = {MemberDeletedEvent.class}, phase = TransactionPhase.BEFORE_COMMIT)
    public void handle(final MemberDeletedEvent event) {
        Events.raise(new ExpenditureMemberDeletedEvent(event.getMemberId()));
    }
}
