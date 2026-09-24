# Enterprise Garment Manufacturing Warehouse & Inventory Management System (WMS)

A production-ready Garment Manufacturing Warehouse & Inventory Management System built with **Java 21**, **Spring Boot 3.x**, **PostgreSQL**, **Flyway**, and a mobile-optimized frontend using **Thymeleaf**, **TailwindCSS**, and **Html5-QRCode**.

Designed specifically for apparel factories, finishing mills, and logistics hubs with support for **Ratio Size Sets**, **Parent-Child Bundle Barcoding**, **Native TSC Thermal Printing (TSPL)**, and **Dynamic BarTender Integration**.

---

## 🌟 Key Architecture & Capabilities

### 1. Domain & Business Model (Ratio Sets & Bundles)
- **Style / Design Master**: Manages style codes, silhouettes, product categories, and fabric compositions.
- **Ratio Size Set Engine**: Computes exact piece allocations per bundle (e.g., *Ratio 12 Set*: Size 30: 1 pc, 32: 3 pcs, 33: 3 pcs, 34: 3 pcs, 36: 2 pcs &rarr; Total 12 pcs per set).
- **Cutting Lot Generation**: Multi-color lot breakdown with automated calculation of:
  - **Complete Ratio Bundles**: Fully packed sets with parent barcodes (`SET-{LOT}-{COLOR}-{INDEX}`).
  - **Loose Pieces**: Remainder garments marked as loose pieces (`PC-{LOT}-{COLOR}-{SIZE}-LOOSE-{INDEX}`).
  - **Relational Hierarchy**: Every single garment piece is attached to its parent Set Barcode, enabling one-scan whole bundle operations or single-piece tracking.

### 2. Multi-Mode Label Printing & Thermal Integration
- **Option A — Dynamic BarTender Data Source Export**:
  - One-click CSV download formatted specifically for Seagull Scientific BarTender Commander / Database Connection.
  - Standard Headers: `Barcode`, `LotNo`, `StyleCode`, `DesignName`, `Color`, `Size`, `ItemType`, `SetCode`, `SetRatio`, `FinishingUnit`.
- **Option B — Exact Millimeter Browser Print Preview**:
  - Pixel-perfect CSS `@media print` rules with zero page margins, exact millimeter dimensions (`50mm x 25mm`, `50mm x 38mm`, `100mm x 50mm`, or custom), and page-break rules.
  - In-app rendering with **ZXing** generating crisp Code 128 barcodes and 2D QR codes embedded as Base64 images.
- **Option C — Native TSPL (TSC Printer Language) Generator**:
  - Direct `.prn` / `.txt` file export containing native commands for TSC thermal printers (TTP-244 Pro, TE200, TX200, MB240).
  - Built with custom utility `TsplCommandBuilder.java`.

### 3. Mobile Warehouse Floor Scanner
- **Camera Scanning**: Uses `html5-qrcode` to leverage any smartphone camera directly from the web browser.
- **Continuous Scan Mode**: Fast scanning with duplicate throttle protection.
- **Acoustic & Haptic Feedback**:
  - Success: High-pitched double-chirp chime + phone vibration.
  - Error/Rejection: Low-frequency warning buzz + phone vibration pattern.
  - Generated via **Web Audio API** (`SoundFX` in `sound-fx.js`), functioning without external audio files.
- **Hardware Laser / Bluetooth Scanner Gun Fallback**: Input box listening for `Enter` key events.

### 4. Role-Based Access Control (RBAC) & Security
- **`ROLE_MASTER`**: Full system administration, user provisioning, database status.
- **`ROLE_ADMIN`**: Manage styles, lots, size sets, parties, print labels.
- **`ROLE_WAREHOUSE_USER`**: Access mobile camera scanner for Inward and Outward, view live stock.
- BCrypt hashed passwords and auto-seeded defaults on fresh startup.

---

## 🔑 Default Credentials

When initialized on a fresh database, the following accounts are automatically seeded:

| Role | Username | Password | Purpose |
| :--- | :--- | :--- | :--- |
| **MASTER** | `master` | `master123` | Global admin & user provisioning |
| **ADMIN** | `admin` | `admin123` | Operations, cutting lots, styles, labels |
| **OPERATOR** | `operator` | `operator123` | Warehouse floor camera scanner |

---

## 📐 Selectable Label Sizes

| Preset | Dimensions | Target Use Case | Output Formats |
| :--- | :--- | :--- | :--- |
| **50mm x 25mm** | Compact | Single piece tag / wash care size | BarTender CSV, Browser Print, TSPL |
| **50mm x 38mm** | Standard | Garment price/barcode hang-tag | BarTender CSV, Browser Print, TSPL |
| **100mm x 50mm** | Master | Set bundle / master carton label | BarTender CSV, Browser Print, TSPL |
| **Custom Size** | W mm x H mm | User-defined label rolls | BarTender CSV, Browser Print, TSPL |

---

## 🖨️ Thermal Printer & BarTender Integration Guide

### Linking Exported CSV to BarTender
1. Open your BarTender template (`.btw`).
2. Go to **File &rarr; Database Connection Setup**.
3. Choose **Text File** and browse to the exported CSV (`BarTender_Lot_..._all.csv`).
4. Set separator to **Comma** and select **First row contains field names**.
5. Map your Barcode object to field `Barcode`, and human-readable text objects to `StyleCode`, `Color`, `Size`, `SetRatio`, etc.
6. Trigger batch printing directly.

### Sending TSPL Commands Directly to TSC Printers
The generated `.prn` files contain raw TSPL commands:
```tspl
SIZE 50.0 mm, 38.0 mm
GAP 2 mm, 0 mm
DIRECTION 1
DENSITY 8
CLS
TEXT 20, 20, "3", 0, 1, 1, "STY-101"
BARCODE 20, 110, "128", 55, 1, 0, 2, 2, "SET-LOT2026001-NAVY-001"
PRINT 1, 1
```
- **Network / LAN Printer**:
  ```bash
  nc -w 3 192.168.1.150 9100 < TSC_LOT-2026-001_all_50x38mm.prn
  ```
- **USB / Raw Spooler (Windows / Mac)**:
  ```bash
  lp -d TSC_TTP-244_Pro -o raw TSC_LOT-2026-001_all_50x38mm.prn
  ```

---

## 🚀 Local Development & Build

### Prerequisites
- Java 21 (Eclipse Temurin or OpenJDK 21)
- Apache Maven 3.9+
- PostgreSQL (optional, embedded H2 in PostgreSQL mode runs automatically if `DATABASE_URL` is omitted)

### Building the Project
```bash
mvn clean package
```

### Running the Application Locally
```bash
mvn spring-boot:run
```
The application will start at: `http://localhost:8080`

---

## 🐳 Docker Deployment

A multi-stage `Dockerfile` is provided using `maven:3.9.6-eclipse-temurin-21-alpine` to compile and `eclipse-temurin:21-jre-alpine` for the runtime container with an unprivileged `wmsuser`.

```bash
# Build Docker image
docker build -t garment-wms:1.0.0 .

# Run Docker container
docker run -p 8080:8080 \
  -e DATABASE_URL="postgresql://user:password@host:5432/garment_wms" \
  garment-wms:1.0.0
```

---

## ☁️ Deploy to Render (Blueprint Setup)

This repository includes a native `render.yaml` blueprint that deploys:
1. **Managed PostgreSQL Database** (`garment-wms-db`).
2. **Docker Web Service** (`garment-wms-web`) with healthcheck at `/actuator/health` and automatic `DATABASE_URL` binding.

### Deployment Steps:
1. Push your repository to GitHub.
2. In the Render Dashboard, click **New + &rarr; Blueprint**.
3. Select your repository.
4. Render will read `render.yaml`, provision the PostgreSQL database, build the multi-stage Docker container, execute Flyway migrations, and launch your WMS.
