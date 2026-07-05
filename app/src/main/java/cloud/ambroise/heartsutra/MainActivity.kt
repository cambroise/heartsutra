package cloud.ambroise.heartsutra

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cloud.ambroise.heartsutra.data.Segment
import cloud.ambroise.heartsutra.data.kanji.KanjiStrokeRepository
import cloud.ambroise.heartsutra.data.settings.SettingsRepository
import cloud.ambroise.heartsutra.data.settings.ThemeMode
import cloud.ambroise.heartsutra.data.srs.SrsRepository
import cloud.ambroise.heartsutra.ui.fulltext.FullTextScreen
import cloud.ambroise.heartsutra.ui.kanji.KanjiStrokeSheet
import cloud.ambroise.heartsutra.ui.review.ReviewScreen
import cloud.ambroise.heartsutra.ui.review.ReviewViewModel
import cloud.ambroise.heartsutra.ui.theme.HeartSutraTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            val appContext = LocalContext.current.applicationContext
            val settings = remember { SettingsRepository(appContext) }
            val themeMode by settings.themeMode.collectAsStateWithLifecycle(initialValue = ThemeMode.SYSTEM)
            val scope = rememberCoroutineScope()

            val dark = when (themeMode) {
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
            }
            HeartSutraTheme(darkTheme = dark) {
                HeartSutraApp(
                    themeMode = themeMode,
                    onSetTheme = { mode -> scope.launch { settings.setThemeMode(mode) } },
                )
            }
        }
    }
}

private object Routes {
    const val REVIEW = "review"
    const val FULL_TEXT = "fulltext"
}

@Composable
private fun HeartSutraApp(
    themeMode: ThemeMode,
    onSetTheme: (ThemeMode) -> Unit,
) {
    val appContext = LocalContext.current.applicationContext
    val repository = remember { SrsRepository(appContext) }
    val kanjiRepository = remember { KanjiStrokeRepository(appContext) }
    val viewModel: ReviewViewModel = viewModel(factory = ReviewViewModel.Factory(repository))
    val navController = rememberNavController()

    var strokeSegment by remember { mutableStateOf<Segment?>(null) }

    NavHost(navController = navController, startDestination = Routes.REVIEW) {
        composable(Routes.REVIEW) {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            ReviewScreen(
                uiState = uiState,
                actions = viewModel,
                themeMode = themeMode,
                onSetTheme = onSetTheme,
                onOpenFullText = { navController.navigate(Routes.FULL_TEXT) },
                onShowStrokes = { strokeSegment = it },
            )
        }
        composable(Routes.FULL_TEXT) {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            FullTextScreen(
                states = uiState.states,
                onSegmentClick = { index ->
                    viewModel.onReviewSingle(index)
                    navController.popBackStack()
                },
                onShowStrokes = { strokeSegment = it },
                onBack = { navController.popBackStack() },
            )
        }
    }

    strokeSegment?.let { segment ->
        KanjiStrokeSheet(
            segment = segment,
            repository = kanjiRepository,
            onDismiss = { strokeSegment = null },
        )
    }
}
