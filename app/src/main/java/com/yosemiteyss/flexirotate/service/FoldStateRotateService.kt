package com.yosemiteyss.flexirotate.service

import android.accessibilityservice.AccessibilityService
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
            val intent = Intent(this, FoldStateRotateSettingsActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
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
        if (Settings.System.canWrite(this)) {
            val rotation = if (enabled) 1 else 0
            Settings.System.putInt(
                contentResolver,
                Settings.System.ACCELEROMETER_ROTATION,
                rotation
            )
            Log.d(TAG, "setAutoRotation: $enabled")
        }
    }

    companion object {
        private const val TAG = "FoldableStateDetectService"
    }
}