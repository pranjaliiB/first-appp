package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.model.JournalEntry
import com.example.data.model.PerformanceDay
import com.example.data.model.Quote
import com.example.data.model.Task
import com.example.data.model.TimelineGoal
import kotlinx.coroutines.flow.Flow

class RiseAgainRepository(private val db: AppDatabase) {

    val allTasks: Flow<List<Task>> = db.taskDao().getAllTasks()
    val allGoals: Flow<List<TimelineGoal>> = db.timelineGoalDao().getAllGoals()
    val allQuotes: Flow<List<Quote>> = db.quoteDao().getAllQuotes()
    val allPerformance: Flow<List<PerformanceDay>> = db.performanceDao().getAllPerformanceDays()
    val recentWeeklyPerformance: Flow<List<PerformanceDay>> = db.performanceDao().getRecentWeeklyPerformance()
    val allJournalEntries: Flow<List<JournalEntry>> = db.journalDao().getAllJournalEntries()

    suspend fun insertTask(task: Task) = db.taskDao().insertTask(task)
    suspend fun updateTask(task: Task) = db.taskDao().updateTask(task)
    suspend fun deleteTask(task: Task) = db.taskDao().deleteTask(task)
    suspend fun deleteTaskById(id: Int) = db.taskDao().deleteTaskById(id)
    suspend fun getTaskById(id: Int) = db.taskDao().getTaskById(id)

    suspend fun insertGoal(goal: TimelineGoal) = db.timelineGoalDao().insertGoal(goal)
    suspend fun updateGoal(goal: TimelineGoal) = db.timelineGoalDao().updateGoal(goal)
    suspend fun deleteGoal(goal: TimelineGoal) = db.timelineGoalDao().deleteGoal(goal)
    suspend fun deleteGoalById(id: Int) = db.timelineGoalDao().deleteGoalById(id)

    suspend fun getQuotesByCategory(category: String): List<Quote> = db.quoteDao().getQuotesByCategory(category)
    suspend fun insertQuotes(quotes: List<Quote>) = db.quoteDao().insertQuotes(quotes)

    suspend fun insertPerformanceDay(day: PerformanceDay) = db.performanceDao().insertPerformanceDay(day)
    suspend fun getPerformanceDay(date: String): PerformanceDay? = db.performanceDao().getPerformanceDay(date)

    suspend fun insertJournalEntry(entry: JournalEntry) = db.journalDao().insertJournalEntry(entry)
    suspend fun deleteJournalEntryById(id: Int) = db.journalDao().deleteJournalEntryById(id)

    suspend fun ensureQuotesPopulated() {
        val count = db.quoteDao().getCount()
        if (count == 0) {
            val defaultQuotes = listOf(
                // Motivating Quotes
                Quote(text = "Discipline today. Freedom tomorrow.", category = "MOTIVATION", author = "Rise Again"),
                Quote(text = "Rise again. Your potential is endless.", category = "MOTIVATION", author = "Rise Again"),
                Quote(text = "Consistency is what transforms average into extraordinary.", category = "MOTIVATION", author = "Rise Again"),
                Quote(text = "You did not wake up today to be mediocre.", category = "MOTIVATION", author = "Rise Again"),
                Quote(text = "The pain of discipline is nothing compared to the pain of regret.", category = "MOTIVATION", author = "Marcus Aurelius"),
                Quote(text = "Quietly build your future. Let results make the noise.", category = "MOTIVATION", author = "Rise Again"),
                Quote(text = "Your current situation is only temporary if you keep marching.", category = "MOTIVATION", author = "Rise Again"),
                Quote(text = "Small wins build powerful futures. Stay consistent.", category = "MOTIVATION", author = "Rise Again"),
                Quote(text = "Every day is an opportunity to outwork your yesterday.", category = "MOTIVATION", author = "Rise Again"),
                Quote(text = "Success is not given. It is earned through sleepless discipline.", category = "MOTIVATION", author = "Rise Again"),
                
                // Harsh Reality Quotes
                Quote(text = "If you stop working today, someone else will replace you tomorrow.", category = "REALITY", author = "Harsh Reality"),
                Quote(text = "Your competitors are working while you make excuses.", category = "REALITY", author = "Harsh Reality"),
                Quote(text = "Regret is the worst pain. You wasted time, and it's gone forever.", category = "REALITY", author = "Harsh Reality"),
                Quote(text = "Nobody is coming to save you. Get up and save yourself.", category = "REALITY", author = "Harsh Reality"),
                Quote(text = "You are not too tired. You are just undisciplined.", category = "REALITY", author = "Harsh Reality"),
                Quote(text = "Comfort is the graveyard of your potential.", category = "REALITY", author = "Harsh Reality"),
                Quote(text = "Every time you skip a task, you increase the gap between you and your dream.", category = "REALITY", author = "Harsh Reality"),
                Quote(text = "Dreams without action become regret.", category = "REALITY", author = "Harsh Reality"),
                Quote(text = "Your competition did not stop today.", category = "REALITY", author = "Harsh Reality"),
                Quote(text = "One week of excuses can destroy months of progress.", category = "REALITY", author = "Harsh Reality"),
                Quote(text = "If you stay inconsistent, someone more focused will take every opportunity you wanted.", category = "REALITY", author = "Harsh Reality")
            )
            db.quoteDao().insertQuotes(defaultQuotes)
        }
    }
}
