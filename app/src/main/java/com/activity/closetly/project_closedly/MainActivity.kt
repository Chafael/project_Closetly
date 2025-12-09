package com.activity.closetly.project_closedly

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.activity.closetly.project_closedly.navegation.NavGraph
import com.activity.closetly.project_closedly.ui.theme.Project_ClosetlyTheme
import com.activity.closetly.project_closedly.ui.viewmodel.AuthStateViewModel
import com.activity.closetly.project_closedly.workers.NotificationWorker
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            startNotificationWork()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            startNotificationWork()
        }

        setContent {
            Project_ClosetlyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val authStateViewModel: AuthStateViewModel = hiltViewModel()
                    val isLoggedIn by authStateViewModel.isUserLoggedIn.collectAsState()

                    NavGraph(isLoggedIn = isLoggedIn)
                }
            }
        }
    }

    private fun startNotificationWork() {
        try {
            val workRequest = OneTimeWorkRequest.Builder(NotificationWorker::class.java)
                .setInitialDelay(30, TimeUnit.SECONDS)
                .build()
            
            WorkManager.getInstance(applicationContext).enqueue(workRequest)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}