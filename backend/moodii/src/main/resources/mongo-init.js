// db.createCollection('moods')
// db.moods.insertMany([
//     {
//         "moodId": 1,
//         "type": "Happy",
//         "emoji": "😊"
//     },
//     {
//         "moodId": 2,
//         "type": "Sad",
//         "emoji": "😢"
//     },
//     {
//         "moodId": 3,
//         "type": "Angry",
//         "emoji": "😠"
//     },
//     {
//         "moodId": 4,
//         "type": "Fearful",
//         "emoji": "😨"
//     },
//     {
//         "moodId": 5,
//         "type": "Neutral",
//         "emoji": "😐"
//     }
// ])

// db.moods.createIndex({ "moodId": 1 }, { unique: true });

db = db.getSiblingDB('moodii');
db.moods.createIndex({ "moodId": 1 }, { unique: true });