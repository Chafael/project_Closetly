package com.activity.closetly.project_closedly

import android.app.Application
import com.activity.closetly.project_closedly.data.remote.CloudinaryService
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class ClosetlyApplication : Application() {

    @Inject
    lateinit var cloudinaryService: CloudinaryService

    override fun onCreate() {
        super.onCreate()

        cloudinaryService.initialize(
            cloudName = "dnsxwuqhw",
            apiKey = "587713743338794",
            apiSecret = "_ptH-7JCnd0Nc7-0a1rYEkyj36c"
        )
    }
}