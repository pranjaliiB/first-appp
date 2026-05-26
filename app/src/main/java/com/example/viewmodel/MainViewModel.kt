package com.example.viewmodel

import android.app.Application
import android.media.AudioAttributes
import android.media.SoundPool
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.SettingsManager
import com.example.data.model.JournalEntry
import com.example.data.model.PerformanceDay
import com.example.data.model.Quote
import com.example.data.model.Task
import com.example.data.model.TimelineGoal
import com.example.data.repository.RiseAgainRepository
import com.example.ui.theme.EmotionalThemeState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = RiseAgainRepository(db)
    private val settingsManager = SettingsManager(application)

    // Flow Streams from Database
    val allTasks: StateFlow<List<Task>> = repository.allTasks.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allGoals: StateFlow<List<TimelineGoal>> = repository.allGoals.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allQuotes: StateFlow<List<Quote>> = repository.allQuotes.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allPerformance: StateFlow<List<PerformanceDay>> = repository.allPerformance.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val recentWeeklyPerformance: StateFlow<List<PerformanceDay>> = repository.recentWeeklyPerformance.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allJournalEntries: StateFlow<List<JournalEntry>> = repository.allJournalEntries.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Flow Streams from Settings DataStore
    val notificationsEnabled: StateFlow<Boolean> = settingsManager.notificationsEnabled.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = true
    )

    val darkThemeEnabled: StateFlow<Boolean> = settingsManager.darkThemeEnabled.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = true
    )

    val consecutiveLowDays: StateFlow<Int> = settingsManager.consecutiveLowDays.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    // Live Selected Quotes for today
    private val _motivationalQuote = MutableStateFlow<Quote?>(null)
    val motivationalQuote = _motivationalQuote.asStateFlow()

    private val _realityQuote = MutableStateFlow<Quote?>(null)
    val realityQuote = _realityQuote.asStateFlow()

    // Focus Mode Timer States
    private val _focusTimeRemaining = MutableStateFlow(25 * 60) // Default 25 min in seconds
    val focusTimeRemaining = _focusTimeRemaining.asStateFlow()

    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning = _isTimerRunning.asStateFlow()

    private val _focusCategory = MutableStateFlow("FOCUSED") // "FOCUSED", "MEDITATIVE", "BREATHING"
    val focusCategory = _focusCategory.asStateFlow()

    private var timerJob: Job? = null
    private var soundPool: SoundPool? = null
    private var playSoundId = 0

    // Dynamic UI Emotional State
    // Determined based on live productivity or consecutive settings value
    val emotionalThemeState: StateFlow<EmotionalThemeState> = combine(
        allTasks,
        consecutiveLowDays
    ) { tasks, consecutiveLows ->
        val todayStartMs = getStartOfDayMs()
        val todayEndMs = getEndOfDayMs()
        
        val todayTasks = tasks.filter {
            it.deadline in todayStartMs..todayEndMs || (it.isCompleted && it.completedAt ?: 0 >= todayStartMs)
        }
        val total = todayTasks.size
        val completed = todayTasks.count { it.isCompleted }

        // If consecutive low days are 3 or more, or live progress with enough tasks is severely behind
        if (consecutiveLows >= 3 || (total >= 2 && (completed.toFloat() / total) < 0.35f)) {
            EmotionalThemeState.SHARP_WARN
        } else {
            EmotionalThemeState.PEACEFUL
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = EmotionalThemeState.PEACEFUL
    )

    init {
        viewModelScope.launch {
            repository.ensureQuotesPopulated()
            refreshDailyQuotes()
        }
        initSynthAudio()
    }

    private fun initSynthAudio() {
        soundPool = SoundPool.Builder()
            .setMaxStreams(2)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .build()
        // Sound Pool would reference resource sound tracks if available. We'll use synth tones where possible in Compose/Activity.
    }

    fun playAlertSynthSound() {
        // SoundPool / Ringtone alert trigger
    }

    // Refresh Daily Quotes using deterministic hashing on current day of the year
    fun refreshDailyQuotes() {
        viewModelScope.launch {
            val allMotivations = repository.getQuotesByCategory("MOTIVATION")
            val allRealities = repository.getQuotesByCategory("REALITY")

            val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)

            if (allMotivations.isNotEmpty()) {
                val index = dayOfYear % allMotivations.size
                _motivationalQuote.value = allMotivations[index]
            }
            if (allRealities.isNotEmpty()) {
                val index = dayOfYear % allRealities.size
                _realityQuote.value = allRealities[index]
            }
        }
    }

    // TASK MANAGEMENT
    fun addTask(title: String, description: String, deadline: Long, priority: String) {
        viewModelScope.launch {
            repository.insertTask(
                Task(
                    title = title,
                    description = description,
                    deadline = deadline,
                    priority = priority
                )
            )
        }
    }

    fun editTask(id: Int, title: String, description: String, deadline: Long, priority: String, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.updateTask(
                Task(
                    id = id,
                    title = title,
                    description = description,
                    deadline = deadline,
                    priority = priority,
                    isCompleted = isCompleted,
                    completedAt = if (isCompleted) System.currentTimeMillis() else null
                )
            )
        }
    }

    fun deleteTask(id: Int) {
        viewModelScope.launch {
            repository.deleteTaskById(id)
        }
    }

    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            val nextStatus = !task.isCompleted
            repository.updateTask(
                task.copy(
                    isCompleted = nextStatus,
                    completedAt = if (nextStatus) System.currentTimeMillis() else null
                )
            )
        }
    }

    // TIMELINE GOALS
    fun addGoal(title: String, type: String, deadline: Long, hasReminder: Boolean) {
        viewModelScope.launch {
            repository.insertGoal(
                TimelineGoal(
                    title = title,
                    type = type,
                    deadline = deadline,
                    hasReminder = hasReminder,
                    reminderTime = if (hasReminder) deadline - (30 * 60 * 1000) else null // 30 min before
                )
            )
        }
    }

    fun toggleGoalCompletion(goal: TimelineGoal) {
        viewModelScope.launch {
            repository.updateGoal(
                goal.copy(isCompleted = !goal.isCompleted)
            )
        }
    }

    fun deleteGoal(id: Int) {
        viewModelScope.launch {
            repository.deleteGoalById(id)
        }
    }

    // MOODS AND JOURNAL
    fun addJournalEntry(mood: String, journalText: String) {
        viewModelScope.launch {
            val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            repository.insertJournalEntry(
                JournalEntry(
                    date = todayStr,
                    mood = mood,
                    journalText = journalText
                )
            )
        }
    }

    fun deleteJournalEntry(id: Int) {
        viewModelScope.launch {
            repository.deleteJournalEntryById(id)
        }
    }

    // SETTINGS PREFERENCES
    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsManager.setNotificationsEnabled(enabled)
        }
    }

    fun setDarkThemeEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsManager.setDarkThemeEnabled(enabled)
        }
    }

    fun setConsecutiveLowDays(count: Int) {
        viewModelScope.launch {
            if (count == 0) {
                settingsManager.resetConsecutiveLowDays()
            } else {
                for (i in 1..count) {
                    settingsManager.incrementConsecutiveLowDays()
                }
            }
        }
    }

    // FOCUS TIMER LOGIC
    fun startFocusTimer() {
        if (_isTimerRunning.value) return
        _isTimerRunning.value = true
        timerJob = viewModelScope.launch {
            while (_focusTimeRemaining.value > 0 && _isTimerRunning.value) {
                delay(1000)
                _focusTimeRemaining.value -= 1
            }
            if (_focusTimeRemaining.value == 0) {
                _isTimerRunning.value = false
                playAlertSynthSound()
                addJournalEntry("FOCUSED", "Completed a ${_focusCategory.value.lowercase()} focus block successfully.")
            }
        }
    }

    fun pauseFocusTimer() {
        _isTimerRunning.value = false
        timerJob?.cancel()
    }

    fun resetFocusTimer(minutes: Int = 25) {
        pauseFocusTimer()
        _focusTimeRemaining.value = minutes * 60
    }

    fun setFocusCategory(category: String) {
        _focusCategory.value = category
    }

    override fun onCleared() {
        super.onCleared()
        soundPool?.release()
    }

    private fun getStartOfDayMs(): Long {
        val calendar = java.util.Calendar.getInstance()
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun getEndOfDayMs(): Long {
        val calendar = java.util.Calendar.getInstance()
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 23)
        calendar.set(java.util.Calendar.MINUTE, 59)
        calendar.set(java.util.Calendar.SECOND, 59)
        calendar.set(java.util.Calendar.MILLISECOND, 999)
        return calendar.timeInMillis
    }
}
