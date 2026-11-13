-- Supplementary UC-04..UC-08 catalogue seed fragment for the Deliverable 2 merge. 
-- Merge this after the existing seed inserts and keep its identity resets after 
-- all master seed statements; this file is intentionally not loaded by Spring. 
-- The current data.sql already provides its own 14-vehicle local demo dataset. 

INSERT INTO vehicles (id, brand, model, trim_name, model_year, shape, vehicle_condition, price, deal_price, mileage_km, 
                      range_km, battery_kwh, energy_kwh_per_100km, battery_health_pct, drivetrain, seats, 
                      description, image_url, quantity, status) VALUES 
 (1001, 'Lucid', 'Air Pure', 'Touring', 2025, 'SEDAN', 'NEW', 69900.00, NULL, 0, 660, 84.0, 14.0, NULL, 'RWD', 5, 
  'Efficient luxury electric sedan.', '/images/vehicles/lucid-air.jpg', 2, 'AVAILABLE'), 
 (1002, 'Rivian', 'R1S', 'Adventure', 2023, 'SUV', 'USED', 73500.00, NULL, 28400, 515, 105.0, 21.0, 94, 'AWD', 7, 
  'Pre-owned three-row electric SUV.', '/images/vehicles/rivian-r1s.jpg', 1, 'AVAILABLE'), 
 (1003, 'Nissan', 'Ariya', 'Evolve+', 2025, 'SUV', 'NEW', 51900.00, NULL, 0, 490, 87.0, 18.0, NULL, 'AWD', 5, 
  'New electric crossover with long-range battery.', '/images/vehicles/nissan-ariya.jpg', 2, 'AVAILABLE'), 
 (1004, 'Volkswagen', 'ID.3', 'Pro S', 2022, 'HATCHBACK', 'USED', 28900.00, NULL, 44700, 420, 77.0, 17.0, 91, 'RWD', 5, 
  'Pre-owned compact electric hatchback.', '/images/vehicles/vw-id3.jpg', 1, 'AVAILABLE'), 
 (1005, 'Chevrolet', 'Equinox EV', 'LT', 2025, 'SUV', 'NEW', 43900.00, NULL, 0, 513, 85.0, 17.5, NULL, 'FWD', 5, 
  'New electric SUV with practical range.', '/images/vehicles/equinox-ev.jpg', 3, 'AVAILABLE'), 
 (1006, 'Polestar', '2', 'Long Range', 2022, 'SEDAN', 'USED', 36900.00, NULL, 51600, 435, 78.0, 18.0, 88, 'AWD', 5, 
  'Pre-owned fastback electric sedan.', '/images/vehicles/polestar-2-used.jpg', 1, 'AVAILABLE'), 
 (1007, 'Hyundai', 'Kona Electric', 'Preferred', 2025, 'SUV', 'NEW', 45900.00, NULL, 0, 420, 65.0, 16.0, NULL, 'FWD', 5, 
  'New compact electric SUV.', '/images/vehicles/kona-electric.jpg', 2, 'AVAILABLE'), 
 (1008, 'Kia', 'Niro EV', 'Wind', 2023, 'SUV', 'USED', 34900.00, NULL, 33200, 407, 64.8, 17.5, 93, 'FWD', 5, 
  'Pre-owned efficient electric crossover.', '/images/vehicles/niro-ev.jpg', 1, 'AVAILABLE'), 
 (1009, 'BMW', 'iX', 'xDrive50', 2025, 'SUV', 'NEW', 89900.00, NULL, 0, 521, 111.5, 21.0, NULL, 'AWD', 5, 
  'New luxury electric SUV.', '/images/vehicles/bmw-ix.jpg', 1, 'AVAILABLE'), 
 (1010, 'Ford', 'Mustang Mach-E', 'Select', 2022, 'SUV', 'USED', 32900.00, NULL, 60200, 397, 70.0, 19.0, 86, 'RWD', 5, 
  'Pre-owned electric performance crossover.', '/images/vehicles/mach-e-used.jpg', 1, 'AVAILABLE'), 
 (1011, 'Tesla', 'Model 3', 'Long Range', 2024, 'SEDAN', 'NEW', 57900.00, NULL, 0, 550, 82.0, 15.0, NULL, 'AWD', 5, 
  'New dual-motor electric sedan.', '/images/vehicles/model-3-seed.jpg', 2, 'AVAILABLE'), 
 (1012, 'Hyundai', 'IONIQ 5', 'Preferred', 2022, 'SUV', 'USED', 33900.00, NULL, 57100, 430, 77.4, 18.0, 89, 'AWD', 5, 
  'Pre-owned electric crossover.', '/images/vehicles/ioniq-5-used.jpg', 1, 'AVAILABLE'); 

INSERT INTO vehicle_history (id, vehicle_id, accident_count, damage_reported, previous_owners, service_records, report_summary, report_date) VALUES 
 (1001, 1002, 0, FALSE, 1, 4, 'No accidents reported; scheduled services are recorded.', CURRENT_DATE), 
 (1002, 1004, 1, TRUE, 2, 5, 'Minor repaired body damage; no structural issues reported.', CURRENT_DATE), 
 (1003, 1006, 0, FALSE, 1, 3, 'Single-owner vehicle with documented service history.', CURRENT_DATE); 

INSERT INTO customization_options (id, category, name, hex_code, price_delta) VALUES 
 (1001, 'EXTERIOR_COLOUR', 'Catalog Seed Blue', '#234B80', 0.00), 
 (1002, 'EXTERIOR_COLOUR', 'Catalog Seed Silver', '#AEB7C0', 950.00), 
 (1003, 'INTERIOR_COLOUR', 'Catalog Seed Charcoal', '#30343B', 0.00), 
 (1004, 'INTERIOR_FABRIC', 'Catalog Seed Textile', NULL, 0.00); 

INSERT INTO vehicle_options (vehicle_id, option_id) VALUES 
 (1001,1001),(1001,1002),(1001,1003),(1001,1004), 
 (1002,1001),(1002,1002),(1002,1003),(1002,1004), 
 (1003,1001),(1003,1002),(1003,1003),(1003,1004), 
 (1004,1001),(1004,1002),(1004,1003),(1004,1004), 
 (1005,1001),(1005,1002),(1005,1003),(1005,1004), 
 (1006,1001),(1006,1002),(1006,1003),(1006,1004), 
 (1007,1001),(1007,1002),(1007,1003),(1007,1004), 
 (1008,1001),(1008,1002),(1008,1003),(1008,1004), 
 (1009,1001),(1009,1002),(1009,1003),(1009,1004), 
 (1010,1001),(1010,1002),(1010,1003),(1010,1004), 
 (1011,1001),(1011,1002),(1011,1003),(1011,1004), 
 (1012,1001),(1012,1002),(1012,1003),(1012,1004); 

ALTER TABLE vehicles ALTER COLUMN id RESTART WITH 2000; 
ALTER TABLE vehicle_history ALTER COLUMN id RESTART WITH 2000; 
ALTER TABLE customization_options ALTER COLUMN id RESTART WITH 2000; 
