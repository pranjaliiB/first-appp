package com.example.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.data.local.AppDatabase
import com.example.data.local.SettingsManager
import com.example.data.repository.RiseAgainRepository
import com.example.util.NotificationHelper
import kotlinx.coroutines.flow.first
import kotlin.random.Random

class HourlyMotivationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val context = applicationContext
        val settings = SettingsManager(context)

        // Check if notifications are enabled in settings
        val isEnabled = settings.notificationsEnabled.first()
        if (!isEnabled) {
            return Result.success()
        }

        // Check if there are incomplete tasks
        val repository = RiseAgainRepository(AppDatabase.getDatabase(context))
        val tasks = repository.allTasks.first()
        val incompleteCount = tasks.count { !it.isCompleted }

        // If no incomplete tasks, we can send general discipline reminders or skip
        val isPending = incompleteCount > 0

        val reminders = if (isPending) {
            listOf(
                "You still have $incompleteCount tasks left. Stand up and finish.",
                "Your future depends on today. Finish your pending goals.",
                "Keep moving or stay behind. Push through your task backlog now.",
                "You still have active goals left on your list today.",
                "Stop scrolling. You have unfinished business in Rise Again.",
                "Every skipped task increases the gap between you and your goals."
            )
        } else {
            listOf(
                "Great work so far. Maintain consistency.",
                "Discipline decides who survives pressure.",
                "Discipline today. Freedom tomorrow.",
                "Even when ahead, never stop improving.",
                "Comfort is the graveyard of your potential. Stand up."
            )
        }

        val randomMessage = reminders[Random.nextInt(reminders.size)]

        NotificationHelper.sendNotification(
            context = context,
            id = 1001,
            channelId = NotificationHelper.CHANNEL_MOTIVATION,
            title = if (isPending) "Rise Again: Goals Pending" else "Rise Again: Mindset Check",
            message = randomMessage
        )

        return Result.success()
    }
}
