package tech.poder.overlay

import java.awt.Color
import java.awt.image.BufferedImage

// TODO: A way to listen to clicks on the overlay?
interface Overlay {

    /**
     * The width for the overlay canvas
     */
    var canvasWidth: Int

    /**
     * The height for the overlay canvas
     */
    var canvasHeight: Int


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
    fun image(image: BufferedImage, position: Position, width: Int, height: Int)

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

    fun onResize(callback: () -> Unit)

    /**
     * Used to store position information for elements
     *
     * @property x The x position
     * @property y The y position
     */
    data class Position(val x: Int, val y: Int)

}