package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.example.ui.screens.MainLayout
import com.example.util.NotificationHelper
import com.example.viewmodel.MainViewModel
import com.example.worker.WorkScheduler

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Setup edge-to-edge drawing
        enableEdgeToEdge()

        // Initialize Notification Channels
        NotificationHelper.createChannels(this)

        // Boot and trigger Work Schedulers
        WorkScheduler.scheduleJobs(this)

        // Initialize View Model
        val viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        setContent {
            MainLayout(viewModel)
        }
    }
}
