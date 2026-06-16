RUNSCRIPT FROM 'C:\Users\DanielTuerk\IdeaProjects\control-center\controlcenter\sys\testData\tables\CONSTRUCTION.sql';
ALTER SEQUENCE PUBLIC.CONSTRUCTION_SEQ RESTART WITH (SELECT COALESCE(MAX(ID), 0) + 1 FROM CONSTRUCTION);

RUNSCRIPT FROM 'C:\Users\DanielTuerk\IdeaProjects\control-center\controlcenter\sys\testData\tables\DEVICE_INFO.sql';
ALTER SEQUENCE PUBLIC.DEVICE_INFO_SEQ RESTART WITH (SELECT COALESCE(MAX(ID), 0) + 1 FROM DEVICE_INFO);

RUNSCRIPT FROM 'C:\Users\DanielTuerk\IdeaProjects\control-center\controlcenter\sys\testData\tables\BUSDATA_CONFIG.sql';
ALTER SEQUENCE PUBLIC.BUSDATA_CONFIG_SEQ RESTART WITH (SELECT COALESCE(MAX(ID), 0) + 1 FROM BUSDATA_CONFIG);
RUNSCRIPT FROM 'C:\Users\DanielTuerk\IdeaProjects\control-center\controlcenter\sys\testData\tables\EVENT_CONFIG.sql';
ALTER SEQUENCE PUBLIC.EVENT_CONFIG_SEQ RESTART WITH (SELECT COALESCE(MAX(ID), 0) + 1 FROM EVENT_CONFIG);
RUNSCRIPT FROM 'C:\Users\DanielTuerk\IdeaProjects\control-center\controlcenter\sys\testData\tables\GRID_POSITION.sql';
ALTER SEQUENCE PUBLIC.GRID_POSITION_SEQ RESTART WITH (SELECT COALESCE(MAX(ID), 0) + 1 FROM GRID_POSITION);

RUNSCRIPT FROM 'C:\Users\DanielTuerk\IdeaProjects\control-center\controlcenter\sys\testData\tables\TRAIN.sql';
ALTER SEQUENCE PUBLIC.TRAIN_SEQ RESTART WITH (SELECT COALESCE(MAX(ID), 0) + 1 FROM TRAIN);

RUNSCRIPT FROM 'C:\Users\DanielTuerk\IdeaProjects\control-center\controlcenter\sys\testData\tables\TRACK_BLOCK.sql';
ALTER SEQUENCE PUBLIC.TRACK_BLOCK_SEQ RESTART WITH (SELECT COALESCE(MAX(ID), 0) + 1 FROM TRACK_BLOCK);

RUNSCRIPT FROM 'C:\Users\DanielTuerk\IdeaProjects\control-center\controlcenter\sys\testData\tables\TRACKPART_BLOCK_STRAIGHT.sql';
RUNSCRIPT FROM 'C:\Users\DanielTuerk\IdeaProjects\control-center\controlcenter\sys\testData\tables\TRACKPART_CURVE.sql';
RUNSCRIPT FROM 'C:\Users\DanielTuerk\IdeaProjects\control-center\controlcenter\sys\testData\tables\TRACKPART_SIGNAL.sql';
RUNSCRIPT FROM 'C:\Users\DanielTuerk\IdeaProjects\control-center\controlcenter\sys\testData\tables\TRACKPART_STRAIGHT.sql';
RUNSCRIPT FROM 'C:\Users\DanielTuerk\IdeaProjects\control-center\controlcenter\sys\testData\tables\TRACKPART_SWITCH.sql';
SELECT COALESCE(MAX(ID), 0) + 1 AS NEXT_ID FROM (
         SELECT ID FROM TRACKPART_BLOCK_STRAIGHT
         UNION ALL
         SELECT ID FROM TRACKPART_CURVE
         UNION ALL
         SELECT ID FROM TRACKPART_SIGNAL
         UNION ALL
         SELECT ID FROM TRACKPART_STRAIGHT
         UNION ALL
         SELECT ID FROM TRACKPART_SWITCH
     );

RUNSCRIPT FROM 'C:\Users\DanielTuerk\IdeaProjects\control-center\controlcenter\sys\testData\tables\SCENARIO.sql';
ALTER SEQUENCE PUBLIC.SCENARIO_SEQ RESTART WITH (SELECT COALESCE(MAX(ID), 0) + 1 FROM SCENARIO);
RUNSCRIPT FROM 'C:\Users\DanielTuerk\IdeaProjects\control-center\controlcenter\sys\testData\tables\SCENARIO_ROUTE.sql';
ALTER SEQUENCE PUBLIC.SCENARIO_ROUTE_SEQ RESTART WITH (SELECT COALESCE(MAX(ID), 0) + 1 FROM SCENARIO_ROUTE);
RUNSCRIPT FROM 'C:\Users\DanielTuerk\IdeaProjects\control-center\controlcenter\sys\testData\tables\SCENARIO_ROUTE_GRID_POSITION.sql';
RUNSCRIPT FROM 'C:\Users\DanielTuerk\IdeaProjects\control-center\controlcenter\sys\testData\tables\SCENARIO_SEQUENCE.sql';
ALTER SEQUENCE PUBLIC.SCENARIO_SEQUENCE_SEQ RESTART WITH (SELECT COALESCE(MAX(ID), 0) + 1 FROM SCENARIO_SEQUENCE);
RUNSCRIPT FROM 'C:\Users\DanielTuerk\IdeaProjects\control-center\controlcenter\sys\testData\tables\SCENARIO_HISTORY.sql';
ALTER SEQUENCE PUBLIC.SCENARIO_HISTORY_SEQ RESTART WITH (SELECT COALESCE(MAX(ID), 0) + 1 FROM SCENARIO_HISTORY);

RUNSCRIPT FROM 'C:\Users\DanielTuerk\IdeaProjects\control-center\controlcenter\sys\testData\tables\STATION.sql';
ALTER SEQUENCE PUBLIC.CONSTRUCTION_SEQ RESTART WITH (SELECT COALESCE(MAX(ID), 0) + 1 FROM STATION);
RUNSCRIPT FROM 'C:\Users\DanielTuerk\IdeaProjects\control-center\controlcenter\sys\testData\tables\STATION_PLATFORM.sql';
ALTER SEQUENCE PUBLIC.CONSTRUCTION_SEQ RESTART WITH (SELECT COALESCE(MAX(ID), 0) + 1 FROM STATION_PLATFORM);