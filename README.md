<div align="center">

# Moodii — Voice-Based Mood Tracking

[![Demo Video](https://img.youtube.com/vi/ERzkv3qhfCc/hqdefault.jpg)](https://www.youtube.com/watch?v=ERzkv3qhfCc)

<em>Speak. Reflect. Understand your emotional patterns over time.</em>

</div>

---

## Table of Contents
1. Overview
2. Demo (Video)
3. Features
4. Architecture
5. Tech Stack
6. Quick Start
7. Running Each Component
8. Android App Flavors
9. CI/CD Pipelines
10. API (High-Level)
11. ML Model
12. Environment Variables
13. Development Tips
14. Roadmap
15. Contributing

---

## 1. Overview
Moodii is a voice‑driven mood tracking platform. Instead of tapping icons, users record brief audio reflections. A speech emotion recognition (SER) model (CNN + LSTM) extracts affective signals (tone, pace, timbre) and helps produce a structured mood log.

## 2. Demo (Video)
Watch: https://www.youtube.com/watch?v=ERzkv3qhfCc

## 3. Features
- Voice mood entries
- Emotion inference via deep learning
- Secure JWT auth & mood history
- Calendar visualization (Compose UI)
- Flavors for dev/prod endpoints
- Containerized backend + ML
- Automated CI for Android / Backend / ML

## 4. Architecture
```
┌────────────┐        REST / JWT         ┌───────────────┐
│  Android   │ ───────────────────────► │  Spring Boot  │
│  App       │                          │  Backend API  │
└─────┬──────┘                          └──────┬────────┘
	│  Upload (future)                       │ HTTP to ML
	│                                        ▼
	▼                               ┌──────────────────┐
┌─────────────┐                      │  ML Service       │
│  MongoDB     │◄── Mood logs / auth │  Flask + TF Model │
└─────────────┘                      └──────────────────┘
```

## 5. Tech Stack
| Layer | Tech |
|-------|------|
| Mobile | Kotlin, Jetpack Compose, Retrofit, OkHttp |
| Backend | Spring Boot 3.5, Java 21, MongoDB, Spring Security, JWT (jjwt) |
| ML | Python 3.9, Flask, TensorFlow 2.13, Librosa |
| Packaging | Docker, GHCR |
| CI/CD | GitHub Actions (frontend, backend, ML, combined) |

## 6. Quick Start
Clone & enter:
```bash
git clone https://github.com/<your-org-or-user>/Moodii.git
cd Moodii
```

### Backend
```bash
./backend/moodii/mvnw -f backend/moodii/pom.xml spring-boot:run
```
Runs on 8080.

### ML Service
```bash
cd ml/CNN+LSTM_Model
python -m venv .venv && source .venv/bin/activate  # Windows: .venv\Scripts\activate
pip install -r requirements.txt
python api.py  # or gunicorn api:app -b 0.0.0.0:5000
```
Runs on 5000.

### Android App
Open `frontend` in Android Studio or:
```bash
cd frontend
./gradlew assembleDevDebug
```

## 7. Running Each Component (Docker ML Example)
```bash
docker build -f ml/CNN+LSTM_Model/Dockerfile -t moodii-ml-local ml/CNN+LSTM_Model
docker run -p 5000:5000 moodii-ml-local
```

## 8. Android App Flavors
- dev: local/staging (10.0.2.2)
- prod: remote backend (Azure)

## 9. CI/CD Pipelines
Workflows in `.github/workflows/` build Android, backend JAR, ML image. Azure deploy disabled currently (insufficient directory permissions) and may be re-enabled later.

## 10. API (High-Level)
Auth:
- POST /auth/register
- POST /auth/login

ML:
- GET /health
- GET /model-info
- POST /predict (multipart audio)

## 11. ML Model
CNN + LSTM over MFCC feature sequences (length ~100 x 13) predicting emotion labels: angry, disgust, fear, happy, neutral, sad, surprise.

## 12. Environment Variables
Backend: SPRING_DATA_MONGODB_URI, ML_API_URL, (JWT secret recommended)
ML: MODEL_PATH (optional override)
Android: BuildConfig field API_BASE_URL via flavors.

## 13. Development Tips
- Use prod flavor when testing remote backend.
- Remove verbose logging for release.
- Consider TFLite for future on-device inference.

## 14. Roadmap
- HTTPS & security hardening
- Lighter on-device model
- Offline queue & sync
- Analytics dashboards
- Test coverage expansion (backend + ML)

## 15. Contributing
PRs welcome. Keep workflows green before requesting review.

---
> Demo Video: https://www.youtube.com/watch?v=ERzkv3qhfCc

_This README will evolve with deployment & feature changes._
