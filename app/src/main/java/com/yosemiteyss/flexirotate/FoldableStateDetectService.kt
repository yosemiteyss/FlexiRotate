package com.yosemiteyss.flexirotate

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.provider.Settings
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

class FoldableStateDetectService : AccessibilityService(), SensorEventListener {

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

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        hingeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HINGE_ANGLE)

        hingeSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        } ?: run {
            Log.d(TAG, "No hinge sensor available on this device")
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
        hingeSensor?.let {
            sensorManager.unregisterListener(this)
        }
        serviceScope.cancel()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
    }

    override fun onInterrupt() {
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

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    private fun setAutoRotation(enabled: Boolean) {
        val rotation = if (enabled) 1 else 0
        Settings.System.putInt(contentResolver, Settings.System.ACCELEROMETER_ROTATION, rotation)
        Log.d(TAG, "setAutoRotation: $enabled")
    }

    companion object {
        private const val TAG = "FoldableStateDetectService"
    }
}