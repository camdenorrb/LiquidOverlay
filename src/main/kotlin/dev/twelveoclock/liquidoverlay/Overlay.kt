package dev.twelveoclock.liquidoverlay

import java.awt.Color
import java.awt.image.BufferedImage

// TODO: A way to listen to clicks on the overlay?
interface Overlay {

    /**
     * The position for the overlay canvas
     */
    val position: Position

    /**
     * The width for the overlay canvas
     */
    val canvasWidth: Float

    /**
     * The height for the overlay canvas
     */
    val canvasHeight: Float

    /**
     * Whether the overlay can be moved or not
     */
    val isDraggable: Boolean


    /**
     * Draws a rectangle onto the overlay
     *
     * @param color The color of the overlay
     * @param first The first position for the rectangle
     * @param second The second position for the rectangle
     */
    fun rectangle(color: Color, first: Position, second: Position)

    /**
     * Draws a circle onto the overlay
     *
     * @param color The color for the circle
     * @param radius The radius of the circle
     * @param center The center position of the circle
     */
    fun circle(color: Color, radius: Int, center: Position)

    /**
     * Draws an image onto the overlay
     *
     * @param image The image to be drawn
     * @param position The top left position for it to be drawn at
     */
    fun image(image: BufferedImage, position: Position, width: Float, height: Float)

    /**
     * Draws text onto the overlay
     *
     * @param value The text value to draw onto the overlay
     * @param fontSize The font size for the text
     * @param color The color for the text
     * @param start The starting position for the text
     */
    fun text(value: String, fontSize: Float, color: Color, start: Position)

    /**
     * Removes all elements from the overlay gui
     */
    fun clear()

    /**
     * Closes the overlay
     */
    fun close()

    /**
     * Show the overlay
     */
    fun show()


    /**
     * Used to store position information for elements
     *
     * @property x The x position
     * @property y The y position
     * @property z The z position
     */
    data class Position(val x: Float, val y: Float, val z: Float)

}