package com.example.worker

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit

object WorkScheduler {

    const val HOURLY_MOTIVATION_ID = "hourly_motivation_work"
    const val NIGHTLY_EVALUATION_ID = "nightly_evaluation_work"

    fun scheduleJobs(context: Context) {
        val workManager = WorkManager.getInstance(context)

        // 1. Schedule Hourly Motivation
        val hourlyRequest = PeriodicWorkRequestBuilder<HourlyMotivationWorker>(
            1, TimeUnit.HOURS
        ).build()

        workManager.enqueueUniquePeriodicWork(
            HOURLY_MOTIVATION_ID,
            ExistingPeriodicWorkPolicy.KEEP,
            hourlyRequest
        )

        // 2. Schedule Nightly Evaluation to run at 10 PM (22:00)
        val calendar = Calendar.getInstance()
        val now = calendar.timeInMillis

        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 22)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (target.timeInMillis <= now) {
            target.add(Calendar.DAY_OF_YEAR, 1)
        }

        val initialDelay = target.timeInMillis - now

        val nightlyRequest = PeriodicWorkRequestBuilder<NightlyEvaluationWorker>(
            24, TimeUnit.HOURS
        )
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .build()

        workManager.enqueueUniquePeriodicWork(
            NIGHTLY_EVALUATION_ID,
            ExistingPeriodicWorkPolicy.KEEP,
            nightlyRequest
        )
    }
}
