package com.poorlex.refactoring.battle.invitation.service.provider;

public interface FriendExistenceProvider {

    boolean isExist(final Long firstMemberId, final Long secondMemberId);
}
