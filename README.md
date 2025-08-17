<div align="center">

# Moodii
<em>A Voice-Based Mood Tracker that listens.</em>

</div>

## Overview
Moodii is a voice‑driven mood tracking platform. Instead of tapping icons, users record brief audio reflections. A speech emotion recognition (SER) model (CNN + LSTM) extracts affective signals (tone, pace, timbre) and helps produce a structured mood log.

## Demo (Video)
<iframe width="560" height="315" src="https://www.youtube.com/embed/ERzkv3qhfCc?si=xOn8PUu943wJZeJe" title="YouTube video player" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin" allowfullscreen></iframe>

## Features
- Voice mood entries
- Emotion inference via deep learning
- Secure JWT auth & mood history
- Calendar visualization (Compose UI)
- Flavors for dev/prod endpoints
- Containerized backend + ML
- Automated CI for Android / Backend / ML

## Architecture
```
┌────────────┐        REST / JWT        ┌───────────────┐
│  Android   │ ───────────────────────► │  Spring Boot  │
│  App       │                          │  Backend API  │
└────────────┘          Upload          └──────┬────────┘
     ┌—————————————————————————————————————————│ HTTP to ML
	 |                                         ▼
	 ▼                                ┌──────────────────┐
┌─────────────┐                       │   ML Service     │
│   MongoDB   │ ◄── Mood logs / auth  │ Flask + TF Model │
└─────────────┘                       └──────────────────┘
```

## Tech Stack
| Layer | Tech |
|-------|------|
| Mobile | Kotlin, Jetpack Compose, Retrofit, OkHttp |
| Backend | Spring Boot 3.5, Java 21, MongoDB, Spring Security, JWT (jjwt) |
| ML | Python 3.9, Flask, TensorFlow 2.13, Librosa |
| Packaging | Docker, GHCR |
| CI/CD | GitHub Actions (frontend, backend, ML, combined) |

## Quick Start
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

## Roadmap
- HTTPS & security hardening
- Lighter on-device model
- Offline queue & sync
- Analytics dashboards
- Test coverage expansion (backend + ML)
