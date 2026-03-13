# KitchenAssistant - Agent Guidelines

## 🍏 Core Identity
KitchenAssistant is a modern, minimalistic Android application designed to be a premium culinary companion. It features a unique **Liquid Glass** aesthetic that emphasizes depth, refraction, and high-fidelity visual feedback.

---

## 🎨 Design Principles: Liquid Glass
The app's soul lies in its visual consistency. When adding new features or components, strictly adhere to these rules:

1.  **Glassmorphism Over Flat Design:** Avoid solid colors or standard cards. Use translucent surfaces with refraction and blur.
2.  **Continuous Shapes:** Use `ContinuousRoundedRectangle` or `ContinuousCapsule` from the `com.kyant.capsule` library. Standard rounded corners (Squircle-lite) are not enough; we need the G2-continuity look.
3.  **Refraction & Depth:**
    *   Every glass element MUST use the `drawBackdrop` modifier.
    *   Utilize `lens` effects (refractionHeight, refractionAmount) to simulate thick glass.
    *   Enable `chromaticAberration` on primary action elements (buttons, user chat bubbles) to add a "liquid" premium feel.
4.  **Shared Backdrop:** All glass components in a screen must share a single `LayerBackdrop` instance. This ensures they all refract the same underlying background image consistently.
5.  **Specular Highlights:** Buttons and interactive elements should have a vertical gradient highlight (White -> Transparent) at the top to simulate light hitting the edge of the glass.
6.  **Minimalist Palette:**
    *   **Primary:** Indigo/Sky Blue gradients for actions.
    *   **Surfaces:** Frosted white (`Color.White.copy(alpha = 0.12f)` to `0.18f`).
    *   **Borders:** Subtle white strokes (`0.8dp` to `1.5dp`) with low opacity.

---

## 🛠 Technical Stack
*   **Language:** Kotlin
*   **UI Framework:** Jetpack Compose (Material 3 foundation)
*   **Aesthetic Engine:** 
    *   `com.kyant.backdrop`: For real-time background refraction and vibrancy.
    *   `com.kyant.capsule`: For smooth, continuous geometric shapes.

---

## 🏗 Key Components & Patterns

### The Backdrop Source
The background image (usually `R.drawable.kitchen_bg`) must be registered as the source:
```kotlin
Image(
    painter = painterResource(id = R.drawable.kitchen_bg),
    modifier = Modifier.layerBackdrop(backdrop)
)
```

### Creating a Glass Element
Use the `drawBackdrop` modifier with the following standard effects:
```kotlin
Modifier.drawBackdrop(
    backdrop = backdrop,
    shape = { ContinuousRoundedRectangle(20.dp) },
    effects = {
        vibrancy()
        blur(16.dp.toPx())
        lens(
            refractionHeight = 10.dp.toPx(),
            refractionAmount = 14.dp.toPx(),
            chromaticAberration = true
        )
    },
    onDrawSurface = {
        drawRect(Color.White.copy(alpha = 0.15f)) // Frosted tint
        drawRect(Color.White.copy(alpha = 0.35f), style = Stroke(width = 1.dp.toPx())) // Edge
    }
)
```

---

## 📂 Project Structure
*   `ui/components/`: Reusable glass-styled atoms (buttons, inputs).
*   `ui/screens/`: Feature layouts (Login, Home/Chat).
*   `ui/theme/`: Basic Material 3 definitions (Typography, basic Colors).

---

## 🚀 Future Roadmap & Intent
*   **Conversational First:** The assistant should feel like a chat with a pro chef.
*   **Step-by-Step Cooking:** Future screens should maintain the glass look while showing recipe steps with high-quality food photography as the backdrop.
*   **Measurement Tools:** Interactive unit converters that "float" on the glass surface.

**When in doubt: make it look like liquid glass.**
