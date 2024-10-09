package com.yosemiteyss.flexirotate.utils

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.view.accessibility.AccessibilityManager

object AccessibilityUtils {
    fun isAccessibilityServiceEnabled(
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
            if (enabledServiceInfo.packageName.equals(context.packageName) && enabledServiceInfo.name.equals(
                    serviceClass.name
                )
            ) {
                return true
            }
        }

        return false
    }
}