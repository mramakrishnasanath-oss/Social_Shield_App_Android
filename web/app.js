/* ══════════════════════════════════
   SocialShield Web App — JavaScript
   Backend: https://socialsheild.onrender.com
   ══════════════════════════════════ */

const API_BASE = "https://socialshield-backend.onrender.com";
// Dev mode: backend accepts any token when Firebase is not initialized
const DEV_TOKEN = "dev_user_web_token_12345678901234567890";

// ─── DOM Refs ────────────────────────────────────────────────
const navbar       = document.getElementById("navbar");
const dropZone     = document.getElementById("dropZone");
const fileInput    = document.getElementById("fileInput");
const browseBtn    = document.getElementById("browseBtn");
const imagePreview = document.getElementById("imagePreview");
const previewImg   = document.getElementById("previewImg");
const previewInfo  = document.getElementById("previewInfo");
const removeBtn    = document.getElementById("removeBtn");
const scanBtn      = document.getElementById("scanBtn");
const errorBox     = document.getElementById("errorBox");
const errorText    = document.getElementById("errorText");
const uploadPanel  = document.getElementById("uploadPanel");
const resultPanel  = document.getElementById("resultPanel");
const scanAgainBtn = document.getElementById("scanAgainBtn");

let selectedFile = null;

// ─── Navbar Scroll ───────────────────────────────────────────
window.addEventListener("scroll", () => {
  navbar.classList.toggle("scrolled", window.scrollY > 40);
});

// ─── Hamburger Menu ──────────────────────────────────────────
document.getElementById("hamburger").addEventListener("click", () => {
  // Simple mobile scroll to scanner
  document.getElementById("scanner").scrollIntoView({ behavior: "smooth" });
});

// ─── Particles ───────────────────────────────────────────────
(function spawnParticles() {
  const container = document.getElementById("particles");
  for (let i = 0; i < 30; i++) {
    const p = document.createElement("div");
    p.style.cssText = `
      position:absolute;
      width:${Math.random() * 3 + 1}px;
      height:${Math.random() * 3 + 1}px;
      background:rgba(0,212,255,${Math.random() * 0.4 + 0.1});
      border-radius:50%;
      left:${Math.random() * 100}%;
      top:${Math.random() * 100}%;
      animation: particleFloat ${Math.random() * 8 + 6}s linear infinite;
      animation-delay:${Math.random() * 8}s;
    `;
    container.appendChild(p);
  }
  const style = document.createElement("style");
  style.textContent = `
    @keyframes particleFloat {
      0% { transform: translateY(0) translateX(0); opacity: 0; }
      10% { opacity: 1; }
      90% { opacity: 1; }
      100% { transform: translateY(-100vh) translateX(${Math.random() > 0.5 ? "" : "-"}${Math.random() * 50}px); opacity: 0; }
    }
  `;
  document.head.appendChild(style);
})();

// ─── Feature Card Animation (Intersection Observer) ──────────
const featureCards = document.querySelectorAll(".feature-card");
const observer = new IntersectionObserver((entries) => {
  entries.forEach(entry => {
    if (entry.isIntersecting) {
      const delay = entry.target.dataset.delay || 0;
      entry.target.style.animationDelay = `${delay}ms`;
      entry.target.style.animationPlayState = "running";
    }
  });
}, { threshold: 0.1 });
featureCards.forEach(card => {
  card.style.animationPlayState = "paused";
  observer.observe(card);
});

// ─── File Handling ───────────────────────────────────────────
browseBtn.addEventListener("click", () => fileInput.click());
dropZone.addEventListener("click", (e) => {
  if (e.target !== browseBtn) fileInput.click();
});

fileInput.addEventListener("change", () => {
  if (fileInput.files.length > 0) handleFile(fileInput.files[0]);
});

dropZone.addEventListener("dragover", (e) => {
  e.preventDefault();
  dropZone.classList.add("dragover");
});
dropZone.addEventListener("dragleave", () => dropZone.classList.remove("dragover"));
dropZone.addEventListener("drop", (e) => {
  e.preventDefault();
  dropZone.classList.remove("dragover");
  const file = e.dataTransfer.files[0];
  if (file) handleFile(file);
});

function handleFile(file) {
  if (!file.type.startsWith("image/")) {
    showError("Please select an image file (JPG, PNG, WEBP, etc.)");
    return;
  }
  if (file.size > 20 * 1024 * 1024) {
    showError("Image is too large. Maximum size is 20MB.");
    return;
  }

  selectedFile = file;
  hideError();

  const reader = new FileReader();
  reader.onload = (e) => {
    previewImg.src = e.target.result;
    const sizeMB = (file.size / 1024 / 1024).toFixed(2);
    previewInfo.textContent = `${file.name} • ${sizeMB} MB • ${file.type}`;
    dropZone.style.display = "none";
    imagePreview.style.display = "block";
    scanBtn.disabled = false;
    hideError();
  };
  reader.readAsDataURL(file);
}

removeBtn.addEventListener("click", resetUpload);

function resetUpload() {
  selectedFile = null;
  fileInput.value = "";
  previewImg.src = "";
  imagePreview.style.display = "none";
  dropZone.style.display = "block";
  scanBtn.disabled = true;
  hideError();
}

// ─── Scan ────────────────────────────────────────────────────
scanBtn.addEventListener("click", startScan);

async function startScan() {
  if (!selectedFile) return;

  // Show loading state
  scanBtn.querySelector(".scan-btn-text").style.display = "none";
  scanBtn.querySelector(".scan-btn-loading").style.display = "flex";
  scanBtn.disabled = true;
  hideError();

  try {
    const formData = new FormData();
    formData.append("file", selectedFile);

    const response = await fetch(`${API_BASE}/api/v1/scan/image`, {
      method: "POST",
      headers: {
        "Authorization": `Bearer ${DEV_TOKEN}`
      },
      body: formData
    });

    if (!response.ok) {
      const errData = await response.json().catch(() => ({}));
      throw new Error(errData.detail || `Server error: ${response.status}`);
    }

    const data = await response.json();
    showResult(data);

  } catch (err) {
    console.error("Scan failed:", err);
    showError(err.message || "Failed to connect to the server. Please try again.");
  } finally {
    // Reset loading state
    scanBtn.querySelector(".scan-btn-text").style.display = "flex";
    scanBtn.querySelector(".scan-btn-loading").style.display = "none";
    scanBtn.disabled = false;
  }
}

// ─── Show Result ─────────────────────────────────────────────
function showResult(data) {
  const verdict = (data.verdict || "REAL").toUpperCase();
  const confidence = parseFloat(data.confidence || 50);
  const fakePct = parseFloat(data.fake_probability || 50);
  const realPct = parseFloat(data.real_probability || 50);
  const risk = (data.risk_level || "LOW").toUpperCase();
  const explanations = data.explanations || [];

  // Hide upload, show result
  uploadPanel.style.display = "none";
  resultPanel.style.display = "block";
  resultPanel.scrollIntoView({ behavior: "smooth", block: "nearest" });

  // Verdict icon & badge
  const verdictIcon = document.getElementById("verdictIcon");
  const verdictBadge = document.getElementById("verdictBadge");
  const verdictTitle = document.getElementById("verdictTitle");
  const verdictSub = document.getElementById("verdictSub");
  const verdictHeader = document.getElementById("verdictHeader");

  const verdictClass = verdict === "FAKE" ? "fake" : verdict === "SUSPICIOUS" ? "suspicious" : "real";
  const verdictEmoji = verdict === "FAKE" ? "🚨" : verdict === "SUSPICIOUS" ? "⚠️" : "✅";
  const verdictLabel = verdict === "FAKE" ? "● FAKE" : verdict === "SUSPICIOUS" ? "◆ SUSPICIOUS" : "✓ REAL";
  const verdictMsg = verdict === "FAKE"
    ? "This image shows strong signs of AI generation or manipulation"
    : verdict === "SUSPICIOUS"
    ? "This image has some anomalous patterns. Manual review recommended"
    : "This image appears to be authentic with no significant manipulation detected";

  verdictIcon.textContent = verdictEmoji;
  verdictIcon.className = `verdict-icon ${verdictClass}`;
  verdictBadge.textContent = verdictLabel;
  verdictBadge.className = `verdict-badge ${verdictClass}`;
  verdictTitle.textContent = `Image is ${verdict === "REAL" ? "Authentic" : verdict}`;
  verdictSub.textContent = verdictMsg;

  // Confidence Arc Color
  const arcFill = document.getElementById("arcFill");
  const arcColor = verdict === "FAKE" ? "#FF3B3B" : verdict === "SUSPICIOUS" ? "#FFB800" : "#06FFA5";
  arcFill.style.stroke = arcColor;

  // Arc Animation — total path length ≈ 283px
  const arcPct = document.getElementById("arcPct");
  arcPct.style.color = arcColor;
  setTimeout(() => {
    const offset = 283 - (283 * confidence / 100);
    arcFill.style.strokeDashoffset = offset;
    animateCount(arcPct, 0, Math.round(confidence), 1200, v => `${v}%`);
  }, 200);

  // Probability Bars
  const fakeFill = document.getElementById("fakeFill");
  const realFill = document.getElementById("realFill");
  const fakePctEl = document.getElementById("fakePct");
  const realPctEl = document.getElementById("realPct");
  setTimeout(() => {
    fakeFill.style.width = `${fakePct}%`;
    realFill.style.width = `${realPct}%`;
    animateCount(fakePctEl, 0, Math.round(fakePct), 1000, v => `${v}%`);
    animateCount(realPctEl, 0, Math.round(realPct), 1000, v => `${v}%`);
  }, 400);

  // Risk Level Bars
  const riskBars = document.querySelectorAll(".risk-bar");
  const riskText = document.getElementById("riskText");
  const riskColors = { HIGH: "#FF3B3B", MEDIUM: "#FFB800", LOW: "#06FFA5" };
  const riskFills = { HIGH: 3, MEDIUM: 2, LOW: 1 };
  const riskColor = riskColors[risk] || "#06FFA5";
  const riskFilled = riskFills[risk] || 1;
  riskBars.forEach((bar, i) => {
    bar.style.background = i < riskFilled ? riskColor : "rgba(255,255,255,0.08)";
  });
  riskText.textContent = `${risk} RISK`;
  riskText.style.color = riskColor;

  // Explanations
  const explainList = document.getElementById("explainList");
  explainList.innerHTML = "";
  if (explanations.length === 0) {
    explanations.push("Analysis complete");
  }
  explanations.forEach(exp => {
    const li = document.createElement("li");
    li.textContent = exp;
    explainList.appendChild(li);
  });
}

// ─── Scan Again ──────────────────────────────────────────────
scanAgainBtn.addEventListener("click", () => {
  resultPanel.style.display = "none";
  uploadPanel.style.display = "block";
  resetUpload();
  document.getElementById("scanner").scrollIntoView({ behavior: "smooth" });
});

// ─── Helpers ─────────────────────────────────────────────────
function showError(msg) {
  errorText.textContent = msg;
  errorBox.style.display = "flex";
}
function hideError() {
  errorBox.style.display = "none";
}

function animateCount(el, from, to, duration, formatter) {
  const start = performance.now();
  function step(now) {
    const progress = Math.min((now - start) / duration, 1);
    const eased = 1 - Math.pow(1 - progress, 3);
    el.textContent = formatter(Math.round(from + (to - from) * eased));
    if (progress < 1) requestAnimationFrame(step);
  }
  requestAnimationFrame(step);
}
