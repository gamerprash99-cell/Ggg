# LifeOS — UI/UX Design System & Styling Reference

This document provides the visual design system, token catalog, component library, and interaction principles for LifeOS.

---

## 1. Design Philosophy: The "Vibrant Palette" System

LifeOS utilizes the **Vibrant Palette** design system, a clean, high-contrast Material 3 visual language designed for clarity, visual rhythm, and cognitive ease.

- **Primary Canvas:** Soft lavender-tinted neutral surface (`#F7F2FA`), preventing harsh white glare while maintaining high contrast.
- **Card Hierarchy:** High-luminance surface containers (`#FEF7FF` and `#F3EDF7`) framed by subtle 1.dp structural borders (`#CAC4D0`).
- **Signature Primary:** Deep royal purple (`#6750A4`) paired with rich lavender containers (`#EADDFF`).
- **Semantic Accents:** Category-specific accents for instant visual scanning (Emerald `#2E7D32` for health and streaks, Cyan `#00639B` for finance, Pink `#B3261E` for urgent tasks, Amber `#E65100` for reminders).

---

## 2. Color Token Catalog (`Color.kt`)

### Light Theme Tokens
| Token Name | Hex Code | Role in UI |
| :--- | :--- | :--- |
| `VibrantPrimary` | `#6750A4` | Primary brand color, FABs, active indicators |
| `VibrantOnPrimary` | `#FFFFFF` | Text and icons on primary elements |
| `VibrantPrimaryContainer` | `#EADDFF` | Active navigation pills, primary highlight cards |
| `VibrantOnPrimaryContainer`| `#21005D` | High-contrast text on primary containers |
| `VibrantSecondary` | `#625B71` | Secondary actions, inactive tab headers |
| `VibrantSecondaryContainer`| `#E8DEF8` | Secondary chips and tags |
| `VibrantBgLight` | `#F7F2FA` | Global scaffold background |
| `VibrantSurfaceLight` | `#FEF7FF` | Card container surfaces |
| `VibrantSurfaceVariantLight`| `#F3EDF7` | Bottom bar container, secondary surface |
| `VibrantOutlineLight` | `#CAC4D0` | Structural card borders and dividers |

### Dark Theme Tokens
| Token Name | Hex Code | Role in UI |
| :--- | :--- | :--- |
| `VibrantBgDark` | `#141218` | Global dark scaffold background |
| `VibrantSurfaceDark` | `#1D1B20` | Dark surface container cards |
| `VibrantPrimaryDark` | `#D0BCFF` | Light purple primary in dark mode |
| `VibrantPrimaryContainerDark`| `#4F378B` | Deep purple container in dark mode |
| `VibrantTextPrimaryDark` | `#E6E0E9` | Primary text on dark surfaces |
| `VibrantOutlineDark` | `#938F99` | Subtle borders in dark mode |

---

## 3. Typography System (`Type.kt`)

LifeOS implements Material 3 typography with optimized line heights and letter spacing:
- **Display Large / Headline Large:** Bold sans-serif for screen titles, metrics, and greeting banners.
- **Title Medium / SemiBold:** Section headers and card titles.
- **Body Large / Regular:** Note body text, diary reflections, and task descriptions.
- **Label Small / Medium:** Status chips, timestamps, and bottom bar navigation labels.

---

## 4. Reusable UI Components (`Components.kt`)

### 4.1 `GlassCard`
- **Purpose:** Standard elevated container for cards throughout the app.
- **Specs:** 28.dp rounded corners, 1.dp outline variant border, subtle surface elevation. Supports optional tap interaction with ripple feedback.

### 4.2 `GradientBannerCard`
- **Purpose:** Hero banner at top of dashboard and key milestones.
- **Specs:** Rich purple linear gradient (`#6750A4` → `#5B3F9B`) with a subtle glowing corner accent and optional action button.

### 4.3 `SectionHeader`
- **Purpose:** Clean, bold typography paired with an optional right-aligned action button (e.g., "See All" or "+ Add").

### 4.4 `StatPill`
- **Purpose:** Compact metrics display showing an icon, value, and label in a capsule layout.

### 4.5 `AppLockDialog`
- **Purpose:** Numeric 4-digit PIN pad overlay for privacy protection.

---

## 5. Accessibility & Ergonomics

1. **Touch Targets:** All interactive elements (checkboxes, navigation icons, folder chips) enforce a minimum touch target size of **48.dp × 48.dp**.
2. **Window Insets:** Fully edge-to-edge aware via `enableEdgeToEdge()`, `statusBarsPadding()`, and `navigationBarsPadding()`.
3. **Automated Testing Tags:** All primary action buttons, tabs, and input fields include explicit `Modifier.testTag()` identifiers (e.g., `"nav_capture_fab"`, `"nav_item_notes"`).
