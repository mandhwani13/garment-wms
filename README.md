# Enterprise Garment Manufacturing ERP & Warehouse Management System (WMS)

A comprehensive, production-grade Garment Manufacturing ERP & Warehouse Management System built with **Java 21**, **Spring Boot 3.3.4**, **PostgreSQL**, **Flyway**, and a responsive mobile web interface using **Thymeleaf**, **TailwindCSS**, **Alpine.js**, and **Html5-QRCode**.

Engineered with architectural paradigms inspired by **ERPNext** (Subcontracting Work Orders, Multi-Location Virtual WIP Warehouses, Bill of Materials with Scrappage) and **Odoo** (Product Attribute Matrix, Parent/Child Package Barcodes, and Automated Stock Picking).

---

## 🌟 Complete 8-Phase Garment Supply Chain Lifecycle

```
[Phase 1: Fabric PO & Drop-Shipment]
       │ (Consignment & Transit Tracking)
       ▼
[Phase 2: Stitching Unit Inward & Roll Inspection]
       │ (Roll-wise meters, weight, defect gate, digital GRN)
       ▼
[Phase 3: Marker Drawing & Average Yield Approval Gate]
       │ (Meters/piece approval before lot creation)
       ▼
[Phase 4: Subcontracting Job Work Routing]
       │ (Embroidery / Embossing / Printing challans with return & damage reconciliation)
       ▼
[Phase 5: Washing Program & Child Color Lot Splitting]
       │ (Wash recipe batches, color separation, shrinkage % audit)
       ▼
[Phase 6: Brand Division & Central Trims Store BOM Issuance]
       │ (Automated labels, rivets, buttons, and polybag deductions)
       ▼
[Phase 7: Barcode / QR Label Engine]
       │ (Single-Piece `KP-800-...`, Set-Bundle `SET-...`, BarTender CSV, TSPL generator)
       ▼
[Phase 8: Mobile Floor Scanning]
       │ (Inward & Outward Dispatch with Web Audio & Haptic vibration feedback)
       ▼
[Finished Goods Inventory & Multi-Location Stock Ledger]
```

---

## 🏛️ Architectural Patterns Adopted from ERPNext & Odoo

### 1. Multi-Location Virtual Warehouse Model (ERPNext-Style)
- **`INTERNAL_WAREHOUSE`**: Central Fabric Store, Trims & Accessories Store, Finished Goods Store.
- **`SUBCONTRACTOR_WIP`**: External Stitching Units, Embroidery Units, Printing Units, Washing Plants, and Finishing Units are modeled as trackable inventory locations.
- **Stock Transfer Ledger**: All movements generate two-step Stock Transfer Entries with transit notes and gate-pass generation.

### 2. Variant Attribute Matrix & Multi-Level BOM (Odoo-Style)
- Dynamic Brand Masters, Fabric Masters, Trim Accessories, and Ratio Size Sets.
- **Finishing BOM**: Auto-calculates trim and accessory consumption per allocated garment piece (e.g. main brand label, care label, brass buttons, polybag).

### 3. Parent-Child Package/Bundle Barcode Hierarchy
- **Parent Set Bundle (`SET-...`)**: Master pack barcode containing proportional ratio pieces (e.g., Ratio 12 Set).
- **Single Child Pieces (`KP-800-...` / `PC-...`)**: Unit-level hang-tags and wash-care labels.
- Scanning a parent bundle automatically updates all contained child pieces in one action.

---

## 🔑 Default Roles & Seeded Accounts

The application automatically provisions dedicated role accounts on startup:

| Role | Username | Password | Workflow Responsibilities |
| :--- | :--- | :--- | :--- |
| **MASTER** | `master` | `master123` | Global Administration, User Provisioning, System Audits |
| **ADMIN** | `admin` | `admin123` | Master Data, Marker Approvals, Cutting Lots, Discrepancy Clearance |
| **PRODUCTION_MANAGER** | `production` | `production123` | Fabric POs, Job Work Challans, Washing Programs, Finishing BOM |
| **STITCHING_UNIT** | `stitching` | `stitching123` | Fabric Roll Physical Inward, Roll Defect Flagging, GRN Generation |
| **WASHING_UNIT** | `washing` | `washing123` | Wash Batch Processing, Color Splitting, Shrinkage Tracking |
| **FINISHING_UNIT** | `finishing` | `finishing123` | Brand Trim Issuance Receipt, Ironing, Final Packing |
| **WAREHOUSE_USER** | `operator` | `operator123` | Mobile Floor Camera Scanner (Inward / Outward Dispatch) |

---

## 🖨️ Multi-Mode Label Printing & Thermal Integration

### Selectable Label Sizes
| Preset | Dimensions | Target Use Case | Supported Formats |
| :--- | :--- | :--- | :--- |
| **50mm x 25mm** | Compact | Single piece tag / wash care size | BarTender CSV, Browser Print, TSPL |
| **50mm x 38mm** | Standard | Garment price/barcode hang-tag | BarTender CSV, Browser Print, TSPL |
| **100mm x 50mm** | Master | Set bundle / master carton label | BarTender CSV, Browser Print, TSPL |
| **Custom Size** | W mm x H mm | User-defined label rolls | BarTender CSV, Browser Print, TSPL |

### 1. Dynamic BarTender Data Source Export
- Download CSV mapped for Seagull Scientific BarTender Commander / Database Connection.
- URL: `/labels/export/bartender/{lotId}?type=ALL`
- Fields: `Barcode`, `LotNo`, `StyleCode`, `DesignName`, `Color`, `Size`, `ItemType`, `SetCode`, `SetRatio`, `FinishingUnit`.

### 2. Browser Print Preview with Exact Millimeters
- URL: `/labels/preview/{lotId}?sizePreset=50X38&barcodeFormat=CODE128`
- CSS `@media print` zero-margin layout with embedded **ZXing** high-resolution Base64 Code 128 and 2D QR codes.

### 3. Native TSPL (TSC Printer Language) Generator
- URL: `/labels/export/tspl/{lotId}?sizePreset=50X38`
- Emits raw printer commands (`SIZE`, `GAP`, `DIRECTION`, `CLS`, `BARCODE 128`, `PRINT`) compatible with TSC TTP-244 Pro, TE200, TX200, MB240 printers.
- Direct network spooling: `nc -w 3 192.168.1.150 9100 < TSC_LOT-2026-001_all_50x38mm.prn`.

---

## 📱 Mobile Warehouse Floor Scanner

- **URL**: `/scanner`
- **Engine**: `html5-qrcode` accessing any device camera with continuous scanning.
- **Audio & Haptic Feedback**: Native Web Audio API synth chimes (`sound-fx.js`) and navigator vibration without external MP3 assets.
- **Barcode Support**: Resolves parent bundles (`SET-...`), loose pieces (`PC-...`), and cutting lots.

---

## 🚀 Local Development & Execution

```bash
# Build with Java 21 & Maven
export JAVA_HOME="/opt/homebrew/opt/openjdk@21"
mvn clean package -DskipTests

# Run with local PostgreSQL
DATABASE_URL="postgresql://localhost:5432/garment_wms" java -jar target/garment-wms-1.0.0.jar --server.port=8080
```
Access in browser: **`http://localhost:8080`**

---

## 🐳 Docker & ☁️ Render Blueprint Deployment

### Multi-Stage Dockerfile
- Build stage: `maven:3.9.6-eclipse-temurin-21-alpine`
- Runtime stage: `eclipse-temurin:21-jre-alpine` running as unprivileged `wmsuser` with G1GC optimization.

### Automated Render Deployment (`render.yaml`)
1. Push to GitHub repository.
2. In [Render Dashboard](https://dashboard.render.com), select **New + &rarr; Blueprint**.
3. Point to your repository. Render automatically provisions:
   - **`garment-wms-db`**: PostgreSQL Managed Instance.
   - **`garment-wms-web`**: Docker Web Service running Spring Boot with health checks at `/actuator/health`.
