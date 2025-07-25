# MoodLog API Documentation

## Base URL
All endpoints are prefixed with `/api`

## Authentication
All endpoints require a `userId` parameter for user identification and security.

## Endpoints

### 1. Create Mood Log
**POST** `/api/moodlogs`

Creates a new mood log entry.

**Request Body:**
```json
{
    "title": "string",
    "transcription": "string", 
    "moodType": "integer",
    "userId": "integer"
}
```

**Response:** `201 Created`
```json
{
    "id": "507f1f77bcf86cd799439011",
    "title": "My mood today",
    "transcription": "I feel great today",
    "moodType": 5,
    "userId": 123,
    "createdAt": "2025-01-24T10:30:00"
}
```

### 2. Update Mood Log Mood
**POST** `/api/moodlog?mood=xxx&id=xxx&userId=xxx`

Updates the mood of an existing log.

**Query Parameters:**
- `mood` (integer): New mood value
- `id` (string): Mood log ID (MongoDB ObjectId)
- `userId` (integer): User ID for security

**Response:** `200 OK`
```json
{
    "id": "507f1f77bcf86cd799439011",
    "title": "My mood today",
    "transcription": "I feel great today", 
    "moodType": 7,
    "userId": 123,
    "createdAt": "2025-01-24T10:30:00"
}
```

### 3. Get Mood Logs by Date
**GET** `/api/moodlogs?date=YYYY-MM-DD&userId=xxx`

Retrieves all mood logs for a specific date.

**Query Parameters:**
- `date` (string): Date in YYYY-MM-DD format
- `userId` (integer): User ID

**Response:** `200 OK`
```json
[
    {
        "id": 1,
        "title": "Morning mood",
        "transcription": "Feeling energetic",
        "moodType": 8,
        "userId": 123,
        "createdAt": "2025-01-24T09:00:00"
    },
    {
        "id": 2,
        "title": "Evening mood",
        "transcription": "Relaxed and peaceful",
        "moodType": 6,
        "userId": 123,
        "createdAt": "2025-01-24T20:00:00"
    }
]
```

### 4. Get Mood Logs by Month (Calendar View)
**GET** `/api/moodlogs/calendar?month=YYYY-MM&userId=xxx`

Retrieves all mood logs for a specific month.

**Query Parameters:**
- `month` (string): Month in YYYY-MM format
- `userId` (integer): User ID

**Response:** `200 OK`
```json
[
    {
        "id": 1,
        "title": "Daily mood",
        "transcription": "Various moods throughout the month",
        "moodType": 7,
        "userId": 123,
        "createdAt": "2025-01-15T14:30:00"
    }
]
```

### 5. Get Mood Log by ID
**GET** `/api/moodlogs/{id}?userId=xxx`

Retrieves a specific mood log by ID.

**Path Parameters:**
- `id` (string): Mood log ID (MongoDB ObjectId)

**Query Parameters:**
- `userId` (integer): User ID for security

**Response:** `200 OK`
```json
{
    "id": "507f1f77bcf86cd799439011",
    "title": "My mood today",
    "transcription": "Detailed description of my mood",
    "moodType": 5,
    "userId": 123,
    "createdAt": "2025-01-24T10:30:00"
}
```

### 6. Delete Mood Log
**DELETE** `/api/moodlogs/{id}?userId=xxx`

Deletes a specific mood log by ID.

**Path Parameters:**
- `id` (string): Mood log ID (MongoDB ObjectId)

**Query Parameters:**
- `userId` (integer): User ID for security

**Response:** `204 No Content`

### 7. Get All Mood Logs for User (Bonus)
**GET** `/api/users/{userId}/moodlogs`

Retrieves all mood logs for a specific user.

**Path Parameters:**
- `userId` (integer): User ID

**Response:** `200 OK`
```json
[
    {
        "id": 1,
        "title": "Mood 1",
        "transcription": "First mood entry",
        "moodType": 5,
        "userId": 123,
        "createdAt": "2025-01-24T10:30:00"
    },
    {
        "id": 2,
        "title": "Mood 2", 
        "transcription": "Second mood entry",
        "moodType": 7,
        "userId": 123,
        "createdAt": "2025-01-24T15:45:00"
    }
]
```

## Error Responses

### 400 Bad Request
```json
{
    "error": "Invalid request",
    "message": "Detailed error message"
}
```

### 404 Not Found
```json
{
    "error": "Not found",
    "message": "Mood log not found or access denied"
}
```

### 500 Internal Server Error
```json
{
    "error": "Internal server error", 
    "message": "An unexpected error occurred"
}
```

## Security Notes
- All endpoints include user-based access control
- Users can only access their own mood logs
- ID-based operations require both the mood log ID and user ID for security
