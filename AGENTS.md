# AGENTS.md

This file describes the current repository structure and the checks expected when changing it.

## Project overview

Jyutping is a Cantonese input method for Android. The repository contains two user-facing Compose surfaces:

- `MainActivity` is the launcher and reference app. It provides setup links, dictionary search, Jyutping and Cantonese reference screens, text-to-speech, display-language settings, and project information.
- `JyutpingInputMethodService` is the input method. It owns keyboard state, input events, candidate generation and selection, user settings, physical-keyboard handling, and learned input memory.

The root Gradle build contains the `:app` Android application. `preparing/` is a separate Gradle build that generates the SQLite asset consumed by both surfaces.

## Toolchain

The checked-in build currently uses:

- Android Studio 2026.1.3 or newer
- JDK 21
- Gradle 9.7.1
- Android Gradle plugin 9.3.2
- Kotlin 2.4.10
- compile SDK 37
- target SDK 37
- minimum SDK 33 (Android 13)

Both Gradle builds require a locally installed JDK 21; automatic toolchain download is disabled.

## Build and validation commands

Run app commands from the repository root:

```bash
./gradlew :app:assembleDebug
./gradlew :app:assembleRelease
./gradlew build --warning-mode all
```

The CI workflow in `.github/workflows/ci.yaml` regenerates the database on Linux x64, Linux ARM64, and Windows ARM64, then builds the app on Linux.

### Tests

Local JVM tests for `:app` are deliberately disabled by this block in `app/build.gradle.kts`:

```kotlin
tasks.withType<Test>().configureEach {
        enabled = false
}
```

The checked-in app test sources are only template smoke tests. Do not report `:app:testDebugUnitTest` as meaningful coverage while the disable block remains. Instrumented tests require a connected emulator or device:

```bash
./gradlew :app:connectedDebugAndroidTest
```

The preparing build has its own test task, although it currently has no substantive test cases:

```bash
cd preparing
./gradlew test
```

For IME behavior, an app build is only a compile/package check. Candidate selection, composing text, keyboard switching, physical-keyboard input, and input-memory behavior must be verified with the installed IME in a real client or emulator when the change affects those paths.

## Database generation

Before the first app build, and after changing generator code or files in `preparing/src/main/resources/`, regenerate the bundled database:

```bash
cd preparing
./gradlew run --warning-mode all
```

`preparing/` is a standalone Kotlin/JVM application using SQLite JDBC. `Main.kt` deletes and recreates exactly one output:

```text
app/src/main/assets/appdb.sqlite3
```

`KeyboardDataPreparer` builds the IME tables and indexes. `AppDataPreparer` adds dictionary and linguistic-reference data. The current generated database contains 25 application tables and 35 explicit indexes.

When changing database data or schema:

1. Change the authoritative source in `preparing/src/main/kotlin/` or `preparing/src/main/resources/`.
2. Regenerate `app/src/main/assets/appdb.sqlite3`; do not hand-edit the database asset.
3. Update all Android queries that consume renamed or reshaped tables.
4. Run the preparing test task and generation command.
5. Check the generated database with `PRAGMA integrity_check`, inspect its schema, and verify affected row content rather than relying only on row counts.
6. Rebuild the app.

At runtime, `utilities/DatabasePreparer.kt` copies the asset to a version-code-specific file in the app database directory. `Elephant` opens that copy through `InheritedDatabaseHelper` and treats it as query-only. Old `appdb-v*` runtime copies are removed after a new version is installed.

## Current database schema

IME and lookup tables:

- `lexicon_core`
- `structure_table`
- `pinyin_lexicon`
- `cangjie_table`
- `quick_table`
- `stroke_table`
- `symbol_table`
- `emoji_skin_map`
- `plain_text_table`
- `syllable_core_table`
- `syllable_9key_table`
- `syllable_pinyin_table`

Character-variant tables:

- `variant_sim`
- `variant_hk`
- `variant_tw`
- `variant_prc`
- `variant_abp`
- `variant_old`

Launcher-app reference tables:

- `collocation_table`
- `dictionary_table`
- `definition_table`
- `yingwaa_table`
- `chohok_table`
- `fanwan_table`
- `gwongwan_table`

The learned user lexicon is not part of `appdb.sqlite3`. `memory/InputMemoryHelper.kt` owns a separate writable SQLite database, including migration from legacy memory tables. Keep bundled-data migrations and user-memory migrations conceptually separate.

## Architecture

### Launcher app

- `MainActivity.kt` initializes the bundled database on an IO dispatcher and hosts the Compose app.
- `AppContent.kt`, `Screen.kt`, and `AppBottomBar.kt` define navigation.
- `app/home/` contains setup, dictionary search, introductions, language settings, and TTS screens.
- `app/romanization/` and `app/cantonese/` contain reference material.
- `app/about/` and `app/common/` contain the about screen and reusable launcher components.
- `utilities/SearchHelper.kt` queries dictionary and historical-reference tables.
- `speech/` wraps Android text-to-speech.

### Input method

- `LifecycleInputMethodService.kt` supplies lifecycle ownership to the IME service.
- `JyutpingInputMethodService.kt` is the central state holder and event handler. There is no separate IME `ViewModel`.
- `ComposeKeyboardView.kt` observes service flows and selects the current keyboard form. It renders soft-keyboard layouts, candidate views, settings, emoji, editing controls, and the compact physical-keyboard candidate bar.
- `keyboard/` contains the main QWERTY, Triple Stroke, Cangjie, numeric/symbolic, candidate, toolbar, settings, and shared key composables.
- `ninekey/`, `stroke/`, `numeric/`, `editingpanel/`, and `emoji/` contain their specialized layouts and controls.
- `models/` contains input events, segmentation, database research, candidates, ranking/conversion, Pinyin and nine-key variants, and character conversion.
- `memory/` contains the learned lexicon and its SQLite helper.
- `feedback/`, `linguistics/`, `extensions/`, `presets/`, and `shapes/` contain supporting behavior and shared types.

### Candidate pipeline

For the normal Cantonese path:

```text
key composable or hardware event
  -> JyutpingInputMethodService.process()/nineKeyProcess()
  -> buffered BasicInputEvent or Combo sequence
  -> Segmenter/NineKeySegmenter
  -> Researcher/NineKeyResearcher plus optional memory, plain-text, and symbol lookups
  -> Converter.dispatch()
  -> candidates StateFlow
  -> CandidateView/CandidateBoard/PhysicalKeyboardCandidateBar
  -> selectCandidate()
  -> InputConnection commit and optional InputMemoryHelper update
```

The service cancels the previous suggestion coroutine whenever the buffer changes. Preserve cancellation checks in potentially expensive query paths.

Special leading keys dispatch reverse lookup from the regular buffer:

- `r`: Mandarin Pinyin via `PinyinSegmenter` and `PinyinResearcher`
- `v`: Cangjie or Quick via `CangjieConverter` and `keyboard/Cangjie.kt`
- `x`: stroke lookup via `stroke/Stroke.kt`
- `q`: component/structure lookup via `models/Structure.kt`

`Converter` merges and orders sources, applies the selected `CharacterStandard`, and emits `Candidate` values. Traditional variants use the generated variant tables; PRC tailoring and simplified conversion additionally use `TailoredConverter` and `Simplifier`.

## Important change surfaces

### Keyboard UI or layout

Start from `ComposeKeyboardView.kt`, `models/KeyboardForm.kt`, `models/KeyboardLayout.kt`, and the relevant package. A new persistent option normally also requires a key in `UserSettingsKey.kt`, service state/update logic, settings UI, and localized resources.

Check phone and tablet, portrait and landscape, soft and physical keyboard branches where relevant. Keyboard UI is hosted by an `InputMethodService`, so do not assume behavior from a normal activity preview alone.

### Candidate or input behavior

Trace both the QWERTY path and the nine-key path. They use related but separate segmenters and researchers. Check composing text, cancellation, candidate ordering, selection offsets, partial candidate consumption, character conversion, and learned-memory updates.

### Launcher search and reference data

Launcher search is in `app/home/HomeScreen.kt` and `utilities/SearchHelper.kt`. Its tables are generated by `AppDataPreparer`; a data-format change can therefore require coordinated resource, generator, query, and UI changes.

### Localization and store metadata

- Android strings are in `app/src/main/res/values*/strings.xml`, using BCP-47 resource directories for English, Cantonese, and Chinese variants.
- `app/src/main/res/resources.properties` declares `en-US` as the unqualified resource locale.
- F-Droid/Fastlane metadata is in `metadata/{en-US,zh-CN,zh-HK,zh-TW}/`.
- Release changelog filenames under `metadata/*/changelogs/` use the Android `versionCode` from `app/build.gradle.kts`.

Keep string keys aligned across affected locales and preserve intentional regional wording. Do not treat store metadata as Android runtime resources.

## Code style and repository hygiene

- Read `.editorconfig` before editing. Kotlin and Kotlin script files use 8-space indentation; XML uses 4 spaces. Files use UTF-8, LF endings, final newlines, and trimmed trailing whitespace.
- Prefer the existing Compose, `StateFlow`, coroutine, and SQLite patterns in the nearest sibling code.
- Keep SQL parameterized when values come from text or external input. When numeric codes are interpolated, preserve the established encoded-integer invariants.
- Treat `app/src/main/assets/appdb.sqlite3` as generated output and `preparing/src/main/resources/` as source data.
- Keep changes scoped. Do not rewrite generated data, translations, metadata, or unrelated formatting unless the task requires it.

## Key paths

- Android source: `app/src/main/java/org/jyutping/jyutping/`
- Android resources: `app/src/main/res/`
- Bundled assets: `app/src/main/assets/`
- Unit-test placeholders: `app/src/test/`
- Instrumented-test placeholders: `app/src/androidTest/`
- Generator source: `preparing/src/main/kotlin/`
- Generator resources: `preparing/src/main/resources/`
- Store metadata: `metadata/`
- CI workflow: `.github/workflows/ci.yaml`
