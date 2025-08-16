#!/usr/bin/env node

// Simple MongoDB connection test
const { MongoClient } = require('mongodb');

const uri = "mongodb+srv://biancabr7:cywkurpjeMIc2vyC@moodii-cluster.qbiqko9.mongodb.net/?retryWrites=true&w=majority&appName=moodii-cluster";

async function testConnection() {
    console.log('🧪 Testing MongoDB Atlas connection...');
    
    const client = new MongoClient(uri);
    
    try {
        // Connect to MongoDB
        await client.connect();
        console.log('✅ Successfully connected to MongoDB Atlas!');
        
        // Test database operations
        const database = client.db('moodii');
        const collection = database.collection('mood_logs');
        
        // Insert a test document
        const testDoc = {
            mood: 'happy',
            confidence: 0.85,
            timestamp: new Date(),
            isTest: true,
            message: 'Connection test successful'
        };
        
        const insertResult = await collection.insertOne(testDoc);
        console.log('✅ Test document inserted with ID:', insertResult.insertedId);
        
        // Read the test document back
        const foundDoc = await collection.findOne({ _id: insertResult.insertedId });
        console.log('✅ Test document retrieved:', foundDoc.mood, 'with confidence:', foundDoc.confidence);
        
        // Count documents in collection
        const count = await collection.countDocuments();
        console.log('✅ Total documents in mood_logs collection:', count);
        
        // Clean up test document
        await collection.deleteOne({ _id: insertResult.insertedId });
        console.log('✅ Test document cleaned up');
        
        console.log('🎉 MongoDB Atlas connection test completed successfully!');
        
    } catch (error) {
        console.error('❌ Connection test failed:', error.message);
        process.exit(1);
    } finally {
        await client.close();
        console.log('🔌 Connection closed');
    }
}

// Run the test
testConnection().catch(console.error);
