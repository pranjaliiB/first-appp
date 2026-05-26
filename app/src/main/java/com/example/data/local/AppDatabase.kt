package com.example.data.local

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import com.example.data.model.JournalEntry
import com.example.data.model.PerformanceDay
import com.example.data.model.Quote
import com.example.data.model.Task
import com.example.data.model.TimelineGoal
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY deadline ASC")
    fun getAllTasks(): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTaskById(id: Int)

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: Int): Task?
}

@Dao
interface TimelineGoalDao {
    @Query("SELECT * FROM timeline_goals ORDER BY deadline ASC")
    fun getAllGoals(): Flow<List<TimelineGoal>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: TimelineGoal)

    @Update
    suspend fun updateGoal(goal: TimelineGoal)

    @Delete
    suspend fun deleteGoal(goal: TimelineGoal)

    @Query("DELETE FROM timeline_goals WHERE id = :id")
    suspend fun deleteGoalById(id: Int)
}

@Dao
interface QuoteDao {
    @Query("SELECT * FROM quotes")
    fun getAllQuotes(): Flow<List<Quote>>

    @Query("SELECT * FROM quotes WHERE category = :category")
    suspend fun getQuotesByCategory(category: String): List<Quote>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuotes(quotes: List<Quote>)

    @Query("SELECT COUNT(*) FROM quotes")
    suspend fun getCount(): Int
}

@Dao
interface PerformanceDao {
    @Query("SELECT * FROM performance_days ORDER BY date DESC")
    fun getAllPerformanceDays(): Flow<List<PerformanceDay>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPerformanceDay(day: PerformanceDay)

    @Query("SELECT * FROM performance_days WHERE date = :date")
    suspend fun getPerformanceDay(date: String): PerformanceDay?

    @Query("SELECT * FROM performance_days ORDER BY date DESC LIMIT 7")
    fun getRecentWeeklyPerformance(): Flow<List<PerformanceDay>>
}

@Dao
interface JournalDao {
    @Query("SELECT * FROM journal_entries ORDER BY timestamp DESC")
    fun getAllJournalEntries(): Flow<List<JournalEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJournalEntry(entry: JournalEntry)

    @Query("DELETE FROM journal_entries WHERE id = :id")
    suspend fun deleteJournalEntryById(id: Int)
}

@Database(
    entities = [Task::class, TimelineGoal::class, Quote::class, PerformanceDay::class, JournalEntry::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun timelineGoalDao(): TimelineGoalDao
    abstract fun quoteDao(): QuoteDao
    abstract fun performanceDao(): PerformanceDao
    abstract fun journalDao(): JournalDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "rise_again_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
