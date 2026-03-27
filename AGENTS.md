# AGENTS.md

This file provides guidance to AI Agents (Codex, Claude Code, etc.) when working with code in this repository.

## Project Overview

**Jyutping** is a Cantonese Input Method Editor (IME) for Android using the Hong Kong Linguistic Society's Jyutping romanization scheme. 

The project uses **Jetpack Compose** for UI and **Kotlin** for implementation. It includes a build-time data preparation module that generates the SQLite database used by the IME.

## Building and Development

### Prerequisites
- Android Studio 2025.1.1+
- Android SDK 36
- Java 21

### One-Time Setup: Prepare Databases

Before opening in Android Studio, prepare the input method databases:

```bash
cd ./preparing/
./gradlew run
```

This generates `appdb.sqlite3` which is copied to `app/src/main/assets/`. The database contains:
- 20+ tables with input lexicon, character data, and linguistic information
- Indexes for fast lookups during input
- Symbol, mark, emoji, and variant character tables

The preparing module is a standalone Gradle application that:
1. Loads .txt resource files (jyutping.txt, collocation.txt, etc.)
2. Creates normalized SQLite tables with proper indexes
3. Outputs the database to app assets

**Note**: Changes to resource files or database schema require re-running this step.

### Building the App

```bash
# Debug build
./gradlew :app:assembleDebug

# Release build
./gradlew :app:assembleRelease
```

### Running Tests

Tests are currently disabled (`tasks.withType<Test>().configureEach { enabled = false }`). To enable:
1. Remove the disable block in `app/build.gradle.kts`
2. Run: `./gradlew :app:testDebugUnitTest`
3. Run instrumented tests: `./gradlew :app:connectedAndroidTest`

## Architecture Overview

### Layered Design

```
Presentation (Compose UI)
  ↓ JyutpingInputMethodService (state holder + event handler)
  ↓ Business Logic (Segmenter, Researcher, Converter)
  ↓ Data Access (DatabaseHelper, UserLexiconHelper)
  ↓ SQLite Database
```

### Key Modules

#### **app/** - Main IME Application (~19,400 LOC, 163 Kotlin files)

**JyutpingInputMethodService.kt** - Core IME service
- Manages 30+ MutableStateFlow properties (input mode, candidates, buffer, settings)
- Handles soft keyboard input (VirtualInputKey enums) and physical keyboard events
- Processes input into candidates via Segmenter, Researcher, and Converter
- Records user memory in UserLexiconHelper for learning

**Package structure:**
- `keyboard/` - 50+ Compose components for different keyboard layouts and candidate display
- `models/` - Input/output data models, linguistic structures, and processing logic
- `utilities/` - Database access, shape mapping (stroke/cangjie), character conversion
- `editingpanel/` - Keyboard text editing buttons (copy/paste/cursor movement)
- `search/` - Part of the main app, linguistic lookup (definitions, collocations, homophones)
- `app/` - Main part of the main app, settings and educational screens (Cantonese, romanization guides)
- `ui/` - Compose theme and reusable components
- `tenkey/`, `emoji/`, `feedback/` - Specialized features

#### **preparing/** - Build-Time Data Preparation (~1,600 LOC, 17 files)

Generates `appdb.sqlite3` by:
- **AppDataPreparer** - Creates linguistic reference tables (definitions, collocations, dictionaries)
- **KeyboardDataPreparer** - Creates input method tables (core lexicon, stroke/cangjie/pinyin data, variants)
- Database includes 70+ indexes for performance

## Critical Data Flows

### Soft Keyboard Input Pipeline
```
User taps key → VirtualInputKey → bufferEvents (observable)
  → Determine input mode (Cantonese/Pinyin/Stroke/etc.)
  → Segmenter.segment() → Researcher.suggest()
  → UserLexiconHelper.search() + fetchTextMarks()
  → Converter.dispatch() (merging + sorting)
  → candidates StateFlow → CandidateBoard renders
```

### Physical Keyboard Input
- Hardware key events → `onKeyDown()/onKeyUp()` → `handlePhysicalKeyEvent()`
- PhysicalKeyMapper converts key codes to VirtualInputKeys
- Can trigger full CandidateBoard or inline PhysicalKeyboardCandidateBar
- Numbers 1-9 and 0 select candidates 1-10 respectively; Tab cycles through groups of 10

### Candidate Selection & User Memory
- User selects candidate → `selectCandidate()`
- Commits text via InputConnection
- Records in UserLexiconHelper (separate database) for future ranking
- Clears buffer if input is complete

## Database Schema

**Main Database: appdb.sqlite3**

Core input tables:
- `core_lexicon` - Main word database
- `structure_table` - Character composition (stroke/shape codes)
- `syllable_table`, `pinyin_syllable_table` - Syllable mappings
- `stroke_table`, `cangjie_table`, `quick_table` - Shape input methods

Linguistic reference tables:
- `collocation_table` - Word collocations
- `dictionary_table` - Character/word definitions
- `yingwaa_table`, `chohok_table`, `fanwan_table`, `gwongwan_table` - Specialized dictionaries

Display/variant tables:
- `symbol_table`, `mark_table` - Symbols and tone marks
- `variant_simplified`, `variant_hongkong`, etc. - Character variants
- `emoji_skin_map` - Emoji modifiers

All tables are indexed on frequently queried columns (spell, anchors, code) for fast lookup.

## Important Implementation Patterns

### State Management
- JyutpingInputMethodService uses MutableStateFlow for all UI state
- Compose components use `collectAsState()` to subscribe to state changes
- No separate ViewModel; service acts as state holder

### Input Processing
- VirtualInputKey enum covers all possible input keys
- BasicInputEvent wraps key with keyboard case info
- Segmenter breaks input into syllables
- Researcher queries database for matching words
- Converter ranks results by frequency, user memory, and context

### Character Conversion
- Simplifier converts Traditional → Simplified
- HongKongVariantConverter, TaiwanVariantConverter handle regional variants
- CharacterStandard StateFlow determines display mode (applied in Converter)

### Database Queries
- DatabaseHelper provides typed query methods
- All queries use parameterized statements (SQL injection safe)
- Results wrapped in domain models (Candidate, Pronunciation, etc.)
- UserLexiconHelper maintains separate user-learned data

## Common Development Tasks

### Adding a New Keyboard Layout
1. Create new `@Composable` function in `keyboard/` package
2. Extend layout data (rows, key configuration) from existing layouts
3. Register in JyutpingInputMethodService's keyboard mode handling
4. Test input flow via physical and soft keyboard

### Modifying Character Conversion
- TaiwanVariantConverter, HongKongVariantConverter handle variants
- Simplifier handles Traditional-Simplified conversion
- Changes applied in Converter.dispatch() before ranking
- Resource files in `preparing/src/main/resources/` drive variant generation

### Debugging IME Input
- JyutpingInputMethodService.bufferEvents observable receives all input
- CandidateBoard displays top candidates in real-time
- UserLexiconHelper can be inspected for learned words
- Physical keyboard candidate bar shows current state

## File Organization Notes

- Source code: `app/src/main/kotlin/org/jyutping/jyutping/`
- Resources: `app/src/main/res/`, `preparing/src/main/resources/`
- Database asset: `app/src/main/assets/appdb.sqlite3`
- Build outputs: `app/build/outputs/`

## Known Constraints

- Tests disabled in build.gradle.kts (IME services difficult to unit test)
- JyutpingInputMethodService is large; state logic concentrated here
- Physical keyboard support only on devices with hardware keyboards
- Database is pre-built; schema changes require preparing module rebuild
- Character variant tables add database size but enable fast conversion

## Version & Build Info

- Min SDK: 29 (Android 10)
- Target SDK: 36 (Android 16)
- Compile SDK: 36
