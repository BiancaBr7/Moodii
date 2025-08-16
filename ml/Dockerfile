# Use Python 3.9 slim (compatible with pinned legacy ML libs)
FROM python:3.9-slim

# Set environment variables
ENV PYTHONDONTWRITEBYTECODE=1 \
	PYTHONUNBUFFERED=1 \
	MODEL_DIR=/app/ml/models

# System dependencies for audio processing (librosa -> soundfile -> libsndfile1)
RUN apt-get update \
	&& apt-get install -y --no-install-recommends libsndfile1 \
	&& rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /app

# Copy requirements first to leverage Docker cache
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copy application code (simple API variant uses api/app.py and models directory)
COPY api/app.py ./api.py
COPY models ./models

# Ensure the models directory exists
RUN mkdir -p ${MODEL_DIR}

# Expose the port the app runs on
EXPOSE 5000

# Command to run the API
CMD ["python", "api.py"]