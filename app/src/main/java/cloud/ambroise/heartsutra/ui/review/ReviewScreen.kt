package cloud.ambroise.heartsutra.ui.review

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Brightness6
import androidx.compose.material.icons.outlined.Gesture
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cloud.ambroise.heartsutra.data.Segment
import cloud.ambroise.heartsutra.data.SutraData
import cloud.ambroise.heartsutra.data.settings.ThemeMode
import cloud.ambroise.heartsutra.data.srs.Grade

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(
    uiState: ReviewUiState,
    actions: ReviewActions,
    themeMode: ThemeMode,
    onSetTheme: (ThemeMode) -> Unit,
    onOpenFullText: () -> Unit,
    onShowStrokes: (Segment) -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("般若心経", style = MaterialTheme.typography.titleLarge)
                        Text("Sūtra du Cœur", style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                actions = {
                    ThemeMenu(current = themeMode, onSelect = onSetTheme)
                    IconButton(onClick = onOpenFullText) {
                        Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = "Texte complet")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            when {
                uiState.loading -> LoadingState()
                uiState.sessionComplete -> SessionCompleteState(uiState, actions)
                uiState.currentIndex != null -> CardState(uiState, actions, onShowStrokes)
            }
        }
    }
}

@Composable
private fun ThemeMenu(current: ThemeMode, onSelect: (ThemeMode) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val label = { mode: ThemeMode ->
        when (mode) {
            ThemeMode.SYSTEM -> "Système"
            ThemeMode.LIGHT -> "Clair"
            ThemeMode.DARK -> "Sombre"
        }
    }
    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(Icons.Outlined.Brightness6, contentDescription = "Thème")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            ThemeMode.entries.forEach { mode ->
                DropdownMenuItem(
                    text = { Text(label(mode)) },
                    onClick = {
                        onSelect(mode)
                        expanded = false
                    },
                    trailingIcon = {
                        if (mode == current) {
                            Icon(Icons.Filled.Check, contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary)
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun LoadingState() {
    Spacer(Modifier.height(120.dp))
    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
}

@Composable
private fun ColumnScope.CardState(
    uiState: ReviewUiState,
    actions: ReviewActions,
    onShowStrokes: (Segment) -> Unit,
) {
    val centerIndex = uiState.currentIndex ?: return
    val scroll = rememberScrollState()

    Text(
        text = uiState.progressLabel,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 4.dp, bottom = 8.dp),
    )

    Column(
        modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
            .verticalScroll(scroll),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        NeighbourList(indices = (centerIndex - 2 until centerIndex))
        CenterCard(
            segment = SutraData.segments[centerIndex],
            revealed = uiState.revealed,
            onShowStrokes = onShowStrokes,
        )
        NeighbourList(indices = (centerIndex + 1..centerIndex + 2))
    }

    GradeControls(revealed = uiState.revealed, actions = actions)
}

@Composable
private fun NeighbourList(indices: IntRange) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        indices.filter { it in SutraData.segments.indices }.forEach { i ->
            Text(
                text = SutraData.segments[i].original,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .alpha(0.45f)
                    .padding(vertical = 6.dp),
            )
        }
    }
}

@Composable
private fun CenterCard(segment: Segment, revealed: Boolean, onShowStrokes: (Segment) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 560.dp)
            .padding(vertical = 16.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            SealBadge()
            Spacer(Modifier.height(20.dp))
            Text(
                text = segment.original,
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .clickable { onShowStrokes(segment) }
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            )
            Spacer(Modifier.height(8.dp))
            TextButton(onClick = { onShowStrokes(segment) }) {
                Icon(
                    imageVector = Icons.Outlined.Gesture,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(Modifier.size(6.dp))
                Text("Tracé des kanji", style = MaterialTheme.typography.labelMedium)
            }
            AnimatedVisibility(
                visible = revealed,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(Modifier.height(20.dp))
                    HorizontalDivider(
                        modifier = Modifier.widthIn(max = 80.dp),
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(Modifier.height(20.dp))
                    Text(
                        text = segment.reading,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = segment.english,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                }
            }
            if (!revealed) {
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Récitez de mémoire, puis affichez",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun SealBadge() {
    // A small vermilion hanko carrying 心, echoing the app icon.
    val seal = MaterialTheme.colorScheme.primary
    androidx.compose.foundation.layout.Box(
        modifier = Modifier.size(44.dp),
        contentAlignment = Alignment.Center,
    ) {
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
            drawRoundRect(
                color = seal,
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(8.dp.toPx()),
            )
        }
        Text(
            text = "心",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimary,
        )
    }
}

@Composable
private fun GradeControls(revealed: Boolean, actions: ReviewActions) {
    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        if (!revealed) {
            FilledTonalButton(
                onClick = actions::onReveal,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            ) {
                Text("Afficher la réponse")
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                GradeButton("Encore", MaterialTheme.colorScheme.errorContainer,
                    MaterialTheme.colorScheme.onErrorContainer, Modifier.weight(1f)) { actions.onGrade(Grade.AGAIN) }
                GradeButton("Difficile", MaterialTheme.colorScheme.surfaceContainerHigh,
                    MaterialTheme.colorScheme.onSurface, Modifier.weight(1f)) { actions.onGrade(Grade.HARD) }
                GradeButton("Correct", MaterialTheme.colorScheme.secondaryContainer,
                    MaterialTheme.colorScheme.onSecondaryContainer, Modifier.weight(1f)) { actions.onGrade(Grade.GOOD) }
                GradeButton("Facile", MaterialTheme.colorScheme.primary,
                    MaterialTheme.colorScheme.onPrimary, Modifier.weight(1f)) { actions.onGrade(Grade.EASY) }
            }
        }
    }
}

@Composable
private fun GradeButton(
    label: String,
    container: Color,
    content: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    FilledTonalButton(
        onClick = onClick,
        modifier = modifier,
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp, vertical = 12.dp),
        colors = ButtonDefaults.filledTonalButtonColors(containerColor = container, contentColor = content),
    ) {
        Text(label, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
private fun SessionCompleteState(uiState: ReviewUiState, actions: ReviewActions) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text("🙏", style = MaterialTheme.typography.displayMedium)
        Spacer(Modifier.height(16.dp))
        Text(
            text = if (uiState.reviewedThisSession > 0) "Session terminée" else "Rien à réviser pour l'instant",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "${uiState.reviewedThisSession} fiche(s) revue(s) · ${uiState.dueCount} à échéance",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(28.dp))
        if (uiState.dueCount > 0) {
            FilledTonalButton(
                onClick = { actions.onStartSession(SessionKind.DUE) },
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            ) { Text("Continuer les fiches dues") }
            Spacer(Modifier.height(12.dp))
        }
        FilledTonalButton(onClick = { actions.onStartSession(SessionKind.ALL) }) {
            Text("Réviser tout le sūtra")
        }
    }
}
