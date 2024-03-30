package com.poorlex.poorlex.user.member.service;

import com.poorlex.poorlex.battle.domain.Battle;
import com.poorlex.poorlex.battle.domain.BattleBudget;
import com.poorlex.poorlex.battle.domain.BattleRepository;
import com.poorlex.poorlex.battle.domain.BattleStatus;
import com.poorlex.poorlex.battle.domain.BattleType;
import com.poorlex.poorlex.battle.fixture.BattleFixture;
import com.poorlex.poorlex.battle.service.dto.response.BattleSuccessCountResponse;
import com.poorlex.poorlex.battlesuccess.domain.BattleSuccessHistory;
import com.poorlex.poorlex.battlesuccess.domain.BattleSuccessHistoryRepository;
import com.poorlex.poorlex.expenditure.domain.Expenditure;
import com.poorlex.poorlex.expenditure.domain.ExpenditureRepository;
import com.poorlex.poorlex.expenditure.fixture.ExpenditureFixture;
import com.poorlex.poorlex.expenditure.service.dto.response.MyPageExpenditureResponse;
import com.poorlex.poorlex.fixture.MemberFixture;
import com.poorlex.poorlex.friend.domain.Friend;
import com.poorlex.poorlex.friend.domain.FriendRepository;
import com.poorlex.poorlex.friend.service.dto.response.FriendResponse;
import com.poorlex.poorlex.participate.domain.BattleParticipant;
import com.poorlex.poorlex.participate.domain.BattleParticipantRepository;
import com.poorlex.poorlex.support.IntegrationTest;
import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import com.poorlex.poorlex.user.member.domain.Member;
import com.poorlex.poorlex.user.member.domain.MemberLevel;
import com.poorlex.poorlex.user.member.domain.MemberRepository;
import static com.poorlex.poorlex.user.member.domain.Oauth2RegistrationId.APPLE;
import com.poorlex.poorlex.user.member.service.dto.ExpenditureDto;
import com.poorlex.poorlex.user.member.service.dto.response.MyPageResponse;
import com.poorlex.poorlex.user.point.domain.MemberPoint;
import com.poorlex.poorlex.user.point.domain.MemberPointRepository;
import com.poorlex.poorlex.user.point.domain.Point;
import java.time.LocalDate;
import java.util.List;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("회원 정보 조회 테스트")
class MemberQueryServiceTest extends IntegrationTest implements ReplaceUnderScoreTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberPointRepository memberPointRepository;

    @Autowired
    private MemberQueryService memberQueryService;

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
    void 멤버의_마이페이지_정보를_전달한다() {
        //given
        final Member 스플릿 = MemberFixture.saveMemberWithOauthId(memberRepository, APPLE, "고유 ID1", "스플릿", "소개");
        final Member 푸얼렉스 = MemberFixture.saveMemberWithOauthId(memberRepository, APPLE, "고유 ID2", "푸얼렉스", "소개");
        두_회원이_친구를_맺는다(스플릿, 푸얼렉스);

        final Battle 난이도_HARD_배틀 = 배틀을_생성한다(40000);
        회원이_배틀에_참가한다(스플릿, 난이도_HARD_배틀);
        회원이_배틀에_참가한다(푸얼렉스, 난이도_HARD_배틀);

        final LocalDate 배틀_시작_날짜 = LocalDate.from(난이도_HARD_배틀.getDuration().getStart());
        final Expenditure 스플릿_첫번쨰_지출 = 지출을_생성한다(10000L, 스플릿.getId(), 배틀_시작_날짜);
        final Expenditure 스플릿_두번쨰_지출 = 지출을_생성한다(20000L, 스플릿.getId(), 배틀_시작_날짜);
        final Expenditure 스플릿_세번쨰_지출 = 지출을_생성한다(30000L, 스플릿.getId(), 배틀_시작_날짜);
        final Expenditure 스플릿_네번쨰_지출 = 지출을_생성한다(40000L, 스플릿.getId(), 배틀_시작_날짜);
        final Expenditure 스플릿_다섯번쨰_지출 = 지출을_생성한다(50000L, 스플릿.getId(), 배틀_시작_날짜);
        final Expenditure 푸얼렉스_첫번쨰_지출 = 지출을_생성한다(10000L, 푸얼렉스.getId(), 배틀_시작_날짜);

        final int 스플릿_배틀_등수 = 1;
        회원이_배틀에_해당_순위로_성공한다(스플릿, 난이도_HARD_배틀, 스플릿_배틀_등수);

        //when
        final MyPageResponse myPageInfo = memberQueryService.getMyPageInfo(스플릿.getId(), 배틀_시작_날짜);

        //then
        final int 배틀_성공_점수 = 난이도_HARD_배틀.getBattleType().getScore(스플릿_배틀_등수);
        final int 회원_레벨_숫자 = myPageInfo.getLevelInfo().getLevel();
        final int 회원_총_포인트 = myPageInfo.getLevelInfo().getPoint();
        final Integer 레벨업을_위해_필요한_포인트 = myPageInfo.getLevelInfo().getPointLeftForLevelUp();
        final MemberLevel 회원_레벨 = MemberLevel.findByNumber(회원_레벨_숫자).get();
        final List<FriendResponse> 회원_친구_목록 = myPageInfo.getFriends();
        final BattleSuccessCountResponse 회원_배틀_성공_기록 = myPageInfo.getBattleSuccessInfo();

        final List<MyPageExpenditureResponse> 예상_최근_지출_4개 = List.of(
                MyPageExpenditureResponse.from(mapFromExpenditure(스플릿_다섯번쨰_지출)),
                MyPageExpenditureResponse.from(mapFromExpenditure(스플릿_네번쨰_지출)),
                MyPageExpenditureResponse.from(mapFromExpenditure(스플릿_세번쨰_지출)),
                MyPageExpenditureResponse.from(mapFromExpenditure(스플릿_두번쨰_지출))
        );

        assertSoftly(
                softly -> {
                    softly.assertThat(회원_레벨_숫자).isOne();
                    softly.assertThat(회원_총_포인트).isEqualTo(배틀_성공_점수);
                    softly.assertThat(레벨업을_위해_필요한_포인트).isEqualTo(회원_레벨.getGetPointForNextLevel(회원_총_포인트));

                    softly.assertThat(myPageInfo.getFriendTotalCount()).isOne();
                    softly.assertThat(회원_친구_목록).hasSize(1);
                    softly.assertThat(회원_친구_목록.get(0).getLevel()).isOne();
                    softly.assertThat(회원_친구_목록.get(0).getWeeklyExpenditure()).isEqualTo(푸얼렉스_첫번쨰_지출.getAmount());
                    softly.assertThat(회원_친구_목록.get(0).getNickname()).isEqualTo(푸얼렉스.getNickname());

                    softly.assertThat(회원_배틀_성공_기록.getTotalBattleSuccessCount()).isOne();
                    softly.assertThat(회원_배틀_성공_기록.getHardBattleSuccessCount()).isOne();
                    softly.assertThat(회원_배틀_성공_기록.getEasyBattleSuccessCount()).isZero();
                    softly.assertThat(회원_배틀_성공_기록.getNormalBattleSuccessCount()).isZero();

                    softly.assertThat(myPageInfo.getExpenditureTotalCount()).isEqualTo(5);
                    softly.assertThat(myPageInfo.getExpenditures())
                            .usingRecursiveComparison()
                            .isEqualTo(예상_최근_지출_4개);
                }
        );
    }

    private void 두_회원이_친구를_맺는다(final Member member, final Member friend) {
        friendRepository.save(Friend.withoutId(member.getId(), friend.getId()));
    }

    private Expenditure 지출을_생성한다(final Long amount, final Long memberId, final LocalDate date) {
        return expenditureRepository.save(ExpenditureFixture.simpleWithMainImage(amount, memberId, date));
    }

    private Battle 배틀을_생성한다(final int 예산) {
        return battleRepository.save(BattleFixture.initialBattleBuilder()
                                             .status(BattleStatus.PROGRESS)
                                             .budget(new BattleBudget(예산))
                                             .build());
    }

    private void 회원이_배틀에_참가한다(final Member member, final Battle battle) {
        final BattleParticipant battleParticipant = BattleParticipant.normalPlayer(battle.getId(), member.getId());
        battleParticipantRepository.save(battleParticipant);
    }

    private void 회원이_배틀에_해당_순위로_성공한다(final Member 회원, final Battle 참가중인_배틀, final int 순위) {
        // 배틀 성공 기록 저장
        final BattleSuccessHistory 배틀_성공_기록 = BattleSuccessHistory.withoutId(회원.getId(),
                                                                             참가중인_배틀.getId(),
                                                                             참가중인_배틀.getDifficulty());
        battleSuccessHistoryRepository.save(배틀_성공_기록);

        // 순위에 따른 배틀 성공 포인트 지급
        final BattleType 배틀_타입 = 참가중인_배틀.getBattleType();
        final int 배틀_성공_점수 = 배틀_타입.getScore(순위);
        memberPointRepository.save(MemberPoint.withoutId(new Point(배틀_성공_점수), 회원.getId()));
    }

    private ExpenditureDto mapFromExpenditure(final Expenditure expenditure) {
        return new ExpenditureDto(expenditure.getId(),
                                  expenditure.getDate(),
                                  expenditure.getAmount(),
                                  expenditure.getMainImageUrl());
    }
}
