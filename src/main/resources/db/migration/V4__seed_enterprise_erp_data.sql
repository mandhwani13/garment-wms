-- V4 Enterprise Seed Data (Locations, Parties, Brands, Fabrics, Trims, and Demo Pipeline)

-- 1. Virtual Warehouse Locations
INSERT INTO locations (id, location_code, location_name, location_type, address)
SELECT 1, 'LOC-CENTRAL-FABRIC', 'Central Fabric Warehouse', 'INTERNAL_WAREHOUSE', 'Sector 18, Textile Hub, Bhiwandi, Maharashtra'
WHERE NOT EXISTS (SELECT 1 FROM locations WHERE id = 1);

INSERT INTO locations (id, location_code, location_name, location_type, address)
SELECT 2, 'LOC-CENTRAL-TRIMS', 'Central Trims & Accessories Store', 'INTERNAL_WAREHOUSE', 'Ground Floor, Plot 14, Okhla Phase 3, New Delhi'
WHERE NOT EXISTS (SELECT 1 FROM locations WHERE id = 2);

INSERT INTO locations (id, location_code, location_name, location_type, address)
SELECT 3, 'LOC-CENTRAL-FG', 'Central Finished Goods Warehouse', 'INTERNAL_WAREHOUSE', 'Logistics Park, Hosur Road, Bangalore, Karnataka'
WHERE NOT EXISTS (SELECT 1 FROM locations WHERE id = 3);

INSERT INTO locations (id, location_code, location_name, location_type, address)
SELECT 4, 'LOC-WIP-STITCH-01', 'Arvind Stitching Plant WIP', 'SUBCONTRACTOR_WIP', 'MIDC Industrial Area, Tarapur, Maharashtra'
WHERE NOT EXISTS (SELECT 1 FROM locations WHERE id = 4);

INSERT INTO locations (id, location_code, location_name, location_type, address)
SELECT 5, 'LOC-WIP-EMB-01', 'Zari & Needle Embroidery WIP', 'SUBCONTRACTOR_WIP', 'GIDC Sachin, Surat, Gujarat'
WHERE NOT EXISTS (SELECT 1 FROM locations WHERE id = 5);

INSERT INTO locations (id, location_code, location_name, location_type, address)
SELECT 6, 'LOC-WIP-WASH-01', 'AquaLuxe Denim Washing Plant WIP', 'SUBCONTRACTOR_WIP', 'Ring Road Textile Park, Ahmedabad, Gujarat'
WHERE NOT EXISTS (SELECT 1 FROM locations WHERE id = 6);

INSERT INTO locations (id, location_code, location_name, location_type, address)
SELECT 7, 'LOC-WIP-FINISH-01', 'Apex Finishing & Packing Unit WIP', 'SUBCONTRACTOR_WIP', 'Apparel Park, Tirupur, Tamil Nadu'
WHERE NOT EXISTS (SELECT 1 FROM locations WHERE id = 7);

-- 2. Parties
INSERT INTO parties (id, party_code, party_name, party_type, city, state, contact_person, phone, gstin, address, linked_location_id)
SELECT 1, 'MILL-VARDHMAN', 'Vardhman Textiles Mill Ltd', 'FABRIC_MILL', 'Ludhiana', 'Punjab', 'Ramesh Jindal', '+91-98140-12345', '03AAACV1234F1Z1', 'Chandigarh Road, Ludhiana', 1
WHERE NOT EXISTS (SELECT 1 FROM parties WHERE id = 1);

INSERT INTO parties (id, party_code, party_name, party_type, city, state, contact_person, phone, gstin, address, linked_location_id)
SELECT 2, 'STITCH-ARVIND', 'Arvind Stitching Solutions Unit 4', 'STITCHING_UNIT', 'Tarapur', 'Maharashtra', 'Manoj Kulkarni', '+91-98220-45678', '27AAACA9876E1Z5', 'MIDC Industrial Area, Tarapur', 4
WHERE NOT EXISTS (SELECT 1 FROM parties WHERE id = 2);

INSERT INTO parties (id, party_code, party_name, party_type, city, state, contact_person, phone, gstin, address, linked_location_id)
SELECT 3, 'JW-EMB-ZARI', 'Zari & Needle Embroidery Craft', 'JOB_WORKER', 'Surat', 'Gujarat', 'Harish Patel', '+91-98790-23456', '24AAACZ4321A1Z9', 'GIDC Sachin, Surat', 5
WHERE NOT EXISTS (SELECT 1 FROM parties WHERE id = 3);

INSERT INTO parties (id, party_code, party_name, party_type, city, state, contact_person, phone, gstin, address, linked_location_id)
SELECT 4, 'WASH-AQUALUXE', 'AquaLuxe Sustainable Wash Plant', 'WASHING_UNIT', 'Ahmedabad', 'Gujarat', 'Vikram Shah', '+91-98250-87654', '24AAACA5678C1Z8', 'Ring Road Textile Park, Ahmedabad', 6
WHERE NOT EXISTS (SELECT 1 FROM parties WHERE id = 4);

INSERT INTO parties (id, party_code, party_name, party_type, city, state, contact_person, phone, gstin, address, linked_location_id)
SELECT 5, 'FIN-APEX', 'Apex Finishing & Garment Packing', 'FINISHING_UNIT', 'Tirupur', 'Tamil Nadu', 'S. Natarajan', '+91-98420-11223', '33AAACA3344D1Z2', 'Apparel Park, Tirupur', 7
WHERE NOT EXISTS (SELECT 1 FROM parties WHERE id = 5);

INSERT INTO parties (id, party_code, party_name, party_type, city, state, contact_person, phone, gstin, address, linked_location_id)
SELECT 6, 'BUYER-NORDIC', 'Nordic Retail Brands Pvt Ltd', 'CUSTOMER_BUYER', 'Mumbai', 'Maharashtra', 'Anita Desai', '+91-99100-77889', '27AAACN5566G1Z4', 'Central Distribution Center, Bhiwandi, Mumbai', NULL
WHERE NOT EXISTS (SELECT 1 FROM parties WHERE id = 6);

-- 3. Brands Master
INSERT INTO brands (id, brand_code, brand_name, description)
SELECT 1, 'BR-DENIMCO', 'DenimCo Premium Jeans', 'Flagship selvedge and raw stretch denim apparel brand'
WHERE NOT EXISTS (SELECT 1 FROM brands WHERE id = 1);

INSERT INTO brands (id, brand_code, brand_name, description)
SELECT 2, 'BR-URBANVOGUE', 'Urban Vogue Studio', 'Contemporary minimalist street-smart casual wear'
WHERE NOT EXISTS (SELECT 1 FROM brands WHERE id = 2);

INSERT INTO brands (id, brand_code, brand_name, description)
SELECT 3, 'BR-INDIGOROOTS', 'Indigo Roots Heritage', 'Artisanal natural-dye washed shirts and trousers'
WHERE NOT EXISTS (SELECT 1 FROM brands WHERE id = 3);

-- 4. Fabric Master
INSERT INTO fabric_masters (id, short_number, fabric_name, weave, width_inches, composition, standard_shrinkage_pct)
SELECT 1, 'FAB-DN-135', '13.5 oz Japanese Indigo Selvedge Denim', '3x1 Right Hand Twill', 58.0, '100% Ring Spun Cotton', 3.5
WHERE NOT EXISTS (SELECT 1 FROM fabric_masters WHERE id = 1);

INSERT INTO fabric_masters (id, short_number, fabric_name, weave, width_inches, composition, standard_shrinkage_pct)
SELECT 2, 'FAB-CH-280', 'Twill Stretch Cotton Chino Fabric', '2x1 Twill', 56.0, '98% Cotton 2% Lycra Spandex', 2.8
WHERE NOT EXISTS (SELECT 1 FROM fabric_masters WHERE id = 2);

INSERT INTO fabric_masters (id, short_number, fabric_name, weave, width_inches, composition, standard_shrinkage_pct)
SELECT 3, 'FAB-OX-40S', 'Oxford 40s Cotton Shirting', 'Basket Weave', 58.0, '100% Combed Cotton', 2.0
WHERE NOT EXISTS (SELECT 1 FROM fabric_masters WHERE id = 3);

-- 5. Trim & Accessories (Central Store stock & BOM items)
INSERT INTO trim_accessories (id, item_code, item_type, item_name, brand_id, uom, unit_rate, stock_quantity, alert_threshold, consumption_per_piece)
SELECT 1, 'TRIM-LBL-DN', 'MAIN_LABEL', 'DenimCo Woven Damask Main Neck/Waist Label', 1, 'PIECES', 4.5, 12500, 1000, 1.0
WHERE NOT EXISTS (SELECT 1 FROM trim_accessories WHERE id = 1);

INSERT INTO trim_accessories (id, item_code, item_type, item_name, brand_id, uom, unit_rate, stock_quantity, alert_threshold, consumption_per_piece)
SELECT 2, 'TRIM-PATCH-DN', 'LEATHER_PATCH', 'DenimCo Embossed Genuine Leather Back Patch', 1, 'PIECES', 18.0, 8400, 500, 1.0
WHERE NOT EXISTS (SELECT 1 FROM trim_accessories WHERE id = 2);

INSERT INTO trim_accessories (id, item_code, item_type, item_name, brand_id, uom, unit_rate, stock_quantity, alert_threshold, consumption_per_piece)
SELECT 3, 'TRIM-BTN-BRASS', 'BUTTON', 'Antique Brass Shank Button 17mm', NULL, 'PIECES', 3.2, 25000, 2000, 1.0
WHERE NOT EXISTS (SELECT 1 FROM trim_accessories WHERE id = 3);

INSERT INTO trim_accessories (id, item_code, item_type, item_name, brand_id, uom, unit_rate, stock_quantity, alert_threshold, consumption_per_piece)
SELECT 4, 'TRIM-RIV-COPPER', 'RIVET', 'Copper Dome Pocket Rivet 9mm', NULL, 'PIECES', 1.1, 80000, 5000, 5.0
WHERE NOT EXISTS (SELECT 1 FROM trim_accessories WHERE id = 4);

INSERT INTO trim_accessories (id, item_code, item_type, item_name, brand_id, uom, unit_rate, stock_quantity, alert_threshold, consumption_per_piece)
SELECT 5, 'TRIM-TAG-DN', 'HANG_TAG', 'DenimCo Raw Kraft Hangtag with Barcode space', 1, 'PIECES', 5.0, 15000, 1000, 1.0
WHERE NOT EXISTS (SELECT 1 FROM trim_accessories WHERE id = 5);

INSERT INTO trim_accessories (id, item_code, item_type, item_name, brand_id, uom, unit_rate, stock_quantity, alert_threshold, consumption_per_piece)
SELECT 6, 'TRIM-POLY-M', 'POLYBAG', 'Recyclable Self-Adhesive Polybag 14x18 inch', NULL, 'PIECES', 2.0, 30000, 2500, 1.0
WHERE NOT EXISTS (SELECT 1 FROM trim_accessories WHERE id = 6);

-- 6. Link Styles with Brand & Fabric
UPDATE styles SET brand_id = 1, fabric_id = 1 WHERE style_code = 'STY-303' AND brand_id IS NULL;
UPDATE styles SET brand_id = 2, fabric_id = 2 WHERE style_code = 'STY-202' AND brand_id IS NULL;
UPDATE styles SET brand_id = 3, fabric_id = 3 WHERE style_code = 'STY-101' AND brand_id IS NULL;

-- 7. Initial Fabric Purchase Order (Phase 1 Demo)
INSERT INTO fabric_purchase_orders (id, consignment_no, po_bill_no, fabric_mill_id, destination_stitching_unit_id, fabric_id, total_rolls, total_billed_meters, rate_per_meter, total_amount, status, transit_slip_no, grn_number)
SELECT 1, 'FCON-2026-001', 'PO-VARD-9081', 1, 2, 1, 10, 1200.0, 285.0, 342000.0, 'INWARDED', 'TRN-SLIP-4411', 'GRN-ARV-001'
WHERE NOT EXISTS (SELECT 1 FROM fabric_purchase_orders WHERE id = 1);

-- 8. Marker Approval Sample (Phase 2 Demo)
INSERT INTO marker_approvals (id, marker_code, style_id, stitching_unit_id, fabric_id, roll_width_utilized, marker_length_meters, lay_count, calculated_average_meters, expected_pieces, status, admin_remarks)
SELECT 1, 'MKR-DN303-01', 3, 2, 1, 57.5, 6.25, 128, 1.45, 800, 'APPROVED', 'Yield average 1.45m/piece verified against 13.5oz denim lay plan'
WHERE NOT EXISTS (SELECT 1 FROM marker_approvals WHERE id = 1);
