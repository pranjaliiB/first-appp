package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.JournalEntry
import com.example.data.model.PerformanceDay
import com.example.data.model.Task
import com.example.data.model.TimelineGoal
import com.example.ui.theme.*
import com.example.viewmodel.MainViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

// Glassmorphism modifier helper
fun Modifier.glassmorphicCard(shape: RoundedCornerShape = RoundedCornerShape(16.dp)): Modifier = this
    .clip(shape)
    .background(Color.White.copy(alpha = 0.04f))
    .border(1.dp, Color.White.copy(alpha = 0.08f), shape)

// Core Screens container & Navigation Manager
@Composable
fun MainLayout(viewModel: MainViewModel) {
    var currentScreen by remember { mutableStateOf("SPLASH") }
    val emotionalState by viewModel.emotionalThemeState.collectAsState()
    val isDarkTheme by viewModel.darkThemeEnabled.collectAsState()

    MyApplicationTheme(
        emotionalState = emotionalState,
        darkTheme = isDarkTheme
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (isDarkTheme) {
                        Brush.verticalGradient(
                            colors = if (emotionalState == EmotionalThemeState.PEACEFUL) {
                                listOf(BackgroundDark, Color(0xFF090B13), Color(0xFF0D0F19))
                            } else {
                                listOf(WarningPrimary, Color(0xFFFF2B3C).copy(alpha = 0.04f), BackgroundDark)
                            }
                        )
                    } else {
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFFEEF1F8), Color(0xFFDFE4F2))
                        )
                    }
                )
        ) {
            if (isDarkTheme) {
                // Smooth atmospheric glowing background ring/sphere matching absolute -top-12 -right-12 bg-indigo-500/20 blur-3xl from HTML design
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val colorAccent = if (emotionalState == EmotionalThemeState.PEACEFUL) {
                        Color(0xFF6366F1).copy(alpha = 0.15f)
                    } else {
                        Color(0xFFEF4444).copy(alpha = 0.10f)
                    }
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(colorAccent, Color.Transparent),
                            center = androidx.compose.ui.geometry.Offset(size.width * 0.95f, 0f),
                            radius = size.width * 0.9f
                        )
                    )
                }
            }
            
            when (currentScreen) {
                "SPLASH" -> SplashScreen { currentScreen = "MAIN" }
                "MAIN" -> HomeScreenContent(viewModel)
            }
        }
    }
}

// 9. Splash Screen
@Composable
fun SplashScreen(onFinish: () -> Unit) {
    var startAnimation by remember { mutableStateOf(false) }
    
    val alphaAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(1500),
        label = "fade"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(3200) // Animated quote duration
        onFinish()
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Floating particles
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(Color.White.copy(alpha = 0.03f), radius = 300f, center = center)
            drawCircle(HarmoniousSecondary.copy(alpha = 0.05f), radius = 500f, center = center)
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(24.dp)
                .align(Alignment.Center)
        ) {
            // Elegant Monogram Ring
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .glassmorphicCard(CircleShape)
                    .clickable {},
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "R",
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Bold,
                    color = HarmoniousGlow,
                    fontFamily = FontFamily.Serif
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "RISE AGAIN",
                fontSize = 32.sp,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 4.sp,
                modifier = Modifier.testTag("app_title_splash")
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Discipline today. Freedom tomorrow.",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(60.dp))

            // Splash Quote
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .glassmorphicCard()
                    .padding(20.dp),
                color = Color.Transparent
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "“Comfort is a beautiful place, but nothing ever grows there.”",
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = alphaAnim),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}

// Global Custom Bottom Navigation and Header View
@Composable
fun HomeScreenContent(viewModel: MainViewModel) {
    var selectedTab by remember { mutableStateOf("HOME") }

    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            // Material 3 Glassmorphic Bottom Navigation Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .glassmorphicCard(RoundedCornerShape(24.dp))
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val menuItems = listOf(
                    Triple("HOME", "Home", Icons.Filled.Home),
                    Triple("TASKS", "Tasks", Icons.Filled.Check),
                    Triple("TIMELINE", "Timeline", Icons.Filled.DateRange),
                    Triple("FOCUS", "Timer", Icons.Filled.PlayArrow),
                    Triple("JOURNAL", "Moods", Icons.Filled.Edit),
                    Triple("STATS", "Review", Icons.Filled.Star)
                )

                menuItems.forEach { (route, label, icon) ->
                    val isActive = selectedTab == route
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { selectedTab = route }
                            .padding(vertical = 6.dp, horizontal = 10.dp)
                            .testTag("nav_tab_$route")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(
                                    if (isActive) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                    else Color.Transparent,
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = label,
                                tint = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Text(
                            text = label,
                            fontSize = 10.sp,
                            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                            color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = {
                    (fadeIn(animationSpec = tween(220)) + slideInVertically(
                        initialOffsetY = { 30 },
                        animationSpec = tween(220)
                    )) togetherWith fadeOut(animationSpec = tween(150))
                },
                label = "tab_nav"
            ) { tab ->
                when (tab) {
                    "HOME" -> QuotesDashboard(viewModel)
                    "TASKS" -> TasksDashboard(viewModel)
                    "TIMELINE" -> TimelineDashboard(viewModel)
                    "FOCUS" -> FocusTimerDashboard(viewModel)
                    "JOURNAL" -> JournalDashboard(viewModel)
                    "STATS" -> AnalyticsDashboard(viewModel)
                }
            }
        }
    }
}

// 1. Home Screen Quotes Dashboard
@Composable
fun QuotesDashboard(viewModel: MainViewModel) {
    val motivationQuote by viewModel.motivationalQuote.collectAsState()
    val realityQuote by viewModel.realityQuote.collectAsState()
    val tasks by viewModel.allTasks.collectAsState()

    val upcomingTask = remember(tasks) {
        tasks.firstOrNull { !it.isCompleted }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Rise Again",
                        style = MaterialTheme.typography.titleLarge,
                        color = Slate100
                    )
                    Text(
                        text = "DISCIPLINE TODAY. FREEDOM TOMORROW.",
                        style = MaterialTheme.typography.labelSmall,
                        color = HarmoniousSecondary,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                IconButton(
                    onClick = { viewModel.refreshDailyQuotes() },
                    modifier = Modifier.glassmorphicCard(CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = "Refresh Quotes",
                        tint = Slate300
                    )
                }
            }
        }

        // Daily Motivation Quote Card
        item {
            Text(
                text = "TODAY'S INSPIRATION",
                style = MaterialTheme.typography.labelSmall,
                color = HarmoniousSecondary,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF1E1B4B).copy(alpha = 0.4f), // indigo-950/40
                                Color(0xFF3B0764).copy(alpha = 0.15f), // purple-950/20
                                Color(0xFF03050C)
                            )
                        )
                    )
            ) {
                // Background soft light glow top-right inside card
                Canvas(modifier = Modifier.matchParentSize()) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFF6366F1).copy(alpha = 0.12f), Color.Transparent),
                            center = androidx.compose.ui.geometry.Offset(size.width * 0.95f, 0f),
                            radius = size.width * 0.5f
                        )
                    )
                }
                Column(modifier = Modifier.padding(22.dp)) {
                    Text(
                        text = motivationQuote?.text ?: "“Discipline today. Freedom tomorrow.”",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 18.sp,
                            lineHeight = 26.sp,
                            fontFamily = FontFamily.Serif
                        ),
                        color = Slate100
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "— ${motivationQuote?.author ?: "Rise Again"}",
                        style = MaterialTheme.typography.labelLarge,
                        color = HarmoniousGlow,
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }
        }

        // Harsh Reality Quote Card
        item {
            Text(
                text = "THE HARSH REALITY",
                style = MaterialTheme.typography.labelSmall,
                color = WarningGlow.copy(alpha = 0.8f),
                modifier = Modifier.padding(bottom = 6.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .border(1.dp, WarningGlow.copy(alpha = 0.2f), RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF450A0A).copy(alpha = 0.2f), // deep dark red
                                Color(0xFF0F0A0C)
                            )
                        )
                    )
            ) {
                Column(modifier = Modifier.padding(22.dp)) {
                    Text(
                        text = realityQuote?.text ?: "“If you stop working today, someone else will replace you tomorrow.”",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 16.sp,
                            lineHeight = 24.sp
                        ),
                        color = Slate200
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "— TRUTH CHECK",
                        style = MaterialTheme.typography.labelSmall,
                        color = WarningGlow,
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }
        }

        // Focus Banner
        item {
            Text(
                text = "ACTIVE PRIORITY",
                style = MaterialTheme.typography.labelSmall,
                color = HarmoniousSecondary,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .glassmorphicCard(),
                color = Color.Transparent
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    if (upcomingTask != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(
                                        when (upcomingTask.priority) {
                                            "HIGH" -> WarningGlow
                                            "MEDIUM" -> HarmoniousSecondary
                                            else -> HarmoniousGlow
                                        },
                                        CircleShape
                                    )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = upcomingTask.priority + " PRIORITY",
                                style = MaterialTheme.typography.labelSmall,
                                color = when (upcomingTask.priority) {
                                    "HIGH" -> WarningGlow
                                    "MEDIUM" -> HarmoniousSecondary
                                    else -> HarmoniousGlow
                                },
                                modifier = Modifier.padding(top = 1.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = upcomingTask.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = Slate100
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = upcomingTask.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Slate400
                        )
                    } else {
                        Text(
                            text = "No active tasks. Tap the Tasks tab to map your progress.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Slate400,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                        )
                    }
                }
            }
        }

        // Settings Quick Switches
        item {
            Text(
                text = "CONFIGURATION CONTROL",
                style = MaterialTheme.typography.labelSmall,
                color = HarmoniousSecondary,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .glassmorphicCard(),
                color = Color.Transparent
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    val notifyEnabled by viewModel.notificationsEnabled.collectAsState()
                    val darkEnabled by viewModel.darkThemeEnabled.collectAsState()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Daily Motivation Alarms",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Slate100
                            )
                            Text(
                                text = "Hourly notification signals",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Slate400
                            )
                        }
                        Switch(
                            checked = notifyEnabled,
                            onCheckedChange = { viewModel.setNotificationsEnabled(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = HarmoniousGlow,
                                uncheckedThumbColor = Slate400,
                                uncheckedTrackColor = Color.White.copy(alpha = 0.05f)
                            ),
                            modifier = Modifier.testTag("toggle_notifications")
                        )
                    }
                    Divider(color = Color.White.copy(alpha = 0.06f), modifier = Modifier.padding(vertical = 12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Calming Dark Mode",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Slate100
                            )
                            Text(
                                text = "Sophisticated dark styling active",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Slate400
                            )
                        }
                        Switch(
                            checked = darkEnabled,
                            onCheckedChange = { viewModel.setDarkThemeEnabled(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = HarmoniousGlow,
                                uncheckedThumbColor = Slate400,
                                uncheckedTrackColor = Color.White.copy(alpha = 0.05f)
                            ),
                            modifier = Modifier.testTag("toggle_dark_theme")
                        )
                    }
                }
            }
        }
    }
}

// 3. Task Management Dashboard Screen
@Composable
fun TasksDashboard(viewModel: MainViewModel) {
    val tasks by viewModel.allTasks.collectAsState()
    var showAddTaskDialog by remember { mutableStateOf(false) }

    // Task add/edit variables
    var taskTitle by remember { mutableStateOf("") }
    var taskDesc by remember { mutableStateOf("") }
    var taskPriority by remember { mutableStateOf("HIGH") }
    var selectedTaskForEdit by remember { mutableStateOf<Task?>(null) }

    val activeTasks = remember(tasks) { tasks.filter { !it.isCompleted } }
    val completedTasks = remember(tasks) { tasks.filter { it.isCompleted } }

    val highPriority = activeTasks.filter { it.priority == "HIGH" }
    val mediumPriority = activeTasks.filter { it.priority == "MEDIUM" }
    val lowPriority = activeTasks.filter { it.priority == "LOW" }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Tasks Mapping",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "${activeTasks.size} active tasks remaining",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Button(
                        onClick = {
                            taskTitle = ""
                            taskDesc = ""
                            taskPriority = "HIGH"
                            selectedTaskForEdit = null
                            showAddTaskDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("add_task_button")
                    ) {
                        Text("Add Task", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Progress Bar
            item {
                val progress = if (tasks.isEmpty()) 0f else completedTasks.size.toFloat() / tasks.size.toFloat()
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .glassmorphicCard()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Day Progress", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                        Text("${(progress * 100).toInt()}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = HarmoniousGlow)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = progress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(CircleShape),
                        color = HarmoniousGlow,
                        trackColor = Color.White.copy(alpha = 0.1f)
                    )
                }
            }

            // High Priority section
            if (highPriority.isNotEmpty()) {
                item {
                    TextSectionHeader("High Priority Tasks", WarningGlow)
                }
                items(highPriority) { task ->
                    TaskCard(
                        task = task,
                        onCompletedToggle = { viewModel.toggleTaskCompletion(task) },
                        onEdit = {
                            selectedTaskForEdit = task
                            taskTitle = task.title
                            taskDesc = task.description
                            taskPriority = task.priority
                            showAddTaskDialog = true
                        },
                        onDelete = { viewModel.deleteTask(task.id) }
                    )
                }
            }

            // Medium Priority section
            if (mediumPriority.isNotEmpty()) {
                item {
                    TextSectionHeader("Medium Priority Tasks", HarmoniousSecondary)
                }
                items(mediumPriority) { task ->
                    TaskCard(
                        task = task,
                        onCompletedToggle = { viewModel.toggleTaskCompletion(task) },
                        onEdit = {
                            selectedTaskForEdit = task
                            taskTitle = task.title
                            taskDesc = task.description
                            taskPriority = task.priority
                            showAddTaskDialog = true
                        },
                        onDelete = { viewModel.deleteTask(task.id) }
                    )
                }
            }

            // Low Priority section
            if (lowPriority.isNotEmpty()) {
                item {
                    TextSectionHeader("Low Priority Tasks", HarmoniousGlow)
                }
                items(lowPriority) { task ->
                    TaskCard(
                        task = task,
                        onCompletedToggle = { viewModel.toggleTaskCompletion(task) },
                        onEdit = {
                            selectedTaskForEdit = task
                            taskTitle = task.title
                            taskDesc = task.description
                            taskPriority = task.priority
                            showAddTaskDialog = true
                        },
                        onDelete = { viewModel.deleteTask(task.id) }
                    )
                }
            }

            // Completed Section
            if (completedTasks.isNotEmpty()) {
                item {
                    Divider(color = Color.White.copy(alpha = 0.1f))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(HarmoniousGlow, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Completed Section",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = OffWhite.copy(alpha = 0.6f)
                        )
                    }
                }
                items(completedTasks) { task ->
                    TaskCard(
                        task = task,
                        onCompletedToggle = { viewModel.toggleTaskCompletion(task) },
                        onEdit = {
                            selectedTaskForEdit = task
                            taskTitle = task.title
                            taskDesc = task.description
                            taskPriority = task.priority
                            showAddTaskDialog = true
                        },
                        onDelete = { viewModel.deleteTask(task.id) }
                    )
                }
            }

            // Empty state helper
            if (tasks.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 80.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "☕ No tasks mapped today.",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Pillars of discipline start with small tasks.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }

        // Add/Edit Dialog sheet
        if (showAddTaskDialog) {
            Dialog(onDismissRequest = { showAddTaskDialog = false }) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(CardBackgroundDark)
                        .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(20.dp)),
                    color = CardBackgroundDark
                ) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = if (selectedTaskForEdit == null) "Map New Task" else "Refine Task",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = OffWhite,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        OutlinedTextField(
                            value = taskTitle,
                            onValueChange = { taskTitle = it },
                            label = { Text("Task Title") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = OffWhite,
                                unfocusedTextColor = OffWhite,
                                focusedBorderColor = HarmoniousGlow,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.15f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("task_title_input")
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = taskDesc,
                            onValueChange = { taskDesc = it },
                            label = { Text("Task Description") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = OffWhite,
                                unfocusedTextColor = OffWhite,
                                focusedBorderColor = HarmoniousGlow,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.15f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("task_desc_input")
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text("Priority level", fontSize = 12.sp, color = GreyText, fontWeight = FontWeight.Bold)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            listOf("HIGH", "MEDIUM", "LOW").forEach { level ->
                                val isChosen = taskPriority == level
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(
                                            if (isChosen) {
                                                when (level) {
                                                    "HIGH" -> WarningGlow.copy(alpha = 0.15f)
                                                    "MEDIUM" -> HarmoniousSecondary.copy(alpha = 0.15f)
                                                    else -> HarmoniousGlow.copy(alpha = 0.15f)
                                                }
                                            } else Color.White.copy(alpha = 0.03f)
                                        )
                                        .border(
                                            1.dp,
                                            if (isChosen) {
                                                when (level) {
                                                    "HIGH" -> WarningGlow
                                                    "MEDIUM" -> HarmoniousSecondary
                                                    else -> HarmoniousGlow
                                                }
                                            } else Color.Transparent,
                                            RoundedCornerShape(10.dp)
                                        )
                                        .clickable { taskPriority = level }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = level,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isChosen) {
                                            when (level) {
                                                "HIGH" -> WarningGlow
                                                "MEDIUM" -> HarmoniousSecondary
                                                else -> HarmoniousGlow
                                            }
                                        } else GreyText
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            TextButton(onClick = { showAddTaskDialog = false }) {
                                Text("Cancel", color = GreyText)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Button(
                                onClick = {
                                    if (taskTitle.isNotEmpty()) {
                                        val nowMs = System.currentTimeMillis()
                                        if (selectedTaskForEdit == null) {
                                            viewModel.addTask(taskTitle, taskDesc, nowMs, taskPriority)
                                        } else {
                                            viewModel.editTask(
                                                id = selectedTaskForEdit!!.id,
                                                title = taskTitle,
                                                description = taskDesc,
                                                deadline = selectedTaskForEdit!!.deadline,
                                                priority = taskPriority,
                                                isCompleted = selectedTaskForEdit!!.isCompleted
                                            )
                                        }
                                        showAddTaskDialog = false
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = HarmoniousGlow),
                                modifier = Modifier.testTag("task_save_button")
                            ) {
                                Text("Commit", color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TextSectionHeader(label: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(5.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = Slate400,
            modifier = Modifier.padding(top = 1.dp)
        )
    }
}

@Composable
fun TaskCard(
    task: Task,
    onCompletedToggle: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .glassmorphicCard()
            .clickable { onEdit() },
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Circle Status Checkbox
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(
                        if (task.isCompleted) HarmoniousGlow.copy(alpha = 0.15f)
                        else Color.White.copy(alpha = 0.03f)
                    )
                    .border(
                        1.5.dp,
                        if (task.isCompleted) HarmoniousGlow else Color.White.copy(alpha = 0.15f),
                        CircleShape
                    )
                    .clickable { onCompletedToggle() },
                contentAlignment = Alignment.Center
            ) {
                if (task.isCompleted) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(HarmoniousGlow, CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (task.isCompleted) GreyText else OffWhite,
                    modifier = Modifier.testTag("task_title_${task.id}")
                )
                if (task.description.isNotEmpty()) {
                    Text(
                        text = task.description,
                        fontSize = 12.sp,
                        color = GreyText
                    )
                }
            }

            // Quick Delete option
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete Task",
                    tint = WarningGlow.copy(alpha = 0.62f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

// 4. Timeline Planner Screen
@Composable
fun TimelineDashboard(viewModel: MainViewModel) {
    val goals by viewModel.allGoals.collectAsState()
    var showGoalDialog by remember { mutableStateOf(false) }

    var goalTitle by remember { mutableStateOf("") }
    var goalType by remember { mutableStateOf("DAILY") }

    val dailyGoals = remember(goals) { goals.filter { it.type == "DAILY" } }
    val weeklyGoals = remember(goals) { goals.filter { it.type == "WEEKLY" } }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Timeline Planner",
                        style = MaterialTheme.typography.titleLarge,
                        color = Slate100
                    )
                    Text(
                        text = "Anchor your schedule around habits",
                        style = MaterialTheme.typography.bodyMedium,
                        color = HarmoniousSecondary
                    )
                }
                Button(
                    onClick = { showGoalDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = HarmoniousPrimary),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Add Goal", fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // Custom Static Minimal Calendar View
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .glassmorphicCard(),
                color = Color.Transparent
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "TIMELINE MATRIX",
                        style = MaterialTheme.typography.labelSmall,
                        color = Slate400,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        val calendar = Calendar.getInstance()
                        val days = listOf("S", "M", "T", "W", "T", "F", "S")
                        val todayDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
                        
                        days.forEachIndexed { idx, day ->
                            val isToday = (idx + 1) == todayDayOfWeek
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = day,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isToday) HarmoniousGlow else Slate500
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .background(
                                            if (isToday) HarmoniousPrimary else Color.White.copy(alpha = 0.03f),
                                            CircleShape
                                        )
                                        .border(
                                            width = 1.dp,
                                            color = if (isToday) Color.White.copy(alpha = 0.2f) else SoftPurple.copy(alpha = 0.05f),
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = (calendar.get(Calendar.DAY_OF_MONTH) - (todayDayOfWeek - (idx + 1))).coerceAtLeast(1).toString(),
                                        fontSize = 11.sp,
                                        fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isToday) Color.White else Slate200
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Daily Goals List
        item {
            TextSectionHeader("Daily Goals Checklist", HarmoniousGlow)
        }
        if (dailyGoals.isEmpty()) {
            item {
                Text("No Daily habits defined yet.", fontSize = 12.sp, color = GreyText, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            }
        } else {
            items(dailyGoals) { goal ->
                GoalRow(goal, onToggle = { viewModel.toggleGoalCompletion(goal) }, onDelete = { viewModel.deleteGoal(goal.id) })
            }
        }

        // Weekly Goals List
        item {
            TextSectionHeader("Weekly Pillars", HarmoniousSecondary)
        }
        if (weeklyGoals.isEmpty()) {
            item {
                Text("No Weekly goals defined yet.", fontSize = 12.sp, color = GreyText, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            }
        } else {
            items(weeklyGoals) { goal ->
                GoalRow(goal, onToggle = { viewModel.toggleGoalCompletion(goal) }, onDelete = { viewModel.deleteGoal(goal.id) })
            }
        }
    }

    if (showGoalDialog) {
        Dialog(onDismissRequest = { showGoalDialog = false }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp)),
                color = CardBackgroundDark
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("New Milestone", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = OffWhite)
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = goalTitle,
                        onValueChange = { goalTitle = it },
                        label = { Text("Goal Title") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = OffWhite,
                            unfocusedTextColor = OffWhite,
                            focusedBorderColor = HarmoniousGlow,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("goal_title_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        listOf("DAILY", "WEEKLY").forEach { type ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (goalType == type) HarmoniousPrimary.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.03f)
                                    )
                                    .clickable { goalType = type }
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(type, fontSize = 11.sp, color = if (goalType == type) HarmoniousGlow else GreyText, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                        TextButton(onClick = { showGoalDialog = false }) {
                            Text("Cancel", color = GreyText)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Button(
                            onClick = {
                                if (goalTitle.isNotEmpty()) {
                                    viewModel.addGoal(goalTitle, goalType, System.currentTimeMillis(), true)
                                    goalTitle = ""
                                    showGoalDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = HarmoniousGlow)
                        ) {
                            Text("Create", color = Color.Black)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GoalRow(
    goal: TimelineGoal,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .glassmorphicCard(),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = goal.isCompleted,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(checkedColor = HarmoniousGlow, uncheckedColor = Color.White.copy(alpha = 0.2f))
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = goal.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (goal.isCompleted) GreyText else OffWhite
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete Goal",
                    tint = WarningGlow.copy(alpha = 0.5f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

// 8. Focus Mode Timer Screen
@Composable
fun FocusTimerDashboard(viewModel: MainViewModel) {
    val totalSeconds by viewModel.focusTimeRemaining.collectAsState()
    val isRunning by viewModel.isTimerRunning.collectAsState()
    val chosenCategory by viewModel.focusCategory.collectAsState()

    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    val timeFormatted = String.format("%02d:%02d", minutes, seconds)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(vertical = 12.dp)
            ) {
                Text(
                    text = "Focus Sanctuary",
                    style = MaterialTheme.typography.titleLarge,
                    color = Slate100
                )
                Text(
                    text = "Silence the noise, unleash your potential",
                    style = MaterialTheme.typography.bodyMedium,
                    color = HarmoniousSecondary
                )
            }
        }

        // Radial Canvas Timer
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(230.dp)
            ) {
                // Background radial glow
                Canvas(modifier = Modifier.size(210.dp)) {
                    drawCircle(Color.White.copy(alpha = 0.015f), radius = size.minDimension / 2)
                }

                CircularProgressIndicator(
                    progress = totalSeconds.toFloat() / (25 * 60f),
                    modifier = Modifier.size(210.dp),
                    color = if (chosenCategory == "MEDITATIVE") HarmoniousSecondary else HarmoniousGlow,
                    strokeWidth = 6.dp,
                    strokeCap = StrokeCap.Round,
                    trackColor = Color.White.copy(alpha = 0.05f)
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = timeFormatted,
                        fontSize = 44.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = OffWhite,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.testTag("focus_timer_text")
                    )
                    Text(
                        text = chosenCategory,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (chosenCategory == "MEDITATIVE") HarmoniousSecondary else HarmoniousGlow,
                        letterSpacing = 1.sp
                    )
                }
            }
        }

        // Quick Category Picker
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("FOCUSED", "MEDITATIVE", "BREATHING").forEach { category ->
                    val isSelected = chosenCategory == category
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                else Color.White.copy(alpha = 0.02f)
                            )
                            .border(
                                1.dp,
                                if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                viewModel.setFocusCategory(category)
                                viewModel.resetFocusTimer(if (category == "BREATHING") 5 else 25)
                            }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            category,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) OffWhite else GreyText
                        )
                    }
                }
            }
        }

        // Start / Pause buttons
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.resetFocusTimer(if (chosenCategory == "BREATHING") 5 else 25) },
                    modifier = Modifier
                        .size(48.dp)
                        .glassmorphicCard(CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = "Reset Timer",
                        tint = OffWhite
                    )
                }

                Button(
                    onClick = {
                        if (isRunning) viewModel.pauseFocusTimer()
                        else viewModel.startFocusTimer()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isRunning) WarningGlow else HarmoniousGlow
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .height(52.dp)
                        .width(130.dp)
                        .testTag("timer_play_button")
                ) {
                    Text(
                        text = if (isRunning) "PAUSE" else "FOCUS",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}

// 8. Daily Journal & Mood Tracker Screen
@Composable
fun JournalDashboard(viewModel: MainViewModel) {
    val entries by viewModel.allJournalEntries.collectAsState()
    var selectedMood by remember { mutableStateOf("PEACEFUL") }
    var journalText by remember { mutableStateOf("") }

    val moodIcons = listOf(
        Pair("PEACEFUL", "🌸"),
        Pair("FOCUSED", "⚡"),
        Pair("ALERT", "🔥"),
        Pair("TIRED", "🔋"),
        Pair("ANXIOUS", "🧠"),
        Pair("REGRETFUL", "🥀")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(modifier = Modifier.padding(vertical = 12.dp)) {
                Text(
                    text = "Daily Reflections",
                    style = MaterialTheme.typography.titleLarge,
                    color = Slate100
                )
                Text(
                    text = "Align your emotional state to stay consistent",
                    style = MaterialTheme.typography.bodyMedium,
                    color = HarmoniousSecondary
                )
            }
        }

        // Mood Picker Row
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .glassmorphicCard(),
                color = Color.Transparent
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Today's Vibe", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = OffWhite)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        moodIcons.forEach { (mood, emoji) ->
                            val isSelected = selectedMood == mood
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { selectedMood = mood }
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                        else Color.Transparent
                                    )
                                    .padding(6.dp)
                            ) {
                                Text(emoji, fontSize = 24.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(mood.take(4), fontSize = 9.sp, color = if (isSelected) OffWhite else GreyText, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Write Journal Card
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .glassmorphicCard(),
                color = Color.Transparent
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Pen Down Thoughts", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = OffWhite)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = journalText,
                        onValueChange = { journalText = it },
                        placeholder = { Text("How has discipline treated you today?") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = OffWhite,
                            unfocusedTextColor = OffWhite,
                            focusedBorderColor = HarmoniousGlow,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.1f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .testTag("journal_input")
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            if (journalText.isNotEmpty()) {
                                viewModel.addJournalEntry(selectedMood, journalText)
                                journalText = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = HarmoniousGlow),
                        modifier = Modifier
                            .align(Alignment.End)
                            .testTag("save_journal_button")
                    ) {
                        Text("Log Entry", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Saved Journals list
        item {
            TextSectionHeader("Historic Reflections", HarmoniousSecondary)
        }
        if (entries.isEmpty()) {
            item {
                Text("Empty space, write your first log.", fontSize = 11.sp, color = GreyText, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            }
        } else {
            items(entries) { entry ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .glassmorphicCard(),
                    color = Color.Transparent
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = when (entry.mood) {
                                        "PEACEFUL" -> "🌸"
                                        "FOCUSED" -> "⚡"
                                        "ALERT" -> "🔥"
                                        "TIRED" -> "🔋"
                                        "ANXIOUS" -> "🧠"
                                        else -> "🥀"
                                    },
                                    fontSize = 18.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = entry.mood,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = HarmoniousGlow
                                )
                            }
                            Text(entry.date, fontSize = 11.sp, color = GreyText)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(entry.journalText, fontSize = 13.sp, color = OffWhite)
                        Spacer(modifier = Modifier.height(4.dp))
                        IconButton(
                            onClick = { viewModel.deleteJournalEntry(entry.id) },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = WarningGlow.copy(alpha = 0.5f), modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}

// 5 & 10. Dashboard & Analytics Screen
@Composable
fun AnalyticsDashboard(viewModel: MainViewModel) {
    val tasks by viewModel.allTasks.collectAsState()
    val weeklyHistory by viewModel.recentWeeklyPerformance.collectAsState()
    val consecutiveLows by viewModel.consecutiveLowDays.collectAsState()

    val todayStartMs = getStartOfDayMs()
    val todayEndMs = getEndOfDayMs()
    
    val todayTasks = remember(tasks) {
        tasks.filter {
            it.deadline in todayStartMs..todayEndMs || (it.isCompleted && (it.completedAt ?: 0) >= todayStartMs)
        }
    }

    val completedCount = todayTasks.count { it.isCompleted }
    val pendingCount = todayTasks.size - completedCount
    val totalCount = todayTasks.size

    val productivityPercentage = if (totalCount == 0) 100f else (completedCount.toFloat() / totalCount.toFloat()) * 100f

    // Overlay triggered state
    var showEvaluationDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Metrics Hub",
                        style = MaterialTheme.typography.titleLarge,
                        color = Slate100
                    )
                    Text(
                        text = "Today's summary of consistency",
                        style = MaterialTheme.typography.bodyMedium,
                        color = HarmoniousSecondary
                    )
                }
                Button(
                    onClick = { showEvaluationDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = HarmoniousSecondary),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Evaluate", fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // Today's Stats Cards Grid
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Completed Card
                StatCard(
                    modifier = Modifier.weight(1f),
                    label = "Completed",
                    value = completedCount.toString(),
                    color = HarmoniousGlow
                )
                // Pending Card
                StatCard(
                    modifier = Modifier.weight(1f),
                    label = "Pending",
                    value = pendingCount.toString(),
                    color = WarningGlow
                )
            }
        }

        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Percentage Card
                StatCard(
                    modifier = Modifier.weight(1f),
                    label = "Success Rate",
                    value = "${productivityPercentage.toInt()}%",
                    color = HarmoniousPrimary
                )
                // Streak Card
                val recentStreak = weeklyHistory.firstOrNull()?.streakCount ?: 0
                StatCard(
                    modifier = Modifier.weight(1f),
                    label = "Streak Count",
                    value = "$recentStreak days",
                    color = HarmoniousSecondary
                )
            }
        }

        // Weekly Sunday reflections
        item {
            SundayReflectionPanel(weeklyHistory = weeklyHistory)
        }

        // Lost Time Panel (Under low productivity)
        if (consecutiveLows >= 2) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = WarningPrimary,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("⚠️ Lost-Time Statistics", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = WarningGlow)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "You are on day $consecutiveLows of excuses. This streak can wipe out all momentum built over months. Stop leaving potential behind.",
                            fontSize = 12.sp,
                            color = OffWhite.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }
    }

    // 10. Nightly Performance evaluation Dialog
    if (showEvaluationDialog) {
        Dialog(onDismissRequest = { showEvaluationDialog = false }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(24.dp)),
                color = CardBackgroundDark
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (productivityPercentage >= 50f) {
                        // Appreciation Screen
                        Text("✨ DISCIPLINE REAPED", fontSize = 21.sp, fontWeight = FontWeight.Bold, color = HarmoniousGlow)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "“Good. You moved forward today. Small wins build powerful futures.”",
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 22.sp,
                            color = OffWhite
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Text("Success Rate: ${productivityPercentage.toInt()}%", fontWeight = FontWeight.Bold, color = HarmoniousGlow)
                        Text("Streak: ${weeklyHistory.firstOrNull()?.streakCount ?: 0 + 1} days", color = GreyText)
                    } else {
                        // Reality check Warning Screen
                        Text("🥀 HARD REALITY CHECK", fontSize = 21.sp, fontWeight = FontWeight.Bold, color = WarningGlow)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "“You wasted hours you will never get back. Someone else is working for the life you want.”",
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 22.sp,
                            color = OffWhite
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Text("Success Rate: ${productivityPercentage.toInt()}%", fontWeight = FontWeight.Bold, color = WarningGlow)
                        Text("Missed tasks: $pendingCount", color = GreyText)
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { showEvaluationDialog = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (productivityPercentage >= 50f) HarmoniousGlow else WarningGlow
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("evaluation_close_button")
                    ) {
                        Text("Understood", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    modifier: Modifier,
    label: String,
    value: String,
    color: Color
) {
    Surface(
        modifier = modifier.glassmorphicCard(),
        color = Color.Transparent
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(label, fontSize = 11.sp, color = GreyText, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = color)
        }
    }
}

// Sunday Reflection Panel
@Composable
fun SundayReflectionPanel(weeklyHistory: List<PerformanceDay>) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .glassmorphicCard(),
        color = Color.Transparent
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Weekly Reflection", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = OffWhite)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Generated weekly on Sundays for analytical oversight", fontSize = 11.sp, color = GreyText)
            Spacer(modifier = Modifier.height(12.dp))

            val totalAssigned = weeklyHistory.sumOf { it.tasksAssigned }
            val totalCompleted = weeklyHistory.sumOf { it.tasksCompleted }
            val avgCompletion = if (totalAssigned == 0) 100 else (totalCompleted * 100) / totalAssigned

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Completion rate", fontSize = 11.sp, color = GreyText)
                    Text("$avgCompletion%", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = HarmoniousGlow)
                }
                Column {
                    Text("Missed deadlines", fontSize = 11.sp, color = GreyText)
                    Text("${totalAssigned - totalCompleted}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = WarningGlow)
                }
                Column {
                    Text("Feedback Indicator", fontSize = 11.sp, color = GreyText)
                    Text(
                        text = if (avgCompletion >= 70) "Consistently Strong" else if (avgCompletion >= 50) "Average Rhythm" else "Extreme Danger State",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (avgCompletion >= 50) HarmoniousGlow else WarningGlow
                    )
                }
            }
        }
    }
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
