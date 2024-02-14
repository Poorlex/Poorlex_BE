--회원
create table member
(
    id                    bigint generated by default as identity,
    oauth2registration_id varchar(255) check (oauth2registration_id in ('KAKAO', 'APPLE')),
    oauth_id              varchar(100),
    description           varchar(300),
    nickname              varchar(255),
    constraint idx_registrationId_oauthId unique (oauth2registration_id, oauth_id),
    primary key (id)
);

create table member_point
(
    id        bigint generated by default as identity,
    point     integer,
    member_id bigint,
    primary key (id)
);

--지출
create table expenditure
(
    id          bigint generated by default as identity,
    description varchar(255),
    amount      bigint,
    date_time   timestamp(6),
    member_id   bigint,
    primary key (id)
);

create table expenditure_image
(
    id             bigint generated by default as identity,
    url            text,
    expenditure_id bigint,
    primary key (id)
);

--배틀
create table battle
(
    id           bigint generated by default as identity,
    name         varchar(255),
    introduction varchar(255),
    image_url    text,
    budget       integer,
    max_size     integer,
    start_time   timestamp(6),
    end_time     timestamp(6),
    status       varchar(255) check (status in ('RECRUITING',
                                                'RECRUITING_FINISHED',
                                                'PROGRESS',
                                                'COMPLETE')),
    primary key (id)
);

create table battle_participant
(
    id        bigint generated by default as identity,
    battle_id bigint,
    member_id bigint,
    role      varchar(255) check (role in ('MANAGER', 'NORMAL_PLAYER')),
    primary key (id)
);

create table weekly_budget
(
    id         bigint generated by default as identity,
    amount     integer,
    start_date timestamp(6),
    end_date   timestamp(6),
    member_id  bigint,
    primary key (id)
);

create table battle_notification
(
    id        bigint generated by default as identity,
    content   varchar(255),
    battle_id bigint,
    image_url text,
    primary key (id)
);

create table battle_alarm
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

create table alarm_reaction
(
    id         bigint generated by default as identity,
    alarm_id   bigint,
    member_id  bigint,
    content    varchar(255),
    type       varchar(255) check (type in ('PRAISE', 'SCOLD')),
    created_at timestamp(6),
    primary key (id)
);

create table battle_alarm_view_history
(
    id             bigint generated by default as identity,
    battle_id      bigint,
    member_id      bigint,
    last_view_time timestamp(6),
    primary key (id)
);

create table goal
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

create table friend
(
    id               bigint generated by default as identity,
    first_member_id  bigint,
    second_member_id bigint,
    primary key (id)
);

create table member_alarm
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
                                    'BATTLE_KICKED_OUT')),
    created_at timestamp(6),
    primary key (id)
);

create table vote
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

create table voting_paper
(
    id              bigint generated by default as identity,
    vote_id         bigint,
    voter_member_id bigint,
    type            varchar(255) check (type in ('AGREE', 'DISAGREE')),
    created_at      timestamp(6),
    primary key (id)
);


insert into battle
values (1L, '배틀', '배틀 소개', 'imageUrl', 10000, 10, now(), now(), 'RECRUITING');
