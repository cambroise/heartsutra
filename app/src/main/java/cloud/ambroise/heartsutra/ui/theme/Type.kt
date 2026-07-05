package cloud.ambroise.heartsutra.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// The kanji is carried by the display* roles. We intentionally keep the default
// family: Android has no bundled *serif* CJK, so FontFamily.Serif silently falls
// back to Noto Sans CJK for kanji anyway. For a calligraphic look, bundle a
// subsetted CJK serif (e.g. Noto Serif CJK JP) in res/font and set it here.
private val kanji = FontFamily.Default

val AppTypography = Typography(
    displayLarge = TextStyle(fontFamily = kanji, fontWeight = FontWeight.Medium, fontSize = 40.sp, lineHeight = 52.sp),
    displayMedium = TextStyle(fontFamily = kanji, fontWeight = FontWeight.Medium, fontSize = 32.sp, lineHeight = 44.sp),
    displaySmall = TextStyle(fontFamily = kanji, fontWeight = FontWeight.Medium, fontSize = 24.sp, lineHeight = 34.sp),
    titleLarge = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 20.sp, lineHeight = 28.sp),
    titleMedium = TextStyle(fontWeight = FontWeight.Medium, fontSize = 16.sp, lineHeight = 24.sp),
    bodyLarge = TextStyle(fontWeight = FontWeight.Normal, fontSize = 18.sp, lineHeight = 26.sp, letterSpacing = 0.5.sp),
    bodyMedium = TextStyle(fontWeight = FontWeight.Normal, fontSize = 15.sp, lineHeight = 22.sp),
    labelLarge = TextStyle(fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.4.sp),
    labelMedium = TextStyle(fontWeight = FontWeight.Medium, fontSize = 12.sp, lineHeight = 16.sp),
)
