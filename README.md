# 🛡️ SocialShield — Full-Stack AI Fraud & Deepfake Detection

A production-grade web application with a **React frontend** and **FastAPI backend** for detecting deepfakes, voice clones, phishing URLs, scam text, and fake social profiles.

---

## 📁 Project Structure

```
SocialShield/
├── frontend/          ← React Web App (Vite)
│   ├── src/
│   │   ├── pages/     ← All 8 pages (Splash, Onboarding, Auth, Home, Scan, Result, History, Map, Settings)
│   │   ├── components/← Layout (sidebar + mobile nav)
│   │   ├── api.js     ← Axios client → connects to backend
│   │   └── AuthContext.jsx ← Firebase auth + dev-mode fallback
│   └── .env           ← VITE_API_URL + Firebase config
│
├── backend/           ← Python FastAPI Backend
│   ├── main.py        ← FastAPI app + CORS
│   ├── routers/       ← 7 routers (image, video, audio, text, url, profile, history)
│   ├── models/        ← Pydantic ScanResult model
│   ├── utils/         ← auth, database, huggingface, gradcam
│   ├── ml/            ← ML model registry
│   ├── .env           ← API keys
│   ├── start.bat      ← Windows startup script
│   └── start.sh       ← Linux/macOS startup script
│
├── android/           ← Original Android/Kotlin app
├── start-all.bat      ← Launch BOTH servers at once (Windows)
└── README.md
```

---

## 🚀 Quick Start

### Option A — One Click (Windows)
```
Double-click: start-all.bat
```
This launches both backend (port 8000) and frontend (port 5173) in separate windows.

---

### Option B — Manual

#### 1. Start Backend
```bash
cd backend
start.bat          # Windows
# or
./start.sh         # Linux / macOS
```
Backend runs at → **http://localhost:8000**
API Docs at → **http://localhost:8000/docs**

#### 2. Start Frontend
```bash
cd frontend
npm install        # first time only
npm run dev
```
Frontend runs at → **http://localhost:5173**

---

## ⚙️ Configuration

### Frontend (`frontend/.env`)
```env
VITE_API_URL=http://localhost:8000

# Optional Firebase (app works in demo mode without these)
VITE_FIREBASE_API_KEY=...
VITE_FIREBASE_AUTH_DOMAIN=...
VITE_FIREBASE_PROJECT_ID=...
VITE_FIREBASE_STORAGE_BUCKET=...
VITE_FIREBASE_MESSAGING_SENDER_ID=...
VITE_FIREBASE_APP_ID=...
```

### Backend (`backend/.env`)
```env
FIREBASE_CREDENTIALS_PATH=./firebase-credentials.json
GOOGLE_SAFE_BROWSING_API_KEY=your_key
VIRUSTOTAL_API_KEY=your_key
HUGGINGFACE_API_KEY=your_key    ← enables real AI deepfake detection
```

> **Note:** The app works in **demo mode** without any API keys — mock results are generated so you can explore the full UI.

---

## 🔌 API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/scan/image` | Upload image for deepfake detection |
| POST | `/api/v1/scan/video` | Upload video for temporal analysis |
| POST | `/api/v1/scan/audio` | Upload audio for voice clone detection |
| POST | `/api/v1/scan/text` | Scan text for scam/phishing |
| POST | `/api/v1/scan/url` | Check URL against Safe Browsing + VirusTotal |
| POST | `/api/v1/scan/profile` | Analyze social profile for fake signals |
| GET | `/api/v1/history` | Paginated scan history |
| GET | `/api/v1/history/{id}` | Full scan detail |
| DELETE | `/api/v1/history/{id}` | Delete a scan record |
| GET | `/api/v1/stats` | User trust score + aggregate stats |
| GET | `/health` | Backend health check |

All endpoints require: `Authorization: Bearer <token>`

---

## 🎨 Frontend Pages

| Route | Page |
|-------|------|
| `/` | Animated splash screen |
| `/onboarding` | 4-slide feature introduction |
| `/auth` | Email/password + Google sign-in |
| `/home` | Dashboard (trust score, scan grid, recent scans) |
| `/scan/:type` | Scan page (image/video/audio/text/url/profile) |
| `/result/:id` | Result with confidence ring, heatmap, AI explanation |
| `/history` | Filterable scan history |
| `/map` | Global fraud bubble map |
| `/settings` | Preferences, AI model info, account |

---

## 🤖 AI Stack

| Feature | Technology |
|---------|-----------|
| Image Deepfake | HuggingFace ensemble (4 models) + fallback mock |
| Text Scam | Regex rules + HuggingFace BERT |
| URL Phishing | Google Safe Browsing + VirusTotal + heuristics |
| Fake Profile | Rule-based signal scoring |
| Auth | Firebase (with demo mode fallback) |
| Storage | Firestore (optional) |

---

## 📄 License
MIT
