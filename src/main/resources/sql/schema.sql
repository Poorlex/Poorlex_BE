--회원
create table member
(
    id       bigint generated by default as identity,
    oauth_id TEXT unique,
    nickname varchar(255),
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
    date        timestamp(6),
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

create table alarm
(
    id         bigint generated by default as identity,
    battle_id  bigint,
    member_id  bigint,
    type       varchar(255) check (type in
                                   ('EXPENDITURE_CREATED', 'EXPENDITURE_NEEDED', 'OVER_BUDGET',
                                    'ZERO_EXPENDITURE', 'BATTLE_NOTIFICATION_CHANGED')),
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
