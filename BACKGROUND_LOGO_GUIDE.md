# Adding Custom Background Images and Logo to Moodii App

This guide explains how to add your own background images and app logo to replace the default ones.

## Quick Setup (Already Working)

The app now includes:
✅ **Background gradients and patterns** - Automatic gradient backgrounds with mood emoji patterns
✅ **App logo** - Custom SVG logo with the Moodii branding
✅ **Responsive design** - Logo and backgrounds adapt to different screen sizes

## How to Add Your Own Images

### Option 1: Add Image Files (PNG, JPG)

1. **Prepare your images**:
   - **Background image**: 1080x1920px or higher (portrait orientation)
   - **Logo**: 512x512px (square, transparent background recommended)
   - **Formats**: PNG (for transparency), JPG (for photos)

2. **Add images to the project**:
   ```
   frontend/app/src/main/res/drawable/
   ├── my_background.png      <- Your background image
   ├── my_logo.png           <- Your logo image
   └── ...
   ```

3. **Update the code**:
   ```kotlin
   // ALREADY UPDATED! Your PNG images are now being used automatically.
   // The code already references:
   // R.drawable.app_background (now uses your app_background.png)
   // R.drawable.app_logo (now uses your app_logo.png)
   
   // Background alpha has been increased from 0.4f to 0.8f for better visibility
   ```

### Option 2: Update Vector Drawables (SVG-style)

1. **Edit existing vectors**:
   - `app_background.xml` - Contains gradient and pattern
   - `app_logo.xml` - Contains the smiley face logo

2. **Use Android Studio's Vector Asset Studio**:
   - Right-click `res/drawable` → New → Vector Asset
   - Choose "Local file" and select your SVG
   - Import as `my_custom_logo.xml`

### Option 3: Multiple Background Images

You can create different backgrounds for different screens:

```kotlin
// Create different background files:
// app_background_login.xml
// app_background_dashboard.xml  
// app_background_recorder.xml

// Use them conditionally:
@Composable
fun AppBackground(
    backgroundType: BackgroundType = BackgroundType.Default,
    content: @Composable BoxScope.() -> Unit
) {
    val backgroundResource = when (backgroundType) {
        BackgroundType.Login -> R.drawable.app_background_login
        BackgroundType.Dashboard -> R.drawable.app_background_dashboard
        BackgroundType.Recorder -> R.drawable.app_background_recorder
        BackgroundType.Default -> R.drawable.app_background
    }
    
    // ... rest of component
}
```

## Current Implementation

### 🎨 **Background System**
- **Gradient base**: Purple gradient that matches your app theme
- **Pattern overlay**: Subtle mood emoji pattern for branding
- **Image layer**: Optional image background (currently vector-based)
- **Overlay**: Semi-transparent overlay for text readability

### 🏷️ **Logo System**
- **Scalable design**: Works at different sizes (Small, Medium, Large, ExtraLarge)
- **Text option**: Can show/hide "MOODII" text below logo
- **Consistent styling**: Matches app's pixelated theme

### 📱 **Usage Examples**

The background and logo are now used in:
- ✅ **DashboardScreen** - Logo in header, background throughout
- ✅ **AudioRecorderScreen** - Background with overlay for better contrast
- 🔄 **Other screens** - Easy to add to any screen with `AppBackground { }`

## Customization Options

### Background Customization
```kotlin
AppBackground(
    showOverlay = true,        // Show dark overlay for text readability
    overlayAlpha = 0.7f       // Overlay transparency (0.0 to 1.0)
) {
    // Your screen content
}
```

### Logo Customization
```kotlin
AppLogo(
    size = LogoSize.Large,     // Small, Medium, Large, ExtraLarge
    showText = true           // Show "MOODII" text below logo
)
```

## File Structure

```
res/drawable/
├── app_background.xml         <- Main background (gradient + pattern)
├── app_logo.xml              <- App logo (smiley face design)
├── ic_trash_can.xml          <- Trash can icon (existing)
└── [your custom images]      <- Add your images here
```

## Design Guidelines

### Background Images
- **Resolution**: At least 1080x1920px for crisp display
- **Aspect ratio**: Portrait orientation (9:16 or similar)
- **Content**: Avoid busy backgrounds that make text hard to read
- **Colors**: Should complement your purple theme

### Logo Design
- **Size**: Square format works best (1:1 aspect ratio)
- **Style**: Simple, recognizable design
- **Colors**: Should work on both light and dark backgrounds
- **Format**: PNG with transparency for best results

## Testing Your Changes

1. **Add your images** to `res/drawable/`
2. **Update the resource IDs** in `AppBackground.kt`
3. **Build and run** the app
4. **Check all screens** to ensure the background looks good everywhere

The background and logo system is now fully functional and ready for your custom branding!
