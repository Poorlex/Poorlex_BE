create table if not exists alarm_reaction
(
    id         bigint not null auto_increment,
    alarm_id   bigint,
    member_id  bigint,
    content    varchar(255),
    type       enum ('PRAISE','SCOLD'),
    created_at datetime(6),
    primary key (id)
) engine = InnoDB;

create table if not exists battle
(
    id                        bigint       not null auto_increment,
    name                      varchar(255) not null,
    budget                    integer      not null,
    max_size                  integer      not null,
    introduction              varchar(255) not null,
    image_url                 text         not null,
    start_time                datetime(6)  not null,
    end_time                  datetime(6)  not null,
    status                    enum ('COMPLETE','PROGRESS','RECRUITING','RECRUITING_FINISHED'),
    is_battle_success_counted boolean,
    created_at                datetime(6),
    primary key (id)
) engine = InnoDB;

create table if not exists battle_alarm
(
    id         bigint not null auto_increment,
    battle_id  bigint,
    member_id  bigint,
    type       enum ('BATTLE_NOTIFICATION_CHANGED','EXPENDITURE_CREATED','EXPENDITURE_NEEDED','OVER_BUDGET','PARTICIPANT_ESCAPE','VOTE_CREATED','VOTING_PAPER_CREATED','ZERO_EXPENDITURE'),
    created_at datetime(6),
    primary key (id)
) engine = InnoDB;

create table if not exists battle_alarm_view_history
(
    id             bigint not null auto_increment,
    battle_id      bigint,
    member_id      bigint,
    last_view_time datetime(6),
    primary key (id)
) engine = InnoDB;

create table if not exists battle_notification
(
    id        bigint not null auto_increment,
    battle_id bigint,
    content   varchar(255),
    image_url text,
    primary key (id)
) engine = InnoDB;

create table if not exists battle_participant
(
    id        bigint                           not null auto_increment,
    battle_id bigint                           not null,
    member_id bigint                           not null,
    role      enum ('MANAGER','NORMAL_PLAYER') not null,
    primary key (id)
) engine = InnoDB;

create table if not exists expenditure
(
    id             bigint not null auto_increment,
    member_id      bigint not null,
    amount         bigint not null,
    description    varchar(255),
    main_image_url text   not null,
    sub_image_url  text,
    date           date   not null,
    created_at     datetime(6),
    point_paid     boolean,
    primary key (id)
) engine = InnoDB;

create table if not exists friend
(
    id               bigint not null auto_increment,
    first_member_id  bigint,
    second_member_id bigint,
    primary key (id)
) engine = InnoDB;

create table if not exists goal
(
    id         bigint not null auto_increment,
    member_id  bigint,
    name       varchar(255),
    amount     bigint,
    status     enum ('FINISH','PROGRESS'),
    type       enum ('REST_AND_REFRESH','STABLE_FUTURE','STRESS_RESOLVE','SUCCESSFUL_AT_WORK','WEALTH_AND_HONOR'),
    end_date   date,
    start_date date,
    primary key (id)
) engine = InnoDB;

create table if not exists member
(
    id                    bigint not null auto_increment,
    oauth2registration_id enum ('KAKAO', 'APPLE'),
    oauth_id              varchar(100),
    nickname              varchar(255),
    description           varchar(255),
    primary key (id)
) engine = InnoDB;

create table if not exists member_alarm
(
    id         bigint not null auto_increment,
    member_id  bigint,
    target_id  bigint,
    type       enum ('BATTLE_INVITATION_ACCEPT','BATTLE_INVITATION_ACCEPTED','BATTLE_INVITATION_DENIED','BATTLE_INVITATION_NOT_ACCEPTED','BATTLE_KICKED_OUT','FRIEND_ACCEPTED','FRIEND_INVITATION_ACCEPTED','FRIEND_INVITATION_DENIED','FRIEND_INVITATION_NOT_ACCEPTED'),
    created_at datetime(6),
    primary key (id)
) engine = InnoDB;

create table if not exists alarm_allowance
(
    id                  bigint not null auto_increment,
    member_id           bigint,
    battle_chat         boolean,
    battle_invite       boolean,
    battle_status       boolean,
    expenditure_request boolean,
    friend              boolean,
    primary key (id)
) engine = InnoDB;

create table if not exists member_point
(
    id         bigint not null auto_increment,
    member_id  bigint,
    point      integer,
    created_at datetime(6),
    primary key (id)
) engine = InnoDB;

create table if not exists vote
(
    id        bigint not null auto_increment,
    battle_id bigint,
    member_id bigint,
    name      varchar(255),
    amount    bigint,
    status    enum ('FINISHED','PROGRESS'),
    start     datetime(6),
    type      enum ('FIVE_MINUTE','SIXTY_MINUTE','TEN_MINUTE','THIRTY_MINUTE','TWENTY_MINUTE'),
    primary key (id)
) engine = InnoDB;

create table if not exists voting_paper
(
    id              bigint not null auto_increment,
    vote_id         bigint,
    voter_member_id bigint,
    type            enum ('AGREE','DISAGREE'),
    created_at      datetime(6),
    primary key (id)
) engine = InnoDB;

create table if not exists weekly_budget
(
    id         bigint not null auto_increment,
    member_id  bigint,
    amount     bigint,
    start_date date,
    end_date   date,
    primary key (id)
) engine = InnoDB;

create table if not exists battle_success_history
(
    id                bigint not null auto_increment,
    member_id         bigint,
    battle_id         bigint,
    battle_difficulty enum ('EASY', 'NORMAL', 'HARD'),
    primary key (id)
) engine = InnoDB;
