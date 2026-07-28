DROP TABLE IF EXISTS STATION_PLATFORM_TRACK_BLOCK;

CREATE TABLE STATION_PLATFORM_BLOCK_STRAIGHT (
    station_platform_id bigint NOT NULL,
    block_straight_id   bigint NOT NULL,
    CONSTRAINT FK_station_platform_block_straight_platform
        FOREIGN KEY (station_platform_id) REFERENCES STATION_PLATFORM,
    CONSTRAINT FK_station_platform_block_straight_block
        FOREIGN KEY (block_straight_id) REFERENCES TRACKPART_BLOCK_STRAIGHT
);
