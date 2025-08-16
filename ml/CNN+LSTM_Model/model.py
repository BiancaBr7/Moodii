import tensorflow as tf
from tensorflow.keras.models import Model
from tensorflow.keras.layers import (
    Input, Conv2D, MaxPooling2D, BatchNormalization, 
    Bidirectional, LSTM, Dense, Dropout, GlobalMaxPooling2D,
    Attention, Concatenate, Reshape, Permute, Lambda, Multiply
)
from tensorflow.keras.optimizers import Adam
from tensorflow.keras.callbacks import EarlyStopping, ReduceLROnPlateau


class CNNLSTMEmotionModel:
    def __init__(self, input_shape=(128, 128, 1), num_classes=4):
        """
        CNN+LSTM Model for Emotion Recognition
        
        Args:
            input_shape: Shape of mel spectrogram input (height, width, channels)
            num_classes: Number of emotion classes (4: Angry, Happy, Sad, Neutral)
        """
        self.input_shape = input_shape
        self.num_classes = num_classes
        self.model = None
    
    def build_feature_extraction_block(self, x, filters, kernel_size=(3, 3), pool_size=(2, 2)):
        """
        Build a feature extraction block: CNN -> BatchNorm -> MaxPooling
        """
        x = Conv2D(filters, kernel_size, activation='relu', padding='same')(x)
        x = BatchNormalization()(x)
        x = MaxPooling2D(pool_size)(x)
        return x
    
    def build_attention_layer(self, x):
        """
        Build attention mechanism for LSTM outputs
        """
        # Simple attention mechanism using Keras layers
        attention_weights = Dense(x.shape[-1], activation='tanh')(x)
        attention_weights = Dense(1, activation='softmax')(attention_weights)
        
        # Apply attention weights and sum using Keras layers
        attended = Multiply()([x, attention_weights])
        attended_output = Lambda(lambda x: tf.reduce_sum(x, axis=1))(attended)
        
        return attended_output
    
    def build_model(self):
        """
        Build the complete CNN+LSTM model architecture
        """
        # Input layer for mel spectrogram
        inputs = Input(shape=self.input_shape, name='mel_spectrogram_input')
        
        # Feature Extraction Part (CNN Layers)
        x = inputs
        
        # First CNN block
        x = self.build_feature_extraction_block(x, filters=32)
        x = Dropout(0.25)(x)
        
        # Second CNN block  
        x = self.build_feature_extraction_block(x, filters=64)
        x = Dropout(0.25)(x)
        
        # Third CNN block
        x = self.build_feature_extraction_block(x, filters=128)
        x = Dropout(0.25)(x)
        
        # Prepare for LSTM input
        # Reshape for time series input: (batch, time_steps, features)
        shape_before_lstm = x.shape
        x = Reshape((shape_before_lstm[1], shape_before_lstm[2] * shape_before_lstm[3]))(x)
        
        # Classification Model Part (LSTM + Attention)
        
        # First Bidirectional LSTM layer
        lstm_out1 = Bidirectional(LSTM(128, return_sequences=True, dropout=0.3))(x)
        
        # Second Bidirectional LSTM layer
        lstm_out2 = Bidirectional(LSTM(64, return_sequences=True, dropout=0.3))(lstm_out1)
        
        # Attention Layer
        attended_output = self.build_attention_layer(lstm_out2)
        
        # Dense layers for final classification
        dense_out = Dense(256, activation='relu')(attended_output)
        dense_out = Dropout(0.5)(dense_out)
        dense_out = Dense(128, activation='relu')(dense_out)
        dense_out = Dropout(0.3)(dense_out)
        
        # Output layer for emotion classification
        outputs = Dense(self.num_classes, activation='softmax', name='emotion_output')(dense_out)
        
        # Create the model
        self.model = Model(inputs=inputs, outputs=outputs, name='CNN_LSTM_Emotion_Model')
        
        return self.model
    
    def compile_model(self, learning_rate=0.001):
        """
        Compile the model with optimizer, loss, and metrics
        """
        if self.model is None:
            raise ValueError("Model must be built before compilation. Call build_model() first.")
        
        self.model.compile(
            optimizer=Adam(learning_rate=learning_rate),
            loss='categorical_crossentropy',
            metrics=['accuracy', 'precision', 'recall']
        )
    
    def get_model_summary(self):
        """
        Print model architecture summary
        """
        if self.model is None:
            raise ValueError("Model must be built first. Call build_model() first.")
        
        return self.model.summary()
    
    def train_model(self, train_data, train_labels, validation_data, validation_labels, 
                   epochs=100, batch_size=32):
        """
        Train the model with early stopping and learning rate reduction
        """
        if self.model is None:
            raise ValueError("Model must be built and compiled first.")
        
        # Callbacks
        early_stopping = EarlyStopping(
            monitor='val_loss',
            patience=15,
            restore_best_weights=True
        )
        
        lr_reducer = ReduceLROnPlateau(
            monitor='val_loss',
            factor=0.5,
            patience=10,
            min_lr=1e-7
        )
        
        # Train the model
        history = self.model.fit(
            train_data, train_labels,
            validation_data=(validation_data, validation_labels),
            epochs=epochs,
            batch_size=batch_size,
            callbacks=[early_stopping, lr_reducer],
            verbose=1
        )
        
        return history


def create_emotion_model(input_shape=(128, 128, 1), num_classes=4):
    """
    Factory function to create and return a CNN+LSTM emotion recognition model
    
    Args:
        input_shape: Shape of mel spectrogram input 
        num_classes: Number of emotion classes
    
    Returns:
        Compiled CNN+LSTM model
    """
    emotion_model = CNNLSTMEmotionModel(input_shape, num_classes)
    model = emotion_model.build_model()
    emotion_model.compile_model()
    
    return model, emotion_model


# Example usage
if __name__ == "__main__":
    # Create the model
    model, emotion_classifier = create_emotion_model(
        input_shape=(128, 128, 1),  # Mel spectrogram dimensions
        num_classes=4  # Angry, Happy, Sad, Neutral
    )
    
    # Print model summary
    print("CNN+LSTM Emotion Recognition Model Architecture:")
    print("=" * 60)
    emotion_classifier.get_model_summary()
    
    # Print model information
    print(f"\nTotal parameters: {model.count_params():,}")
    print(f"Input shape: {model.input_shape}")
    print(f"Output shape: {model.output_shape}")
