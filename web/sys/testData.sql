INSERT INTO STATION VALUES (1, 'HBF');
INSERT INTO STATION VALUES (2, 'SBF - lang');
INSERT INTO STATION VALUES (3, 'SBF - kurz');

-- HBF
INSERT INTO STATION_RAIL VALUES (1, 1, 1);
INSERT INTO STATION_RAIL VALUES (2, 2, 1);
INSERT INTO STATION_RAIL VALUES (3, 3, 1);
INSERT INTO STATION_RAIL VALUES (4, 4, 1);
-- SBF - kurz
INSERT INTO STATION_RAIL VALUES (5, 1, 2);
INSERT INTO STATION_RAIL VALUES (6, 2, 2);
INSERT INTO STATION_RAIL VALUES (7, 3, 2);
-- SBF - lang
INSERT INTO STATION_RAIL VALUES (8, 1, 3);
INSERT INTO STATION_RAIL VALUES (9, 2, 3);
INSERT INTO STATION_RAIL VALUES (10, 3, 3);

-- scenario vt18
INSERT INTO SCENARIO VALUES (1, '*/10 * * * *', 'HBF to sbf8', 2301);


INSERT INTO SCENARIO_ROUTE VALUES (1, 'HBF -> sbf-lang', 1, 8, 1);
INSERT INTO SCENARIO_ROUTE_BLOCK VALUES (1, 1, 941, 1, 636);
-- hbf ausfahrt
INSERT INTO SCENARIO_ROUTE_BLOCK_PART VALUES (1, 1, 1, 610);
INSERT INTO SCENARIO_ROUTE_BLOCK_PART VALUES (2, 0, 1, 612);
-- sbf einfahrt g 8
INSERT INTO SCENARIO_ROUTE_BLOCK_PART VALUES (3, 0, 1, 694);
INSERT INTO SCENARIO_ROUTE_BLOCK_PART VALUES (4, 0, 1, 700);
INSERT INTO SCENARIO_ROUTE_BLOCK_PART VALUES (5, 0, 1, 690);
INSERT INTO SCENARIO_ROUTE_BLOCK_PART VALUES (6, 0, 1, 772);

INSERT INTO SCENARIO_ROUTE_SEQUENCE VALUES (1, 1, 1, 1);

-- zurueck
INSERT INTO SCENARIO VALUES (2, '*/20 * * * *', 'sbf8 to HBF', 2301);

INSERT INTO SCENARIO_ROUTE VALUES (2, 'sbf-lang -> HBF', 1, 1, 8);
INSERT INTO SCENARIO_ROUTE_BLOCK VALUES (2, 1, 1801, 2, 2401);
-- hbf einfahrt
INSERT INTO SCENARIO_ROUTE_BLOCK_PART VALUES (10, 1, 2, 610);
INSERT INTO SCENARIO_ROUTE_BLOCK_PART VALUES (11, 0, 2, 612);
-- sbf ausfahrt g 8
INSERT INTO SCENARIO_ROUTE_BLOCK_PART VALUES (12, 0, 2, 694);
INSERT INTO SCENARIO_ROUTE_BLOCK_PART VALUES (13, 0, 2, 700);
INSERT INTO SCENARIO_ROUTE_BLOCK_PART VALUES (14, 0, 2, 690);
INSERT INTO SCENARIO_ROUTE_BLOCK_PART VALUES (15, 0, 2, 772);

INSERT INTO SCENARIO_ROUTE_SEQUENCE VALUES (2, 1, 2, 2);