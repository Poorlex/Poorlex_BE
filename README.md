# PoorLex 도메인

# 배틀

- **배틀 ( Battle )**

  **배틀방 고유 번호 [ 필수 ]( BattleId )**

    - 고유 번호는 숫자이다.

  **배틀 제목 [ 필수 | 입력값 ] ( BattleName )**

    - 제목의 길이는 2이상 12이하이다.
    - 제목에 포함될 수 있는 문자
        - 한글
        - 영어
        - 특수문자
        - 이모티콘
    - 공백만 있는 경우는 입력값이 없는 것으로 간주한다.
    - 앞 뒤 공백은 제거하고 저장한다.

  **배틀 소개글 [ 필수 | 입력값 ] ( BattleIntroduction )**

    - 소개글의 길이는 2이상 200이하이다.
    - 제목의 길이는 2이상 12이하이다.
    - 제목에 포함될 수 있는 문자
        - 한글
        - 영어
        - 특수문자
        - 이모티콘
    - 공백만 있는 경우는 입력값이 없는 것으로 간주한다.
    - 앞 뒤 공백은 제거하고 저장한다.

  **배틀 이미지 [ 필수 | 입력값 ] ( BattleImageUrl )**

    - 이미지를 저장하는 방식은 링크이다.

  **배틀 예산 [ 필수 | 입력값 ] ( BattleBudget )**

    - 배틀 예산은 최소 1만원, 최대 20만원이다.
    - 배틀 예산은 만원 단위이다.

  **배틀 난이도 [ 필수 ]( BattleDifficulty )**

    - 난이도에는 `쉬움`, `보통`, `어려움` 3개가 있다.
    - 난이도는 예산의 양으로 정해진다.
        - 쉬움 : 15만원 ~ 20만원
        - 보통 : 9만원 ~ 14만원
        - 어려움 : 1만원 ~ 8만원

  **배틀 최대 참가자 수 [ 필수 | 입력값 ] ( BattleParticipantSize )**

    - 참가자 수는 1이상 10이하이다.
    - 참가자 수에 따라 두가지 타입을 가진다. **( BattleType )**
        - 작음 : 1 ~ 5
        - 큼 : 6 ~ 10

          ![점수 표.png](%E1%84%83%E1%85%A9%E1%84%86%E1%85%A6%E1%84%8B%E1%85%B5%E1%86%AB%20%E1%84%87%E1%85%A7%E1%86%AF%20%E1%84%86%E1%85%AE%E1%86%AB%E1%84%89%E1%85%A5%E1%84%92%E1%85%AA%2076b6664da7674bc4bc8e825835abbec7/%25E1%2584%258C%25E1%2585%25A5%25E1%2586%25B7%25E1%2584%2589%25E1%2585%25AE_%25E1%2584%2591%25E1%2585%25AD.png)

    **배틀 상태 [ 필수 ] ( BattleStatus )**
    
    - 배틀의 상태는 `모집 중`, `모집 완료`, `진행중`, `완료` 4개가 존재한다.
    - 모집 중 ( 배틀 시작 전 )
        - 최대 참가자 수보다 참여자의 수가 적을 때
    - 모집 완료 ( 배틀 시작 전 )
        - 최대 참가자 수와 참여자의 수가 같을 때
    - 진행 중 ( 배틀 시작 후 )
        - 월요일 09:00 가 되면 배틀이 시작
    - 완료 ( 배틀 시작 후 )
        - 일요일 22:00 가 되면 배틀이 종료
    
    **배틀 기간 [ 필수 ] ( BattleDuration )**
    
    - 시작일 : 월요일 9:00
    - 종료일 : 일요일 22:00

- **배틀(Battle) 의 기능**

  **배틀 시작**

    - **배틀은 준비 중, 준비 완료시에만 시작할 수 있다.**
    - **배틀은 반드시 월요일 9:00 에 시작된다.**
        - 모든 배틀의 시작 처리가 되는 시간을 고려하여 Minute 까지는 검증하지 않는다.

  **배틀 종료**

    - **배틀은 진행 중에만 완료할 수 있다.**
    - **배틀은 반드시 일요일 22:00 에 종료된다.**
        - 모든 배틀의 종료 처리가 되는 시간을 고려하여 Minute 까지는 검증하지 않는다.

  **참가자 랭킹 조회**

    - **매니저를 포함한 배틀 참가자들을 지출이 적은 순으로 정렬하여 등수를 정한다. ( 1등, 2등 … )**
        - 지출이 같을 경우 같은 등수가 된다.

# 배틀 참가

- **배틀 참가자 ( BattleParticipant )**
    - **배틀 참가자 ID [ 필수 ]**
    - **참가 배틀 ID [ 필수 ]**
    - **참가자 멤버 ID [ 필수 ]**
    - **역할 ( BattleParticipantRole ) [ 필수 ]**
        - Normal Player : 일반 플레이어
        - Manager : 관리자 ( 방을 생성한 참가자 )
- **배틀 참가 기능**

  **사용자 참가**

    - **배틀이 진행중이거나 완료된 경우에는 참가할 수 없다.**
    - **배틀의 최대 참가자 수보다 현재 참가자 수(매니저 포함)가 적을 때만 참가할 수 있다.**
    - **배틀에 동일한 참여자의 중복 참여는 불가하다.**
    - **배틀의 최대 참가자 수만큼 참가시 준비 완료로 상태를 변경한다.**

  **사용자 참가 취소**

    - **배틀 진행 중에는 참가 취소를 할 수 없다.**
    - **배틀 관리자는 참가 취소를 할 수 없다.**
    - **배틀의 상태가 준비 완료 상태였다면 준비 중으로 변경한다.**

# 배틀 공지 **( Notification )**

- **배틀 공지 ( BattleNotification )**

  **공지 내용 [ 필수 | 입력값 ] ( BattleNotificationContent )**

    - 공지 내용에 포함될 수 있는 문자
        - 한글
        - 영어
        - 특수문자
        - 이모티콘
    - 공지 내용의 길이는 20이상 200이하이다.

  **공지 이미지 [ 옵션 | 입력값 ]( BattleNotificationImageUrl )**

    - 공지 이미지는 선택사항이다.
    - 이미지의 개수는 1개이다.
- **배틀 공지의 기능**

  **공지 내용 수정**

    - 같은 배틀에 참가중인 관리자만 변경할 수 있다.
    - 종료된 배틀의 공지는 변경할 수 없다.

  **공지 이미지 추가, 변경**

    - 같은 배틀에 참가중인 관리자만 추가, 변경할 수 있다.
    - 종료된 배틀의 공지는 변경할 수 없다.

# 회원 ( Member )

- **회원**

  **회원 고유 번호 [ 필수 ]( MemberId )**

    - 고유 번호는 숫자이다.

  **닉네임 [ 필수 | 입력값 ] ( MemberNickname )**

    - 닉네임의 길이는 2자 이상 15자 이내이다.
    - 닉네임은 한글, 영문자, 숫자, 특수 기호 를 포함할 수 있다.
        - 특수 기호는 “-”, “_” 만 사용할 수 있다.
        - 정규식 : `"[가-힣a-zA-Z0-9_-]+"`
    - 닉네임은 중복이 가능하다.

  **레벨 ( MemberLevel )**

    - 레벨에는 1부터 5까지의 5단계가 있다.
    - 포인트를 통해서 레벨이 결정된다.
        - Lv1 : 0 ~ 69
        - Lv2 : 70 ~ 189
        - Lv3 : 190 ~ 599
        - Lv4 : 600 ~ 1439
        - Lv5 : 1440 ~

  **포인트 ( MemberPoint )**

    - 포인트는 0 이상의 정수이다.
    - 초기 포인트는 0 이다.
    - 포인트의 최대값은 존재하지 않는다.
- **회원(Member)의 기능**

  **닉네임을 변경한다**

  **레벨 조회**

    - 포인트를 기반으로 레벨을 조회한다.

# 포인트

- **멤버 포인트 ( MemberPoint )**
    - 추가하는 포인트는 음수일 수 없다.
- **포인트 기능**
    - 포인트 추가
    - 멤버의 총 포인트 조회

# 투표 ( Vote )

- **투표 정보 ( VoteInfo )**

  **투표 제목 [ 필수 | 입력값 ] ( VoteName )**

    - 제목의 길이는 1이상 12이하이다.
        - 공백만 있는 경우는 어떻게 처리하는가?
    - 제목에 포함될 수 있는 문자의 종류는 ??? 이다.

  **투표 금액 [ 필수 | 입력값 ] ( VoteAmount )**

    - 금액은 최소 0원 이상 9,999,999 이하인 숫자다.

  **투표 기간 [ 필수 | 입력값 ] ( VoteDuration )**

    - 투표기간은 총 5가지 이다.
        - 5분
        - 10분
        - 20분
        - 30분
        - 1시간

  **투표 진행 상태 ( VoteStatus )**

    - 진행 상태는 총 2가지 이다.
        - 진행 중
        - 완료
- **투표된 표들 ( Votings )**

  **투표된 표 ( Voting )**

    - 투표한 참가자 ( Voter )
        - 투표한 참가자의 고유 번호를 가진다.
    - 투표 종류 ( VotingType )
        - 찬성
        - 반대
- **투표(Vote) 의 기능**

  **투표에 표를 추가한다.**

    - 같은 참가자의 표가 있으면 추가할 수 없다.

  **현시점 투표 결과를 조회한다. ( VoteResult )**

    - 찬성한 표의 수를 조회한다.
    - 반대한 표의 수를 조회한다.

  **투표를 종료한다.**

    - 종료 시점의 시간이 투표 종료시간 이전이라면 종료할 수 없다.

# 채팅

- 채팅

  **채팅 작성자 프로필**

    - 작성자 캐릭터
    - 작성자 레벨
    - 작성자 닉네임

  **채팅 내용**

  **채팅 작성 시간**

    - 작성 시간의 형식은 “[ 오전 | 오후 ]hh:mm” 이다. (ex. 오후 3:49, 오전 11:29 )
- 반응 ( 혼내기, 칭찬하기 )

  **혼내기**

    - 사유를 입력한다.
    - 사유는 반드시 필요하다.
    - 사유는 한글로만 이루어진 문자열이다.
    - 사유의 최대 길이는 30이다.
        - 길이에 공백이 포함된다.
    - 공백만 있는 경우는 어떻게 처리하는가??

  **칭찬하기**

    - 사유를 입력한다.
    - 사유는 반드시 필요하다.
    - 사유는 한글로만 이루어진 문자열이다.
    - 사유의 최대 길이는 30이다.
        - 길이에 공백이 포함된다.
    - 공백만 있는 경우는 어떻게 처리하는가??

# 목표 ( Goal )

- 목표

  **가치**

    - 가치는 반드시 필요하다.
    - 가치에는 5가지가 있다.
        - 안정된 미래
        - 부귀영화
        - 스트레스 해소
        - 일적인 성공
        - 휴식과 리프레시

  **목표명**

    - 목표명은 반드시 필요하다.
    - 목표명은 길이가 16이하인 문자열이다.
        - 공백으로만 이루어진 경우에는 어떻게 되는가??
        - 가치별로 추천 목표이 존재한다.

  **목표 금액**

    - 목표 금액은 반드시 필요하다.
    - 목표 금액은 최소 0원 이상 9,999,999 이하 이다.

  **목표 기간**

    - 목표 기간은 반드시 필요하다
    - 목표 기간에는 시작일과 종료일이 존재한다.
        - yyyy.mm.dd 형식을 가진다.

  **목표 종류**

    - 목표 종류는 목표 기간에 따라 정해진다.
    - 목표 종류로는 `단기`, `중기`, `장기` 가 있다.
        - 단기 : 12 개월 미만
        - 중기 : 1년 이상 5년 미만
        - 장기 : 5년 이상

  **목표 상태**

    - 목표 상태는 `진행`, `완료` 가 존재한다.

# 예산 ( Budget )

- 예산

  **예산 금액 [ 필수 | 입력값 ]**

    - 예산은 반드시 존재해야 한다.
    - 금액은 0 이상의 정수를 뜻한다.
    - 금액의 최대값은 9,999,999 이다.

  **예산 기간 [ 필수 ]**

    - 기간은 일주일이다.
        - 일주일은 월요일 09:00 ~ 일요일 10:00 를 뜻한다.

# 지출 ( Expenditure )

- 지출

  **지출 금액 [ 필수 | 입력값 ]**

    - 금액은 반드시 필요하다.
    - 금액은 0이상인 정수이다.
    - 금액의 최대값은 9,999,999 이다.

  **지출 일자 [ 필수 | 입력값 ]**

    - 지출 일자는 반드시 필요하다.

  **지출에 대한 메모 [ 입력값 ]**

    - 메모는 선택사항이다.
    - 메모는 길이가 30자 이하인 문자열이다.
    - 메모 포함될 수 있는 문자
        - 한글
        - 영어
        - 특수문자
        - 이모티콘
    - 공백만 있는 경우는 입력값이 없는 것으로 간주한다.
    - 앞 뒤 공백은 제거하고 저장한다.

  **지출 인증 이미지 [ 필수 | 입력값 ]**

    - 이미지는 반드시 필요하다.
    - 이미지는 최대 2개이다.

# 알림 (Alarm )

- 배틀 채팅 알림
    - **알림 타입 ( AlarmType )**
        - 배틀 참여자가 지출을 입력한 경우
        - 배틀 참여자가 당일 지출을 입력하지 않은 경우
        - 배틀 참여자가 예산을 초과하여 지출한 경우
        - 배틀 참여자가 지출이 없는 경우
        - 배틀의 공지사항이 변경된 경우
    - **알림 포함 배틀 Id**
    - **알림 생성 멤버 Id**
    - **알림 생성 시간 ( createdAt )**
- 멤버 개인 알림
    - **알림 타입**
        - 친구 요청이 올 경우 알림을 생성한다.
        - 방 초대 요청이 올 경우 알림을 생성한다.
        - 친구 요청이 올 경우 알림을 생성한다.
        - 배틀 방에서 강퇴된 경우 알림을 생성한다.
    - **알림 대상 멤버 Id**
    - **알림 생성 시간 ( createdAt )**

# 알림 반응 ( AlarmReaction )

- 배틀 알림 반응
    - **반응한 배틀 알림 Id [ 필수 ]**
        - 반응을 남길 수 없는 알림이 있다
            - 지출 입력 요청 알림
            - 공지사항 변경 알림
    - **배틀 알림 반응 타입 [ 필수 ]**
        - 혼내기
        - 칭찬하기
    - **배틀 알림 반응 문구 [ 필수 | 입력값 ]**
        - 문구에 포함될 수 있는 문자
            - 한글
            - 영어
            - 특수문자
            - 이모티콘
        - 문구는 반드시 2자 이상 30자 이하의 한글이다

## 입력값 공백 처리

- 공백문자는 space, tab 을 모두 포함한다.
- 공백으로만 이루어진 경우는 예외로 처리한다.
- 앞뒤에 공백이 있는 경우는 앞뒤 공백 제거한다.
- 중간에 공백이 연속하여 있는 경우는 정상적인 입력으로 처리한다.
