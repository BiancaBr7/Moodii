# AudioFile and MoodLog Relationship Documentation

## **Relationship Type: One-to-One**

### **Overview**
Each MoodLog can have **one associated AudioFile** (optional), and each AudioFile belongs to **exactly one MoodLog**.

### **Database Schema**

#### **MoodLog**
```json
{
    "_id": "507f1f77bcf86cd799439011",  // MongoDB ObjectId (String)
    "title": "Morning mood",
    "transcription": "Feeling great today!",
    "moodType": 8,
    "userId": 1,
    "createdAt": "2025-01-24T09:15:30"
}
```

#### **AudioFile**
```json
{
    "_id": "507f1f77bcf86cd799439012",  // MongoDB ObjectId (String)
    "fileId": 1001,                     // Optional: sequential file number
    "logId": "507f1f77bcf86cd799439011", // Foreign Key -> MoodLog._id
    "audioBlob": "binary_data_here",
    "format": "audio/wav"
}
```

### **Relationship Rules**

✅ **Valid Scenarios:**
- MoodLog exists **without** AudioFile (text-only mood log)
- MoodLog exists **with one** AudioFile (audio mood log)

❌ **Invalid Scenarios:**
- AudioFile exists **without** MoodLog (orphaned audio)
- MoodLog has **multiple** AudioFiles (violates one-to-one)

### **API Usage Examples**

#### **1. Create MoodLog with Audio**
```bash
# Step 1: Create MoodLog
POST /api/moodlogs
{
    "title": "Audio mood log",
    "transcription": "Voice recording of my mood",
    "moodType": 7,
    "userId": 1
}

# Response: {"id": "507f1f77bcf86cd799439011", ...}

# Step 2: Upload Audio for this MoodLog
POST /api/audio/upload?logId=507f1f77bcf86cd799439011
[audio file upload]
```

#### **2. Get MoodLog with Associated Audio**
```bash
# Get MoodLog
GET /api/moodlogs/507f1f77bcf86cd799439011?userId=1

# Get associated audio (if exists)
GET /api/audio/by-log/507f1f77bcf86cd799439011
```

### **Database Constraints**

#### **Referential Integrity** (Application Level)
- `AudioFile.logId` must reference existing `MoodLog.id`
- When MoodLog is deleted, associated AudioFile should be deleted (cascade)

#### **Uniqueness**
- Each `AudioFile.logId` should be unique (one audio per mood log)

### **Implementation Notes**

1. **Type Consistency**: Both IDs use String (MongoDB ObjectId)
2. **Optional Relationship**: AudioFile is optional for MoodLog
3. **Cascade Delete**: Deleting MoodLog should delete associated AudioFile
4. **Error Handling**: Handle cases where audio exists but MoodLog doesn't

### **Future Enhancements**

Consider adding JPA-like annotations for better relationship definition:
```java
// In AudioFile.java
@DBRef
private MoodLog moodLog;  // Direct reference instead of logId
```

Or using MongoDB's `$lookup` for join queries in repositories.
