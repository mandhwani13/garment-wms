-- V3 Enterprise Garment Manufacturing ERP & WMS Lifecycle
-- Patterns inspired by ERPNext (Subcontracting, Multi-location WIP, Stock Transfers) & Odoo (Attribute Matrix, Package Hierarchy)

-- 1. Locations (Virtual Warehouse Model: Central Stores & Subcontractor Units)
CREATE TABLE IF NOT EXISTS locations (
    id BIGSERIAL PRIMARY KEY,
    location_code VARCHAR(50) NOT NULL UNIQUE,
    location_name VARCHAR(100) NOT NULL,
    location_type VARCHAR(30) NOT NULL, -- 'INTERNAL_WAREHOUSE', 'SUBCONTRACTOR_WIP', 'TRANSIT', 'CUSTOMER'
    address TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 2. Party Masters (Mills, Stitching, Job-workers, Washing, Finishing, Buyers)
CREATE TABLE IF NOT EXISTS parties (
    id BIGSERIAL PRIMARY KEY,
    party_code VARCHAR(50) NOT NULL UNIQUE,
    party_name VARCHAR(100) NOT NULL,
    party_type VARCHAR(30) NOT NULL, -- 'FABRIC_MILL', 'STITCHING_UNIT', 'JOB_WORKER', 'WASHING_UNIT', 'FINISHING_UNIT', 'CUSTOMER_BUYER'
    city VARCHAR(100),
    state VARCHAR(100),
    contact_person VARCHAR(100),
    phone VARCHAR(30),
    gstin VARCHAR(30),
    address TEXT,
    linked_location_id BIGINT REFERENCES locations(id),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 3. Brand Masters (Unlimited Brand Catalog)
CREATE TABLE IF NOT EXISTS brands (
    id BIGSERIAL PRIMARY KEY,
    brand_code VARCHAR(50) NOT NULL UNIQUE,
    brand_name VARCHAR(100) NOT NULL,
    description TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 4. Fabric Masters (Short Number, Weave, Shrinkage %)
CREATE TABLE IF NOT EXISTS fabric_masters (
    id BIGSERIAL PRIMARY KEY,
    short_number VARCHAR(50) NOT NULL UNIQUE,
    fabric_name VARCHAR(100) NOT NULL,
    weave VARCHAR(50),
    width_inches DOUBLE PRECISION NOT NULL DEFAULT 58.0,
    composition VARCHAR(150),
    standard_shrinkage_pct DOUBLE PRECISION NOT NULL DEFAULT 3.0,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 5. Trim & Accessory Masters (BOM Elements)
CREATE TABLE IF NOT EXISTS trim_accessories (
    id BIGSERIAL PRIMARY KEY,
    item_code VARCHAR(50) NOT NULL UNIQUE,
    item_type VARCHAR(50) NOT NULL, -- 'MAIN_LABEL', 'SIZE_LABEL', 'LEATHER_PATCH', 'BUTTON', 'RIVET', 'HANG_TAG', 'POLYBAG', 'POCKETING_FABRIC'
    item_name VARCHAR(100) NOT NULL,
    brand_id BIGINT REFERENCES brands(id),
    uom VARCHAR(20) NOT NULL DEFAULT 'PIECES', -- 'PIECES', 'GROSS', 'METERS'
    unit_rate DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    stock_quantity DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    alert_threshold DOUBLE PRECISION NOT NULL DEFAULT 50.0,
    consumption_per_piece DOUBLE PRECISION NOT NULL DEFAULT 1.0,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 6. Fabric Purchase Orders & Drop-Shipment
CREATE TABLE IF NOT EXISTS fabric_purchase_orders (
    id BIGSERIAL PRIMARY KEY,
    consignment_no VARCHAR(50) NOT NULL UNIQUE,
    po_bill_no VARCHAR(50) NOT NULL,
    fabric_mill_id BIGINT NOT NULL REFERENCES parties(id),
    destination_stitching_unit_id BIGINT NOT NULL REFERENCES parties(id),
    fabric_id BIGINT NOT NULL REFERENCES fabric_masters(id),
    total_rolls INT NOT NULL DEFAULT 1,
    total_billed_meters DOUBLE PRECISION NOT NULL,
    rate_per_meter DOUBLE PRECISION NOT NULL,
    total_amount DOUBLE PRECISION NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'IN_TRANSIT', -- 'IN_TRANSIT', 'INWARDED', 'DISCREPANCY_PENDING', 'APPROVED'
    transit_slip_no VARCHAR(100),
    grn_number VARCHAR(50),
    admin_approval_notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    inwarded_at TIMESTAMP WITH TIME ZONE
);

-- 7. Fabric Roll-wise Inward & Inspection
CREATE TABLE IF NOT EXISTS fabric_roll_inwards (
    id BIGSERIAL PRIMARY KEY,
    po_id BIGINT NOT NULL REFERENCES fabric_purchase_orders(id) ON DELETE CASCADE,
    roll_number VARCHAR(30) NOT NULL,
    billed_meters DOUBLE PRECISION NOT NULL,
    received_meters DOUBLE PRECISION NOT NULL,
    weight_kg DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    has_defects BOOLEAN NOT NULL DEFAULT FALSE,
    defect_notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 8. Marker Approvals & Yield Gating (Pre-cutting approval)
CREATE TABLE IF NOT EXISTS marker_approvals (
    id BIGSERIAL PRIMARY KEY,
    marker_code VARCHAR(50) NOT NULL UNIQUE,
    style_id BIGINT NOT NULL REFERENCES styles(id),
    stitching_unit_id BIGINT NOT NULL REFERENCES parties(id),
    fabric_id BIGINT NOT NULL REFERENCES fabric_masters(id),
    roll_width_utilized DOUBLE PRECISION NOT NULL,
    marker_length_meters DOUBLE PRECISION NOT NULL,
    lay_count INT NOT NULL,
    calculated_average_meters DOUBLE PRECISION NOT NULL,
    expected_pieces INT NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING', -- 'PENDING', 'APPROVED', 'REJECTED'
    admin_remarks TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    approved_at TIMESTAMP WITH TIME ZONE
);

-- 9. Subcontracting Job Work Challans (Embroidery, Printing, Emboss)
CREATE TABLE IF NOT EXISTS job_work_challans (
    id BIGSERIAL PRIMARY KEY,
    challan_no VARCHAR(50) NOT NULL UNIQUE,
    cutting_lot_id BIGINT NOT NULL REFERENCES cutting_lots(id),
    job_worker_id BIGINT NOT NULL REFERENCES parties(id),
    component_type VARCHAR(50) NOT NULL, -- 'BACK_POCKET_EMBROIDERY', 'WAISTBAND_EMBOSS', 'FRONT_PANEL_LASER_PRINT', 'CHEST_EMBROIDERY'
    pieces_sent INT NOT NULL,
    pieces_received INT NOT NULL DEFAULT 0,
    damaged_pieces INT NOT NULL DEFAULT 0,
    status VARCHAR(30) NOT NULL DEFAULT 'ISSUED', -- 'ISSUED', 'RECEIVED_COMPLETED', 'DISCREPANCY'
    notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    returned_at TIMESTAMP WITH TIME ZONE
);

-- 10. Washing Batches & Color Lot Splitting
CREATE TABLE IF NOT EXISTS washing_batches (
    id BIGSERIAL PRIMARY KEY,
    batch_no VARCHAR(50) NOT NULL UNIQUE,
    cutting_lot_id BIGINT NOT NULL REFERENCES cutting_lots(id),
    washing_unit_id BIGINT NOT NULL REFERENCES parties(id),
    color_name VARCHAR(50) NOT NULL,
    pieces_in INT NOT NULL,
    pieces_out INT NOT NULL DEFAULT 0,
    shrinkage_actual_pct DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    rejection_pieces INT NOT NULL DEFAULT 0,
    status VARCHAR(30) NOT NULL DEFAULT 'SENT_TO_WASH', -- 'SENT_TO_WASH', 'WASHED_COMPLETED'
    notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP WITH TIME ZONE
);

-- 11. Brand Division & Finishing Programs
CREATE TABLE IF NOT EXISTS finishing_programs (
    id BIGSERIAL PRIMARY KEY,
    program_no VARCHAR(50) NOT NULL UNIQUE,
    cutting_lot_id BIGINT NOT NULL REFERENCES cutting_lots(id),
    brand_id BIGINT NOT NULL REFERENCES brands(id),
    finishing_unit_id BIGINT NOT NULL REFERENCES parties(id),
    pieces_allocated INT NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING_TRIMS', -- 'PENDING_TRIMS', 'TRIMS_ISSUED', 'PACKED_COMPLETED'
    dispatch_slip_no VARCHAR(50),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 12. Trim Issuance (BOM Deductions per Finishing Program)
CREATE TABLE IF NOT EXISTS trim_issuances (
    id BIGSERIAL PRIMARY KEY,
    finishing_program_id BIGINT NOT NULL REFERENCES finishing_programs(id) ON DELETE CASCADE,
    trim_id BIGINT NOT NULL REFERENCES trim_accessories(id),
    required_qty DOUBLE PRECISION NOT NULL,
    issued_qty DOUBLE PRECISION NOT NULL,
    issued_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 13. Stock Transfers (ERPNext 2-Step Location Inventory Ledger)
CREATE TABLE IF NOT EXISTS stock_transfers (
    id BIGSERIAL PRIMARY KEY,
    transfer_no VARCHAR(50) NOT NULL UNIQUE,
    from_location_id BIGINT NOT NULL REFERENCES locations(id),
    to_location_id BIGINT NOT NULL REFERENCES locations(id),
    item_type VARCHAR(30) NOT NULL, -- 'FABRIC', 'TRIM', 'CUT_PANELS', 'WASHED_GARMENTS', 'FINISHED_PACK'
    reference_id VARCHAR(50),
    quantity DOUBLE PRECISION NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'DISPATCHED', -- 'DISPATCHED', 'IN_TRANSIT', 'RECEIVED'
    remarks TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    received_at TIMESTAMP WITH TIME ZONE
);

-- 14. Enhance Styles with Brand and Fabric links
ALTER TABLE styles ADD COLUMN IF NOT EXISTS brand_id BIGINT REFERENCES brands(id);
ALTER TABLE styles ADD COLUMN IF NOT EXISTS fabric_id BIGINT REFERENCES fabric_masters(id);

-- 15. Enhance Single Pieces with Brand and Location tracking
ALTER TABLE single_pieces ADD COLUMN IF NOT EXISTS brand_id BIGINT REFERENCES brands(id);
ALTER TABLE single_pieces ADD COLUMN IF NOT EXISTS current_location_id BIGINT REFERENCES locations(id);
ALTER TABLE set_bundles ADD COLUMN IF NOT EXISTS current_location_id BIGINT REFERENCES locations(id);

-- Indexes for performance
CREATE INDEX IF NOT EXISTS idx_parties_type ON parties(party_type);
CREATE INDEX IF NOT EXISTS idx_fabric_po_consignment ON fabric_purchase_orders(consignment_no);
CREATE INDEX IF NOT EXISTS idx_marker_approvals_status ON marker_approvals(status);
CREATE INDEX IF NOT EXISTS idx_job_work_lot ON job_work_challans(cutting_lot_id);
CREATE INDEX IF NOT EXISTS idx_washing_batches_lot ON washing_batches(cutting_lot_id);
CREATE INDEX IF NOT EXISTS idx_finishing_programs_brand ON finishing_programs(brand_id);
CREATE INDEX IF NOT EXISTS idx_trim_acc_type ON trim_accessories(item_type);
CREATE INDEX IF NOT EXISTS idx_stock_transfers_status ON stock_transfers(status);
