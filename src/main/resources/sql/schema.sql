--회원
create table if not exists member
(
    id                    bigint generated by default as identity,
    oauth2registration_id varchar(255) check (oauth2registration_id in ('KAKAO', 'APPLE')),
    oauth_id              varchar(100),
    description           varchar(300),
    nickname              varchar(255),
    constraint idx_registrationId_oauthId unique (oauth2registration_id, oauth_id),
    primary key (id)
);

create table if not exists member_point
(
    id         bigint generated by default as identity,
    point      integer,
    member_id  bigint,
    created_at datetime(6),
    primary key (id)
);

--지출
create table if not exists expenditure
(
    id             bigint generated by default as identity,
    member_id      bigint,
    amount         bigint,
    description    varchar(255),
    date           date,
    main_image_url text,
    sub_image_url  text,
    created_at     datetime(6),
    point_paid     boolean,
    primary key (id)
);

--배틀
create table if not exists battle
(
    id                        bigint generated by default as identity,
    name                      varchar(255),
    introduction              varchar(255),
    image_url                 text,
    budget                    integer,
    max_size                  integer,
    start_time                timestamp(6),
    end_time                  timestamp(6),
    status                    varchar(255) check (status in ('RECRUITING',
                                                             'RECRUITING_FINISHED',
                                                             'PROGRESS',
                                                             'COMPLETE')),
    is_battle_success_counted boolean,
    primary key (id)
);

create table if not exists battle_participant
(
    id        bigint generated by default as identity,
    battle_id bigint,
    member_id bigint,
    role      varchar(255) check (role in ('MANAGER', 'NORMAL_PLAYER')),
    primary key (id)
);

create table if not exists weekly_budget
(
    id         bigint generated by default as identity,
    amount     bigint,
    start_date date,
    end_date   date,
    member_id  bigint,
    primary key (id)
);

create table if not exists battle_notification
(
    id        bigint generated by default as identity,
    content   varchar(255),
    battle_id bigint,
    image_url text,
    primary key (id)
);

create table if not exists battle_alarm
(
    id         bigint generated by default as identity,
    battle_id  bigint,
    member_id  bigint,
    type       varchar(255) check (type in
                                   ('EXPENDITURE_CREATED',
                                    'EXPENDITURE_NEEDED',
                                    'OVER_BUDGET',
                                    'ZERO_EXPENDITURE',
                                    'PARTICIPANT_ESCAPE',
                                    'BATTLE_NOTIFICATION_CHANGED')),
    created_at timestamp(6),
    primary key (id)
);

create table if not exists alarm_reaction
(
    id         bigint generated by default as identity,
    alarm_id   bigint,
    member_id  bigint,
    content    varchar(255),
    type       varchar(255) check (type in ('PRAISE', 'SCOLD')),
    created_at timestamp(6),
    primary key (id)
);

create table if not exists battle_alarm_view_history
(
    id             bigint generated by default as identity,
    battle_id      bigint,
    member_id      bigint,
    last_view_time timestamp(6),
    primary key (id)
);

create table if not exists goal
(
    id         bigint generated by default as identity,
    member_id  bigint,
    name       varchar(255),
    amount     bigint,
    type       varchar(255) check (type in ('STABLE_FUTURE',
                                            'WEALTH_AND_HONOR',
                                            'STRESS_RESOLVE',
                                            'SUCCESSFUL_AT_WORK',
                                            'REST_AND_REFRESH')),
    status     varchar(255) check (status in ('PROGRESS', 'FINISH')),
    start_date date,
    end_date   date,
    primary key (id)
);

create table if not exists friend
(
    id               bigint generated by default as identity,
    first_member_id  bigint,
    second_member_id bigint,
    primary key (id)
);

create table if not exists member_alarm
(
    id         bigint generated by default as identity,
    member_id  bigint,
    target_id  bigint,
    type       varchar(255) check (type in
                                   ('FRIEND_INVITATION_NOT_ACCEPTED',
                                    'FRIEND_INVITATION_ACCEPTED',
                                    'FRIEND_INVITATION_DENIED',
                                    'FRIEND_ACCEPTED',
                                    'BATTLE_INVITATION_NOT_ACCEPTED',
                                    'BATTLE_INVITATION_ACCEPTED',
                                    'BATTLE_INVITATION_ACCEPT',
                                    'BATTLE_INVITATION_DENIED',
                                    'BATTLE_KICKED_OUT')),
    created_at timestamp(6),
    primary key (id)
);

create table if not exists alarm_allowance
(
    id                  bigint generated by default as identity,
    member_id           bigint,
    battle_chat         boolean,
    battle_invite       boolean,
    battle_status       boolean,
    expenditure_request boolean,
    friend              boolean,
    primary key (id)
);

create table if not exists vote
(
    id        bigint generated by default as identity,
    battle_id bigint,
    member_id bigint,
    name      varchar(255),
    amount    bigint,
    start     timestamp(6),
    status    varchar(255) check (status in ('PROGRESS', 'FINISHED')),
    type      varchar(255) check (type in
                                  ('FIVE_MINUTE',
                                   'TEN_MINUTE',
                                   'TWENTY_MINUTE',
                                   'THIRTY_MINUTE',
                                   'SIXTY_MINUTE')),
    primary key (id)
);

create table if not exists voting_paper
(
    id              bigint generated by default as identity,
    vote_id         bigint,
    voter_member_id bigint,
    type            varchar(255) check (type in ('AGREE', 'DISAGREE')),
    created_at      timestamp(6),
    primary key (id)
);

create table if not exists battle_success_history
(
    id                bigint generated by default as identity,
    member_id         bigint,
    battle_id         bigint,
    battle_difficulty varchar(255) check (battle_difficulty in ('EASY', 'NORMAL', 'HARD')),
    primary key (id)
);
