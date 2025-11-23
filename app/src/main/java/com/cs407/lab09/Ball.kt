package com.cs407.lab09

/**
 * Represents a ball that can move. (No Android UI imports!)
 *
 * Constructor parameters:
 * - backgroundWidth: the width of the background, of type Float
 * - backgroundHeight: the height of the background, of type Float
 * - ballSize: the width/height of the ball, of type Float
 */
class Ball(
    private val backgroundWidth: Float,
    private val backgroundHeight: Float,
    private val ballSize: Float
) {
    var posX = 0f
    var posY = 0f
    var velocityX = 0f
    var velocityY = 0f
    private var accX = 0f
    private var accY = 0f

    private var isFirstUpdate = true

    init {
        // TODO: Call reset()
        reset()
    }

    /**
     * Updates the ball's position and velocity based on the given acceleration and time step.
     * (See lab handout for physics equations)
     *
     * Uses linear interpolation of acceleration between (a0 -> a1) over a small dt:
     * v1 = v0 + 0.5 * (a0 + a1) * dt
     * s  = v0 * dt + (dt^2 / 6) * (3 * a0 + a1)
     */
    fun updatePositionAndVelocity(xAcc: Float, yAcc: Float, dT: Float) {
        if (isFirstUpdate) {
            isFirstUpdate = false
            accX = xAcc
            accY = yAcc
            return
        }

        val a0x = accX
        val a0y = accY
        val a1x = xAcc
        val a1y = yAcc

        val dt = dT.coerceAtLeast(0f)

        // Distance traveled along each axis during dt
        val dt2 = dt * dt
        val sx = velocityX * dt + (dt2 / 6f) * (3f * a0x + a1x)
        val sy = velocityY * dt + (dt2 / 6f) * (3f * a0y + a1y)

        // Update positions
        posX += sx
        posY += sy

        // Update velocities using average acceleration over dt
        velocityX += 0.5f * (a0x + a1x) * dt
        velocityY += 0.5f * (a0y + a1y) * dt

        // Update current acceleration to new value
        accX = a1x
        accY = a1y

        // Keep inside boundaries
        checkBoundaries()
    }

    /**
     * Ensures the ball does not move outside the boundaries.
     * When it collides, velocity and acceleration perpendicular to the
     * boundary should be set to 0.
     */
    fun checkBoundaries() {
        // Left boundary
        if (posX < 0f) {
            posX = 0f
            velocityX = 0f
            accX = 0f
        }
        // Right boundary
        val maxX = (backgroundWidth - ballSize).coerceAtLeast(0f)
        if (posX > maxX) {
            posX = maxX
            velocityX = 0f
            accX = 0f
        }
        // Top boundary
        if (posY < 0f) {
            posY = 0f
            velocityY = 0f
            accY = 0f
        }
        // Bottom boundary
        val maxY = (backgroundHeight - ballSize).coerceAtLeast(0f)
        if (posY > maxY) {
            posY = maxY
            velocityY = 0f
            accY = 0f
        }
    }

    /**
     * Resets the ball to the center of the screen with zero
     * velocity and acceleration.
     */
    fun reset() {
        posX = (backgroundWidth - ballSize) / 2f
        posY = (backgroundHeight - ballSize) / 2f
        velocityX = 0f
        velocityY = 0f
        accX = 0f
        accY = 0f
        isFirstUpdate = true
    }
}