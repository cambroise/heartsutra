# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

An Android app to memorise the **Heart Sutra** (般若心経) by heart. The whole
sutra is ~41 short segments; each is studied as a flashcard showing the segment
in three parallel forms — original kanji, romanised reading, English — with the
two neighbouring segments dimmed above and below for positional context. A
spaced-repetition (SRS) scheduler decides what to review; the full text is
always browsable.

UI chrome is in **French**; the sutra content stays trilingual.

## Build & run

```bash
./gradlew :app:assembleDebug          # build debug APK
./gradlew :app:installDebug           # install on a connected device/emulator
./gradlew :app:compileDebugKotlin     # fast compile-only check
./gradlew test                        # JVM unit tests
./gradlew :app:testDebugUnitTest --tests "*CardSrs*"   # single test class
```

`local.properties` must contain `sdk.dir=<Android SDK path>` (untracked). There
is no emulator/AVD configured in this environment — build with `assembleDebug`
and run on a real device, or spin up an AVD separately.

Stack (all cached, mutually compatible — do not bump casually): Gradle 8.13,
AGP 8.13.2, Kotlin 2.0.20, **compileSdk 34** (only 34 and 36 are installed
locally — avoid 35), minSdk 26, Compose BOM 2024.09.03. Versions live in
`gradle/libs.versions.toml`.

## Architecture

Single `:app` module, package `cloud.ambroise.heartsutra`. MVVM + Compose,
Navigation-Compose, no DI framework (the one ViewModel is built with a manual
`ViewModelProvider.Factory`).

- **`data/`** — content is *static code*, not a database. `SutraData` is the
  ordered `List<Segment>` and the single source of truth; `Segment.index` is
  both the recitation order and the SRS key. `SutraData.windowAround(i)` returns
  the card window (center ± 2, clamped at the ends).
- **`data/srs/`** — `CardSrs` holds per-card SM-2-lite state and computes its own
  next state in `grade()`. `SrsRepository` persists the whole
  `Map<Int, CardSrs>` as one JSON blob in a DataStore Preferences key (~40 rows
  — deliberately not Room). A never-reviewed card has `dueEpochDay == 0`, which
  is always in the past, so new cards count as due. Dates are `epochDay` longs
  (`LocalDate.now().toEpochDay()`).
- **`ui/review/`** — `ReviewViewModel` owns the session: it builds a queue (due
  cards, or all, or a single card picked from the full text), tracks position +
  reveal state, and grades. `ReviewUiState` follows the four-bucket model
  (persisted `states`, transient session fields, derived getters like
  `currentCard`/`dueCount`/`sessionComplete`). The VM implements a `ReviewActions`
  interface so the content composable never sees the VM.
- **`ui/fulltext/`** — the whole sutra as a tappable list; a due-dot marks cards
  due today; tapping a segment starts a single-card review via
  `onReviewSingle(index)`.
- **`data/kanji/` + `ui/kanji/`** — kanji stroke-order tracing. `assets/kanjivg/strokes.json`
  maps each character to its ordered KanjiVG stroke paths (SVG `d` strings, 109×109
  viewBox). `KanjiStrokeRepository` loads/caches it; `KanjiStrokeAnimation` draws
  the trace on a `Canvas` — every stroke shown faint, then filled in order,
  revealing the active stroke via `PathEffect.dashPathEffect([len,len], len*(1-fraction))`
  (chosen over `PathMeasure.getSegment`, which has render quirks). `KanjiStrokeSheet`
  is a `ModalBottomSheet` opened from the review card and full-text rows (brush icon).
  Regenerate the data with `python3 tools/build_kanjivg.py` (downloads only the
  sutra's ~118 unique kanji). 揭 (U+63ED) is absent from KanjiVG → shown as
  "tracé indisponible", never substituted. The data is CC BY-SA 3.0
  (`assets/kanjivg/NOTICE.txt`); attribution is shown in the sheet — keep it.
- **`ui/theme/`** — custom M3 `ColorScheme` (no dynamic color): "sumi ink on warm
  washi paper, vermilion hanko seal as the accent", light + dark. Kanji is
  carried by the `display*` typography roles (serif); readings/English use
  `body*`. Keep colours as `colorScheme` roles, not hardcoded hex.

The single `ReviewViewModel` is hosted at the `NavHost` level so both the review
and full-text destinations share one SRS state stream.

## Conventions

- The app icon is a red hanko seal with 心 knocked out (paper shows through), so
  it doubles as the M3 `monochrome` layer. It is generated, not hand-drawn:
  `tools/IconGen.java` (`java tools/IconGen.java`) renders
  `mipmap-*/ic_launcher_foreground.png` from a system CJK font. Re-run it to
  change the seal; do not edit the PNGs by hand.
- **The kanji is sacred text — never silently "correct" or paraphrase it.**
  Segment 2 was restored to its canonical form 観自在菩薩行深般若波羅蜜多時
  (the source dropped 若); every other segment reproduces the supplied source
  verbatim. `無限界` in segment 16 is likely a typo for the canonical `無眼界`
  but was left as supplied — confirm with the user before changing content.
