-- Seed Initial Data for Garment Manufacturing WMS
-- ANSI SQL compatible with both PostgreSQL and H2

-- 1. Initial Styles
INSERT INTO styles (style_code, design_name, category, fabric_details)
SELECT 'STY-101', 'Oxford Casual Button-Down', 'Shirts', '100% Combed Cotton 40s Poplin'
WHERE NOT EXISTS (SELECT 1 FROM styles WHERE style_code = 'STY-101');

INSERT INTO styles (style_code, design_name, category, fabric_details)
SELECT 'STY-202', 'Slim Tapered Stretch Chino', 'Trousers', '98% Cotton 2% Spandex Twill'
WHERE NOT EXISTS (SELECT 1 FROM styles WHERE style_code = 'STY-202');

INSERT INTO styles (style_code, design_name, category, fabric_details)
SELECT 'STY-303', 'Raw Selvedge Denim Jacket', 'Outerwear', '13.5 oz Japanese Indigo Selvedge Denim'
WHERE NOT EXISTS (SELECT 1 FROM styles WHERE style_code = 'STY-303');

-- 2. Size Sets
INSERT INTO size_sets (id, set_name, description, total_ratio_pieces)
SELECT 1, 'Ratio 12 Set (30-36)', 'Standard 12-piece trouser/waist ratio set', 12
WHERE NOT EXISTS (SELECT 1 FROM size_sets WHERE id = 1);

INSERT INTO size_sets (id, set_name, description, total_ratio_pieces)
SELECT 2, 'Standard S-XXL Set (Ratio 10)', 'Standard 10-piece shirt/top ratio set', 10
WHERE NOT EXISTS (SELECT 1 FROM size_sets WHERE id = 2);

-- 3. Size Set Breakdown Ratios
INSERT INTO size_set_ratios (size_set_id, size_name, ratio_count, sort_order)
SELECT 1, '30', 1, 1 WHERE NOT EXISTS (SELECT 1 FROM size_set_ratios WHERE size_set_id = 1 AND size_name = '30');
INSERT INTO size_set_ratios (size_set_id, size_name, ratio_count, sort_order)
SELECT 1, '32', 3, 2 WHERE NOT EXISTS (SELECT 1 FROM size_set_ratios WHERE size_set_id = 1 AND size_name = '32');
INSERT INTO size_set_ratios (size_set_id, size_name, ratio_count, sort_order)
SELECT 1, '33', 3, 3 WHERE NOT EXISTS (SELECT 1 FROM size_set_ratios WHERE size_set_id = 1 AND size_name = '33');
INSERT INTO size_set_ratios (size_set_id, size_name, ratio_count, sort_order)
SELECT 1, '34', 3, 4 WHERE NOT EXISTS (SELECT 1 FROM size_set_ratios WHERE size_set_id = 1 AND size_name = '34');
INSERT INTO size_set_ratios (size_set_id, size_name, ratio_count, sort_order)
SELECT 1, '36', 2, 5 WHERE NOT EXISTS (SELECT 1 FROM size_set_ratios WHERE size_set_id = 1 AND size_name = '36');

INSERT INTO size_set_ratios (size_set_id, size_name, ratio_count, sort_order)
SELECT 2, 'S', 1, 1 WHERE NOT EXISTS (SELECT 1 FROM size_set_ratios WHERE size_set_id = 2 AND size_name = 'S');
INSERT INTO size_set_ratios (size_set_id, size_name, ratio_count, sort_order)
SELECT 2, 'M', 3, 2 WHERE NOT EXISTS (SELECT 1 FROM size_set_ratios WHERE size_set_id = 2 AND size_name = 'M');
INSERT INTO size_set_ratios (size_set_id, size_name, ratio_count, sort_order)
SELECT 2, 'L', 3, 3 WHERE NOT EXISTS (SELECT 1 FROM size_set_ratios WHERE size_set_id = 2 AND size_name = 'L');
INSERT INTO size_set_ratios (size_set_id, size_name, ratio_count, sort_order)
SELECT 2, 'XL', 2, 4 WHERE NOT EXISTS (SELECT 1 FROM size_set_ratios WHERE size_set_id = 2 AND size_name = 'XL');
INSERT INTO size_set_ratios (size_set_id, size_name, ratio_count, sort_order)
SELECT 2, 'XXL', 1, 5 WHERE NOT EXISTS (SELECT 1 FROM size_set_ratios WHERE size_set_id = 2 AND size_name = 'XXL');

-- 4. Finishing Units (Sources for Inward)
INSERT INTO finishing_units (unit_code, unit_name, contact_person, phone, address, active)
SELECT 'FU-APEX', 'Apex Garment Finishing & Dyeing', 'Rajesh Sharma', '+91-98200-11223', 'Plot 45, Sector 8, Industrial Area, Surat', true
WHERE NOT EXISTS (SELECT 1 FROM finishing_units WHERE unit_code = 'FU-APEX');

INSERT INTO finishing_units (unit_code, unit_name, contact_person, phone, address, active)
SELECT 'FU-CREST', 'Crestview Steam, Press & Packing', 'Sunil Verma', '+91-98330-44556', 'Unit 12, Apparel Park, Tirupur', true
WHERE NOT EXISTS (SELECT 1 FROM finishing_units WHERE unit_code = 'FU-CREST');

-- 5. Customers (Destinations for Outward)
INSERT INTO customers (customer_code, customer_name, contact_person, phone, address, active)
SELECT 'CUST-NORDIC', 'Nordic Retail Brands Pvt Ltd', 'Anita Desai', '+91-99100-77889', 'Central Distribution Center, Bhiwandi, Mumbai', true
WHERE NOT EXISTS (SELECT 1 FROM customers WHERE customer_code = 'CUST-NORDIC');

INSERT INTO customers (customer_code, customer_name, contact_person, phone, address, active)
SELECT 'CUST-URBAN', 'Urban Vogue Warehousing', 'Karan Mehra', '+91-99200-99001', 'Logistics Park, Hosur Road, Bangalore', true
WHERE NOT EXISTS (SELECT 1 FROM customers WHERE customer_code = 'CUST-URBAN');

-- 6. Label Presets
INSERT INTO label_settings (id, user_id, preset_name, width_mm, height_mm, is_default, tspl_density, tspl_direction)
SELECT 1, NULL, '50mm x 25mm (Single Piece / Wash Tag)', 50.0, 25.0, false, 8, 1
WHERE NOT EXISTS (SELECT 1 FROM label_settings WHERE id = 1);

INSERT INTO label_settings (id, user_id, preset_name, width_mm, height_mm, is_default, tspl_density, tspl_direction)
SELECT 2, NULL, '50mm x 38mm (Garment Price / Barcode Tag)', 50.0, 38.0, true, 8, 1
WHERE NOT EXISTS (SELECT 1 FROM label_settings WHERE id = 2);

INSERT INTO label_settings (id, user_id, preset_name, width_mm, height_mm, is_default, tspl_density, tspl_direction)
SELECT 3, NULL, '100mm x 50mm (Set Bundle / Master Carton)', 100.0, 50.0, false, 8, 1
WHERE NOT EXISTS (SELECT 1 FROM label_settings WHERE id = 3);
