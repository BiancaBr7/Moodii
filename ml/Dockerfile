# Use official Python image
FROM python:3.9-slim

# Set environment variables
ENV PYTHONDONTWRITEBYTECODE 1
ENV PYTHONUNBUFFERED 1
ENV MODEL_DIR=/app/ml/models

# Create and set working directory
WORKDIR /app

# Copy requirements first to leverage Docker cache
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copy the entire project
COPY . .

# Ensure the models directory exists
RUN mkdir -p ${MODEL_DIR}

# Expose the port the app runs on
EXPOSE 5000

# Command to run the API
CMD ["python", "ml/api/app.py"]