package com.yosemiteyss.flexirotate.ui.screen

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.core.content.ContextCompat
import com.yosemiteyss.flexirotate.service.FoldStateRotateService
import com.yosemiteyss.flexirotate.utils.AccessibilityUtils

object SetupPageManager {
    fun getPageList(): List<SetupPage> {
        val pages = mutableListOf<SetupPage>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pages.add(SetupPage.ENABLE_NOTIFICATION)
        }

        pages.add(SetupPage.ENABLE_WRITE_SETTINGS)
        pages.add(SetupPage.ENABLE_ACCESSIBILITY)
        pages.add(SetupPage.COMPLETED)

        return pages
    }

    fun getCurrentPage(context: Context): SetupPage {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return SetupPage.ENABLE_NOTIFICATION
            }
        }

        if (!Settings.System.canWrite(context)) {
            return SetupPage.ENABLE_WRITE_SETTINGS
        }

        if (!AccessibilityUtils.isAccessibilityServiceEnabled(
                context,
                FoldStateRotateService::class.java
            )
        ) {
            return SetupPage.ENABLE_ACCESSIBILITY
        }

        return SetupPage.COMPLETED
    }

    enum class SetupPage {
        ENABLE_NOTIFICATION,
        ENABLE_WRITE_SETTINGS,
        ENABLE_ACCESSIBILITY,
        COMPLETED
    }
}