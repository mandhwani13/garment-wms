-- Garment Manufacturing Warehouse & Inventory Management System
-- V1 Schema Initialization with Optimized Indexing

-- 1. App Users (RBAC)
CREATE TABLE app_users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role VARCHAR(30) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 2. Styles / Design Master
CREATE TABLE styles (
    id BIGSERIAL PRIMARY KEY,
    style_code VARCHAR(50) NOT NULL UNIQUE,
    design_name VARCHAR(100) NOT NULL,
    category VARCHAR(50),
    fabric_details VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 3. Size Sets Master
CREATE TABLE size_sets (
    id BIGSERIAL PRIMARY KEY,
    set_name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    total_ratio_pieces INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 4. Size Set Ratio Breakdowns
CREATE TABLE size_set_ratios (
    id BIGSERIAL PRIMARY KEY,
    size_set_id BIGINT NOT NULL REFERENCES size_sets(id) ON DELETE CASCADE,
    size_name VARCHAR(30) NOT NULL,
    ratio_count INT NOT NULL DEFAULT 1,
    sort_order INT NOT NULL DEFAULT 0
);

-- 5. Finishing Units (Sources for Inward)
CREATE TABLE finishing_units (
    id BIGSERIAL PRIMARY KEY,
    unit_code VARCHAR(50) NOT NULL UNIQUE,
    unit_name VARCHAR(100) NOT NULL,
    contact_person VARCHAR(100),
    phone VARCHAR(30),
    address TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 6. Customers (Destinations for Outward)
CREATE TABLE customers (
    id BIGSERIAL PRIMARY KEY,
    customer_code VARCHAR(50) NOT NULL UNIQUE,
    customer_name VARCHAR(100) NOT NULL,
    contact_person VARCHAR(100),
    phone VARCHAR(30),
    address TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 7. Cutting Lots
CREATE TABLE cutting_lots (
    id BIGSERIAL PRIMARY KEY,
    lot_number VARCHAR(50) NOT NULL UNIQUE,
    style_id BIGINT NOT NULL REFERENCES styles(id),
    size_set_id BIGINT NOT NULL REFERENCES size_sets(id),
    total_pieces INT NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PLANNED',
    created_by VARCHAR(50),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 8. Cutting Lot Colors
CREATE TABLE cutting_lot_colors (
    id BIGSERIAL PRIMARY KEY,
    cutting_lot_id BIGINT NOT NULL REFERENCES cutting_lots(id) ON DELETE CASCADE,
    color_name VARCHAR(50) NOT NULL,
    pieces_allocated INT NOT NULL,
    calculated_sets INT NOT NULL,
    loose_pieces INT NOT NULL
);

-- 9. Set Bundles (Parent Barcodes)
CREATE TABLE set_bundles (
    id BIGSERIAL PRIMARY KEY,
    barcode VARCHAR(100) NOT NULL UNIQUE,
    cutting_lot_id BIGINT NOT NULL REFERENCES cutting_lots(id),
    color_name VARCHAR(50) NOT NULL,
    set_index INT NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PLANNED',
    current_finishing_unit_id BIGINT REFERENCES finishing_units(id),
    current_customer_id BIGINT REFERENCES customers(id),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 10. Single Pieces (Child Barcodes linked to parent Set)
CREATE TABLE single_pieces (
    id BIGSERIAL PRIMARY KEY,
    barcode VARCHAR(100) NOT NULL UNIQUE,
    parent_set_id BIGINT REFERENCES set_bundles(id) ON DELETE SET NULL,
    cutting_lot_id BIGINT NOT NULL REFERENCES cutting_lots(id),
    color_name VARCHAR(50) NOT NULL,
    size_name VARCHAR(30) NOT NULL,
    is_loose BOOLEAN NOT NULL DEFAULT FALSE,
    status VARCHAR(30) NOT NULL DEFAULT 'PLANNED',
    current_finishing_unit_id BIGINT REFERENCES finishing_units(id),
    current_customer_id BIGINT REFERENCES customers(id),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 11. Inventory Transactions (Audit Log for Inward / Outward)
CREATE TABLE inventory_transactions (
    id BIGSERIAL PRIMARY KEY,
    transaction_type VARCHAR(20) NOT NULL,
    barcode_type VARCHAR(20) NOT NULL,
    barcode_value VARCHAR(100) NOT NULL,
    pieces_count INT NOT NULL DEFAULT 1,
    cutting_lot_id BIGINT REFERENCES cutting_lots(id),
    style_code VARCHAR(50),
    color_name VARCHAR(50),
    size_name VARCHAR(50),
    party_name VARCHAR(100),
    reference_no VARCHAR(100),
    remarks TEXT,
    performed_by VARCHAR(50),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 12. Label Settings & Presets
CREATE TABLE label_settings (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES app_users(id),
    preset_name VARCHAR(50) NOT NULL,
    width_mm DOUBLE PRECISION NOT NULL,
    height_mm DOUBLE PRECISION NOT NULL,
    is_default BOOLEAN NOT NULL DEFAULT FALSE,
    tspl_density INT NOT NULL DEFAULT 8,
    tspl_direction INT NOT NULL DEFAULT 1
);

-- Database Index Optimizations
CREATE INDEX idx_lot_number ON cutting_lots(lot_number);
CREATE INDEX idx_set_barcode ON set_bundles(barcode);
CREATE INDEX idx_piece_barcode ON single_pieces(barcode);
CREATE INDEX idx_piece_parent_set ON single_pieces(parent_set_id);
CREATE INDEX idx_piece_status ON single_pieces(status);
CREATE INDEX idx_set_status ON set_bundles(status);
CREATE INDEX idx_piece_lot_color ON single_pieces(cutting_lot_id, color_name);
CREATE INDEX idx_trans_barcode ON inventory_transactions(barcode_value);
CREATE INDEX idx_trans_created ON inventory_transactions(created_at DESC);
