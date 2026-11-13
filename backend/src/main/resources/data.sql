-- ===================================================================== 
-- Fritz EV Online Store - seed data 
-- Owner: Mohammed (PB-16) 
-- 
-- Explicit ids keep foreign keys readable. After seeding, every identity 
-- column is restarted at 1000 so new rows never collide with seed rows. 
-- Dates are relative to "now" so reports and test-drive slots stay fresh. 
-- 
-- Demo logins (passwords are stored ONLY as BCrypt hashes): 
--   admin  / Admin123!     ADMIN 
--   alice  / Password123!  CUSTOMER, verified EV owner 
--   bob    / Password123!  CUSTOMER, NOT verified (UC-15 negative test) 
--   carol  / Password123!  CUSTOMER, verified EV owner 
-- ===================================================================== 

-- ---------------- users ---------------- 
INSERT INTO users (id, username, email, password_hash, first_name, last_name, role, verified_owner) VALUES 
 (1, 'admin', 'admin@fritzev.ca', '$2a$10$jefxfbcHloicpx1bEu07petc5EIUjLtB5hlN9jDCyhv5JdGkQt3Vy',    'Fritz', 'Admin',  'ADMIN',    FALSE), 
 (2, 'alice', 'alice@example.com', '$2a$10$mWB/EWkCNA02XhxJY0h7I.DWoW9CqshHy5X07NzOsQKhdpVLEzqby', 'Alice', 'Nguyen', 'CUSTOMER', TRUE), 
 (3, 'bob',   'bob@example.com',   '$2a$10$mWB/EWkCNA02XhxJY0h7I.DWoW9CqshHy5X07NzOsQKhdpVLEzqby', 'Bob',   'Singh',  'CUSTOMER', FALSE), 
 (4, 'carol', 'carol@example.com', '$2a$10$mWB/EWkCNA02XhxJY0h7I.DWoW9CqshHy5X07NzOsQKhdpVLEzqby', 'Carol', 'Rossi',  'CUSTOMER', TRUE); 

INSERT INTO addresses (id, user_id, type, street, city, province, country, postal_code, phone) VALUES 
 (1, 2, 'BILLING',  '4700 Keele St',     'Toronto',     'ON', 'Canada', 'M3J 1P3', '416-555-0101'), 
 (2, 2, 'SHIPPING', '4700 Keele St',     'Toronto',     'ON', 'Canada', 'M3J 1P3', '416-555-0101'), 
 (3, 3, 'BILLING',  '100 Queen St W',    'Toronto',     'ON', 'Canada', 'M5H 2N2', '416-555-0102'), 
 (4, 3, 'SHIPPING', '100 Queen St W',    'Toronto',     'ON', 'Canada', 'M5H 2N2', '416-555-0102'), 
 (5, 4, 'BILLING',  '1 Main St',         'Vaughan',     'ON', 'Canada', 'L4K 0B2', '905-555-0103'), 
 (6, 4, 'SHIPPING', '25 Lakeshore Rd',   'Mississauga', 'ON', 'Canada', 'L5H 1G4', '905-555-0103'); 

-- ---------------- vehicles ---------------- 
-- energy_kwh_per_100km feeds the UC-20 savings estimator. 
-- deal_price NOT NULL => listed under Hot Deals. 
INSERT INTO vehicles (id, brand, model, trim_name, model_year, shape, vehicle_condition, price, deal_price, mileage_km, 
                      range_km, battery_kwh, energy_kwh_per_100km, battery_health_pct, drivetrain, seats, 
                      description, image_url, quantity, status) VALUES 
 (1,  'Tesla',      'Model 3',     'Long Range',   2025, 'SEDAN',     'NEW',  54990.00, NULL,     0,      584, 82.0, 14.9, NULL, 'AWD', 5, 
      'Dual-motor sedan with a minimalist interior and over-the-air updates.', '/images/vehicles/tesla-model-3.jpg', 4, 'AVAILABLE'), 
 (2,  'Tesla',      'Model Y',     'Performance',  2024, 'SUV',       'USED', 52900.00, 47900.00, 21500,  514, 81.0, 16.9, 96,   'AWD', 5, 
      'Performance SUV, one owner, full self-driving hardware.', '/images/vehicles/tesla-model-y.jpg', 1, 'AVAILABLE'), 
 (3,  'Hyundai',    'IONIQ 5',     'Preferred',    2025, 'SUV',       'NEW',  57999.00, 53999.00, 0,      488, 84.0, 17.4, NULL, 'AWD', 5, 
      '800V ultra-fast charging, 10-80% in about 18 minutes.', '/images/vehicles/hyundai-ioniq-5.jpg', 3, 'AVAILABLE'), 
 (4,  'Hyundai',    'IONIQ 6',     'Long Range',   2023, 'SEDAN',     'USED', 41500.00, NULL,     38200,  509, 77.4, 14.3, 93,   'RWD', 5, 
      'Streamlined sedan, very efficient on the highway.', '/images/vehicles/hyundai-ioniq-6.jpg', 1, 'AVAILABLE'), 
 (5,  'Kia',        'EV6',         'GT-Line',      2024, 'HATCHBACK', 'NEW',  59995.00, NULL,     0,      499, 77.4, 17.0, NULL, 'AWD', 5, 
      'Sporty crossover hatch with vehicle-to-load power outlet.', '/images/vehicles/kia-ev6.jpg', 2, 'AVAILABLE'), 
 (6,  'Kia',        'EV9',         'Land',         2025, 'SUV',       'NEW',  74995.00, 69995.00, 0,      451, 99.8, 22.4, NULL, 'AWD', 7, 
      'Three-row family SUV with captain chairs.', '/images/vehicles/kia-ev9.jpg', 2, 'AVAILABLE'), 
 (7,  'Chevrolet',  'Bolt EUV',    'LT',           2022, 'HATCHBACK', 'USED', 24900.00, 21900.00, 54000,  397, 65.0, 17.6, 89,   'FWD', 5, 
      'Affordable commuter, battery replaced under recall in 2023.', '/images/vehicles/chevrolet-bolt-euv.jpg', 1, 'AVAILABLE'), 
 (8,  'Ford',       'Mustang Mach-E', 'Premium',   2023, 'SUV',       'USED', 46500.00, NULL,     29800,  434, 91.0, 20.3, 94,   'AWD', 5, 
      'Extended-range battery, BlueCruise hands-free highway driving.', '/images/vehicles/ford-mach-e.jpg', 1, 'AVAILABLE'), 
 (9,  'Ford',       'F-150 Lightning', 'Lariat',   2024, 'TRUCK',     'NEW',  89990.00, NULL,     0,      515, 131.0, 25.4, NULL, 'AWD', 5, 
      'Full-size electric pickup with Pro Power Onboard.', '/images/vehicles/ford-f150-lightning.jpg', 1, 'AVAILABLE'), 
 (10, 'Nissan',     'Leaf',        'SV Plus',      2021, 'HATCHBACK', 'USED', 19500.00, NULL,     72000,  346, 62.0, 18.0, 82,   'FWD', 5, 
      'Reliable city car. Minor rear bumper damage reported and repaired.', '/images/vehicles/nissan-leaf.jpg', 1, 'AVAILABLE'), 
 (11, 'Polestar',   '2',           'Long Range Dual Motor', 2024, 'SEDAN', 'NEW', 64950.00, NULL, 0,      482, 82.0, 18.6, NULL, 'AWD', 5, 
      'Scandinavian design, Google built-in infotainment.', '/images/vehicles/polestar-2.jpg', 2, 'AVAILABLE'), 
 (12, 'BMW',        'i4',          'eDrive40',     2023, 'COUPE',     'USED', 49900.00, 45900.00, 25600,  484, 83.9, 17.4, 95,   'RWD', 5, 
      'Gran Coupe with hatchback practicality.', '/images/vehicles/bmw-i4.jpg', 1, 'AVAILABLE'), 
 (13, 'Volkswagen', 'ID.4',        'Pro S',        2024, 'SUV',       'NEW',  56995.00, NULL,     0,      443, 82.0, 18.5, NULL, 'AWD', 5, 
      'Spacious compact SUV, 3 years of included fast charging.', '/images/vehicles/vw-id4.jpg', 3, 'AVAILABLE'), 
 (14, 'Tesla',      'Model S',     'Plaid',        2022, 'SEDAN',     'USED', 79900.00, NULL,     41000,  560, 100.0, 18.9, 91,  'AWD', 5, 
      'Tri-motor flagship. Sold - kept for sales-report history.', '/images/vehicles/tesla-model-s.jpg', 0, 'SOLD'); 

-- ---------------- vehicle history (used vehicles only) ---------------- 
INSERT INTO vehicle_history (id, vehicle_id, accident_count, damage_reported, previous_owners, service_records, report_summary, report_date) VALUES 
 (1, 2,  0, FALSE, 1, 3, 'No accidents reported. All scheduled maintenance completed at dealer.', CURRENT_DATE), 
 (2, 4,  0, FALSE, 1, 4, 'Clean history. Lease return.', CURRENT_DATE), 
 (3, 7,  0, FALSE, 2, 6, 'No accidents. Battery module replaced under manufacturer recall.', CURRENT_DATE), 
 (4, 8,  1, TRUE,  1, 3, 'Minor front-end collision (cosmetic), repaired by certified body shop.', CURRENT_DATE), 
 (5, 10, 1, TRUE,  2, 7, 'Rear bumper damage reported and repaired. No structural damage.', CURRENT_DATE), 
 (6, 12, 0, FALSE, 1, 2, 'Clean history, single owner.', CURRENT_DATE), 
 (7, 14, 0, FALSE, 1, 5, 'Clean history.', CURRENT_DATE); 

-- ---------------- customization options (UC-08) ---------------- 
INSERT INTO customization_options (id, category, name, hex_code, price_delta) VALUES 
 (1,  'EXTERIOR_COLOUR', 'Solid Black',        '#111111', 0.00), 
 (2,  'EXTERIOR_COLOUR', 'Pearl White',        '#F5F5F0', 1500.00), 
 (3,  'EXTERIOR_COLOUR', 'Deep Blue Metallic', '#1E3A8A', 1500.00), 
 (4,  'EXTERIOR_COLOUR', 'Ultra Red',          '#B91C1C', 2500.00), 
 (5,  'EXTERIOR_COLOUR', 'Lunar Silver',       '#C0C4C8', 1000.00), 
 (6,  'INTERIOR_COLOUR', 'All Black',          '#0A0A0A', 0.00), 
 (7,  'INTERIOR_COLOUR', 'Black and White',    '#E5E5E5', 1200.00), 
 (8,  'INTERIOR_COLOUR', 'Cognac',             '#8B4513', 1500.00), 
 (9,  'INTERIOR_FABRIC', 'Recycled Textile',   NULL,      0.00), 
 (10, 'INTERIOR_FABRIC', 'Vegan Leather',      NULL,      1800.00), 
 (11, 'INTERIOR_FABRIC', 'Nappa Leather',      NULL,      3500.00), 
 (12, 'WHEELS',          '19in Aero',          NULL,      0.00), 
 (13, 'WHEELS',          '20in Sport',         NULL,      2200.00), 
 (14, 'WHEELS',          '21in Performance',   NULL,      4500.00); 

-- New vehicles: full option set. Used vehicles: only their existing colour/interior. 
INSERT INTO vehicle_options (vehicle_id, option_id) VALUES 
 (1,1),(1,2),(1,3),(1,4),(1,6),(1,7),(1,9),(1,10),(1,12),(1,13), 
 (3,1),(3,2),(3,5),(3,6),(3,8),(3,9),(3,10),(3,12),(3,13), 
 (5,1),(5,2),(5,3),(5,4),(5,6),(5,7),(5,9),(5,10),(5,12),(5,13),(5,14), 
 (6,1),(6,2),(6,5),(6,6),(6,8),(6,10),(6,11),(6,12),(6,13), 
 (9,1),(9,3),(9,4),(9,5),(9,6),(9,9),(9,10),(9,13),(9,14), 
 (11,1),(11,2),(11,5),(11,6),(11,9),(11,10),(11,11),(11,12),(11,13), 
 (13,1),(13,2),(13,3),(13,5),(13,6),(13,7),(13,9),(13,10),(13,12),(13,13), 
 (2,2),(2,6),(2,10),(2,14), 
 (4,5),(4,6),(4,9),(4,12), 
 (7,4),(7,6),(7,9),(7,12), 
 (8,3),(8,6),(8,10),(8,13), 
 (10,5),(10,6),(10,9),(10,12), 
 (12,1),(12,8),(12,10),(12,13), 
 (14,4),(14,7),(14,11),(14,14); 

-- ---------------- orders (UC-17 sales report history) ---------------- 
INSERT INTO orders (id, user_id, status, total, ship_street, ship_city, ship_province, ship_country, ship_postal_code, created_at) VALUES 
 (1, 2, 'COMPLETED', 79900.00, '4700 Keele St',   'Toronto',     'ON', 'Canada', 'M3J 1P3', CURRENT_TIMESTAMP - INTERVAL '75' DAY), 
 (2, 4, 'COMPLETED', 56490.00, '25 Lakeshore Rd', 'Mississauga', 'ON', 'Canada', 'L5H 1G4', CURRENT_TIMESTAMP - INTERVAL '40' DAY), 
 (3, 2, 'COMPLETED', 59499.00, '4700 Keele St',   'Toronto',     'ON', 'Canada', 'M3J 1P3', CURRENT_TIMESTAMP - INTERVAL '12' DAY), 
 (4, 3, 'COMPLETED', 64950.00, '100 Queen St W',  'Toronto',     'ON', 'Canada', 'M5H 2N2', CURRENT_TIMESTAMP - INTERVAL '3' DAY); 

INSERT INTO order_items (id, order_id, vehicle_id, quantity, unit_price, options_summary) VALUES 
 (1, 1, 14, 1, 79900.00, 'Ultra Red / Black and White / Nappa Leather / 21in Performance'), 
 (2, 2, 1,  1, 56490.00, 'Pearl White / All Black / Recycled Textile / 19in Aero'), 
 (3, 3, 3,  1, 59499.00, 'Pearl White / Cognac / Vegan Leather / 19in Aero'), 
 (4, 4, 11, 1, 64950.00, 'Solid Black / All Black / Recycled Textile / 19in Aero'); 

-- ---------------- reviews (UC-15, verified owners only) ---------------- 
INSERT INTO reviews (id, vehicle_id, user_id, rating, winter_range_km, winter_range_notes, home_charging_notes, software_update_notes, overall_satisfaction, comment) VALUES 
 (1, 1, 4, 5, 430, 'Lost about 25% range at -15C with preconditioning.', 'Level 2 charger overnight, always full by morning.', 'Monthly OTA updates, new features often.', 5, 'Best car I have owned.'), 
 (2, 3, 2, 4, 360, 'Heat pump helps, still noticeable drop below -10C.',  'Installed 48A wall box, easy setup.',                 'Fewer OTA updates than expected.',        4, 'Charging speed is amazing on road trips.'), 
 (3, 11, 2, 4, NULL, 'Not tested in winter yet.',                         '7kW home charger works fine.',                         'Google apps update smoothly.',            4, 'Great build quality.'); 

-- ---------------- test-drive slots (UC-11) ---------------- 
INSERT INTO test_drive_slots (id, vehicle_id, drive_type, slot_start, slot_minutes) VALUES 
 (1, 1,  'AT_HOME', CAST(CURRENT_DATE AS TIMESTAMP) + INTERVAL '2' DAY + INTERVAL '10' HOUR, 60), 
 (2, 1,  'AT_HOME', CAST(CURRENT_DATE AS TIMESTAMP) + INTERVAL '2' DAY + INTERVAL '14' HOUR, 60), 
 (3, 1,  'VIRTUAL', CAST(CURRENT_DATE AS TIMESTAMP) + INTERVAL '1' DAY + INTERVAL '18' HOUR, 30), 
 (4, 3,  'AT_HOME', CAST(CURRENT_DATE AS TIMESTAMP) + INTERVAL '3' DAY + INTERVAL '11' HOUR, 60), 
 (5, 3,  'VIRTUAL', CAST(CURRENT_DATE AS TIMESTAMP) + INTERVAL '1' DAY + INTERVAL '19' HOUR, 30), 
 (6, 5,  'AT_HOME', CAST(CURRENT_DATE AS TIMESTAMP) + INTERVAL '4' DAY + INTERVAL '10' HOUR, 60), 
 (7, 6,  'VIRTUAL', CAST(CURRENT_DATE AS TIMESTAMP) + INTERVAL '2' DAY + INTERVAL '17' HOUR, 30), 
 (8, 9,  'AT_HOME', CAST(CURRENT_DATE AS TIMESTAMP) + INTERVAL '5' DAY + INTERVAL '13' HOUR, 90), 
 (9, 13, 'AT_HOME', CAST(CURRENT_DATE AS TIMESTAMP) + INTERVAL '3' DAY + INTERVAL '15' HOUR, 60); 

INSERT INTO test_drive_bookings (id, slot_id, user_id, address, status) VALUES 
 (1, 2, 3, '100 Queen St W, Toronto, ON', 'BOOKED'); 

-- ---------------- visit events (UC-18 / UC-19) ---------------- 
INSERT INTO visit_events (session_id, user_id, vehicle_id, event_type, created_at) VALUES 
 ('s-001', NULL, 1,  'VIEW',     CURRENT_TIMESTAMP - INTERVAL '20' DAY), 
 ('s-001', NULL, 3,  'VIEW',     CURRENT_TIMESTAMP - INTERVAL '20' DAY), 
 ('s-002', 2,    3,  'VIEW',     CURRENT_TIMESTAMP - INTERVAL '13' DAY), 
 ('s-002', 2,    3,  'CART',     CURRENT_TIMESTAMP - INTERVAL '13' DAY), 
 ('s-002', 2,    3,  'PURCHASE', CURRENT_TIMESTAMP - INTERVAL '12' DAY), 
 ('s-003', NULL, 2,  'VIEW',     CURRENT_TIMESTAMP - INTERVAL '8' DAY), 
 ('s-003', NULL, 7,  'VIEW',     CURRENT_TIMESTAMP - INTERVAL '8' DAY), 
 ('s-003', NULL, 7,  'CART',     CURRENT_TIMESTAMP - INTERVAL '8' DAY), 
 ('s-004', 3,    11, 'VIEW',     CURRENT_TIMESTAMP - INTERVAL '4' DAY), 
 ('s-004', 3,    11, 'CART',     CURRENT_TIMESTAMP - INTERVAL '4' DAY), 
 ('s-004', 3,    11, 'PURCHASE', CURRENT_TIMESTAMP - INTERVAL '3' DAY), 
 ('s-005', NULL, 6,  'VIEW',     CURRENT_TIMESTAMP - INTERVAL '1' DAY), 
 ('s-005', NULL, 9,  'VIEW',     CURRENT_TIMESTAMP - INTERVAL '1' DAY), 
 ('s-006', 4,    12, 'VIEW',     CURRENT_TIMESTAMP - INTERVAL '2' HOUR); 

-- ---------------- restart identity columns past the seed ids ---------------- 
ALTER TABLE users                 ALTER COLUMN id RESTART WITH 1000; 
ALTER TABLE addresses             ALTER COLUMN id RESTART WITH 1000; 
ALTER TABLE vehicles              ALTER COLUMN id RESTART WITH 1000; 
ALTER TABLE vehicle_history       ALTER COLUMN id RESTART WITH 1000; 
ALTER TABLE customization_options ALTER COLUMN id RESTART WITH 1000; 
ALTER TABLE carts                 ALTER COLUMN id RESTART WITH 1000; 
ALTER TABLE cart_items            ALTER COLUMN id RESTART WITH 1000; 
ALTER TABLE orders                ALTER COLUMN id RESTART WITH 1000; 
ALTER TABLE order_items           ALTER COLUMN id RESTART WITH 1000; 
ALTER TABLE reviews               ALTER COLUMN id RESTART WITH 1000; 
ALTER TABLE test_drive_slots      ALTER COLUMN id RESTART WITH 1000; 
ALTER TABLE test_drive_bookings   ALTER COLUMN id RESTART WITH 1000; 
ALTER TABLE visit_events          ALTER COLUMN id RESTART WITH 1000; 
