package com.cs407.lab09

import android.hardware.Sensor
import android.hardware.SensorEvent
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.abs

class BallViewModel : ViewModel() {

    private var ball: Ball? = null
    private var lastTimestamp: Long = 0L

    // Expose the ball's position as a StateFlow
    private val _ballPosition = MutableStateFlow(Offset.Zero)
    val ballPosition: StateFlow<Offset> = _ballPosition.asStateFlow()

    // Sensitivity factor to map m/s^2 to pixels/s^2 (tune as needed)
    // You may adjust based on your field size; this is a reasonable default.
    private var accelToPixel = 50f

    /**
     * Called by the UI when the game field's size is known.
     */
    fun initBall(fieldWidth: Float, fieldHeight: Float, ballSizePx: Float) {
        if (ball == null) {
            ball = Ball(
                backgroundWidth = fieldWidth,
                backgroundHeight = fieldHeight,
                ballSize = ballSizePx
            )
            // You can tune sensitivity proportional to smaller dimension
            val minDim = kotlin.math.min(fieldWidth, fieldHeight)
            accelToPixel = (minDim / 9.81f) * 0.25f // quarter-screen per 1g approx
            _ballPosition.value = Offset(ball!!.posX, ball!!.posY)
        }
    }

    /**
     * Called by the SensorEventListener in the UI.
     */
    fun onSensorDataChanged(event: SensorEvent) {
        // Ensure ball is initialized
        val currentBall = ball ?: return

        if (event.sensor.type == Sensor.TYPE_GRAVITY) {
            if (lastTimestamp != 0L) {
                // TODO: Calculate the time difference (dT) in seconds
                // Hint: event.timestamp is in nanoseconds
                // val NS2S = 1.0f / 1000000000.0f
                // val dT = ...
                val NS2S = 1.0f / 1_000_000_000.0f
                val dT = (event.timestamp - lastTimestamp) * NS2S


                // Raw gravity components in m/s^2
                val gx = event.values[0]
                val gy = event.values[1]

                // If ball feels inverted, flip signs like this:
                val screenAx = (-gx) * accelToPixel
                val screenAy = (gy) * accelToPixel


                // Update physics
                currentBall.updatePositionAndVelocity(
                    xAcc = screenAx,
                    yAcc = screenAy,
                    dT = dT
                )

                // Notify UI
                _ballPosition.update { Offset(currentBall.posX, currentBall.posY) }
            }

            // Update the lastTimestamp
            lastTimestamp = event.timestamp
        }
    }

    fun reset() {
        // Reset the ball's state
        ball?.reset()

        // Update the StateFlow with the reset position
        ball?.let {
            _ballPosition.value = Offset(it.posX, it.posY)
        }

        // Reset the lastTimestamp
        lastTimestamp = 0L
    }
}