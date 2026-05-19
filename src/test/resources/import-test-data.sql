-- Test data for Integration Tests (H2)
-- Insert test constructions
INSERT INTO construction (id, name)
VALUES (next value for CONSTRUCTION_SEQ, 'Integration Test Construction 1');
INSERT INTO construction (id, name)
VALUES (next value for CONSTRUCTION_SEQ, 'Integration Test Construction 2');

-- Insert test device
INSERT INTO device_info (id, device_key, type)
VALUES (next value for DEVICE_INFO_SEQ, 'TEST', 1);
