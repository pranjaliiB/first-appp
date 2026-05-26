package com.example.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.data.local.AppDatabase
import com.example.data.local.SettingsManager
import com.example.data.model.PerformanceDay
import com.example.data.repository.RiseAgainRepository
import com.example.util.NotificationHelper
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

class NightlyEvaluationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val context = applicationContext
        val settings = SettingsManager(context)

        // Ensure notification is enabled
        val isEnabled = settings.notificationsEnabled.first()
        if (!isEnabled) {
            return Result.success()
        }

        val repository = RiseAgainRepository(AppDatabase.getDatabase(context))
        val tasks = repository.allTasks.first()

        // Filter tasks due today or completed today
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        
        // Count tasks representing work for today
        // (Tasks completed today or active with deadlines today)
        val todayStartMs = getStartOfDayMs()
        val todayEndMs = getEndOfDayMs()

        val todayTasks = tasks.filter { task ->
            // Due today or completed today
            val dueToday = task.deadline in todayStartMs..todayEndMs
            val completedToday = task.isCompleted && task.completedAt != null && task.completedAt in todayStartMs..todayEndMs
            dueToday || completedToday || (!task.isCompleted && task.deadline < todayEndMs) // carry over pending past tasks
        }

        val totalTasks = todayTasks.size
        val completedTasks = todayTasks.count { it.isCompleted }

        val percentage = if (totalTasks == 0) {
            100f // perfect day if no tasks assigned
        } else {
            (completedTasks.toFloat() / totalTasks.toFloat()) * 100f
        }

        // Get yesterday's performance to carry over streak
        val yesterdayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(System.currentTimeMillis() - 86400000))
        val yesterdayPerf = repository.getPerformanceDay(yesterdayStr)
        val previousStreak = yesterdayPerf?.streakCount ?: 0

        val currentStreak = if (percentage >= 50f) {
            previousStreak + 1
        } else {
            0
        }

        // Save daily performance to room
        val performanceDay = PerformanceDay(
            date = todayStr,
            tasksAssigned = totalTasks,
            tasksCompleted = completedTasks,
            productivityPercentage = percentage,
            streakCount = currentStreak
        )
        repository.insertPerformanceDay(performanceDay)

        if (percentage >= 50f) {
            // High Productivity Day
            settings.resetConsecutiveLowDays()

            val successQuotes = listOf(
                "Good. You moved forward today.",
                "Small wins build powerful futures.",
                "You stayed disciplined while others wasted time.",
                "Consistency is changing your life. Keep pushing."
            )
            val selectedMsg = successQuotes[Random.nextInt(successQuotes.size)]

            NotificationHelper.sendNotification(
                context = context,
                id = 2001,
                channelId = NotificationHelper.CHANNEL_ACHIEVEMENT,
                title = "Rise Again: Appreciation",
                message = "$selectedMsg (${percentage.toInt()}% Completed, Streak: $currentStreak days!)"
            )
        } else {
            // Low Productivity Day
            settings.incrementConsecutiveLowDays()
            val consecutiveLow = settings.consecutiveLowDays.first()

            if (consecutiveLow >= 3) {
                // Strict habit warning
                val harshStreakMessages = listOf(
                    "One week of excuses can destroy months of progress.",
                    "Discipline decides who survives pressure.",
                    "If you stay inconsistent, someone more focused will take every opportunity you wanted."
                )
                val selectedHarshMsg = harshStreakMessages[Random.nextInt(harshStreakMessages.size)]

                NotificationHelper.sendNotification(
                    context = context,
                    id = 2002,
                    channelId = NotificationHelper.CHANNEL_CONSISTENCY,
                    title = "Rise Again: Strict Warning",
                    message = "$selectedHarshMsg (Day $consecutiveLow of decline. Wake up!)"
                )
            } else {
                val failureQuotes = listOf(
                    "You wasted hours you will never get back.",
                    "Dreams without action become regret.",
                    "Someone else is working for the life you want.",
                    "Every skipped task increases the gap between you and your goals.",
                    "Your competition did not stop today."
                )
                val selectedMsg = failureQuotes[Random.nextInt(failureQuotes.size)]

                NotificationHelper.sendNotification(
                    context = context,
                    id = 2003,
                    channelId = NotificationHelper.CHANNEL_WARNING,
                    title = "Rise Again: Reality Check",
                    message = "$selectedMsg Only completed ${percentage.toInt()}%."
                )
            }
        }

        return Result.success()
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
