<div align="center">

<img src="docs/icon.png" width="120" alt="Heart Sutra icon" />

# Heart Sutra · 般若心経

An Android app to **learn the Heart Sutra (Hannya Shingyō) by heart**, using
spaced-repetition flashcards, and to **learn how to trace each kanji**.

</div>

---

## Idea

The text is split into ~41 segments. Each card shows **one central segment** in
its three parallel forms — original kanji, romanised reading, English translation —
framed by the **two segments before and after** (dimmed) so the passage is placed
within the recitation. A **spaced-repetition (SRS)** scheduler decides what to
review and you grade your recall. The **full text** stays available at any time,
and an **animated kanji trace** (stroke order and direction) helps you write them
by hand.

The UI is in English by default and switches to French on French-locale devices;
the sutra itself is always trilingual.

<div align="center">
<img src="docs/card_light.png" width="300" alt="Card (light)" />&nbsp;&nbsp;
<img src="docs/card_dark.png" width="300" alt="Card (dark)" />
<br/><em>Design mockups — light and dark themes.</em>
</div>

## Features

- 🎴 **Flashcards**: central segment + 2 context segments above and below.
- 🧠 **Spaced repetition** (lightweight SM-2) with four grades: *Again / Hard / Good / Easy*.
- 📖 **Full text** browsing, with a marker for cards due for review.
- ✍️ **Animated kanji stroke order**, from the [KanjiVG](https://kanjivg.tagaini.net) project.
- 🎨 "Sumi ink on washi paper, vermilion seal" design, with a **System / Light / Dark** theme selector.
- 📴 Fully **offline** — no network access, in keeping with a meditation practice.

## Tech stack

Kotlin · Jetpack Compose (Material 3) · Navigation-Compose · DataStore · kotlinx.serialization
A single `:app` module, MVVM. compileSdk 34, minSdk 26.

## Build and install

```bash
# Requires the Android SDK; set local.properties -> sdk.dir=<SDK path>
./gradlew installDebug     # build and install on a connected device (USB debugging)
./gradlew assembleDebug    # produces app/build/outputs/apk/debug/app-debug.apk
./gradlew test             # unit tests (SRS engine)
```

## Regenerate the stroke data (KanjiVG)

```bash
python3 tools/build_kanjivg.py   # downloads only the ~118 kanji used in the sutra
```

<div align="center">
<img src="docs/kanji_shin.png" width="120" alt="Stroke order of the kanji 心" />
</div>

## Credits and licences

- **Application code**: © 2026 Christophe Ambroise, under the
  **[GNU General Public License v3.0](LICENSE)** (GPL-3.0-or-later).
- **Kanji strokes**: data derived from the **[KanjiVG](https://kanjivg.tagaini.net)**
  project by Ulrich Apel, under the **[Creative Commons BY-SA 3.0](https://creativecommons.org/licenses/by-sa/3.0/)**
  licence. `app/src/main/assets/kanjivg/strokes.json` is a derivative work and is
  distributed under CC BY-SA 3.0 (see `app/src/main/assets/kanjivg/NOTICE.txt`).
  This licence applies to the data, not to the application code.
- **Sutra text**: the Heart Sutra is in the public domain.
- **Icon**: a hand-made 心 hanko seal by the author.
