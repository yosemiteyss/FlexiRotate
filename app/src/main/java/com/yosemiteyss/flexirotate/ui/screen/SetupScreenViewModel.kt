package com.yosemiteyss.flexirotate.ui.screen

import android.Manifest
import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.yosemiteyss.flexirotate.service.FoldStateRotateService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class SetupSteps(
    var isNotificationEnabled: Boolean = false,
    var isWriteSettingsEnabled: Boolean = false,
    var isAccessibilityEnabled: Boolean = false,
    var isBatteryOptimizationIgnored: Boolean = false
) {
    val canEnableAccessibility: Boolean
        get() = isNotificationEnabled && isWriteSettingsEnabled && isBatteryOptimizationIgnored
}

class SetupScreenViewModel : ViewModel() {
    private val _setupSteps = MutableStateFlow(SetupSteps())
    val setupSteps: StateFlow<SetupSteps> = _setupSteps.asStateFlow()

    fun refreshSetupSteps(context: Context) {
        val updatedSteps = _setupSteps.value.copy()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            updatedSteps.isNotificationEnabled = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        }

        updatedSteps.isWriteSettingsEnabled = Settings.System.canWrite(context)

        updatedSteps.isAccessibilityEnabled = isAccessibilityServiceEnabled(
            context,
            FoldStateRotateService::class.java
        )

        updatedSteps.isBatteryOptimizationIgnored = isIgnoringBatteryOptimizations(context)

        _setupSteps.value = updatedSteps
    }

    private fun isIgnoringBatteryOptimizations(context: Context): Boolean {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        return powerManager.isIgnoringBatteryOptimizations(context.packageName)
    }

    private fun isAccessibilityServiceEnabled(
        context: Context,
        serviceClass: Class<out AccessibilityService>
    ): Boolean {
        val accessibilityManager =
            context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val enabledServices = accessibilityManager.getEnabledAccessibilityServiceList(
            AccessibilityServiceInfo.FEEDBACK_ALL_MASK
        )

        for (enabledService in enabledServices) {
            val enabledServiceInfo = enabledService.resolveInfo.serviceInfo
            if (enabledServiceInfo.packageName == context.packageName && enabledServiceInfo.name == serviceClass.name) {
                return true
            }
        }

        return false
    }
}