package com.poorlex.poorlex.friend.domain;

import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import com.poorlex.poorlex.support.db.UsingDataJpaTest;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class FriendRepositoryTest extends UsingDataJpaTest implements ReplaceUnderScoreTest {

    @Autowired
    private FriendRepository friendRepository;

    @BeforeEach
    void setUp() {
        initializeDataBase();
    }

    @Test
    void Id를_포함한_모든_친구를_조회한다() {
        //given
        final long memberId = 1L;
        final long friend1Id = 2L;
        final long friend2Id = 3L;

        final Friend firstFriendShip = Friend.withoutId(memberId, friend1Id);
        final Friend secondFriendShip = Friend.withoutId(friend2Id, memberId);

        friendRepository.save(firstFriendShip);
        friendRepository.save(secondFriendShip);

        //when
        final List<Friend> friends = friendRepository.findFriendsByMemberId(memberId);

        //then
        final List<Friend> expectedFriends = List.of(firstFriendShip, secondFriendShip);

        assertThat(friends).hasSize(2);
        assertThat(friends).usingRecursiveAssertion()
                .ignoringFields("id")
                .isEqualTo(expectedFriends);
    }
}
