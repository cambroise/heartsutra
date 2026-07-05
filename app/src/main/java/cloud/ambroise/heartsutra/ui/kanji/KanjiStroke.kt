package cloud.ambroise.heartsutra.ui.kanji

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.vector.PathParser
import kotlin.math.floor

// KanjiVG paths live in a 109×109 viewBox and are ordered by stroke.
private const val VIEW_BOX = 109f
private const val STROKE_WIDTH = 5f
private const val MS_PER_STROKE = 620

private class ParsedStroke(val path: Path, val length: Float)

@Composable
private fun rememberStrokes(dList: List<String>): List<ParsedStroke> = remember(dList) {
    dList.map { d ->
        val path = PathParser().parsePathString(d).toPath()
        val length = PathMeasure().apply { setPath(path, false) }.length
        ParsedStroke(path, length)
    }
}

/**
 * Draws [dList] (a kanji's ordered KanjiVG strokes) as an animated trace: every
 * stroke is shown faintly as a guide, then filled in order — the stroke being
 * drawn is highlighted in [inkColor], finished strokes settle to [doneColor].
 * Bumping [replayKey] restarts the animation.
 */
@Composable
fun KanjiStrokeAnimation(
    dList: List<String>,
    inkColor: Color,
    doneColor: Color,
    guideColor: Color,
    modifier: Modifier = Modifier,
    replayKey: Int = 0,
) {
    val strokes = rememberStrokes(dList)
    val progress = remember { Animatable(0f) }

    LaunchedEffect(dList, replayKey) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = strokes.size.toFloat(),
            animationSpec = tween(durationMillis = strokes.size * MS_PER_STROKE, easing = LinearEasing),
        )
    }

    Canvas(modifier) {
        val scale = size.minDimension / VIEW_BOX
        withTransform({ scale(scale, scale, pivot = Offset.Zero) }) {
            val base = Stroke(width = STROKE_WIDTH, cap = StrokeCap.Round, join = StrokeJoin.Round)
            strokes.forEach { drawPath(it.path, guideColor, style = base) }

            val prog = progress.value
            val currentIndex = floor(prog).toInt()
            strokes.forEachIndexed { i, stroke ->
                when {
                    i < currentIndex -> drawPath(stroke.path, doneColor, style = base)
                    i == currentIndex -> {
                        val fraction = (prog - i).coerceIn(0f, 1f)
                        if (fraction > 0f && stroke.length > 0f) {
                            val len = stroke.length
                            drawPath(
                                path = stroke.path,
                                color = inkColor,
                                style = Stroke(
                                    width = STROKE_WIDTH,
                                    cap = StrokeCap.Round,
                                    join = StrokeJoin.Round,
                                    // Reveal from the stroke's start by shifting a [len,len] dash.
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(len, len), len * (1f - fraction)),
                                ),
                            )
                        }
                    }
                }
            }
        }
    }
}
