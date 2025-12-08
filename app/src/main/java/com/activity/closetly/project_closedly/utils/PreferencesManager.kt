package com.activity.closetly.project_closedly.utils

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private val sharedPreferences = context.getSharedPreferences(
        "closetly_preferences",
        Context.MODE_PRIVATE
    )

    fun saveProfileImageUri(uri: String) {
        sharedPreferences.edit().putString(KEY_PROFILE_IMAGE, uri).apply()
    }

    fun getProfileImageUri(): String? {
        return sharedPreferences.getString(KEY_PROFILE_IMAGE, null)
    }

    fun clearProfileImageUri() {
        sharedPreferences.edit().remove(KEY_PROFILE_IMAGE).apply()
    }

    companion object {
        private const val KEY_PROFILE_IMAGE = "profile_image_uri"
    }
}