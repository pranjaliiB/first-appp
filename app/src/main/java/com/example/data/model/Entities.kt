package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val deadline: Long, // Timestamp in ms
    val priority: String, // "HIGH", "MEDIUM", "LOW"
    val isCompleted: Boolean = false,
    val completedAt: Long? = null
)

@Entity(tableName = "timeline_goals")
data class TimelineGoal(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val type: String, // "DAILY", "WEEKLY"
    val deadline: Long, // Timestamp in ms
    val isCompleted: Boolean = false,
    val hasReminder: Boolean = false,
    val reminderTime: Long? = null
)

@Entity(tableName = "quotes")
data class Quote(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val text: String,
    val category: String, // "MOTIVATION", "REALITY"
    val author: String = "Rise Again"
)

@Entity(tableName = "performance_days")
data class PerformanceDay(
    @PrimaryKey val date: String, // YYYY-MM-DD
    val tasksAssigned: Int,
    val tasksCompleted: Int,
    val productivityPercentage: Float,
    val streakCount: Int
)

@Entity(tableName = "journal_entries")
data class JournalEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String, // YYYY-MM-DD
    val mood: String, // "PEACEFUL", "ALERT", "FOCUSED", "TIRED", "ANXIOUS", "REGRETFUL"
    val journalText: String,
    val timestamp: Long = System.currentTimeMillis()
)
