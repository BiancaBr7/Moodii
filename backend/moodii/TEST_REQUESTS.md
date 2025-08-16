# MoodLog API Test Requests

## Test with curl or REST client

### 1. Create a new mood log
```bash
curl -X POST http://localhost:8080/api/moodlogs \
  -H "Content-Type: application/json" \
  -d '{
    "title": "My first mood log",
    "transcription": "I feel great today after a good workout",
    "moodType": 8,
    "userId": 1
  }'
```

### 2. Update mood of existing log
```bash
curl -X POST "http://localhost:8080/api/moodlog?mood=9&id=507f1f77bcf86cd799439011&userId=1"
```

### 3. Get mood logs for specific date
```bash
curl -X GET "http://localhost:8080/api/moodlogs?date=2025-01-24&userId=1"
```

### 4. Get mood logs for specific month (calendar)
```bash
curl -X GET "http://localhost:8080/api/moodlogs/calendar?month=2025-01&userId=1"
```

### 5. Get specific mood log by ID
```bash
curl -X GET "http://localhost:8080/api/moodlogs/507f1f77bcf86cd799439011?userId=1"
```

### 6. Delete mood log
```bash
curl -X DELETE "http://localhost:8080/api/moodlogs/507f1f77bcf86cd799439011?userId=1"
```

### 7. Get all mood logs for user
```bash
curl -X GET "http://localhost:8080/api/users/1/moodlogs"
```
