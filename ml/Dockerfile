# FROM python:3.9-slim

# WORKDIR /app
# COPY requirements.txt .
# RUN pip install --upgrade pip && \
#     pip install -i https://pypi.tuna.tsinghua.edu.cn/simple -r requirements.txt

# COPY . .

# CMD ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8000"]

FROM python:3.9-slim

WORKDIR /app

# Install system dependencies first
RUN apt-get update && \
    apt-get install -y --no-install-recommends gcc python3-dev \
    libsndfile1 \
    ffmpeg \
    && rm -rf /var/lib/apt/lists/*

# Configure pip
COPY requirements.txt .
RUN python -m pip install --upgrade pip && \
    pip config set global.timeout 100 && \
    pip config set global.retries 10 && \
    pip install --no-cache-dir -r requirements.txt && \
    apt-get purge -y --auto-remove gcc python3-dev

COPY . .

COPY ./docker/ml/models /app/models
RUN mkdir -p /app/models

CMD ["python", "-m", "uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8000"]