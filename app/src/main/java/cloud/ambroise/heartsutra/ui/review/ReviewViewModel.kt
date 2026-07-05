package cloud.ambroise.heartsutra.ui.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import cloud.ambroise.heartsutra.data.Segment
import cloud.ambroise.heartsutra.data.SutraData
import cloud.ambroise.heartsutra.data.srs.CardSrs
import cloud.ambroise.heartsutra.data.srs.Grade
import cloud.ambroise.heartsutra.data.srs.SrsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

/** How the current session's queue was assembled. */
enum class SessionKind { DUE, ALL, SINGLE }

data class ReviewUiState(
    // Persisted snapshot — SRS state for every card, keyed by segment index.
    val states: Map<Int, CardSrs> = emptyMap(),
    // Transient session state.
    val queue: List<Int> = emptyList(),
    val position: Int = 0,
    val revealed: Boolean = false,
    val kind: SessionKind = SessionKind.DUE,
    val reviewedThisSession: Int = 0,
    val loading: Boolean = true,
) {
    val sessionComplete: Boolean get() = !loading && position >= queue.size
    val currentIndex: Int? get() = queue.getOrNull(position)
    val currentCard: List<Segment>? get() = currentIndex?.let { SutraData.windowAround(it) }
    val dueCount: Int
        get() = SutraData.segments.count { (states[it.index] ?: CardSrs()).isDue(LocalDate.now().toEpochDay()) }
    val progressLabel: String get() = "${(position + 1).coerceAtMost(queue.size)} / ${queue.size}"
}

interface ReviewActions {
    fun onReveal()
    fun onGrade(grade: Grade)
    fun onStartSession(kind: SessionKind)
    fun onReviewSingle(index: Int)
}

class ReviewViewModel(private val repository: SrsRepository) : ViewModel(), ReviewActions {

    private val _uiState = MutableStateFlow(ReviewUiState())
    val uiState: StateFlow<ReviewUiState> = _uiState.asStateFlow()

    private val today: Long get() = LocalDate.now().toEpochDay()

    init {
        viewModelScope.launch {
            var initialised = false
            repository.states.collect { states ->
                _uiState.update { it.copy(states = states, loading = false) }
                if (!initialised) {
                    initialised = true
                    onStartSession(SessionKind.DUE)
                }
            }
        }
    }

    override fun onStartSession(kind: SessionKind) {
        val states = _uiState.value.states
        val indices = when (kind) {
            SessionKind.ALL -> SutraData.segments.map { it.index }
            SessionKind.DUE, SessionKind.SINGLE -> SutraData.segments
                .map { it.index }
                .filter { (states[it] ?: CardSrs()).isDue(today) }
                .sortedBy { states[it]?.dueEpochDay ?: 0L }
        }
        _uiState.update {
            it.copy(queue = indices, position = 0, revealed = false, kind = kind, reviewedThisSession = 0)
        }
    }

    override fun onReviewSingle(index: Int) {
        _uiState.update {
            it.copy(
                queue = listOf(index),
                position = 0,
                revealed = false,
                kind = SessionKind.SINGLE,
                reviewedThisSession = 0,
            )
        }
    }

    override fun onReveal() {
        _uiState.update { it.copy(revealed = true) }
    }

    override fun onGrade(grade: Grade) {
        val index = _uiState.value.currentIndex ?: return
        viewModelScope.launch { repository.grade(index, grade, today) }
        _uiState.update {
            it.copy(
                position = it.position + 1,
                revealed = false,
                reviewedThisSession = it.reviewedThisSession + 1,
            )
        }
    }

    class Factory(private val repository: SrsRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T =
            ReviewViewModel(repository) as T
    }
}
