package com.poorlex.poorlex.member.service;

import com.poorlex.poorlex.battle.domain.Battle;
import com.poorlex.poorlex.battle.domain.BattleBudget;
import com.poorlex.poorlex.battle.domain.BattleRepository;
import com.poorlex.poorlex.battle.domain.BattleStatus;
import com.poorlex.poorlex.battle.fixture.BattleFixture;
import com.poorlex.poorlex.battlesuccess.domain.BattleSuccessHistory;
import com.poorlex.poorlex.battlesuccess.domain.BattleSuccessHistoryRepository;
import com.poorlex.poorlex.expenditure.domain.Expenditure;
import com.poorlex.poorlex.expenditure.domain.ExpenditureRepository;
import com.poorlex.poorlex.expenditure.fixture.ExpenditureFixture;
import com.poorlex.poorlex.expenditure.service.dto.response.ExpenditureResponse;
import com.poorlex.poorlex.expenditure.service.dto.response.MyPageExpenditureResponse;
import com.poorlex.poorlex.friend.domain.Friend;
import com.poorlex.poorlex.friend.domain.FriendRepository;
import com.poorlex.poorlex.member.domain.*;
import com.poorlex.poorlex.member.service.dto.request.MemberProfileUpdateRequest;
import com.poorlex.poorlex.member.service.dto.response.MyPageResponse;
import com.poorlex.poorlex.participate.domain.BattleParticipant;
import com.poorlex.poorlex.participate.domain.BattleParticipantRepository;
import com.poorlex.poorlex.support.IntegrationTest;
import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class MemberServiceTest extends IntegrationTest implements ReplaceUnderScoreTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberService memberService;

    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private ExpenditureRepository expenditureRepository;

    @Autowired
    private BattleRepository battleRepository;

    @Autowired
    private BattleSuccessHistoryRepository battleSuccessHistoryRepository;

    @Autowired
    private BattleParticipantRepository battleParticipantRepository;

    @Test
    void 멤버의_프로필을_업데이트한다() {
        //given
        final Member member = memberRepository.save(
            Member.withoutId(Oauth2RegistrationId.APPLE, "oauthId", new MemberNickname("nickname")));
        final MemberProfileUpdateRequest request = new MemberProfileUpdateRequest("newNickname", "newDescription");

        //when
        memberService.updateProfile(member.getId(), request);

        //then
        final Member updatedMember = memberRepository.findById(member.getId())
            .orElseThrow(IllegalArgumentException::new);
        assertThat(updatedMember.getNickname()).isEqualTo(request.getNickname());
        assertThat(updatedMember.getDescription()).isPresent()
            .get()
            .isEqualTo(request.getDescription());
    }

    @Test
    void 멤버의_프로필을_업데이트한다_닉네임이_null일_경우() {
        //given
        final Member prevMember = Member.withoutId(Oauth2RegistrationId.APPLE, "oauthId",
            new MemberNickname("nickname"));
        prevMember.changeDescription(new MemberDescription("description"));
        memberRepository.save(prevMember);
        final MemberProfileUpdateRequest request = new MemberProfileUpdateRequest(null, "newDescription");

        //when
        memberService.updateProfile(prevMember.getId(), request);

        //then
        final Member updatedMember = memberRepository.findById(prevMember.getId())
            .orElseThrow(IllegalArgumentException::new);
        assertThat(updatedMember.getNickname()).isEqualTo("nickname");
        assertThat(updatedMember.getDescription()).isPresent()
            .get()
            .isEqualTo(request.getDescription());
    }

    @Test
    void 멤버의_프로필을_업데이트한다_소개가_null일_경우() {
        //given
        final Member prevMember = Member.withoutId(Oauth2RegistrationId.APPLE, "oauthId",
            new MemberNickname("nickname"));
        prevMember.changeDescription(new MemberDescription("description"));
        memberRepository.save(prevMember);
        final MemberProfileUpdateRequest request = new MemberProfileUpdateRequest("newNickname", null);

        //when
        memberService.updateProfile(prevMember.getId(), request);

        //then
        final Member updatedMember = memberRepository.findById(prevMember.getId())
            .orElseThrow(IllegalArgumentException::new);
        assertThat(updatedMember.getNickname()).isEqualTo(request.getNickname());
        assertThat(updatedMember.getDescription()).isPresent()
            .get()
            .isEqualTo("description");
    }

    @Test
    void 멤버의_프로필을_업데이트한다_둘다_null일_경우() {
        //given
        final Member prevMember = Member.withoutId(Oauth2RegistrationId.APPLE, "oauthId",
            new MemberNickname("nickname"));
        prevMember.changeDescription(new MemberDescription("description"));
        memberRepository.save(prevMember);
        final MemberProfileUpdateRequest request = new MemberProfileUpdateRequest(null, null);

        //when
        memberService.updateProfile(prevMember.getId(), request);

        //then
        final Member updatedMember = memberRepository.findById(prevMember.getId())
            .orElseThrow(IllegalArgumentException::new);
        assertThat(updatedMember.getNickname()).isEqualTo("nickname");
        assertThat(updatedMember.getDescription()).isPresent()
            .get()
            .isEqualTo("description");
    }

    @Test
    void 멤버의_마이페이지_정보를_전달한다() {
        //given
        final Member member = createMember("oauthId1", "me");
        final Battle battle = createBattle(40000);
        join(member, battle);

        final LocalDateTime battleStartTime = battle.getDuration().getStart();

        final Expenditure myExpenditure1 = createExpenditure(10000, member.getId(), battleStartTime);
        final Expenditure myExpenditure2 = createExpenditure(20000, member.getId(), battleStartTime);
        successBattle(member, battle);

        final Member friend = createMember("oauthId2", "friend");
        createExpenditure(10000, friend.getId(), battleStartTime);

        beFriend(member, friend);

        //when
        final MyPageResponse myPageInfo = memberService.getMyPageInfo(member.getId(), battleStartTime);

        //then
        assertSoftly(
            softly -> {
                softly.assertThat(myPageInfo.getLevelInfo().getLevel()).isOne();
                softly.assertThat(myPageInfo.getLevelInfo().getPoint()).isEqualTo(0);
                softly.assertThat(myPageInfo.getLevelInfo().getPointLeftForLevelUp()).isEqualTo(70);

                softly.assertThat(myPageInfo.getFriendTotalCount()).isOne();
                softly.assertThat(myPageInfo.getFriends()).hasSize(1);
                softly.assertThat(myPageInfo.getFriends().get(0).getLevel()).isOne();
                softly.assertThat(myPageInfo.getFriends().get(0).getWeeklyExpenditure()).isEqualTo(10000);
                softly.assertThat(myPageInfo.getFriends().get(0).getNickname()).isEqualTo("friend");

                softly.assertThat(myPageInfo.getBattleSuccessInfo().getTotalBattleSuccessCount()).isOne();
                softly.assertThat(myPageInfo.getBattleSuccessInfo().getHardBattleSuccessCount()).isOne();
                softly.assertThat(myPageInfo.getBattleSuccessInfo().getEasyBattleSuccessCount()).isZero();
                softly.assertThat(myPageInfo.getBattleSuccessInfo().getNormalBattleSuccessCount()).isZero();

                final List<MyPageExpenditureResponse> expectExpenditures = List.of(
                    MyPageExpenditureResponse.from(ExpenditureResponse.from(myExpenditure1)),
                    MyPageExpenditureResponse.from(ExpenditureResponse.from(myExpenditure2))
                );
                softly.assertThat(myPageInfo.getExpenditureTotalCount()).isEqualTo(2);
                softly.assertThat(myPageInfo.getExpenditures())
                    .usingRecursiveComparison()
                    .isEqualTo(expectExpenditures);
            }
        );

    }

    private Member createMember(final String oauthId, final String nickname) {
        final Member member = Member.withoutId(Oauth2RegistrationId.APPLE, oauthId,
            new MemberNickname(nickname));
        return memberRepository.save(member);
    }

    private void beFriend(final Member member, final Member friend) {
        friendRepository.save(Friend.withoutId(member.getId(), friend.getId()));
    }

    private Expenditure createExpenditure(final int amount, final Long memberId, final LocalDateTime date) {
        return expenditureRepository.save(ExpenditureFixture.simpleWith(amount, memberId, date));
    }

    private Battle createBattle(final int budget) {
        return battleRepository.save(BattleFixture.initialBattleBuilder()
            .status(BattleStatus.PROGRESS)
            .budget(new BattleBudget(budget))
            .build()
        );
    }

    private void join(final Member member, final Battle battle) {
        final BattleParticipant battleParticipant = BattleParticipant.normalPlayer(battle.getId(), member.getId());
        battleParticipantRepository.save(battleParticipant);
    }

    private void successBattle(final Member member, final Battle battle) {
        battleSuccessHistoryRepository.save(
            BattleSuccessHistory.withoutId(member.getId(), battle.getId(), battle.getDifficulty()));
    }
}
