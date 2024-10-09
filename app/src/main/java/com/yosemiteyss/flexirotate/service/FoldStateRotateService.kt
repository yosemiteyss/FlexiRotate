package com.yosemiteyss.flexirotate.service

import android.accessibilityservice.AccessibilityService
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.provider.Settings
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.yosemiteyss.flexirotate.FoldStateRotateSettingsActivity
import com.yosemiteyss.flexirotate.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

class FoldStateRotateService : AccessibilityService(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var hingeSensor: Sensor? = null

    private val foldState: MutableStateFlow<FoldState?> = MutableStateFlow(null)
    private val serviceScope = CoroutineScope(Dispatchers.Default + Job())

    enum class FoldState {
        FULLY_FOLDED,
        HALF_FOLDED,
        UNFOLDED,
    }

    override fun onServiceConnected() {
        super.onServiceConnected()

        // Go to settings if write system is not permitted.
        if (!Settings.System.canWrite(this)) {
            val intent = Intent(this, FoldStateRotateSettingsActivity::class.java)
            startActivity(intent)
            return
        }

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        hingeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HINGE_ANGLE)

        // No hinge sensor found.
        if (hingeSensor == null) {
            Toast.makeText(this, R.string.error_cannot_detect_fold_state, Toast.LENGTH_SHORT).show()
            return
        }

        // Cannot attach sensor listener.
        if (!sensorManager.registerListener(this, hingeSensor, SensorManager.SENSOR_DELAY_NORMAL)) {
            Toast.makeText(this, R.string.error_cannot_detect_fold_state, Toast.LENGTH_SHORT).show()
            return
        }

        createNotificationChannel()
        startForegroundNotification()

        serviceScope.launch {
            foldState.filterNotNull().collect {
                if (it == FoldState.FULLY_FOLDED) {
                    setAutoRotation(false)
                } else {
                    setAutoRotation(true)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        if (hingeSensor != null) {
            sensorManager.unregisterListener(this)
        }

        serviceScope.cancel()
        stopForegroundNotification()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            serviceScope.launch {
                val hingeAngle = event.values[0]
                when {
                    hingeAngle <= 10 -> foldState.emit(FoldState.FULLY_FOLDED)
                    hingeAngle >= 170 -> foldState.emit(FoldState.UNFOLDED)
                    else -> foldState.emit(FoldState.HALF_FOLDED)
                }
            }
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
    }

    override fun onInterrupt() {
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    private fun setAutoRotation(enabled: Boolean) {
        val rotation = if (enabled) 1 else 0
        Settings.System.putInt(contentResolver, Settings.System.ACCELEROMETER_ROTATION, rotation)
        Log.d(TAG, "setAutoRotation: $enabled")
    }

    private fun createNotificationChannel() {
        val notificationManager = getSystemService(NotificationManager::class.java)

        val notificationChannel = NotificationChannel(
            CHANNEL_ID,
            getString(R.string.accessibility_notification_channel),
            NotificationManager.IMPORTANCE_LOW
        )

        notificationManager.createNotificationChannel(notificationChannel)
    }

    private fun startForegroundNotification() {
        val notificationIntent = Intent(this, FoldStateRotateSettingsActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.accessibility_notification_title))
            .setContentText(getString(R.string.accessibility_service_description))
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }

    private fun stopForegroundNotification() {
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    companion object {
        private const val TAG = "FoldableStateDetectService"
        private const val CHANNEL_ID = "accessibility_service_channel"
        private const val NOTIFICATION_ID = 1
    }
}