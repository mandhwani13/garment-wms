/**
 * Warehouse Mobile Barcode & QR Scanner Application
 * Supports rear camera continuous scanning and external laser/Bluetooth barcode guns.
 */
document.addEventListener("DOMContentLoaded", () => {
    let html5QrCode = null;
    let isScanning = false;
    let lastScannedCode = null;
    let lastScannedTime = 0;
    const SCAN_COOLDOWN_MS = 1500;

    let scanCount = 0;
    let piecesCount = 0;

    const btnStartCamera = document.getElementById("btn-start-camera");
    const btnStopCamera = document.getElementById("btn-stop-camera");
    const cameraContainer = document.getElementById("reader");
    const manualBarcodeInput = document.getElementById("manual-barcode-input");
    const manualSubmitBtn = document.getElementById("manual-submit-btn");
    const scanLogBody = document.getElementById("scan-log-body");
    const scanCounterEl = document.getElementById("scan-counter");
    const piecesCounterEl = document.getElementById("pieces-counter");
    const alertBanner = document.getElementById("alert-banner");
    const laserBeam = document.getElementById("scanner-laser");

    // Mode & Form Elements
    const modeSelect = document.getElementById("mode-select");
    const finishingUnitSelect = document.getElementById("finishing-unit-select");
    const customerSelect = document.getElementById("customer-select");
    const finishingUnitContainer = document.getElementById("finishing-unit-container");
    const customerContainer = document.getElementById("customer-container");
    const referenceNoInput = document.getElementById("reference-no");
    const remarksInput = document.getElementById("remarks-input");

    function updateModeUI() {
        const mode = modeSelect.value;
        if (mode === "INWARD") {
            if (finishingUnitContainer) finishingUnitContainer.classList.remove("hidden");
            if (customerContainer) customerContainer.classList.add("hidden");
        } else {
            if (finishingUnitContainer) finishingUnitContainer.classList.add("hidden");
            if (customerContainer) customerContainer.classList.remove("hidden");
        }
    }

    if (modeSelect) {
        modeSelect.addEventListener("change", updateModeUI);
        updateModeUI();
    }

    // Camera Start
    if (btnStartCamera) {
        btnStartCamera.addEventListener("click", () => {
            if (isScanning) return;

            html5QrCode = new Html5Qrcode("reader");
            const config = {
                fps: 15,
                qrbox: { width: 280, height: 200 },
                aspectRatio: 1.333334
            };

            html5QrCode.start(
                { facingMode: "environment" },
                config,
                onScanSuccess,
                onScanFailure
            ).then(() => {
                isScanning = true;
                btnStartCamera.classList.add("hidden");
                btnStopCamera.classList.remove("hidden");
                if (laserBeam) laserBeam.classList.remove("hidden");
                showAlert("Camera active. Align barcode inside frame.", "info");
            }).catch(err => {
                console.error("Camera start error:", err);
                showAlert("Could not access camera: " + err, "error");
            });
        });
    }

    // Camera Stop
    if (btnStopCamera) {
        btnStopCamera.addEventListener("click", () => {
            if (!isScanning || !html5QrCode) return;
            html5QrCode.stop().then(() => {
                isScanning = false;
                btnStartCamera.classList.remove("hidden");
                btnStopCamera.classList.add("hidden");
                if (laserBeam) laserBeam.classList.add("hidden");
                showAlert("Camera stopped.", "info");
            }).catch(err => console.error("Camera stop error:", err));
        });
    }

    function onScanSuccess(decodedText, decodedResult) {
        const now = Date.now();
        if (decodedText === lastScannedCode && (now - lastScannedTime) < SCAN_COOLDOWN_MS) {
            return; // Cooldown throttle
        }

        lastScannedCode = decodedText;
        lastScannedTime = now;
        processBarcode(decodedText);
    }

    function onScanFailure(error) {
        // Continuous scanning frame misses are normal, ignore
    }

    // Manual / Barcode Gun Enter
    if (manualBarcodeInput) {
        manualBarcodeInput.addEventListener("keypress", (e) => {
            if (e.key === "Enter") {
                e.preventDefault();
                submitManualBarcode();
            }
        });
    }

    if (manualSubmitBtn) {
        manualSubmitBtn.addEventListener("click", (e) => {
            e.preventDefault();
            submitManualBarcode();
        });
    }

    function submitManualBarcode() {
        const code = manualBarcodeInput.value.trim();
        if (!code) return;
        manualBarcodeInput.value = "";
        processBarcode(code);
        manualBarcodeInput.focus();
    }

    function processBarcode(barcode) {
        const mode = modeSelect.value;
        const finishingUnitId = finishingUnitSelect ? finishingUnitSelect.value : null;
        const customerId = customerSelect ? customerSelect.value : null;
        const referenceNo = referenceNoInput ? referenceNoInput.value.trim() : "";
        const remarks = remarksInput ? remarksInput.value.trim() : "";

        if (mode === "OUTWARD" && (!customerId || customerId === "")) {
            SoundFX.playError();
            showAlert("Please select a Customer destination for Outward dispatch!", "error");
            return;
        }

        const payload = {
            barcode: barcode,
            transactionType: mode,
            finishingUnitId: finishingUnitId ? parseInt(finishingUnitId) : null,
            customerId: customerId ? parseInt(customerId) : null,
            referenceNo: referenceNo,
            remarks: remarks
        };

        fetch("/api/inventory/scan", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(payload)
        })
        .then(res => res.json())
        .then(data => {
            if (data.success) {
                SoundFX.playSuccess();
                scanCount++;
                piecesCount += (data.piecesUpdated || 1);
                if (scanCounterEl) scanCounterEl.textContent = scanCount;
                if (piecesCounterEl) piecesCounterEl.textContent = piecesCount;

                showAlert(data.message, "success");
                addLogRow(data, true);
            } else {
                SoundFX.playError();
                showAlert(data.message, "error");
                addLogRow({
                    barcode: barcode,
                    message: data.message,
                    barcodeType: "UNKNOWN",
                    piecesUpdated: 0,
                    styleCode: "-",
                    color: "-",
                    size: "-"
                }, false);
            }
        })
        .catch(err => {
            SoundFX.playError();
            console.error("Scan API Error:", err);
            showAlert("Network / Server error processing scan: " + err, "error");
        });
    }

    function showAlert(msg, type) {
        if (!alertBanner) return;
        alertBanner.classList.remove("hidden", "bg-emerald-500", "bg-red-500", "bg-blue-600");
        if (type === "success") {
            alertBanner.classList.add("bg-emerald-500");
        } else if (type === "error") {
            alertBanner.classList.add("bg-red-500");
        } else {
            alertBanner.classList.add("bg-blue-600");
        }
        alertBanner.textContent = msg;
    }

    function addLogRow(data, isSuccess) {
        if (!scanLogBody) return;
        const tr = document.createElement("tr");
        tr.className = isSuccess ? "bg-white hover:bg-slate-50 transition border-b" : "bg-red-50 hover:bg-red-100 transition border-b";

        const timeStr = new Date().toLocaleTimeString();
        tr.innerHTML = `
            <td class="px-3 py-2 text-xs font-mono font-bold text-slate-800">${escapeHtml(data.barcode || "")}</td>
            <td class="px-3 py-2 text-xs">
                <span class="inline-flex px-2 py-0.5 rounded text-xs font-medium ${isSuccess ? 'bg-emerald-100 text-emerald-800' : 'bg-red-100 text-red-800'}">
                    ${isSuccess ? 'SUCCESS' : 'FAILED'}
                </span>
            </td>
            <td class="px-3 py-2 text-xs font-semibold text-slate-700">${escapeHtml(data.styleCode || "-")}</td>
            <td class="px-3 py-2 text-xs text-slate-600">${escapeHtml(data.color || "-")}</td>
            <td class="px-3 py-2 text-xs font-medium text-slate-800">${escapeHtml(data.size || "-")}</td>
            <td class="px-3 py-2 text-xs text-right font-bold text-slate-900">${data.piecesUpdated || 0}</td>
            <td class="px-3 py-2 text-xs text-slate-400 text-right">${timeStr}</td>
        `;

        if (scanLogBody.firstChild) {
            scanLogBody.insertBefore(tr, scanLogBody.firstChild);
        } else {
            scanLogBody.appendChild(tr);
        }
    }

    function escapeHtml(string) {
        return String(string).replace(/[&<>"'`=\/]/g, function (s) {
            return {
                '&': '&amp;',
                '<': '&lt;',
                '>': '&gt;',
                '"': '&quot;',
                "'": '&#39;',
                '/': '&#x2F;',
                '`': '&#x60;',
                '=': '&#x3D;'
            }[s];
        });
    }
});
