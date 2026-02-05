package uk.jamesbaker.openrndr.everyday.february

import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.dialogs.saveFileDialog
import org.openrndr.extensions.Screenshots
import org.openrndr.extra.composition.composition
import org.openrndr.extra.composition.draw
import org.openrndr.extra.composition.drawComposition
import org.openrndr.extra.noise.scatter
import org.openrndr.extra.olive.oliveProgram
import org.openrndr.extra.svg.saveToFile
import org.openrndr.math.Vector2
import org.openrndr.shape.Circle
import org.openrndr.shape.Rectangle
import org.openrndr.shape.intersection
import kotlin.random.Random

private fun main() = application {
    configure {
        width = 1000
        height = 1000
    }
    oliveProgram {
        extend(Screenshots())
        val design = drawComposition {}
        val random = Random(seed = 0b100010)

        val margin = 10
        val frameWidth = (drawer.bounds.width - (margin * 3)) / 2
        val frameHeight = drawer.bounds.height - (margin * 2)
        val leftFrame = Rectangle.fromCenter(
            center = Vector2(x = (frameWidth / 2) + margin, y = drawer.bounds.height / 2),
            width = frameWidth,
            height = frameHeight,
        )

        val leftCircles = leftFrame
            .scatter(placementRadius = 40.0, random = random)
            .map { Circle(center = it, radius = random.nextDouble(from = 30.0, until = 150.0)) }
            .sortedByDescending { it.radius }

        val leftFrameShape = leftFrame.shape
        val leftShapes = leftCircles.map { it.shape.intersection(leftFrameShape) }

        val shift = Vector2(x = frameWidth + margin, y = 0.0)
        val rightFrame = leftFrame.movedBy(shift)

        val maxShift = 150.0
        val miniShift = maxShift / leftCircles.size

        val circleShifts = generateSequence(-maxShift / 2) { it + miniShift }
            .take(leftCircles.size)
            .map { shift + Vector2(x = it, y = 0.0) }
            .toList()

        val rightCircles = leftCircles.zip(circleShifts) { circle, circleShift -> circle.movedBy(circleShift) }

        val rightFrameShape = rightFrame.shape
        val rightShapes = rightCircles.map { it.shape.intersection(rightFrameShape) }

        extend {
            drawer.clear(ColorRGBa.BLACK)

            design.clear()
            design.draw {
                group {
                    stroke = ColorRGBa.WHITE
                    fill = ColorRGBa.BLACK
                    shapes(leftShapes)
                    shapes(rightShapes)
                    stroke = ColorRGBa.WHITE
                    fill = null
                    rectangle(leftFrame)
                    rectangle(rightFrame)
                }
            }
            drawer.composition(design)
        }
        // Show a save dialog when pressing the `s` key, then save the design
        // with the chosen file name into the selected folder.
        keyboard.keyDown.listen {
            if (it.name == "s") {
                saveFileDialog(supportedExtensions = listOf("SVG" to listOf("svg"))) { file ->
                    design.saveToFile(file)
                }
            }
        }
    }
}
