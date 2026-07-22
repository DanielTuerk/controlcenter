
    create sequence BUSDATA_CONFIG_SEQ start with 1 iNCREMENT BY 50;

    create sequence CONSTRUCTION_SEQ start with 1 iNCREMENT BY 50;

    create sequence DEVICE_INFO_SEQ start with 1 iNCREMENT BY 50;

    create sequence EVENT_CONFIG_SEQ start with 1 iNCREMENT BY 50;

    create sequence GRID_POSITION_SEQ start with 1 iNCREMENT BY 50;

    create sequence SCENARIO_HISTORY_SEQ start with 1 iNCREMENT BY 50;

    create sequence SCENARIO_ROUTE_SEQ start with 1 iNCREMENT BY 50;

    create sequence SCENARIO_SEQ start with 1 iNCREMENT BY 50;

    create sequence SCENARIO_SEQUENCE_SEQ start with 1 iNCREMENT BY 50;

    create sequence STATION_PLATFORM_SEQ start with 1 iNCREMENT BY 50;

    create sequence STATION_SEQ start with 1 iNCREMENT BY 50;

    create sequence TRACK_BLOCK_SEQ start with 1 iNCREMENT BY 50;

    create sequence TRACK_PART_SEQ start with 1 iNCREMENT BY 50;

    create sequence TRACKPART_FUNCTION_SEQ start with 1 iNCREMENT BY 50;

    create sequence TRAIN_FUNCTION_SEQ start with 1 iNCREMENT BY 50;

    create sequence TRAIN_SEQ start with 1 iNCREMENT BY 50;

    create table BUSDATA_CONFIG (
        id bigint not null,
        CONFIG_ADDRESS integer,
        CONFIG_BIT integer,
        CONFIG_BIT_STATE boolean,
        CONFIG_BUS integer,
        primary key (id)
    );

    create table CONFIG_VALUE (
        config_key varchar(255) not null,
        config_value varchar(255) not null,
        primary key (config_key)
    );

    create table CONSTRUCTION (
        id bigint not null,
        name varchar(255),
        primary key (id)
    );

    create table DEVICE_INFO (
        id bigint not null,
        device_key varchar(255),
        type tinyint check ((type between 0 and 1)),
        primary key (id)
    );

    create table EVENT_CONFIG (
        id bigint not null,
        stateOffConfig_id bigint,
        stateOnConfig_id bigint,
        primary key (id)
    );

    create table GRID_POSITION (
        id bigint not null,
        construction_id bigint not null,
        x integer not null,
        y integer not null,
        primary key (id),
        unique (x, y, construction_id)
    );

    create table SCENARIO (
        id bigint not null,
        cron varchar(255),
        name varchar(255),
        startDrivingLevel integer,
        stationPlatformEndId bigint,
        stationPlatformStartId bigint,
        trainDrivingDirection tinyint check ((trainDrivingDirection between 0 and 1)),
        construction_id bigint,
        train_id bigint,
        primary key (id)
    );

    create table SCENARIO_HISTORY (
        id bigint not null,
        elapsedTimeMillis bigint not null,
        endDateTime timestamp(6),
        startDateTime timestamp(6),
        scenario_id bigint,
        primary key (id)
    );

    create table SCENARIO_ROUTE (
        id bigint not null,
        name varchar(255),
        oneway boolean,
        construction_id bigint,
        end_id bigint,
        start_id bigint,
        primary key (id)
    );

    create table SCENARIO_ROUTE_GRID_POSITION (
        RouteEntity_id bigint not null,
        waypoints_id bigint not null
    );

    create table SCENARIO_SEQUENCE (
        id bigint not null,
        endDelayInSeconds integer not null,
        position integer not null,
        route_id bigint,
        scenario_id bigint,
        primary key (id)
    );

    create table STATION (
        id bigint not null,
        name varchar(255),
        primary key (id)
    );

    create table STATION_PLATFORM (
        id bigint not null,
        name varchar(255),
        station_id bigint,
        primary key (id)
    );

    create table STATION_PLATFORM_TRACK_BLOCK (
        StationPlatformEntity_id bigint not null,
        trackBlocks_id bigint not null unique
    );

    create table TRACK_BLOCK (
        id bigint not null,
        backwardTargetDrivingLevel integer,
        construction_id bigint not null,
        drivingLevelAdjustType int default 0 not null check ((drivingLevelAdjustType between 0 and 2)),
        feedback boolean not null,
        forwardTargetDrivingLevel integer,
        name varchar(255),
        blockFunction_id bigint,
        primary key (id)
    );

    create table TRACKPART_BLOCK_STRAIGHT (
        id bigint not null,
        construction_id bigint not null,
        gridPosition_id bigint unique,
        direction tinyint check ((direction between 0 and 1)),
        blockLength integer not null,
        leftTrackBlock_id bigint,
        middleTrackBlock_id bigint,
        rightTrackBlock_id bigint,
        primary key (id)
    );

    create table TRACKPART_CURVE (
        id bigint not null,
        construction_id bigint not null,
        gridPosition_id bigint unique,
        direction tinyint check ((direction between 0 and 3)),
        primary key (id)
    );

    create table TRACKPART_FUNCTION (
        id bigint not null,
        functionKey varchar(255),
        configuration_id bigint,
        primary key (id)
    );

    create table TRACKPART_SIGNAL (
        id bigint not null,
        construction_id bigint not null,
        gridPosition_id bigint unique,
        direction tinyint check ((direction between 0 and 1)),
        type tinyint check ((type between 0 and 3)),
        signalConfigGreen1_id bigint,
        signalConfigGreen2_id bigint,
        signalConfigRed1_id bigint,
        signalConfigRed2_id bigint,
        signalConfigWhite_id bigint,
        signalConfigYellow1_id bigint,
        signalConfigYellow2_id bigint,
        stopBlock_id bigint,
        primary key (id)
    );

    create table TRACKPART_STRAIGHT (
        id bigint not null,
        construction_id bigint not null,
        gridPosition_id bigint unique,
        direction tinyint check ((direction between 0 and 1)),
        primary key (id)
    );

    create table TRACKPART_SWITCH (
        id bigint not null,
        construction_id bigint not null,
        gridPosition_id bigint unique,
        currentDirection tinyint check ((currentDirection between 0 and 1)),
        currentPresentation tinyint check ((currentPresentation between 0 and 3)),
        eventConfiguration_id bigint,
        toggleFunction_id bigint,
        primary key (id)
    );

    create table TRACKPART_UNCOUPLER (
        id bigint not null,
        construction_id bigint not null,
        gridPosition_id bigint unique,
        direction tinyint check ((direction between 0 and 1)),
        eventConfiguration_id bigint,
        toggleFunction_id bigint,
        primary key (id)
    );

    create table TRAIN (
        id bigint not null,
        address integer unique,
        name varchar(255),
        primary key (id)
    );

    create table TRAIN_FUNCTION (
        id bigint not null,
        FUNCTION_ALIAS varchar(255),
        configuration_id bigint,
        train_id bigint,
        primary key (id)
    );

    alter table if exists EVENT_CONFIG 
       add constraint FKi47odopgqxaiofb74ce38s3l4 
       foreign key (stateOffConfig_id) 
       references BUSDATA_CONFIG;

    alter table if exists EVENT_CONFIG 
       add constraint FKo4xtvvmwf88f679rl1y5xpmv3 
       foreign key (stateOnConfig_id) 
       references BUSDATA_CONFIG;

    alter table if exists SCENARIO 
       add constraint FKoqqe8b80w2qyfyc1jtplbaidy 
       foreign key (construction_id) 
       references CONSTRUCTION;

    alter table if exists SCENARIO 
       add constraint FKitpn3wtb2c3th2ehoehn9fp67 
       foreign key (train_id) 
       references TRAIN;

    alter table if exists SCENARIO_HISTORY 
       add constraint FK1v0y0kx8pb6jqx16e2mptq71u 
       foreign key (scenario_id) 
       references SCENARIO;

    alter table if exists SCENARIO_ROUTE 
       add constraint FKfwoaqljdbd3tncrvfwukusfj0 
       foreign key (construction_id) 
       references CONSTRUCTION;

    alter table if exists SCENARIO_ROUTE 
       add constraint FKayi3e2wurf7sonr7gsdlhiht0 
       foreign key (end_id) 
       references TRACK_BLOCK;

    alter table if exists SCENARIO_ROUTE 
       add constraint FKf0xaxyvrql8ryj0av0ma11okx 
       foreign key (start_id) 
       references TRACKPART_BLOCK_STRAIGHT;

    alter table if exists SCENARIO_ROUTE_GRID_POSITION 
       add constraint FKac6kiudbgak0y4riyrrbltyyj 
       foreign key (waypoints_id) 
       references GRID_POSITION;

    alter table if exists SCENARIO_ROUTE_GRID_POSITION 
       add constraint FKmt4rpdl2skmawvois3uv0obma 
       foreign key (RouteEntity_id) 
       references SCENARIO_ROUTE;

    alter table if exists SCENARIO_SEQUENCE 
       add constraint FKiwy75ph177d9awbc2mkrsm0rh 
       foreign key (scenario_id) 
       references SCENARIO;

    alter table if exists STATION_PLATFORM 
       add constraint FKnd8twblx9582qgqbc7ejgna4l 
       foreign key (station_id) 
       references STATION;

    alter table if exists STATION_PLATFORM_TRACK_BLOCK 
       add constraint FKlk7f5hah97uox57x909jsfk0g 
       foreign key (trackBlocks_id) 
       references TRACK_BLOCK;

    alter table if exists STATION_PLATFORM_TRACK_BLOCK 
       add constraint FKn3kakjw8y5sve3d1j38e1m6yj 
       foreign key (StationPlatformEntity_id) 
       references STATION_PLATFORM;

    alter table if exists TRACK_BLOCK 
       add constraint FKlkmd6053t5q2xetjf1x0v6lvt 
       foreign key (blockFunction_id) 
       references BUSDATA_CONFIG;

    alter table if exists TRACKPART_BLOCK_STRAIGHT 
       add constraint FKd5f93ql6u3u306emi35j9cwrs 
       foreign key (leftTrackBlock_id) 
       references TRACK_BLOCK;

    alter table if exists TRACKPART_BLOCK_STRAIGHT 
       add constraint FK794o9kwrdl2uf297y5ya1eoco 
       foreign key (middleTrackBlock_id) 
       references TRACK_BLOCK;

    alter table if exists TRACKPART_BLOCK_STRAIGHT 
       add constraint FKjebd6k7oxdghb1xnkpjfmu8xm 
       foreign key (rightTrackBlock_id) 
       references TRACK_BLOCK;

    alter table if exists TRACKPART_BLOCK_STRAIGHT 
       add constraint FK4hb0k6xg4ve590fcoft9dnhpv 
       foreign key (gridPosition_id) 
       references GRID_POSITION;

    alter table if exists TRACKPART_CURVE 
       add constraint FKnder2dqi96scb8rfy2e4m03ko 
       foreign key (gridPosition_id) 
       references GRID_POSITION;

    alter table if exists TRACKPART_FUNCTION 
       add constraint FKjn8rauu7n0sl3q2e47hv9bfb 
       foreign key (configuration_id) 
       references BUSDATA_CONFIG;

    alter table if exists TRACKPART_SIGNAL 
       add constraint FKecrdl4qrb2s6udai8esbuqqqr 
       foreign key (signalConfigGreen1_id) 
       references BUSDATA_CONFIG;

    alter table if exists TRACKPART_SIGNAL 
       add constraint FKgyefnrm1v7wrid99e2rdi79s6 
       foreign key (signalConfigGreen2_id) 
       references BUSDATA_CONFIG;

    alter table if exists TRACKPART_SIGNAL 
       add constraint FKhvw8rj40lradsqfi10sfyq2a7 
       foreign key (signalConfigRed1_id) 
       references BUSDATA_CONFIG;

    alter table if exists TRACKPART_SIGNAL 
       add constraint FKi7v3hdermarvuli0ff2j8u2g9 
       foreign key (signalConfigRed2_id) 
       references BUSDATA_CONFIG;

    alter table if exists TRACKPART_SIGNAL 
       add constraint FK5vjovkqokfk5cclsfphahw882 
       foreign key (signalConfigWhite_id) 
       references BUSDATA_CONFIG;

    alter table if exists TRACKPART_SIGNAL 
       add constraint FKeuq2drv2t9doy2bskabq0u8lf 
       foreign key (signalConfigYellow1_id) 
       references BUSDATA_CONFIG;

    alter table if exists TRACKPART_SIGNAL 
       add constraint FK2mlce3ud577bctdfxigd0i9np 
       foreign key (signalConfigYellow2_id) 
       references BUSDATA_CONFIG;

    alter table if exists TRACKPART_SIGNAL 
       add constraint FKkt189qaq8bhwcmrnioqdgllgp 
       foreign key (stopBlock_id) 
       references TRACK_BLOCK;

    alter table if exists TRACKPART_SIGNAL 
       add constraint FK4rn809knifxvu3jg4fxmvmv6v 
       foreign key (gridPosition_id) 
       references GRID_POSITION;

    alter table if exists TRACKPART_STRAIGHT 
       add constraint FKol8q9sp9e25i3s87dxjhlmdn0 
       foreign key (gridPosition_id) 
       references GRID_POSITION;

    alter table if exists TRACKPART_SWITCH 
       add constraint FK90i39y7lp0mjpawf3chkiayt0 
       foreign key (eventConfiguration_id) 
       references EVENT_CONFIG;

    alter table if exists TRACKPART_SWITCH 
       add constraint FKlwe1kfesfebllm96crn9py3ky 
       foreign key (toggleFunction_id) 
       references BUSDATA_CONFIG;

    alter table if exists TRACKPART_SWITCH 
       add constraint FKlhugtkenrpg1pacp4oe55rwmu 
       foreign key (gridPosition_id) 
       references GRID_POSITION;

    alter table if exists TRACKPART_UNCOUPLER 
       add constraint FKb2vfju2415bqx5e3prwvv3ssm 
       foreign key (eventConfiguration_id) 
       references EVENT_CONFIG;

    alter table if exists TRACKPART_UNCOUPLER 
       add constraint FKnhk9bvc4wccpy2ji9vcy8cg23 
       foreign key (toggleFunction_id) 
       references BUSDATA_CONFIG;

    alter table if exists TRACKPART_UNCOUPLER 
       add constraint FK6ibb3vyojwv6gpi6crnqclpih 
       foreign key (gridPosition_id) 
       references GRID_POSITION;

    alter table if exists TRAIN_FUNCTION 
       add constraint FK3owlvwfwnfwxn15luejj23emj 
       foreign key (configuration_id) 
       references BUSDATA_CONFIG;

    alter table if exists TRAIN_FUNCTION 
       add constraint FK17gv0m94osy18pfagi0klt45r 
       foreign key (train_id) 
       references TRAIN;
