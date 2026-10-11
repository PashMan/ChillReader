package com.example.handsfree

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.abs

enum class NodGestureState {
    IDLE,
    NOD_DETECTED,
    SCROLLING_ACTIVE,
    PAUSED
}

class HandsFreeController(private val context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
    private val accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val gyroscope = sensorManager?.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

    private val _isTracking = MutableStateFlow(false)
    val isTracking: StateFlow<Boolean> = _isTracking.asStateFlow()

    private val _nodEvents = MutableSharedFlow<NodGestureState>(extraBufferCapacity = 10)
    val nodEvents: SharedFlow<NodGestureState> = _nodEvents.asSharedFlow()

    private val _currentPitch = MutableStateFlow(0f)
    val currentPitch: StateFlow<Float> = _currentPitch.asStateFlow()

    private val _isAutoScrolling = MutableStateFlow(false)
    val isAutoScrolling: StateFlow<Boolean> = _isAutoScrolling.asStateFlow()

    private var lastNodTimestamp = 0L
    private val nodCooldown = 750L // ms cooldown between nods
    private var basePitch = 0f
    private var isCalibrated = false

    fun startTracking() {
        _isTracking.value = true
        isCalibrated = false
        accelerometer?.let {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
        gyroscope?.let {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    fun stopTracking() {
        _isTracking.value = false
        _isAutoScrolling.value = false
        sensorManager?.unregisterListener(this)
    }

    fun triggerManualNod() {
        handleNodDetected()
    }

    private fun handleNodDetected() {
        val now = System.currentTimeMillis()
        if (now - lastNodTimestamp < nodCooldown) return
        lastNodTimestamp = now

        val newScrolling = !_isAutoScrolling.value
        _isAutoScrolling.value = newScrolling

        if (newScrolling) {
            _nodEvents.tryEmit(NodGestureState.SCROLLING_ACTIVE)
        } else {
            _nodEvents.tryEmit(NodGestureState.PAUSED)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (!_isTracking.value || event == null) return

        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val y = event.values[1] // y-axis acceleration / tilt
            _currentPitch.value = y

            if (!isCalibrated) {
                basePitch = y
                isCalibrated = true
                return
            }

            val deltaY = y - basePitch
            // Detect sharp downward nod motion (positive delta Y shift when head tilts down forward)
            if (deltaY > 2.8f || abs(event.values[2] - 9.8f) > 3.5f) {
                handleNodDetected()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No-op
    }
}
